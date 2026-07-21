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

package com.balugaq.jeg.implementation.items;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.1
 */
@NullMarked
public class CompanionItem extends JEGSlimefunItem {
    public CompanionItem(final ItemGroup itemGroup, final SlimefunItemStack item, final RecipeType recipeType, final @Nullable ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    public CompanionItem(final ItemGroup itemGroup, final SlimefunItemStack item, final RecipeType recipeType, final @Nullable ItemStack[] recipe, @Nullable final ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
    }

    protected CompanionItem(final ItemGroup itemGroup, final ItemStack item, final String id, final RecipeType recipeType, final ItemStack[] recipe) {
        super(itemGroup, item, id, recipeType, recipe);
    }
}
