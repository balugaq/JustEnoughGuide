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
