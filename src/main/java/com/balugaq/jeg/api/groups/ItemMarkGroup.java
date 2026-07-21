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

package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.BookmarkRelocation;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.balugaq.jeg.utils.clickhandler.OnDisplay;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * This class used to create groups to mark items into {@link BookmarkGroup} in the guide.
 * Will not display Item Mark Button in {@link NestedItemGroup}
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"deprecation", "unused"})
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

        Format format = Formats.sub;
        OnClick.preset(chestMenu);

        GuideUtil.addBackButton(
            chestMenu,
            itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getBackButton(implementation, player)
                : format.getChars(Formats.Char.BACK),
            playerProfile,
            player
        );

        GuideUtil.addSettingsPanelButton(chestMenu, format, playerProfile, player);

        GuideUtil.addSearchButton(
            chestMenu,
            itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getSearchButton(implementation, player)
                : format.getChars(Formats.Char.SEARCH),
            playerProfile,
            player
        );

        int maxPage = (this.slimefunItemList.size() - 1) / format.getChars(Formats.Char.CONTENT).size() + 1;
        GuideUtil.addPreviousPageButton(
            chestMenu,
            itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getPreviousButton(implementation, player)
                : format.getChars(Formats.Char.PREVIOUS_PAGE),
            playerProfile,
            player,
            this,
            page,
            maxPage
        );

        GuideUtil.addNextPageButton(
            chestMenu,
            itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getNextButton(implementation, player)
                : format.getChars(Formats.Char.NEXT_PAGE),
            playerProfile,
            player,
            this,
            page,
            maxPage
        );

        GuideUtil.addBackgroundItems(
            chestMenu,
            itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getBorder(implementation, player)
                : format.getChars(Formats.Char.BACKGROUND),
            playerProfile,
            Models.ITEM_MARK_BACKGROUND
        );

        List<Integer> contentSlots = itemGroup instanceof BookmarkRelocation relocation
            ? relocation.getMainContents(implementation, player)
            : format.getChars(Formats.Char.CONTENT);

        for (int i = 0; i < contentSlots.size(); i++) {
            int index = i + this.page * contentSlots.size() - contentSlots.size();
            if (index < this.slimefunItemList.size()) {
                SlimefunItem slimefunItem = slimefunItemList.get(index);
                OnDisplay.Item.display(player, slimefunItem, OnDisplay.Item.ItemMark, implementation)
                    .at(chestMenu, contentSlots.get(i), page);
            }
        }

        GuideUtil.addRTSButton(chestMenu, format, playerProfile, player);
        GuideUtil.addBookMarkButton(chestMenu, format, playerProfile, player, this);
        GuideUtil.addItemMarkButton(chestMenu, format, playerProfile, player, this);

        if (!(itemGroup instanceof BookmarkRelocation)) {
            format.renderCustom(chestMenu);
        }
        return chestMenu;
    }
}
