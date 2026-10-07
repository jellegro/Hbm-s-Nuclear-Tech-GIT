package com.usanaem.occultic_ntm.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

/** Vanilla world query hook must be installed before Minecraft classes load.
 * No optional-mod class or registry is resolved during coremod discovery. */
@IFMLLoadingPlugin.Name("Occultic NTM Vanilla Hooks")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions("com.usanaem.occultic_ntm.bootstrap.OcculticCorePlugin")
public final class OcculticCorePlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {
    public String getMixinConfig() { return "mixins.occultic_ntm.glyphs.json"; }
    public List<String> getMixins(Set<String> loadedCoreMods) {
        List<String> mixins = new ArrayList<String>();
        mixins.add("MixinCircleBlockQuery");
        return mixins;
    }
    public String[] getASMTransformerClass() { return new String[0]; }
    public String getModContainerClass() { return null; }
    public String getSetupClass() { return null; }
    public void injectData(Map<String, Object> data) { }
    public String getAccessTransformerClass() { return null; }
}
