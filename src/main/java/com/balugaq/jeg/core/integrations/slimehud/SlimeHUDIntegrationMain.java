/*
 * Copyright (c) 2024-2026 balugaq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.balugaq.jeg.core.integrations.slimehud;

import com.balugaq.jeg.core.lang.Lang;
import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.MinecraftVersion;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.Snowable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Barrel;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Comparator;
import org.bukkit.block.data.type.DaylightDetector;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.block.data.type.Jukebox;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.block.data.type.SculkShrieker;
import org.bukkit.block.data.type.TNT;
import org.bukkit.block.data.type.TrialSpawner;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.block.data.type.Vault;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("removal")
@NullMarked
public class SlimeHUDIntegrationMain implements Integration {

    @SuppressWarnings({"unused"})
    public static String getVanillaBlockName(Player player, Block block) {
        if (block.getType().isAir() || !block.getType().isItem()) {
            return "";
        }

        String name = "";
        String base = ItemStackHelper.getDisplayName(new ItemStack(block.getType()));
        BlockData data = block.getBlockData();
        if (data instanceof Openable d && !d.isOpen() && !(data instanceof Barrel)) {
            name += Lang.t("hud.states.closed");
        }
        if (data instanceof Lightable d1 && d1.isLit()) {
            if (data instanceof Campfire d2 && d2.isSignalFire()) {
                name += Lang.t("hud.states.ignited");
            } else {
                name += Lang.t("hud.states.lit");
            }
        }
        if (data instanceof Waterlogged d && d.isWaterlogged()) {
            name += Lang.t("hud.states.waterlogged");
        }
        if (data instanceof Snowable d && d.isSnowy()) {
            name += Lang.t("hud.states.snowy");
        }
        if (data instanceof Farmland d && d.getMoisture() == d.getMaximumMoisture()) {
            name += Lang.t("hud.states.moist");
        }
        if (data instanceof Powerable d && d.isPowered() && (data instanceof Repeater || data instanceof Comparator)) {
            name += Lang.t("hud.states.activated");
        }
        if (data instanceof DaylightDetector d && d.isInverted()) {
            name += Lang.t("hud.states.night");
        }
        if ((data instanceof Repeater d1 && d1.isLocked()) || (data instanceof Hopper d2 && !d2.isEnabled())) {
            name += Lang.t("hud.states.locked");
        }
        if (data instanceof Jukebox d && d.hasRecord()) {
            name += Lang.t("hud.states.playing");
        }
        if (data instanceof Piston d && d.isExtended()) {
            name += Lang.t("hud.states.extended");
        }
        if (data instanceof PistonHead d && d.isShort()) {
            name += Lang.t("hud.states.short");
        }
        if (data instanceof SculkShrieker d && d.isCanSummon()) {
            name += Lang.t("hud.states.can-summon");
        }
        if (data instanceof SculkShrieker d && d.isShrieking()) {
            name += Lang.t("hud.states.screeching");
        }
        if (data instanceof TNT d && d.isUnstable()) {
            name += Lang.t("hud.states.unstable");
        }
        if (data instanceof Tripwire d && d.isDisarmed()) {
            name += Lang.t("hud.states.triggered");
        }
        if (MinecraftVersion.current().isAtLeast(MinecraftVersion.V1_21)) {
            if ((data instanceof TrialSpawner d1 && d1.isOminous()) || (data instanceof Vault d2 && d2.isOminous())) {
                name += Lang.t("hud.states.ominous");
            }
        }
        name += base;
        if (data instanceof EndPortalFrame d && d.hasEye()) {
            name += Lang.t("hud.states.has-eyes");
        }
        return name;
    }

    @Override
    public String getHookPlugin() {
        return "SlimeHUD";
    }

    @Override
    public void onEnable() {
        JEGGuideSettings.addOption(HUDMachineInfoLocationGuideOption.instance());
        JEGGuideSettings.addOption(VanillaBlockHUDDisplayGuideOption.instance());
        JEGGuideSettings.addOption(HUDReachBlockGuideOption.instance());
        for (Player player : Bukkit.getOnlinePlayers()) {
            JEGPlayerWAILA.wrap(player);
        }

        JustEnoughGuide.getListenerManager().registerListener(new PlayerWAILAUpdateListener());
    }

    @Override
    public void onDisable() {
        JEGPlayerWAILA.onDisable();
    }
}
