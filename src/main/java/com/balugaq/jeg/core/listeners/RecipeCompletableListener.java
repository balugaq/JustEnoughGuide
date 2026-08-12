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

import com.balugaq.jeg.api.objects.collection.Pair;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.enums.RecipeCompleteOpenMode;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.api.objects.events.PatchEvent;
import com.balugaq.jeg.api.objects.events.RecipeCompleteEvents;
import com.balugaq.jeg.api.patches.JEGGuideHistory;
import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.api.recipe_complete.source.base.Source;
import com.balugaq.jeg.core.integrations.ItemPatchListener;
import com.balugaq.jeg.core.integrations.justenoughguide.ShulkerBoxPlayerInventoryItemSeeker;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.items.ItemsSetup;
import com.balugaq.jeg.implementation.option.RecipeCompleteOpenModeGuideOption;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.StackUtils;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"unused", "ConstantValue", "removal"})
@NullMarked
public class RecipeCompletableListener implements ItemPatchListener {
    public static final NamespacedKey RECIPE_COMPLETE_EXIT_KEY = KeyUtil.newKey("recipe_complete_exit");
    public static final int[] DISPENSER_SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    public static final ConcurrentHashMap<UUID, GuideEvents.ItemButtonClickEvent> LAST_EVENTS =
        new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<UUID, GuideHistory> GUIDE_HISTORY = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<UUID, BiConsumer<GuideEvents.ItemButtonClickEvent, PlayerProfile>> PROFILE_CALLBACKS =
        new ConcurrentHashMap<>();
    public static final Set<UUID> listening = ConcurrentHashMap.newKeySet();
    public static final ConcurrentHashMap<SlimefunItem, Pair<int[], Boolean>> INGREDIENT_SLOTS =
        new ConcurrentHashMap<>();
    public static final ArrayList<SlimefunItem> NOT_APPLICABLE_ITEMS = new ArrayList<>();
    public static final ConcurrentHashMap<UUID, Location> DISPENSER_LISTENING = new ConcurrentHashMap<>();
    public static final NamespacedKey LAST_RECIPE_COMPLETE_KEY = KeyUtil.newKey("last_recipe_complete");
    public static final ConcurrentHashMap<UUID, ArrayList<ItemStack>> missingMaterials = new ConcurrentHashMap<>();
    public static final Map<NamespacedKey, PlayerInventoryItemSeeker> PLAYER_INVENTORY_ITEM_GETTERS = new HashMap<>();
    private static @UnknownNullability ItemStack RECIPE_COMPLETABLE_BOOK_ITEM = null;

