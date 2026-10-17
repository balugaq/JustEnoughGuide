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

package com.balugaq.jeg.api.recipe_complete.source;

import com.balugaq.jeg.utils.BlockMenuUtil;
import com.balugaq.jeg.utils.InventoryUtil;
import com.balugaq.jeg.utils.RecipeCompletionUtils;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.1
 */
@NullMarked
public
interface ContainerInteractor {
    boolean unordered();

    int[] ingredientSlots();

    @Nullable ItemStack getExistingStack(int slot);

    default IntSet makePreferredSlots(@Nullable IntSet preferredSlots, int ingredientIndex) {
        if (unordered()) {
            return RecipeCompletionUtils.mergeSlots(preferredSlots, ingredientSlots());
        }
        return IntSets.singleton(ingredientSlots()[ingredientIndex]);
    }

    default boolean fits(ItemStack stack, int ingredientIndex, @Nullable IntSet preferredSlots) {
        return fits(stack, makePreferredSlots(preferredSlots, ingredientIndex).toIntArray());
    }


    default void pushItem(ItemStack stack, int ingredientIndex, @Nullable IntSet preferredSlots) {
        pushItem(stack, makePreferredSlots(preferredSlots, ingredientIndex).toIntArray());
    }

    boolean fits(ItemStack stack, int... slots);

    void pushItem(ItemStack stack, int... slots);

    static ContainerInteractor slimefun(BlockMenu blockMenu, boolean unordered, int[] ingredientSlots) {
        return new ContainerInteractor() {

            @Override
            public boolean unordered() {
                return unordered;
            }

            @Override
            public int[] ingredientSlots() {
                return ingredientSlots;
            }

            @Override
            @Nullable
            public ItemStack getExistingStack(int slot) {
                if (slot < blockMenu.getSize()) {
                    return blockMenu.getItemInSlot(slot);
                }
                return null;
            }

            @Override
            public boolean fits(ItemStack stack, int... slots) {
                return BlockMenuUtil.fits(blockMenu, stack, slots);
            }

            @Override
            public void pushItem(ItemStack stack, int... slots) {
                BlockMenuUtil.pushItem(blockMenu, stack, slots);
            }
        };
    }

    static ContainerInteractor vanilla(Inventory inventory, boolean unordered, int[] ingredientSlots) {
        return new ContainerInteractor() {
            @Override
            public boolean unordered() {
                return unordered;
            }

            @Override
            public int[] ingredientSlots() {
                return ingredientSlots;
            }

            @Override
            public @Nullable ItemStack getExistingStack(int slot) {
                if (slot < inventory.getSize()) {
                    return inventory.getItem(slot);
                }
                return null;
            }

            @Override
            public boolean fits(ItemStack stack, int... slots) {
                return InventoryUtil.fits(inventory, stack, slots);
            }

            @Override
            public void pushItem(ItemStack stack, int... slots) {
                InventoryUtil.pushItem(inventory, stack, slots);
            }
        };
    }
}
