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

package com.balugaq.jeg.api.helper;

import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.1
 */
@NullMarked
public class AnvilInventoryHelper {
    public static AnvilInventoryOperator findAnvilInventory(Player player) {
        // todo
        return null;
    }

    /**
     * @author balugaq
     * @since 2.1
     */
    @NullMarked
    public static class AnvilInventoryOperator {
        public AnvilInventoryOperator setGuiItem(int slot, ItemStack item, ChestMenu.MenuClickHandler clickHandler) {
            // todo
            return this;
        }

        public AnvilInventoryOperator presetName(String presetName) {
            // todo
            return this;
        }

        public AnvilInventoryOperator onOpen(OpenHandler handler) {
            // todo
            return this;
        }

        public AnvilInventoryOperator onRename(RenameHandler handler) {
            // todo
            return this;
        }

        public AnvilInventoryOperator onClose(CloseHandler handler) {
            // todo
            return this;
        }

        public AnvilInventoryOperator title(String title) {
            // todo
            return this;
        }

        public String title() {
            // todo
            return null;
        }

        public void open(Player player) {
            AnvilGuiHolder holder = new AnvilGuiHolder();
            AnvilInventory anvilInventory = (AnvilInventory) Bukkit.createInventory(holder, InventoryType.ANVIL, title());
            holder.setInventory(anvilInventory);

            // todo
            player.openInventory(anvilInventory);
        }
    }

    /**
     * @author balugaq
     * @since 2.1
     */
    @NullMarked
    public static class AnvilGuiHolder implements InventoryHolder {
        private @Getter @Setter Inventory inventory;
    }

    /**
     * @author balugaq
     * @since 2.1
     */
    public interface OpenHandler {
        void onOpen(Player player, InventoryOpenEvent event);
    }

    /**
     * @author balugaq
     * @since 2.1
     */
    @NullMarked
    @FunctionalInterface
    public interface RenameHandler {
        void onRename(Player player, String oldName, String newName, PrepareAnvilEvent event);
    }

    /**
     * @author balugaq
     * @since 2.1
     */
    public interface CloseHandler {
        void onClose(Player player, InventoryCloseEvent event);
    }
}
