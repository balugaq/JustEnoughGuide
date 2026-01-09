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

import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.objects.collection.data.Bookmark;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.balugaq.jeg.utils.clickhandler.OnDisplay;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * This class used to create groups to display all the marked items in the guide. Displayed items are already marked in
 * {@link ItemMarkGroup} Players can't open this group if players haven't marked any item.
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"deprecation", "unused"})
@NotDisplayInSurvivalMode
@NotDisplayInCheatMode
@NullMarked
public class BookmarkGroup extends BaseGroup<BookmarkGroup> {
    private final SlimefunGuideImplementation implementation;
    @Getter
    private final List<Bookmark> bookmarks;

    public BookmarkGroup(
            final SlimefunGuideImplementation implementation,
            final List<Bookmark> bookmarks) {
        super();
        this.page = 1;
        this.implementation = implementation;
        this.bookmarks = bookmarks;
        this.pageMap.put(1, this);
    }

    @Override
    public ChestMenu generateMenu(
            final Player player,
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu("收藏页 - JEG");

        OnClick.preset(chestMenu);

        for (int ss : Formats.sub.getChars('b')) {
            chestMenu.addItem(ss, PatchScope.Back.patch(playerProfile, ChestMenuUtils.getBackButton(player)));
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

        for (int ss : Formats.sub.getChars('S')) {
            chestMenu.addItem(ss, PatchScope.Search.patch(playerProfile, ChestMenuUtils.getSearchButton(player)));
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

        for (int ss : Formats.sub.getChars('P')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.PreviousPage.patch(
                            playerProfile,
                            ChestMenuUtils.getPreviousButton(
                                    player,
                                    this.page,
                                    (this.bookmarks.size() - 1)
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
                                BookmarkGroup bookMarkGroup = this.getByPage(Math.max(this.page - 1, 1));
                                bookMarkGroup.open(player, playerProfile, slimefunGuideMode);
                                return false;
                            })
            );
        }

        for (int ss : Formats.sub.getChars('N')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.NextPage.patch(
                            playerProfile,
                            ChestMenuUtils.getNextButton(
                                    player,
                                    this.page,
                                    (this.bookmarks.size() - 1)
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
                                BookmarkGroup bookMarkGroup = this.getByPage(Math.min(
                                        this.page + 1,
                                        (this.bookmarks.size() - 1)
                                                / Formats.sub.getChars('i').size()
                                                + 1
                                ));
                                bookMarkGroup.open(player, playerProfile, slimefunGuideMode);
                                return false;
                            })
            );
        }

        for (int ss : Formats.sub.getChars('B')) {
            chestMenu.addItem(ss, PatchScope.Background.patch(playerProfile, ChestMenuUtils.getBackground()));
            chestMenu.addMenuClickHandler(ss, ChestMenuUtils.getEmptyClickHandler());
        }

        List<Integer> contentSlots = Formats.sub.getChars('i');
        for (int i = 0; i < contentSlots.size(); i++) {
            int index = i + this.page * contentSlots.size() - contentSlots.size();
            if (index < this.bookmarks.size()) {
                Bookmark bookmark = bookmarks.get(index);
                if (bookmark instanceof Bookmark.Item bi) {
                    SlimefunItem slimefunItem = bi.getSlimefunItem();
                    OnDisplay.Item.display(
                                    player, Converter.getItem(slimefunItem.getItem()), OnDisplay.Item.Bookmark
                                    , implementation
                            )
                            .at(chestMenu, contentSlots.get(i), page);
                }
                if (bookmark instanceof Bookmark.ItemGroup big) {
                    ItemGroup itemGroup = big.getItemGroup();
                    OnDisplay.ItemGroup.display(player, itemGroup, OnDisplay.ItemGroup.Bookmark, implementation)
                            .at(chestMenu, contentSlots.get(i), page);
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
}
