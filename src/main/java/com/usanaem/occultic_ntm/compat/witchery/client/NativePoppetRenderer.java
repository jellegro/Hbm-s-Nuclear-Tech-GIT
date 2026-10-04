package com.usanaem.occultic_ntm.compat.witchery.client;

import com.emoniph.witchery.Witchery;
import com.hbm.blocks.ITooltipProvider;
import com.usanaem.occultic_ntm.compat.witchery.IrradiationPoppet;
import com.usanaem.occultic_ntm.compat.witchery.IrradiationPoppetRegistry;
import com.usanaem.occultic_ntm.config.IntegrationConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

/** Physical-client boundary: native icon layers, texture reloads, and dose tooltip.
 * Ordinary Witchery variants delegate to their original renderer or vanilla rendering.
 */
public final class NativePoppetRenderer implements IItemRenderer, ITooltipProvider {
    public static void initialize(IntegrationConfig config, Logger logger) {
        IItemRenderer previous = null;
        List<ItemStack> variants = new ArrayList<ItemStack>();
        Witchery.Items.POPPET.getSubItems(Witchery.Items.POPPET, Witchery.Items.POPPET.getCreativeTab(), variants);
        for (ItemStack variant : variants) {
            for (IItemRenderer.ItemRenderType renderType : IItemRenderer.ItemRenderType.values()) {
                IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(variant, renderType);
                if (renderer != null) previous = renderer;
            }
        }
        NativePoppetRenderer renderer = new NativePoppetRenderer(config,
                Witchery.Items.POPPET.unboundPoppet.createStack(), previous);
        MinecraftForgeClient.registerItemRenderer(Witchery.Items.POPPET, renderer);
        MinecraftForge.EVENT_BUS.register(renderer);
        logger.info("Native Irradiation Poppet renderer handles subtype 12 only; other variants retain their renderer.");
    }

    /** Register after all mods' init/post-init tooltip handlers are installed. */
    public static void initializeNei(Logger logger) {
        ItemStack poppet = IrradiationPoppetRegistry.createPoppet();
        if (poppet == null) return;
        com.usanaem.occultic_ntm.compat.nei.client.NeiPoppetPresentation.initialize(poppet);
        logger.info("NEI Irradiation Poppet attribution handler registered, including final hovered-stack tooltip callback; registry and mod-search ownership unchanged.");
    }

    private final IntegrationConfig config;
    private final ItemStack base;
    private final IItemRenderer delegate;
    private IIcon overlay;

    public NativePoppetRenderer(IntegrationConfig config, ItemStack base, IItemRenderer delegate) {
        this.config = config;
        this.base = base.copy();
        this.delegate = delegate;
    }

    @SubscribeEvent
    public void stitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 1) overlay = event.map.registerIcon("occultic_ntm:irradiation_poppet_overlay");
    }

    @Override public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        return IrradiationPoppetRegistry.isIrradiationPoppet(stack) || delegate != null && delegate.handleRenderType(stack, type);
    }
    @Override public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
        if (!IrradiationPoppetRegistry.isIrradiationPoppet(stack)) return delegate != null && delegate.shouldUseRenderHelper(type, stack, helper);
        return type == ItemRenderType.ENTITY && (helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION);
    }

    public int overlayColor(ItemStack stack) {
        double fill = Math.min(1D, IrradiationPoppet.getStoredRadiation(stack) / config.getPoppetCapacity());
        return (int) Math.round(32 + 96 * fill) << 16 | (int) Math.round(72 + 183 * fill) << 8
                | (int) Math.round(24 + 24 * fill);
    }

    @Override public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (!IrradiationPoppetRegistry.isIrradiationPoppet(stack)) {
            if (delegate != null) delegate.renderItem(type, stack, data);
            return;
        }
        // Query again after resource reload; never cache the dependency's icon.
        IIcon icon = base.getItem().getIcon(base, 0);
        if (icon == null) return;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        GL11.glPushMatrix();
        try {
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationItemsTexture);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if (type == ItemRenderType.INVENTORY) GL11.glDisable(GL11.GL_LIGHTING);
            if (type == ItemRenderType.ENTITY) GL11.glTranslatef(-.5F, -.25F, 0);
            tint(base.getItem().getColorFromItemStack(base, 0));
            layer(icon, type, 0);
            if (overlay != null) { tint(overlayColor(stack)); layer(overlay, type, .001F); }
        } finally { GL11.glPopMatrix(); GL11.glPopAttrib(); }
    }

    private static void tint(int rgb) {
        GL11.glColor4f((rgb >> 16 & 255) / 255F, (rgb >> 8 & 255) / 255F, (rgb & 255) / 255F, 1);
    }

    private static void layer(IIcon icon, ItemRenderType type, float z) {
        Tessellator tess = Tessellator.instance;
        if (type != ItemRenderType.INVENTORY) {
            GL11.glTranslatef(0, 0, z);
            ItemRenderer.renderItemIn2D(tess, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(),
                    icon.getIconWidth(), icon.getIconHeight(), .0625F);
            return;
        }
        tess.startDrawingQuads();
        tess.addVertexWithUV(0, 16, z, icon.getMinU(), icon.getMaxV());
        tess.addVertexWithUV(16, 16, z, icon.getMaxU(), icon.getMaxV());
        tess.addVertexWithUV(16, 0, z, icon.getMaxU(), icon.getMinV());
        tess.addVertexWithUV(0, 0, z, icon.getMinU(), icon.getMinV());
        tess.draw();
    }

    @SubscribeEvent
    public void tooltip(ItemTooltipEvent event) {
        if (!IrradiationPoppetRegistry.isIrradiationPoppet(event.itemStack)) return;
        addInformation(event.itemStack, event.entityPlayer, event.toolTip, event.showAdvancedItemTooltips);
    }

    @Override public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        double stored = IrradiationPoppet.getStoredRadiation(stack);
        list.add(StatCollector.translateToLocalFormatted("occultic_ntm.poppet.stored", number(stored)));
        list.add(StatCollector.translateToLocalFormatted("occultic_ntm.poppet.capacity", number(config.getPoppetCapacity())));
        List<String> details = new ArrayList<String>();
        details.add(StatCollector.translateToLocal("occultic_ntm.poppet.absorption"));
        details.add(StatCollector.translateToLocal("occultic_ntm.poppet.bound_safe"));
        details.add(StatCollector.translateToLocalFormatted("occultic_ntm.poppet.emission", number(IrradiationPoppet.getEmissionRate(stack))));
        details.add(StatCollector.translateToLocal("occultic_ntm.poppet.rupture"));
        if (stored >= config.getPoppetCapacity()) details.add(StatCollector.translateToLocal("occultic_ntm.poppet.full"));
//        details.add(StatCollector.translateToLocal("occultic_ntm.poppet.protection_location"));
        if (!config.isIrradiationPoppetEnabled()) details.add(StatCollector.translateToLocal("occultic_ntm.poppet.inactive"));
        // HBM's standard helper owns the Shift check, hint, formatting and '$'
        // line splitting. Its translator accepts resolved text as a fallback key.
        addStandardInfo(stack, String.join("$", details), player, list, advanced);
    }

    private static String number(double value) { return String.format(Locale.ROOT, "%.3f", value); }
}
