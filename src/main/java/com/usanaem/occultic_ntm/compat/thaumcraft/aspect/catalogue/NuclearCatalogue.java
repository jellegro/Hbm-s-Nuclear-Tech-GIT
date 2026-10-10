package com.usanaem.occultic_ntm.compat.thaumcraft.aspect.catalogue;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import static com.usanaem.occultic_ntm.compat.thaumcraft.aspect.catalogue.CatalogueHelper.*;

public final class NuclearCatalogue {

    private NuclearCatalogue() { }

    public static void register() {
        registerFuels();
        registerWaste();
        registerReactorComponents();
    }

    private static void registerFuels() {
        // PWR Fuel Assemblies
        for (int i = 0; i < 15; i++) {
            reg(ModItems.pwr_fuel_hot, i, a("metallum", 4, "strontio", 4, "potentia", 5, "radio", 3));
            reg(ModItems.pwr_fuel_depleted, i, a("metallum", 4, "contaminatio", 5, "radio", 3, "perditio", 2));
        }
        reg(ModItems.pellet_antimatter, a("radio", 2, "potentia", 2));
        reg(ModItems.pellet_buckshot, a("radio", 2, "potentia", 2));
        reg(ModItems.pellet_charged, a("radio", 2, "potentia", 2));
        reg(ModItems.pellet_cluster, a("radio", 2, "potentia", 2));
        reg(ModItems.pellet_gas, a("radio", 2, "potentia", 2));
        reg(ModItems.pellet_rtg, a("radio", 4, "electrum", 2, "ignis", 2, "potentia", 2));
        reg(ModItems.pellet_rtg_actinium, a("radio", 4, "electrum", 2, "ignis", 2, "potentia", 2));
        reg(ModItems.pellet_rtg_americium, a("radio", 4, "electrum", 2, "ignis", 2, "potentia", 2));
        reg(ModItems.pellet_rtg_cobalt, a("radio", 4, "electrum", 2, "ignis", 2, "potentia", 2));
        reg(ModItems.pellet_rtg_depleted, a("radio", 4, "electrum", 2, "ignis", 2, "potentia", 2));
        reg(ModItems.pellet_rtg_gold, a("radio", 4, "electrum", 2, "ignis", 2, "potentia", 2));
        reg(ModItems.pellet_rtg_lead, a("radio", 4, "electrum", 2, "ignis", 2, "potentia", 2));
        reg(ModItems.pellet_rtg_polonium, a("radio", 4, "electrum", 2, "ignis", 2, "potentia", 2));
        reg(ModItems.pellet_rtg_radium, a("radio", 4, "electrum", 2, "ignis", 2, "potentia", 2));
        reg(ModItems.pellet_rtg_strontium, a("radio", 4, "electrum", 2, "ignis", 2, "potentia", 2));
        reg(ModItems.pellet_rtg_weak, a("radio", 4, "electrum", 2, "ignis", 2, "potentia", 2));
        reg(ModItems.rbmk_fuel_empty, a("metallum", 2, "strontio", 3, "potentia", 3, "radio", 2));
        reg(ModItems.rod_dual, a("metallum", 2, "strontio", 3, "potentia", 3, "radio", 2));
        reg(ModItems.rod_dual_empty, a("metallum", 2, "strontio", 3, "potentia", 3, "radio", 2));
        reg(ModItems.rod_empty, a("metallum", 2, "strontio", 3, "potentia", 3, "radio", 2));
        reg(ModItems.rod_of_discord, a("metallum", 2, "strontio", 3, "potentia", 3, "radio", 2));
        reg(ModItems.rod_quad, a("metallum", 2, "strontio", 3, "potentia", 3, "radio", 2));
        reg(ModItems.rod_quad_empty, a("metallum", 2, "strontio", 3, "potentia", 3, "radio", 2));
        reg(ModItems.rod_zirnox_empty, a("metallum", 2, "strontio", 3, "potentia", 3, "radio", 2));
        reg(ModItems.rod_zirnox_les_fuel_depleted, a("metallum", 2, "contaminatio", 4, "radio", 2, "perditio", 1));
        reg(ModItems.rod_zirnox_mox_fuel_depleted, a("metallum", 2, "contaminatio", 4, "radio", 2, "perditio", 1));
        reg(ModItems.rod_zirnox_natural_uranium_fuel_depleted, a("metallum", 2, "contaminatio", 4, "radio", 2, "perditio", 1));
        reg(ModItems.rod_zirnox_plutonium_fuel_depleted, a("metallum", 2, "contaminatio", 4, "radio", 2, "perditio", 1));
        reg(ModItems.rod_zirnox_thorium_fuel_depleted, a("metallum", 2, "contaminatio", 4, "radio", 2, "perditio", 1));
        reg(ModItems.rod_zirnox_tritium, a("metallum", 2, "strontio", 3, "potentia", 3, "radio", 2));
        reg(ModItems.rod_zirnox_u233_fuel_depleted, a("metallum", 2, "contaminatio", 4, "radio", 2, "perditio", 1));
        reg(ModItems.rod_zirnox_u235_fuel_depleted, a("metallum", 2, "contaminatio", 4, "radio", 2, "perditio", 1));
        reg(ModItems.rod_zirnox_uranium_fuel_depleted, a("metallum", 2, "contaminatio", 4, "radio", 2, "perditio", 1));
        reg(ModItems.rod_zirnox_zfb_mox_depleted, a("metallum", 2, "contaminatio", 4, "radio", 2, "perditio", 1));
    }

