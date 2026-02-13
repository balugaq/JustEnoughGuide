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

package com.balugaq.jeg.core.integrations.momotech;

import com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry;
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.option.AbstractItemSettingsGuideOption;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"DataFlowIssue", "ConstantValue", "unused"})
@NullMarked
public class MomotechIntegrationMain implements Integration {
    // @formatter:off
    public static final int[] CREATIVE_ITEM_GENERATOR_RECIPE_SLOTS = new int[]{
            0,  1,  2,  3,  4,  5,  6,  7,  8,
            9,  10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26
    };
    public static final int[] QUANTITY_CONSTRUCTOR_RECIPE_SLOTS = new int[]{
            0,  1,  2,  3,  4,  5,  6,  7,  8,
            9,  10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44
    };
    public static final int[] NONE_GENERATOR_RECIPE_SLOTS = new int[]{
            0, 1, 2, 3, 4, 5, 6, 7, 8
    };
    public static final int[] MOMOTECH_CREATIVE_AMOUNTS = new int[]{
            9, 8, 7, 6, 5, 4, 3, 2, 1,
            9, 8, 7, 6, 5, 4, 3, 2, 1,
            9, 8, 7, 6, 5, 4, 3, 2, 1
    };
    public static final int[] MOMOTECH_CREATIVE_I_AMOUNTS = new int[]{
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            1, 2, 3, 4, 5, 6, 7, 8, 9
    };
    public static final int[] MOMOTECH_NONE_AMOUNTS = new int[]{
            1, 2, 3, 4, 5, 6, 7, 8, 9
    };
    public static final int[] MAX_AMOUNTS = new int[]{
            64, 64, 64, 64, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64, 64
    };
    // @formatter:on
    public static final List<SlimefunItem> handledSlimefunItems = new ArrayList<>();
    public static final ItemStack UNCONTROLLABLE_EMPTY;
    public static final ItemStack RESOURCE;
    public static final ItemStack FINAL_ITEM;
    public static final ItemStack COMMAND_BLOCK = new ItemStack(Material.COMMAND_BLOCK);
    public static final ItemStack REPEATING_COMMAND_BLOCK = new ItemStack(Material.REPEATING_COMMAND_BLOCK);
    public static final ItemStack CHAIN_COMMAND_BLOCK = new ItemStack(Material.CHAIN_COMMAND_BLOCK);
    public static JavaPlugin plugin = null;

    static {
        // @formatter:off
        var sf1              = SlimefunItem.getById("MOMOTECH_UNCONTROLLABLE_EMPTY_");
        UNCONTROLLABLE_EMPTY = sf1 == null ? null : sf1.getItem();
        var sf2              = SlimefunItem.getById("MOMOTECH_RESOURCE");
        RESOURCE             = sf2 == null ? null : sf2.getItem();
        var sf3              = SlimefunItem.getById("MOMOTECH_FINAL_ITEM");
        FINAL_ITEM           = sf3 == null ? null : sf3.getItem();
        // @formatter:on
    }

    public static JavaPlugin getPlugin() {
        if (plugin == null) {
            plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Momotech");
        }

        if (plugin == null) {
            plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Momotech-Changed");
        }

        return plugin;
    }

    @Override
    public String getHookPlugin() {
        return "Momotech";
    }

    @Override
    public void onEnable() {
        SlimefunGuideSettings.addOption(MomotechCreativeItemRecipeSettingsGuideOption.instance());
        SlimefunGuideSettings.addOption(MomotechNoneRecipeSettingsGuideOption.instance());
        SlimefunGuideSettings.addOption(MomotechQuantityItemRecipeSettingsGuideOption.instance());
        var UEC = AbstractItemSettingsGuideOption.generateChoices(UNCONTROLLABLE_EMPTY, MAX_AMOUNTS);
        var RC = AbstractItemSettingsGuideOption.generateChoices(RESOURCE, MAX_AMOUNTS);
        var FC = AbstractItemSettingsGuideOption.generateChoices(FINAL_ITEM, MAX_AMOUNTS);
        var CC1 = AbstractItemSettingsGuideOption.generateChoices(COMMAND_BLOCK, MAX_AMOUNTS);
        var CC2 = AbstractItemSettingsGuideOption.generateChoices(REPEATING_COMMAND_BLOCK, MAX_AMOUNTS);
        var CC3 = AbstractItemSettingsGuideOption.generateChoices(CHAIN_COMMAND_BLOCK, MAX_AMOUNTS);
        RecipeCompleteProvider.registerSpecialRecipeHandler((p, i, s) -> {
            if (s == null) return null;

            return switch (s.getId()) {
                case "MOMOTECH_CREATIVE" ->
                        AbstractItemSettingsGuideOption.generateChoices(MomotechCreativeItemRecipeSettingsGuideOption.getItem(p), MOMOTECH_CREATIVE_AMOUNTS);
                case "MOMOTECH_CREATIVE_I" ->
                        AbstractItemSettingsGuideOption.generateChoices(MomotechCreativeItemRecipeSettingsGuideOption.getItem(p), MOMOTECH_CREATIVE_I_AMOUNTS);
                case "MOMOTECH_NONE" ->
                        AbstractItemSettingsGuideOption.generateChoices(MomotechNoneRecipeSettingsGuideOption.getItems(p), MOMOTECH_NONE_AMOUNTS);
                case "MOMOTECH_QUANTITY_ITEM" ->
                        AbstractItemSettingsGuideOption.generateChoices(MomotechQuantityItemRecipeSettingsGuideOption.getItem(p), MAX_AMOUNTS);
                case "MOMOTECH_CREATIVE_II" -> UEC;
                case "MOMOTECH_FINAL_RULE" -> RC;
                case "MOMOTECH_FINAL_STAR" -> FC;
                case "MOMOTECH_COMMAND_BLOCK1" -> CC1;
                case "MOMOTECH_COMMAND_BLOCK2" -> CC2;
                case "MOMOTECH_COMMAND_BLOCK3" -> CC3;
                default -> null;
            };
        });

        rrc("MOMOTECH_CREATIVE_ITEM_GENERATOR", CREATIVE_ITEM_GENERATOR_RECIPE_SLOTS, false);
        rrc("MOMOTECH_NONE_GENERATOR", NONE_GENERATOR_RECIPE_SLOTS, false);
        rrc("MOMOTECH_QUANTITY_CONSTRUCTOR", QUANTITY_CONSTRUCTOR_RECIPE_SLOTS, true);

        JustEnoughGuide.getListenerManager().registerListener(new MomotechCreativeItemRecipeCompletePrecheckListener());
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
