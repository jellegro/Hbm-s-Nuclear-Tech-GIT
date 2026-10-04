package com.usanaem.occultic_ntm.compat.thaumcraft.catalogue;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import static com.usanaem.occultic_ntm.compat.thaumcraft.catalogue.CatalogueHelper.*;

public final class MachineCatalogue {

    private MachineCatalogue() { }

    public static void register() {
        registerProcessing();
        registerPowerAndChemical();
        registerStorageAndLogistics();
        registerUpgradesAndAnvils();
    }

    private static void registerProcessing() {
        reg(ModBlocks.conveyor_express, a("machina", 3, "instrumentum", 2, "metallum", 2));
        reg(ModBlocks.furnace_combination, a("machina", 3, "instrumentum", 2, "metallum", 2));
        reg(ModBlocks.furnace_iron, a("machina", 3, "instrumentum", 2, "metallum", 2));
        reg(ModBlocks.furnace_steel, a("machina", 3, "instrumentum", 2, "metallum", 2));
        reg(ModBlocks.machine_ammo_press, a("machina", 3, "instrumentum", 2, "metallum", 2));
        reg(ModBlocks.machine_arc_furnace, a("machina", 4, "electrum", 4, "ignis", 3, "metallum", 3));
        reg(ModBlocks.machine_blast_furnace, a("machina", 3, "ignis", 4, "metallum", 3));
        reg(ModBlocks.machine_centrifuge, a("machina", 3, "instrumentum", 2, "metallum", 2));
        reg(ModBlocks.machine_compressor, a("machina", 3, "instrumentum", 2, "metallum", 2));
        reg(ModBlocks.machine_compressor_compact, a("machina", 3, "instrumentum", 2, "metallum", 2));
        reg(ModBlocks.machine_conveyor_press, a("machina", 3, "instrumentum", 2, "metallum", 2));
        reg(ModBlocks.machine_crucible, a("machina", 3, "ignis", 4, "metallum", 3));
        reg(ModBlocks.machine_electric_furnace_off, a("machina", 4, "electrum", 4, "ignis", 3, "metallum", 3));
        reg(ModBlocks.machine_electric_furnace_on, a("machina", 4, "electrum", 4, "ignis", 3, "metallum", 3));
        reg(ModBlocks.machine_epress, a("machina", 3, "instrumentum", 2, "metallum", 2));
        reg(ModBlocks.machine_furnace_brick_off, a("machina", 3, "instrumentum", 2, "metallum", 2));
        reg(ModBlocks.machine_furnace_brick_on, a("machina", 3, "instrumentum", 2, "metallum", 2));
        reg(ModBlocks.machine_icf_press, a("machina", 3, "instrumentum", 2, "metallum", 2));
        reg(ModBlocks.machine_press, a("machina", 3, "instrumentum", 2, "metallum", 2));
        reg(ModBlocks.machine_rotary_furnace, a("machina", 3, "instrumentum", 2, "metallum", 2));
        reg(ModBlocks.press_preheater, a("machina", 3, "instrumentum", 2, "metallum", 2));
    }

