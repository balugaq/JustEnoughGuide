/*
 * Copyright (c) 2024-2025 balugaq
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
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * @author balugaq
 * @since 1.7
 */
@SuppressWarnings("unused")
@UtilityClass
public class KeyUtil {
    @NotNull public static NamespacedKey newKey(@NotNull String key) {
        return new NamespacedKey(JustEnoughGuide.getInstance(), key);
    }

    @ParametersAreNonnullByDefault
    @NotNull public static NamespacedKey customKey(Plugin plugin, String key) {
        return new NamespacedKey(plugin, key);
    }

    @ParametersAreNonnullByDefault
    @NotNull public static NamespacedKey customKey(String namespace, String key) {
        return new NamespacedKey(namespace, key);
    }
}
