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

package com.balugaq.jeg.utils;

import com.balugaq.jeg.api.cost.please_set_cer_patch_to_false_in_config_when_you_see_this.CERCalculator;
import com.balugaq.jeg.api.editor.GroupResorter;
import com.balugaq.jeg.api.groups.ActionSelectGroup;
import com.balugaq.jeg.api.groups.CERRecipeGroup;
import com.balugaq.jeg.api.groups.KeybindItemsGroup;
import com.balugaq.jeg.api.groups.KeybindsItemsGroup;
import com.balugaq.jeg.api.groups.RTSSearchGroup;
import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.groups.SubKeybindsItemsGroup;
import com.balugaq.jeg.api.interfaces.BookmarkRelocation;
import com.balugaq.jeg.api.interfaces.DisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.DisplayInSurvivalMode;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.api.objects.collection.data.MachineData;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.api.objects.events.RTSEvents;
import com.balugaq.jeg.core.listeners.GuideListener;
import com.balugaq.jeg.core.listeners.RTSListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.clickhandler.BaseAction;
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.formatter.Format;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunGuideOpenEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.LockedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SeasonalItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.experimental.UtilityClass;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains utility methods for the guide system.
 *
 * @author Final_ROOT
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings({"unused", "deprecation"})
@UtilityClass
@NullMarked
public final class GuideUtil {
    private static final List<ItemGroup> forceHiddens = new ArrayList<>();
    private static final ItemStack BOOK_MARK_MENU_BUTTON =
            ItemStackUtil.getCleanItem(Converter.getItem(new SlimefunItemStack(
                    "JEG_BOOK_MARK_BUTTON",
                    Material.NETHER_STAR, "&e&l收藏物列表"
            )));
    private static final ItemStack ITEM_MARK_MENU_BUTTON =
            ItemStackUtil.getCleanItem(Converter.getItem(new SlimefunItemStack(
                    "JEG_ITEM_MARK_BUTTON",
                    Material.WRITABLE_BOOK, "&e&l收藏物品"
            )));
    private static final ItemStack CER_MENU_BUTTON =
            ItemStackUtil.getCleanItem(Converter.getItem(new SlimefunItemStack(
                    "JEG_CER_BUTTON", Material.EMERALD,
                    "&e&l性价比界面（仅供参考）"
            )));

    public static void openMainMenuAsync(Player player) {
        openMainMenuAsync(player, getLastGuide(player).getMode());
    }

    public static void openMainMenuAsync(Player player, SlimefunGuideMode mode) {
        openMainMenuAsync(player, mode, 1);
    }

    /**
     * Open the main menu of the guide for the given player and mode.
     *
     * @param player
     *         The player to open the guide for.
     * @param mode
     *         The mode to open the guide for.
     * @param selectedPage
     *         The page to open the guide to.
     */
    public static void openMainMenuAsync(Player player, SlimefunGuideMode mode, int selectedPage) {
        if (!PlayerProfile.get(
                player, profile -> Slimefun.runSync(() -> openMainMenu(player, profile, mode, selectedPage)))) {
            Slimefun.getLocalization().sendMessage(player, "messages.opening-guide");
        }
    }

    /**
     * Open the main menu of the guide for the given player and mode.
     *
     * @param player
     *         The player to open the guide for.
     * @param profile
     *         The player's profile.
     * @param mode
     *         The mode to open the guide for.
     * @param selectedPage
     *         The page to open the guide to.
     */
    public static void openMainMenu(Player player, PlayerProfile profile, SlimefunGuideMode mode, int selectedPage) {
        getGuide(player, mode).openMainMenu(profile, selectedPage);
    }

    public static void removeLastEntry(GuideHistory guideHistory) {
        try {
            Method getLastEntry = guideHistory.getClass().getDeclaredMethod("getLastEntry", boolean.class);
            getLastEntry.setAccessible(true);
            getLastEntry.invoke(guideHistory, true);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Debug.trace(e);
        }
    }

