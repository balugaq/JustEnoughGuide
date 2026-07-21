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
