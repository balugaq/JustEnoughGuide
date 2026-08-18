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

package com.balugaq.jeg.utils.clickhandler;

import com.balugaq.jeg.utils.KeyUtil;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.function.Supplier;

/**
 * @author balugaq
 * @since 2.0
 */
@ApiStatus.Internal
@NullMarked
public interface ActionKey {
    static ActionKey of(Supplier<OnClick> belong, String name) {
        return new ActionKey() {
            @Override
            public Supplier<OnClick> belong() {
                return belong;
            }

            @Override
            public String name() {
                return name;
            }
        };
    }

    @SuppressWarnings("unused")
    Supplier<OnClick> belong();

    @Nullable
    default String get(Player player, String key) {
        return player.getPersistentDataContainer().get(KeyUtil.newKey(asPrefix() + key), PersistentDataType.STRING);
    }

    default String asPrefix() {
        return "keybind-" + name() + "-";
    }

    String name();
}
