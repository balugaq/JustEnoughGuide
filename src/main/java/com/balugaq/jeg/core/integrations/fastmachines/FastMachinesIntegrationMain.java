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

package com.balugaq.jeg.core.integrations.fastmachines;

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
public class FastMachinesIntegrationMain implements Integration {
    public static final int[] MANUAL_CRAFTER_INPUT_SLOTS = new int[]{
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
    };
    public static final List<SlimefunItem> handledSlimefunItems = new ArrayList<>();

    public static void rrc(@NotNull String id) {
        SlimefunItem slimefunItem = SlimefunItem.getById(id);
        if (slimefunItem != null) {
            rrc(slimefunItem, MANUAL_CRAFTER_INPUT_SLOTS);
        }
    }

    public static void rrc(@NotNull SlimefunItem slimefunItem, int @NotNull [] slots) {
        handledSlimefunItems.add(slimefunItem);
        RecipeCompletableRegistry.registerRecipeCompletable(slimefunItem, slots, true);
    }

    @Override
    public @NotNull String getHookPlugin() {
        return "FastMachines";
    }

    @Override
    public void onEnable() {
        rrc("FM_FAST_CRAFTING_TABLE");
        rrc("FM_FAST_FURNACE");
        rrc("FM_FAST_ENHANCED_CRAFTING_TABLE");
        rrc("FM_FAST_GRIND_STONE");
        rrc("FM_FAST_ARMOR_FORGE");
        rrc("FM_FAST_ORE_CRUSHER");
        rrc("FM_FAST_COMPRESSOR");
        rrc("FM_FAST_SMELTERY");
        rrc("FM_FAST_PRESSURE_CHAMBER");
        rrc("FM_FAST_MAGIC_WORKBENCH");
        rrc("FM_FAST_ORE_WASHER");
        rrc("FM_FAST_TABLE_SAW");
        rrc("FM_FAST_COMPOSTER");
        rrc("FM_FAST_PANNING_MACHINE");
        rrc("FM_FAST_JUICER");
        rrc("FM_FAST_ANCIENT_ALTAR");
        if (JustEnoughGuide.getIntegrationManager().isEnabledInfinityExpansion()) {
            rrc("FM_FAST_INFINITY_WORKBENCH");
            rrc("FM_FAST_MOB_DATA_INFUSER");
        }
        if (JustEnoughGuide.getIntegrationManager().isEnabledSlimeFrame()) {
            rrc("FM_FAST_SLIMEFRAME_FOUNDRY");
        }
        if (JustEnoughGuide.getIntegrationManager().isEnabledInfinityExpansion2()) {
            rrc("FM_FAST_INFINITY_WORKBENCH_2");
            rrc("FM_FAST_MOB_DATA_INFUSER_2");
        }
    }

    @Override
    public void onDisable() {
        for (SlimefunItem slimefunItem : handledSlimefunItems) {
            RecipeCompletableRegistry.unregisterRecipeCompletable(slimefunItem);
        }
    }
}
