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

import com.balugaq.jeg.api.groups.RTSSearchGroup;
import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.multiblock.MultiBlockBuilder;
import com.balugaq.jeg.api.objects.events.RTSEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.items.MultiBlockBuilderItem;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.JEGVersionedItemFlag;
import com.balugaq.jeg.utils.LocalHelper;
import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handle events related to the Real-Time Search (RTS) mode, consisting of
 * 1) Handling RTS anvil inventory
 * 2) Handling fake items interactions
 * 3) Restoring player inventory items
 *
 * @author balugaq
 * @since 1.4
 */
@SuppressWarnings({"deprecation", "UnnecessaryUnicodeEscape", "ConstantValue"})
@Getter
@NullMarked
public class RTSListener implements Listener {
    public static final NamespacedKey FAKE_ITEM_KEY = new NamespacedKey(JustEnoughGuide.getInstance(), "fake_item");
    public static final NamespacedKey CHEAT_AMOUNT_KEY =
        new NamespacedKey(JustEnoughGuide.getInstance(), "cheat_amount");
    // Use openingPlayers must be by keyword "synchronized"
    public static final Map<Player, SlimefunGuideMode> openingPlayers = new HashMap<>();
    public static final Map<Player, List<ItemStack>> cheatItems = new HashMap<>();
    public static final Integer[] FILL_ORDER = {
        9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35
    };

