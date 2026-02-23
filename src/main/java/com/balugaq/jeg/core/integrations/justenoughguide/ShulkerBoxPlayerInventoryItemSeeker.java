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
