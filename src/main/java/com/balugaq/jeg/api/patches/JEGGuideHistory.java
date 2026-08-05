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
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.SpecialMenuProvider;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import javax.annotation.Nullable;
import java.util.Deque;
import java.util.LinkedList;

/**
 * @author balugaq
 * @since 2.1
 */
@NullMarked
public class JEGGuideHistory extends GuideHistory {
    @Getter
    private final PlayerProfile profile;

    @Getter
    private final Deque<JEGGuideEntry<?>> queue = new LinkedList<>();

    public JEGGuideHistory(PlayerProfile profile) {
        super(profile);
        this.profile = profile;
    }

    @Override
    public void clear() {
        this.queue.clear();
    }

    @Override
    public int size() {
        return this.queue.size();
    }

    public void removeLastEntry() {
        if (queue.isEmpty()) return;
        queue.removeLast();
    }

    @Nullable
    public JEGGuideEntry<?> getLastEntry(boolean doRemoveFirst) {
        if (doRemoveFirst && !queue.isEmpty()) {
            queue.removeLast();
        }

        return queue.isEmpty() ? null : queue.getLast();
    }

    @Override
    public void openLastEntry(SlimefunGuideImplementation guide) {
        openEntry(getLastEntry(false), guide);
    }

    private void openEntry(@Nullable JEGGuideEntry<?> entry, SlimefunGuideImplementation guide) {
        var jeg = GuideUtil.asJEG(guide);
        if (jeg == null) {
            Debug.severe("Cannot open GuideHistory entry because the guide is not a JEGSlimefunGuideImplementation!");
            return;
        }
        openEntry(entry, jeg);
    }

    private void openEntry(@Nullable JEGGuideEntry<?> entry, JEGSlimefunGuideImplementation jeg) {
        if (entry == null) {
            jeg.openMainMenu(profile, getMainMenuPage());
            return;
        }

        if (!entry.open(jeg, profile)) {
            goBack(jeg);
        }
    }

    @Override
    public void add(ItemGroup itemGroup, int page) {
        setPageOrAddEntry(itemGroup, page, () -> new JEGGuideEntry.ItemGroupEntry(itemGroup, page));
    }

    @Override
    public void add(ItemStack item, int page) {
        setPageOrAddEntry(item, page, () -> new JEGGuideEntry.ItemStackEntry(item, page));
    }

    @Override
    public void add(SlimefunItem item) {
        add(item, 0);
    }

    public void add(SlimefunItem item, int page) {
        Validate.notNull(item, "Cannot add a non-existing SlimefunItem to the GuideHistory!");
        setPageOrAddEntry(item, page, () -> new JEGGuideEntry.SlimefunItemEntry(item, page));
    }

    @Override
    public void add(String searchTerm) {
        Validate.notNull(searchTerm, "Cannot add an empty Search Term to the GuideHistory!");
        queue.add(new JEGGuideEntry.SearchTermEntry(searchTerm));
    }

    protected <T> void setPageOrAddEntry(T indexedObject, int page, EntryCreator<T> entryCreator) {
        var entry = getLastEntry(false);
        if (entry == null) {
            // add new entry
            queue.add(entryCreator.createEntry());
            return;
        }

        if (!(entry instanceof JEGGuideEntry.PagedGuideEntry<?> paged)) {
            // re-add it
            removeLastEntry();
            queue.add(entryCreator.createEntry());
            return;
        }

        var obj = entry.get();
        if (indexedObject.equals(obj)) {
            // just set page of existing one
            paged.setPage(page);
            return;
        }

        // add new entry
        queue.add(entryCreator.createEntry());
    }

    public void goBack() {
        goBack(GuideUtil.getLastGuide(profile.getPlayer()));
    }

    @Override
    public void goBack(SlimefunGuideImplementation guide) {
        if (guide instanceof JEGSlimefunGuideImplementation jeg) {
            goBack(jeg);
            return;
        }

        super.goBack(guide);
    }

    public void goBack(JEGSlimefunGuideImplementation guide) {
        openEntry(getLastEntry(true), guide);
    }

    public void removeTailPlaceholders() {
        while (!queue.isEmpty() && queue.getLast() instanceof JEGGuideEntry.SearchTermEntry entry) {
            var obj = entry.get();
            if (obj != null && obj.equals(SpecialMenuProvider.PLACEHOLDER_SEARCH_TERM)) removeLastEntry();
            else return;
        }
    }
}
