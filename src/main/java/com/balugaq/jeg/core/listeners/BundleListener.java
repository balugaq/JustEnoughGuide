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

package com.balugaq.jeg.core.listeners;

import city.norain.slimefun4.holder.SlimefunInventoryHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.0
 */
@NullMarked
public class BundleListener implements Listener {
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isBundle(@Nullable ItemStack item) {
        if (item == null) {
            return false;
        }

        return item.getType().name().endsWith("BUNDLE");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBundleClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SlimefunInventoryHolder)) {
            return;
        }

        if (event.getWhoClicked().isOp()) {
            return;
        }

        if (!isBundle(event.getCursor()) && !isBundle(event.getCurrentItem())) {
            return;
        }

        event.setCancelled(true);
    }
}
