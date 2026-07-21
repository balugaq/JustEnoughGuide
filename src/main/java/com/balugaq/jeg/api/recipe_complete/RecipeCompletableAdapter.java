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

package com.balugaq.jeg.api.recipe_complete;

import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.StackUtils;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * @author balugaq
 * @since 2.1
 */
public interface RecipeCompletableAdapter {
    static void tryAutoAddJEGButton(@NotNull SlimefunItem slimefunItem, @NotNull BlockMenu blockMenu) {
        // find slot
        int[] slots = {
            4, 13, 22, 31, 40, 49, 3, 5, 2,
            6, 1, 7, 0, 8, 9, 17, 18, 26,
            27, 35, 36, 44, 45, 53, 46, 52, 47,
            51, 48, 50, 37, 38, 39, 41, 42, 43,
            28, 29, 30, 32, 33, 34, 19, 20, 21,
            23, 24, 25, 10, 11, 12, 14, 15, 16
        };

        for (int slot : slots) {
            if (slot >= blockMenu.getSize()) {
                continue;
            }

            ItemStack existing = blockMenu.getItemInSlot(slot);

            if (StackUtils.itemsMatch(existing, ChestMenuUtils.getBackground()) || StackUtils.itemsMatch(existing, ChestMenuUtils.getInputSlotTexture()) || StackUtils.itemsMatch(existing, ChestMenuUtils.getOutputSlotTexture())) {
                addJEGButton(slimefunItem, blockMenu, slot);
                return;
            }
        }
    }

    static void addJEGButton(@NotNull SlimefunItem slimefunItem, @NotNull BlockMenu blockMenu, @Range(from = 0, to = 53) int slot) {
        blockMenu.replaceExistingItem(slot, Converter.getItem(Models.JEG_RECIPE_COMPLETE_BUTTON));
        blockMenu.addMenuClickHandler(slot, (player, slot1, item, action) -> {
            if (!Bukkit.getPluginManager().isPluginEnabled("JustEnoughGuide")) {
                player.sendMessage(ChatColors.color("&cJustEnoughGuide 附属已被禁用，配方补全功能无法使用"));
                return false;
            }

            if (!player.isOp() && !Slimefun.getWorldSettingsService().isWorldEnabled(player.getWorld())) {
                player.sendMessage(ChatColors.color("&c你没有权限打开粘液科技指南书"));
                return false;
            }

            if (RecipeCompletableListener.isSelectingItemStackToRecipeComplete(player)) {
                var session = RecipeCompleteSession.getSession(player);
                if (session == null) return false;
                if (session.getMenu() != null && session.getMenu().getLocation().equals(blockMenu.getLocation())) {
                    GuideUtil.openGuide(player);
                    return false;
                } else {
                    session.cancel();
                }
            }

            RecipeCompletableListener.allowSelectingItemStackToRecipeComplete(player);
            int[] slots = RecipeCompletableListener.getIngredientSlots(slimefunItem);
            boolean unordered = RecipeCompletableListener.isUnordered(slimefunItem);
            var session = RecipeCompleteSession.create(blockMenu, player, action, slots, unordered, 1);
            if (session == null) return false;
            RecipeCompleteProvider.openSlimefun(session);
            return false;
        });
    }
}
