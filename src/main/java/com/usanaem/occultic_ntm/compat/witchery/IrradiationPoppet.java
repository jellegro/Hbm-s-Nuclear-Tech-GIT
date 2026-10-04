package com.usanaem.occultic_ntm.compat.witchery;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockPoppetShelf.TileEntityPoppetShelf;
import com.emoniph.witchery.crafting.RecipeAttachTaglock;
import com.emoniph.witchery.item.ItemPoppet;
import com.hbm.hazard.modifier.HazardModifier;
import com.usanaem.occultic_ntm.config.IntegrationConfig;
import com.usanaem.occultic_ntm.radiation.RadiationExposureEvent;
import com.usanaem.occultic_ntm.radiation.TemporaryRadiationRelease;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import org.apache.logging.log4j.Logger;

/** Dose lifecycle of native subtype 12: exposure, item hazard, and dose-preserving binding.
 * This is behavior on Witchery's ItemPoppet, not a separately registered Item. (and an absolute pain to get working)
 */
public final class IrradiationPoppet extends HazardModifier implements IRecipe {
    public static final String STATE_KEY = "occultic_ntm:irradiation_poppet";
    private static final String DOSE_KEY = "storedRAD";
    public static final double EMISSION_SECONDS = 100D;

    private final IntegrationConfig config;
    private final Logger logger;
    private final IRecipe nativeRecipe;
    private boolean bindingAvailable = true;
    private boolean lookupAvailable = true;
    private boolean missingLocationLogged;

    IrradiationPoppet(IntegrationConfig config, Logger logger, ItemStack stack) {
        this.config = config;
        this.logger = logger;
        nativeRecipe = new RecipeAttachTaglock(stack.copy(), stack.copy(),
                new ItemStack(Witchery.Items.TAGLOCK_KIT, 1, 1));
    }

    private static NBTTagCompound read(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound().getCompoundTag(STATE_KEY) : new NBTTagCompound();
    }