    /**
     * Handles the event when an RTS is opened for a player.
     *
     * @param event
     *         the OpenRTSEvent to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onOpenRTS(RTSEvents.OpenRTSEvent event) {
        Player player = event.getPlayer();
        Debug.debug("[RTS] Opening for " + player.getName());
        synchronized (openingPlayers) {
            openingPlayers.put(player, GuideUtil.getLastGuideMode(player));
        }
        synchronized (RTSSearchGroup.RTS_PLAYERS) {
            RTSSearchGroup.RTS_PLAYERS.put(player, event.getOpeningInventory());
        }
        synchronized (RTSSearchGroup.RTS_PAGES) {
            RTSSearchGroup.RTS_PAGES.put(player, 1);
        }
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
     * @param event
     *         the SearchTermChangeEvent to handle
     */
    @EventHandler
    public void onRTS(RTSEvents.SearchTermChangeEvent event) {
        Player player = event.getPlayer();
        Debug.debug("[RTS] Searching for " + player.getName());
        SlimefunGuideImplementation implementation = GuideUtil.getSlimefunGuide(event.getGuideMode());
        SearchGroup searchGroup = new SearchGroup(
            implementation,
            player,
            event.getNewSearchTerm(),
            JustEnoughGuide.getConfigManager().isPinyinSearch(),
            true
        );
        if (!isRTSPlayer(player)) {
            return;
        }
        synchronized (RTSSearchGroup.RTS_SEARCH_GROUPS) {
            RTSSearchGroup.RTS_SEARCH_GROUPS.put(player, searchGroup);
        }

        synchronized (RTSSearchGroup.RTS_PAGES) {
            RTSSearchGroup.RTS_PAGES.put(player, 1);
        }

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
         * For page buttons' click handler see {@link SurvivalGuideImplementation#createHeader(Player,
         * PlayerProfile, ChestMenu)}
         */
        AnvilInventory anvilInventory = event.getOpeningInventory();
        anvilInventory.setItem(
            1,
            ChestMenuUtils.getPreviousButton(
                player, page, (searchGroup.slimefunItemList.size() - 1) / FILL_ORDER.length + 1)
        );
        anvilInventory.setItem(
            2,
            ChestMenuUtils.getNextButton(
                player, page, (searchGroup.slimefunItemList.size() - 1) / FILL_ORDER.length + 1)
        );
    }

    /**
     * Checks if a player is currently in the RTS (Real-Time Search) mode.
     *
     * @param player
     *         the player to check
     *
     * @return true if the player is in RTS mode, false otherwise
     */
    public static boolean isRTSPlayer(Player player) {
        return openingPlayers.containsKey(player);
    }

    /**
     * Creates a fake ItemStack for a SlimefunItem to display in the RTS inventory.
     *
     * @param slimefunItem
     *         the SlimefunItem to create a fake item for
     * @param player
     *         the player for whom the fake item is created
     *
     * @return the fake ItemStack, or null if the SlimefunItem or player is null
     */
    @Contract("null, _ -> null; _, null -> null; !null, !null -> !null")
    @UnknownNullability
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

    /**
     * Generates a unique hash for a player head ItemStack.
     *
     * @param item
     *         the ItemStack to generate a hash for
     *
     * @return the hash of the player head, or null if the item is not a player head
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
     * Handles the event when the page changes in the RTS system.
     *
     * @param event
     *         the PageChangeEvent to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onRTSPageChange(RTSEvents.PageChangeEvent event) {
        Player player = event.getPlayer();
        Debug.debug("[RTS] Changing page for " + player.getName());
        int page = event.getNewPage();
        SearchGroup searchGroup = RTSSearchGroup.RTS_SEARCH_GROUPS.get(player);
        if (searchGroup != null) {
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
            AnvilInventory anvilInventory = RTSSearchGroup.RTS_PLAYERS.get(player);
            anvilInventory.setItem(
                1,
                ChestMenuUtils.getPreviousButton(
                    player, page, (searchGroup.slimefunItemList.size() - 1) / FILL_ORDER.length + 1)
            );
            anvilInventory.setItem(
                2,
                ChestMenuUtils.getNextButton(
                    player, page, (searchGroup.slimefunItemList.size() - 1) / FILL_ORDER.length + 1)
            );
        }
    }

    /**
     * Handles the event when an RTS is closed.
     *
     * @param event
     *         the CloseRTSEvent to handle
     */
    @EventHandler
    public void onCloseRTS(RTSEvents.CloseRTSEvent event) {
        Player player = event.getPlayer();
        tryQuitRTS(player);
    }

    /**
     * Quits the RTS mode for a player and restores their inventory.
     *
     * @param player
     *         the player to quit RTS mode
     */
    public static void tryQuitRTS(Player player) {
        if (!isRTSPlayer(player)) {
            return;
        }

        synchronized (openingPlayers) {
            openingPlayers.remove(player);
        }
        synchronized (RTSSearchGroup.RTS_PLAYERS) {
            RTSSearchGroup.RTS_PLAYERS.remove(player);
        }
        synchronized (RTSSearchGroup.RTS_SEARCH_TERMS) {
            RTSSearchGroup.RTS_SEARCH_TERMS.remove(player);
        }
        synchronized (RTSSearchGroup.RTS_SEARCH_GROUPS) {
            RTSSearchGroup.RTS_SEARCH_GROUPS.remove(player);
        }
        synchronized (RTSSearchGroup.RTS_PAGES) {
            RTSSearchGroup.RTS_PAGES.remove(player);
        }
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
    }

    /**
     * Restores the player's inventory when they join the server.
     *
     * @param event
     *         the PlayerJoinEvent to handle
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void restore(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        tryQuitRTS(player);
    }

    @Contract(pure = true, value = "null -> null; !null -> !null")
    public static @Nullable ItemStack @Nullable [] trimItems(@Nullable ItemStack @Nullable[] itemStacks) {
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
        if (itemStack == null) return null;

        if (isFakeItem(itemStack)) {
            return null;
        }

        return itemStack;
    }

    /**
     * Checks if an ItemStack is a fake item used in the RTS system.
     *
     * @param itemStack
     *         the ItemStack to check
     *
     * @return true if the itemStack is a fake item, false otherwise
     */
    public static boolean isFakeItem(@Nullable ItemStack itemStack) {
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            return itemStack.getItemMeta().getPersistentDataContainer().get(FAKE_ITEM_KEY, PersistentDataType.STRING)
                != null;
        }
        return false;
    }

    /**
     * Restores the player's inventory when they respawn.
     *
     * @param event
     *         the PlayerRespawnEvent to handle
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void restore(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        tryQuitRTS(player);
    }

    /**
     * Quits the RTS mode for a player when they quit the server.
     *
     * @param event
     *         the PlayerQuitEvent to handle
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        tryQuitRTS(player);
    }

    /**
     * Quits the RTS mode for a player when they die and keeps their inventory.
     *
     * @param event
     *         the PlayerDeathEvent to handle
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (isRTSPlayer(player)) {
            tryQuitRTS(player);
            event.setKeepInventory(true);
            event.getDrops().clear();
        }
    }

    /**
     * Quits the RTS mode for a player when they open an inventory.
     *
     * @param event
     *         the InventoryOpenEvent to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onOpenInventory(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        tryQuitRTS(player);
    }

    /**
     * Handles the event when a player clicks on an item in the RTS inventory.
     *
     * @param event
     *         the InventoryClickEvent to handle
     */
    @SuppressWarnings("DataFlowIssue")
    @EventHandler
    public void onLookup(InventoryClickEvent event) {
        Player player = (Player) ReflectionUtil.invokeMethod(event.getView(), "getPlayer");
        if (isRTSPlayer(player)) {
            InventoryAction action = event.getAction();
            if (action == InventoryAction.PICKUP_ONE
                || action == InventoryAction.PICKUP_HALF
                || action == InventoryAction.PICKUP_ALL
                || action == InventoryAction.PICKUP_SOME) {
                ItemStack itemStack = event.getCurrentItem();
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    return;
                }

                SlimefunGuideMode mode = openingPlayers.get(player);
                SlimefunGuideImplementation implementation =
                    GuideUtil.getSlimefunGuide(mode);
                PlayerProfile profile = PlayerProfile.find(player).orElse(null);
                if (profile != null) {
                    SlimefunItem slimefunItem = SlimefunItem.getById(itemStack
                        .getItemMeta()
                        .getPersistentDataContainer()
                        .get(
                            FAKE_ITEM_KEY,
                            PersistentDataType.STRING
                        ));
                    if (slimefunItem == null) {
                        event.setCancelled(true);
                        return;
                    }

                    if (mode == SlimefunGuideMode.SURVIVAL_MODE) {
                        RTSSearchGroup back = new RTSSearchGroup(
                            RTSSearchGroup.RTS_PLAYERS.get(player),
                            RTSSearchGroup.RTS_SEARCH_TERMS.get(player),
                            RTSSearchGroup.RTS_PAGES.get(player)
                        );
                        profile.getGuideHistory().add(back, 1);
                        implementation.displayItem(profile, slimefunItem, true);
                        tryQuitRTS(player);
                    } else if (mode == SlimefunGuideMode.CHEAT_MODE) {
                        if (player.isOp() || player.hasPermission("slimefun.cheat.items")) {
                            ItemStack clonedItem = MultiBlockBuilder.getItem(slimefunItem);

                            int addAmount = clonedItem.getMaxStackSize();
                            clonedItem.setAmount(addAmount);

                            cheatItems.putIfAbsent(player, new ArrayList<>());
                            cheatItems.get(player).add(clonedItem);

                            ItemMeta meta = itemStack.getItemMeta();
                            int originalAmount = meta.getPersistentDataContainer()
                                .getOrDefault(CHEAT_AMOUNT_KEY, PersistentDataType.INTEGER, 0);
                            int totalAmount = originalAmount + addAmount;
                            meta.getPersistentDataContainer()
                                .set(CHEAT_AMOUNT_KEY, PersistentDataType.INTEGER, totalAmount);
                            meta.setDisplayName(ChatColors.color(
                                ItemStackHelper.getDisplayName(clonedItem) + " &c已拿取物品 x" + totalAmount));
                            itemStack.setItemMeta(meta);
                        } else {
                            Slimefun.getLocalization().sendMessage(player, "messages.no-permission", true);
                        }
                    }
                }
            }

            event.setCancelled(true);
        }
    }

    /**
     * Cancels player interactions when they are in RTS mode.
     *
     * @param event
     *         the PlayerInteractEvent to handle
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
            return;
        }

        ItemStack itemStack = event.getItem();
        if (isFakeItem(itemStack)) {
            if (!player.isOp() && player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Cancels the event when a player tries to drop an item while in RTS mode.
     *
     * @param event
     *         the PlayerDropItemEvent to handle
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
            return;
        }

        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (isFakeItem(itemStack)) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels the event when a player tries to place a block while in RTS mode.
     *
     * @param event
     *         the BlockPlaceEvent to handle
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
            return;
        }

        ItemStack itemStack = event.getItemInHand();
        if (isFakeItem(itemStack)) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels the event when a player tries to swap items between hands while in RTS mode.
     *
     * @param event
     *         the PlayerSwapHandItemsEvent to handle
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
            return;
        }

        ItemStack itemStack = event.getMainHandItem();
        if (isFakeItem(itemStack)) {
            event.setCancelled(true);
            return;
        }

        ItemStack itemStack2 = event.getOffHandItem();
        if (isFakeItem(itemStack2)) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels the event when a player sends a chat message while in RTS mode.
     *
     * @param event
     *         the AsyncPlayerChatEvent to handle
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
     *
     * @param event
     *         the PlayerCommandPreprocessEvent to handle
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
     *
     * @param event
     *         the PlayerArmorStandManipulateEvent to handle
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
     *
     * @param event
     *         the PlayerChatEvent to handle
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
     *
     * @param event
     *         the PlayerItemConsumeEvent to handle
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArmor(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
            return;
        }
        ItemStack itemStack = event.getItem();
        if (isFakeItem(itemStack)) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels the event when a player clicks on an item in an inventory while not in RTS mode.
     *
     * @param event
     *         the InventoryClickEvent to handle
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!isRTSPlayer(player)) {
            ItemStack itemStack = event.getCurrentItem();
            if (isFakeItem(itemStack)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Cancels the event when a player picks up an item while in RTS mode.
     *
     * @param event
     *         the EntityPickupItemEvent to handle
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (isRTSPlayer(player)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Cancels the event when a player right-clicks while in RTS mode.
     *
     * @param event
     *         the PlayerRightClickEvent to handle
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onRightClick(PlayerRightClickEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.cancel();
        } else {
            ItemStack itemStack = event.getItem();
            if (isFakeItem(itemStack)) {
                event.cancel();
            }
        }
    }

    /**
     * Cancels the event when a player interacts with an entity while in RTS mode.
     *
     * @param event
     *         the PlayerInteractEntityEvent to handle
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
        } else {
            ItemStack itemStack = player.getInventory().getItem(event.getHand());
            if (isFakeItem(itemStack)) {
                event.setCancelled(true);
            }
        }
    }
}