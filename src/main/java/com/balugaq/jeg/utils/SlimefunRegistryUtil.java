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

package com.balugaq.jeg.utils;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"unused", "ConstantValue"})
@UtilityClass
@NullMarked
public class SlimefunRegistryUtil {
    public static SlimefunItem registerItem(SlimefunItem item, SlimefunAddon addon) {
        item.register(addon);
        return item;
    }

    public static void unregisterItems(SlimefunAddon addon) {
        List<SlimefunItem> copy = new ArrayList<>(Slimefun.getRegistry().getAllSlimefunItems());
        for (SlimefunItem item : copy) {
            if (item.getAddon().equals(addon)) {
                unregisterItem(item);
            }
        }
    }

    public static void unregisterItem(SlimefunItem item) {
        if (item == null) {
            return;
        }

        if (item instanceof Radioactive) {
            synchronized (Slimefun.getRegistry().getRadioactiveItems()) {
                Slimefun.getRegistry().getRadioactiveItems().remove(item);
            }
        }

        if (item instanceof GEOResource geor) {
            synchronized (Slimefun.getRegistry().getGEOResources()) {
                Slimefun.getRegistry().getGEOResources().remove(geor.getKey());
            }
        }

        synchronized (Slimefun.getRegistry().getTickerBlocks()) {
            Slimefun.getRegistry().getTickerBlocks().remove(item.getId());
        }
        synchronized (Slimefun.getRegistry().getEnabledSlimefunItems()) {
            Slimefun.getRegistry().getEnabledSlimefunItems().remove(item);
        }

        synchronized (Slimefun.getRegistry().getSlimefunItemIds()) {
            Slimefun.getRegistry().getSlimefunItemIds().remove(item.getId());
        }
        synchronized (Slimefun.getRegistry().getAllSlimefunItems()) {
            Slimefun.getRegistry().getAllSlimefunItems().remove(item);
        }
        synchronized (Slimefun.getRegistry().getMenuPresets()) {
            Slimefun.getRegistry().getMenuPresets().remove(item.getId());
        }
        synchronized (Slimefun.getRegistry().getBarteringDrops()) {
            Slimefun.getRegistry().getBarteringDrops().remove(item.getItem());
        }
    }

    public static void unregisterItemGroups(SlimefunAddon addon) {
        List<ItemGroup> copy;
        synchronized (Slimefun.getRegistry().getAllItemGroups()) {
            copy = new ArrayList<>(Slimefun.getRegistry().getAllItemGroups());
        }
        for (ItemGroup itemGroup : copy) {
            if (Objects.equals(itemGroup.getAddon(), addon)) {
                unregisterItemGroup(itemGroup);
            }
        }
    }

    public static void unregisterItemGroup(ItemGroup itemGroup) {
        if (itemGroup == null) {
            return;
        }

        synchronized (Slimefun.getRegistry().getAllItemGroups()) {
            Slimefun.getRegistry().getAllItemGroups().remove(itemGroup);
        }
    }
}
