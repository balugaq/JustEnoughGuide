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
import com.balugaq.jeg.api.groups.RTSSearchGroup;
import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.objects.events.RTSEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.JEGVersionedItemFlag;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.LocalHelper;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handle events related to the Real-Time Search (RTS) mode, consisting of
 * 1) Handling RTS anvil inventory
 * 2) Handling fake items interactions
 * 3) Restoring player inventory items
 *
 * @author balugaq
 * @since 1.4
 */
// todo : may make bugs with velocity / bungeecord server
@SuppressWarnings({"deprecation", "UnnecessaryUnicodeEscape", "ConstantValue"})
@Getter
@NullMarked
public class RTSListener implements Listener {
    public static final NamespacedKey FAKE_ITEM_KEY = KeyUtil.newKey("fake_item");
    public static final NamespacedKey CHEAT_AMOUNT_KEY = KeyUtil.newKey("cheat_amount");
    public static final Set<Player> openingPlayers = ConcurrentHashMap.newKeySet();
    public static final Map<Player, List<ItemStack>> cheatItems = new HashMap<>();
    public static final Integer[] FILL_ORDER = {
        9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
        27, 28, 29, 30, 31, 32, 33, 34, 35
    };

    /**
     * Checks if a player is currently in the RTS mode.
     *
     * @param player the player to check
     * @return true if the player is in RTS mode, false otherwise
     */
    public static boolean isRTSPlayer(Player player) {
        return openingPlayers.contains(player);
    }

