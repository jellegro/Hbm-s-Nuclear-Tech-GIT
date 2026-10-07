package com.usanaem.occultic_ntm.mixin.witchery;

import com.usanaem.occultic_ntm.bootstrap.GlyphCircleQueries;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Uses only the established public API signatures, without inspecting native implementation. */
@Mixin(targets = "com.emoniph.witchery.ritual.Circle", remap = false)
public abstract class MixinCircleGlyphQuery {
    @Inject(method = "addGlyph(Lnet/minecraft/world/World;III)V", at = @At("HEAD"), remap = false)
    private void occultic$enter(World world, int x, int y, int z, CallbackInfo callback) { GlyphCircleQueries.enter(world, x, y, z); }
    @Inject(method = "addGlyph(Lnet/minecraft/world/World;IIIZ)V", at = @At("HEAD"), remap = false)
    private void occultic$enterFlag(World world, int x, int y, int z, boolean flag, CallbackInfo callback) { GlyphCircleQueries.enter(world, x, y, z); }
    @Inject(method = {"addGlyph(Lnet/minecraft/world/World;III)V", "addGlyph(Lnet/minecraft/world/World;IIIZ)V"}, at = @At("RETURN"), remap = false)
    private void occultic$leave(CallbackInfo callback) { GlyphCircleQueries.leave(); }
}
