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

import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.JEGVersionedItemFlag;
import com.balugaq.jeg.utils.Lang;
import com.balugaq.jeg.utils.SlimefunOfficialSupporter;
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
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * This class used to create groups to display all the Nexcavate items in the guide.
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"deprecation", "unused"})
@NotDisplayInSurvivalMode
public class NexcavateItemsGroup extends FlexItemGroup {
    private static final List<SlimefunItem> ALL_SLIMEFUN_ITEMS = Slimefun.getRegistry().getAllSlimefunItems();
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
    private final int page;
    private final List<SlimefunItem> slimefunItemList;
    private Map<Integer, NexcavateItemsGroup> pageMap = new LinkedHashMap<>();

    @ParametersAreNonnullByDefault
    public NexcavateItemsGroup(NamespacedKey key, ItemStack icon) {
        super(key, icon);
        this.page = 1;
        List<SlimefunItem> slimefunItemList = new ArrayList<>();
        for (SlimefunItem item : ALL_SLIMEFUN_ITEMS) {
            if ("nexcavate".equalsIgnoreCase(item.getAddon().getName())) {
                slimefunItemList.add(item);
            }
        }
        this.slimefunItemList = slimefunItemList;
        this.pageMap.put(1, this);
    }

    /**
     * Constructor of nexvacateItemsGroup.
     *
     * @param nexcavateItemsGroup The nexvacateItemsGroup to copy.
     * @param page                The page number to display.
     */
    protected NexcavateItemsGroup(@NotNull NexcavateItemsGroup nexcavateItemsGroup, int page) {
        super(nexcavateItemsGroup.key, new ItemStack(Material.BARRIER));
        this.page = page;
        this.slimefunItemList = nexcavateItemsGroup.slimefunItemList;
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
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        return true;
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
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
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
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
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
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu("Nexcavate Items");

        chestMenu.setEmptySlotsClickable(false);
        chestMenu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sounds.GUIDE_BUTTON_CLICK_SOUND, 1, 1));

        for (var ss : Formats.sub.getChars('b')) {
            chestMenu.addItem(ss, ItemStackUtil.getCleanItem(ChestMenuUtils.getBackButton(player)));
            chestMenu.addMenuClickHandler(ss, (pl, s, is, action) -> {
                GuideHistory guideHistory = playerProfile.getGuideHistory();
                if (action.isShiftClicked()) {
                    SlimefunGuide.openMainMenu(playerProfile, slimefunGuideMode, guideHistory.getMainMenuPage());
                } else {
                    guideHistory.goBack(Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.CHEAT_MODE));
                }
                return false;
            });
        }

        SlimefunGuideImplementation implementation = Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.CHEAT_MODE);

        // Search feature!
        for (var ss : Formats.sub.getChars('S')) {
            chestMenu.addItem(ss, ItemStackUtil.getCleanItem(ChestMenuUtils.getSearchButton(player)));
            chestMenu.addMenuClickHandler(ss, (pl, slot, item, action) -> {
                pl.closeInventory();

                Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                ChatInput.waitForPlayer(
                        JAVA_PLUGIN,
                        pl,
                        msg -> implementation.openSearch(
                                playerProfile, msg, implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE));

                return false;
            });
        }

        for (var ss : Formats.sub.getChars('P')) {
            chestMenu.addItem(
                    ss,
                    ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(
                            player, this.page, (this.slimefunItemList.size() - 1) / Formats.sub.getChars('i').size() + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> {
                GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                NexcavateItemsGroup nexcavateItemsGroup = this.getByPage(Math.max(this.page - 1, 1));
                nexcavateItemsGroup.open(player, playerProfile, slimefunGuideMode);
                return false;
            });
        }

        for (var ss : Formats.sub.getChars('N')) {
            chestMenu.addItem(
                    ss,
                    ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(
                            player, this.page, (this.slimefunItemList.size() - 1) / Formats.sub.getChars('i').size() + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> {
                GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                NexcavateItemsGroup nexcavateItemsGroup = this.getByPage(
                        Math.min(this.page + 1, (this.slimefunItemList.size() - 1) / Formats.sub.getChars('i').size() + 1));
                nexcavateItemsGroup.open(player, playerProfile, slimefunGuideMode);
                return false;
            });
        }

        for (var ss : Formats.sub.getChars('B')) {
            chestMenu.addItem(ss, ItemStackUtil.getCleanItem(ChestMenuUtils.getBackground()));
            chestMenu.addMenuClickHandler(ss, ChestMenuUtils.getEmptyClickHandler());
        }

        var contentSlots = Formats.sub.getChars('i');
        for (int i = 0; i < contentSlots.size(); i++) {
            int index = i + this.page * contentSlots.size() - contentSlots.size();
            if (index < this.slimefunItemList.size()) {
                SlimefunItem slimefunItem = slimefunItemList.get(index);
                Research research = slimefunItem.getResearch();
                ItemStack itemstack;
                ChestMenu.MenuClickHandler handler;
                if (implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE
                        && research != null
                        && !playerProfile.hasUnlocked(research)) {

                    itemstack = ItemStackUtil.getCleanItem(Converter.getItem(
                            ChestMenuUtils.getNoPermissionItem(),
                            "&f" + ItemUtils.getItemName(slimefunItem.getItem()),
                            "&7" + slimefunItem.getId(),
                            "&4&l" + Slimefun.getLocalization().getMessage(player, "guide.locked"),
                            "",
                            Lang.getGuideMessage("click-to-unlock"),
                            "",
                            Lang.getGuideMessage("cost", "cost", research.getCost())));
                    handler = (pl, slot, item, action) -> {
                        research.unlockFromGuide(
                                implementation, pl, playerProfile, slimefunItem, slimefunItem.getItemGroup(), page);
                        return false;
                    };
                } else {
                    itemstack = ItemStackUtil.getCleanItem(Converter.getItem(slimefunItem.getItem(), meta -> {
                        ItemGroup itemGroup = slimefunItem.getItemGroup();
                        if (meta.hasLore() && meta.getLore() != null) {
                            List<String> lore = meta.getLore();
                            meta.setLore(lore);
                        }

                        meta.addItemFlags(
                                ItemFlag.HIDE_ATTRIBUTES,
                                ItemFlag.HIDE_ENCHANTS,
                                JEGVersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                    }));
                    handler = (pl, slot, itm, action) -> {
                        try {
                            if (implementation.getMode() != SlimefunGuideMode.SURVIVAL_MODE
                                    && (pl.isOp() || pl.hasPermission("slimefun.cheat.items"))) {
                                pl.getInventory()
                                        .addItem(slimefunItem.getItem().clone());
                            } else {
                                implementation.displayItem(playerProfile, slimefunItem, true);
                            }
                        } catch (Exception | LinkageError x) {
                            printErrorMessage(pl, slimefunItem, x);
                        }

                        return false;
                    };
                }

                chestMenu.addItem(contentSlots.get(i), ItemStackUtil.getCleanItem(itemstack), handler);
            }
        }

        GuideUtil.addRTSButton(chestMenu, player, playerProfile, Formats.sub, slimefunGuideMode, implementation);
        if (implementation instanceof JEGSlimefunGuideImplementation jeg) {
            GuideUtil.addBookMarkButton(chestMenu, player, playerProfile, Formats.sub, jeg, this);
            GuideUtil.addItemMarkButton(chestMenu, player, playerProfile, Formats.sub, jeg, this);
        }

        return chestMenu;
    }

    /**
     * Gets the nexvacateItemsGroup by page.
     *
     * @param page The page number.
     * @return The nexvacateItemsGroup by page.
     */
    @NotNull
    private NexcavateItemsGroup getByPage(int page) {
        if (this.pageMap.containsKey(page)) {
            return this.pageMap.get(page);
        } else {
            synchronized (this.pageMap.get(1)) {
                if (this.pageMap.containsKey(page)) {
                    return this.pageMap.get(page);
                }

                NexcavateItemsGroup nexcavateItemsGroup = new NexcavateItemsGroup(this, page);
                nexcavateItemsGroup.pageMap = this.pageMap;
                this.pageMap.put(page, nexcavateItemsGroup);
                return nexcavateItemsGroup;
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
        return SlimefunOfficialSupporter.isShowHiddenItemGroups()
                || slimefunItem.getItemGroup().isAccessible(p);
    }

    @ParametersAreNonnullByDefault
    private void printErrorMessage(Player p, Throwable x) {
        p.sendMessage(Lang.getError("internal-error"));
        JustEnoughGuide.getInstance().getLogger().log(Level.SEVERE, Lang.getError("error-occurred"), x);
        JustEnoughGuide.getInstance().getLogger().warning(Lang.getError("trying-fix-guide", "player_name", p.getName()));
        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.removeLastEntry(profile.getGuideHistory());
    }

    @ParametersAreNonnullByDefault
    private void printErrorMessage(Player p, SlimefunItem item, Throwable x) {
        p.sendMessage(Lang.getError("internal-error"));
        item.error(Lang.getError("item-error"), x);
        JustEnoughGuide.getInstance()
                .getLogger()
                .warning(Lang.getError("trying-fix-guide", "player_name", p.getName()));
        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.removeLastEntry(profile.getGuideHistory());
    }

    @Override
    public boolean isCrossAddonItemGroup() {
        return true;
    }

    @Override
    public int getTier() {
        return Integer.MAX_VALUE;
    }
}
