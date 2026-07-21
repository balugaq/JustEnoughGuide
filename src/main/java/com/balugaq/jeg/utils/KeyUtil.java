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

import com.balugaq.jeg.implementation.JustEnoughGuide;
import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

/**
 * @author balugaq
 * @since 1.7
 */
@SuppressWarnings("unused")
@UtilityClass
@NullMarked
public class KeyUtil {
    @Unmodifiable
    private static final NamespacedKey PLACEHOLDER_KEY = KeyUtil.newKey("placeholder");

    public static NamespacedKey placeholder() {
        return PLACEHOLDER_KEY;
    }

    public static NamespacedKey random() {
        return newKey(UUID.randomUUID().toString());
    }

    public static NamespacedKey newKey(String key) {
        return customKey(JustEnoughGuide.getInstance(), key);
    }

    public static NamespacedKey customKey(Plugin plugin, String key) {
        return new NamespacedKey(plugin, key);
    }

    public static NamespacedKey append(NamespacedKey key, String append) {
        return customKey(key.getNamespace(), key.getKey() + append);
    }

    public static NamespacedKey customKey(String namespace, String key) {
        return new NamespacedKey(namespace, key);
    }
}
