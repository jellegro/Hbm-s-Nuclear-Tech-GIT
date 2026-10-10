package com.usanaem.occultic_ntm.compat.witchery.mixin.accessor;

import java.util.ArrayList;
import com.emoniph.witchery.item.ItemPoppet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ItemPoppet.class, remap = false)
public interface MixinItemPoppetAccessor {
    @Accessor(value = "poppetTypes", remap = false)
    ArrayList<ItemPoppet.PoppetType> occultic$getPoppetTypes();
}
