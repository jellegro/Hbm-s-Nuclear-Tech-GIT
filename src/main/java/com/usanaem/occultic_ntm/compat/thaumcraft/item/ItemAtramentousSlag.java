package com.usanaem.occultic_ntm.compat.thaumcraft.item;

import java.util.List;

import com.usanaem.occultic_ntm.OcculticNTM;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

/**
 * Atramentous Slag: Vitrified, slightly radioactive clinker residue
 * left behind after radioactive matter is devoured by the Black Sun.
 */
public class ItemAtramentousSlag extends Item {

    public ItemAtramentousSlag() {
        setUnlocalizedName("occultic_ntm.slag_atramentous");
        setTextureName("occultic_ntm:slag_atramentous");
        setCreativeTab(OcculticNTM.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(StatCollector.translateToLocal("occultic_ntm.tooltip.slag_atramentous.1"));
    }
}