    /**
     * Get a unique hash for a player head ItemStack.
     *
     * @param item the ItemStack to generate a hash for
     * @return the hash of the player head, or null if the item is not a player head
     * @author m1919810
     */
    @SuppressWarnings("DataFlowIssue")
    public static String getHash(@Nullable ItemStack item) {
        if (item != null && (item.getType() == Material.PLAYER_HEAD || item.getType() == Material.PLAYER_WALL_HEAD)) {
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof SkullMeta) {
                try {
                    URL t = ((SkullMeta) meta).getOwnerProfile().getTextures().getSkin();
                    String path = t.getPath();
                    String[] parts = path.split("/");
                    return parts[parts.length - 1];
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    /**
     * Quits the RTS mode for a player and restores their inventory.
     */
    public static void tryQuitRTS(Player player) {
        if (!isRTSPlayer(player)) {
            return;
        }

        openingPlayers.remove(player);
        RTSSearchGroup.RTS_ANVIL_INVENTORIES.remove(player);
        RTSSearchGroup.RTS_SEARCH_GROUPS.remove(player);
        RTSSearchGroup.RTS_PAGES.remove(player);
        JustEnoughGuide.getInstance().getRtsBackpackManager().restoreInventoryFor(player);
        player.getInventory().setContents(trimItems(player.getInventory().getContents()));
        player.updateInventory();
        if (cheatItems.containsKey(player)) {
            if (player.isOp() || player.hasPermission("slimefun.cheat.items")) {
                List<ItemStack> items = cheatItems.get(player);
                for (ItemStack item : items) {
                    Map<Integer, ItemStack> remnant = player.getInventory().addItem(item);
                    remnant.values().forEach(itm -> player.getWorld().dropItemNaturally(player.getLocation(), itm));
                }
            }
            cheatItems.remove(player);
        }
        Bukkit.getPluginManager().callEvent(new RTSEvents.CloseRTSEvent(player));
    }

    @Contract(pure = true, value = "null -> null; !null -> !null")
    public static @Nullable ItemStack @Nullable [] trimItems(@Nullable ItemStack @Nullable [] itemStacks) {
        if (itemStacks == null) return null;
        @Nullable ItemStack[] copy = new ItemStack[itemStacks.length];
        System.arraycopy(itemStacks, 0, copy, 0, itemStacks.length);
        for (int i = 0; i < copy.length; i++) {
            copy[i] = trimItem(copy[i]);
        }

        return copy;
    }

    @Contract(pure = true, value = "null -> null")
    @Nullable
    public static ItemStack trimItem(@Nullable ItemStack itemStack) {
        if (itemStack == null || isFakeItem(itemStack)) return null;
        return itemStack;
    }

    /**
     * Checks if an ItemStack is a fake item used in the RTS module.
     */
    public static boolean isFakeItem(@Nullable ItemStack itemStack) {
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            return itemStack.getItemMeta().getPersistentDataContainer()
                .get(FAKE_ITEM_KEY, PersistentDataType.STRING) != null;
        }
        return false;
    }

    /**
     * Handles the event when an RTS is opened for a player.
     *
     * @param event the OpenRTSEvent to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onOpenRTS(RTSEvents.OpenRTSEvent event) {
        Player player = event.getPlayer();
        Debug.debug("[RTS] Opening for " + player.getName());
        openingPlayers.add(player);
        RTSSearchGroup.RTS_ANVIL_INVENTORIES.put(player, event.getOpeningInventory());
        RTSSearchGroup.RTS_PAGES.put(player, 1);
        JustEnoughGuide.getInstance().getRtsBackpackManager().saveInventoryBackupFor(player);
        JustEnoughGuide.getInstance().getRtsBackpackManager().clearInventoryFor(player);
        ItemStack[] itemStacks = new ItemStack[36];
        for (int i = 0; i < 36; i++) {
            itemStacks[i] = RTSSearchGroup.PLACEHOLDER.clone();
        }
        player.getInventory().setStorageContents(itemStacks);
    }

    /**
     * Handles the event when the search term changes in the RTS system.
     *
     * @param event the SearchTermChangeEvent to handle
     */
    @EventHandler
    public void onRTS(RTSEvents.SearchTermChangeEvent event) {
        Player player = event.getPlayer();
        Debug.debug("[RTS] Searching for " + player.getName());
        SlimefunGuideImplementation implementation = GuideUtil.getLastGuide(player);
        SearchGroup searchGroup = new SearchGroup(
            implementation,
            player,
            event.getNewSearchTerm(),
            JustEnoughGuide.getConfigManager().isPinyinSearch(),
            true
        );
        if (!isRTSPlayer(player)) {
            tryQuitRTS(player);
            return;
        }

        RTSSearchGroup.RTS_SEARCH_GROUPS.put(player, searchGroup);
        RTSSearchGroup.RTS_PAGES.put(player, 1);

        int page = RTSSearchGroup.RTS_PAGES.get(player);
        for (int i = 0; i < FILL_ORDER.length; i++) {
            int index = i + page * FILL_ORDER.length - FILL_ORDER.length;
            if (index < searchGroup.slimefunItemList.size()) {
                SlimefunItem slimefunItem = searchGroup.slimefunItemList.get(index);
                ItemStack fake = getFakeItem(slimefunItem, player);
                player.getInventory().setItem(FILL_ORDER[i], fake);
            } else {
                player.getInventory().setItem(FILL_ORDER[i], RTSSearchGroup.PLACEHOLDER.clone());
            }
        }
        /*
         * Page buttons' icons.
         * For page buttons' click handler see RTSSearchGroup
         */
        AnvilMenu anvilMenu = AnvilMenu.getMenu(player);
        if (anvilMenu == null) {
            tryQuitRTS(player);
            return;
        }
        anvilMenu.setGuiItem(
            1,
            ChestMenuUtils.getPreviousButton(
                player, page, (searchGroup.slimefunItemList.size() - 1) / FILL_ORDER.length + 1)
        );
        anvilMenu.setGuiItem(
            2,
            ChestMenuUtils.getNextButton(
                player, page, (searchGroup.slimefunItemList.size() - 1) / FILL_ORDER.length + 1)
        );
    }

    /**
     * Creates a fake ItemStack for a SlimefunItem to display in the RTS inventory.
     *
     * @param slimefunItem the SlimefunItem to create a fake item for
     * @param player       the player for whom the fake item is created
     * @return the fake ItemStack, or null if the SlimefunItem or player is null
     */
    @Contract("null, _ -> null; _, null -> null; !null, !null -> !null")
    @Nullable
    public ItemStack getFakeItem(@Nullable SlimefunItem slimefunItem, @Nullable Player player) {
        if (slimefunItem == null || player == null) {
            return null;
        }

        ItemStack legacy = slimefunItem.getItem();
        Material material = legacy.getType();
        ItemStack itemStack;
        if (material == Material.PLAYER_HEAD || material == Material.PLAYER_WALL_HEAD) {
            String hash = getHash(legacy);
            if (hash != null) {
                itemStack = PlayerHead.getItemStack(PlayerSkin.fromHashCode(hash));
            } else {
                itemStack = new ItemStack(material);
            }
        } else {
            itemStack = new ItemStack(material);
        }
        itemStack.setAmount(legacy.getAmount());

        ItemMeta legacyMeta = legacy.getItemMeta();
        ItemMeta meta = itemStack.getItemMeta();

        ItemGroup itemGroup = slimefunItem.getItemGroup();
        List<String> additionLore = List.of(
            "",
            ChatColor.DARK_GRAY + "\u21E8 " + ChatColor.WHITE
                + (LocalHelper.getAddonName(itemGroup, slimefunItem.getId())) + ChatColor.WHITE + " - "
                + LocalHelper.getDisplayName(itemGroup, player)
        );
        if (legacyMeta.hasLore() && legacyMeta.getLore() != null) {
            List<String> lore = legacyMeta.getLore();
            lore.addAll(additionLore);
            meta.setLore(lore);
        } else {
            meta.setLore(additionLore);
        }

        meta.addItemFlags(
            ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, JEGVersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP);

        meta.getPersistentDataContainer().set(FAKE_ITEM_KEY, PersistentDataType.STRING, slimefunItem.getId());

        if (legacyMeta.hasDisplayName()) {
            String name = legacyMeta.getDisplayName();
            meta.setDisplayName(" " + name + " ");
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @EventHandler(ignoreCancelled = true)
    public void onRTSPageChange(RTSEvents.PageChangeEvent event) {
        Player player = event.getPlayer();
        Debug.debug("[RTS] Changing page for " + player.getName());
        int page = event.getNewPage();
        SearchGroup searchGroup = RTSSearchGroup.RTS_SEARCH_GROUPS.get(player);
        if (searchGroup == null) return;

        for (int i = 0; i < FILL_ORDER.length; i++) {
            int index = i + page * FILL_ORDER.length - FILL_ORDER.length;
            if (index < searchGroup.slimefunItemList.size()) {
                SlimefunItem slimefunItem = searchGroup.slimefunItemList.get(index);
                ItemStack fake = getFakeItem(slimefunItem, player);

                player.getInventory().setItem(FILL_ORDER[i], fake);
            } else {
                player.getInventory().setItem(FILL_ORDER[i], RTSSearchGroup.PLACEHOLDER.clone());
            }
        }
        int pages = (searchGroup.slimefunItemList.size() - 1) / FILL_ORDER.length + 1;
        AnvilInventory anvilInventory = event.getOpeningInventory();
        anvilInventory.setItem(1, ChestMenuUtils.getPreviousButton(player, page, pages));
        anvilInventory.setItem(2, ChestMenuUtils.getNextButton(player, page, pages));
    }

    /**
     * Restores the player's inventory when they join the server.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void restore(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        tryQuitRTS(player);
    }

    /**
     * Restores the player's inventory when they respawn.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void restore(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        tryQuitRTS(player);
    }

    /**
     * Quits the RTS mode for a player when they die and keeps their inventory.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (isRTSPlayer(player)) {
            tryQuitRTS(player);
            event.setKeepInventory(true); // intentionally
            event.getDrops().clear();
        }
    }

    /**
     * Cancels player interactions when they are in RTS mode.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
            return;
        }

        handleFakeItem(event.getItem(), player, event);
    }

    /**
     * Cancels the event when a player tries to drop an item while in RTS mode.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
            return;
        }

        handleFakeItem(event.getItemDrop().getItemStack(), player, event);
    }

    /**
     * Cancels the event when a player tries to place a block while in RTS mode.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
            return;
        }

        handleFakeItem(event.getItemInHand(), player, event);
    }

    /**
     * Cancels the event when a player tries to swap items between hands while in RTS mode.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
            return;
        }

        handleFakeItem(event.getMainHandItem(), player, event);
        handleFakeItem(event.getOffHandItem(), player, event);
    }

    /**
     * Cancels the event when a player sends a chat message while in RTS mode.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels the event when a player tries to execute a command while in RTS mode.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels the event when a player tries to manipulate an armor stand while in RTS mode.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels the event when a player sends a chat message while in RTS mode.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels the event when a player consumes an item while in RTS mode.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArmor(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
            return;
        }
        handleFakeItem(event.getItem(), player, event);
    }

    /**
     * Cancels the event when a player clicks on an item in an inventory while not in RTS mode.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!isRTSPlayer(player)) {
            handleFakeItem(event.getCurrentItem(), player, event);
        }
    }

    /**
     * Cancels the event when a player picks up an item while in RTS mode.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player && isRTSPlayer(player)) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels the event when a player interacts with an entity while in RTS mode.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
            return;
        }

        handleFakeItem(player.getInventory().getItem(event.getHand()), player, event);
    }

    public void handleFakeItem(@Nullable ItemStack itemStack, Player player, Cancellable event) {
        if (isFakeItem(itemStack) && !player.isOp() && player.getGameMode() != GameMode.CREATIVE) {
            itemStack.setType(Material.AIR);
            itemStack.setAmount(0);
            event.setCancelled(true);
        }
    }
}
