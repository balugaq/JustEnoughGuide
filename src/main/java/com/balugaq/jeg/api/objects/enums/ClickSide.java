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

package com.balugaq.jeg.api.objects.enums;

import com.balugaq.jeg.implementation.option.RecipeCompletionGuideOption;
import com.balugaq.jeg.utils.KeyUtil;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.NamespacedKey;

import java.util.Locale;

/**
 * @author balugaq
 * @since 2.1
 */
public enum ClickSide {
    LEFT,
    RIGHT;

    private final NamespacedKey key;

    ClickSide() {
        this.key = KeyUtil.append(RecipeCompletionGuideOption.instance().getKey(), name().toLowerCase(Locale.ROOT));
    }

    public NamespacedKey key() {
        return key;
    }

    public static ClickSide from(ClickAction action, boolean reopen) {
        if (reopen ? action.isRightClicked() : action.isShiftClicked()) return ClickSide.RIGHT;
        else return ClickSide.LEFT;
    }
}
