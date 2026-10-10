package com.usanaem.occultic_ntm.compat.witchery.rites;

/**
 * Injected into Witchery's com.emoniph.witchery.ritual.Circle via MixinCircle.
 * Allows ritual circles to declare whether they require Radiant Glyphs.
 */
public interface IRadiantCircle {
    boolean occultic$isRadiant();
    void occultic$setRadiant(boolean radiant);
}
