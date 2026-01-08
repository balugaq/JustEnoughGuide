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

import com.balugaq.jeg.implementation.JustEnoughGuide;
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
        MinecraftVersion version = JustEnoughGuide.getMinecraftVersion();
        HIDE_ADDITIONAL_TOOLTIP = version.isAtLeast(MinecraftVersion.V1_20_5)
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