    private static void registerPowerAndChemical() {
        reg(ModBlocks.cm_engine, a("machina", 3, "potentia", 3, "metallum", 2));
        reg(ModBlocks.fraction_spacer, a("machina", 5, "chemica", 5, "permutatio", 3, "aqua", 2));
        reg(ModBlocks.fusion_boiler, a("machina", 3, "potentia", 3, "metallum", 2));
        reg(ModBlocks.hadron_coil_alloy, a("machina", 5, "magneto", 5, "electrum", 4, "nebrisum", 3));
        reg(ModBlocks.hadron_coil_chlorophyte, a("machina", 5, "magneto", 5, "electrum", 4, "nebrisum", 3));
        reg(ModBlocks.hadron_coil_gold, a("machina", 5, "magneto", 5, "electrum", 4, "nebrisum", 3));
        reg(ModBlocks.hadron_coil_magtung, a("machina", 5, "magneto", 5, "electrum", 4, "nebrisum", 3));
        reg(ModBlocks.hadron_coil_mese, a("machina", 5, "magneto", 5, "electrum", 4, "nebrisum", 3));
        reg(ModBlocks.hadron_coil_neodymium, a("machina", 5, "magneto", 5, "electrum", 4, "nebrisum", 3));
        reg(ModBlocks.hadron_coil_schrabidate, a("machina", 5, "magneto", 5, "electrum", 4, "nebrisum", 3));
        reg(ModBlocks.hadron_coil_schrabidium, a("machina", 5, "magneto", 5, "electrum", 4, "nebrisum", 3));
        reg(ModBlocks.hadron_coil_starmetal, a("machina", 5, "magneto", 5, "electrum", 4, "nebrisum", 3));
        reg(ModBlocks.machine_boiler, a("machina", 3, "potentia", 3, "metallum", 2));
        reg(ModBlocks.machine_boiler_off, a("machina", 3, "potentia", 3, "metallum", 2));
        reg(ModBlocks.machine_combustion_engine, a("machina", 3, "potentia", 3, "metallum", 2));
        reg(ModBlocks.machine_fraction_tower, a("machina", 5, "chemica", 5, "permutatio", 3, "aqua", 2));
        reg(ModBlocks.machine_industrial_boiler, a("machina", 3, "potentia", 3, "metallum", 2));
        reg(ModBlocks.machine_industrial_generator, a("machina", 4, "electrum", 4, "motus", 3, "potentia", 3));
        reg(ModBlocks.machine_industrial_turbine, a("machina", 4, "electrum", 4, "motus", 3, "potentia", 3));
        reg(ModBlocks.machine_refinery, a("machina", 5, "chemica", 5, "permutatio", 3, "aqua", 2));
        reg(ModBlocks.machine_solar_boiler, a("machina", 3, "potentia", 3, "metallum", 2));
        reg(ModBlocks.machine_steam_engine, a("machina", 3, "potentia", 3, "metallum", 2));
        reg(ModBlocks.machine_turbinegas, a("machina", 4, "electrum", 4, "motus", 3, "potentia", 3));
        reg(ModBlocks.rbmk_boiler, a("machina", 3, "potentia", 3, "metallum", 2));
    }

    private static void registerStorageAndLogistics() {
        reg(ModBlocks.barrel_antimatter, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.barrel_corroded, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.barrel_plastic, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.barrel_steel, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.barrel_tcalloy, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.cm_tank, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.conveyor, a("iter", 2, "machina", 1, "metallum", 1));
        reg(ModBlocks.conveyor_chute, a("iter", 2, "machina", 1, "metallum", 1));
        reg(ModBlocks.conveyor_double, a("iter", 2, "machina", 1, "metallum", 1));
        reg(ModBlocks.conveyor_express, a("iter", 2, "machina", 1, "metallum", 1));
        reg(ModBlocks.conveyor_lift, a("iter", 2, "machina", 1, "metallum", 1));
        reg(ModBlocks.conveyor_triple, a("iter", 2, "machina", 1, "metallum", 1));
        reg(ModBlocks.crate, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.crate_ammo, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.crate_can, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.crate_desh, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.crate_iron, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.crate_jungle, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.crate_lead, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.crate_metal, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.crate_red, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.crate_steel, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.crate_supply, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.crate_tungsten, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.crate_weapon, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.drone_crate, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.drone_crate_provider, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.drone_crate_requester, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.fluid_duct_box, a("iter", 2, "machina", 1, "metallum", 1));
        reg(ModBlocks.fluid_duct_exhaust, a("iter", 2, "machina", 1, "metallum", 1));
        reg(ModBlocks.fluid_duct_gauge, a("iter", 2, "machina", 1, "metallum", 1));
        reg(ModBlocks.fluid_duct_neo, a("iter", 2, "machina", 1, "metallum", 1));
        reg(ModBlocks.fluid_duct_paintable, a("iter", 2, "machina", 1, "metallum", 1));
        reg(ModBlocks.fluid_duct_paintable_block_exhaust, a("iter", 2, "machina", 1, "metallum", 1));
        reg(ModBlocks.foundry_tank, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.lox_barrel, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.machine_bigasstank, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.machine_conveyor_press, a("iter", 2, "machina", 1, "metallum", 1));
        reg(ModBlocks.machine_fluidtank, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.machine_puf6_tank, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.machine_uf6_tank, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.oil_pipe, a("iter", 2, "machina", 1, "metallum", 1));
        reg(ModBlocks.pink_barrel, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.pipe_anchor, a("iter", 2, "machina", 1, "metallum", 1));
        reg(ModBlocks.red_barrel, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.silo_hatch, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.silo_hatch_large, a("vacuos", 2, "metallum", 2));
        reg(ModBlocks.taint_barrel, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.vitrified_barrel, a("vacuos", 3, "chemica", 1, "metallum", 2));
        reg(ModBlocks.yellow_barrel, a("vacuos", 3, "chemica", 1, "metallum", 2));
    }

