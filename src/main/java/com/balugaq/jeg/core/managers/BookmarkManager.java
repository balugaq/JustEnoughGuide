/*
 * Copyright (c) 2024-2025 balugaq
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

package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.api.objects.collection.data.Bookmark;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.ProfileDataController;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.config.SlimefunDatabaseManager;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is responsible for managing bookmarks.
 * It provides methods to add, remove, get, and clear bookmarks.
 * This feature is based on CN-Slimefun4's {@link SlimefunDatabaseManager}
 * to create a backpack for each player and store their bookmarks in it.
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"unused", "deprecation"})
@Getter
public class BookmarkManager extends AbstractManager {
    private static final int DATA_ITEM_SLOT = 0;
    private static final String BACKPACK_NAME = "JEGBookmarkBackpack";
    private static final @Nullable ProfileDataController controller =
            Slimefun.getDatabaseManager().getProfileDataController();
    private final @NotNull NamespacedKey BOOKMARKS_KEY;
    private final @NotNull Plugin plugin;

    public BookmarkManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.BOOKMARKS_KEY = new NamespacedKey(plugin, "bookmarks");
    }

    public void addBookmark(@NotNull Player player, @NotNull SlimefunItem slimefunItem) {
        PlayerBackpack backpack = getOrCreateBookmarkBackpack(player);
        if (backpack == null) {
            return;
        }

        addBookmark0(player, backpack, slimefunItem);
    }

    public void addBookmark(@NotNull Player player, @NotNull ItemGroup itemGroup) {
        PlayerBackpack backpack = getOrCreateBookmarkBackpack(player);
        if (backpack == null) {
            return;
        }

        addBookmark0(player, backpack, itemGroup);
    }

    private void addBookmark0(
            final @NotNull Player player, @NotNull PlayerBackpack backpack, @NotNull SlimefunItem slimefunItem) {
        ItemStack bookmarksItem = backpack.getInventory().getItem(DATA_ITEM_SLOT);
        if (bookmarksItem == null || bookmarksItem.getType() == Material.AIR) {
            bookmarksItem = markItemAsBookmarksItem(new ItemStack(Material.DIRT), player);
        }

        ItemStack itemStack = ItemStackUtil.getCleanItem(Converter.getItem(bookmarksItem, itemMeta -> {
            List<String> lore = itemMeta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            String id = slimefunItem.getId();
            lore.remove(id);
            lore.add(id);
            itemMeta.setLore(lore);
        }));

        backpack.getInventory().setItem(DATA_ITEM_SLOT, itemStack);
        operateController(controller -> {
            controller.saveBackpackInventory(backpack, DATA_ITEM_SLOT);
        });
    }

    private void addBookmark0(
            final @NotNull Player player, @NotNull PlayerBackpack backpack, @NotNull ItemGroup itemGroup) {
        ItemStack bookmarksItem = backpack.getInventory().getItem(DATA_ITEM_SLOT);
        if (bookmarksItem == null || bookmarksItem.getType() == Material.AIR) {
            bookmarksItem = markItemAsBookmarksItem(new ItemStack(Material.DIRT), player);
        }

        ItemStack itemStack = ItemStackUtil.getCleanItem(Converter.getItem(bookmarksItem, itemMeta -> {
            List<String> lore = itemMeta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            String id = "itemgroup:" + itemGroup.getKey();
            lore.remove(id);
            lore.add(id);
            itemMeta.setLore(lore);
        }));

        backpack.getInventory().setItem(DATA_ITEM_SLOT, itemStack);
        operateController(controller -> {
            controller.saveBackpackInventory(backpack, DATA_ITEM_SLOT);
        });
    }

    @Nullable
    public List<Bookmark> getBookmarkedItems(@NotNull Player player) {
        PlayerBackpack backpack = getBookmarkBackpack(player);
        if (backpack == null) {
            return null;
        }

        ItemStack bookmarksItem = backpack.getInventory().getItem(DATA_ITEM_SLOT);
        if (bookmarksItem == null || bookmarksItem.getType() == Material.AIR) {
            return null;
        }

        if (!isBookmarksItem(bookmarksItem, player)) {
            return null;
        }

        List<Bookmark> bookmarkedItems = new ArrayList<>();
        ItemMeta itemMeta = bookmarksItem.getItemMeta();
        if (itemMeta == null) {
            return null;
        }

        List<String> lore = itemMeta.getLore();
        if (lore != null) {
            for (String id : lore) {
                if (id.startsWith("itemgroup:")) {
                    String s = id.substring(10);
                    for (ItemGroup itemGroup : Slimefun.getRegistry().getAllItemGroups()) {
                        if (itemGroup.getKey().toString().equals(s)) {
                            bookmarkedItems.add(Bookmark.of(itemGroup));
                        }
                    }
                } else {
                    SlimefunItem sfitem = SlimefunItem.getById(id);
                    if (sfitem != null) {
                        bookmarkedItems.add(Bookmark.of(sfitem));
                    }
                }
            }
        }

        return bookmarkedItems;
    }

    public void removeBookmark(@NotNull Player player, @NotNull SlimefunItem slimefunItem) {
        PlayerBackpack backpack = getBookmarkBackpack(player);
        if (backpack == null) {
            return;
        }

        removeBookmark0(backpack, slimefunItem);
    }

    public void removeBookmark(@NotNull Player player, @NotNull ItemGroup itemGroup) {
        PlayerBackpack backpack = getBookmarkBackpack(player);
        if (backpack == null) {
            return;
        }

        removeBookmark0(backpack, itemGroup);
    }

    private void removeBookmark0(@NotNull PlayerBackpack backpack, @NotNull SlimefunItem slimefunItem) {
        ItemStack bookmarksItem = backpack.getInventory().getItem(DATA_ITEM_SLOT);
        if (bookmarksItem == null || bookmarksItem.getType() == Material.AIR) {
            return;
        }

        ItemStack itemStack = ItemStackUtil.getCleanItem(Converter.getItem(bookmarksItem, itemMeta -> {
            List<String> lore = itemMeta.getLore();
            if (lore == null) {
                return;
            }
            lore.remove(slimefunItem.getId());
            itemMeta.setLore(lore);
        }));

        backpack.getInventory().setItem(DATA_ITEM_SLOT, itemStack);
        operateController(controller -> {
            controller.saveBackpackInventory(backpack, DATA_ITEM_SLOT);
        });
    }

    private void removeBookmark0(@NotNull PlayerBackpack backpack, @NotNull ItemGroup itemGroup) {
        ItemStack bookmarksItem = backpack.getInventory().getItem(DATA_ITEM_SLOT);
        if (bookmarksItem == null || bookmarksItem.getType() == Material.AIR) {
            return;
        }

        ItemStack itemStack = ItemStackUtil.getCleanItem(Converter.getItem(bookmarksItem, itemMeta -> {
            List<String> lore = itemMeta.getLore();
            if (lore == null) {
                return;
            }
            lore.remove("itemgroup:" + itemGroup.getKey());
            itemMeta.setLore(lore);
        }));

        backpack.getInventory().setItem(DATA_ITEM_SLOT, itemStack);
        operateController(controller -> {
            controller.saveBackpackInventory(backpack, DATA_ITEM_SLOT);
        });
    }

    public void clearBookmarks(@NotNull Player player) {
        PlayerBackpack backpack = getBookmarkBackpack(player);
        if (backpack == null) {
            return;
        }

        clearBookmarks0(backpack);
    }

    private void clearBookmarks0(@NotNull PlayerBackpack backpack) {
        ItemStack bookmarksItem = backpack.getInventory().getItem(DATA_ITEM_SLOT);
        if (bookmarksItem == null || bookmarksItem.getType() == Material.AIR) {
            return;
        }

        ItemStack itemStack = ItemStackUtil.getCleanItem(
                Converter.getItem(bookmarksItem, itemMeta -> itemMeta.setLore(new ArrayList<>())));

        backpack.getInventory().setItem(DATA_ITEM_SLOT, itemStack);
        operateController(controller -> {
            controller.saveBackpackInventory(backpack, DATA_ITEM_SLOT);
        });
    }

    @Nullable
    public PlayerBackpack getOrCreateBookmarkBackpack(@NotNull Player player) {
        PlayerBackpack backpack = getBookmarkBackpack(player);
        if (backpack == null) {
            backpack = createBackpack(player);
        }

        return backpack;
    }

    @Nullable
    public PlayerBackpack createBackpack(@NotNull Player player) {
        PlayerProfile profile = operateController(controller -> {
            return controller.getProfile(player);
        });
        if (profile == null) {
            return null;
        }

        PlayerBackpack backpack = operateController(controller -> {
            return controller.createBackpack(player, BACKPACK_NAME, profile.nextBackpackNum(), 9);
        });
        if (backpack == null) {
            return null;
        }

        backpack.getInventory().setItem(DATA_ITEM_SLOT, markItemAsBookmarksItem(new ItemStack(Material.DIRT), player));
        operateController(controller -> {
            controller.saveBackpackInventory(backpack, DATA_ITEM_SLOT);
        });
        return backpack;
    }

    @Nullable
    public PlayerBackpack getBookmarkBackpack(@NotNull Player player) {
        PlayerProfile profile = operateController(controller -> {
            return controller.getProfile(player);
        });
        if (profile == null) {
            return null;
        }

        Set<PlayerBackpack> backpacks = operateController(controller -> {
            return controller.getBackpacks(profile.getUUID().toString());
        });
        if (backpacks == null || backpacks.isEmpty()) {
            return null;
        }

        for (PlayerBackpack backpack : backpacks) {
            if (backpack.getName().equals(BACKPACK_NAME)) {
                Inventory inventory = backpack.getInventory();
                ItemStack[] contents = inventory.getContents();

                ItemStack bookmarksItem = contents[DATA_ITEM_SLOT];
                if (bookmarksItem == null || bookmarksItem.getType() == Material.AIR) {
                    return null;
                }

                if (!isBookmarksItem(bookmarksItem, player)) {
                    return null;
                }

                for (int i = 0; i < contents.length; i++) {
                    if (i != DATA_ITEM_SLOT && contents[i] != null && contents[i].getType() != Material.AIR) {
                        return null;
                    }
                }

                return backpack;
            }
        }

        return null;
    }

    @NotNull
    public ItemStack markItemAsBookmarksItem(@NotNull ItemStack itemStack, @NotNull Player player) {
        return ItemStackUtil.getCleanItem(Converter.getItem(itemStack, itemMeta -> itemMeta.getPersistentDataContainer()
                .set(
                        BOOKMARKS_KEY,
                        PersistentDataType.STRING,
                        player.getUniqueId().toString())));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isBookmarksItem(@NotNull ItemStack itemStack, @NotNull Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }

        String uuid = itemMeta.getPersistentDataContainer().get(BOOKMARKS_KEY, PersistentDataType.STRING);
        return uuid != null && uuid.equals(player.getUniqueId().toString());
    }

    public void unmarkBookmarksItem(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().remove(BOOKMARKS_KEY);
            itemStack.setItemMeta(itemMeta);
        }
    }

    private void operateController(@NotNull Consumer<ProfileDataController> consumer) {
        if (controller != null) {
            consumer.accept(controller);
        }
    }

    private <T, R> @Nullable R operateController(@NotNull Function<ProfileDataController, R> function) {
        if (controller != null) {
            return function.apply(controller);
        }
        return null;
    }
}
