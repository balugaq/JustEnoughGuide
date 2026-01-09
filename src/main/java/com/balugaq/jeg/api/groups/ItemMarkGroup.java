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

package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.BookmarkRelocation;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.balugaq.jeg.utils.clickhandler.OnDisplay;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * This class used to create groups to mark items into {@link BookmarkGroup} in the guide. Will not display Item Mark
 * Button in {@link NestedItemGroup}
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"deprecation", "unused"})
@NotDisplayInSurvivalMode
@NotDisplayInCheatMode
@NullMarked
public class ItemMarkGroup extends BaseGroup<ItemMarkGroup> {
    private final JEGSlimefunGuideImplementation implementation;
    private final Player player;
    private final ItemGroup itemGroup;
    private final List<SlimefunItem> slimefunItemList;

    public ItemMarkGroup(JEGSlimefunGuideImplementation implementation, ItemGroup itemGroup, Player player) {
        this(implementation, itemGroup, player, 1);
    }

    public ItemMarkGroup(JEGSlimefunGuideImplementation implementation, ItemGroup itemGroup, Player player, int page) {
        super();
        this.page = page;
        this.player = player;
        this.itemGroup = itemGroup;
        this.slimefunItemList = itemGroup.getItems();
        this.implementation = implementation;
        this.pageMap.put(page, this);
    }

    protected ItemMarkGroup(ItemMarkGroup itemMarkGroup, int page) {
        this(itemMarkGroup.implementation, itemMarkGroup.itemGroup, itemMarkGroup.player, page);
    }

    @Override
    public boolean isVisible(
            final Player player,
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode) {
        return false;
    }

    public ChestMenu generateMenu(
            final Player player,
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu("添加收藏物 - JEG");

        OnClick.preset(chestMenu);

        for (int ss : itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getBackButton(implementation, player)
                : Formats.sub.getChars('b')) {
            chestMenu.addItem(ss, PatchScope.Back.patch(player, ChestMenuUtils.getBackButton(player)));
            chestMenu.addMenuClickHandler(
                    ss, (pl, s, is, action) -> EventUtil.callEvent(
                                    new GuideEvents.BackButtonClickEvent(pl, is, s, action, chestMenu, implementation))
                            .ifSuccess(() -> {
                                GuideHistory guideHistory = playerProfile.getGuideHistory();
                                if (action.isShiftClicked()) {
                                    SlimefunGuide.openMainMenu(
                                            playerProfile, slimefunGuideMode, guideHistory.getMainMenuPage());
                                } else {
                                    GuideUtil.goBack(guideHistory);
                                }
                                return false;
                            })
            );
        }

        for (int ss : itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getSearchButton(implementation, player)
                : Formats.sub.getChars('S')) {
            chestMenu.addItem(ss, PatchScope.Search.patch(player, ChestMenuUtils.getSearchButton(player)));
            chestMenu.addMenuClickHandler(
                    ss, (pl, slot, item, action) -> EventUtil.callEvent(
                                    new GuideEvents.SearchButtonClickEvent(
                                            pl, item, slot, action, chestMenu,
                                            implementation
                                    ))
                            .ifSuccess(() -> {
                                pl.closeInventory();

                                Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                                ChatInput.waitForPlayer(
                                        JustEnoughGuide.getInstance(),
                                        pl,
                                        msg -> implementation.openSearch(
                                                playerProfile,
                                                msg,
                                                true
                                        )
                                );

                                return false;
                            })
            );
        }

        for (int ss : itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getPreviousButton(implementation, player)
                : Formats.sub.getChars('P')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.PreviousPage.patch(
                            player,
                            ChestMenuUtils.getPreviousButton(
                                    player,
                                    this.page,
                                    (this.slimefunItemList.size() - 1)
                                            / Formats.sub.getChars('i').size()
                                            + 1
                            )
                    )
            );
            chestMenu.addMenuClickHandler(
                    ss, (p, slot, item, action) -> EventUtil.callEvent(
                                    new GuideEvents.PreviousButtonClickEvent(
                                            p, item, slot, action, chestMenu,
                                            implementation
                                    ))
                            .ifSuccess(() -> {
                                GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                                ItemMarkGroup itemMarkGroup = this.getByPage(Math.max(this.page - 1, 1));
                                itemMarkGroup.open(player, playerProfile, slimefunGuideMode);
                                return false;
                            })
            );
        }

        for (int ss : itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getNextButton(implementation, player)
                : Formats.sub.getChars('N')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.NextPage.patch(
                            player,
                            ChestMenuUtils.getNextButton(
                                    player,
                                    this.page,
                                    (this.slimefunItemList.size() - 1)
                                            / Formats.sub.getChars('i').size()
                                            + 1
                            )
                    )
            );
            chestMenu.addMenuClickHandler(
                    ss, (p, slot, item, action) -> EventUtil.callEvent(
                                    new GuideEvents.NextButtonClickEvent(
                                            p, item, slot, action, chestMenu,
                                            implementation
                                    ))
                            .ifSuccess(() -> {
                                GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                                ItemMarkGroup itemMarkGroup = this.getByPage(Math.min(
                                        this.page + 1,
                                        (this.slimefunItemList.size() - 1)
                                                / Formats.sub.getChars('i').size()
                                                + 1
                                ));
                                itemMarkGroup.open(player, playerProfile, slimefunGuideMode);
                                return false;
                            })
            );
        }

        for (int ss : itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getBorder(implementation, player)
                : Formats.sub.getChars('B')) {
            chestMenu.addItem(ss, PatchScope.Background.patch(player, Models.ITEM_MARK_BACKGROUND));
            chestMenu.addMenuClickHandler(ss, ChestMenuUtils.getEmptyClickHandler());
        }

        List<Integer> contentSlots = itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getMainContents(implementation, player)
                : Formats.sub.getChars('i');

        for (int i = 0; i < contentSlots.size(); i++) {
            int index = i + this.page * contentSlots.size() - contentSlots.size();
            if (index < this.slimefunItemList.size()) {
                SlimefunItem slimefunItem = slimefunItemList.get(index);
                OnDisplay.Item.display(player, slimefunItem, OnDisplay.Item.ItemMark, implementation)
                        .at(chestMenu, contentSlots.get(i), page);
            }
        }

        GuideUtil.addRTSButton(chestMenu, player, playerProfile, Formats.sub, slimefunGuideMode, implementation);
        GuideUtil.addBookMarkButton(chestMenu, player, playerProfile, Formats.sub, implementation, this);
        GuideUtil.addItemMarkButton(chestMenu, player, playerProfile, Formats.sub, implementation, this);

        Formats.sub.renderCustom(chestMenu);
        return chestMenu;
    }
}
