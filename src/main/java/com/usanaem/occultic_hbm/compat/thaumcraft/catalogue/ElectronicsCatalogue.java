package com.usanaem.occultic_hbm.compat.thaumcraft.catalogue;

import com.hbm.items.ModItems;
import static com.usanaem.occultic_hbm.compat.thaumcraft.catalogue.CatalogueHelper.*;

public final class ElectronicsCatalogue {

    private ElectronicsCatalogue() { }

    public static void register() {
        registerCircuits();
        registerBatteries();
        registerComponents();
    }

    private static void registerCircuits() {
        reg(ModItems.circuit, 0, a("electrum", 2, "vitreus", 1, "machina", 1));
        reg(ModItems.circuit, 1, a("electrum", 2, "potentia", 1, "vinculum", 1));
        reg(ModItems.circuit, 2, a("electrum", 3, "potentia", 2, "metallum", 1));
        reg(ModItems.circuit, 3, a("electrum", 2, "chemica", 2, "machina", 1));
        reg(ModItems.circuit, 4, a("vitreus", 2, "electrum", 2, "ordo", 1));
        reg(ModItems.circuit, 5, a("cognitio", 2, "electrum", 3, "machina", 2));
        reg(ModItems.circuit, 6, a("cognitio", 3, "electrum", 4, "machina", 3));
        reg(ModItems.circuit, 7, a("cognitio", 4, "electrum", 5, "magneto", 2));
        reg(ModItems.circuit, 8, a("cognitio", 5, "nebrisum", 3, "electrum", 4));
        reg(ModItems.circuit, 9, a("cognitio", 5, "strontio", 3, "electrum", 4, "radio", 2));
        reg(ModItems.circuit, 10, a("cognitio", 2, "electrum", 2, "instrumentum", 1));
        reg(ModItems.circuit, 11, a("cognitio", 3, "lux", 3, "electrum", 2));
        reg(ModItems.circuit, 12, a("cognitio", 3, "electrum", 3, "machina", 2));
        reg(ModItems.circuit, 13, a("cognitio", 3, "electrum", 3, "machina", 2));
        reg(ModItems.circuit, 14, a("cognitio", 3, "electrum", 3, "machina", 2));
        reg(ModItems.circuit, 15, a("cognitio", 3, "electrum", 3, "machina", 2));
        reg(ModItems.circuit, 16, a("cognitio", 3, "electrum", 3, "machina", 2));
        reg(ModItems.circuit, 17, a("cognitio", 3, "electrum", 3, "machina", 2));
        reg(ModItems.circuit, 18, a("cognitio", 3, "electrum", 3, "machina", 2));
        reg(ModItems.circuit, 19, a("cognitio", 3, "electrum", 3, "machina", 2));
        reg(ModItems.circuit, 20, a("cognitio", 3, "electrum", 3, "machina", 2));
    }

    private static void registerBatteries() {
        reg(ModItems.armor_battery, a("electrum", 3, "potentia", 2, "metallum", 1));
        reg(ModItems.armor_battery_mk2, a("electrum", 3, "potentia", 2, "metallum", 1));
        reg(ModItems.armor_battery_mk3, a("electrum", 3, "potentia", 2, "metallum", 1));
        reg(ModItems.battery_creative, a("electrum", 10, "praecantatio", 8, "potentia", 8));
        reg(ModItems.battery_pack, a("electrum", 3, "potentia", 2, "metallum", 1));
        reg(ModItems.battery_potato, a("electrum", 3, "potentia", 2, "metallum", 1));
        reg(ModItems.battery_potatos, a("electrum", 3, "potentia", 2, "metallum", 1));
        reg(ModItems.battery_sc, a("electrum", 3, "potentia", 2, "metallum", 1));
        reg(ModItems.battery_spark, a("electrum", 3, "potentia", 2, "metallum", 1));
        reg(ModItems.battery_trixite, a("electrum", 3, "potentia", 2, "metallum", 1));
        reg(ModItems.hev_battery, a("electrum", 3, "potentia", 2, "metallum", 1));
        reg(ModItems.mp_fuselage_10_solid_battery, a("electrum", 3, "potentia", 2, "metallum", 1));
    }

    private static void registerComponents() {
        reg(ModItems.coil_copper, a("magneto", 3, "electrum", 2, "metallum", 2));
        reg(ModItems.coil_copper_torus, a("magneto", 3, "electrum", 2, "metallum", 2));
        reg(ModItems.coil_gold, a("magneto", 3, "electrum", 2, "metallum", 2));
        reg(ModItems.coil_gold_torus, a("magneto", 3, "electrum", 2, "metallum", 2));
        reg(ModItems.coil_magnetized_tungsten, a("magneto", 3, "electrum", 2, "metallum", 2));
        reg(ModItems.coil_tungsten, a("magneto", 3, "electrum", 2, "metallum", 2));
        reg(ModItems.gun_coilgun, a("magneto", 3, "electrum", 2, "metallum", 2));
        reg(ModItems.motor, a("motus", 3, "electrum", 3, "magneto", 2, "machina", 2));
        reg(ModItems.motor_bismuth, a("motus", 3, "electrum", 3, "magneto", 2, "machina", 2));
        reg(ModItems.motor_desh, a("motus", 3, "electrum", 3, "magneto", 2, "machina", 2));
        reg(ModItems.pa_coil, a("magneto", 3, "electrum", 2, "metallum", 2));
        reg(ModItems.wire_dense, a("electrum", 2, "machina", 1, "metallum", 1));
        reg(ModItems.wire_fine, a("electrum", 2, "machina", 1, "metallum", 1));
    }
}
