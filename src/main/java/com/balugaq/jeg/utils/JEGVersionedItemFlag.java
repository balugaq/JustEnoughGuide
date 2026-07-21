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

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemFlag;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Field;

/**
 * This class provides a way to access the ItemFlag constants that were added in different versions of Minecraft. Used
 * to fix compatibility issues with different versions of Minecraft.
 *
 * @author balugaq
 * @since 1.2
 */
@UtilityClass
@NullMarked
public class JEGVersionedItemFlag {
    public static final ItemFlag HIDE_ADDITIONAL_TOOLTIP;

    static {
        HIDE_ADDITIONAL_TOOLTIP = MinecraftVersion.current().isAtLeast(MinecraftVersion.V1_20_5)
            ? getKey("HIDE_ADDITIONAL_TOOLTIP")
            : getKey("HIDE_POTION_EFFECTS");
    }

    @SuppressWarnings("DataFlowIssue")
    private static ItemFlag getKey(String key) {
        try {
            Field field = ItemFlag.class.getDeclaredField(key);
            return (ItemFlag) field.get(null);
        } catch (Exception ignored) {
            return null;
        }
    }
}
