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

import com.balugaq.jeg.utils.SpecialMenuProvider;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import me.matl114.logitech.utils.MenuUtils;
import me.matl114.logitech.utils.UtilClass.MenuClass.GuideCustomMenu;
import me.matl114.logitech.utils.UtilClass.MenuClass.MenuFactory;
import org.bukkit.entity.Player;

/**
 * @author balugaq
 * @since 2.1
 */
public class LogiTechSpecialMenuOpener {
    public static void openLogiTechMenu(Player player, PlayerProfile profile, SlimefunItem slimefunItem) {
        MenuFactory factory = MenuUtils.createItemRecipeDisplay(slimefunItem, new CustomMenuHandlerImpl(), null);;
        GuideCustomMenu menu = factory.buildGuide(null, null);
        menu.open(player);
        SpecialMenuProvider.insertUselessHistory(profile);
    }
}
