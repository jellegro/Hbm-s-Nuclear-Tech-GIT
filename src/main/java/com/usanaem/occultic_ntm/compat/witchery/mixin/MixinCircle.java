package com.usanaem.occultic_ntm.compat.witchery.mixin;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.ritual.Circle;
import com.usanaem.occultic_ntm.compat.witchery.rites.IRadiantCircle;
import com.usanaem.occultic_ntm.compat.witchery.item.RadiantChalk;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Circle.class, remap = false)
public abstract class MixinCircle implements IRadiantCircle {

    @Unique
    private boolean occultic$radiant;

    @Override
    public boolean occultic$isRadiant() {
        return this.occultic$radiant;
    }

    @Override
    public void occultic$setRadiant(boolean radiant) {
        this.occultic$radiant = radiant;
    }

    @Unique
    private Block occultic$redirectBlock(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (RadiantChalk.glyph != null && block == RadiantChalk.glyph) {
            this.occultic$radiant = true;
            return Witchery.Blocks.GLYPH_RITUAL;
        }
        return block;
    }

    @Redirect(
        method = {
            "addGlyph(Lnet/minecraft/world/World;III)V",
            "addGlyph(Lnet/minecraft/world/World;IIIZ)V"
        },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"
        ),
        require = 0,
        remap = false
    )
    private Block occultic$filterGlyph(World world, int x, int y, int z) {
        return occultic$redirectBlock(world, x, y, z);
    }

    @Redirect(
        method = {
            "addGlyph(Lnet/minecraft/world/World;III)V",
            "addGlyph(Lnet/minecraft/world/World;IIIZ)V"
        },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;func_147439_a(III)Lnet/minecraft/block/Block;"
        ),
        require = 0,
        remap = false
    )
    private Block occultic$filterGlyphSrg(World world, int x, int y, int z) {
        return occultic$redirectBlock(world, x, y, z);
    }

    @Inject(
        method = "isMatch(Lcom/emoniph/witchery/ritual/Circle;)Z",
        at = @At("HEAD"),
        cancellable = true,
        require = 0,
        remap = false
    )
    private void occultic$checkRadiantMatch(Circle obj, CallbackInfoReturnable<Boolean> cir) {
        if (obj instanceof IRadiantCircle) {
            if (this.occultic$radiant != ((IRadiantCircle) obj).occultic$isRadiant()) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(
        method = "equals",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void occultic$checkRadiantEquals(Object obj, CallbackInfoReturnable<Boolean> cir) {
        if (obj instanceof IRadiantCircle) {
            if (this.occultic$radiant != ((IRadiantCircle) obj).occultic$isRadiant()) {
                cir.setReturnValue(false);
            }
        }
    }
}
