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

package com.balugaq.jeg.api.anvil;

import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.MinecraftVersion;
import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.1
 */
@Getter
@NullMarked
public class AnvilMenu implements InventoryHolder {
    private static @Nullable Class<?> anvilViewClass;
    static {
        try {
            // Paper 1.21+ API
            if (PaperLib.isPaper() && MinecraftVersion.current().isAtLeast(MinecraftVersion.V1_21)) {
                anvilViewClass = Class.forName("org.bukkit.inventory.view.AnvilView");
            } else {
                anvilViewClass = null;
            }
        } catch (ClassNotFoundException e) {
            Debug.severe("[AnvilMenu] Unable to get AnvilView class.");
            Debug.trace(e);
            anvilViewClass = null;
        }
    }
    private final Int2ObjectOpenHashMap<@Nullable ItemStack> guiItems = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<ChestMenu.@Nullable MenuClickHandler> guiClickHandlers = new Int2ObjectOpenHashMap<>();
    private @Nullable String title;
    private @Nullable OpenHandler openHandler;
    private @Nullable RenameHandler renameHandler;
    private @Nullable CloseHandler closeHandler;
    private boolean playerInventoryClickable = true;
    private boolean emptySlotClickable = true;
    private @Nullable ChestMenu.MenuClickHandler playerInventoryClickHandler;
    private @Setter AnvilInventory inventory;

    public static AnvilMenu create() {
        return new AnvilMenu();
    }

    public AnvilMenu setPlayerInventoryClickable(boolean clickable) {
        playerInventoryClickable = clickable;
        return this;
    }

    public AnvilMenu setEmptySlotClickable(boolean clickable) {
        emptySlotClickable = clickable;
        return this;
    }

    public AnvilMenu setPlayerInventoryClickHandler(ChestMenu.MenuClickHandler clickHandler) {
        playerInventoryClickHandler = clickHandler;
        return this;
    }

    public @Nullable ChestMenu.MenuClickHandler getClickHandler(int slot) {
        return guiClickHandlers.get(slot);
    }

    public AnvilMenu setGuiItem(int slot, ItemStack item) {
        guiItems.put(slot, item);
        return this;
    }

    public AnvilMenu setGuiItem(int slot, ItemStack item, ChestMenu.MenuClickHandler clickHandler) {
        guiItems.put(slot, item);
        guiClickHandlers.put(slot, clickHandler);
        return this;
    }

    public AnvilMenu setGuiClickHandler(int slot, ChestMenu.MenuClickHandler clickHandler) {
        guiClickHandlers.put(slot, clickHandler);
        return this;
    }

    public AnvilMenu onOpen(OpenHandler handler) {
        this.openHandler = handler;
        return this;
    }

    public AnvilMenu onRename(RenameHandler handler) {
        this.renameHandler = handler;
        return this;
    }

    public AnvilMenu onClose(CloseHandler handler) {
        this.closeHandler = handler;
        return this;
    }

    public AnvilMenu title(String title) {
        this.title = title;
        return this;
    }

    public @Nullable String title() {
        return title;
    }

    public void open(Player player) {
        AnvilInventory anvilInventory = (AnvilInventory) Bukkit.createInventory(this, InventoryType.ANVIL, title());
        setInventory(anvilInventory);
        if (guiItems.containsKey(0)) {
            anvilInventory.setFirstItem(guiItems.get(0));
        }
        if (guiItems.containsKey(1)) {
            anvilInventory.setSecondItem(guiItems.get(1));
        }
        if (guiItems.containsKey(2)) {
            anvilInventory.setResult(guiItems.get(2));
        }

        player.openInventory(anvilInventory);
        if (openHandler != null) {
            openHandler.onOpen(player);
        }
        if (anvilViewClass != null && anvilViewClass.isInstance(player.getOpenInventory())) {
            AnvilView view = (AnvilView) player.getOpenInventory();
            if (PaperLib.isPaper()) {
                view.bypassEnchantmentLevelRestriction(true);
            }
        }
    }

    @Nullable
    public static String getRenameText(Player player) {
        if (anvilViewClass != null && anvilViewClass.isInstance(player.getOpenInventory())) {
            return (String) ReflectionUtil.invokeMethod(player.getOpenInventory(), "getRenameText");
        }

        Inventory topInventory = (Inventory) ReflectionUtil.invokeMethod(player.getOpenInventory(), "getTopInventory", Inventory.class);
        if (!(topInventory instanceof AnvilInventory anvilInventory)) {
            return null;
        }

        return anvilInventory.getRenameText();
    }

    @Nullable
    public static AnvilMenu getMenu(Player player) {
        Inventory topInventory = (Inventory) ReflectionUtil.invokeMethod(player.getOpenInventory(), "getTopInventory", Inventory.class);
        if (topInventory != null && topInventory.getHolder() instanceof AnvilMenu menu) return menu;
        return null;
    }
}
