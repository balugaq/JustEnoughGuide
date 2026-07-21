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

import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.core.managers.IntegrationManager;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.platform.PlatformUtil;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

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
                        "LogiTech");
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
