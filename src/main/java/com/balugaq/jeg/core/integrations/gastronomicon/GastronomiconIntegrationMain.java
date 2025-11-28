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
    public static final int[] BASIC_MACHINE_INPUT_SLOTS = new int[] {10, 11, 12, 19, 20, 21, 28, 29, 30};
    public static final int[] ELECTRIC_KITCHEN_INPUT_SLOTS = new int[] {10, 11, 12, 19, 20, 21, 28, 29, 30, 37, 38, 39};
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
