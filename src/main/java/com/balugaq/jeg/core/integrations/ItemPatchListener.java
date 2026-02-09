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

package com.balugaq.jeg.core.integrations;

import com.balugaq.jeg.utils.KeyUtil;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public interface ItemPatchListener extends Listener, Keyed {
    @Nullable
    @Contract("null -> null")
    static ItemStack untag(@Nullable ItemStack dirty) {
        if (dirty == null || dirty.getType() == Material.AIR) {
            return null;
        }
        SlimefunItem sfi = SlimefunItem.getByItem(dirty);
        return sfi == null ? new ItemStack(dirty.getType()) : sfi.getItem();
    }

    default void tagMeta(ItemMeta meta) {
        meta.getPersistentDataContainer().set(getKey(), PersistentDataType.BOOLEAN, true);
    }

    default NamespacedKey getKey() {
        return KeyUtil.newKey(getClass().getSimpleName().toLowerCase());
    }

    default boolean isTagged(@Nullable ItemStack stack) {
        if (stack == null) return true;
        var meta = stack.getItemMeta();
        if (meta == null) return true;
        return isTagged(meta);
    }

    default boolean isTagged(ItemMeta meta) {
        return meta.getPersistentDataContainer().has(getKey(), PersistentDataType.BOOLEAN);
    }
}
