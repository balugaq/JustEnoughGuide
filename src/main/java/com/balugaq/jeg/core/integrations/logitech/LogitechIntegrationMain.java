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

/**
 * @author balugaq
 * @since 1.9
 */
package com.balugaq.jeg.core.integrations.logitech;

import com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry;
import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.ItemStackUtil;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetProvider;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("DataFlowIssue")
@NullMarked
public class LogitechIntegrationMain implements Integration {
    // @formatter:off
    public static final int[] MANUAL_CRAFTER_INPUT_SLOTS = new int[] {
            0,  1,  2,  3,  4,  5,  6,  7,  8,
            9,  10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29,             33, 34, 35,
            36, 37, 38,             42, 43, 44,
            45, 46, 47,             51, 52, 53
    };
    public static final int[] BUG_CRAFTER_INPUT_SLOTS = new int[] {
            0,  1,  2,  3,  4,  5,
            9,  10, 11, 12, 13, 14,
            18, 19, 20, 21, 22, 23,
            27, 28, 29, 30, 31, 32,
            36, 37, 38, 39, 40, 41,
            45, 46, 47, 48, 49, 50
    };
    public static final int[] CARD_MAKER_INPUT_SLOTS = new int[] {
            11, 12, 13,
            20, 21, 22,
            29, 30, 31
    };
    public static final int[] STAR_SMELTERY_SLOTS = new int[] {
            0,  1,
            9,  10,
            18, 19,
            27, 28,
            36, 37,
            45, 46
    };
    public static final int[] STACKMACHINE_SLOTS = new int[] {
            0,  1,  2,
            9,  10, 11,
            18, 19, 20,
            27, 28, 29,
            36, 37, 38,
            45, 46, 47
    };
    public static final int[] MULTIBLOCK_MANUAL_SLOTS = new int[] {
            4,  5,  6,  7,  8,
            13, 14, 15, 16, 17,
            22, 23, 24, 25, 26,
            31, 32, 33, 34, 35,
            40, 41, 42, 43, 44,
            49, 50, 51, 52, 53
    };
    // @formatter:on
    public static final List<SlimefunItem> handledSlimefunItems = new ArrayList<>();

    public static final Set<SlimefunItem> stackableMachines = new HashSet<>();
    public static final Set<SlimefunItem> stackableMaterialGenerators = new HashSet<>();

    public static @Nullable JavaPlugin plugin = null;

    public static JavaPlugin getPlugin() {
        if (plugin == null) {
            plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Logitech");
        }

        return plugin;
    }

    public static boolean isMachineStackable(SlimefunItem sf) {
        return stackableMachines.contains(sf);
    }

    public static boolean isGeneratorStackable(SlimefunItem sf) {
        var className = sf.getClass().getName();
        return sf instanceof EnergyNetProvider
                && !"me.matl114.logitech.core.Machines.Electrics.EnergyAmplifier".equals(className)
                && !"me.matl114.logitech.SlimefunItem.Machines.Electrics.EnergyAmplifier".equals(className)
                && !ItemStackUtil.isInstance(sf, "me.matl114.logitech.SlimefunItem.Machines.Electrics.AbstractEnergyMachine")
                && !ItemStackUtil.isInstance(sf, "me.matl114.logitech.core.Machines.Abstracts.AbstractEnergyMachine");
    }

    public static boolean isMaterialGeneratorStackable(SlimefunItem sf) {
        return stackableMaterialGenerators.contains(sf);
    }

