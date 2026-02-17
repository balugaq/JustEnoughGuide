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

package com.balugaq.jeg.core.integrations.rykenslimefuncustomizer;

import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry;
import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"unchecked", "unused"})
@NullMarked
public class RykenSlimefunCustomizerIntegrationMain implements Integration {
    // @formatter:off
    private static final int[] FVV_FAST_DEVINE_ALTAR_SLOTS = new int[] {
            0,  1,  2,  3,  4,  5,  6,  7,  8,
            9,  10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29,             33, 34, 35,
            36, 37, 38,             42, 43, 44,
            45, 46, 47,             51, 52, 53
    };
    private static final int[] FVV_FAST_LIQUEFACTION_BASIN_SLOTS = new int[] {
            0,  1,  2,  3,  4,  5,  6,  7,  8,
            9,  10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29,             33, 34, 35,
            36, 37, 38,             42, 43, 44,
            45, 46, 47,             51, 52, 53
    };
    // @formatter:on
    public static final List<SlimefunItem> handledSlimefunItems = new ArrayList<>();
    public static @Nullable Class<? extends SlimefunItem> classCustomWorkbench = null;
    public static @Nullable Class<? extends SlimefunItem> classCustomLinkedRecipeMachine = null;
    public static @Nullable Class<? extends SlimefunItem> classCustomRecipeMachine = null;

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
        return "RykenSlimefunCustomizer";
    }

    @Override
    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    public void onEnable() {
        try {
            classCustomWorkbench = (Class<? extends SlimefunItem>)
                    Class.forName("org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomWorkbench");
        } catch (Exception ignored) {
            classCustomWorkbench = null;
        }
        try {
            classCustomLinkedRecipeMachine = (Class<? extends SlimefunItem>) Class.forName(
                    "org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomLinkedRecipeMachine");
        } catch (Exception ignored) {
            classCustomLinkedRecipeMachine = null;
        }
        try {
            classCustomRecipeMachine = (Class<? extends SlimefunItem>) Class.forName(
                    "org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomRecipeMachine");
        } catch (Exception e) {
            classCustomRecipeMachine = null;
        }

        if (classCustomWorkbench == null && classCustomLinkedRecipeMachine == null && classCustomRecipeMachine == null) {
            return;
        }

        for (SlimefunItem sf : new ArrayList<>(Slimefun.getRegistry().getAllSlimefunItems())) {
            Class<? extends SlimefunItem> clazz = sf.getClass();
            if (clazz != classCustomWorkbench && clazz != classCustomLinkedRecipeMachine && clazz != classCustomRecipeMachine) {
                continue;
            }

            int[] input;
            try {
                input = (int[]) ReflectionUtil.getValue(sf, "input");
            } catch (Throwable ignored) {
                continue;
            }
            if (input == null) {
                continue;
            }

            rrc(sf, input, clazz == classCustomRecipeMachine);
        }

        rrc("FVV_FAST_DEVINE_ALTAR", FVV_FAST_DEVINE_ALTAR_SLOTS, true);
        rrc("FVV_FAST_LIQUEFACTION_BASIN", FVV_FAST_LIQUEFACTION_BASIN_SLOTS, true);
    }

    @Override
    public void onDisable() {
        for (SlimefunItem slimefunItem : handledSlimefunItems) {
            RecipeCompletableRegistry.unregisterRecipeCompletable(slimefunItem);
        }
    }
}
