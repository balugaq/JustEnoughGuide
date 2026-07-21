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

package com.balugaq.jeg.core.integrations.magicexpansion;

import com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry;
import com.balugaq.jeg.core.integrations.Integration;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@NullMarked
public class MagicExpansionIntegrationMain implements Integration {
    public static final int[] QUICK_MACHINE_INPUT_SLOTS = new int[]{
        1, 2, 3, 4, 5, 6, 7, 8,
        9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
    };
    public static final List<SlimefunItem> handledSlimefunItems = new ArrayList<>();

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
    public String getHookPlugin() {
        return "magicexpansion";
    }

    @Override
    public void onEnable() {
        rrc("MAGIC_EXPANSION_QUICK_ENHANCED_CRAFTING_TABLE_BV", QUICK_MACHINE_INPUT_SLOTS, true);
        rrc("MAGIC_EXPANSION_QUICK_SMELTERY_BV", QUICK_MACHINE_INPUT_SLOTS, true);
        rrc("MAGIC_EXPANSION_QUICK_GRIND_STONE_BV", QUICK_MACHINE_INPUT_SLOTS, true);
        rrc("MAGIC_EXPANSION_QUICK_ORE_CRUSHER_BV", QUICK_MACHINE_INPUT_SLOTS, true);
        rrc("MAGIC_EXPANSION_QUICK_COMPRESSOR_BV", QUICK_MACHINE_INPUT_SLOTS, true);
        rrc("MAGIC_EXPANSION_QUICK_ARMOR_FORGE_BV", QUICK_MACHINE_INPUT_SLOTS, true);
        rrc("MAGIC_EXPANSION_QUICK_PRESSURE_CHAMBER_BV", QUICK_MACHINE_INPUT_SLOTS, true);
        rrc("MAGIC_EXPANSION_QUICK_MAGIC_WORKBENCH_BV", QUICK_MACHINE_INPUT_SLOTS, true);
        rrc("MAGIC_EXPANSION_QUICK_AUTOMATED_PANNING_MACHINE_BV", QUICK_MACHINE_INPUT_SLOTS, true);
        rrc("MAGIC_EXPANSION_QUICK_AUTOMATED_ANCIENT_ALTAR_BV", QUICK_MACHINE_INPUT_SLOTS, true);
        rrc("MAGIC_EXPANSION_QUICK_ELECTRIC_ORE_GRINDER_BV", QUICK_MACHINE_INPUT_SLOTS, true);
        rrc("MAGIC_EXPANSION_QUICK_HEATED_PRESSURE_CHAMBER_BV", QUICK_MACHINE_INPUT_SLOTS, true);
    }

    @Override
    public void onDisable() {
        for (SlimefunItem slimefunItem : handledSlimefunItems) {
            RecipeCompletableRegistry.unregisterRecipeCompletable(slimefunItem);
        }
    }
}