    private static void registerUpgradesAndAnvils() {
        reg(ModItems.stamp_357, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_44, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_50, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_9, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_book, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_desh_357, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_desh_44, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_desh_50, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_desh_9, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_desh_circuit, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_desh_flat, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_desh_plate, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_desh_wire, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_iron_circuit, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_iron_flat, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_iron_plate, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_iron_wire, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_obsidian_circuit, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_obsidian_flat, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_obsidian_plate, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_obsidian_wire, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_steel_circuit, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_steel_flat, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_steel_plate, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_steel_wire, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_stone_circuit, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_stone_flat, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_stone_plate, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_stone_wire, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_titanium_circuit, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_titanium_flat, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_titanium_plate, a("machina", 2, "instrumentum", 2));
        reg(ModItems.stamp_titanium_wire, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_5g, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_afterburn_1, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_afterburn_2, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_afterburn_3, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_centrifuge, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_crystallizer, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_effect_1, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_effect_2, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_effect_3, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_ejector, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_fortune_1, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_fortune_2, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_fortune_3, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_gc_speed, a("machina", 2, "electrum", 2, "motus", 2));
        reg(ModItems.upgrade_health, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_muffler, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_nullifier, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_overdrive_1, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_overdrive_2, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_overdrive_3, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_power_1, a("machina", 2, "electrum", 2, "ordo", 1));
        reg(ModItems.upgrade_power_2, a("machina", 2, "electrum", 2, "ordo", 1));
        reg(ModItems.upgrade_power_3, a("machina", 2, "electrum", 2, "ordo", 1));
        reg(ModItems.upgrade_radius, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_screm, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_shredder, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_smelter, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_speed_1, a("machina", 2, "electrum", 2, "motus", 2));
        reg(ModItems.upgrade_speed_2, a("machina", 2, "electrum", 2, "motus", 2));
        reg(ModItems.upgrade_speed_3, a("machina", 2, "electrum", 2, "motus", 2));
        reg(ModItems.upgrade_stack, a("machina", 2, "instrumentum", 2));
        reg(ModItems.upgrade_template, a("machina", 2, "instrumentum", 2));
        reg(ModBlocks.anvil_arsenic_bronze, a("instrumentum", 4, "fabrico", 2, "metallum", 4));
        reg(ModBlocks.anvil_bismuth_bronze, a("instrumentum", 4, "fabrico", 2, "metallum", 4));
        reg(ModBlocks.anvil_desh, a("instrumentum", 4, "fabrico", 2, "metallum", 4));
        reg(ModBlocks.anvil_dnt, a("instrumentum", 4, "fabrico", 2, "metallum", 4));
        reg(ModBlocks.anvil_ferrouranium, a("instrumentum", 4, "fabrico", 2, "metallum", 4));
        reg(ModBlocks.anvil_iron, a("instrumentum", 4, "fabrico", 2, "metallum", 4));
        reg(ModBlocks.anvil_lead, a("instrumentum", 4, "fabrico", 2, "metallum", 4));
        reg(ModBlocks.anvil_murky, a("instrumentum", 4, "fabrico", 2, "metallum", 4));
        reg(ModBlocks.anvil_osmiridium, a("instrumentum", 4, "fabrico", 2, "metallum", 4));
        reg(ModBlocks.anvil_saturnite, a("instrumentum", 4, "fabrico", 2, "metallum", 4));
        reg(ModBlocks.anvil_schrabidate, a("instrumentum", 4, "fabrico", 2, "metallum", 4));
        reg(ModBlocks.anvil_steel, a("instrumentum", 4, "fabrico", 2, "metallum", 4));
    }
}