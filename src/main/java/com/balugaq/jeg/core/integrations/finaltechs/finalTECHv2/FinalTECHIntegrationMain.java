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

package com.balugaq.jeg.core.integrations.finaltechs.finalTECHv2;

import com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry;
import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.core.integrations.finaltechs.finalTECHCommon.FinalTECHItemPatchListener;
import com.balugaq.jeg.core.integrations.finaltechs.finalTECHCommon.FinalTECHValueDisplayOption;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@NullMarked
public class FinalTECHIntegrationMain implements Integration {
    public static final int[] MATRIX_CRAFTING_TABLE_INPUT_SLOTS = new int[] {
            0, 1, 2, 3, 4, 5,
            9, 10, 11, 12, 13, 14,
            18, 19, 20, 21, 22, 23,
            27, 28, 29, 30, 31, 32,
            36, 37, 38, 39, 40, 41,
            45, 46, 47, 48, 49, 50
    };
    public static final int[] MANUAL_CRAFTER_INPUT_SLOTS = new int[] {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28,
            29,
            30, 31, 32, 33, 34, 35
    };
    public static final List<SlimefunItem> handledSlimefunItems = new ArrayList<>();

    @Override
    public String getHookPlugin() {
        return "FinalTECH";
    }

    @Override
    public void onEnable() {
        if (JustEnoughGuide.getConfigManager().isFinalTECHValueDisplay()) {
            if (!FinalTECHValueDisplayOption.booted()) {
                FinalTECHValueDisplayOption.boot();
                SlimefunGuideSettings.addOption(FinalTECHValueDisplayOption.instance());
                JustEnoughGuide.getListenerManager().registerListener(new FinalTECHItemPatchListener());
            }
        }

        rrc("FINALTECH_MANUAL_CRAFT_MACHINE");
        rrc("FINALTECH_MANUAL_CRAFTING_TABLE");
        rrc("FINALTECH_MANUAL_ENHANCED_CRAFTING_TABLE");
        rrc("FINALTECH_MANUAL_GRIND_STONE");
        rrc("FINALTECH_MANUAL_ARMOR_FORGE");
        rrc("FINALTECH_MANUAL_ORE_CRUSHER");
        rrc("FINALTECH_MANUAL_COMPRESSOR");
        rrc("FINALTECH_MANUAL_SMELTERY");
        rrc("FINALTECH_MANUAL_PRESSURE_CHAMBER");
        rrc("FINALTECH_MANUAL_MAGIC_WORKBENCH");
        rrc("FINALTECH_MANUAL_ORE_WASHER");
        rrc("FINALTECH_MANUAL_COMPOSTER");
        rrc("FINALTECH_MANUAL_GOLD_PAN");
        rrc("FINALTECH_CRUCIBLE");
        rrc("FINALTECH_MANUAL_JUICER");
        rrc("FINALTECH_MANUAL_ANCIENT_ALTAR");
        rrc("FINALTECH_MANUAL_HEATED_PRESSURE_CHAMBER");
        rrc("FINALTECH_MATRIX_CRAFTING_TABLE", MATRIX_CRAFTING_TABLE_INPUT_SLOTS, false);
    }

    public static void rrc(String id) {
        SlimefunItem slimefunItem = SlimefunItem.getById(id);
        if (slimefunItem != null) {
            rrc(slimefunItem, MANUAL_CRAFTER_INPUT_SLOTS);
        }
    }

    public static void rrc(String id, int[] slots, boolean unordered) {
        SlimefunItem slimefunItem = SlimefunItem.getById(id);
        if (slimefunItem != null) {
            rrc(slimefunItem, slots, unordered);
        }
    }

    public static void rrc(SlimefunItem slimefunItem, int[] slots) {
        handledSlimefunItems.add(slimefunItem);
        RecipeCompletableRegistry.registerRecipeCompletable(slimefunItem, slots, true);
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
