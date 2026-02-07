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

import com.balugaq.jeg.api.objects.collection.Pair;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.api.objects.events.PatchEvent;
import com.balugaq.jeg.api.objects.events.RecipeCompleteEvents;
import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.api.recipe_complete.source.base.SlimefunSource;
import com.balugaq.jeg.api.recipe_complete.source.base.VanillaSource;
import com.balugaq.jeg.core.integrations.ItemPatchListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.items.ItemsSetup;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import lombok.SneakyThrows;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
@SuppressWarnings({"unused", "ConstantValue"})
@NullMarked
public class RecipeCompletableListener implements ItemPatchListener {
    public static final NamespacedKey RECIPE_COMPLETE_EXIT_KEY = KeyUtil.newKey("recipe_complete_exit");
    public static final int[] DISPENSER_SLOTS = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8};
    public static final ConcurrentHashMap<Player, GuideEvents.ItemButtonClickEvent> LAST_EVENTS =
            new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Player, GuideHistory> GUIDE_HISTORY = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Player, BiConsumer<GuideEvents.ItemButtonClickEvent, PlayerProfile>> PROFILE_CALLBACKS =
            new ConcurrentHashMap<>();
    public static final Set<Player> listening = ConcurrentHashMap.newKeySet();
    public static final ConcurrentHashMap<SlimefunItem, Pair<int[], Boolean>> INGREDIENT_SLOTS =
            new ConcurrentHashMap<>();
    public static final ArrayList<SlimefunItem> NOT_APPLICABLE_ITEMS = new ArrayList<>();
    public static final ConcurrentHashMap<Player, Location> DISPENSER_LISTENING = new ConcurrentHashMap<>();
    public static final NamespacedKey LAST_RECIPE_COMPLETE_KEY = KeyUtil.newKey("last_recipe_complete");
    public static final ConcurrentHashMap<Player, ArrayList<ItemStack>> missingMaterials = new ConcurrentHashMap<>();
    private static @UnknownNullability ItemStack RECIPE_COMPLETABLE_BOOK_ITEM = null;

    static {
        JustEnoughGuide.runTimerAsync(
                () -> {
                    for (Player oldPlayer : missingMaterials.keySet()) {
                        Player player = GuideUtil.updatePlayer(oldPlayer);
                        if (player == null) {
                            continue;
                        }

                        var v = missingMaterials.get(player);
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
                            player.sendMessage(ChatColors.color("&c缺少 &7" + ItemStackHelper.getDisplayName(entry.getKey()) + "&ax" + entry.getValue()));
                        }
                    }
                }, 1L, 20L
        );
    }

    /**
     * @param slimefunItem
     *         the {@link SlimefunItem} to add
     *
     * @see NotApplicable
     */
    public static void addNotApplicableItem(SlimefunItem slimefunItem) {
        NOT_APPLICABLE_ITEMS.add(slimefunItem);
    }

    /**
     * @param slimefunItem
     *         the {@link SlimefunItem} to remove
     *
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
        var p = GuideUtil.updatePlayer(uuid);
        if (p != null) addCallback(p, callback);
    }

    public static void addCallback(
            final Player player, BiConsumer<GuideEvents.ItemButtonClickEvent, PlayerProfile> callback) {
        PROFILE_CALLBACKS.put(player, callback);
    }

    public static void removeCallback(UUID uuid) {
        var p = GuideUtil.updatePlayer(uuid);
        if (p != null) removeCallback(p);
    }

    public static void removeCallback(Player player) {
        PROFILE_CALLBACKS.remove(player);
    }

    public static void tagGuideOpen(Player player) {
        if (!PROFILE_CALLBACKS.containsKey(player)) {
            return;
        }

        PlayerProfile profile = getPlayerProfile(player);
        saveOriginGuideHistory(profile);
        clearGuideHistory(profile);
    }

    @SneakyThrows
    public static PlayerProfile getPlayerProfile(OfflinePlayer player) {
        // Shouldn't be null;
        return PlayerProfile.find(player).orElseThrow(() -> new RuntimeException("PlayerProfile not found"));
    }

    public static void saveOriginGuideHistory(PlayerProfile profile) {
        GuideHistory oldHistory = profile.getGuideHistory();
        GuideHistory newHistory = new GuideHistory(profile);
        ReflectionUtil.setValue(newHistory, "mainMenuPage", oldHistory.getMainMenuPage());
        LinkedList<?> queue = ReflectionUtil.getValue(oldHistory, "queue", LinkedList.class);
        ReflectionUtil.setValue(newHistory, "queue", queue != null ? queue.clone() : new LinkedList<>());
        var p = GuideUtil.updatePlayer(profile.getUUID());
        if (p != null) GUIDE_HISTORY.put(p, newHistory);
    }

    public static void clearGuideHistory(PlayerProfile profile) {
        ReflectionUtil.setValue(profile, "guideHistory", new GuideHistory(profile));
    }

    @Deprecated(forRemoval = true)
    @Nullable
    public static GuideEvents.ItemButtonClickEvent getLastEvent(UUID playerUUID) {
        var p = GuideUtil.updatePlayer(playerUUID);
        if (p != null) return getLastEvent(p);
        return null;
    }

    @Nullable
    public static GuideEvents.ItemButtonClickEvent getLastEvent(Player player) {
        return LAST_EVENTS.get(player);
    }

    @Deprecated(forRemoval = true)
    public static void clearLastEvent(UUID playerUUID) {
        var p = GuideUtil.updatePlayer(playerUUID);
        if (p != null) clearLastEvent(p);
    }

    public static void clearLastEvent(Player player) {
        LAST_EVENTS.remove(player);
    }

    @Deprecated(forRemoval = true)
    public static void addDispenserListening(UUID uuid, Location location) {
        var p = GuideUtil.updatePlayer(uuid);
        if (p != null) addDispenserListening(p, location);
    }

    public static void addDispenserListening(Player player, Location location) {
        DISPENSER_LISTENING.put(player, location);
    }

    @Deprecated(forRemoval = true)
    public static boolean isOpeningDispenser(UUID uuid) {
        var p = GuideUtil.updatePlayer(uuid);
        if (p != null) return isOpeningDispenser(p);
        return false;
    }

    public static boolean isOpeningDispenser(Player player) {
        return DISPENSER_LISTENING.containsKey(player);
    }

    @Deprecated(forRemoval = true)
    public static void removeDispenserListening(UUID uuid) {
        var p = GuideUtil.updatePlayer(uuid);
        if (p != null) removeDispenserListening(p);
    }

    public static void removeDispenserListening(Player player) {
        DISPENSER_LISTENING.remove(player);
    }

    @EventHandler
    public void prepare(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof BlockMenu blockMenu) {
            tryAddClickHandler(blockMenu);
        }

        if (event.getInventory().getHolder() instanceof Dispenser dispenser) {
            tryAddVanillaListen(event, dispenser.getBlock(), event.getInventory());
        }
    }

    @SuppressWarnings("deprecation")
    private static void tryAddClickHandler(BlockMenu blockMenu) {
        SlimefunItem sf = blockMenu.getPreset().getSlimefunItem();
        if (!isApplicable(sf)) {
            return;
        }

        if (!hasIngredientSlots(sf)) {
            return;
        }

        ChestMenu.MenuClickHandler old = blockMenu.getPlayerInventoryClickHandler();
        if (old instanceof TaggedRecipeCompletable) {
            // Already added
            return;
        }

        blockMenu.addPlayerInventoryClickHandler(
                (RecipeCompletableClickHandler) (player, slot, itemStack, clickAction) -> {
                    // mixin start
                    if (StackUtils.itemsMatch(itemStack, getRecipeCompletableBookItem(), false, false, false, false)
                            && blockMenu.isPlayerInventoryClickable()) {
                        if (isSelectingItemStackToRecipeComplete(player)) {
                            player.sendMessage(ChatColors.color("&c[配方补全] 你已经在进行配方补全了"));
                            return false;
                        }

                        enterSelectingItemStackToRecipeComplete(player);
                        int[] slots = getIngredientSlots(sf);
                        boolean unordered = isUnordered(sf);
                        var session = RecipeCompleteSession.create(blockMenu, player, clickAction, slots, unordered, 1);
                        if (session == null) return false;
                        for (SlimefunSource source : RecipeCompleteProvider.getSlimefunSources()) {
                            // Strategy mode
                            // Default strategy see {@link DefaultPlayerInventoryRecipeCompleteSlimefunSource}
                            if (source.handleable(session)) {
                                source.openGuide(session);
                                break;
                            }
                        }

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
        addDispenserListening(p, block.getLocation());
    }

    @SuppressWarnings("RedundantIfStatement")
    public static boolean isApplicable(SlimefunItem slimefunItem) {
        if (slimefunItem instanceof NotApplicable) {
            return false;
        }

        if (NOT_APPLICABLE_ITEMS.contains(slimefunItem)) {
            return false;
        }

        // No idea yet.
        return true;
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

    public static boolean isSelectingItemStackToRecipeComplete(Player player) {
        return listening.contains(player);
    }

    public static void enterSelectingItemStackToRecipeComplete(Player player) {
        listening.add(player);
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

    public static void exitSelectingItemStackToRecipeComplete(Player player) {
        listening.remove(player);
    }

    @EventHandler
    public void exit(RecipeCompleteEvents.SessionCancelEvent event) {
        exitSelectingItemStackToRecipeComplete(event.getPlayer());
    }

    @EventHandler
    public void exit(RecipeCompleteEvents.SessionCompleteEvent event) {
        exitSelectingItemStackToRecipeComplete(event.getPlayer());
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
        if (player == null || !isOpeningDispenser(player)) {
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
        for (VanillaSource source : RecipeCompleteProvider.getVanillaSources()) {
            // Strategy mode
            // Default strategy see {@link DefaultPlayerInventoryRecipeCompleteVanillaSource}
            if (source.handleable(session)) {
                source.openGuide(session);
                break;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void exitVanilla(InventoryOpenEvent event) {
        var p = GuideUtil.updatePlayer(event.getPlayer().getUniqueId());
        if (p != null) removeDispenserListening(p);
    }

    @EventHandler
    public void onJEGItemClick(GuideEvents.ItemButtonClickEvent event) {
        Player player = event.getPlayer();
        if (!RecipeCompletableListener.PROFILE_CALLBACKS.containsKey(player)) {
            return;
        }

        PlayerProfile profile = RecipeCompletableListener.getPlayerProfile(player);
        rollbackGuideHistory(profile);
        RecipeCompletableListener.PROFILE_CALLBACKS.get(player).accept(event, profile);
        RecipeCompletableListener.PROFILE_CALLBACKS.remove(player);
        RecipeCompletableListener.LAST_EVENTS.put(player, event);

        ItemStack clickedItemStack = event.getClickedItem();
        if (clickedItemStack != null) {
            tryPatchRecipeCompleteBook(player, clickedItemStack);
        }
    }

    public static void rollbackGuideHistory(PlayerProfile profile) {
        var p = GuideUtil.updatePlayer(profile.getUUID());
        if (p == null) return;

        GuideHistory originHistory = RecipeCompletableListener.GUIDE_HISTORY.get(p);
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
                        lore.remove(lore.size() - 1);
                    }
                    if (lore.size() >= 6) {
                        lore.remove(lore.size() - 1);
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        tryRemoveRecipeCompleteBookLastRecipeCompleteLore(event.getPlayer());
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
                        lore.remove(lore.size() - 1);
                    }
                    if (lore.size() >= 6) {
                        lore.remove(lore.size() - 1);
                    }
                }

                meta.getPersistentDataContainer().set(LAST_RECIPE_COMPLETE_KEY, PersistentDataType.BOOLEAN, false);
                // Patch end

                meta.setLore(lore);
                itemStack.setItemMeta(meta);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.NORMAL)
    public void patchItem(PatchEvent event) {
        PatchScope scope = event.getPatchScope();
        if (scope != PatchScope.SlimefunItem && scope != PatchScope.SearchItem) {
            return;
        }

        if (isSelectingItemStackToRecipeComplete(event.getPlayer())) {
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
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.NORMAL)
    public void patchBackground(PatchEvent event) {
        PatchScope scope = event.getPatchScope();
        if (scope != PatchScope.Background) {
            return;
        }

        if (isSelectingItemStackToRecipeComplete(event.getPlayer())) {
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
        RecipeCompleteSession.cancel(player);
        PlayerProfile profile = RecipeCompletableListener.getPlayerProfile(player);
        rollbackGuideHistory(profile);
        RecipeCompletableListener.PROFILE_CALLBACKS.remove(player);
        player.closeInventory();
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
    public interface NotApplicable {
    }

    /**
     * @author balugaq
     * @since 1.9
     */
    public interface TaggedRecipeCompletable {
    }

    /**
     * @author balugaq
     * @since 1.9
     */
    @SuppressWarnings("deprecation")
    @FunctionalInterface
    public interface RecipeCompletableClickHandler extends ChestMenu.MenuClickHandler, TaggedRecipeCompletable {
    }

    public static NamespacedKey getKey0() {
        return KeyUtil.newKey(RecipeCompletableListener.class.getSimpleName().toLowerCase());
    }
}
