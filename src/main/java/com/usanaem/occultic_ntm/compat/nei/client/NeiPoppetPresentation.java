package com.usanaem.occultic_ntm.compat.nei.client;

import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerTooltipHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

/** NEI presentation only: the supplied native poppet keeps its real registry owner. 
 * Super annoying, but all this does is replace the witchery mod id on the item display with the one from this mod
 * */
@SideOnly(Side.CLIENT)
public final class NeiPoppetPresentation implements IContainerTooltipHandler {
    private static boolean registered;
    private final Item poppetItem;
    private final int subtype;

    public static void initialize(ItemStack nativePoppet) {
        if (registered) return;
        GuiContainerManager.addTooltipHandler(new NeiPoppetPresentation(nativePoppet));
        registered = true;
    }

    public NeiPoppetPresentation(ItemStack nativePoppet) {
        poppetItem = nativePoppet.getItem();
        subtype = nativePoppet.getItemDamage();
    }

    @Override public List<String> handleTooltip(GuiContainer gui, int x, int y, List<String> tooltip) {
        // The item/name callbacks can run before NEI adds its owner line. Use the
        // final tooltip callback too, resolving its stack through NEI's public API.
        return gui == null ? tooltip : attribution(GuiContainerManager.getStackMouseOver(gui), tooltip);
    }

    @Override public List<String> handleItemDisplayName(GuiContainer gui, ItemStack stack, List<String> tooltip) {
        return attribution(stack, tooltip);
    }

    @Override public List<String> handleItemTooltip(GuiContainer gui, ItemStack stack, int x, int y, List<String> tooltip) {
        return attribution(stack, tooltip);
    }

    // GTNH 2.8.155 adds this public callback; omit @Override to also compile against NEI 1.0.3.74.
    public Map<String, String> handleHotkeys(GuiContainer gui, int x, int y, Map<String, String> hotkeys) { return hotkeys; }

    private List<String> attribution(ItemStack stack, List<String> tooltip) {
        if (stack == null || stack.getItem() != poppetItem || stack.getItemDamage() != subtype) return tooltip;
        // Replace only the complete owner line, never the name or native binding/radiation text.
        for (int index = tooltip.size() - 1; index > 0; index--) {
            String line = tooltip.get(index);
            if (!"Witchery".equals(EnumChatFormatting.getTextWithoutFormattingCodes(line))) continue;
            int name = line.indexOf("Witchery");
            if (name < 0) continue;
            String prefix = line.substring(0, name);
            // NEI's blue/italic owner line is distinct from a target named "Witchery".
            if (!prefix.contains(EnumChatFormatting.BLUE.toString()) || !prefix.contains(EnumChatFormatting.ITALIC.toString())) continue;
            String replacement = line.substring(0, name) + "Occultic NTM" + line.substring(name + "Witchery".length());
            // Keep the supplied shared list consistent even if the caller retains
            // it instead of using the returned list.
            try {
                tooltip.set(index, replacement);
                return tooltip;
            } catch (UnsupportedOperationException immutable) {
                List<String> result = new ArrayList<String>(tooltip);
                result.set(index, replacement);
                return result;
            }
        }
        // Respect NEI's attribution visibility setting; do not append a missing owner line.
        return tooltip;
    }
}
