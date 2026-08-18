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

package com.balugaq.jeg.api.cfgparse.annotations;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author balugaq
 * @since 1.9
 */
@NullMarked
@SuppressWarnings("unused")
public interface IParsable {
    static @Nullable String[] fieldNames(final Class<? extends IParsable> clazz) {
        try {
            clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No no-arg constructor found in " + clazz.getName(), e);
        }

        return Arrays.stream(clazz.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Key.class))
            .map(Field::getName)
            .toArray(String[]::new);
    }
}
