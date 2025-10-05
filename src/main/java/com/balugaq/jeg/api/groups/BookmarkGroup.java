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

package com.balugaq.jeg.api.groups;

import city.norain.slimefun4.VaultIntegration;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.JEGVersionedItemFlag;
import com.balugaq.jeg.utils.LocalHelper;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.compatibility.Sounds;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This class used to create groups to display all the marked items in the guide.
 * Displayed items are already marked in {@link ItemMarkGroup}
 * Players can't open this group if players haven't marked any item.
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"deprecation", "unused", "UnnecessaryUnicodeEscape"})
@NotDisplayInSurvivalMode
@NotDisplayInCheatMode
public class BookmarkGroup extends FlexItemGroup {
    @Deprecated
    private static final int BACK_SLOT = 1;

    @Deprecated
    private static final int SEARCH_SLOT = 7;

    @Deprecated
    private static final int PREVIOUS_SLOT = 46;

    @Deprecated
    private static final int NEXT_SLOT = 52;

    @Deprecated
    private static final int[] BORDER = new int[]{0, 2, 3, 4, 5, 6, 8, 45, 47, 48, 49, 50, 51, 53};

    @Deprecated
    private static final int[] MAIN_CONTENT = new int[]{
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44
    };

    private static final JavaPlugin JAVA_PLUGIN = JustEnoughGuide.getInstance();
    private final SlimefunGuideImplementation implementation;
    private final Player player;
    private final int page;
    private final List<SlimefunItem> slimefunItemList;
    private Map<Integer, BookmarkGroup> pageMap = new LinkedHashMap<>();

    /**
     * Constructor of BookmarkGroup.
     *
     * @param implementation   The Slimefun guide implementation.
     * @param player           The player who opened the group.
     * @param slimefunItemList The list of marked items.
     */
    @ParametersAreNonnullByDefault
    public BookmarkGroup(
            final @NotNull SlimefunGuideImplementation implementation,
            final @NotNull Player player,
            final @NotNull List<SlimefunItem> slimefunItemList) {
        super(
                new NamespacedKey(JAVA_PLUGIN, "jeg_bookmark_group_" + UUID.randomUUID()),
                new ItemStack(Material.BARRIER));
        this.page = 1;
        this.player = player;
        this.implementation = implementation;
        this.slimefunItemList = slimefunItemList;
        this.pageMap.put(1, this);
    }

    /**
     * Constructor of BookmarkGroup.
     *
     * @param bookmarkGroup The BookmarkGroup to copy.
     * @param page          The page number to display.
     */
    protected BookmarkGroup(@NotNull BookmarkGroup bookmarkGroup, int page) {
        super(bookmarkGroup.key, new ItemStack(Material.BARRIER));
        this.page = page;
        this.player = bookmarkGroup.player;
        this.implementation = bookmarkGroup.implementation;
        this.slimefunItemList = bookmarkGroup.slimefunItemList;
        this.pageMap.put(page, this);
    }

    /**
     * Always returns false.
     *
     * @param player            The player who opened the group.
     * @param playerProfile     The player's profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     * @return false.
     */
    @Override
    public boolean isVisible(
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode) {
        return false;
    }

    /**
     * Opens the group for the player.
     *
     * @param player            The player who opened the group.
     * @param playerProfile     The player's profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     */
    @Override
    public void open(
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode) {
        playerProfile.getGuideHistory().add(this, this.page);
        this.generateMenu(player, playerProfile, slimefunGuideMode).open(player);
    }

    /**
     * Reopens the menu for the player.
     *
     * @param player            The player who opened the group.
     * @param playerProfile     The player's profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     */
    public void refresh(
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode) {
        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
        this.open(player, playerProfile, slimefunGuideMode);
    }

