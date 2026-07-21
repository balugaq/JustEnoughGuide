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

package com.balugaq.jeg.implementation;

import com.balugaq.jeg.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"deprecated", "deprecation", "DataFlowIssue"})
@NullMarked
public class WatchdogHandler implements Listener {
    public static Object instance = null;
    public static long timeoutTime = 0;

    static {
        try {
            instance = ReflectionUtil.getStaticValue(Class.forName("org.spigotmc.WatchdogThread"), "instance");
            if (instance != null) {
                timeoutTime = ReflectionUtil.getValue(instance, "timeoutTime", long.class);
            }
        } catch (ClassNotFoundException ignored) {
        }
    }

    public static void disableWatchdog() {
        ReflectionUtil.setValue(instance, "timeoutTime", Long.MAX_VALUE / 2);
        ReflectionUtil.setValue(instance, "stopping", true);
    }

    public static void enableWatchdog() {
        ReflectionUtil.setValue(instance, "timeoutTime", timeoutTime);
        ReflectionUtil.setValue(instance, "stopping", false);
    }

    @EventHandler
    public void controlWatchdog(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) {
            return;
        }

        if (!JustEnoughGuide.getConfigManager().isDebug()) {
            return;
        }

        String msg = event.getMessage();
        if (msg.equals("dw 0")) {
            disableWatchdog();
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage("Disabled WatchdogThread by " + player.getName());
            }
        }

        if (msg.equals("dw 1")) {
            enableWatchdog();
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage("Enabled WatchdogThread by " + player.getName());
            }
        }
    }
}
