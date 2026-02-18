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

package com.balugaq.jeg.core.integrations.networks;

import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.StackUtils;
import io.github.sefiraat.networks.slimefun.network.NetworkQuantumStorage;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.datatypes.DataTypeMethods;
import io.github.sefiraat.networks.utils.datatypes.PersistentQuantumStorageType;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
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
public class QuantumStoragePlayerInventoryItemGetter implements RecipeCompletableListener.PlayerInventoryItemGetter {
    @Override
    public @NonNegative int getItemStack(final RecipeCompleteSession session, final ItemStack target, final ItemStack item, int amount) {
        if (!(SlimefunItem.getByItem(item) instanceof NetworkQuantumStorage)) {
            return 0;
        }

        var meta = item.getItemMeta();
        var instance = DataTypeMethods.getCustom(meta, Keys.QUANTUM_STORAGE_INSTANCE, PersistentQuantumStorageType.TYPE);
        if (instance == null) instance = DataTypeMethods.getCustom(meta, Keys.QUANTUM_STORAGE_INSTANCE2, PersistentQuantumStorageType.TYPE);
        if (instance == null) instance = DataTypeMethods.getCustom(meta, Keys.QUANTUM_STORAGE_INSTANCE3, PersistentQuantumStorageType.TYPE);
        if (instance == null) return 0;

        ItemStack innerItem = instance.getItemStack();
        if (innerItem == null || innerItem.getType() == Material.AIR || !StackUtils.itemsMatch(innerItem, target)) {
            return 0;
        }

        long innerItemAmount = instance.getAmount();
        if (innerItemAmount <= 0) return 0;

        if (innerItemAmount <= amount) {
            instance.reduceAmount((int) innerItemAmount);
            return (int) innerItemAmount;
        } else {
            instance.reduceAmount(amount);
            return amount;
        }
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return KeyUtil.newKey("quantum_storage_handler");
    }
}
