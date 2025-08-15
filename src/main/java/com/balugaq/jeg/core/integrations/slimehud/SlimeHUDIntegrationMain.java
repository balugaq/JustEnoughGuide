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

package com.balugaq.jeg.core.integrations.slimehud;

import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import io.github.schntgaispock.slimehud.SlimeHUD;
import io.github.schntgaispock.slimehud.waila.PlayerWAILA;
import io.github.schntgaispock.slimehud.waila.WAILAManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * @author balugaq
 * @since 1.9
 */
public class SlimeHUDIntegrationMain implements Integration {
    public static final long tickRate = SlimeHUD.getInstance().getConfig().getLong("waila.tick-rate");

    @NotNull
    public static Map<UUID, PlayerWAILA> getWailaMap() {
        return WAILAManager.getInstance().getWailas();
    }

    public static void wrap(@NotNull Player player) {
        synchronized (getWailaMap()) {
            getWailaMap().compute(player.getUniqueId(), (k, waila) -> runTaskAsync(JustEnoughGuide.getInstance(), JEGPlayerWAILA.wrap(player, waila)));
        }
    }

    public static <T extends BukkitRunnable> T runTaskAsync(@NotNull Plugin plugin, @Nullable T runnable) {
        if (runnable == null) {
            return null;
        }
        runnable.runTaskTimerAsynchronously(plugin, 0L, tickRate);
        return runnable;
    }

    @Override
    public @NotNull String getHookPlugin() {
        return "SlimeHUD";
    }

    @Override
    public void onEnable() {
        JEGGuideSettings.addOption(HUDMachineInfoLocationGuideOption.instance());
        for (Player player : Bukkit.getOnlinePlayers()) {
            wrap(player);
        }

        JustEnoughGuide.getListenerManager().registerListener(new PlayerWAILAUpdateListener());
    }

    @Override
    public void onDisable() {
        synchronized (getWailaMap()) {
            for (Map.Entry<UUID, PlayerWAILA> entry : getWailaMap().entrySet()) {
                getWailaMap().compute(entry.getKey(), (k, waila) -> runTaskAsync(SlimeHUD.getInstance(), JEGPlayerWAILA.unwrap(waila)));
            }
        }
    }
}
