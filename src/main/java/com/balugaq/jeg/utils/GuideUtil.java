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

package com.balugaq.jeg.utils;

import com.balugaq.jeg.api.cost.CERCalculator;
import com.balugaq.jeg.api.groups.CERRecipeGroup;
import com.balugaq.jeg.api.groups.RTSSearchGroup;
import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.interfaces.BookmarkRelocation;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.api.objects.collection.data.MachineData;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.api.objects.events.RTSEvents;
import com.balugaq.jeg.core.listeners.RTSListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.formatter.Format;
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
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.experimental.UtilityClass;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains utility methods for the guide system.
 *
 * @author Final_ROOT
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings("unused")
@UtilityClass
public final class GuideUtil {
    private static final List<ItemGroup> forceHiddens = new ArrayList<>();
    private static final ItemStack BOOK_MARK_MENU_BUTTON = Converter.getItem(new SlimefunItemStack("JEG_BOOK_MARK_BUTTON", Lang.getIcon("book-mark-button", Material.NETHER_STAR)));
    private static final ItemStack ITEM_MARK_MENU_BUTTON = Converter.getItem(new SlimefunItemStack("JEG_ITEM_MARK_BUTTON", Lang.getIcon("item-mark-button", Material.WRITABLE_BOOK)));
    private static final ItemStack CER_MENU_BUTTON = Converter.getItem(new SlimefunItemStack("JEG_CER_BUTTON", Lang.getIcon("cer-menu-button", Material.EMERALD)));

    /**
     * Open the main menu of the guide for the given player and mode.
     *
     * @param player       The player to open the guide for.
     * @param mode         The mode to open the guide for.
     * @param selectedPage The page to open the guide to.
     */
    @ParametersAreNonnullByDefault
    public static void openMainMenuAsync(Player player, SlimefunGuideMode mode, int selectedPage) {
        if (!PlayerProfile.get(
                player, profile -> Slimefun.runSync(() -> openMainMenu(player, profile, mode, selectedPage)))) {
            Slimefun.getLocalization().sendMessage(player, "messages.opening-guide");
        }
    }

    /**
     * Open the main menu of the guide for the given player and mode.
     *
     * @param player       The player to open the guide for.
     * @param profile      The player's profile.
     * @param mode         The mode to open the guide for.
     * @param selectedPage The page to open the guide to.
     */
    @ParametersAreNonnullByDefault
    public static void openMainMenu(Player player, PlayerProfile profile, SlimefunGuideMode mode, int selectedPage) {
        getGuide(player, mode).openMainMenu(profile, selectedPage);
    }

    /**
     * Get the guide implementation for the given player and mode.
     *
     * @param player The player to get the guide for.
     * @param mode   The mode to get the guide for.
     * @return The guide implementation for the given player and mode.
     */
    public static @NotNull SlimefunGuideImplementation getGuide(@NotNull Player player, SlimefunGuideMode mode) {
        if (mode == SlimefunGuideMode.SURVIVAL_MODE) {
            return Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.SURVIVAL_MODE);
        }

