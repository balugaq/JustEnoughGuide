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
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.0
 */
@NullMarked
public interface BaseAction extends Keyed {
    static void redirect(Player player, ActionKey type, BaseAction from, BaseAction to) {
        player.getPersistentDataContainer().set(
            KeyUtil.newKey(type.asPrefix() + from.getKey().getKey()),
            PersistentDataType.STRING, to.getKey().getKey()
        );
    }

    static BaseAction remap(Player player, OnClick keybind, BaseAction action) {
        return keybind.findAction(player, action.getKey().getKey());
    }

    Material material();

    ActionKey parent();

    String name();
}
