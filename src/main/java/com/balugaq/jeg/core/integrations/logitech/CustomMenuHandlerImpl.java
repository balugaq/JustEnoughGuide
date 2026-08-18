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

package com.balugaq.jeg.core.integrations.logitech;

import com.balugaq.jeg.utils.GuideUtil;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import me.matl114.logitech.utils.UtilClass.MenuClass.CustomMenu;
import me.matl114.logitech.utils.UtilClass.MenuClass.CustomMenuHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.jspecify.annotations.NullMarked;

/**
 * A better back implementation for the LogiTech special menu.
 *
 * @author balugaq
 * @since 1.5
 */
@NullMarked
public class CustomMenuHandlerImpl implements CustomMenuHandler {
    @Override
    public ChestMenu.MenuClickHandler getInstance(
        CustomMenu menu) {
        return (p, s, i, a) -> {
            PlayerProfile.find(p).ifPresent(GuideUtil::goBack);
            return false;
        };
    }
}
