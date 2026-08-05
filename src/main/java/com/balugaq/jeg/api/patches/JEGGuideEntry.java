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

package com.balugaq.jeg.api.patches;

import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.utils.GuideUtil;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @param <T> inner object type
 * @since 2.1
 */
@NullMarked
public interface JEGGuideEntry<T> {
    @Contract(pure = true) T get();

    /**
     * @return true if the entry was successfully navigated to
     */
    boolean open(JEGSlimefunGuideImplementation guide, PlayerProfile profile);

    /**
     * @author balugaq
     * @since 2.1
     */
    @NullMarked
    interface PagedGuideEntry<T> extends JEGGuideEntry<T> {
        @Contract(pure = true)
        @Range(from = 0, to = Integer.MAX_VALUE)
        int getPage();

        void setPage(@Range(from = 0, to = Integer.MAX_VALUE) int page);
    }

    /**
     * @author balugaq
     * @since 2.1
     */
    @NullMarked
    @RequiredArgsConstructor
    abstract class JEGGuideEntryImpl<T> implements JEGGuideEntry<T> {
        @Getter
        public final T obj;

        @Override
        public T get() {
            return obj;
        }
    }

    /**
     * @author balugaq
     * @since 2.1
     */
    @NullMarked
    class ItemGroupEntry extends JEGGuideEntryImpl<ItemGroup> implements PagedGuideEntry<ItemGroup> {
        @Getter
        @Setter
        private int page;
        public ItemGroupEntry(ItemGroup itemGroup, int page) {
            super(itemGroup);
            this.page = page;
        }

        @Override
        public boolean open(JEGSlimefunGuideImplementation guide, PlayerProfile profile) {
            var obj = GuideUtil.refreshGroup(get());
            guide.openItemGroup(profile, obj, page);
            return true;
        }
    }

    /**
     * @author balugaq
     * @since 2.1
     */
    @NullMarked
    class SearchTermEntry extends JEGGuideEntryImpl<String> {
        public SearchTermEntry(String searchTerm) {
            super(searchTerm);
        }

        @Override
        public boolean open(JEGSlimefunGuideImplementation guide, PlayerProfile profile) {
            var obj = get();
            guide.openSearch(profile, obj, false);
            return true;
        }
    }

    /**
     * @author balugaq
     * @since 2.1
     */
    @NullMarked
    class ItemStackEntry extends JEGGuideEntryImpl<ItemStack> implements PagedGuideEntry<ItemStack> {
        @Getter
        @Setter
        private int page;

        public ItemStackEntry(ItemStack stack, int page) {
            super(stack);
            this.page = page;
        }

        @Override
        public boolean open(JEGSlimefunGuideImplementation guide, PlayerProfile profile) {
            var obj = get();
            guide.displayItem(profile, obj, page, false);
            return true;
        }
    }

    /**
     * @author balugaq
     * @since 2.1
     */
    @NullMarked
    class SlimefunItemEntry extends JEGGuideEntryImpl<SlimefunItem> implements PagedGuideEntry<SlimefunItem> {
        @Getter
        @Setter
        private int page;

        public SlimefunItemEntry(SlimefunItem item, int page) {
            super(item);
            this.page = page;
        }

        @Override
        public boolean open(JEGSlimefunGuideImplementation guide, PlayerProfile profile) {
            var obj = GuideUtil.refreshSlimefunItem(get());
            guide.displayItem(profile, obj, false);
            return true;
        }
    }
}
