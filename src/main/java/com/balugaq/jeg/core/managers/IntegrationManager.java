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

package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Lang;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * This class is responsible for managing integrations with other plugins.
 *
 * @author balugaq
 * @since 1.2
 */
@Getter
public class IntegrationManager extends AbstractManager {
    private final @NotNull JavaPlugin plugin;
    private final boolean enabledNetworksExpansion;
    private final boolean enabledOreWorkshop;
    private final boolean enabledSlimefunTranslation;

    public IntegrationManager(@NotNull JavaPlugin plugin) {
        boolean tmp;
        this.plugin = plugin;

        // Check if NetworksExpansion is enabled
        try {
            Class.forName("com.ytdd9527.networksexpansion.core.listener.NetworksGuideListener");
            tmp = true;
        } catch (ClassNotFoundException e) {
            tmp = false;
        }

        enabledNetworksExpansion = tmp;

        // Check if OreWorkshop is enabled
        this.enabledOreWorkshop = plugin.getServer().getPluginManager().isPluginEnabled("OreWorkshop");
        this.enabledSlimefunTranslation = plugin.getServer().getPluginManager().isPluginEnabled("SlimefunTranslation");
        if (enabledSlimefunTranslation) {
            JustEnoughGuide.getInstance().getLogger().info(Lang.getStartup("hook-slimefun-translation"));
        }
    }
}
