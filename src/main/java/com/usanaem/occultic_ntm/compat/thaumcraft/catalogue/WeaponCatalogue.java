package com.usanaem.occultic_ntm.compat.thaumcraft.catalogue;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import static com.usanaem.occultic_ntm.compat.thaumcraft.catalogue.CatalogueHelper.*;

public final class WeaponCatalogue {

    private WeaponCatalogue() { }

    public static void register() {
        registerExplosives();
        registerAmmunition();
        registerFirearms();
        registerTurrets();
    }

    private static void registerExplosives() {
        reg(ModBlocks.block_c4, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.bomb_multi, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.c4, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.charge_c4, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.charge_dynamite, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.charge_miner, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.charge_semtex, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.charger, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.det_charge, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.det_miner, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.det_nuke, a("detonatio", 8, "strontio", 6, "radio", 4, "perditio", 5));
        reg(ModBlocks.emp_bomb, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.fissure_bomb, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.float_bomb, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.mine_ap, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.mine_fat, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.mine_he, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.mine_naval, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.mine_shrap, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModBlocks.nuke_boy, a("detonatio", 8, "strontio", 6, "radio", 4, "perditio", 5));
        reg(ModBlocks.nuke_custom, a("detonatio", 8, "strontio", 6, "radio", 4, "perditio", 5));
        reg(ModBlocks.nuke_fleija, a("detonatio", 8, "strontio", 6, "radio", 4, "perditio", 5));
        reg(ModBlocks.nuke_fstbmb, a("detonatio", 8, "strontio", 6, "radio", 4, "perditio", 5));
        reg(ModBlocks.nuke_gadget, a("detonatio", 8, "strontio", 6, "radio", 4, "perditio", 5));
        reg(ModBlocks.nuke_man, a("detonatio", 8, "strontio", 6, "radio", 4, "perditio", 5));
        reg(ModBlocks.nuke_mike, a("detonatio", 8, "strontio", 6, "radio", 4, "perditio", 5));
        reg(ModBlocks.nuke_n2, a("detonatio", 8, "strontio", 6, "radio", 4, "perditio", 5));
        reg(ModBlocks.nuke_prototype, a("detonatio", 8, "strontio", 6, "radio", 4, "perditio", 5));
        reg(ModBlocks.nuke_solinium, a("detonatio", 8, "strontio", 6, "radio", 4, "perditio", 5));
        reg(ModBlocks.nuke_tsar, a("detonatio", 8, "strontio", 6, "radio", 4, "perditio", 5));
        reg(ModBlocks.tnt, a("detonatio", 5, "perditio", 4, "ignis", 3));
        reg(ModItems.ball_dynamite, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.grenade_extra, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.grenade_filling, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.grenade_fuze, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.grenade_shell, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.grenade_universal, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.gun_missile_launcher, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.ingot_c4, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_anti_ballistic, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_assembly, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_bhole, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_burst, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_buster, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_buster_strong, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_cluster, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_cluster_strong, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_custom, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_decoy, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_doomsday, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_doomsday_rusted, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_drill, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_emp, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_emp_strong, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_generic, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_incendiary, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_incendiary_strong, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_inferno, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_kit, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_lambda, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_micro, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_nuclear, a("detonatio", 7, "strontio", 5, "radio", 3));
        reg(ModItems.missile_nuclear_cluster, a("detonatio", 7, "strontio", 5, "radio", 3));
        reg(ModItems.missile_rain, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_schrabidium, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_shuttle, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_soyuz, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_soyuz_lander, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_stealth, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_strong, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_taint, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_test, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.missile_volcano, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.mp_warhead_10_buster, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.mp_warhead_10_cloud, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.mp_warhead_10_he, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.mp_warhead_10_incendiary, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.mp_warhead_10_nuclear, a("detonatio", 7, "strontio", 5, "radio", 3));
        reg(ModItems.mp_warhead_10_nuclear_large, a("detonatio", 7, "strontio", 5, "radio", 3));
        reg(ModItems.mp_warhead_10_taint, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.mp_warhead_15_balefire, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.mp_warhead_15_boxcar, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.mp_warhead_15_he, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.mp_warhead_15_incendiary, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.mp_warhead_15_n2, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.mp_warhead_15_nuclear, a("detonatio", 7, "strontio", 5, "radio", 3));
        reg(ModItems.mp_warhead_15_nuclear_mimi, a("detonatio", 7, "strontio", 5, "radio", 3));
        reg(ModItems.mp_warhead_15_nuclear_shark, a("detonatio", 7, "strontio", 5, "radio", 3));
        reg(ModItems.mp_warhead_15_turbine, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.pc4, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.stick_c4, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.stick_dynamite, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.warhead_buster_large, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.warhead_buster_medium, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.warhead_buster_small, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.warhead_cluster_large, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.warhead_cluster_medium, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.warhead_cluster_small, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.warhead_generic_large, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.warhead_generic_medium, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.warhead_generic_small, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.warhead_incendiary_large, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.warhead_incendiary_medium, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.warhead_incendiary_small, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.warhead_mirv, a("detonatio", 4, "perditio", 3, "telum", 2));
        reg(ModItems.warhead_nuclear, a("detonatio", 7, "strontio", 5, "radio", 3));
        reg(ModItems.warhead_volcano, a("detonatio", 4, "perditio", 3, "telum", 2));
    }

