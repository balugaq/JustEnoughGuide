/*
 * Copyright (c) 2024-2026 balugaq
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
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

/**
 * @author balugaq
 */
@Getter
public class SearchReloadListener implements Listener {
    private boolean scheduleReload = false;

    public SearchReloadListener() {
        JustEnoughGuide.runTimerAsync(() -> {
            if (scheduleReload) {
                JustEnoughGuide.reload(JustEnoughGuide.getInstance(), Bukkit.getConsoleSender());
                scheduleReload = false;
            }
        }, 5000, 20 * 60 * 5);
    }

    @EventHandler
    public void onSlimefunAddonEnable(PluginEnableEvent event) {
        if (event.getPlugin() instanceof SlimefunAddon) {
            scheduleReload = true;
        }
    }

    @EventHandler
    public void onSlimefunAddonDisable(PluginDisableEvent event) {
        if (event.getPlugin() instanceof SlimefunAddon && !"JustEnoughGuide".equals(event.getPlugin().getName())) {
            scheduleReload = true;
        }
    }
}