    private static void registerWaste() {
        reg(ModItems.billet_nuclear_waste, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.fallout, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.nuclear_waste, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.nuclear_waste_long, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.nuclear_waste_long_depleted, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.nuclear_waste_long_depleted_tiny, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.nuclear_waste_long_tiny, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.nuclear_waste_short, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.nuclear_waste_short_depleted, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.nuclear_waste_short_depleted_tiny, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.nuclear_waste_short_tiny, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.nuclear_waste_tiny, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.nuclear_waste_vitrified, a("vitreus", 3, "contaminatio", 3, "vinculum", 2));
        reg(ModItems.nuclear_waste_vitrified_tiny, a("vitreus", 3, "contaminatio", 3, "vinculum", 2));
        reg(ModItems.trinitite, a("contaminatio", 3, "radio", 2, "terra", 2, "vitreus", 1));
        reg(ModItems.waste_mox, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.waste_natural_uranium, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.waste_plutonium, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.waste_schrabidium, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.waste_thorium, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.waste_u233, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.waste_u235, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.waste_uranium, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModItems.waste_zfb_mox, a("contaminatio", 3, "radio", 2, "perditio", 1));
        reg(ModBlocks.block_corium, a("contaminatio", 6, "strontio", 4, "ignis", 4, "perditio", 3));
        reg(ModBlocks.block_corium_cobble, a("contaminatio", 6, "strontio", 4, "ignis", 4, "perditio", 3));
        reg(ModBlocks.block_fallout, a("contaminatio", 4, "radio", 3, "perditio", 2, "aer", 1));
        reg(ModBlocks.block_trinitite, a("contaminatio", 4, "radio", 3, "terra", 3, "vitreus", 2));
        reg(ModBlocks.block_waste, a("contaminatio", 4, "radio", 3, "metallum", 2));
        reg(ModBlocks.block_waste_painted, a("contaminatio", 4, "radio", 3, "metallum", 2));
        reg(ModBlocks.block_waste_vitrified, a("contaminatio", 4, "radio", 3, "metallum", 2));
        reg(ModBlocks.corium_block, a("contaminatio", 6, "strontio", 4, "ignis", 4, "perditio", 3));
        reg(ModBlocks.fallout, a("contaminatio", 4, "radio", 3, "perditio", 2, "aer", 1));
        reg(ModBlocks.glass_trinitite, a("contaminatio", 4, "radio", 3, "terra", 3, "vitreus", 2));
        reg(ModBlocks.machine_waste_drum, a("contaminatio", 4, "radio", 3, "metallum", 2));
        reg(ModBlocks.waste_earth, a("contaminatio", 4, "radio", 3, "metallum", 2));
        reg(ModBlocks.waste_leaves, a("contaminatio", 4, "radio", 3, "metallum", 2));
        reg(ModBlocks.waste_log, a("contaminatio", 4, "radio", 3, "metallum", 2));
        reg(ModBlocks.waste_mycelium, a("contaminatio", 4, "radio", 3, "metallum", 2));
        reg(ModBlocks.waste_planks, a("contaminatio", 4, "radio", 3, "metallum", 2));
        reg(ModBlocks.waste_trinitite, a("contaminatio", 4, "radio", 3, "terra", 3, "vitreus", 2));
        reg(ModBlocks.waste_trinitite_red, a("contaminatio", 4, "radio", 3, "terra", 3, "vitreus", 2));
    }

