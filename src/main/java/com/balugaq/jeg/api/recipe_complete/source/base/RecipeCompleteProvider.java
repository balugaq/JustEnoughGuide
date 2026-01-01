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

package com.balugaq.jeg.api.recipe_complete.source.base;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
@Getter
@NullMarked
public class RecipeCompleteProvider {
    @Getter
    private static final List<SlimefunSource> slimefunSources = new ArrayList<>();

    @Getter
    private static final List<VanillaSource> vanillaSources = new ArrayList<>();

    public static void addSource(SlimefunSource source) {
        if (JustEnoughGuide.getConfigManager().isRecipeComplete()) {
            slimefunSources.add(source);
        }
    }

    public static void addSource(VanillaSource source) {
        if (JustEnoughGuide.getConfigManager().isRecipeComplete()) {
            vanillaSources.add(source);
        }
    }

    @Nullable
    public static SlimefunSource removeSlimefunSource(SlimefunSource source) {
        return slimefunSources.remove(source) ? source : null;
    }

    @Nullable
    public static SlimefunSource removeSlimefunSource(JavaPlugin plugin) {
        for (SlimefunSource source : slimefunSources) {
            if (source.plugin().equals(plugin)) {
                return slimefunSources.remove(source) ? source : null;
            }
        }
        return null;
    }

    @Nullable
    public static VanillaSource removeVanillaSource(VanillaSource source) {
        return vanillaSources.remove(source) ? source : null;
    }

    @Nullable
    public static VanillaSource removeVanillaSource(JavaPlugin plugin) {
        for (VanillaSource source : vanillaSources) {
            if (source.plugin().equals(plugin)) {
                return vanillaSources.remove(source) ? source : null;
            }
        }
        return null;
    }

    public static void shutdown() {
        slimefunSources.clear();
        vanillaSources.clear();
    }
}
