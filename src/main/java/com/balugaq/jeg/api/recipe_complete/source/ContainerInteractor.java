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
    @Nullable ItemStack getExistingStack(int slot);

    boolean fits(ItemStack stack, int ingredientIndex);

    void pushItem(ItemStack stack, int ingredientIndex, IntSet capacitySlots);

    static ContainerInteractor slimefun(BlockMenu blockMenu, boolean unordered, int[] ingredientSlots) {
        return new ContainerInteractor() {

            @Override
            @Nullable
            public ItemStack getExistingStack(int slot) {
                if (slot < blockMenu.getSize()) {
                    return blockMenu.getItemInSlot(slot);
                }
                return null;
            }

            @Override
            public boolean fits(ItemStack template, int ingredientIndex) {
                return BlockMenuUtil.fits(blockMenu, template, unordered ? ingredientSlots : new int[]{ingredientSlots[ingredientIndex]});
            }

            @Override
            public void pushItem(ItemStack received, int ingredientIndex, IntSet capacitySlots) {
                if (unordered) {
                    BlockMenuUtil.pushItem(blockMenu, received, RecipeCompletionUtils.mergeSlots(capacitySlots, ingredientSlots));
                } else {
                    BlockMenuUtil.pushItem(blockMenu, received, ingredientSlots[ingredientIndex]);
                }
            }
        };
    }

    static ContainerInteractor vanilla(Inventory inventory, boolean unordered, int[] ingredientSlots) {
        return new ContainerInteractor() {
            @Override
            public @Nullable ItemStack getExistingStack(int slot) {
                if (slot < inventory.getSize()) {
                    return inventory.getItem(slot);
                }
                return null;
            }

            @Override
            public boolean fits(ItemStack received, int ingredientIndex) {
                return InventoryUtil.fits(inventory, received, unordered ? ingredientSlots : new int[]{ingredientSlots[ingredientIndex]});
            }

            @Override
            public void pushItem(ItemStack received, int ingredientIndex, IntSet capacitySlots) {
                if (unordered) {
                    InventoryUtil.pushItem(inventory, received, RecipeCompletionUtils.mergeSlots(capacitySlots, ingredientSlots));
                } else {
                    InventoryUtil.pushItem(inventory, received, ingredientSlots[ingredientIndex]);
                }
            }
        };
    }
}
