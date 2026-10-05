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
 * Atramentous Ingot: An impossible heavy metal formed by forcing radioactive waste
 * and base actinides into an unnatural alchemical ground state using the Black Sun.
 */
public class ItemAtramentousIngot extends Item {

    public ItemAtramentousIngot() {
        setUnlocalizedName("occultic_ntm.ingot_atramentous");
        setTextureName("occultic_ntm:ingot_atramentous");
        setCreativeTab(OcculticNTM.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(StatCollector.translateToLocal("occultic_ntm.tooltip.ingot_atramentous.1"));
        list.add(StatCollector.translateToLocal("occultic_ntm.tooltip.ingot_atramentous.2"));
    }
}