    @SuppressWarnings("deprecation")
    public static void addRTSButton(
            ChestMenu menu,
            Player p,
            PlayerProfile profile,
            Format format,
            SlimefunGuideMode mode,
            SlimefunGuideImplementation implementation) {
        if (JustEnoughGuide.getConfigManager().isRTSSearch()) {
            for (int ss : format.getChars('R')) {
                menu.addItem(
                        ss,
                        PatchScope.RealTimeSearch.patch(p, Models.RTS_ITEM),
                        (pl, slot, itemstack, action) -> EventUtil.callEvent(new GuideEvents.RTSButtonClickEvent(
                                        pl, itemstack, slot, action, menu, implementation))
                                .ifSuccess(() -> {
                                    try {
                                        RTSSearchGroup.newRTSInventoryFor(
                                                pl,
                                                mode,
                                                (s, stateSnapshot) -> {
                                                    if (s == AnvilGUI.Slot.INPUT_LEFT) {
                                                        // back button clicked
                                                        GuideHistory history = profile.getGuideHistory();
                                                        if (action.isShiftClicked()) {
                                                            implementation.openMainMenu(
                                                                    profile,
                                                                    history.getMainMenuPage()
                                                            );
                                                        } else {
                                                            GuideUtil.goBack(history);
                                                        }
                                                    } else if (s == AnvilGUI.Slot.INPUT_RIGHT) {
                                                        // previous page button clicked
                                                        SearchGroup rts = RTSSearchGroup.RTS_SEARCH_GROUPS.get(pl);
                                                        if (rts != null) {
                                                            int oldPage = RTSSearchGroup.RTS_PAGES.getOrDefault(pl, 1);
                                                            int newPage = Math.max(1, oldPage - 1);
                                                            RTSEvents.PageChangeEvent event =
                                                                    new RTSEvents.PageChangeEvent(
                                                                            pl,
                                                                            RTSSearchGroup.RTS_PLAYERS.get(pl),
                                                                            oldPage,
                                                                            newPage,
                                                                            mode
                                                                    );
                                                            Bukkit.getPluginManager()
                                                                    .callEvent(event);
                                                            if (!event.isCancelled()) {
                                                                synchronized (RTSSearchGroup.RTS_PAGES) {
                                                                    RTSSearchGroup.RTS_PAGES.put(pl, newPage);
                                                                }
                                                            }
                                                        }
                                                    } else if (s == AnvilGUI.Slot.OUTPUT) {
                                                        // next page button clicked
                                                        SearchGroup rts = RTSSearchGroup.RTS_SEARCH_GROUPS.get(pl);
                                                        if (rts != null) {
                                                            int oldPage = RTSSearchGroup.RTS_PAGES.getOrDefault(pl, 1);
                                                            int newPage = Math.min(
                                                                    (rts.slimefunItemList.size() - 1)
                                                                            / RTSListener.FILL_ORDER.length
                                                                            + 1,
                                                                    oldPage + 1
                                                            );
                                                            RTSEvents.PageChangeEvent event =
                                                                    new RTSEvents.PageChangeEvent(
                                                                            pl,
                                                                            RTSSearchGroup.RTS_PLAYERS.get(pl),
                                                                            oldPage,
                                                                            newPage,
                                                                            mode
                                                                    );
                                                            Bukkit.getPluginManager()
                                                                    .callEvent(event);
                                                            if (!event.isCancelled()) {
                                                                synchronized (RTSSearchGroup.RTS_PAGES) {
                                                                    RTSSearchGroup.RTS_PAGES.put(pl, newPage);
                                                                }
                                                            }
                                                        }
                                                    }
                                                },
                                                new int[] {
                                                        AnvilGUI.Slot.INPUT_LEFT,
                                                        AnvilGUI.Slot.INPUT_RIGHT,
                                                        AnvilGUI.Slot.OUTPUT
                                                },
                                                null
                                        );
                                    } catch (Exception ignored) {
                                        p.sendMessage(ChatColor.RED + "不兼容的版本! 无法使用实时搜索");
                                    }
                                    return false;
                                })
                );
            }
        } else {
            for (int ss : format.getChars('R')) {
                menu.addItem(ss, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public static void goBack(GuideHistory history) {
        goBack(ReflectionUtil.getValue(history, "profile", PlayerProfile.class));
    }

    public static void goBack(PlayerProfile profile) {
        var p = profile.getPlayer();
        if (p == null) return;

        GuideHistory history = profile.getGuideHistory();
        var entry = ReflectionUtil.invokeMethod(history, "getLastEntry", true);
        var guide = getLastGuide(p);

        if (entry == null) {
            guide.openMainMenu(profile, history.getMainMenuPage());
            return;
        }
        var content = ReflectionUtil.invokeMethod(entry, "getIndexedObject");
        @SuppressWarnings("DataFlowIssue") int page = (int) ReflectionUtil.invokeMethod(entry, "getPage");
        if (content instanceof final ItemGroup group) {
            guide.openItemGroup(profile, group, page);
            return;
        }

        if (content instanceof final SlimefunItem item) {
            guide.displayItem(profile, item, false);
            return;
        }

        if (content instanceof final ItemStack stack) {
            guide.displayItem(profile, stack, page, false);
            return;
        }

        if (content instanceof final String query) {
            guide.openSearch(profile, query, false);
            return;
        }

        throw new IllegalStateException("Unknown GuideHistory entry: " + content);
    }

    public SlimefunGuideImplementation getLastGuide(Player player) {
        var mode = GuideListener.guideModeMap.get(player);
        return GuideUtil.getGuide(player, mode == null ? SlimefunGuideMode.SURVIVAL_MODE : mode);
    }

    /**
     * Get the guide implementation for the given player and mode.
     *
     * @param player
     *         The player to get the guide for.
     * @param mode
     *         The mode to get the guide for.
     *
     * @return The guide implementation for the given player and mode.
     */
    public static SlimefunGuideImplementation getGuide(Player player, SlimefunGuideMode mode) {
        if (mode == SlimefunGuideMode.SURVIVAL_MODE) {
            return GuideUtil.getSlimefunGuide(SlimefunGuideMode.SURVIVAL_MODE);
        }

        // Player must be op or have the permission "slimefun.cheat.items" to access the cheat guide
        if ((player.isOp() || player.hasPermission("slimefun.cheat.items")) && mode == SlimefunGuideMode.CHEAT_MODE) {
            return GuideUtil.getSlimefunGuide(SlimefunGuideMode.CHEAT_MODE);
        }

        // Fallback to survival guide if no permission is given
        return GuideUtil.getSlimefunGuide(SlimefunGuideMode.SURVIVAL_MODE);
    }

    @ApiStatus.Obsolete
    public static SlimefunGuideImplementation getSlimefunGuide(SlimefunGuideMode mode) {
        return Slimefun.getRegistry().getSlimefunGuide(mode);
    }

    @SuppressWarnings("deprecation")
    public static void addBookMarkButton(
            ChestMenu menu,
            Player p,
            PlayerProfile profile,
            Format format,
            JEGSlimefunGuideImplementation implementation,
            @Nullable ItemGroup itemGroup) {
        if (JustEnoughGuide.getConfigManager().isBookmark()) {
            BookmarkRelocation b =
                    itemGroup instanceof BookmarkRelocation bookmarkRelocation ? bookmarkRelocation : null;
            for (int s : b != null ? b.getBookMark(implementation, p) : format.getChars('C')) {
                menu.addItem(
                        s,
                        PatchScope.BookMark.patch(p, getBookMarkMenuButton()),
                        (pl, slot, itemstack, action) -> EventUtil.callEvent(new GuideEvents.BookMarkButtonClickEvent(
                                        pl, itemstack, slot, action, menu, implementation))
                                .ifSuccess(() -> {
                                    implementation.openBookMarkGroup(pl, profile);
                                    return false;
                                })
                );
            }
        } else {
            for (int s : format.getChars('C')) {
                menu.addItem(s, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
            }
        }
    }

    public static ItemStack getBookMarkMenuButton() {
        return BOOK_MARK_MENU_BUTTON;
    }

    @SuppressWarnings("deprecation")
    public static void addItemMarkButton(
            ChestMenu menu,
            Player p,
            PlayerProfile profile,
            Format format,
            JEGSlimefunGuideImplementation implementation,
            @Nullable ItemGroup itemGroup) {
        if (itemGroup != null && JustEnoughGuide.getConfigManager().isBookmark() && isTaggedGroupType(itemGroup)) {
            BookmarkRelocation b = itemGroup instanceof BookmarkRelocation relocation ? relocation : null;
            for (int ss : b != null ? b.getItemMark(implementation, p) : format.getChars('c')) {
                menu.addItem(
                        ss,
                        PatchScope.ItemMark.patch(p, getItemMarkMenuButton()),
                        (pl, slot, itemstack, action) -> EventUtil.callEvent(new GuideEvents.ItemMarkButtonClickEvent(
                                        pl, itemstack, slot, action, menu, implementation))
                                .ifSuccess(() -> {
                                    implementation.openItemMarkGroup(itemGroup, pl, profile);
                                    return false;
                                })
                );
            }
        } else {
            for (int ss : format.getChars('c')) {
                menu.addItem(ss, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
            }
        }
    }

    public static boolean isTaggedGroupType(ItemGroup itemGroup) {
        Class<?> clazz = itemGroup.getClass();
        return !(itemGroup instanceof FlexItemGroup)
                && (clazz == ItemGroup.class
                || clazz == SubItemGroup.class
                || clazz == LockedItemGroup.class
                || clazz == SeasonalItemGroup.class
                || itemGroup instanceof BookmarkRelocation
                || clazz.getName().equalsIgnoreCase("me.voper.slimeframe.implementation.groups.ChildGroup")
                || clazz.getName().endsWith("DummyItemGroup")
                || clazz.getName().endsWith("SubGroup"));
    }

    public static ItemStack getItemMarkMenuButton() {
        return ITEM_MARK_MENU_BUTTON;
    }

    @SuppressWarnings({"deprecation"})
    @CallTimeSensitive(CallTimeSensitive.AfterIntegrationsLoaded)
    public static void addCerButton(ChestMenu menu, Player p, PlayerProfile profile, SlimefunItem machine,
                                    SlimefunGuideImplementation implementation, Format format) {
        for (int ss : format.getChars('m')) {
            if (JustEnoughGuide.getConfigManager().isCerPatch()) {
                if (CERCalculator.cerable(machine)) {
                    menu.addItem(
                            ss, PatchScope.Cer.patch(p, getCerMenuButton()),
                            (pl, slot, itemstack, action) -> EventUtil.callEvent(new GuideEvents.CerButtonClickEvent(pl, itemstack, slot, action, menu, implementation)).ifSuccess(() -> new CERRecipeGroup(implementation, pl, machine, MachineData.get(machine).wrap()).open(pl, profile, implementation.getMode()))
                    );
                }
            }
        }
    }

    public static ItemStack getCerMenuButton() {
        return CER_MENU_BUTTON;
    }

    public static void setForceHiddens(ItemGroup itemGroup, boolean forceHidden) {
        if (forceHidden) {
            forceHiddens.add(itemGroup);
        } else {
            forceHiddens.remove(itemGroup);
        }
    }

    public static List<ItemGroup> getForceHiddens() {
        return new ArrayList<>(forceHiddens);
    }

    public static void shutdown() {
        forceHiddens.clear();
    }

    public static void openKeybindsGui(Player player) {
        PlayerProfile.get(
                player, prf ->
                        new KeybindsItemsGroup().open(player, prf, SlimefunGuideMode.SURVIVAL_MODE)
        );
    }

    public static void openSubKeybindsGui(Player player, OnClick keybindsSet) {
        PlayerProfile.get(
                player, prf ->
                        new SubKeybindsItemsGroup(keybindsSet).open(player, prf, SlimefunGuideMode.SURVIVAL_MODE)
        );
    }

    public static ItemStack getKeybindIcon(OnClick keybind) {
        return Converter.getItem(keybind.material(), ChatColors.color("&7" + keybind.name()));
    }

    public static void openKeybindGui(Player player, OnClick keybind) {
        PlayerProfile.get(
                player, prf ->
                        new KeybindItemsGroup(player, keybind).open(player, prf, SlimefunGuideMode.SURVIVAL_MODE)
        );
    }

    public static void openActionSelectGui(Player player, OnClick keybind, BaseAction action) {
        PlayerProfile.get(
                player, prf -> new ActionSelectGroup(player, keybind, action)
                        .open(player, prf, SlimefunGuideMode.SURVIVAL_MODE)
        );
    }

    public static ItemStack getActionIcon(BaseAction action) {
        return Converter.getItem(action.material(), ChatColors.color("&7" + action.name()));
    }

    public static ItemStack getLeftActionIcon(BaseAction action) {
        return Converter.getItem(
                action.material(),
                ChatColors.color("&7按下 " + action.getKey().getKey() + " 时 (" + action.name() + ")")
        );
    }

    public static @Nullable Player updatePlayer(Player player) {
        return Bukkit.getPlayer(player.getUniqueId());
    }

    public static @Nullable Player updatePlayer(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }

    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    public static List<ItemGroup> getVisibleItemGroupsCheat(Player p, PlayerProfile profile, boolean guideTierMode) {
        List<ItemGroup> groups = new ArrayList<>();
        List<ItemGroup> specialGroups = new ArrayList<>();
        List<ItemGroup> survival = getVisibleItemGroupsSurvival(p, profile);
        for (ItemGroup group : new ArrayList<>(Slimefun.getRegistry().getAllItemGroups())) {
            try {
                if (group.getClass().isAnnotationPresent(NotDisplayInCheatMode.class)) {
                    continue;
                }
                if (group.getClass().isAnnotationPresent(DisplayInCheatMode.class)) {
                    groups.add(group);
                    continue;
                }

                if (!guideTierMode && GuideUtil.isForceHidden(group)) {
                    continue;
                }

                if (DisplayInCheatMode.Checker.contains(group)) {
                    if (DisplayInCheatMode.Checker.isSpecial(group)) {
                        specialGroups.add(group);
                        continue;
                    } else {
                        groups.add(group);
                        continue;
                    }
                } else if (NotDisplayInCheatMode.Checker.contains(group)) {
                    continue;
                }

                if (group instanceof SeasonalItemGroup) {
                    specialGroups.add(group);
                } else {
                    if (survival.contains(group) || (group.isVisible(p) && !group.isHidden(p))) {
                        groups.add(group);
                    } else {
                        var sm = group.getClass().getSimpleName();
                        if ("AdvancementsItemGroup".equals(sm)) {
                            continue;
                        }
                        if (!(group instanceof SubItemGroup) && !sm.equals("DummyItemGroup")) {
                            String key = group.getKey().getKey();
                            if (sm.equals("SubGroup")) {
                                if (!key.equals("infinity_cheat") && !key.equals("omc_forge_cheat")) {
                                    continue;
                                }
                            }
                            if (key.equals("momotech_final_")) {
                                continue;
                            }
                            specialGroups.add(group);
                        }
                    }
                }
            } catch (Exception | LinkageError x) {
                SlimefunAddon addon = group.getAddon();
                Logger logger = addon != null
                        ? addon.getLogger()
                        : JustEnoughGuide.getInstance().getLogger();
                logger.log(Level.SEVERE, x, () -> "Could not display item group: " + group);
            }
        }

        groups = new ArrayList<>(groups.stream().distinct().toList());
        GroupResorter.sort(groups);
        groups.addAll(specialGroups);

        return groups;
    }

    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    public static List<ItemGroup> getVisibleItemGroupsSurvival(Player p, PlayerProfile profile) {
        List<ItemGroup> groups = new ArrayList<>();

        for (ItemGroup group : new ArrayList<>(Slimefun.getRegistry().getAllItemGroups())) {
            try {
                if (group.getClass().isAnnotationPresent(NotDisplayInSurvivalMode.class)) {
                    continue;
                }
                if (group.getClass().isAnnotationPresent(DisplayInSurvivalMode.class)) {
                    groups.add(group);
                    continue;
                }
                if (GuideUtil.isForceHidden(group)) {
                    continue;
                }
                if (group instanceof FlexItemGroup flexItemGroup) {
                    if (flexItemGroup.isVisible(p, profile, SlimefunGuideMode.SURVIVAL_MODE)) {
                        groups.add(group);
                    }
                } else if (!group.isHidden(p) && group.isVisible(p) && group.isAccessible(p)) {
                    groups.add(group);
                }
            } catch (Exception | LinkageError x) {
                SlimefunAddon addon = group.getAddon();

                if (addon != null) {
                    addon.getLogger().log(Level.SEVERE, x, () -> "Could not display item group: " + group);
                } else {
                    JustEnoughGuide.getInstance()
                            .getLogger()
                            .log(Level.SEVERE, x, () -> "Could not display item group: " + group);
                }
            }
        }
        GroupResorter.sort(groups);

        return groups;
    }

    public static void openGuide(Player player) {
        SlimefunGuideOpenEvent event = new SlimefunGuideOpenEvent(
                player, getLastGuide(player).getItem(), getLastGuideMode(player)
        );
        Bukkit.getPluginManager().callEvent(event);
    }

    public static boolean isForceHidden(ItemGroup group) {
        return forceHiddens.contains(group);
    }

    public SlimefunGuideMode getLastGuideMode(Player player) {
        return getLastGuide(player).getMode();
    }

    public static void goBack(Player player) {
        PlayerProfile.get(player, GuideUtil::goBack);
    }
}
