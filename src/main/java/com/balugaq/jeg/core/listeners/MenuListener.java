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

import city.norain.slimefun4.api.menu.UniversalMenu;
import city.norain.slimefun4.holder.SlimefunInventoryHolder;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMenuClick(InventoryClickEvent event) {
        if ((event.getClick() == ClickType.DOUBLE_CLICK
            || event.getClick() == ClickType.SHIFT_LEFT
            || event.getClick() == ClickType.SHIFT_RIGHT)
            && event.getRawSlot() >= event.getInventory().getSize()) {
            var holder = event.getInventory().getHolder();
            if (isBlacklistedHolder(holder)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMenuDrag(InventoryDragEvent event) {
        if (event.getRawSlots().stream().mapToInt(i -> i).max().orElse(0) < event.getInventory().getSize()) {
            var holder = event.getInventory().getHolder();
            if (isBlacklistedHolder(holder)) {
                event.setCancelled(true);
            }
        }
    }

    public static boolean isBlacklistedHolder(InventoryHolder holder) {
        // filter out guide menus
        return holder instanceof SlimefunInventoryHolder
            && !(holder instanceof BlockMenu)
            && !(holder instanceof UniversalMenu)
            && !(holder instanceof PlayerBackpack);
    }
}
