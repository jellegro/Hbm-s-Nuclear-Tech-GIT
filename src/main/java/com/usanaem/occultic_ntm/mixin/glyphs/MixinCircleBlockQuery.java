package com.usanaem.occultic_ntm.mixin.glyphs;

import com.usanaem.occultic_ntm.bootstrap.GlyphCircleQueries;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class MixinCircleBlockQuery {
    @Inject(method = "getBlock(III)Lnet/minecraft/block/Block;", at = @At("RETURN"), cancellable = true)
    private void occultic$circleBlock(int x, int y, int z, CallbackInfoReturnable<Block> result) {
        Block actual = result.getReturnValue();
        Block resolved = GlyphCircleQueries.resolve((World) (Object) this, x, y, z, actual);
        if (resolved != actual) result.setReturnValue(resolved);
    }
}
