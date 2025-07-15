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

package com.balugaq.jeg.api.recipe_complete;

import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
@ApiStatus.Obsolete
public class RecipeCompletableRegistry {
    /**
     * @param slimefunItem the {@link SlimefunItem} to add
     * @see RecipeCompletableListener.NotApplicable
     */
    @ApiStatus.Obsolete
    public static void addNotApplicableItem(@NotNull SlimefunItem slimefunItem) {
        RecipeCompletableListener.addNotApplicableItem(slimefunItem);
    }

    /**
     * @param slimefunItem the {@link SlimefunItem} to remove
     * @see RecipeCompletableListener.NotApplicable
     */
    @ApiStatus.Obsolete
    public static void removeNotApplicableItem(@NotNull SlimefunItem slimefunItem) {
        RecipeCompletableListener.removeNotApplicableItem(slimefunItem);
    }

    @ApiStatus.Obsolete
    public static void registerRecipeCompletable(
            @NotNull SlimefunItem slimefunItem, @Range(from = 0, to = 53) int @NotNull [] slots) {
        RecipeCompletableListener.registerRecipeCompletable(slimefunItem, slots);
    }

    @ApiStatus.Obsolete
    public static void registerRecipeCompletable(
            @NotNull SlimefunItem slimefunItem, @Range(from = 0, to = 53) int @NotNull [] slots, boolean unordered) {
        RecipeCompletableListener.registerRecipeCompletable(slimefunItem, slots, unordered);
    }

    @ApiStatus.Obsolete
    public static void unregisterRecipeCompletable(@NotNull SlimefunItem slimefunItem) {
        RecipeCompletableListener.unregisterRecipeCompletable(slimefunItem);
    }
}
