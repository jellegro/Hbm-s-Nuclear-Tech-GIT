package com.usanaem.occultic_ntm.registry;

import com.usanaem.occultic_ntm.OcculticNTM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import java.util.List;

/** All visible fork content belongs to this tab. */
public final class OcculticCreativeTab extends CreativeTabs {
    private ItemStack poppetStack;

    public OcculticCreativeTab() { super(OcculticNTM.MOD_ID); }

    /** Common data supplied by the optional integration; this tab never loads Witchery. */
    public void setPoppetStack(ItemStack stack) { poppetStack = stack.copy(); }

    public ItemStack getPoppetStack() { return poppetStack == null ? null : poppetStack.copy(); }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() { return getIconItemStack().getItem(); }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getIconItemStack() {
        ItemStack nativeStack = getPoppetStack();
        return nativeStack == null ? new ItemStack(Items.leather) : nativeStack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void displayAllReleventItems(List items) {
        super.displayAllReleventItems(items);
        ItemStack nativeStack = getPoppetStack();
        if (nativeStack != null) items.add(nativeStack);
    }
}
