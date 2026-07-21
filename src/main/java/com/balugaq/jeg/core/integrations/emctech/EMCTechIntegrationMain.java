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

package com.balugaq.jeg.core.integrations.emctech;

import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import io.github.sefiraat.emctech.utils.EmcUtils;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.9
 */
@NullMarked
public class EMCTechIntegrationMain implements Integration {
    /**
     * Get EMC value of itemStack
     *
     * @param itemStack The itemStack
     * @return 0.0D if no EMC value, or EMC value
     */
    @Range(from = 0, to = Long.MAX_VALUE)
    public static double getEMCInDematerializer(ItemStack itemStack) {
        return EmcUtils.getEmcValue(itemStack);
    }

    /**
     * Get EMC value of itemStack
     *
     * @param itemStack The itemStack
     * @return 0.0D if no EMC value, or EMC value
     */
    @Range(from = 0, to = Long.MAX_VALUE)
    public static double getEMCInMaterializer(ItemStack itemStack) {
        return EmcUtils.getEmcValueMultiplied(itemStack);
    }

    @Override
    public String getHookPlugin() {
        return "EMCTech";
    }

    @Override
    public void onEnable() {
        if (JustEnoughGuide.getConfigManager().isEMCValueDisplay()) {
            SlimefunGuideSettings.addOption(EMCValueDisplayGuideOption.instance());
            JustEnoughGuide.getListenerManager().registerListener(new EMCItemPatchListener());
        }
    }

    @Override
    public void onDisable() {
    }
}
