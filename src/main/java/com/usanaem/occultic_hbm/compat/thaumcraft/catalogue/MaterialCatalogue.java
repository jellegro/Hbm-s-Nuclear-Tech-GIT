package com.usanaem.occultic_hbm.compat.thaumcraft.catalogue;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.Item;
import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.items.ModItems;
import static com.usanaem.occultic_hbm.compat.thaumcraft.catalogue.CatalogueHelper.*;

public final class MaterialCatalogue {

    private static final Map<NTMMaterial, AspectAmounts> BASE_ASPECTS = new HashMap<NTMMaterial, AspectAmounts>();

    private MaterialCatalogue() { }

    public static void register() {
        initBaseAspects();
        registerAutogenShapes();
        registerStandaloneIngots();
        registerStandaloneNuggets();
        registerStandalonePowders();
        registerStandalonePlates();
        registerStandaloneBillets();
        registerStandaloneBlocks();
        registerStandaloneOres();
        registerMiscellaneousMaterials();
    }

    private static void initBaseAspectsChunk0() {
        BASE_ASPECTS.put(Mats.MAT_WOOD, a("arbor", 2, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_IVORY, a("corpus", 2, "spiritus", 1, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_STONE, a("terra", 2, "perditio", 1));
        BASE_ASPECTS.put(Mats.MAT_CARBON, a("ignis", 1, "terra", 1, "vinculum", 1));
        BASE_ASPECTS.put(Mats.MAT_COAL, a("potentia", 2, "ignis", 2, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_LIGNITE, a("potentia", 1, "ignis", 1, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_COALCOKE, a("potentia", 3, "ignis", 3, "chemica", 1));
        BASE_ASPECTS.put(Mats.MAT_PETCOKE, a("potentia", 3, "ignis", 3, "chemica", 2));
        BASE_ASPECTS.put(Mats.MAT_LIGCOKE, a("potentia", 2, "ignis", 2, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_GRAPHITE, a("ordo", 2, "terra", 2, "electrum", 1));
        BASE_ASPECTS.put(Mats.MAT_DIAMOND, a("vitreus", 3, "lucrum", 2, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_IRON, a("metallum", 3));
        BASE_ASPECTS.put(Mats.MAT_GOLD, a("metallum", 2, "lucrum", 2, "electrum", 1));
        BASE_ASPECTS.put(Mats.MAT_REDSTONE, a("electrum", 3, "potentia", 2, "machina", 1));
        BASE_ASPECTS.put(Mats.MAT_OBSIDIAN, a("terra", 2, "ignis", 2, "tenebrae", 1));
        BASE_ASPECTS.put(Mats.MAT_HEMATITE, a("metallum", 2, "terra", 2));
        BASE_ASPECTS.put(Mats.MAT_WROUGHTIRON, a("metallum", 3, "ordo", 1));
        BASE_ASPECTS.put(Mats.MAT_PIGIRON, a("metallum", 3, "ignis", 1));
        BASE_ASPECTS.put(Mats.MAT_METEORICIRON, a("metallum", 3, "alienis", 2, "nebrisum", 1));
        BASE_ASPECTS.put(Mats.MAT_MALACHITE, a("metallum", 2, "vitreus", 2, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_BAUXITE, a("metallum", 2, "terra", 2, "volatus", 1));
        BASE_ASPECTS.put(Mats.MAT_CRYOLITE, a("gelum", 2, "chemica", 2, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_URANIUM, a("metallum", 2, "radio", 2, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_U233, a("metallum", 2, "strontio", 3, "potentia", 2));
        BASE_ASPECTS.put(Mats.MAT_U235, a("metallum", 2, "strontio", 3, "potentia", 3));
        BASE_ASPECTS.put(Mats.MAT_U238, a("metallum", 2, "radio", 2, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_THORIUM, a("metallum", 2, "radio", 2, "potentia", 1));
        BASE_ASPECTS.put(Mats.MAT_PLUTONIUM, a("metallum", 2, "strontio", 3, "radio", 2, "mortuus", 1));
        BASE_ASPECTS.put(Mats.MAT_RGP, a("metallum", 2, "strontio", 2, "radio", 2, "mortuus", 1));
        BASE_ASPECTS.put(Mats.MAT_PU238, a("metallum", 2, "radio", 4, "ignis", 3, "potentia", 2));
    }

    private static void initBaseAspectsChunk1() {
        BASE_ASPECTS.put(Mats.MAT_PU239, a("metallum", 2, "strontio", 4, "mortuus", 2, "potentia", 3));
        BASE_ASPECTS.put(Mats.MAT_PU240, a("metallum", 2, "strontio", 3, "radio", 3, "perditio", 2));
        BASE_ASPECTS.put(Mats.MAT_PU241, a("metallum", 2, "strontio", 3, "radio", 3, "permutatio", 1));
        BASE_ASPECTS.put(Mats.MAT_RGA, a("metallum", 2, "strontio", 2, "radio", 3));
        BASE_ASPECTS.put(Mats.MAT_AM241, a("metallum", 2, "radio", 3, "electrum", 1, "sensus", 1));
        BASE_ASPECTS.put(Mats.MAT_AM242, a("metallum", 2, "strontio", 4, "potentia", 4, "perditio", 2));
        BASE_ASPECTS.put(Mats.MAT_NEPTUNIUM, a("metallum", 2, "radio", 3, "venenum", 2));
        BASE_ASPECTS.put(Mats.MAT_POLONIUM, a("radio", 4, "contaminatio", 3, "ignis", 2, "mortuus", 2));
        BASE_ASPECTS.put(Mats.MAT_TECHNETIUM, a("metallum", 2, "contaminatio", 2, "perditio", 1));
        BASE_ASPECTS.put(Mats.MAT_RADIUM, a("radio", 4, "lux", 3, "metallum", 1));
        BASE_ASPECTS.put(Mats.MAT_ACTINIUM, a("radio", 3, "lux", 2, "potentia", 1));
        BASE_ASPECTS.put(Mats.MAT_CO60, a("metallum", 2, "radio", 4, "mortuus", 3));
        BASE_ASPECTS.put(Mats.MAT_AU198, a("metallum", 2, "radio", 2, "sano", 1));
        BASE_ASPECTS.put(Mats.MAT_PB209, a("metallum", 2, "radio", 2, "perditio", 1));
        BASE_ASPECTS.put(Mats.MAT_SCHRABIDIUM, a("nebrisum", 5, "radio", 4, "potentia", 4, "alienis", 3));
        BASE_ASPECTS.put(Mats.MAT_SOLINIUM, a("nebrisum", 4, "vitreus", 3, "lux", 2, "alienis", 2));
        BASE_ASPECTS.put(Mats.MAT_SCHRABIDATE, a("nebrisum", 4, "chemica", 3, "venenum", 2));
        BASE_ASPECTS.put(Mats.MAT_SCHRARANIUM, a("metallum", 3, "nebrisum", 3, "strontio", 3));
        BASE_ASPECTS.put(Mats.MAT_GHIORSIUM, a("nebrisum", 4, "metallum", 3, "alienis", 2));
        BASE_ASPECTS.put(Mats.MAT_TITANIUM, a("metallum", 3, "tutamen", 2));
        BASE_ASPECTS.put(Mats.MAT_COPPER, a("metallum", 2, "electrum", 2));
        BASE_ASPECTS.put(Mats.MAT_TUNGSTEN, a("metallum", 3, "ignis", 2, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_ALUMINIUM, a("metallum", 2, "volatus", 1));
        BASE_ASPECTS.put(Mats.MAT_LEAD, a("metallum", 2, "tutamen", 2, "radio", 1));
        BASE_ASPECTS.put(Mats.MAT_BISMUTH, a("metallum", 2, "vitreus", 2, "ordo", 1));
        BASE_ASPECTS.put(Mats.MAT_ARSENIC, a("venenum", 3, "chemica", 2));
        BASE_ASPECTS.put(Mats.MAT_TANTALIUM, a("metallum", 2, "electrum", 2, "tutamen", 1));
        BASE_ASPECTS.put(Mats.MAT_NEODYMIUM, a("metallum", 2, "magneto", 4));
        BASE_ASPECTS.put(Mats.MAT_NIOBIUM, a("metallum", 2, "magneto", 2, "electrum", 1));
        BASE_ASPECTS.put(Mats.MAT_BERYLLIUM, a("metallum", 2, "venenum", 1, "vitreus", 1));
    }

    private static void initBaseAspectsChunk2() {
        BASE_ASPECTS.put(Mats.MAT_EMERALD, a("vitreus", 3, "lucrum", 2, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_COBALT, a("metallum", 2, "magneto", 2));
        BASE_ASPECTS.put(Mats.MAT_BORON, a("metallum", 1, "vinculum", 2, "ordo", 1));
        BASE_ASPECTS.put(Mats.MAT_BORAX, a("terra", 2, "chemica", 2, "ordo", 1));
        BASE_ASPECTS.put(Mats.MAT_LANTHANIUM, a("metallum", 2, "sensus", 1));
        BASE_ASPECTS.put(Mats.MAT_ZIRCONIUM, a("metallum", 2, "tutamen", 2, "ignis", 1));
        BASE_ASPECTS.put(Mats.MAT_SODIUM, a("chemica", 2, "ignis", 2, "aqua", 1));
        BASE_ASPECTS.put(Mats.MAT_SODALITE, a("vitreus", 2, "chemica", 1, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_STRONTIUM, a("ignis", 2, "lux", 2, "chemica", 1));
        BASE_ASPECTS.put(Mats.MAT_CALCIUM, a("corpus", 2, "terra", 1, "chemica", 1));
        BASE_ASPECTS.put(Mats.MAT_LITHIUM, a("electrum", 2, "potentia", 2, "metallum", 1));
        BASE_ASPECTS.put(Mats.MAT_SULFUR, a("chemica", 2, "ignis", 2, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_KNO, a("chemica", 2, "detonatio", 2, "ignis", 1));
        BASE_ASPECTS.put(Mats.MAT_FLUORITE, a("chemica", 3, "venenum", 2, "vitreus", 1));
        BASE_ASPECTS.put(Mats.MAT_PHOSPHORUS, a("ignis", 3, "detonatio", 2, "venenum", 1));
        BASE_ASPECTS.put(Mats.MAT_CHLOROCALCITE, a("chemica", 2, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_MOLYSITE, a("chemica", 2, "metallum", 1));
        BASE_ASPECTS.put(Mats.MAT_CINNABAR, a("chemica", 2, "venenum", 2, "metallum", 1));
        BASE_ASPECTS.put(Mats.MAT_CADMIUM, a("venenum", 3, "metallum", 2, "chemica", 1));
        BASE_ASPECTS.put(Mats.MAT_SILICON, a("vitreus", 2, "electrum", 2, "machina", 1));
        BASE_ASPECTS.put(Mats.MAT_ASBESTOS, a("tutamen", 3, "contaminatio", 2, "terra", 1));
        BASE_ASPECTS.put(Mats.MAT_OSMIRIDIUM, a("metallum", 3, "lucrum", 2, "tutamen", 2));
        BASE_ASPECTS.put(Mats.MAT_STEEL, a("metallum", 3, "ordo", 1));
        BASE_ASPECTS.put(Mats.MAT_MINGRADE, a("metallum", 2, "electrum", 2, "potentia", 1));
        BASE_ASPECTS.put(Mats.MAT_DURA, a("metallum", 3, "volatus", 1, "tutamen", 1));
        BASE_ASPECTS.put(Mats.MAT_DESH, a("metallum", 3, "alienis", 1, "ignis", 1));
        BASE_ASPECTS.put(Mats.MAT_STAR, a("metallum", 3, "tutamen", 2, "magneto", 2));
        BASE_ASPECTS.put(Mats.MAT_FERRO, a("metallum", 3, "radio", 2, "potentia", 1));
        BASE_ASPECTS.put(Mats.MAT_TCALLOY, a("metallum", 3, "tutamen", 2, "instrumentum", 1));
        BASE_ASPECTS.put(Mats.MAT_CDALLOY, a("metallum", 2, "vinculum", 2, "chemica", 1));
    }

    private static void initBaseAspectsChunk3() {
        BASE_ASPECTS.put(Mats.MAT_BBRONZE, a("metallum", 2, "instrumentum", 1, "chemica", 1));
        BASE_ASPECTS.put(Mats.MAT_ABRONZE, a("metallum", 3, "tutamen", 1));
        BASE_ASPECTS.put(Mats.MAT_BSCCO, a("metallum", 2, "electrum", 4, "magneto", 4, "gelum", 2));
        BASE_ASPECTS.put(Mats.MAT_MAGTUNG, a("metallum", 3, "magneto", 4, "ignis", 1));
        BASE_ASPECTS.put(Mats.MAT_CMB, a("metallum", 4, "tutamen", 3, "nebrisum", 1));
        BASE_ASPECTS.put(Mats.MAT_DNT, a("metallum", 3, "potentia", 2, "radio", 1));
        BASE_ASPECTS.put(Mats.MAT_FLUX, a("chemica", 2, "ordo", 1, "ignis", 1));
        BASE_ASPECTS.put(Mats.MAT_SLAG, a("terra", 2, "chemica", 1, "perditio", 1));
        BASE_ASPECTS.put(Mats.MAT_MUD, a("terra", 2, "aqua", 1));
        BASE_ASPECTS.put(Mats.MAT_GUNMETAL, a("metallum", 3, "telum", 1, "detonatio", 1));
        BASE_ASPECTS.put(Mats.MAT_WEAPONSTEEL, a("metallum", 3, "telum", 1, "tutamen", 1));
        BASE_ASPECTS.put(Mats.MAT_SATURN, a("metallum", 3, "alienis", 2, "tutamen", 2));
        BASE_ASPECTS.put(Mats.MAT_RAREEARTH, a("terra", 2, "electrum", 1, "sensus", 1));
        BASE_ASPECTS.put(Mats.MAT_POLYMER, a("chemica", 2, "fabrico", 2, "vinculum", 1));
        BASE_ASPECTS.put(Mats.MAT_BAKELITE, a("chemica", 2, "fabrico", 2, "tutamen", 1));
        BASE_ASPECTS.put(Mats.MAT_RUBBER, a("chemica", 2, "limus", 2, "motus", 1, "vinculum", 1));
        BASE_ASPECTS.put(Mats.MAT_HARDPLASTIC, a("chemica", 2, "fabrico", 2, "tutamen", 1));
        BASE_ASPECTS.put(Mats.MAT_PVC, a("chemica", 2, "fabrico", 2, "venenum", 1));
    }

    private static void initBaseAspects() {
        initBaseAspectsChunk0();
        initBaseAspectsChunk1();
        initBaseAspectsChunk2();
        initBaseAspectsChunk3();
    }

    private static AspectAmounts scaleNugget(AspectAmounts base) {
        AspectAmounts r = a();
        for (Map.Entry<String, Integer> e : base.asMap().entrySet()) {
            int amt = Math.max(1, e.getValue() / 3);
            r.add(e.getKey(), amt);
            break;
        }
        return r;
    }

    private static AspectAmounts scaleBillet(AspectAmounts base) {
        AspectAmounts r = a();
        for (Map.Entry<String, Integer> e : base.asMap().entrySet()) {
            r.add(e.getKey(), Math.max(1, (e.getValue() * 2) / 3));
        }
        return r;
    }

    private static AspectAmounts scaleDust(AspectAmounts base) {
        AspectAmounts r = a();
        for (Map.Entry<String, Integer> e : base.asMap().entrySet()) {
            r.add(e.getKey(), e.getValue());
        }
        return r.add("perditio", 1);
    }

    private static AspectAmounts scalePlate(AspectAmounts base) {
        AspectAmounts r = a();
        for (Map.Entry<String, Integer> e : base.asMap().entrySet()) {
            r.add(e.getKey(), e.getValue());
        }
        return r.add("instrumentum", 1);
    }

    private static AspectAmounts scaleCastPlate(AspectAmounts base) {
        AspectAmounts r = a();
        for (Map.Entry<String, Integer> e : base.asMap().entrySet()) {
            r.add(e.getKey(), e.getValue() * 3);
        }
        return r.add("instrumentum", 2);
    }

    private static AspectAmounts scaleWeldedPlate(AspectAmounts base) {
        AspectAmounts r = a();
        for (Map.Entry<String, Integer> e : base.asMap().entrySet()) {
            r.add(e.getKey(), e.getValue() * 6);
        }
        return r.add("instrumentum", 3);
    }

    private static AspectAmounts scaleBlock(AspectAmounts base) {
        AspectAmounts r = a();
        for (Map.Entry<String, Integer> e : base.asMap().entrySet()) {
            r.add(e.getKey(), e.getValue() * 7);
        }
        return r;
    }

    private static AspectAmounts scaleGunPart(AspectAmounts base, String extraAspect, int extraAmt) {
        AspectAmounts r = a();
        for (Map.Entry<String, Integer> e : base.asMap().entrySet()) {
            r.add(e.getKey(), e.getValue() * 2);
        }
        return r.add(extraAspect, extraAmt);
    }

    private static void registerAutogenShapes() {
        for (NTMMaterial mat : Mats.orderedList) {
            AspectAmounts base = BASE_ASPECTS.get(mat);
            if (base == null) continue;
            if (mat.autogen.contains(MaterialShapes.INGOT)) reg(ModItems.ingot_raw, mat.id, base);
            if (mat.autogen.contains(MaterialShapes.NUGGET) || mat.autogen.contains(MaterialShapes.TINY)) reg(ModItems.bedrock_ore_fragment, mat.id, scaleNugget(base));
            if (mat.autogen.contains(MaterialShapes.CASTPLATE)) reg(ModItems.plate_cast, mat.id, scaleCastPlate(base));
            if (mat.autogen.contains(MaterialShapes.WELDEDPLATE)) reg(ModItems.plate_welded, mat.id, scaleWeldedPlate(base));
            if (mat.autogen.contains(MaterialShapes.WIRE)) reg(ModItems.wire_fine, mat.id, scaleNugget(base).add("electrum", 1));
            if (mat.autogen.contains(MaterialShapes.DENSEWIRE)) reg(ModItems.wire_dense, mat.id, base.add("electrum", 2));
            if (mat.autogen.contains(MaterialShapes.BOLT)) reg(ModItems.bolt, mat.id, scaleNugget(base).add("instrumentum", 1));
            if (mat.autogen.contains(MaterialShapes.PIPE)) reg(ModItems.pipe, mat.id, scaleCastPlate(base).add("iter", 1));
            if (mat.autogen.contains(MaterialShapes.SHELL)) reg(ModItems.shell, mat.id, scalePlate(base).add("tutamen", 1));
            if (mat.autogen.contains(MaterialShapes.LIGHTBARREL)) reg(ModItems.part_barrel_light, mat.id, scaleGunPart(base, "telum", 1).add("detonatio", 1));
            if (mat.autogen.contains(MaterialShapes.HEAVYBARREL)) reg(ModItems.part_barrel_heavy, mat.id, scaleGunPart(base, "telum", 2).add("detonatio", 2));
            if (mat.autogen.contains(MaterialShapes.LIGHTRECEIVER)) reg(ModItems.part_receiver_light, mat.id, scaleGunPart(base, "machina", 1).add("detonatio", 1));
            if (mat.autogen.contains(MaterialShapes.HEAVYRECEIVER)) reg(ModItems.part_receiver_heavy, mat.id, scaleGunPart(base, "machina", 2).add("detonatio", 1));
            if (mat.autogen.contains(MaterialShapes.MECHANISM)) reg(ModItems.part_mechanism, mat.id, scaleGunPart(base, "machina", 2).add("detonatio", 1));
            if (mat.autogen.contains(MaterialShapes.STOCK)) reg(ModItems.part_stock, mat.id, scaleGunPart(base, "instrumentum", 1));
            if (mat.autogen.contains(MaterialShapes.GRIP)) reg(ModItems.part_grip, mat.id, scaleNugget(base).add("instrumentum", 1));
        }
    }

    private static void registerStandaloneIngots() {
        reg(ModItems.ingot_actinium, a("radio", 3, "lux", 2, "potentia", 1));
        reg(ModItems.ingot_aluminium, a("metallum", 2, "volatus", 1));
        reg(ModItems.ingot_am241, a("metallum", 2, "radio", 3, "electrum", 1, "sensus", 1));
        reg(ModItems.ingot_am242, a("metallum", 2, "strontio", 4, "potentia", 4, "perditio", 2));
        reg(ModItems.ingot_am_mix, a("metallum", 2));
        reg(ModItems.ingot_americium_fuel, a("metallum", 2));
        reg(ModItems.ingot_arsenic, a("venenum", 3, "chemica", 2));
        reg(ModItems.ingot_arsenic_bronze, a("metallum", 2));
        reg(ModItems.ingot_asbestos, a("tutamen", 3, "contaminatio", 2, "terra", 1));
        reg(ModItems.ingot_au198, a("metallum", 2, "radio", 2, "sano", 1));
        reg(ModItems.ingot_australium, a("metallum", 2));
        reg(ModItems.ingot_bakelite, a("chemica", 2, "fabrico", 2, "tutamen", 1));
        reg(ModItems.ingot_beryllium, a("metallum", 2, "venenum", 1, "vitreus", 1));
        reg(ModItems.ingot_biorubber, a("metallum", 2));
        reg(ModItems.ingot_bismuth, a("metallum", 2, "vitreus", 2, "ordo", 1));
        reg(ModItems.ingot_bismuth_bronze, a("metallum", 2));
        reg(ModItems.ingot_boron, a("metallum", 1, "vinculum", 2, "ordo", 1));
        reg(ModItems.ingot_bscco, a("metallum", 2, "electrum", 4, "magneto", 4, "gelum", 2));
        reg(ModItems.ingot_c4, a("metallum", 2));
        reg(ModItems.ingot_cadmium, a("venenum", 3, "metallum", 2, "chemica", 1));
        reg(ModItems.ingot_calcium, a("corpus", 2, "terra", 1, "chemica", 1));
        reg(ModItems.ingot_cdalloy, a("metallum", 2, "vinculum", 2, "chemica", 1));
        reg(ModItems.ingot_cft, a("metallum", 2));
        reg(ModItems.ingot_chainsteel, a("metallum", 2));
        reg(ModItems.ingot_co60, a("metallum", 2, "radio", 4, "mortuus", 3));
        reg(ModItems.ingot_cobalt, a("metallum", 2, "magneto", 2));
        reg(ModItems.ingot_combine_steel, a("metallum", 2));
        reg(ModItems.ingot_copper, a("metallum", 2, "electrum", 2));
        reg(ModItems.ingot_desh, a("metallum", 3, "alienis", 1, "ignis", 1));
        reg(ModItems.ingot_dineutronium, a("metallum", 2));
        reg(ModItems.ingot_dura_steel, a("metallum", 2));
        reg(ModItems.ingot_electronium, a("metallum", 2));
        reg(ModItems.ingot_euphemium, a("metallum", 2));
        reg(ModItems.ingot_ferrouranium, a("metallum", 2));
        reg(ModItems.ingot_fiberglass, a("metallum", 2));
        reg(ModItems.ingot_firebrick, a("metallum", 2));
        reg(ModItems.ingot_gh336, a("metallum", 2));
        reg(ModItems.ingot_graphite, a("ordo", 2, "terra", 2, "electrum", 1));
        reg(ModItems.ingot_gunmetal, a("metallum", 3, "telum", 1, "detonatio", 1));
        reg(ModItems.ingot_hes, a("metallum", 2));
        reg(ModItems.ingot_lanthanium, a("metallum", 2, "sensus", 1));
        reg(ModItems.ingot_lead, a("metallum", 2, "tutamen", 2, "radio", 1));
        reg(ModItems.ingot_les, a("metallum", 2));
        reg(ModItems.ingot_magnetized_tungsten, a("metallum", 2));
        reg(ModItems.ingot_mercury, a("metallum", 2));
        reg(ModItems.ingot_metal, a("metallum", 2));
        reg(ModItems.ingot_meteorite, a("metallum", 2));
        reg(ModItems.ingot_meteorite_forged, a("metallum", 2));
        reg(ModItems.ingot_mox_fuel, a("metallum", 2));
        reg(ModItems.ingot_mud, a("terra", 2, "aqua", 1));
        reg(ModItems.ingot_neptunium, a("metallum", 2, "radio", 3, "venenum", 2));
        reg(ModItems.ingot_neptunium_fuel, a("metallum", 2));
        reg(ModItems.ingot_niobium, a("metallum", 2, "magneto", 2, "electrum", 1));
        reg(ModItems.ingot_osmiridium, a("metallum", 3, "lucrum", 2, "tutamen", 2));
        reg(ModItems.ingot_pb209, a("metallum", 2, "radio", 2, "perditio", 1));
        reg(ModItems.ingot_pc, a("metallum", 2));
        reg(ModItems.ingot_phosphorus, a("ignis", 3, "detonatio", 2, "venenum", 1));
        reg(ModItems.ingot_plutonium, a("metallum", 2, "strontio", 3, "radio", 2, "mortuus", 1));
        reg(ModItems.ingot_plutonium_fuel, a("metallum", 2));
        reg(ModItems.ingot_polonium, a("radio", 4, "contaminatio", 3, "ignis", 2, "mortuus", 2));
        reg(ModItems.ingot_polymer, a("chemica", 2, "fabrico", 2, "vinculum", 1));
        reg(ModItems.ingot_pu238, a("metallum", 2, "radio", 4, "ignis", 3, "potentia", 2));
        reg(ModItems.ingot_pu239, a("metallum", 2, "strontio", 4, "mortuus", 2, "potentia", 3));
        reg(ModItems.ingot_pu240, a("metallum", 2, "strontio", 3, "radio", 3, "perditio", 2));
        reg(ModItems.ingot_pu241, a("metallum", 2, "strontio", 3, "radio", 3, "permutatio", 1));
        reg(ModItems.ingot_pu_mix, a("metallum", 2));
        reg(ModItems.ingot_pvc, a("chemica", 2, "fabrico", 2, "venenum", 1));
        reg(ModItems.ingot_ra226, a("metallum", 2));
        reg(ModItems.ingot_raw, a("metallum", 2));
        reg(ModItems.ingot_red_copper, a("metallum", 2));
        reg(ModItems.ingot_rubber, a("chemica", 2, "limus", 2, "motus", 1, "vinculum", 1));
        reg(ModItems.ingot_saturnite, a("metallum", 2));
        reg(ModItems.ingot_schrabidate, a("nebrisum", 4, "chemica", 3, "venenum", 2));
        reg(ModItems.ingot_schrabidium, a("nebrisum", 5, "radio", 4, "potentia", 4, "alienis", 3));
        reg(ModItems.ingot_schrabidium_fuel, a("metallum", 2));
        reg(ModItems.ingot_schraranium, a("metallum", 3, "nebrisum", 3, "strontio", 3));
        reg(ModItems.ingot_semtex, a("metallum", 2));
        reg(ModItems.ingot_silicon, a("vitreus", 2, "electrum", 2, "machina", 1));
        reg(ModItems.ingot_smore, a("metallum", 2));
        reg(ModItems.ingot_solinium, a("nebrisum", 4, "vitreus", 3, "lux", 2, "alienis", 2));
        reg(ModItems.ingot_sr90, a("metallum", 2));
        reg(ModItems.ingot_starmetal, a("metallum", 2));
        reg(ModItems.ingot_steel, a("metallum", 3, "ordo", 1));
        reg(ModItems.ingot_steel_dusted, a("metallum", 2));
        reg(ModItems.ingot_tantalium, a("metallum", 2, "electrum", 2, "tutamen", 1));
        reg(ModItems.ingot_tcalloy, a("metallum", 3, "tutamen", 2, "instrumentum", 1));
        reg(ModItems.ingot_technetium, a("metallum", 2, "contaminatio", 2, "perditio", 1));
        reg(ModItems.ingot_th232, a("metallum", 2));
        reg(ModItems.ingot_thorium_fuel, a("metallum", 2));
        reg(ModItems.ingot_titanium, a("metallum", 3, "tutamen", 2));
        reg(ModItems.ingot_tungsten, a("metallum", 3, "ignis", 2, "terra", 1));
        reg(ModItems.ingot_tungsten_carbide, a("metallum", 2));
        reg(ModItems.ingot_u233, a("metallum", 2, "strontio", 3, "potentia", 2));
        reg(ModItems.ingot_u235, a("metallum", 2, "strontio", 3, "potentia", 3));
        reg(ModItems.ingot_u238, a("metallum", 2, "radio", 2, "terra", 1));
        reg(ModItems.ingot_u238m2, a("metallum", 2));
        reg(ModItems.ingot_uranium, a("metallum", 2, "radio", 2, "terra", 1));
        reg(ModItems.ingot_uranium_fuel, a("metallum", 2));
        reg(ModItems.ingot_weaponsteel, a("metallum", 3, "telum", 1, "tutamen", 1));
        reg(ModItems.ingot_zirconium, a("metallum", 2, "tutamen", 2, "ignis", 1));
    }

    private static void registerStandaloneNuggets() {
        reg(ModItems.nugget_actinium, a("radio", 1));
        reg(ModItems.nugget_am241, a("metallum", 1));
        reg(ModItems.nugget_am242, a("metallum", 1));
        reg(ModItems.nugget_am_mix, a("metallum", 2));
        reg(ModItems.nugget_americium_fuel, a("metallum", 2));
        reg(ModItems.nugget_arsenic, a("venenum", 1));
        reg(ModItems.nugget_au198, a("metallum", 1));
        reg(ModItems.nugget_australium, a("metallum", 2));
        reg(ModItems.nugget_australium_greater, a("metallum", 2));
        reg(ModItems.nugget_australium_lesser, a("metallum", 2));
        reg(ModItems.nugget_beryllium, a("metallum", 1));
        reg(ModItems.nugget_bismuth, a("metallum", 1));
        reg(ModItems.nugget_co60, a("metallum", 1));
        reg(ModItems.nugget_cobalt, a("metallum", 1));
        reg(ModItems.nugget_desh, a("metallum", 1));
        reg(ModItems.nugget_dineutronium, a("metallum", 2));
        reg(ModItems.nugget_euphemium, a("metallum", 2));
        reg(ModItems.nugget_gh336, a("metallum", 2));
        reg(ModItems.nugget_hes, a("metallum", 2));
        reg(ModItems.nugget_lead, a("metallum", 1));
        reg(ModItems.nugget_les, a("metallum", 2));
        reg(ModItems.nugget_mercury, a("metallum", 2));
        reg(ModItems.nugget_mox_fuel, a("metallum", 2));
        reg(ModItems.nugget_neptunium, a("metallum", 1));
        reg(ModItems.nugget_neptunium_fuel, a("metallum", 2));
        reg(ModItems.nugget_niobium, a("metallum", 1));
        reg(ModItems.nugget_osmiridium, a("metallum", 1));
        reg(ModItems.nugget_pb209, a("metallum", 1));
        reg(ModItems.nugget_plutonium, a("metallum", 1));
        reg(ModItems.nugget_plutonium_fuel, a("metallum", 2));
        reg(ModItems.nugget_polonium, a("radio", 1));
        reg(ModItems.nugget_pu238, a("metallum", 1));
        reg(ModItems.nugget_pu239, a("metallum", 1));
        reg(ModItems.nugget_pu240, a("metallum", 1));
        reg(ModItems.nugget_pu241, a("metallum", 1));
        reg(ModItems.nugget_pu_mix, a("metallum", 2));
        reg(ModItems.nugget_ra226, a("metallum", 2));
        reg(ModItems.nugget_schrabidium, a("nebrisum", 1));
        reg(ModItems.nugget_schrabidium_fuel, a("metallum", 2));
        reg(ModItems.nugget_silicon, a("vitreus", 1));
        reg(ModItems.nugget_solinium, a("nebrisum", 1));
        reg(ModItems.nugget_sr90, a("metallum", 2));
        reg(ModItems.nugget_tantalium, a("metallum", 1));
        reg(ModItems.nugget_technetium, a("metallum", 1));
        reg(ModItems.nugget_th232, a("metallum", 2));
        reg(ModItems.nugget_thorium_fuel, a("metallum", 2));
        reg(ModItems.nugget_u233, a("metallum", 1));
        reg(ModItems.nugget_u235, a("metallum", 1));
        reg(ModItems.nugget_u238, a("metallum", 1));
        reg(ModItems.nugget_uranium, a("metallum", 1));
        reg(ModItems.nugget_uranium_fuel, a("metallum", 2));
        reg(ModItems.nugget_zirconium, a("metallum", 1));
    }

    private static void registerStandalonePowders() {
        reg(ModItems.powder_actinium, a("radio", 3, "lux", 2, "potentia", 1).add("perditio", 1));
        reg(ModItems.powder_actinium_tiny, a("metallum", 2));
        reg(ModItems.powder_aluminium, a("metallum", 2, "volatus", 1).add("perditio", 1));
        reg(ModItems.powder_asbestos, a("tutamen", 3, "contaminatio", 2, "terra", 1).add("perditio", 1));
        reg(ModItems.powder_ash, a("metallum", 2));
        reg(ModItems.powder_astatine, a("metallum", 2));
        reg(ModItems.powder_at209, a("metallum", 2));
        reg(ModItems.powder_au198, a("metallum", 2, "radio", 2, "sano", 1).add("perditio", 1));
        reg(ModItems.powder_australium, a("metallum", 2));
        reg(ModItems.powder_bakelite, a("chemica", 2, "fabrico", 2, "tutamen", 1).add("perditio", 1));
        reg(ModItems.powder_balefire, a("metallum", 2));
        reg(ModItems.powder_beryllium, a("metallum", 2, "venenum", 1, "vitreus", 1).add("perditio", 1));
        reg(ModItems.powder_bismuth, a("metallum", 2, "vitreus", 2, "ordo", 1).add("perditio", 1));
        reg(ModItems.powder_borax, a("terra", 2, "chemica", 2, "ordo", 1).add("perditio", 1));
        reg(ModItems.powder_boron, a("metallum", 1, "vinculum", 2, "ordo", 1).add("perditio", 1));
        reg(ModItems.powder_boron_tiny, a("metallum", 2));
        reg(ModItems.powder_bromine, a("metallum", 2));
        reg(ModItems.powder_cadmium, a("venenum", 3, "metallum", 2, "chemica", 1).add("perditio", 1));
        reg(ModItems.powder_caesium, a("metallum", 2));
        reg(ModItems.powder_calcium, a("corpus", 2, "terra", 1, "chemica", 1).add("perditio", 1));
        reg(ModItems.powder_cement, a("metallum", 2));
        reg(ModItems.powder_cerium, a("metallum", 2));
        reg(ModItems.powder_cerium_tiny, a("metallum", 2));
        reg(ModItems.powder_chlorocalcite, a("chemica", 2, "terra", 1).add("perditio", 1));
        reg(ModItems.powder_chlorophyte, a("metallum", 2));
        reg(ModItems.powder_co60, a("metallum", 2, "radio", 4, "mortuus", 3).add("perditio", 1));
        reg(ModItems.powder_coal, a("potentia", 2, "ignis", 2, "terra", 1).add("perditio", 1));
        reg(ModItems.powder_coal_tiny, a("metallum", 2));
        reg(ModItems.powder_cobalt, a("metallum", 2, "magneto", 2).add("perditio", 1));
        reg(ModItems.powder_cobalt_tiny, a("metallum", 2));
        reg(ModItems.powder_coltan, a("metallum", 2));
        reg(ModItems.powder_coltan_ore, a("metallum", 2));
        reg(ModItems.powder_combine_steel, a("metallum", 2));
        reg(ModItems.powder_copper, a("metallum", 2, "electrum", 2).add("perditio", 1));
        reg(ModItems.powder_cs137, a("metallum", 2));
        reg(ModItems.powder_cs137_tiny, a("metallum", 2));
        reg(ModItems.powder_desh, a("metallum", 3, "alienis", 1, "ignis", 1).add("perditio", 1));
        reg(ModItems.powder_desh_mix, a("metallum", 2));
        reg(ModItems.powder_desh_ready, a("metallum", 2));
        reg(ModItems.powder_diamond, a("vitreus", 3, "lucrum", 2, "terra", 1).add("perditio", 1));
        reg(ModItems.powder_dineutronium, a("metallum", 2));
        reg(ModItems.powder_dura_steel, a("metallum", 2));
        reg(ModItems.powder_emerald, a("vitreus", 3, "lucrum", 2, "terra", 1).add("perditio", 1));
        reg(ModItems.powder_euphemium, a("metallum", 2));
        reg(ModItems.powder_fertilizer, a("metallum", 2));
        reg(ModItems.powder_fire, a("metallum", 2));
        reg(ModItems.powder_flux, a("chemica", 2, "ordo", 1, "ignis", 1).add("perditio", 1));
        reg(ModItems.powder_gold, a("metallum", 2, "lucrum", 2, "electrum", 1).add("perditio", 1));
        reg(ModItems.powder_i131, a("metallum", 2));
        reg(ModItems.powder_i131_tiny, a("metallum", 2));
        reg(ModItems.powder_ice, a("metallum", 2));
        reg(ModItems.powder_impure_osmiridium, a("metallum", 2));
        reg(ModItems.powder_iodine, a("metallum", 2));
        reg(ModItems.powder_iron, a("metallum", 3).add("perditio", 1));
        reg(ModItems.powder_lanthanium, a("metallum", 2, "sensus", 1).add("perditio", 1));
        reg(ModItems.powder_lanthanium_tiny, a("metallum", 2));
        reg(ModItems.powder_lapis, a("metallum", 2));
        reg(ModItems.powder_lead, a("metallum", 2, "tutamen", 2, "radio", 1).add("perditio", 1));
        reg(ModItems.powder_lignite, a("potentia", 1, "ignis", 1, "terra", 1).add("perditio", 1));
        reg(ModItems.powder_limestone, a("metallum", 2));
        reg(ModItems.powder_lithium, a("electrum", 2, "potentia", 2, "metallum", 1).add("perditio", 1));
        reg(ModItems.powder_lithium_tiny, a("metallum", 2));
        reg(ModItems.powder_magic, a("metallum", 2));
        reg(ModItems.powder_magnetized_tungsten, a("metallum", 2));
        reg(ModItems.powder_meteorite, a("metallum", 2));
        reg(ModItems.powder_meteorite_tiny, a("metallum", 2));
        reg(ModItems.powder_molysite, a("chemica", 2, "metallum", 1).add("perditio", 1));
        reg(ModItems.powder_neodymium, a("metallum", 2, "magneto", 4).add("perditio", 1));
        reg(ModItems.powder_neodymium_tiny, a("metallum", 2));
        reg(ModItems.powder_neptunium, a("metallum", 2, "radio", 3, "venenum", 2).add("perditio", 1));
        reg(ModItems.powder_niobium, a("metallum", 2, "magneto", 2, "electrum", 1).add("perditio", 1));
        reg(ModItems.powder_niobium_tiny, a("metallum", 2));
        reg(ModItems.powder_nitan_mix, a("metallum", 2));
        reg(ModItems.powder_paleogenite, a("metallum", 2));
        reg(ModItems.powder_paleogenite_tiny, a("metallum", 2));
        reg(ModItems.powder_plutonium, a("metallum", 2, "strontio", 3, "radio", 2, "mortuus", 1).add("perditio", 1));
        reg(ModItems.powder_poison, a("metallum", 2));
        reg(ModItems.powder_polonium, a("radio", 4, "contaminatio", 3, "ignis", 2, "mortuus", 2).add("perditio", 1));
        reg(ModItems.powder_polymer, a("chemica", 2, "fabrico", 2, "vinculum", 1).add("perditio", 1));
        reg(ModItems.powder_power, a("metallum", 2));
        reg(ModItems.powder_quartz, a("metallum", 2));
        reg(ModItems.powder_ra226, a("metallum", 2));
        reg(ModItems.powder_red_copper, a("metallum", 2));
        reg(ModItems.powder_sawdust, a("metallum", 2));
        reg(ModItems.powder_schrabidate, a("nebrisum", 4, "chemica", 3, "venenum", 2).add("perditio", 1));
        reg(ModItems.powder_schrabidium, a("nebrisum", 5, "radio", 4, "potentia", 4, "alienis", 3).add("perditio", 1));
        reg(ModItems.powder_semtex_mix, a("metallum", 2));
        reg(ModItems.powder_sodium, a("chemica", 2, "ignis", 2, "aqua", 1).add("perditio", 1));
        reg(ModItems.powder_spark_mix, a("metallum", 2));
        reg(ModItems.powder_sr90, a("metallum", 2));
        reg(ModItems.powder_sr90_tiny, a("metallum", 2));
        reg(ModItems.powder_steel, a("metallum", 3, "ordo", 1).add("perditio", 1));
        reg(ModItems.powder_steel_tiny, a("metallum", 2));
        reg(ModItems.powder_strontium, a("ignis", 2, "lux", 2, "chemica", 1).add("perditio", 1));
        reg(ModItems.powder_tantalium, a("metallum", 2, "electrum", 2, "tutamen", 1).add("perditio", 1));
        reg(ModItems.powder_tcalloy, a("metallum", 3, "tutamen", 2, "instrumentum", 1).add("perditio", 1));
        reg(ModItems.powder_tektite, a("metallum", 2));
        reg(ModItems.powder_tennessine, a("metallum", 2));
        reg(ModItems.powder_thermite, a("metallum", 2));
        reg(ModItems.powder_thorium, a("metallum", 2, "radio", 2, "potentia", 1).add("perditio", 1));
        reg(ModItems.powder_titanium, a("metallum", 3, "tutamen", 2).add("perditio", 1));
        reg(ModItems.powder_tungsten, a("metallum", 3, "ignis", 2, "terra", 1).add("perditio", 1));
        reg(ModItems.powder_uranium, a("metallum", 2, "radio", 2, "terra", 1).add("perditio", 1));
        reg(ModItems.powder_xe135, a("metallum", 2));
        reg(ModItems.powder_xe135_tiny, a("metallum", 2));
        reg(ModItems.powder_yellowcake, a("metallum", 2));
        reg(ModItems.powder_zirconium, a("metallum", 2, "tutamen", 2, "ignis", 1).add("perditio", 1));
    }

    private static void registerStandalonePlates() {
        reg(ModItems.plate_aluminium, a("metallum", 2, "volatus", 1).add("instrumentum", 1));
        reg(ModItems.plate_armor_ajr, a("metallum", 2));
        reg(ModItems.plate_armor_dnt, a("metallum", 2));
        reg(ModItems.plate_armor_fau, a("metallum", 2));
        reg(ModItems.plate_armor_hev, a("metallum", 2));
        reg(ModItems.plate_armor_lunar, a("metallum", 2));
        reg(ModItems.plate_armor_titanium, a("metallum", 2));
        reg(ModItems.plate_bismuth, a("metallum", 2, "vitreus", 2, "ordo", 1).add("instrumentum", 1));
        reg(ModItems.plate_cast, a("metallum", 2));
        reg(ModItems.plate_combine_steel, a("metallum", 2));
        reg(ModItems.plate_copper, a("metallum", 2, "electrum", 2).add("instrumentum", 1));
        reg(ModItems.plate_dalekanium, a("metallum", 2));
        reg(ModItems.plate_desh, a("metallum", 3, "alienis", 1, "ignis", 1).add("instrumentum", 1));
        reg(ModItems.plate_dineutronium, a("metallum", 2));
        reg(ModItems.plate_dura_steel, a("metallum", 2));
        reg(ModItems.plate_euphemium, a("metallum", 2));
        reg(ModItems.plate_gold, a("metallum", 2, "lucrum", 2, "electrum", 1).add("instrumentum", 1));
        reg(ModItems.plate_gunmetal, a("metallum", 3, "telum", 1, "detonatio", 1).add("instrumentum", 1));
        reg(ModItems.plate_iron, a("metallum", 3).add("instrumentum", 1));
        reg(ModItems.plate_kevlar, a("metallum", 2));
        reg(ModItems.plate_lead, a("metallum", 2, "tutamen", 2, "radio", 1).add("instrumentum", 1));
        reg(ModItems.plate_mixed, a("metallum", 2));
        reg(ModItems.plate_paa, a("metallum", 2));
        reg(ModItems.plate_polymer, a("chemica", 2, "fabrico", 2, "vinculum", 1).add("instrumentum", 1));
        reg(ModItems.plate_saturnite, a("metallum", 2));
        reg(ModItems.plate_schrabidium, a("nebrisum", 5, "radio", 4, "potentia", 4, "alienis", 3).add("instrumentum", 1));
        reg(ModItems.plate_steel, a("metallum", 3, "ordo", 1).add("instrumentum", 1));
        reg(ModItems.plate_titanium, a("metallum", 3, "tutamen", 2).add("instrumentum", 1));
        reg(ModItems.plate_weaponsteel, a("metallum", 3, "telum", 1, "tutamen", 1).add("instrumentum", 1));
        reg(ModItems.plate_welded, a("metallum", 2));
    }

    private static void registerStandaloneBillets() {
        reg(ModItems.billet_actinium, a("radio", 3, "lux", 2, "potentia", 1));
        reg(ModItems.billet_am241, a("metallum", 2, "radio", 3, "electrum", 1, "sensus", 1));
        reg(ModItems.billet_am242, a("metallum", 2, "strontio", 4, "potentia", 4, "perditio", 2));
        reg(ModItems.billet_am_mix, a("metallum", 2));
        reg(ModItems.billet_americium_fuel, a("metallum", 2));
        reg(ModItems.billet_au198, a("metallum", 2, "radio", 2, "sano", 1));
        reg(ModItems.billet_australium, a("metallum", 2));
        reg(ModItems.billet_australium_greater, a("metallum", 2));
        reg(ModItems.billet_australium_lesser, a("metallum", 2));
        reg(ModItems.billet_balefire_gold, a("metallum", 2));
        reg(ModItems.billet_beryllium, a("metallum", 2, "venenum", 1, "vitreus", 1));
        reg(ModItems.billet_bismuth, a("metallum", 2, "vitreus", 2, "ordo", 1));
        reg(ModItems.billet_co60, a("metallum", 2, "radio", 4, "mortuus", 3));
        reg(ModItems.billet_cobalt, a("metallum", 2, "magneto", 2));
        reg(ModItems.billet_flashlead, a("metallum", 2));
        reg(ModItems.billet_gh336, a("metallum", 2));
        reg(ModItems.billet_hes, a("metallum", 2));
        reg(ModItems.billet_les, a("metallum", 2));
        reg(ModItems.billet_mox_fuel, a("metallum", 2));
        reg(ModItems.billet_neptunium, a("metallum", 2, "radio", 3, "venenum", 2));
        reg(ModItems.billet_neptunium_fuel, a("metallum", 2));
        reg(ModItems.billet_nuclear_waste, a("metallum", 2));
        reg(ModItems.billet_pb209, a("metallum", 2, "radio", 2, "perditio", 1));
        reg(ModItems.billet_plutonium, a("metallum", 2, "strontio", 3, "radio", 2, "mortuus", 1));
        reg(ModItems.billet_plutonium_fuel, a("metallum", 2));
        reg(ModItems.billet_po210be, a("metallum", 2));
        reg(ModItems.billet_polonium, a("radio", 4, "contaminatio", 3, "ignis", 2, "mortuus", 2));
        reg(ModItems.billet_pu238, a("metallum", 2, "radio", 4, "ignis", 3, "potentia", 2));
        reg(ModItems.billet_pu238be, a("metallum", 2));
        reg(ModItems.billet_pu239, a("metallum", 2, "strontio", 4, "mortuus", 2, "potentia", 3));
        reg(ModItems.billet_pu240, a("metallum", 2, "strontio", 3, "radio", 3, "perditio", 2));
        reg(ModItems.billet_pu241, a("metallum", 2, "strontio", 3, "radio", 3, "permutatio", 1));
        reg(ModItems.billet_pu_mix, a("metallum", 2));
        reg(ModItems.billet_ra226, a("metallum", 2));
        reg(ModItems.billet_ra226be, a("metallum", 2));
        reg(ModItems.billet_schrabidium, a("nebrisum", 5, "radio", 4, "potentia", 4, "alienis", 3));
        reg(ModItems.billet_schrabidium_fuel, a("metallum", 2));
        reg(ModItems.billet_silicon, a("vitreus", 2, "electrum", 2, "machina", 1));
        reg(ModItems.billet_solinium, a("nebrisum", 4, "vitreus", 3, "lux", 2, "alienis", 2));
        reg(ModItems.billet_sr90, a("metallum", 2));
        reg(ModItems.billet_technetium, a("metallum", 2, "contaminatio", 2, "perditio", 1));
        reg(ModItems.billet_th232, a("metallum", 2));
        reg(ModItems.billet_thorium_fuel, a("metallum", 2));
        reg(ModItems.billet_u233, a("metallum", 2, "strontio", 3, "potentia", 2));
        reg(ModItems.billet_u235, a("metallum", 2, "strontio", 3, "potentia", 3));
        reg(ModItems.billet_u238, a("metallum", 2, "radio", 2, "terra", 1));
        reg(ModItems.billet_uranium, a("metallum", 2, "radio", 2, "terra", 1));
        reg(ModItems.billet_uranium_fuel, a("metallum", 2));
        reg(ModItems.billet_uzh, a("metallum", 2));
        reg(ModItems.billet_yharonite, a("metallum", 2));
        reg(ModItems.billet_zfb_am_mix, a("metallum", 2));
        reg(ModItems.billet_zfb_bismuth, a("metallum", 2));
        reg(ModItems.billet_zfb_pu241, a("metallum", 2));
        reg(ModItems.billet_zirconium, a("metallum", 2, "tutamen", 2, "ignis", 1));
    }

    private static void registerStandaloneBlocks() {
        reg(ModBlocks.block_actinium, a("radio", 21, "lux", 14, "potentia", 7));
        reg(ModBlocks.block_aluminium, a("metallum", 14, "volatus", 7));
        reg(ModBlocks.block_asbestos, a("tutamen", 21, "contaminatio", 14, "terra", 7));
        reg(ModBlocks.block_bakelite, a("chemica", 14, "fabrico", 14, "tutamen", 7));
        reg(ModBlocks.block_beryllium, a("metallum", 14, "venenum", 7, "vitreus", 7));
        reg(ModBlocks.block_bismuth, a("metallum", 14, "vitreus", 14, "ordo", 7));
        reg(ModBlocks.block_boron, a("metallum", 7, "vinculum", 14, "ordo", 7));
        reg(ModBlocks.block_cadmium, a("venenum", 21, "metallum", 14, "chemica", 7));
        reg(ModBlocks.block_cdalloy, a("metallum", 14, "vinculum", 14, "chemica", 7));
        reg(ModBlocks.block_cobalt, a("metallum", 14, "magneto", 14));
        reg(ModBlocks.block_copper, a("metallum", 14, "electrum", 14));
        reg(ModBlocks.block_desh, a("metallum", 21, "alienis", 7, "ignis", 7));
        reg(ModBlocks.block_fluorite, a("chemica", 21, "venenum", 14, "vitreus", 7));
        reg(ModBlocks.block_graphite, a("ordo", 14, "terra", 14, "electrum", 7));
        reg(ModBlocks.block_lanthanium, a("metallum", 14, "sensus", 7));
        reg(ModBlocks.block_lead, a("metallum", 14, "tutamen", 14, "radio", 7));
        reg(ModBlocks.block_lithium, a("electrum", 14, "potentia", 14, "metallum", 7));
        reg(ModBlocks.block_neptunium, a("metallum", 14, "radio", 21, "venenum", 14));
        reg(ModBlocks.block_niobium, a("metallum", 14, "magneto", 14, "electrum", 7));
        reg(ModBlocks.block_plutonium, a("metallum", 14, "strontio", 21, "radio", 14, "mortuus", 7));
        reg(ModBlocks.block_polonium, a("radio", 28, "contaminatio", 21, "ignis", 14, "mortuus", 14));
        reg(ModBlocks.block_polymer, a("chemica", 14, "fabrico", 14, "vinculum", 7));
        reg(ModBlocks.block_pu238, a("metallum", 14, "radio", 28, "ignis", 21, "potentia", 14));
        reg(ModBlocks.block_pu239, a("metallum", 14, "strontio", 28, "mortuus", 14, "potentia", 21));
        reg(ModBlocks.block_pu240, a("metallum", 14, "strontio", 21, "radio", 21, "perditio", 14));
        reg(ModBlocks.block_rubber, a("chemica", 14, "limus", 14, "motus", 7, "vinculum", 7));
        reg(ModBlocks.block_schrabidate, a("nebrisum", 28, "chemica", 21, "venenum", 14));
        reg(ModBlocks.block_schrabidium, a("nebrisum", 35, "radio", 28, "potentia", 28, "alienis", 21));
        reg(ModBlocks.block_schraranium, a("metallum", 21, "nebrisum", 21, "strontio", 21));
        reg(ModBlocks.block_slag, a("terra", 14, "chemica", 7, "perditio", 7));
        reg(ModBlocks.block_solinium, a("nebrisum", 28, "vitreus", 21, "lux", 14, "alienis", 14));
        reg(ModBlocks.block_steel, a("metallum", 21, "ordo", 7));
        reg(ModBlocks.block_sulfur, a("chemica", 14, "ignis", 14, "terra", 7));
        reg(ModBlocks.block_tantalium, a("metallum", 14, "electrum", 14, "tutamen", 7));
        reg(ModBlocks.block_tcalloy, a("metallum", 21, "tutamen", 14, "instrumentum", 7));
        reg(ModBlocks.block_thorium, a("metallum", 14, "radio", 14, "potentia", 7));
        reg(ModBlocks.block_titanium, a("metallum", 21, "tutamen", 14));
        reg(ModBlocks.block_tungsten, a("metallum", 21, "ignis", 14, "terra", 7));
        reg(ModBlocks.block_u233, a("metallum", 14, "strontio", 21, "potentia", 14));
        reg(ModBlocks.block_u235, a("metallum", 14, "strontio", 21, "potentia", 21));
        reg(ModBlocks.block_u238, a("metallum", 14, "radio", 14, "terra", 7));
        reg(ModBlocks.block_uranium, a("metallum", 14, "radio", 14, "terra", 7));
        reg(ModBlocks.block_zirconium, a("metallum", 14, "tutamen", 14, "ignis", 7));
    }

    private static void registerStandaloneOres() {
        reg(ModBlocks.ore_alexandrite, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_aluminium, a("metallum", 2, "volatus", 1, "terra", 1, "perfodio", 1));
        reg(ModBlocks.ore_asbestos, a("tutamen", 3, "contaminatio", 2, "terra", 2, "perfodio", 1));
        reg(ModBlocks.ore_australium, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_basalt, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_bedrock, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_bedrock_oil, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_beryllium, a("metallum", 2, "venenum", 1, "vitreus", 1, "terra", 1, "perfodio", 1));
        reg(ModBlocks.ore_cinnebar, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_cobalt, a("metallum", 2, "magneto", 2, "terra", 1, "perfodio", 1));
        reg(ModBlocks.ore_coltan, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_copper, a("metallum", 2, "electrum", 2, "terra", 1, "perfodio", 1));
        reg(ModBlocks.ore_depth_borax, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_depth_cinnebar, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_depth_nether_neodymium, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_depth_zirconium, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_fluorite, a("chemica", 3, "venenum", 2, "vitreus", 1, "terra", 1, "perfodio", 1));
        reg(ModBlocks.ore_gneiss_asbestos, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_gneiss_copper, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_gneiss_gas, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_gneiss_gold, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_gneiss_iron, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_gneiss_lithium, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_gneiss_rare, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_gneiss_schrabidium, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_gneiss_uranium, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_gneiss_uranium_scorched, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_lead, a("metallum", 2, "tutamen", 2, "radio", 1, "terra", 1, "perfodio", 1));
        reg(ModBlocks.ore_lignite, a("potentia", 1, "ignis", 1, "terra", 2, "perfodio", 1));
        reg(ModBlocks.ore_meteor, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_nether_coal, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_nether_cobalt, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_nether_fire, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_nether_plutonium, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_nether_schrabidium, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_nether_smoldering, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_nether_sulfur, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_nether_tungsten, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_nether_uranium, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_nether_uranium_scorched, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_niter, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_oil, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_oil_empty, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_oil_sand, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_rare, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_schrabidium, a("nebrisum", 5, "radio", 4, "potentia", 4, "alienis", 3, "terra", 1, "perfodio", 1));
        reg(ModBlocks.ore_sellafield_diamond, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_sellafield_emerald, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_sellafield_radgem, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_sellafield_schrabidium, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_sellafield_uranium_scorched, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_sulfur, a("chemica", 2, "ignis", 2, "terra", 2, "perfodio", 1));
        reg(ModBlocks.ore_tektite_osmiridium, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_thorium, a("metallum", 2, "radio", 2, "potentia", 1, "terra", 1, "perfodio", 1));
        reg(ModBlocks.ore_tikite, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_titanium, a("metallum", 3, "tutamen", 2, "terra", 1, "perfodio", 1));
        reg(ModBlocks.ore_tungsten, a("metallum", 3, "ignis", 2, "terra", 2, "perfodio", 1));
        reg(ModBlocks.ore_uranium, a("metallum", 2, "radio", 2, "terra", 2, "perfodio", 1));
        reg(ModBlocks.ore_uranium_scorched, a("terra", 2, "perfodio", 1, "metallum", 1));
        reg(ModBlocks.ore_volcano, a("terra", 2, "perfodio", 1, "metallum", 1));
    }

    private static void registerMiscellaneousMaterials() {
        reg(ModItems.coke, 0, a("potentia", 3, "ignis", 3, "chemica", 1));
        reg(ModItems.coke, 1, a("potentia", 2, "ignis", 2));
        reg(ModItems.coke, 2, a("potentia", 3, "ignis", 3, "chemica", 2));
        reg(ModItems.briquette, 0, a("potentia", 2, "ignis", 2));
        reg(ModItems.briquette, 1, a("potentia", 1, "ignis", 1));
        reg(ModItems.briquette, 2, a("arbor", 2, "ignis", 1));
        reg(ModItems.oil_tar, 0, a("chemica", 3, "ignis", 2, "motus", 1));
        reg(ModItems.chunk_ore, 0, a("terra", 2, "perfodio", 1, "lucrum", 1));
        reg(ModItems.powder_ash, 0, a("perditio", 1, "terra", 1));
    }
}