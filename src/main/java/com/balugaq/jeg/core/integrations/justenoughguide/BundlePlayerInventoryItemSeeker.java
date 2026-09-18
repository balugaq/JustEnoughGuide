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
import com.balugaq.jeg.utils.RecipeCompleteUtils;
import com.balugaq.jeg.utils.StackUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;

/**
 * @author balugaq
 * @since 2.1
 */
@NullMarked
public class BundlePlayerInventoryItemSeeker implements RecipeCompletableListener.PlayerInventoryItemSeeker {
    @SuppressWarnings("ConstantValue")
    @Override
    public @NonNegative long getItemStack(final RecipeCompleteSession session, final ItemStack target, final ItemStack item, long amount) {
        if (!item.getType().name().endsWith("BUNDLE")) {
            return 0;
        }

        var meta = item.getItemMeta();
        if (!(meta instanceof BundleMeta bundle)) {
            return 0;
        }

        var origin = bundle.getItems();
        if (origin == null || origin.isEmpty()) {
            return 0;
        }

        var items = new ArrayList<>(origin);
        int got = 0;
        for (int idx = 0; idx < items.size(); idx++) {
            ItemStack innerItem = items.get(idx);
            if (innerItem == null || innerItem.getType() == Material.AIR || !StackUtils.itemsMatch(innerItem, target)) {
                continue;
            }

            int existing = innerItem.getAmount();

            if (existing <= amount) {
                amount -= existing;
                got += existing;
                items.set(idx, null);
            } else {
                innerItem.setAmount((int) (existing - amount));
                amount = 0;
            }

            if (amount <= 0) {
                bundle.setItems(RecipeCompleteUtils.trimItems(items));
                item.setItemMeta(meta);
                return got;
            }
        }
        bundle.setItems(RecipeCompleteUtils.trimItems(items));
        item.setItemMeta(meta);
        return got;
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public @NonNegative long countItemStack(final RecipeCompleteSession session, final ItemStack target, final ItemStack item) {
        if (!item.getType().name().endsWith("BUNDLE")) {
            return 0;
        }

        var meta = item.getItemMeta();
        if (!(meta instanceof BundleMeta bundle)) {
            return 0;
        }

        var origin = bundle.getItems();
        if (origin == null || origin.isEmpty()) {
            return 0;
        }

        var items = new ArrayList<>(origin);
        long got = 0;
        for (ItemStack innerItem : items) {
            if (innerItem == null || innerItem.getType() == Material.AIR || !StackUtils.itemsMatch(innerItem, target)) {
                continue;
            }

            int existing = innerItem.getAmount();
            got += existing;
        }
        bundle.setItems(RecipeCompleteUtils.trimItems(items));
        item.setItemMeta(meta);
        return got;
    }

    @Override
    public NamespacedKey getKey() {
        return KeyUtil.newKey("bundle_handler");
    }
}