    static {
        JustEnoughGuide.runTimerAsync(
            () -> {
                for (UUID uuid : missingMaterials.keySet()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null || !player.isOnline()) {
                        continue;
                    }

                    var v = missingMaterials.get(uuid);
                    ArrayList<ItemStack> clone;
                    if (v != null) {
                        synchronized (v) {
                            clone = new ArrayList<>(v);
                            v.clear();
                        }
                    } else {
                        clone = new ArrayList<>();
                    }

                    Map<ItemStack, Integer> map = new HashMap<>();
                    for (ItemStack item : clone) {
                        map.merge(StackUtils.getAsQuantity(item, 1), item.getAmount(), Integer::sum);
                    }

                    for (var entry : map.entrySet()) {
                        ItemStack itemStack = entry.getKey();
                        String amountString = getAmountString(entry, itemStack);
                        if (PaperLib.isPaper()) {
                            var builder = Component.text().color(NamedTextColor.RED).append(Component.text("缺少 "));
                            var itemBuilder = Component.text(ItemStackHelper.getDisplayName(itemStack));
                            SlimefunItem sf = SlimefunItem.getByItem(itemStack);
                            if (sf != null) {
                                itemBuilder = itemBuilder
                                    .hoverEvent(HoverEvent.showText(Component.text().color(NamedTextColor.YELLOW).append(Component.text("点击查看"))))
                                    .clickEvent(ClickEvent.runCommand("/jeg viewitem " + sf.getId()));
                            }
                            builder.color(NamedTextColor.GRAY).append(itemBuilder);
                            builder.append(Component.text().color(NamedTextColor.GREEN).append(Component.text(" x")).append(Component.text(amountString)));
                            player.sendMessage(builder);
                        } else {
                            player.sendMessage(ChatColors.color("&c缺少 &7" + ItemStackHelper.getDisplayName(itemStack) + " &r&ax&7" + amountString));
                        }
                    }
                }
            }, 1L, 20L
        );
    }

    private static String getAmountString(Map.Entry<ItemStack, Integer> entry, ItemStack itemStack) {
        int amount = entry.getValue();
        int stacks = amount / Math.max(1, itemStack.getMaxStackSize());
        int left = amount - stacks * Math.max(1, itemStack.getMaxStackSize());
        String amountString = "" + amount;
        if (amount > itemStack.getMaxStackSize()) {
            amountString += " ( " + stacks + " 组";
            if (left > 0) {
                amountString += " + " + left + " 个";
            }
            amountString += ")";
        }
        return amountString;
    }

    /**
     * @param slimefunItem the {@link SlimefunItem} to add
     * @see NotApplicable
     */
    public static void addNotApplicableItem(SlimefunItem slimefunItem) {
        NOT_APPLICABLE_ITEMS.add(slimefunItem);
    }

    /**
     * @param slimefunItem the {@link SlimefunItem} to remove
     * @see NotApplicable
     */
    public static void removeNotApplicableItem(SlimefunItem slimefunItem) {
        NOT_APPLICABLE_ITEMS.remove(slimefunItem);
    }

    public static void registerRecipeCompletable(SlimefunItem slimefunItem, int[] slots) {
        registerRecipeCompletable(slimefunItem, slots, false);
    }

    public static void registerRecipeCompletable(SlimefunItem slimefunItem, int[] slots, boolean unordered) {
        INGREDIENT_SLOTS.put(slimefunItem, new Pair<>(slots, unordered));
    }

    public static void unregisterRecipeCompletable(SlimefunItem slimefunItem) {
        INGREDIENT_SLOTS.remove(slimefunItem);
    }

    public static void addCallback(
        final UUID uuid, BiConsumer<GuideEvents.ItemButtonClickEvent, PlayerProfile> callback) {
        PROFILE_CALLBACKS.put(uuid, callback);
    }

    public static void removeCallback(UUID uuid) {
        PROFILE_CALLBACKS.remove(uuid);
    }

    public static boolean isRecipeCompleting(UUID uuid) {
        return PROFILE_CALLBACKS.containsKey(uuid);
    }

    public static void tagGuideOpen(Player player) {
        if (!isSelectingItemStackToRecipeComplete(player.getUniqueId())) {
            return;
        }

        PlayerProfile profile = getPlayerProfile(player);
        if (RecipeCompleteOpenModeGuideOption.instance().get(player) == RecipeCompleteOpenMode.NEW) {
            saveOriginGuideHistory(profile);
            clearGuideHistory(profile);
        }
        Debug.debug(player.getName() + " is tagged guide open");
    }

    public static PlayerProfile getPlayerProfile(OfflinePlayer player) {
        return GuideUtil.getProfile(player);
    }

    public static void saveOriginGuideHistory(PlayerProfile profile) {
        GuideHistory oldHistory = profile.getGuideHistory();
        GuideHistory newHistory = new JEGGuideHistory(profile);
        ReflectionUtil.setValue(newHistory, "mainMenuPage", oldHistory.getMainMenuPage());
        LinkedList<?> queue = ReflectionUtil.getValue(oldHistory, "queue", LinkedList.class);
        ReflectionUtil.setValue(newHistory, "queue", queue != null ? queue.clone() : new LinkedList<>());
        var p = GuideUtil.updatePlayer(profile.getUUID());
        if (p != null) GUIDE_HISTORY.put(p.getUniqueId(), newHistory);
    }

    public static void clearGuideHistory(PlayerProfile profile) {
        ReflectionUtil.setValue(profile, "guideHistory", new JEGGuideHistory(profile));
    }

    @Nullable
    public static GuideEvents.ItemButtonClickEvent getLastEvent(UUID uuid) {
        return LAST_EVENTS.get(uuid);
    }

    public static void clearLastEvent(UUID uuid) {
        LAST_EVENTS.remove(uuid);
    }

    public static void addDispenserListening(UUID uuid, Location location) {
        DISPENSER_LISTENING.put(uuid, location);
    }

    public static boolean isOpeningDispenser(UUID uuid) {
        return DISPENSER_LISTENING.containsKey(uuid);
    }

    public static void removeDispenserListening(UUID uuid) {
        DISPENSER_LISTENING.remove(uuid);
    }

    public static NamespacedKey getKey0() {
        return KeyUtil.newKey(RecipeCompletableListener.class.getSimpleName().toLowerCase(Locale.ROOT));
    }

    @SuppressWarnings("deprecation")
    private static void tryAddPlayerInventoryClickHandler(BlockMenu blockMenu) {
        SlimefunItem sf = blockMenu.getPreset().getSlimefunItem();
        if (!isApplicable(sf)) {
            return;
        }

        if (!hasIngredientSlots(sf)) {
            return;
        }

        ChestMenu.MenuClickHandler old = blockMenu.getPlayerInventoryClickHandler();
        if (old instanceof TaggedRecipeCompletable) {
            // Already modified
            return;
        }

        blockMenu.addPlayerInventoryClickHandler(
            (RecipeCompletableClickHandler) (player, slot, itemStack, clickAction) -> {
                // mixin start
                if (StackUtils.itemsMatch(itemStack, getRecipeCompletableBookItem(), false, false, false, false)
                    && blockMenu.isPlayerInventoryClickable()) {
                    if (isSelectingItemStackToRecipeComplete(player.getUniqueId())) {
                        var session = RecipeCompleteSession.getSession(player);
                        if (session == null) return false;
                        if (session.getMenu() != null && session.getMenu().getLocation().equals(blockMenu.getLocation())) {
                            GuideUtil.openGuide(player);
                            return false;
                        } else {
                            session.cancel();
                        }
                    }

                    allowSelectingItemStackToRecipeComplete(player.getUniqueId());
                    int[] slots = getIngredientSlots(sf);
                    boolean unordered = isUnordered(sf);
                    var session = RecipeCompleteSession.create(blockMenu, player, clickAction, slots, unordered, 1);
                    if (session == null) return false;
                    RecipeCompleteProvider.openSlimefun(session);

                    return false;
                }
                // mixin end

                if (old != null) {
                    return old.onClick(player, slot, itemStack, clickAction);
                }

                return true;
            });
    }

    private static void tryAddVanillaListen(InventoryOpenEvent event, Block block, Inventory inventory) {
        var p = GuideUtil.updatePlayer(event.getPlayer().getUniqueId());
        if (p == null) return;
        addDispenserListening(p.getUniqueId(), block.getLocation());
    }

    public static boolean isApplicable(SlimefunItem slimefunItem) {
        if (slimefunItem instanceof NotApplicable) {
            return false;
        }

        return !NOT_APPLICABLE_ITEMS.contains(slimefunItem);
    }

    public static boolean hasIngredientSlots(SlimefunItem slimefunItem) {
        return INGREDIENT_SLOTS.containsKey(slimefunItem);
    }

    public static ItemStack getRecipeCompletableBookItem() {
        if (RECIPE_COMPLETABLE_BOOK_ITEM == null) {
            RECIPE_COMPLETABLE_BOOK_ITEM =
                ItemsSetup.RECIPE_COMPLETE_GUIDE.getItem().clone();
        }

        return RECIPE_COMPLETABLE_BOOK_ITEM;
    }

    public static boolean isSelectingItemStackToRecipeComplete(UUID uuid) {
        return listening.contains(uuid);
    }

    public static void allowSelectingItemStackToRecipeComplete(UUID uuid) {
        Debug.debug("Allow " + uuid + " to recipe complete");
        listening.add(uuid);
    }

    public static int[] getIngredientSlots(SlimefunItem slimefunItem) {
        return Optional.ofNullable(INGREDIENT_SLOTS.get(slimefunItem))
            .orElse(new Pair<>(new int[0], false))
            .first();
    }

    public static boolean isUnordered(SlimefunItem slimefunItem) {
        return Optional.ofNullable(INGREDIENT_SLOTS.get(slimefunItem))
            .orElse(new Pair<>(new int[0], false))
            .second();
    }

    public static void exitSelectingItemStackToRecipeComplete(UUID uuid) {
        listening.remove(uuid);
    }

    public static void rollbackGuideHistory(PlayerProfile profile) {
        GuideHistory originHistory = RecipeCompletableListener.GUIDE_HISTORY.get(profile.getUUID());
        if (originHistory == null) {
            return;
        }

        ReflectionUtil.setValue(profile, "guideHistory", originHistory);
    }

    @SuppressWarnings({"deprecation", "DuplicateCondition", "ConstantValue"})
    private static void tryPatchRecipeCompleteBook(Player player, ItemStack clickedItemStack) {
        for (ItemStack itemStack : player.getInventory()) {
            if (StackUtils.itemsMatch(itemStack, getRecipeCompletableBookItem(), false, false, false, false)) {
                ItemMeta meta = itemStack.getItemMeta();
                if (meta == null) {
                    continue;
                }

                List<String> lore = meta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }

                // Patch start
                boolean applied = meta.getPersistentDataContainer().has(LAST_RECIPE_COMPLETE_KEY);
                if (lore.size() >= 7 && applied) {
                    // Remove last two lines
                    if (lore.size() >= 7) {
                        lore.removeLast();
                    }
                    if (lore.size() >= 6) {
                        lore.removeLast();
                    }
                }

                String itemName = ItemStackHelper.getDisplayName(clickedItemStack);
                lore.add("");
                lore.add(ChatColors.color("&6上次补全物品: " + itemName));

                if (!applied) {
                    meta.getPersistentDataContainer().set(LAST_RECIPE_COMPLETE_KEY, PersistentDataType.BOOLEAN, true);
                }

                // Patch end

                meta.setLore(lore);
                itemStack.setItemMeta(meta);
                return;
            }
        }
    }

    @SuppressWarnings({"deprecation", "DuplicateCondition", "ConstantValue"})
    private static void tryRemoveRecipeCompleteBookLastRecipeCompleteLore(Player player) {
        for (ItemStack itemStack : player.getInventory()) {
            if (StackUtils.itemsMatch(itemStack, getRecipeCompletableBookItem(), false, false, false, false)) {
                ItemMeta meta = itemStack.getItemMeta();
                if (meta == null) {
                    continue;
                }

                List<String> lore = meta.getLore();
                if (lore == null) {
                    continue;
                }

                // Patch start
                boolean applied = meta.getPersistentDataContainer().has(LAST_RECIPE_COMPLETE_KEY);
                if (lore.size() >= 7 && applied) {
                    // Remove last two lines
                    if (lore.size() >= 7) {
                        lore.removeLast();
                    }
                    if (lore.size() >= 6) {
                        lore.removeLast();
                    }
                }

                meta.getPersistentDataContainer().set(LAST_RECIPE_COMPLETE_KEY, PersistentDataType.BOOLEAN, false);
                // Patch end

                meta.setLore(lore);
                itemStack.setItemMeta(meta);
            }
        }
    }

    public static void registerPlayerInventoryItemGetter(PlayerInventoryItemSeeker itemGetter) {
        PLAYER_INVENTORY_ITEM_GETTERS.put(itemGetter.getKey(), itemGetter);
    }

    public static void unregisterPlayerInventoryItemGetter(NamespacedKey key) {
        PLAYER_INVENTORY_ITEM_GETTERS.remove(key);
    }

    @EventHandler
    public void prepare(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof BlockMenu blockMenu) {
            tryAddPlayerInventoryClickHandler(blockMenu);
        }

        if (event.getInventory().getHolder() instanceof Dispenser dispenser) {
            tryAddVanillaListen(event, dispenser.getBlock(), event.getInventory());
        }
    }

    @EventHandler
    public void exit(RecipeCompleteEvents.SessionCancelEvent event) {
        exitSelectingItemStackToRecipeComplete(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void exit(RecipeCompleteEvents.SessionCompleteEvent event) {
        exitSelectingItemStackToRecipeComplete(event.getPlayer().getUniqueId());
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void clickVanilla(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (event.getRawSlot() < inventory.getSize()) {
            return;
        }

        if (!(inventory.getHolder() instanceof Dispenser dispenser)) {
            return;
        }

        Player player = GuideUtil.updatePlayer(event.getWhoClicked().getUniqueId());
        if (player == null || !isOpeningDispenser(player.getUniqueId())) {
            return;
        }

        if (!StackUtils.itemsMatch(
            event.getCurrentItem(), getRecipeCompletableBookItem(), false, false, false, false)) {
            return;
        }

        Block block = dispenser.getBlock();
        ClickAction clickAction = new ClickAction(event.isRightClick(), event.isShiftClick());
        var session = RecipeCompleteSession.create(block, inventory, player, clickAction, DISPENSER_SLOTS, false, 1);
        if (session == null) return;
        RecipeCompleteProvider.openVanilla(session);

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void exitVanilla(InventoryOpenEvent event) {
        removeDispenserListening(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onJEGItemClick(GuideEvents.ItemButtonClickEvent event) {
        Player player = event.getPlayer();
        if (!isSelectingItemStackToRecipeComplete(player.getUniqueId())) {
            return;
        }

        if (event.getClickAction().isShiftClicked()) {
            return;
        }

        PlayerProfile profile = RecipeCompletableListener.getPlayerProfile(player);
        // try
        if (RecipeCompleteOpenModeGuideOption.instance().get(player) == RecipeCompleteOpenMode.NEW) {
            rollbackGuideHistory(profile);
        }
        // finally
        GUIDE_HISTORY.remove(player.getUniqueId());
        var callback = RecipeCompletableListener.PROFILE_CALLBACKS.get(player.getUniqueId());
        if (callback != null) {
            callback.accept(event, profile);
            RecipeCompletableListener.PROFILE_CALLBACKS.remove(player.getUniqueId());
        }
        RecipeCompletableListener.LAST_EVENTS.put(player.getUniqueId(), event);

        ItemStack clickedItemStack = event.getClickedItem();
        if (clickedItemStack != null) {
            tryPatchRecipeCompleteBook(player, clickedItemStack);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        tryRemoveRecipeCompleteBookLastRecipeCompleteLore(event.getPlayer());
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.NORMAL)
    public void patchItem(PatchEvent event) {
        PatchScope scope = event.getPatchScope();
        if (scope != PatchScope.SlimefunItem && scope != PatchScope.SearchItem) {
            return;
        }

        if (!isSelectingItemStackToRecipeComplete(event.getPlayer().getUniqueId())) {
            return;
        }

        ItemStack old = event.getItemStack();
        if (old == null || old.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = old.getItemMeta();
        if (meta == null) {
            return;
        }

        if (isTagged(meta)) {
            return;
        }

        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        // Patch hint start
        lore.add("");
        lore.add(ChatColors.color(Models.RECIPE_COMPLETE_GUI_MECHANISM_1));
        lore.add(ChatColors.color(Models.RECIPE_COMPLETE_GUI_MECHANISM_2));
        // Patch hint end

        meta.setLore(lore);
        tagMeta(meta);
        old.setItemMeta(meta);
        event.setItemStack(old);
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.NORMAL)
    public void patchBackground(PatchEvent event) {
        PatchScope scope = event.getPatchScope();
        if (scope != PatchScope.Background) {
            return;
        }

        if (isSelectingItemStackToRecipeComplete(event.getPlayer().getUniqueId())) {
            ItemStack old = event.getItemStack();
            if (old == null || old.getType() == Material.AIR) {
                return;
            }

            ItemMeta meta = old.getItemMeta();
            if (meta == null) {
                return;
            }

            if (isTagged(meta)) {
                return;
            }

            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }

            // Patch start
            old.setType(Material.RED_STAINED_GLASS_PANE);
            lore.add(ChatColors.color("&a你正在进行配方补全，如果是误触进入，请点击这里"));
            meta.getPersistentDataContainer().set(RECIPE_COMPLETE_EXIT_KEY, PersistentDataType.BOOLEAN, true);
            // Patch end

            meta.setLore(lore);
            tagMeta(meta);
            old.setItemMeta(meta);
            event.setItemStack(old);
        }
    }

    @EventHandler
    public void exit(InventoryClickEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) {
            return;
        }

        if (itemStack.getType() != Material.RED_STAINED_GLASS_PANE) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        if (!meta.getPersistentDataContainer().has(RECIPE_COMPLETE_EXIT_KEY, PersistentDataType.BOOLEAN)) {
            return;
        }

        exit((Player) event.getWhoClicked());
    }

    public void exit(Player player) {
        RecipeCompleteSession session = RecipeCompleteSession.getSession(player);
        RecipeCompleteSession.cancel(player);
        PlayerProfile profile = RecipeCompletableListener.getPlayerProfile(player);
        rollbackGuideHistory(profile);
        RecipeCompletableListener.PROFILE_CALLBACKS.remove(player.getUniqueId());
        if (session != null) {
            if (session.getMenu() != null) {
                BlockMenu actualMenu = StorageCacheUtils.getMenu(session.getMenu().getLocation());
                if (actualMenu != null) actualMenu.open(player);
            }
        }
    }

    @EventHandler
    public void updateInventory(RecipeCompleteEvents.SessionCompleteEvent event) {
        event.getPlayer().updateInventory();
    }

    @EventHandler
    public void updateInventory(RecipeCompleteEvents.SessionCancelEvent event) {
        event.getPlayer().updateInventory();
    }

    /**
     * @author balugaq
     * @see RecipeCompletableListener#addNotApplicableItem(SlimefunItem)
     * @since 1.9
     */
    @NullMarked
    public interface NotApplicable {
    }

    /**
     * @author balugaq
     * @since 1.9
     */
    @NullMarked
    public interface TaggedRecipeCompletable {
    }

    /**
     * @author balugaq
     * @since 1.9
     */
    @SuppressWarnings("deprecation")
    @NullMarked
    @FunctionalInterface
    public interface RecipeCompletableClickHandler extends ChestMenu.MenuClickHandler, TaggedRecipeCompletable {
    }

    /**
     * @author balugaq
     * @see ShulkerBoxPlayerInventoryItemSeeker
     * @see Source#getItemStackFromPlayerInventory(RecipeCompleteSession, ItemStack, int)
     * @since 2.1
     */
    @NullMarked
    public interface PlayerInventoryItemSeeker extends Keyed {
        /**
         * @param session The session
         * @param target  The target item
         * @param item    The item to be checked or handled
         * @param need    The requested amount
         * @return gotten item stack amount
         */
        @NonNegative
        int getItemStack(RecipeCompleteSession session, ItemStack target, ItemStack item, int need);
    }
}
