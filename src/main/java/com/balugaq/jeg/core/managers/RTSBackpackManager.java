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
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

/**
 * @author balugaq
 * @since 1.3
 */
@Getter
public class RTSBackpackManager extends AbstractManager {
    private static final int IDENTIFIER_SLOT = 53;
    @Deprecated
    private static final String BACKPACK_NAME = "JEGRTSBackpack";
    private static final NamespacedKey STATUS_KEY = new NamespacedKey(JustEnoughGuide.getInstance(), "status");
    private static final String OPEN_STATUS = "open";
    private static final String CLOSE_STATUS = "close";
    private final @NotNull Plugin plugin;
    private final @NotNull NamespacedKey SERVER_KEY;
    private final @NotNull NamespacedKey OWNER_KEY;

    public RTSBackpackManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.SERVER_KEY = new NamespacedKey(plugin, "server");
        this.OWNER_KEY = new NamespacedKey(plugin, "owner");
    }

    public static ItemStack @NotNull [] getStorageContents(@NotNull PlayerInventory inventory) {
        ItemStack[] contents = new ItemStack[36];
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = inventory.getItem(i);
            contents[i] = itemStack;
        }
        return contents;
    }

    /**
     * Saves the player's inventory backup to a backpack.
     *
     * @param player the player whose inventory to back up
     */
    public void saveInventoryBackupFor(@NotNull Player player) {
        PlayerProfile profile = PlayerProfile.find(player).orElse(null);
        if (profile == null) {
            return;
        }

        PlayerBackpack b = null;
        // find an existing backpack
        Collection<PlayerBackpack> backpacks = profile.getPlayerData().getBackpacks().values();
        if (backpacks != null && !backpacks.isEmpty()) {
            for (PlayerBackpack backpack : backpacks) {
                Inventory inventory = backpack.getInventory();
                if (inventory.getContents().length < 54) {
                    continue;
                }

                // check if identifier is valid
                ItemStack identifierItem = inventory.getItem(IDENTIFIER_SLOT);
                if (identifierItem == null || identifierItem.getType() == Material.AIR) {
                    continue;
                }

                if (!isIdentifier(identifierItem, player)) {
                    continue;
                }

                b = backpack;
                break;
            }
        }

        // create a new backpack
        if (b == null) {
            b = profile.createBackpack(54);
        }
        Inventory inventory = b.getInventory();
        setIdentifier(player, inventory, IDENTIFIER_SLOT, true);
        ItemStack[] contents = getStorageContents(player.getInventory());
        for (int i = 0; i < contents.length; i++) {
            ItemStack itemStack = contents[i];
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            inventory.setItem(i, itemStack);
        }
        profile.save();
    }

    /**
     * Clears the player's inventory.
     *
     * @param player the player whose inventory to clear
     */
    public void clearInventoryFor(@NotNull Player player) {
        ItemStack[] newContents = new ItemStack[player.getInventory().getStorageContents().length];
        for (int i = 0; i < newContents.length; i++) {
            newContents[i] = new ItemStack(Material.AIR);
        }
        player.getInventory().setStorageContents(newContents);
    }

    /**
     * Restores the player's inventory from a backpack.
     *
     * @param player the player whose inventory to restore
     */
    public void restoreInventoryFor(@NotNull Player player) {
        PlayerProfile profile = PlayerProfile.find(player).orElse(null);
        if (profile == null) {
            return;
        }

        Collection<PlayerBackpack> backpacks = profile.getPlayerData().getBackpacks().values();
        if (backpacks == null || backpacks.isEmpty()) {
            return;
        }

        for (PlayerBackpack backpack : backpacks) {
            Inventory inventory = backpack.getInventory();
            ItemStack[] contents = inventory.getContents();
            if (contents.length < 54) {
                continue;
            }

            ItemStack identifierItem = inventory.getItem(IDENTIFIER_SLOT);
            if (identifierItem == null || identifierItem.getType() == Material.AIR) {
                continue;
            }

            if (!isIdentifier(identifierItem, player)) {
                continue;
            }

            if (!isOpenIdentifier(identifierItem)) {
                continue;
            }

            // found the backpack, now restore it
            for (int i = 0; i < 36; i++) {
                ItemStack itemStack = contents[i];
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    player.getInventory().setItem(i, new ItemStack(Material.AIR));
                    continue;
                }
                player.getInventory().setItem(i, itemStack.clone());
            }

            // clear the backpack
            for (int i = 0; i < contents.length; i++) {
                if (i != IDENTIFIER_SLOT) {
                    backpack.getInventory().setItem(i, new ItemStack(Material.AIR));
                }
            }
            setIdentifier(player, backpack.getInventory(), IDENTIFIER_SLOT, false);
            profile.save();

            break;
        }
    }

    /**
     * Sets the identifier item in the inventory.
     *
     * @param player    the player
     * @param inventory the inventory
     * @param slot      the slot to set the identifier item
     * @param open      whether the identifier item should indicate an open status
     */
    public void setIdentifier(@NotNull Player player, @NotNull Inventory inventory, int slot, boolean open) {
        inventory.setItem(slot, getIdentifierItem(player, open));
    }

    /**
     * Creates and returns the identifier item.
     *
     * @param player the player
     * @param open   whether the identifier item should indicate an open status
     * @return the identifier item
     */
    public @NotNull ItemStack getIdentifierItem(@NotNull Player player, boolean open) {
        return Converter.getItem(Converter.getItem(Material.DIRT, "[RTS]", "[RTS]", "[RTS]", "[RTS]", UUID.randomUUID().toString()), meta -> {
            meta.getPersistentDataContainer().set(OWNER_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
            meta.getPersistentDataContainer().set(SERVER_KEY, PersistentDataType.STRING, JustEnoughGuide.getServerUUID().toString());
            if (open) {
                meta.getPersistentDataContainer().set(STATUS_KEY, PersistentDataType.STRING, OPEN_STATUS);
            } else {
                meta.getPersistentDataContainer().set(STATUS_KEY, PersistentDataType.STRING, CLOSE_STATUS);
            }
        });
    }

    /**
     * Checks if the item is a valid identifier for the player.
     *
     * @param item   the item to check
     * @param player the player
     * @return true if the item is a valid identifier, false otherwise
     */
    public boolean isIdentifier(@Nullable ItemStack item, @NotNull Player player) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        String owner = item.getItemMeta().getPersistentDataContainer().get(OWNER_KEY, PersistentDataType.STRING);
        if (owner == null) {
            return false;
        }

        if (!owner.equals(player.getUniqueId().toString())) {
            return false;
        }

        String serverUUID = item.getItemMeta().getPersistentDataContainer().get(SERVER_KEY, PersistentDataType.STRING);
        if (serverUUID == null) {
            return false;
        }

        if (!serverUUID.equals(JustEnoughGuide.getServerUUID().toString())) {
            return false;
        }

        return true;
    }

    /**
     * Checks if the identifier item indicates an open status.
     *
     * @param item the identifier item to check
     * @return true if the identifier item indicates an open status, false otherwise
     */
    public boolean isOpenIdentifier(@Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        String status = item.getItemMeta().getPersistentDataContainer().get(STATUS_KEY, PersistentDataType.STRING);
        if (status == null) {
            return false;
        }

        if (status.equals(OPEN_STATUS)) {
            return true;
        }

        return false;
    }
}
