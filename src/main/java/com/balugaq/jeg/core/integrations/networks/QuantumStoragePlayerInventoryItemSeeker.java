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

package com.balugaq.jeg.core.integrations.networks;

import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.StackUtils;
import io.github.sefiraat.networks.network.stackcaches.QuantumCache;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.datatypes.DataTypeMethods;
import io.github.sefiraat.networks.utils.datatypes.PersistentQuantumStorageType;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * @author balugaq
 * @since 2.1
 */
@NullMarked
public class QuantumStoragePlayerInventoryItemSeeker implements RecipeCompletableListener.PlayerInventoryItemSeeker {
    public @Nullable QuantumCache getInstance(ItemMeta meta) {
        var instance = DataTypeMethods.getCustom(meta, Keys.QUANTUM_STORAGE_INSTANCE, PersistentQuantumStorageType.TYPE);
        if (instance == null)
            instance = DataTypeMethods.getCustom(meta, Keys.QUANTUM_STORAGE_INSTANCE2, PersistentQuantumStorageType.TYPE);
        if (instance == null)
            instance = DataTypeMethods.getCustom(meta, Keys.QUANTUM_STORAGE_INSTANCE3, PersistentQuantumStorageType.TYPE);
        return instance;
    }

    @Override
    public @NonNegative long getItemStack(final RecipeCompleteSession session, final ItemStack target, final ItemStack item, long amount) {
        if (!(SlimefunItem.getByItem(item) instanceof NetworkQuantumStorage nqs)) {
            return 0;
        }

        var meta = item.getItemMeta();
        var instance = getInstance(meta);
        if (instance == null) return 0;

        ItemStack innerItem = instance.getItemStack();
        if (innerItem == null || innerItem.getType() == Material.AIR || !StackUtils.itemsMatch(innerItem, target)) {
            return 0;
        }

        long innerItemAmount = instance.getAmount();
        if (innerItemAmount <= 0) return 0;

        long got;
        if (innerItemAmount <= amount) {
            instance.reduceAmount((int) innerItemAmount);
            got = (int) innerItemAmount;
        } else {
            instance.reduceAmount((int) amount);
            got = amount;
        }
        var newMeta = nqs.getItem().getItemMeta();
        DataTypeMethods.setCustom(newMeta, Keys.QUANTUM_STORAGE_INSTANCE, PersistentQuantumStorageType.TYPE, instance);
        instance.addMetaLore(newMeta);
        item.setItemMeta(newMeta);

        return got;
    }

    @Override
    public @NonNegative long countItemStack(final RecipeCompleteSession session, final ItemStack target, final ItemStack item) {
        if (!(SlimefunItem.getByItem(item) instanceof NetworkQuantumStorage)) {
            return 0;
        }

        var meta = item.getItemMeta();
        var instance = getInstance(meta);
        if (instance == null) return 0;

        ItemStack innerItem = instance.getItemStack();
        if (innerItem == null || innerItem.getType() == Material.AIR || !StackUtils.itemsMatch(innerItem, target)) {
            return 0;
        }

        long innerItemAmount = instance.getAmount();
        if (innerItemAmount <= 0) return 0;
        return innerItemAmount;
    }

    @Override
    public NamespacedKey getKey() {
        return KeyUtil.newKey("quantum_storage_handler");
    }
}