    @Override
    public String getHookPlugin() {
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
            rrc(me.matl114.logitech.core.AddSlimefunItems.MULTICRAFTTABLE_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
            if (JustEnoughGuide.getIntegrationManager().isEnabledInfinityExpansion()) {
                try {
                    rrc(me.matl114.logitech.core.Registries.AddDepends.MOBDATA_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                } catch (Exception ignored) {
                }
                try {
                    rrc(me.matl114.logitech.core.Registries.AddDepends.INFINITY_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                } catch (Exception ignored) {
                }
            }
            if (JustEnoughGuide.getIntegrationManager().isEnabledNetworks()) {
                try {
                    rrc(me.matl114.logitech.core.Registries.AddDepends.NTWWORKBENCH_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                } catch (Exception ignored) {
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
                        MANUAL_CRAFTER_INPUT_SLOTS
                );
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.FURNACE_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.GOLD_PAN_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.GRIND_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(
                        me.matl114.logitech.SlimefunItem.AddSlimefunItems.MAGIC_WORKBENCH_MANUAL,
                        MANUAL_CRAFTER_INPUT_SLOTS
                );
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.ORE_CRUSHER_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.ORE_WASHER_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.PRESSURE_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.SMELTERY_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(me.matl114.logitech.SlimefunItem.AddSlimefunItems.TABLESAW_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                rrc(
                        me.matl114.logitech.SlimefunItem.AddSlimefunItems.MULTICRAFTTABLE_MANUAL,
                        MANUAL_CRAFTER_INPUT_SLOTS
                );
                if (JustEnoughGuide.getIntegrationManager().isEnabledInfinityExpansion()) {
                    try {
                        rrc(me.matl114.logitech.SlimefunItem.AddDepends.MOBDATA_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                    } catch (Exception ignored2) {
                    }
                    try {
                        rrc(me.matl114.logitech.SlimefunItem.AddDepends.INFINITY_MANUAL, MANUAL_CRAFTER_INPUT_SLOTS);
                    } catch (Exception ignored2) {
                    }
                }
                if (JustEnoughGuide.getIntegrationManager().isEnabledNetworks()) {
                    try {
                        rrc(
                                me.matl114.logitech.SlimefunItem.AddDepends.NTWWORKBENCH_MANUAL,
                                MANUAL_CRAFTER_INPUT_SLOTS
                        );
                    } catch (Exception ignored2) {
                    }
                }
            } catch (ClassNotFoundException ignored2) {
            }
        }

        rrc("LOGITECH_BUG_CRAFTER", BUG_CRAFTER_INPUT_SLOTS, false);
        rrc("LOGITECH_CARD_MAKER", CARD_MAKER_INPUT_SLOTS, false);
        rrc("LOGITECH_STACKMACHINE", STACKMACHINE_SLOTS, true);
        rrc("LOGITECH_STAR_SMELTERY", STAR_SMELTERY_SLOTS, true);
        rrc("LOGITECH_MULTIBLOCK_MANUAL", MULTIBLOCK_MANUAL_SLOTS, true);

        try {
            // LogiTech v1.0.4
            stackableMachines.addAll(me.matl114.logitech.core.Registries.RecipeSupporter.STACKMACHINE_LIST.keySet());
            stackableMaterialGenerators.addAll(me.matl114.logitech.core.Registries.RecipeSupporter.STACKMGENERATOR_LIST.keySet());
        } catch (Exception ignored) {
            // LogiTech v1.0.3
            try {
                stackableMachines.addAll(me.matl114.logitech.Utils.RecipeSupporter.STACKMACHINE_LIST.keySet());
                stackableMaterialGenerators.addAll(me.matl114.logitech.Utils.RecipeSupporter.STACKMGENERATOR_LIST.keySet());
            } catch (Exception ignored2) {
            }
        }

        if (JustEnoughGuide.getConfigManager().isLogitechMachineStackableDisplay()) {
            SlimefunGuideSettings.addOption(MachineStackableDisplayGuideOption.instance());
            JustEnoughGuide.getListenerManager().registerListener(new LogitechItemPatchListener());
        }

        JustEnoughGuide.getListenerManager().registerListener(new ManualMachineAutoSelectListener());
    }

    public static void rrc(SlimefunItem slimefunItem, int[] slots) {
        handledSlimefunItems.add(slimefunItem);
        RecipeCompletableRegistry.registerRecipeCompletable(slimefunItem, slots, true);
    }

    public static void rrc(String id, int[] slots, boolean unordered) {
        SlimefunItem slimefunItem = SlimefunItem.getById(id);
        if (slimefunItem != null) {
            rrc(slimefunItem, slots, unordered);
        }
    }

    public static void rrc(SlimefunItem slimefunItem, int[] slots, boolean unordered) {
        handledSlimefunItems.add(slimefunItem);
        RecipeCompletableRegistry.registerRecipeCompletable(slimefunItem, slots, unordered);
    }

    @Override
    public void onDisable() {
        for (SlimefunItem slimefunItem : handledSlimefunItems) {
            RecipeCompletableRegistry.unregisterRecipeCompletable(slimefunItem);
        }
    }
}
