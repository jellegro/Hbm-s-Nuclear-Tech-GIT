package com.usanaem.occultic_ntm.haunting;

import net.minecraft.entity.passive.EntityWolf;

/** Vanilla companions only. No aggression flag or attack target is changed. */
public final class CompanionAwareness {
    private CompanionAwareness() { }
    public static void react(EntityHerobrine figure) {
        HauntingActivity activity = HauntingDirector.activity(figure.worldObj);
        for (Object object : figure.worldObj.getEntitiesWithinAABB(EntityWolf.class, figure.boundingBox.expand(activity.companionRange, 16, activity.companionRange))) {
            EntityWolf wolf = (EntityWolf) object;
            if (!wolf.isTamed() || !wolf.isSitting() || wolf.getAttackTarget() != null
                    || wolf.getDistanceSqToEntity(figure) > activity.companionRange * activity.companionRange) continue;
            wolf.getLookHelper().setLookPosition(figure.posX, figure.posY + 1.6, figure.posZ, 30, 30);
            if (figure.ticksExisted % activity.growlTicks == 0) wolf.playSound("mob.wolf.growl", .2F, .85F);
        }
    }
    public static boolean hint(net.minecraft.entity.player.EntityPlayerMP player, HauntingActivity activity) {
        double angle = Math.toRadians(player.rotationYaw + 180), x = player.posX - Math.sin(angle) * 8, z = player.posZ + Math.cos(angle) * 8;
        if (!HauntingPlacement.loaded(player.worldObj, x, z, player.posX, player.posZ)) return false;
        boolean reacted = false;
        for (Object object : player.worldObj.getEntitiesWithinAABB(EntityWolf.class, player.boundingBox.expand(16, 8, 16))) {
            EntityWolf wolf = (EntityWolf) object;
            if (!wolf.isTamed() || !wolf.isSitting() || wolf.getAttackTarget() != null || !player.getUniqueID().toString().equals(wolf.func_152113_b())) continue;
            wolf.getLookHelper().setLookPosition(x, player.posY + 1.6, z, 30, 30);
            wolf.playSound("mob.wolf.growl", activity == HauntingActivity.QUIET ? .12F : .2F, .85F); reacted = true;
        }
        return reacted;
    }
}
