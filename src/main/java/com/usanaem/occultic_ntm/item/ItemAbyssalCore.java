package com.usanaem.occultic_ntm.item;

import java.util.List;

import com.usanaem.occultic_ntm.OcculticNTM;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

/**
 * Abyssal Fuel Core: A hyperdense occult fuel core that burns with cold, silent criticality,
 * feeding on the void between atoms and releasing severe ionizing and eldritch radiation.
 */
public class ItemAbyssalCore extends Item {

    public ItemAbyssalCore() {
        setUnlocalizedName("occultic_ntm.core_abyssal");
        setTextureName("occultic_ntm:core_abyssal");
        setMaxStackSize(16);
        setCreativeTab(OcculticNTM.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(StatCollector.translateToLocal("occultic_ntm.tooltip.core_abyssal.1"));
        list.add(StatCollector.translateToLocal("occultic_ntm.tooltip.core_abyssal.2"));
        list.add(StatCollector.translateToLocal("occultic_ntm.tooltip.core_abyssal.3"));
    }
}
