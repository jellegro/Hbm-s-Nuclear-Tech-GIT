package com.usanaem.occultic_hbm.compat.thaumcraft.catalogue;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public final class CatalogueHelper {

    private CatalogueHelper() { }

    public static AspectAmounts a() {
        return new AspectAmounts();
    }

    public static AspectAmounts a(String tag, int amount) {
        return new AspectAmounts().add(tag, amount);
    }

    public static AspectAmounts a(String tag1, int amount1, String tag2, int amount2) {
        return new AspectAmounts().add(tag1, amount1).add(tag2, amount2);
    }

    public static AspectAmounts a(String tag1, int amount1, String tag2, int amount2, String tag3, int amount3) {
        return new AspectAmounts().add(tag1, amount1).add(tag2, amount2).add(tag3, amount3);
    }

    public static AspectAmounts a(String tag1, int amount1, String tag2, int amount2, String tag3, int amount3, String tag4, int amount4) {
        return new AspectAmounts().add(tag1, amount1).add(tag2, amount2).add(tag3, amount3).add(tag4, amount4);
    }

    public static AspectAmounts a(String tag1, int amount1, String tag2, int amount2, String tag3, int amount3, String tag4, int amount4, String tag5, int amount5) {
        return new AspectAmounts().add(tag1, amount1).add(tag2, amount2).add(tag3, amount3).add(tag4, amount4).add(tag5, amount5);
    }

    public static AspectAmounts a(String tag1, int amount1, String tag2, int amount2, String tag3, int amount3, String tag4, int amount4, String tag5, int amount5, String tag6, int amount6) {
        return new AspectAmounts().add(tag1, amount1).add(tag2, amount2).add(tag3, amount3).add(tag4, amount4).add(tag5, amount5).add(tag6, amount6);
    }

    public static AspectAmounts a(String tag1, int amount1, String tag2, int amount2, String tag3, int amount3, String tag4, int amount4, String tag5, int amount5, String tag6, int amount6, String tag7, int amount7) {
        return new AspectAmounts().add(tag1, amount1).add(tag2, amount2).add(tag3, amount3).add(tag4, amount4).add(tag5, amount5).add(tag6, amount6).add(tag7, amount7);
    }

    public static void reg(Item item, AspectAmounts aspects) {
        CatalogueRegistry.register(item, 0, aspects);
    }

    public static void reg(Item item, int metadata, AspectAmounts aspects) {
        CatalogueRegistry.register(item, metadata, aspects);
    }

    public static void regWildcard(Item item, AspectAmounts aspects) {
        CatalogueRegistry.registerWildcard(item, aspects);
    }

    public static void reg(Block block, AspectAmounts aspects) {
        CatalogueRegistry.register(block, 0, aspects);
    }

    public static void reg(Block block, int metadata, AspectAmounts aspects) {
        CatalogueRegistry.register(block, metadata, aspects);
    }

    public static void skip(String name, String reason) {
        CatalogueRegistry.markSkipped(name, reason);
    }
}
