package com.balugaq.jeg.core.listeners;

import com.balugaq.jeg.api.groups.RTSSearchGroup;
import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.objects.events.RTSEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.guide.CheatGuideImplementation;
import com.balugaq.jeg.implementation.guide.SurvivalGuideImplementation;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.JEGVersionedItemFlag;
import com.balugaq.jeg.utils.LocalHelper;
import com.balugaq.jeg.utils.SlimefunOfficialSupporter;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
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
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("deprecation")
@Getter
public class RTSListener implements Listener {
    public static final NamespacedKey FAKE_ITEM_KEY = new NamespacedKey(JustEnoughGuide.getInstance(), "fake_item");
    public static final NamespacedKey CHEAT_AMOUNT_KEY = new NamespacedKey(JustEnoughGuide.getInstance(), "cheat_amount");
    // Use openingPlayers must be by keyword "synchronized"
    public static final Map<Player, SlimefunGuideMode> openingPlayers = new HashMap<>();
    public static final Map<Player, List<ItemStack>> cheatItems = new HashMap<>();
    public static final Integer[] FILL_ORDER = {9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};

    public static boolean isRTSPlayer(Player player) {
        return openingPlayers.containsKey(player);
    }

    public static boolean isFakeItem(ItemStack itemStack) {
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            if (itemStack.getItemMeta().getPersistentDataContainer().get(FAKE_ITEM_KEY, PersistentDataType.STRING) != null) {
                return true;
            }
        }
        return false;
    }

    public static void quitRTS(Player player) {
        if (isRTSPlayer(player)) {
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
            if (cheatItems.containsKey(player)) {
                if (player.isOp() || player.hasPermission("slimefun.cheat.items")) {
                    List<ItemStack> items = cheatItems.get(player);
                    for (ItemStack item : items) {
                        player.getInventory().addItem(item);
                    }
                    cheatItems.remove(player);
                } else {
                    cheatItems.remove(player);
                }
            }
        }
    }

    @EventHandler
    public void onOpenRTS(RTSEvents.OpenRTSEvent event) {
        Player player = event.getPlayer();
        synchronized (openingPlayers) {
            openingPlayers.put(player, event.getGuideMode());
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

        String presetSearchTerm = event.getPresetSearchTerm();
        if (presetSearchTerm != null) {
            synchronized (RTSSearchGroup.RTS_SEARCH_TERMS) {
                RTSSearchGroup.RTS_SEARCH_TERMS.put(player, presetSearchTerm);
            }
            RTSEvents.SearchTermChangeEvent e = new RTSEvents.SearchTermChangeEvent(player, player.getOpenInventory(), event.getOpeningInventory(), null, presetSearchTerm, event.getGuideMode());
            Bukkit.getPluginManager().callEvent(e);
        }
    }

    @EventHandler
    public void onRTS(RTSEvents.SearchTermChangeEvent event) {
        Player player = event.getPlayer();
        SlimefunGuideImplementation implementation = Slimefun.getRegistry().getSlimefunGuide(event.getGuideMode());
        SearchGroup searchGroup = new SearchGroup(implementation, player, event.getNewSearchTerm(), false, true);
        if (isRTSPlayer(player)) {
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
                    ItemStack itemStack = ItemStackUtil.getCleanItem(new CustomItemStack(SlimefunOfficialSupporter.translateItem(player, slimefunItem.getItem()), meta -> {
                        ItemGroup itemGroup = slimefunItem.getItemGroup();
                        List<String> additionLore = List.of(
                                "",
                                ChatColor.DARK_GRAY + "\u21E8 " + ChatColor.WHITE
                                        + (LocalHelper.getAddonName(itemGroup, slimefunItem.getId())) + " - "
                                        + itemGroup.getDisplayName(player));
                        if (meta.hasLore() && meta.getLore() != null) {
                            List<String> lore = meta.getLore();
                            lore.addAll(additionLore);
                            meta.setLore(lore);
                        } else {
                            meta.setLore(additionLore);
                        }

                        meta.addItemFlags(
                                ItemFlag.HIDE_ATTRIBUTES,
                                ItemFlag.HIDE_ENCHANTS,
                                JEGVersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP);

                        Set<NamespacedKey> keys = new HashSet<>(meta.getPersistentDataContainer().getKeys());
                        for (NamespacedKey key : keys) {
                            meta.getPersistentDataContainer().remove(key);
                        }
                        meta.getPersistentDataContainer().set(FAKE_ITEM_KEY, PersistentDataType.STRING, slimefunItem.getId());

                        if (meta.hasDisplayName()) {
                            String name = meta.getDisplayName();
                            meta.setDisplayName(" " + name + " ");
                        }
                    }));
                    player.getInventory().setItem(FILL_ORDER[i], itemStack);
                } else {
                    player.getInventory().setItem(FILL_ORDER[i], RTSSearchGroup.PLACEHOLDER.clone());
                }
            }
            /**
             * Page buttons' icons.
             * For page buttons' click handler see {@link SurvivalGuideImplementation#createHeader(Player, PlayerProfile, ChestMenu)}
             * or {@link CheatGuideImplementation#createHeader(Player, PlayerProfile, ChestMenu)}
             */
            AnvilInventory anvilInventory = event.getOpeningInventory();
            anvilInventory.setItem(1, ChestMenuUtils.getPreviousButton(player, page, (searchGroup.slimefunItemList.size() - 1) / FILL_ORDER.length + 1));
            anvilInventory.setItem(2, ChestMenuUtils.getNextButton(player, page, (searchGroup.slimefunItemList.size() - 1) / FILL_ORDER.length + 1));
        }
    }

    @EventHandler
    public void onRTSPageChange(RTSEvents.PageChangeEvent event) {
        Player player = event.getPlayer();
        int page = event.getNewPage();
        SearchGroup searchGroup = RTSSearchGroup.RTS_SEARCH_GROUPS.get(player);
        if (searchGroup != null) {
            for (int i = 0; i < FILL_ORDER.length; i++) {
                int index = i + page * FILL_ORDER.length - FILL_ORDER.length;
                if (index < searchGroup.slimefunItemList.size()) {
                    SlimefunItem slimefunItem = searchGroup.slimefunItemList.get(index);
                    ItemStack itemStack = ItemStackUtil.getCleanItem(new CustomItemStack(SlimefunOfficialSupporter.translateItem(player, slimefunItem.getItem()), meta -> {
                        ItemGroup itemGroup = slimefunItem.getItemGroup();
                        List<String> additionLore = List.of(
                                "",
                                ChatColor.DARK_GRAY + "\u21E8 " + ChatColor.WHITE
                                        + (LocalHelper.getAddonName(itemGroup, slimefunItem.getId())) + " - "
                                        + itemGroup.getDisplayName(player));
                        if (meta.hasLore() && meta.getLore() != null) {
                            List<String> lore = meta.getLore();
                            lore.addAll(additionLore);
                            meta.setLore(lore);
                        } else {
                            meta.setLore(additionLore);
                        }

                        meta.addItemFlags(
                                ItemFlag.HIDE_ATTRIBUTES,
                                ItemFlag.HIDE_ENCHANTS,
                                JEGVersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP);

                        Set<NamespacedKey> keys = new HashSet<>(meta.getPersistentDataContainer().getKeys());
                        for (NamespacedKey key : keys) {
                            meta.getPersistentDataContainer().remove(key);
                        }
                        meta.getPersistentDataContainer().set(FAKE_ITEM_KEY, PersistentDataType.STRING, slimefunItem.getId());

                        if (meta.hasDisplayName()) {
                            String name = meta.getDisplayName();
                            meta.setDisplayName(" " + name + " ");
                        }
                    }));

                    player.getInventory().setItem(FILL_ORDER[i], itemStack);
                } else {
                    player.getInventory().setItem(FILL_ORDER[i], RTSSearchGroup.PLACEHOLDER.clone());
                }
            }
            AnvilInventory anvilInventory = RTSSearchGroup.RTS_PLAYERS.get(player);
            anvilInventory.setItem(1, ChestMenuUtils.getPreviousButton(player, page, (searchGroup.slimefunItemList.size() - 1) / FILL_ORDER.length + 1));
            anvilInventory.setItem(2, ChestMenuUtils.getNextButton(player, page, (searchGroup.slimefunItemList.size() - 1) / FILL_ORDER.length + 1));
        }
    }

    @EventHandler
    public void onCloseRTS(RTSEvents.CloseRTSEvent event) {
        Player player = event.getPlayer();
        quitRTS(player);
    }

    @EventHandler
    public void restore(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        quitRTS(player);
        ItemStack[] itemStacks = player.getInventory().getContents();
        for (ItemStack itemStack : itemStacks) {
            if (isFakeItem(itemStack)) {
                itemStack.setAmount(0);
                itemStack.setType(Material.AIR);
            }
        }
        player.getInventory().setContents(itemStacks);
    }

    @EventHandler
    public void restore(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            quitRTS(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            quitRTS(player);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (isRTSPlayer(player)) {
            quitRTS(player);
            event.setKeepInventory(true);
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if (isRTSPlayer(player)) {
            quitRTS(player);
        }
    }

    @EventHandler
    public void onLookup(InventoryClickEvent event) {
        Player player = (Player) event.getView().getPlayer();
        if (isRTSPlayer(player)) {
            InventoryAction action = event.getAction();
            if (action == InventoryAction.PICKUP_ONE || action == InventoryAction.PICKUP_HALF || action == InventoryAction.PICKUP_ALL || action == InventoryAction.PICKUP_SOME) {
                ItemStack itemStack = event.getCurrentItem();
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    return;
                }

                SlimefunGuideMode mode = openingPlayers.get(player);
                SlimefunGuideImplementation implementation = Slimefun.getRegistry().getSlimefunGuide(mode);
                PlayerProfile profile = PlayerProfile.find(player).orElse(null);
                if (profile != null) {
                    SlimefunItem slimefunItem = SlimefunItem.getById(itemStack.getItemMeta().getPersistentDataContainer().get(FAKE_ITEM_KEY, PersistentDataType.STRING));
                    if (slimefunItem == null) {
                        event.setCancelled(true);
                        return;
                    }

                    if (mode == SlimefunGuideMode.SURVIVAL_MODE) {
                        RTSSearchGroup back = new RTSSearchGroup(RTSSearchGroup.RTS_PLAYERS.get(player), RTSSearchGroup.RTS_SEARCH_TERMS.get(player), RTSSearchGroup.RTS_PAGES.get(player));
                        profile.getGuideHistory().add(back, 1);
                        implementation.displayItem(profile, slimefunItem, true);
                        quitRTS(player);
                    }
                    else if (mode == SlimefunGuideMode.CHEAT_MODE) {
                        if (player.isOp() || player.hasPermission("slimefun.cheat.items")) {
                            if (slimefunItem instanceof MultiBlockMachine) {
                                Slimefun.getLocalization().sendMessage(player, "guide.cheat.no-multiblocks");
                            } else {
                                ItemStack clonedItem = slimefunItem.getItem().clone();

                                int addAmount = clonedItem.getMaxStackSize();
                                clonedItem.setAmount(addAmount);

                                cheatItems.putIfAbsent(player, new ArrayList<>());
                                cheatItems.get(player).add(clonedItem);

                                ItemMeta meta = itemStack.getItemMeta();
                                int originalAmount = meta.getPersistentDataContainer().getOrDefault(CHEAT_AMOUNT_KEY, PersistentDataType.INTEGER, 0);
                                int totalAmount = originalAmount + addAmount;
                                meta.getPersistentDataContainer().set(CHEAT_AMOUNT_KEY, PersistentDataType.INTEGER, totalAmount);
                                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ItemUtils.getItemName(clonedItem) + " &cTaken x" + totalAmount));
                                itemStack.setItemMeta(meta);
                            }
                        } else {
                            Slimefun.getLocalization().sendMessage(player, "messages.no-permission", true);
                        }
                    }
                }
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
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

    @EventHandler
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

    @EventHandler
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

    @EventHandler
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

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (isRTSPlayer(player)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
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
}
