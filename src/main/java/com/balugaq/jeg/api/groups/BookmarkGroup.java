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

import com.balugaq.jeg.api.objects.collection.data.Bookmark;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.clickhandler.OnDisplay;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
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

        Format format = Formats.sub;
        int maxPage = (this.bookmarks.size() - 1) / format.getChars('i').size() + 1;
        GuideUtil.commonRender(chestMenu, format, playerProfile, player, this, this.page, maxPage);

        List<Integer> contentSlots = format.getChars('i');
        for (int i = 0; i < contentSlots.size(); i++) {
            int index = i + this.page * contentSlots.size() - contentSlots.size();
            if (index >= this.bookmarks.size()) {
                break;
            }

            Bookmark bookmark = bookmarks.get(index);
            if (bookmark instanceof Bookmark.Item bi) {
                SlimefunItem slimefunItem = bi.getSlimefunItem();
                OnDisplay.Item.display(player, slimefunItem.getItem(), OnDisplay.Item.Bookmark, implementation)
                    .at(chestMenu, contentSlots.get(i), page);
            }
            if (bookmark instanceof Bookmark.ItemGroup big) {
                ItemGroup itemGroup = big.getItemGroup();
                OnDisplay.ItemGroup.display(player, itemGroup, OnDisplay.ItemGroup.Bookmark, implementation)
                    .at(chestMenu, contentSlots.get(i), page);
            }
        }

        return chestMenu;
    }
}
