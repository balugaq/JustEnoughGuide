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

package com.balugaq.jeg.core.listeners;

import com.balugaq.jeg.api.objects.annotations.PatchCode;
import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.GuideUtil;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunGuideOpenEvent;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class listens to {@link SlimefunGuideOpenEvent} and opens the corresponding guide for the player.
 *
 * @author TheBusyBiscuit
 * @author balugaq
 * @since 1.0
 */
@Getter
@NullMarked
public class GuideListener implements Listener {
    public static final Map<Player, SlimefunGuideMode> guideModeMap = new ConcurrentHashMap<>();
    public final boolean giveOnFirstJoin;

    public GuideListener() {
        this.giveOnFirstJoin = Slimefun.getConfigManager().getPluginConfig().getBoolean("guide.receive-on-first-join");
    }

    @Internal
    public static void openGuide(Player player, SlimefunGuideMode mode) {
        if (!player.isOp() && !Slimefun.getWorldSettingsService().isWorldEnabled(player.getWorld())) {
            player.sendMessage(ChatColors.color("&c你没有权限打开粘液科技指南书"));
            return;
        }

        Optional<PlayerProfile> optional = PlayerProfile.find(player);

        if (optional.isPresent()) {
            PlayerProfile profile = optional.get();
            SlimefunGuideImplementation guide = GuideUtil.getGuide(player, mode);
            SlimefunGuideMode lastMode = GuideUtil.getLastGuideMode(player);
            guideModeMap.put(player, mode);
            if (lastMode != mode) {
                GuideUtil.openMainMenu(player, profile, mode, 1);
            } else {
                profile.getGuideHistory().openLastEntry(guide);
            }
        } else {
            GuideUtil.openMainMenuAsync(player, mode, 1);
        }
    }

    @Internal
    public static void openGuideAsync(Player player, SlimefunGuideMode mode) {
        JustEnoughGuide.runLaterAsync(() -> openGuide(player, mode), 1L);
    }

    @PatchCode("io.github.thebusybiscuit.slimefun4.implementation.listeners.SlimefunGuideListener.tryOpenGuide(Player, PlayerRightClickEvent, SlimefunGuideMode)")
    @Internal
    public static Event.Result tryOpenGuide(Player p, PlayerRightClickEvent e, SlimefunGuideMode layout) {
        ItemStack item = e.getItem();
        if (SlimefunUtils.isItemSimilar(item, SlimefunGuide.getItem(layout), false, false)) {
            if (!Slimefun.getWorldSettingsService().isWorldEnabled(p.getWorld())) {
                Slimefun.getLocalization().sendMessage(p, "messages.disabled-item", true);
                return Event.Result.DENY;
            } else {
                return Event.Result.ALLOW;
            }
        } else {
            return Event.Result.DEFAULT;
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onGuideOpen(SlimefunGuideOpenEvent e) {
        e.setCancelled(true);

        Player p = e.getPlayer();
        SlimefunGuideMode mode = e.getGuideLayout();
        try {
            openGuide(p, mode);
        } catch (Exception ex) {
            PlayerProfile.find(e.getPlayer()).ifPresent(profile -> {
                GuideUtil.removeLastEntry(profile.getGuideHistory());
            });
        }
    }

    @Internal
    public void openGuideSync(Player player, SlimefunGuideMode mode) {
        JustEnoughGuide.runLater(
            () -> {
                Optional<PlayerProfile> optional = PlayerProfile.find(player);

                if (optional.isPresent()) {
                    PlayerProfile profile = optional.get();
                    SlimefunGuideImplementation guide = GuideUtil.getGuide(player, mode);
                    SlimefunGuideMode lastMode = guideModeMap.get(player);
                    guideModeMap.put(player, mode);
                    if (lastMode != mode) {
                        GuideUtil.openMainMenu(player, profile, mode, 1);
                    } else {
                        profile.getGuideHistory().openLastEntry(guide);
                    }
                } else {
                    GuideUtil.openMainMenuAsync(player, mode, 1);
                }
            }, 1L
        );
    }

    @PatchCode("io.github.thebusybiscuit.slimefun4.implementation.listeners.SlimefunGuideListener.onInteract(PlayerRightClickEvent)")
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInteract(PlayerRightClickEvent e) {
        Player p = e.getPlayer();

        if (tryOpenGuide(p, e, SlimefunGuideMode.SURVIVAL_MODE) == Event.Result.ALLOW) {
            if (p.isSneaking()) {
                JEGGuideSettings.openSettings(p, e.getItem());
            } else {
                SlimefunGuideOpenEvent event = new SlimefunGuideOpenEvent(
                    p, e.getItem(),
                    SlimefunGuideMode.SURVIVAL_MODE
                );
                Bukkit.getPluginManager().callEvent(event);
            }
        } else if (tryOpenGuide(p, e, SlimefunGuideMode.CHEAT_MODE) == Event.Result.ALLOW) {
            if (p.isSneaking()) {
                JEGGuideSettings.openSettings(
                    p,
                    p.hasPermission("slimefun.cheat.items")
                        ? e.getItem()
                        : SlimefunGuide.getItem(SlimefunGuideMode.SURVIVAL_MODE)
                );
            } else {
                p.chat("/sf cheat");
            }
        }
    }

    @PatchCode("io.github.thebusybiscuit.slimefun4.implementation.listeners.SlimefunGuideListener.onJoin(PlayerJoinEvent)")
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (this.giveOnFirstJoin && !e.getPlayer().hasPlayedBefore()) {
            Player p = e.getPlayer();
            if (!Slimefun.getWorldSettingsService().isWorldEnabled(p.getWorld())) {
                return;
            }

            SlimefunGuideMode type = SlimefunGuide.getDefaultMode();
            p.getInventory().addItem(SlimefunGuide.getItem(type).clone());
        }
    }
}
