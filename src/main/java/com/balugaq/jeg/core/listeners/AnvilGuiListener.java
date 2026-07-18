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

package com.balugaq.jeg.core.listeners;

import com.balugaq.jeg.api.anvil.AnvilMenu;
import com.balugaq.jeg.api.anvil.CloseHandler;
import com.balugaq.jeg.api.anvil.RenameHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author balugaq
 * @since 2.1
 */
@SuppressWarnings("deprecation")
public class AnvilGuiListener implements Listener {
    private final Map<UUID, String> anvilText = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRename(PrepareAnvilEvent event) {
        if (!(event.getInventory().getHolder() instanceof AnvilMenu menu)) {
            return;
        }

        Player player = (Player) event.getInventory().getViewers().get(0);

        String newName = AnvilMenu.getRenameText(player);
        String oldName = anvilText.put(player.getUniqueId(), newName);
        if (newName == null || !Objects.equals(oldName, newName)) {
            return;
        }

        RenameHandler renameHandler = menu.getRenameHandler();
        if (renameHandler != null) {
            renameHandler.onRename(player, oldName, newName, event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof AnvilMenu menu)) {
            return;
        }

        CloseHandler closeHandler = menu.getCloseHandler();
        if (closeHandler != null) {
            closeHandler.onClose((Player) event.getInventory().getViewers().get(0), event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof AnvilMenu menu)) {
            return;
        }

        if (!menu.isEmptySlotClickable() && (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)) {
            event.setCancelled(true);
            return;
        }

        ChestMenu.MenuClickHandler clickHandler;
        if (event.getSlot() < 3) {
            clickHandler = menu.getClickHandler(event.getSlot());
        } else {
            if (!menu.isPlayerInventoryClickable()) {
                event.setCancelled(true);
                return;
            }
            clickHandler = menu.getPlayerInventoryClickHandler();
        }

        if (clickHandler != null) invokeClickHandler(clickHandler, event);
    }

    public void invokeClickHandler(ChestMenu.MenuClickHandler handler, InventoryClickEvent event) {
        if (handler instanceof ChestMenu.AdvancedMenuClickHandler advanced) {
            event.setCancelled(!advanced.onClick(event, (Player) event.getWhoClicked(), event.getRawSlot(), event.getCursor(), new ClickAction(event.isRightClick(), event.isShiftClick())));
        } else {
            event.setCancelled(!handler.onClick((Player) event.getWhoClicked(), event.getRawSlot(), event.getCurrentItem(), new ClickAction(event.isRightClick(), event.isShiftClick())));
        }
    }
}
