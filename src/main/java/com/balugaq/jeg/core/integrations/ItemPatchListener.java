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
        // Fix https://github.com/balugaq/JustEnoughGuide/issues/93
        // meta.getPersistentDataContainer().set(getKey(), PersistentDataType.BOOLEAN, true);
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
