/*
 * Copyright (c) 2024-2025 balugaq
 *
 * This file is part of JustEnoughGuide, available under MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The author's name (balugaq or 大香蕉) and project name (JustEnoughGuide or JEG) shall not be
 *   removed or altered from any source distribution or documentation.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.balugaq.jeg.core.listeners;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunGuideOpenEvent;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class listens to {@link SlimefunGuideOpenEvent}
 * and opens the corresponding guide for the player.
 *
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings("DuplicatedCode")
@Getter
public class GuideListener implements Listener {
    public static final int OPEN_GUIDE_DEFAULT_FATAL_ERROR_CODE = 12208;
    public static final int OPEN_GUIDE_ASYNC_FATAL_ERROR_CODE = 12209;
    public static final int OPEN_GUIDE_SYNC_FATAL_ERROR_CODE = 12210;
    public static final Map<Player, SlimefunGuideMode> guideModeMap = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    public void onGuideOpen(@NotNull SlimefunGuideOpenEvent e) {
        if (!e.isCancelled()) {
            e.setCancelled(true);

            Player p = e.getPlayer();
            SlimefunGuideMode mode = e.getGuideLayout();
            try {
                openGuide(p, mode);
            } catch (Throwable ex) {
                try {
                    openGuideAsync(p, mode);
                } catch (Throwable ex2) {
                    try {
                        openGuideSync(p, mode);
                    } catch (Throwable ex3) {
                        Debug.traceExactly(ex, "opening guide", OPEN_GUIDE_DEFAULT_FATAL_ERROR_CODE);
                        Debug.traceExactly(ex2, "opening guide asynchronously", OPEN_GUIDE_ASYNC_FATAL_ERROR_CODE);
                        Debug.traceExactly(ex3, "opening guide synchronously", OPEN_GUIDE_SYNC_FATAL_ERROR_CODE);
                        PlayerProfile.find(e.getPlayer())
                                .ifPresent(profile -> GuideUtil.removeLastEntry(profile.getGuideHistory()));
                    }
                }
            }
        }
    }

    public void openGuide(@NotNull Player player, @NotNull SlimefunGuideMode mode) {
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
    }

    public void openGuideAsync(@NotNull Player player, @NotNull SlimefunGuideMode mode) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(JustEnoughGuide.getInstance(), () -> {
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
        }, 1L);
    }

    public void openGuideSync(@NotNull Player player, @NotNull SlimefunGuideMode mode) {
        Bukkit.getScheduler().runTaskLater(JustEnoughGuide.getInstance(), () -> {
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
        }, 1L);
    }
}
