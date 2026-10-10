package com.usanaem.occultic_ntm.compat.witchery.rites;

import com.emoniph.witchery.ritual.Circle;

/**
 * Circle builders for Occultic NTM Witchery rituals.
 * Standardizes native Witchery ring sizes and provides first-class Radiant Chalk integration.
 */
public final class OcculticCircles {
    private OcculticCircles() { }

    /** 7x7 Inner Ring (radius 3) requiring 16 Otherwhere glyphs */
    public static Circle otherwhereSmall() {
        return new Circle(0, 16, 0);
    }

    /** 11x11 Middle Ring (radius 5) requiring 28 Otherwhere glyphs */
    public static Circle otherwhereMedium() {
        return new Circle(0, 28, 0);
    }

    /** 15x15 Outer Ring (radius 7) requiring 40 Otherwhere glyphs */
    public static Circle otherwhereLarge() {
        return new Circle(0, 40, 0);
    }

    /** 7x7 Inner Ring (radius 3) requiring 16 White glyphs */
    public static Circle whiteSmall() {
        return new Circle(16, 0, 0);
    }

    /** 11x11 Middle Ring (radius 5) requiring 28 White glyphs */
    public static Circle whiteMedium() {
        return new Circle(28, 0, 0);
    }

    /** 15x15 Outer Ring (radius 7) requiring 40 White glyphs */
    public static Circle whiteLarge() {
        return new Circle(40, 0, 0);
    }

    /** 7x7 Inner Ring (radius 3) requiring 16 Infernal glyphs */
    public static Circle infernalSmall() {
        return new Circle(0, 0, 16);
    }

    /** 11x11 Middle Ring (radius 5) requiring 28 Infernal glyphs */
    public static Circle infernalMedium() {
        return new Circle(0, 0, 28);
    }

    /** 15x15 Outer Ring (radius 7) requiring 40 Infernal glyphs */
    public static Circle infernalLarge() {
        return new Circle(0, 0, 40);
    }

    /** 7x7 Inner Ring (radius 3) requiring 16 Radiant glyphs */
    public static Circle radiantSmall() {
        return markRadiant(new Circle(16, 0, 0));
    }

    /** 11x11 Middle Ring (radius 5) requiring 28 Radiant glyphs */
    public static Circle radiantMedium() {
        return markRadiant(new Circle(28, 0, 0));
    }

    /** 15x15 Outer Ring (radius 7) requiring 40 Radiant glyphs */
    public static Circle radiantLarge() {
        return markRadiant(new Circle(40, 0, 0));
    }

    /** Tags any circle instance to strictly demand Radiant Chalk via IRadiantCircle. */
    public static Circle markRadiant(Circle circle) {
        if (circle instanceof IRadiantCircle) {
            ((IRadiantCircle) circle).occultic$setRadiant(true);
        }
        return circle;
    }
}
