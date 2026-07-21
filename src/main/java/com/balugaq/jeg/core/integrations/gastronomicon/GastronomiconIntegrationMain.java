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

package com.balugaq.jeg.core.integrations.gastronomicon;

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
public class GastronomiconIntegrationMain implements Integration {
    public static final int[] BASIC_MACHINE_INPUT_SLOTS = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30};
    public static final int[] ELECTRIC_KITCHEN_INPUT_SLOTS = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30, 37, 38, 39};
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
        return "Gastronomicon";
    }

    @Override
    public void onEnable() {
        rrc("GN_CULINARY_WORKBENCH", BASIC_MACHINE_INPUT_SLOTS, false);
        rrc("GN_MULTI_STOVE", BASIC_MACHINE_INPUT_SLOTS, false);
        rrc("GN_REFRIDGERATOR", BASIC_MACHINE_INPUT_SLOTS, false);
        rrc("GN_MILL", BASIC_MACHINE_INPUT_SLOTS, false);
        rrc("GN_FERMENTER", BASIC_MACHINE_INPUT_SLOTS, false);
        rrc("GN_LARGE_FERMENTER", BASIC_MACHINE_INPUT_SLOTS, false);
        rrc("GN_ELECTRIC_KITCHEN_I", ELECTRIC_KITCHEN_INPUT_SLOTS, false);
        rrc("GN_ELECTRIC_KITCHEN_II", ELECTRIC_KITCHEN_INPUT_SLOTS, false);
        rrc("GN_ELECTRIC_KITCHEN_III", ELECTRIC_KITCHEN_INPUT_SLOTS, false);
    }

    @Override
    public void onDisable() {
        for (SlimefunItem slimefunItem : handledSlimefunItems) {
            RecipeCompletableRegistry.unregisterRecipeCompletable(slimefunItem);
        }
    }
}