    private static void registerReactorComponents() {
        reg(ModBlocks.dfc_core, a("machina", 4, "strontio", 4, "potentia", 4, "metallum", 3));
        reg(ModBlocks.dfc_emitter, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.dfc_injector, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.dfc_receiver, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.dfc_stabilizer, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.fusion_boiler, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.fusion_breeder, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.fusion_collector, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.fusion_component, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.fusion_coupler, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.fusion_klystron, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.fusion_klystron_creative, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.fusion_mhdt, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.fusion_plasma_forge, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.fusion_torus, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.icf_block, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.icf_component, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.icf_controller, a("machina", 3, "vinculum", 3, "electrum", 2, "ordo", 2));
        reg(ModBlocks.icf_laser_component, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.pwr_block, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.pwr_casing, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.pwr_channel, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.pwr_control, a("machina", 3, "vinculum", 3, "electrum", 2, "ordo", 2));
        reg(ModBlocks.pwr_controller, a("machina", 3, "vinculum", 3, "electrum", 2, "ordo", 2));
        reg(ModBlocks.pwr_heatex, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.pwr_heatsink, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.pwr_neutron_source, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.pwr_port, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.pwr_reflector, a("machina", 2, "terra", 3, "ordo", 2, "tutamen", 2));
        reg(ModBlocks.rbmk_absorber, a("machina", 3, "vinculum", 3, "electrum", 2, "ordo", 2));
        reg(ModBlocks.rbmk_autoloader, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_blank, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_boiler, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_console, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_control, a("machina", 3, "vinculum", 3, "electrum", 2, "ordo", 2));
        reg(ModBlocks.rbmk_control_auto, a("machina", 3, "vinculum", 3, "electrum", 2, "ordo", 2));
        reg(ModBlocks.rbmk_control_mod, a("machina", 3, "vinculum", 3, "electrum", 2, "ordo", 2));
        reg(ModBlocks.rbmk_control_reasim, a("machina", 3, "vinculum", 3, "electrum", 2, "ordo", 2));
        reg(ModBlocks.rbmk_control_reasim_auto, a("machina", 3, "vinculum", 3, "electrum", 2, "ordo", 2));
        reg(ModBlocks.rbmk_cooler, a("machina", 2, "gelum", 3, "aqua", 2, "chemica", 1));
        reg(ModBlocks.rbmk_crane_console, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_display, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_display_blank, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_gauge, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_graph, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_heater, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_indicator, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_key_pad, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_lever, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_loader, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_moderator, a("machina", 2, "terra", 3, "ordo", 2, "tutamen", 2));
        reg(ModBlocks.rbmk_numitron, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_outgasser, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_reflector, a("machina", 2, "terra", 3, "ordo", 2, "tutamen", 2));
        reg(ModBlocks.rbmk_rod, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_rod_mod, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_rod_reasim, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_rod_reasim_mod, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_steam_inlet, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_steam_outlet, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_storage, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.rbmk_terminal, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.watz_cooler, a("machina", 2, "gelum", 3, "aqua", 2, "chemica", 1));
        reg(ModBlocks.watz_element, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.watz_end, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.watz_pump, a("machina", 3, "metallum", 3, "tutamen", 2));
        reg(ModBlocks.zirnox_destroyed, a("machina", 3, "metallum", 3, "tutamen", 2));
    }
}