    private static NBTTagCompound write(ItemStack stack) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (!stack.getTagCompound().hasKey(STATE_KEY, 10)) stack.getTagCompound().setTag(STATE_KEY, new NBTTagCompound());
        return stack.getTagCompound().getCompoundTag(STATE_KEY);
    }

    public static double getStoredRadiation(ItemStack stack) {
        double stored = read(stack).getDouble(DOSE_KEY);
        // Corrupt non-finite state is treated as saturated, never as a free shield.
        if (Double.isNaN(stored) || Double.isInfinite(stored)) return Double.MAX_VALUE;
        return Math.max(0D, stored);
    }

    public static float getEmissionRate(ItemStack stack) {
        return (float) Math.min(Float.MAX_VALUE, getStoredRadiation(stack) / EMISSION_SECONDS);
    }

    /** Single-stack transaction on the server: retained player dose + stored dose is conserved. */
    public static double absorb(ItemStack stack, RadiationExposureEvent event, double capacity) {
        if (Double.isNaN(capacity) || Double.isInfinite(capacity) || capacity <= 0) return 0;
        double stored = getStoredRadiation(stack);
        double requested = Math.min(event.getRemainingExposure(), Math.max(0D, capacity - stored));
        // Do not redirect a dose too small to be represented by the stored double.
        if (!(stored + requested > stored)) return 0;
        double transferred = event.redirect(requested);
        if (transferred > 0) write(stack).setDouble(DOSE_KEY, stored + transferred);
        return transferred;
    }
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onExposure(RadiationExposureEvent event) {
        if (!(event.entity instanceof EntityPlayer) || event.entity.worldObj.isRemote || !config.isIrradiationPoppetEnabled()) return;
        EntityPlayer player = (EntityPlayer) event.entity;
        PoppetLocation poppet = findPoppet(player);
        if (poppet == null) return;
        boolean changed = absorb(poppet.stack, event, config.getPoppetCapacity()) > 0;
        if (getStoredRadiation(poppet.stack) >= config.getPoppetCapacity()) rupture(player, poppet);
        else if (changed) poppet.markDirty();
        // Overflow from this exposure reaches the player, rather than chaining into another poppet.
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer) || event.entityLiving.worldObj.isRemote
                || !config.isIrradiationPoppetEnabled()) return;
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        // Catch saved saturation/lowered capacity without running the world lookup every player tick.
        if (player.ticksExisted % 20 != 0) return;
        PoppetLocation poppet = findPoppet(player);
        if (poppet != null && getStoredRadiation(poppet.stack) >= config.getPoppetCapacity()) rupture(player, poppet);
    }

	// More annoyance... this was also super fun to try and get working without any api
    private PoppetLocation findPoppet(EntityPlayer player) {
        // Preserve the working carried protection path independently of the world-use lookup.
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            ItemStack carried = player.inventory.mainInventory[slot];
            if (carried != null && carried.stackSize == 1 && IrradiationPoppetRegistry.isIrradiationPoppet(carried)
                    && isBoundTo(carried, player)) return new PoppetLocation(player.inventory, slot, null, carried);
        }
        if (!lookupAvailable) return null;
        ItemStack stack;
        try {
            // This public overload has existing runtime evidence for zero-damage shelf lookup.
            // Let Witchery select shelf eligibility without assigning meanings to unknown flags.
            stack = ItemPoppet.findBoundPoppetInWorld(IrradiationPoppetRegistry.getIrradiationType(), player, 0);
        } catch (LinkageError | RuntimeException failure) {
            lookupAvailable = false;
            logger.error("Native Witchery shelf lookup failed; shelf radiation protection unavailable for this session. Carried protection remains active.", failure);
            return null;
        }
        if (stack == null || stack.stackSize != 1 || !IrradiationPoppetRegistry.isIrradiationPoppet(stack)) return null;
        int slot = findSlot(player.inventory, stack);
        if (slot >= 0) return new PoppetLocation(player.inventory, slot, null, stack);
        // Locate only the stack Witchery selected; this does not recreate its shelf selection policy.
        for (WorldServer world : DimensionManager.getWorlds()) {
            for (Object loaded : world.loadedTileEntityList) {
                if (!(loaded instanceof TileEntityPoppetShelf)) continue;
                TileEntityPoppetShelf shelf = (TileEntityPoppetShelf) loaded;
                if (shelf.isInvalid() || shelf.getWorldObj() != world) continue;
                slot = findSlot(shelf, stack);
                if (slot >= 0) return new PoppetLocation(shelf, slot, shelf, stack);
            }
        }
        if (!missingLocationLogged) {
            missingLocationLogged = true;
            logger.error("Native Witchery lookup returned a poppet without a live inventory/shelf slot; radiation was not redirected.");
        }
        return null;
    }

    static int findSlot(IInventory inventory, ItemStack stack) {
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (inventory.getStackInSlot(slot) == stack) return slot;
        }
        return -1;
    }

    private void rupture(EntityPlayer player, PoppetLocation poppet) {
        double stored = getStoredRadiation(poppet.stack);
        TileEntity shelf = poppet.shelf;
        // Deposit first: consumption must never discard the accumulated dose.
        if (shelf == null) TemporaryRadiationRelease.add(player.worldObj, player.posX, player.posY, player.posZ, stored);
        else TemporaryRadiationRelease.add(shelf.getWorldObj(), shelf.xCoord + .5D, shelf.yCoord + .5D, shelf.zCoord + .5D, stored);
        write(poppet.stack).setDouble(DOSE_KEY, 0D);
        poppet.stack.stackSize = 0;
        poppet.inventory.setInventorySlotContents(poppet.slot, null);
        poppet.markDirty();
    }

    /** Location is transient and resolved by identity; no shelf registry or lifecycle cache. */
    private static final class PoppetLocation {
        final IInventory inventory;
        final int slot;
        final TileEntity shelf;
        final ItemStack stack;

        PoppetLocation(IInventory inventory, int slot, TileEntity shelf, ItemStack stack) {
            this.inventory = inventory;
            this.slot = slot;
            this.shelf = shelf;
            this.stack = stack;
        }

        void markDirty() {
            inventory.markDirty();
            if (shelf != null) shelf.getWorldObj().markBlockForUpdate(shelf.xCoord, shelf.yCoord, shelf.zCoord);
        }
    }

    /** Witchery owns binding state; query only its established native contracts. */
    private boolean isBoundTo(ItemStack stack, EntityLivingBase target) {
        if (!bindingAvailable) return false;
        try {
            return Witchery.Items.TAGLOCK_KIT.isTaglockPresent(stack, 1)
                    && Witchery.Items.TAGLOCK_KIT.getBoundEntity(target.worldObj, null, stack, 1) == target;
        } catch (LinkageError | RuntimeException failure) {
            bindingAvailable = false;
            logger.error("Native Witchery binding query failed; protection and target-specific hazard exemption unavailable for this session.", failure);
            return false;
        }
    }

    @Override
    public float modify(ItemStack stack, EntityLivingBase holder, float level) {
        if (holder != null && holder.worldObj != null && !holder.worldObj.isRemote && isBoundTo(stack, holder)) return 0F;
        return getEmissionRate(stack);
    }

    @Override public boolean matches(InventoryCrafting grid, World world) { return nativeRecipe.matches(grid, world); }

    @Override public ItemStack getCraftingResult(InventoryCrafting grid) {
        ItemStack output = nativeRecipe.getCraftingResult(grid);
        if (!IrradiationPoppetRegistry.isIrradiationPoppet(output)) return output;
        for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
            ItemStack input = grid.getStackInSlot(slot);
            if (IrradiationPoppetRegistry.isIrradiationPoppet(input) && input.hasTagCompound()
                    && input.getTagCompound().hasKey(STATE_KEY, 10)) {
                if (!output.hasTagCompound()) output.setTagCompound(new NBTTagCompound());
                output.getTagCompound().setTag(STATE_KEY,
                        input.getTagCompound().getCompoundTag(STATE_KEY).copy());
                break;
            }
        }
        return output;
    }

    @Override public int getRecipeSize() { return nativeRecipe.getRecipeSize(); }
    @Override public ItemStack getRecipeOutput() { return IrradiationPoppetRegistry.createPoppet(); }
}
