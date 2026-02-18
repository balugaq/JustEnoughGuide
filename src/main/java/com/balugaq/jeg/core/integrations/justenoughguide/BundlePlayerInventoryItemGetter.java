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
import com.balugaq.jeg.api.recipe_complete.source.base.Source;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.MinecraftVersion;
import com.balugaq.jeg.utils.StackUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;

/**
 * @author balugaq
 * @since 2.1
 */
@NullMarked
public class BundlePlayerInventoryItemGetter implements RecipeCompletableListener.PlayerInventoryItemGetter {
    @Override
    public @NonNegative int getItemStack(final RecipeCompleteSession session, final ItemStack target, final ItemStack item, int amount) {
        if (!item.getType().name().contains("BUNDLE")) {
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
                innerItem.setAmount(existing - amount);
                amount = 0;
            }

            if (amount <= 0) {
                bundle.setItems(Source.trimItems(items));
                item.setItemMeta(meta);
                return got;
            }
        }
        bundle.setItems(Source.trimItems(items));
        item.setItemMeta(meta);
        return got;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return KeyUtil.newKey("bundle_handler");
    }
}
