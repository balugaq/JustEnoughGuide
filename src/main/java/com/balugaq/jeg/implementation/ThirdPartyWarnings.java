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

import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.core.managers.IntegrationManager;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.platform.PlatformUtil;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("UnstableApiUsage")
public class ThirdPartyWarnings {
    @CallTimeSensitive(CallTimeSensitive.AfterIntegrationsLoaded)
    public static void check() {
        IntegrationManager.scheduleRun(ThirdPartyWarnings::checkInternal);
    }

    @ApiStatus.Internal
    private static void checkInternal() {
        if (!PlatformUtil.isPaper()) {
            // CANNOT check dependencies because PluginMeta is Paper's API
            PaperLib.suggestPaper(JustEnoughGuide.getInstance());
            return;
        }

        if (JustEnoughGuide.getIntegrationManager().isEnabledNetworksExpansion() && JustEnoughGuide.getIntegrationManager().isEnabledLogiTech()) { // Fuck Logitech. Fuck NetworksExpansion
            Plugin netex = Bukkit.getPluginManager().getPlugin("Networks");
            if (netex != null && netex.isEnabled()) {
                // Check if NetworksExpansion is in affected versions
                if (netex.getPluginMeta().getPluginSoftDependencies().contains(JustEnoughGuide.getInstance().getName())) {
                    Debug.warn("Potential dependency cycle detected: Logitech -> Networks -> JustEnoughGuide -> " +
                                       "Logitech");
                    Debug.warn("1. This may cause SpecialMenuProvider module load incorrectly, which will break the " +
                                       "Big Recipe module");
                    Debug.warn("2. This may cause incorrect load order where Logitech loads AFTER Networks and " +
                                       "InfinityExpansion");
                    Debug.warn("Consequences of incorrect load order:");
                    Debug.warn("- Logitech will fail to load DependencyNetwork module");
                    Debug.warn("- Logitech will fail to load DependencyInfinity module");
                    Debug.warn("Impact:");
                    Debug.warn("- Without DependencyNetwork: Network Quantum Storage won't be recognized, and " +
                                       "Networks Fast Machine won't load (may break some Logitech machines)");
                    Debug.warn("- Without DependencyInfinity: Infinity Fast Machines won't load (may break some " +
                                       "Logitech machines)");
                    Debug.warn("Solution: Update NetworksExpansion to the latest version");
                }
            }
        }
    }
}
