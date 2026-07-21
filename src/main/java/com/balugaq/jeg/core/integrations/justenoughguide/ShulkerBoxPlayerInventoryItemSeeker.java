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

package com.balugaq.jeg.core.integrations.justenoughguide;

import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.StackUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.1
 */
@NullMarked
public class ShulkerBoxPlayerInventoryItemSeeker implements RecipeCompletableListener.PlayerInventoryItemSeeker {
    @Override
    public @NonNegative int getItemStack(final RecipeCompleteSession session, final ItemStack target, final ItemStack item, int amount) {
        if (!item.getType().name().contains("SHULKER_BOX")) {
            return 0;
        }

        var meta = item.getItemMeta();
        if (!(meta instanceof BlockStateMeta blockStateMeta)) {
            return 0;
        }

        var blockState = blockStateMeta.getBlockState();
        if (!(blockState instanceof Container container)) {
            return 0;
        }

        Inventory shulker = container.getInventory();
        int got = 0;
        for (int idx = 0; idx < shulker.getSize(); idx++) {
            ItemStack innerItem = shulker.getItem(idx);

            if (innerItem == null || innerItem.getType() == Material.AIR || !StackUtils.itemsMatch(innerItem, target)) {
                continue;
            }

            int innerItemAmount = innerItem.getAmount();

            if (innerItemAmount <= amount) {
                amount -= innerItemAmount;
                got += innerItemAmount;
                shulker.clear(idx);
            } else {
                innerItem.setAmount(innerItemAmount - amount);
                got += amount;
                shulker.setItem(idx, item);
                amount = 0;
            }

            if (amount <= 0) {
                blockStateMeta.setBlockState(blockState);
                item.setItemMeta(blockStateMeta);
                return got;
            }
        }
        blockStateMeta.setBlockState(blockState);
        item.setItemMeta(blockStateMeta);
        return got;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return KeyUtil.newKey("shulker_box_handler");
    }
}
