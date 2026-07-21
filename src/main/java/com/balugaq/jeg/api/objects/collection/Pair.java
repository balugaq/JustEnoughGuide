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

package com.balugaq.jeg.api.objects.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.9
 */
@AllArgsConstructor
@Data
@NullMarked
public class Pair<F, S> implements Cloneable {
    public final F first;
    public final S second;

    public F first() {
        return first;
    }

    public S second() {
        return second;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<F, S> clone() {
        try {
            return (Pair<F, S>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
