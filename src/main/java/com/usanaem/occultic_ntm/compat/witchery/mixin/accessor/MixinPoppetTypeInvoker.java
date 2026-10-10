package com.usanaem.occultic_ntm.compat.witchery.mixin.accessor;

import java.util.ArrayList;
import com.emoniph.witchery.item.ItemPoppet.PoppetType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = PoppetType.class, remap = false)
public interface MixinPoppetTypeInvoker {
    @Invoker(value = "<init>", remap = false)
    static PoppetType occultic$create(int id, String key, String displayName) {
        throw new AssertionError("UniMixins constructor bridge was not transformed");
    }
	
    @Invoker(value = "register", remap = false)
    static PoppetType occultic$register(PoppetType type, ArrayList<PoppetType> types) {
        throw new AssertionError("UniMixins registration bridge was not transformed");
    }
}
