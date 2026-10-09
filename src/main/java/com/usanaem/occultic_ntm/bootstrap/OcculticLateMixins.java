package com.usanaem.occultic_ntm.bootstrap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

/** No optional-mod types or class loading before Forge discovers ordinary mods. */
@LateMixin
public final class OcculticLateMixins implements ILateMixinLoader {
    @Override
    public String getMixinConfig() { return "mixins.occultic_ntm.witchery.json"; }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        if (!loadedMods.contains("witchery")) return Collections.emptyList();
        return Arrays.asList("accessor.MixinItemPoppetAccessor",
                "accessor.MixinPoppetTypeInvoker", "MixinCircle");
    }
}
