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

/**
 * @author balugaq
 * @since 1.9
 */
package com.balugaq.jeg.core.integrations.logitech;

import com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry;
import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
public class LogitechIntegrationMain implements Integration {
    public static final int[] MANUAL_CRAFTER_INPUT_SLOTS = new int[]{
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
            33, 34, 35, 36, 37, 38, 42, 43, 44, 45, 46, 47, 51, 52, 53
    };
    public static final int[] BUG_CRAFTER_INPUT_SLOTS = new int[]{
            0, 1, 2, 3, 4, 5,
            9, 10, 11, 12, 13, 14,
            18, 19, 20, 21, 22, 23,
            27, 28, 29, 30, 31, 32,
            36, 37, 38, 39, 40, 41,
            45, 46, 47, 48, 49, 50
    };
    public static final List<SlimefunItem> handledSlimefunItems = new ArrayList<>();

    public static void rrc(@NotNull SlimefunItem slimefunItem, int @NotNull [] slots) {
        handledSlimefunItems.add(slimefunItem);
        RecipeCompletableRegistry.registerRecipeCompletable(slimefunItem, slots, true);
    }

    public static void rrc(@NotNull String id, int @NotNull [] slots, boolean unordered) {
        SlimefunItem slimefunItem = SlimefunItem.getById(id);
        if (slimefunItem != null) {
            rrc(slimefunItem, slots, unordered);
        }
    }

    public static void rrc(@NotNull SlimefunItem slimefunItem, int @NotNull [] slots, boolean unordered) {
        handledSlimefunItems.add(slimefunItem);
        RecipeCompletableRegistry.registerRecipeCompletable(slimefunItem, slots, unordered);
    }

    @Override
    public @NotNull String getHookPlugin() {
        return "Logitech";
    }

    @Override
    public void onEnable() {
        try {
            // LogiTech v1.0.4
            Class.forName("me.matl114.logitech.core.AddSlimefunItems");
            rrc(me.matl114.logitech.core.AddSlimefunItems.CRAFT_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            rrc(me.matl114.logitech.core.AddSlimefunItems.ADV_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            rrc(me.matl114.logitech.core.AddSlimefunItems.CRUCIBLE_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            rrc(me.matl114.logitech.core.AddSlimefunItems.ANCIENT_ALTAR_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            rrc(me.matl114.logitech.core.AddSlimefunItems.ARMOR_FORGE_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            rrc(me.matl114.logitech.core.AddSlimefunItems.COMPRESSOR_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            rrc(me.matl114.logitech.core.AddSlimefunItems.ENHANCED_CRAFT_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            rrc(me.matl114.logitech.core.AddSlimefunItems.FURNACE_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            rrc(me.matl114.logitech.core.AddSlimefunItems.GOLD_PAN_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            rrc(me.matl114.logitech.core.AddSlimefunItems.GRIND_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            rrc(me.matl114.logitech.core.AddSlimefunItems.MAGIC_WORKBENCH_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            rrc(me.matl114.logitech.core.AddSlimefunItems.ORE_CRUSHER_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            rrc(me.matl114.logitech.core.AddSlimefunItems.ORE_WASHER_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            rrc(me.matl114.logitech.core.AddSlimefunItems.PRESSURE_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            rrc(me.matl114.logitech.core.AddSlimefunItems.SMELTERY_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            rrc(me.matl114.logitech.core.AddSlimefunItems.TABLESAW_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            if (JustEnoughGuide.getIntegrationManager().isEnabledInfinityExpansion()) {
                try {
                    rrc(me.matl114.logitech.core.Registries.AddDepends.MOBDATA_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                } catch (Throwable ignored) {
                }
                try {
                    rrc(me.matl114.logitech.core.Registries.AddDepends.INFINITY_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                } catch (Throwable ignored) {
                }
            }
            if (JustEnoughGuide.getIntegrationManager().isEnabledNetworks()) {
                try {
                    rrc(me.matl114.logitech.core.Registries.AddDepends.NTWWORKBENCH_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                } catch (Throwable ignored) {
                }
            }
        } catch (ClassNotFoundException ignored) {
            // LogiTech v1.0.3
            try {
                Class.forName("me.matl114.logitech.SlimefunItem.AddSlimefunItems");
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.CRAFT_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.ADV_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.CRUCIBLE_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.ANCIENT_ALTAR_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.ARMOR_FORGE_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.COMPRESSOR_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(
                        me.matl114.logitech.SlimefunItem.AddSlimefunItems.ENHANCED_CRAFT_MANUAL,
                        MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.FURNACE_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.GOLD_PAN_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.GRIND_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(
                        me.matl114.logitech.SlimefunItem.AddSlimefunItems.MAGIC_WORKBENCH_MANUAL,
                        MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.ORE_CRUSHER_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.ORE_WASHER_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.PRESSURE_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.SMELTERY_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.TABLESAW_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                if (JustEnoughGuide.getIntegrationManager().isEnabledInfinityExpansion()) {
                    try {
                        rrc(me.matl114.logitech.SlimefunItem.AddDepends.MOBDATA_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                    } catch (Throwable ignored2) {
                    }
                    try {
                        rrc(me.matl114.logitech.SlimefunItem.AddDepends.INFINITY_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                    } catch (Throwable ignored2) {
                    }
                }
                if (JustEnoughGuide.getIntegrationManager().isEnabledNetworks()) {
                    try {
                        rrc(
                                me.matl114.logitech.SlimefunItem.AddDepends.NTWWORKBENCH_MANUAL,
                                MANUAL_CRAFTER_INPUT_SLOTS);
                    } catch (Throwable ignored2) {
                    }
                }
            } catch (ClassNotFoundException ignored2) {
            }
        }

        rrc("LOGITECH_BUG_CRAFTER", BUG_CRAFTER_INPUT_SLOTS, false);
    }

    @Override
    public void onDisable() {
        for (SlimefunItem slimefunItem : handledSlimefunItems) {
            RecipeCompletableRegistry.unregisterRecipeCompletable(slimefunItem);
        }
    }
}
