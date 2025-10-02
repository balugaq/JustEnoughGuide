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

package com.balugaq.jeg.implementation;

import com.balugaq.jeg.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"deprecated", "deprecation", "DataFlowIssue"})
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