        // Player must be op or have the permission "slimefun.cheat.items" to access the cheat guide
        if ((player.isOp() || player.hasPermission("slimefun.cheat.items")) && mode == SlimefunGuideMode.CHEAT_MODE) {
            return Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.CHEAT_MODE);
        }

        // Fallback to survival guide if no permission is given
        return Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.SURVIVAL_MODE);
    }

    public static void removeLastEntry(@NotNull GuideHistory guideHistory) {
        try {
            Method getLastEntry = guideHistory.getClass().getDeclaredMethod("getLastEntry", boolean.class);
            getLastEntry.setAccessible(true);
            getLastEntry.invoke(guideHistory, true);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Debug.trace(e);
        }
    }

    public static @NotNull ItemStack getBookMarkMenuButton() {
        return BOOK_MARK_MENU_BUTTON;
    }

    public static @NotNull ItemStack getItemMarkMenuButton() {
        return ITEM_MARK_MENU_BUTTON;
    }

    public static boolean isTaggedGroupType(@NotNull ItemGroup itemGroup) {
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

    @SuppressWarnings("deprecation")
    public static void addRTSButton(
            @NotNull ChestMenu menu,
            @NotNull Player p,
            @NotNull PlayerProfile profile,
            @NotNull Format format,
            SlimefunGuideMode mode,
            @NotNull SlimefunGuideImplementation implementation) {
        if (JustEnoughGuide.getConfigManager().isRTSSearch()) {
            for (int ss : format.getChars('R')) {
                menu.addItem(
                        ss,
                        PatchScope.RealTimeSearch.patch(p, Lang.RTS_ITEM),
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
                                                                    profile.getGuideHistory()
                                                                            .getMainMenuPage());
                                                        } else {
                                                            history.goBack(implementation);
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
                                                                            mode);
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
                                                                    oldPage + 1);
                                                            RTSEvents.PageChangeEvent event =
                                                                    new RTSEvents.PageChangeEvent(
                                                                            pl,
                                                                            RTSSearchGroup.RTS_PLAYERS.get(pl),
                                                                            oldPage,
                                                                            newPage,
                                                                            mode);
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
                                                new int[]{
                                                        AnvilGUI.Slot.INPUT_LEFT,
                                                        AnvilGUI.Slot.INPUT_RIGHT,
                                                        AnvilGUI.Slot.OUTPUT
                                                },
                                                null);
                                    } catch (Exception ignored) {
                                        p.sendMessage(ChatColor.RED + "Unable to use RTS caused by unadapted Minecraft version.");
                                    }
                                    return false;
                                }));
            }
        } else {
            for (int ss : format.getChars('R')) {
                menu.addItem(ss, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void addBookMarkButton(
            @NotNull ChestMenu menu,
            @NotNull Player p,
            @NotNull PlayerProfile profile,
            @NotNull Format format,
            @NotNull JEGSlimefunGuideImplementation implementation,
            ItemGroup itemGroup) {
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
                                }));
            }
        } else {
            for (int s : format.getChars('C')) {
                menu.addItem(s, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void addItemMarkButton(
            @NotNull ChestMenu menu,
            @NotNull Player p,
            @NotNull PlayerProfile profile,
            @NotNull Format format,
            @NotNull JEGSlimefunGuideImplementation implementation,
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
                                }));
            }
        } else {
            for (int ss : format.getChars('c')) {
                menu.addItem(ss, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
            }
        }
    }

    @SuppressWarnings({"deprecation", "DataFlowIssue"})
    @CallTimeSensitive(CallTimeSensitive.AfterIntegrationsLoaded)
    public static void addCerButton(ChestMenu menu, Player p, PlayerProfile profile, SlimefunItem machine, SlimefunGuideImplementation implementation, Format format) {
        for (int ss : format.getChars('m')) {
            if (CERCalculator.cerable(machine)) {
                menu.addItem(ss, PatchScope.Cer.patch(p, getCerMenuButton()),
                        (pl, slot, itemstack, action) -> EventUtil.callEvent(new GuideEvents.CerButtonClickEvent(pl, itemstack, slot, action, menu, implementation)).ifSuccess(() -> new CERRecipeGroup(implementation, pl, machine, MachineData.get(machine).wrap()).open(pl, profile, implementation.getMode())));
            }
        }
    }

    public static ItemStack getCerMenuButton() {
        return CER_MENU_BUTTON;
    }

    public static void setForceHiddens(@NotNull ItemGroup itemGroup, boolean forceHidden) {
        if (forceHidden) {
            forceHiddens.add(itemGroup);
        } else {
            forceHiddens.remove(itemGroup);
        }
    }

    @NotNull
    public static List<ItemGroup> getForceHiddens() {
        return new ArrayList<>(forceHiddens);
    }

    public static boolean isForceHidden(@NotNull ItemGroup group) {
        return forceHiddens.contains(group);
    }

    public static void shutdown() {
        forceHiddens.clear();
    }
}
