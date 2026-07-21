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

package com.balugaq.jeg.api.recipe_complete;

import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Set;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
@ApiStatus.Obsolete
@NullMarked
public class RecipeCompletableRegistry {
    /**
     * @param slimefunItem the {@link SlimefunItem} to add
     * @see RecipeCompletableListener.NotApplicable
     */
    @ApiStatus.Obsolete
    public static void addNotApplicableItem(SlimefunItem slimefunItem) {
        RecipeCompletableListener.addNotApplicableItem(slimefunItem);
    }

    /**
     * @param slimefunItem the {@link SlimefunItem} to remove
     * @see RecipeCompletableListener.NotApplicable
     */
    @ApiStatus.Obsolete
    public static void removeNotApplicableItem(SlimefunItem slimefunItem) {
        RecipeCompletableListener.removeNotApplicableItem(slimefunItem);
    }

    @ApiStatus.Obsolete
    public static void registerRecipeCompletable(
        SlimefunItem slimefunItem, @Range(from = 0, to = 53) int[] slots) {
        RecipeCompletableListener.registerRecipeCompletable(slimefunItem, slots);
    }

    @ApiStatus.Obsolete
    public static void registerRecipeCompletable(
        SlimefunItem slimefunItem, @Range(from = 0, to = 53) int[] slots, boolean unordered) {
        RecipeCompletableListener.registerRecipeCompletable(slimefunItem, slots, unordered);
    }

    @ApiStatus.Obsolete
    public static void unregisterRecipeCompletable(SlimefunItem slimefunItem) {
        RecipeCompletableListener.unregisterRecipeCompletable(slimefunItem);
    }

    @ApiStatus.Obsolete
    public static void registerPlayerInventoryItemGetter(RecipeCompletableListener.PlayerInventoryItemSeeker itemGetter) {
        RecipeCompletableListener.registerPlayerInventoryItemGetter(itemGetter);
    }

    @ApiStatus.Obsolete
    public static void unregisterPlayerInventoryItemGetter(NamespacedKey key) {
        RecipeCompletableListener.unregisterPlayerInventoryItemGetter(key);
    }

    @ApiStatus.Obsolete
    public static void unregisterPlayerInventoryItemGetter(RecipeCompletableListener.PlayerInventoryItemSeeker itemGetter) {
        RecipeCompletableListener.unregisterPlayerInventoryItemGetter(itemGetter.getKey());
    }

    @ApiStatus.Obsolete
    public static Collection<RecipeCompletableListener.PlayerInventoryItemSeeker> getPlayerInventoryItemGetters() {
        return RecipeCompletableListener.PLAYER_INVENTORY_ITEM_GETTERS.values();
    }

    @ApiStatus.Obsolete
    public static Set<SlimefunItem> getAllRecipeCompletableBlocks() {
        return RecipeCompletableListener.INGREDIENT_SLOTS.keySet();
    }
}
