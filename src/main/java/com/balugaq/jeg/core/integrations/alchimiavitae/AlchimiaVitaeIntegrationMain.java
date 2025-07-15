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

package com.balugaq.jeg.core.integrations.alchimiavitae;

import com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry;
import com.balugaq.jeg.core.integrations.Integration;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
public class AlchimiaVitaeIntegrationMain implements Integration {
    public static final int[] AV_CRAFTER_INPUT_SLOTS = new int[]{
            0, 1, 2,
            9, 10, 11,
            18, 19, 20
    };
    public static final List<SlimefunItem> handledSlimefunItems = new ArrayList<>();

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
        return "AlchimiaVitae";
    }

    @Override
    public void onEnable() {
        rrc("AV_DIVINE_ALTAR", AV_CRAFTER_INPUT_SLOTS, false);
        rrc("AV_ORNATE_CAULDRON", AV_CRAFTER_INPUT_SLOTS, false);
        rrc("AV_ALTAR_OF_INFUSION", AV_CRAFTER_INPUT_SLOTS, false);
    }

    @Override
    public void onDisable() {
        for (SlimefunItem slimefunItem : handledSlimefunItems) {
            RecipeCompletableRegistry.unregisterRecipeCompletable(slimefunItem);
        }
    }
}
