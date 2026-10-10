package com.usanaem.occultic_ntm.compat.witchery.item;

import com.emoniph.witchery.Witchery;
import com.hbm.hazard.HazardData;
import com.hbm.hazard.HazardSystem;
import com.hbm.hazard.type.HazardTypeRadiation;
import com.hbm.items.ModItems;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.usanaem.occultic_ntm.compat.witchery.block.BlockRadiantGlyph;
import com.usanaem.occultic_ntm.compat.witchery.rites.RadiantGlyphData;
import com.usanaem.occultic_ntm.config.IntegrationConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraft.block.Block;
import org.apache.logging.log4j.Logger;
import java.util.*;

/** Separate glyph identity, with a sparse hazard index and migration of old classified saves. */
public final class RadiantChalk {
    public static Item item;
    public static BlockRadiantGlyph glyph;
    private static boolean attempted;
    private RadiantChalk() { }

    public static void initialize(IntegrationConfig config, Logger logger) {
        if (attempted || !config.isRadiantChalkEnabled()) return;
        attempted = true;
        Item chalk = GameRegistry.findItem("witchery", "chalkritual");
        if (chalk == null || Witchery.Blocks.GLYPH_RITUAL == null) {
            logger.error("Radiant Chalk unavailable: native chalkritual item or white glyph missing.");
            return;
        }
        item = new ItemRadiantChalk();
        glyph = new BlockRadiantGlyph(Witchery.Blocks.GLYPH_RITUAL);
        // A hidden registered stack supports WAILA/pick-block identification; chalk owns placement.
        GameRegistry.registerBlock(glyph, ItemBlockRadiantGlyph.class, "radiant_glyph");
        GameRegistry.registerItem(item, "radiant_chalk");
        GameRegistry.addShapelessRecipe(new ItemStack(item), new ItemStack(chalk), new ItemStack(ModItems.powder_uranium), new ItemStack(Items.slime_ball));
        HazardSystem.register(item, new HazardData().addEntry(new HazardTypeRadiation(), 0.025F));
        RadiantChalk handler = new RadiantChalk();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance().bus().register(handler);
        logger.info("Radiant Chalk registered: distinct occultic_ntm:radiant_glyph, green native patterns, light level 3, mild HBM exposure.");
    }

    public static boolean isRadiantGlyph(World world, int x, int y, int z) {
        return glyph != null && world.blockExists(x, y, z) && world.getBlock(x, y, z) == glyph;
    }

    /** Material/count requirement on a ring already recognized by Witchery, not a geometry detector. */
    public static boolean hasRequiredRadiantGlyphs(World world, int x, int y, int z, int radius, int expected) {
        if (item == null || radius < 1 || radius > 7 || expected < 1) return false;
        int white = 0;
        for (int dx = -radius; dx <= radius; dx++) for (int dz = -radius; dz <= radius; dz++) {
            if (!world.blockExists(x + dx, y, z + dz)) return false;
            Block block = world.getBlock(x + dx, y, z + dz);
            if (block == Witchery.Blocks.GLYPH_RITUAL) return false;
            if (block == glyph) white++;
        }
        return white == expected;
    }

    @SubscribeEvent public void loaded(ChunkEvent.Load event) {
        if (event.world.isRemote || glyph == null) return;
        RadiantGlyphData data = RadiantGlyphData.find(event.world);
        if (data == null) return;
        for (int[] pos : data.inChunk(event.getChunk().xPosition, event.getChunk().zPosition)) migrate(event.world, data, pos);
    }
    private static void migrate(World world, RadiantGlyphData data, int[] pos) {
        Block block = world.getBlock(pos[0], pos[1], pos[2]);
        // Only saved radiant coordinates qualify. Ordinary native chalk is never transformed.
        if (block == Witchery.Blocks.GLYPH_RITUAL) {
            world.setBlock(pos[0], pos[1], pos[2], glyph, world.getBlockMetadata(pos[0], pos[1], pos[2]), 3);
        } else if (block != glyph) data.remove(pos[0], pos[1], pos[2]);
    }

    @SubscribeEvent public void tick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (event.phase != TickEvent.Phase.END || world.isRemote || world.getTotalWorldTime() % 20 != 0) return;
        RadiantGlyphData data = RadiantGlyphData.find(world);
        if (data == null) return;
        // Snapshot before radiation callbacks, so poppet rupture cannot invalidate iteration.
        Map<Long, List<int[]>> chunks = data.snapshot();
        if (chunks.isEmpty()) return;
        for (List<int[]> glyphs : chunks.values()) {
            if (glyphs.isEmpty()) continue;
            int gx = glyphs.get(0)[0], gz = glyphs.get(0)[2];
            if (!world.getChunkProvider().chunkExists(gx >> 4, gz >> 4)) continue;
            int minY = 255, maxY = 0;
            List<int[]> live = new ArrayList<int[]>();
            for (int[] pos : glyphs) {
                migrate(world, data, pos);
                if (world.getBlock(pos[0], pos[1], pos[2]) != glyph) continue;
                live.add(pos); minY = Math.min(minY, pos[1]); maxY = Math.max(maxY, pos[1]);
            }
            if (live.isEmpty()) continue;
            int chunkX = (gx >> 4) << 4, chunkZ = (gz >> 4) << 4;
            List<?> targets = world.getEntitiesWithinAABB(EntityLivingBase.class,
                    AxisAlignedBB.getBoundingBox(chunkX - 6, minY - 6, chunkZ - 6, chunkX + 22, maxY + 7, chunkZ + 22));
            for (Object object : targets) {
                EntityLivingBase target = (EntityLivingBase) object;
                float amount = 0;
                for (int[] pos : live) {
                    double distance = target.getDistance(pos[0] + 0.5, pos[1] + 0.1, pos[2] + 0.5);
                    if (distance < 6) amount += 0.10F * (float) Math.max(0.25, 1 - distance / 6);
                }
                if (amount > 0) ContaminationUtil.contaminate(target, HazardType.RADIATION, ContaminationType.CREATIVE, amount);
            }
        }
    }
}
