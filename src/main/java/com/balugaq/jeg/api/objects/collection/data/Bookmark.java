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

package com.balugaq.jeg.api.objects.collection.data;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.0
 */
@NullMarked
public interface Bookmark {
    static Item of(SlimefunItem slimefunItem) {
        return new Item(slimefunItem);
    }

    static ItemGroup of(io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup) {
        return new ItemGroup(itemGroup);
    }

    /**
     * @author balugaq
     * @since 2.0
     */
    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    @Data
    @NullMarked
    class Item implements Bookmark {
        final SlimefunItem slimefunItem;
    }

    /**
     * @author balugaq
     * @since 2.0
     */
    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    @Data
    @NullMarked
    class ItemGroup implements Bookmark {
        final io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup;
    }
}