    /**
     * Generates the menu for the player.
     *
     * @param player            The player who opened the group.
     * @param playerProfile     The player's profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     * @return The generated menu.
     */
    @NotNull
    private ChestMenu generateMenu(
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu("收藏页 - JEG");

        chestMenu.setEmptySlotsClickable(false);
        chestMenu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sounds.GUIDE_BUTTON_CLICK_SOUND, 1, 1));

        for (int ss : Formats.sub.getChars('b')) {
            chestMenu.addItem(ss, PatchScope.Back.patch(playerProfile, ChestMenuUtils.getBackButton(player)));
            chestMenu.addMenuClickHandler(ss, (pl, s, is, action) -> EventUtil.callEvent(
                            new GuideEvents.BackButtonClickEvent(pl, is, s, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        GuideHistory guideHistory = playerProfile.getGuideHistory();
                        if (action.isShiftClicked()) {
                            SlimefunGuide.openMainMenu(
                                    playerProfile, slimefunGuideMode, guideHistory.getMainMenuPage());
                        } else {
                            guideHistory.goBack(Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode));
                        }
                        return false;
                    }));
        }

        // Search feature!
        for (int ss : Formats.sub.getChars('S')) {
            chestMenu.addItem(ss, PatchScope.Search.patch(playerProfile, ChestMenuUtils.getSearchButton(player)));
            chestMenu.addMenuClickHandler(ss, (pl, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.SearchButtonClickEvent(pl, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        pl.closeInventory();

                        Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                        ChatInput.waitForPlayer(
                                JAVA_PLUGIN,
                                pl,
                                msg -> implementation.openSearch(
                                        playerProfile,
                                        msg,
                                        implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE));

                        return false;
                    }));
        }

        for (int ss : Formats.sub.getChars('P')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.PreviousPage.patch(
                            playerProfile,
                            ChestMenuUtils.getPreviousButton(
                                    player,
                                    this.page,
                                    (this.slimefunItemList.size() - 1)
                                            / Formats.sub.getChars('i').size()
                                            + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.PreviousButtonClickEvent(p, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                        BookmarkGroup bookMarkGroup = this.getByPage(Math.max(this.page - 1, 1));
                        bookMarkGroup.open(player, playerProfile, slimefunGuideMode);
                        return false;
                    }));
        }

        for (int ss : Formats.sub.getChars('N')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.NextPage.patch(
                            playerProfile,
                            ChestMenuUtils.getNextButton(
                                    player,
                                    this.page,
                                    (this.slimefunItemList.size() - 1)
                                            / Formats.sub.getChars('i').size()
                                            + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.NextButtonClickEvent(p, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                        BookmarkGroup bookMarkGroup = this.getByPage(Math.min(
                                this.page + 1,
                                (this.slimefunItemList.size() - 1)
                                        / Formats.sub.getChars('i').size()
                                        + 1));
                        bookMarkGroup.open(player, playerProfile, slimefunGuideMode);
                        return false;
                    }));
        }

        for (int ss : Formats.sub.getChars('B')) {
            chestMenu.addItem(ss, PatchScope.Background.patch(playerProfile, ChestMenuUtils.getBackground()));
            chestMenu.addMenuClickHandler(ss, ChestMenuUtils.getEmptyClickHandler());
        }

        List<Integer> contentSlots = Formats.sub.getChars('i');
        for (int i = 0; i < contentSlots.size(); i++) {
            int index = i + this.page * contentSlots.size() - contentSlots.size();
            if (index < this.slimefunItemList.size()) {
                SlimefunItem slimefunItem = slimefunItemList.get(index);
                Research research = slimefunItem.getResearch();
                if (implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE
                        && research != null
                        && !playerProfile.hasUnlocked(research)) {
                    String lore;

                    if (VaultIntegration.isEnabled()) {
                        lore = String.format("%.2f", research.getCurrencyCost()) + " 游戏币";
                    } else {
                        lore = research.getLevelCost() + " 级经验";
                    }

                    var itemstack = ItemStackUtil.getCleanItem(Converter.getItem(
                            ChestMenuUtils.getNoPermissionItem(),
                            "&f" + ItemUtils.getItemName(slimefunItem.getItem()),
                            "&7" + slimefunItem.getId(),
                            "&4&l" + Slimefun.getLocalization().getMessage(player, "guide.locked"),
                            "",
                            "&a> 单击解锁",
                            "",
                            "&7需要 &b",
                            lore));
                    ChestMenu.MenuClickHandler handler = (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.ResearchItemEvent(
                                    pl, item, slot, action, chestMenu, implementation))
                            .ifSuccess(() -> {
                                research.unlockFromGuide(
                                        implementation,
                                        pl,
                                        playerProfile,
                                        slimefunItem,
                                        slimefunItem.getItemGroup(),
                                        page);
                                return false;
                            });

                    chestMenu.addItem(contentSlots.get(i), PatchScope.BookMarkItem.patch(player, itemstack), handler);
                } else {
                    var itemstack = ItemStackUtil.getCleanItem(Converter.getItem(slimefunItem.getItem(), meta -> {
                        ItemGroup itemGroup = slimefunItem.getItemGroup();
                        List<String> additionLore = List.of(
                                "",
                                ChatColor.DARK_GRAY + "\u21E8 " + ChatColor.WHITE
                                        + (LocalHelper.getAddonName(itemGroup, slimefunItem.getId())) + ChatColor.WHITE
                                        + " - "
                                        + LocalHelper.getDisplayName(itemGroup, player),
                                ChatColor.YELLOW + "右键以取消收藏物品");
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
                    }));
                    ChestMenu.AdvancedMenuClickHandler handler = new ChestMenu.AdvancedMenuClickHandler() {
                        @Override
                        public boolean onClick(InventoryClickEvent e, Player pl, int slot, ItemStack itm, ClickAction action) {
                            return EventUtil.callEvent(new GuideEvents.ItemButtonClickEvent(
                                            pl, itm, slot, action, chestMenu, implementation))
                                    .ifSuccess(() -> {
                                        try {
                                            if (action.isRightClicked()) {
                                                GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                                                JustEnoughGuide.getBookmarkManager().removeBookmark(player, slimefunItem);

                                                List<SlimefunItem> items = JustEnoughGuide.getBookmarkManager()
                                                        .getBookmarkedItems(player);
                                                if (items == null || items.isEmpty()) {
                                                    pl.closeInventory();
                                                    return false;
                                                }
                                                new BookmarkGroup(BookmarkGroup.this.implementation, BookmarkGroup.this.player, items)
                                                        .open(player, playerProfile, slimefunGuideMode);
                                            } else {
                                                if (!(implementation.getMode() == SlimefunGuideMode.CHEAT_MODE && (pl.isOp() || pl.hasPermission("slimefun.cheat.items")))) {
                                                    implementation.displayItem(playerProfile, slimefunItem, true);
                                                    return false;
                                                }

                                                ItemStack cursor = pl.getItemOnCursor();
                                                if (!(e.getClick() == ClickType.MIDDLE && (cursor == null || cursor.getType() == Material.AIR))) {
                                                    pl.getInventory().addItem(slimefunItem.getItem().clone());
                                                    return false;
                                                }

                                                if (slimefunItem instanceof MultiBlockMachine) {
                                                    Slimefun.getLocalization().sendMessage(pl, "guide.cheat.no-multiblocks");
                                                    return false;
                                                }

                                                ItemStack clonedItem = slimefunItem.getItem().clone();

                                                if (action.isShiftClicked()) {
                                                    clonedItem.setAmount(clonedItem.getMaxStackSize());
                                                }

                                                pl.setItemOnCursor(clonedItem);
                                            }
                                        } catch (Exception | LinkageError x) {
                                            printErrorMessage(pl, slimefunItem, x);
                                        }

                                        return false;
                                    });
                        }

                        @Override
                        public boolean onClick(Player player, int i, ItemStack itemStack, ClickAction clickAction) {
                            return false;
                        }
                    };

                    chestMenu.addItem(contentSlots.get(i), PatchScope.BookMarkItem.patch(player, itemstack), handler);
                }
            }
        }

        GuideUtil.addRTSButton(chestMenu, player, playerProfile, Formats.sub, slimefunGuideMode, implementation);
        if (implementation instanceof JEGSlimefunGuideImplementation jeg) {
            GuideUtil.addBookMarkButton(chestMenu, player, playerProfile, Formats.sub, jeg, this);
            GuideUtil.addItemMarkButton(chestMenu, player, playerProfile, Formats.sub, jeg, this);
        }

        Formats.sub.renderCustom(chestMenu);

        return chestMenu;
    }

    /**
     * Gets the BookmarkGroup by page.
     *
     * @param page The page number.
     * @return The BookmarkGroup by page.
     */
    @NotNull
    private BookmarkGroup getByPage(int page) {
        if (this.pageMap.containsKey(page)) {
            return this.pageMap.get(page);
        } else {
            synchronized (this.pageMap.get(1)) {
                if (this.pageMap.containsKey(page)) {
                    return this.pageMap.get(page);
                }

                BookmarkGroup bookMarkGroup = new BookmarkGroup(this, page);
                bookMarkGroup.pageMap = this.pageMap;
                this.pageMap.put(page, bookMarkGroup);
                return bookMarkGroup;
            }
        }
    }

    /**
     * Checks if the item group is accessible for the player.
     *
     * @param p            The player.
     * @param slimefunItem The Slimefun item.
     * @return True if the item group is accessible for the player.
     */
    @ParametersAreNonnullByDefault
    private boolean isItemGroupAccessible(@NotNull Player p, @NotNull SlimefunItem slimefunItem) {
        return Slimefun.getConfigManager().isShowHiddenItemGroupsInSearch()
                || slimefunItem.getItemGroup().isAccessible(p);
    }

    /**
     * Prints an error message to the player.
     *
     * @param p The player.
     * @param x The exception.
     */
    @ParametersAreNonnullByDefault
    private void printErrorMessage(@NotNull Player p, @NotNull Throwable x) {
        p.sendMessage("&4服务器发生了一个内部错误. 请联系管理员处理.");
        JAVA_PLUGIN.getLogger().log(Level.SEVERE, "在打开指南书里的 Slimefun 物品时发生了意外!", x);
    }

    /**
     * Prints an error message to the player.
     *
     * @param p    The player.
     * @param item The Slimefun item.
     * @param x    The exception.
     */
    @ParametersAreNonnullByDefault
    private void printErrorMessage(@NotNull Player p, @NotNull SlimefunItem item, @NotNull Throwable x) {
        p.sendMessage(ChatColor.DARK_RED
                + "An internal server error has occurred. Please inform an admin, check the console for"
                + " further info.");
        item.error(
                "This item has caused an error message to be thrown while viewing it in the Slimefun" + " guide.", x);
    }
}
