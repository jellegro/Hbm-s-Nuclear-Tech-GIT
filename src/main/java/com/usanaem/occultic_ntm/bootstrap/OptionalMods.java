package com.usanaem.occultic_ntm.bootstrap;

import cpw.mods.fml.common.Loader;

/** Presence checks must run during lifecycle handling, never in static initialization. */
public final class OptionalMods {

    public static final String HBM = "hbm";
    public static final String THAUMCRAFT = "Thaumcraft";
    public static final String WITCHERY = "witchery";
    public static final String NEI = "NotEnoughItems";
	
    private OptionalMods() { }

    public static boolean isThaumcraftLoaded() {
        return Loader.isModLoaded(THAUMCRAFT);
    }

    public static boolean isWitcheryLoaded() { return Loader.isModLoaded(WITCHERY); }
    public static boolean isNeiLoaded() { return Loader.isModLoaded(NEI); }
}
