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

package com.balugaq.jeg.utils.clickhandler;

import com.balugaq.jeg.api.objects.ExtendedClickHandler;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.option.BeginnersGuideOption;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author balugaq
 * @since 1.5
 */
@SuppressWarnings("deprecation")
public class BeginnerUtils {
    public static void applyBeginnersGuide(SlimefunGuideImplementation guide, ChestMenu menu, int slot) {
        if (!JustEnoughGuide.getConfigManager().isBeginnerOption()) {
            return;
        }

        ChestMenu.MenuClickHandler origin = menu.getMenuClickHandler(slot);
        if (origin instanceof BeginnerClickHandler) {
            return;
        }

        menu.addMenuClickHandler(slot, (BeginnerClickHandler) (player, clickedSlot, clickedItem, action) -> {
            if (isNew(player) && action.isShiftClicked() && action.isRightClicked() && clickedItem != null) {
                PlayerProfile.get(player, profile -> guide.openSearch(profile, ChatColor.stripColor(ItemStackHelper.getDisplayName(clickedItem)), true));
                return false;
            }

            // call origin handler
            if (origin != null) {
                return origin.onClick(player, clickedSlot, clickedItem, action);
            } else {
                return false;
            }
        });
    }

    public static boolean isNew(Player player) {
        return BeginnersGuideOption.isEnabled(player);
    }

    public interface BeginnerClickHandler extends ExtendedClickHandler {
    }
}