    private static void registerAmmunition() {
        for (int i = 0; i < 95; i++) {
            reg(ModItems.ammo_standard, i, a("telum", 2, "detonatio", 2, "metallum", 1));
        }
        regWildcard(ModItems.ammo_arty, a("telum", 2, "detonatio", 2, "metallum", 1));
        regWildcard(ModItems.ammo_bag, a("telum", 2, "detonatio", 2, "metallum", 1));
        regWildcard(ModItems.ammo_bag_infinite, a("telum", 2, "detonatio", 2, "metallum", 1));
        regWildcard(ModItems.ammo_container, a("telum", 2, "detonatio", 2, "metallum", 1));
        regWildcard(ModItems.ammo_dgk, a("telum", 2, "detonatio", 2, "metallum", 1));
        regWildcard(ModItems.ammo_himars, a("telum", 2, "detonatio", 2, "metallum", 1));
        regWildcard(ModItems.ammo_secret, a("telum", 2, "detonatio", 2, "metallum", 1));
    }

    private static void registerFirearms() {
        regWildcard(ModItems.gun_aberrator, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_aberrator_eott, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_am180, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_amat, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_amat_penance, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_amat_subtlety, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_autoshotgun, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_autoshotgun_heretic, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_autoshotgun_sexy, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_autoshotgun_shredder, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_b92, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_b92_ammo, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_bolter, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_carbine, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_charge_thrower, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_chemthrower, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_coilgun, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_congolake, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_double_barrel, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_double_barrel_sacred_dragon, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_drill, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_fatman, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_fireext, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_flamer, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_flamer_daybreaker, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_flamer_topaz, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_flaregun, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_folly, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_g3, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_g3_zebra, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_greasegun, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_hangman, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_heavy_revolver, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_heavy_revolver_lilmac, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_heavy_revolver_protege, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_henry, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_henry_lincoln, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_kit_1, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_kit_2, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_lag, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_laser_pistol, a("telum", 5, "electrum", 4, "machina", 3));
        regWildcard(ModItems.gun_laser_pistol_morning_glory, a("telum", 5, "electrum", 4, "machina", 3));
        regWildcard(ModItems.gun_laser_pistol_pew_pew, a("telum", 5, "electrum", 4, "machina", 3));
        regWildcard(ModItems.gun_lasrifle, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_liberator, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_light_revolver, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_light_revolver_atlas, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_light_revolver_dani, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_m2, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_maresleg, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_maresleg_akimbo, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_maresleg_broken, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_mas36, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_minigun, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_minigun_dual, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_minigun_lacunae, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_missile_launcher, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_mk108, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_n_i_4_n_i, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_pa_melee, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_pa_ranged, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_panzerschreck, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_pepperbox, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_quadro, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_spas12, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_star_f, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_star_f_akimbo, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_stg77, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_stinger, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_tau, a("telum", 5, "electrum", 4, "machina", 3));
        regWildcard(ModItems.gun_tesla_cannon, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_uzi, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.gun_uzi_akimbo, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.weapon_mod_caliber, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.weapon_mod_generic, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.weapon_mod_special, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
        regWildcard(ModItems.weapon_mod_test, a("telum", 4, "detonatio", 2, "metallum", 3, "machina", 2));
    }

    private static void registerTurrets() {
        reg(ModBlocks.turret_arty, a("telum", 5, "machina", 4, "electrum", 3, "sensus", 2));
        reg(ModBlocks.turret_chekhov, a("telum", 5, "machina", 4, "electrum", 3, "sensus", 2));
        reg(ModBlocks.turret_friendly, a("telum", 5, "machina", 4, "electrum", 3, "sensus", 2));
        reg(ModBlocks.turret_fritz, a("telum", 5, "machina", 4, "electrum", 3, "sensus", 2));
        reg(ModBlocks.turret_himars, a("telum", 5, "machina", 4, "electrum", 3, "sensus", 2));
        reg(ModBlocks.turret_howard, a("telum", 5, "machina", 4, "electrum", 3, "sensus", 2));
        reg(ModBlocks.turret_howard_damaged, a("telum", 5, "machina", 4, "electrum", 3, "sensus", 2));
        reg(ModBlocks.turret_jeremy, a("telum", 5, "machina", 4, "electrum", 3, "sensus", 2));
        reg(ModBlocks.turret_maxwell, a("telum", 5, "machina", 4, "electrum", 3, "sensus", 2));
        reg(ModBlocks.turret_richard, a("telum", 5, "machina", 4, "electrum", 3, "sensus", 2));
        reg(ModBlocks.turret_sentry, a("telum", 5, "machina", 4, "electrum", 3, "sensus", 2));
        reg(ModBlocks.turret_sentry_damaged, a("telum", 5, "machina", 4, "electrum", 3, "sensus", 2));
        reg(ModBlocks.turret_tauon, a("telum", 5, "machina", 4, "electrum", 3, "sensus", 2));
    }
}