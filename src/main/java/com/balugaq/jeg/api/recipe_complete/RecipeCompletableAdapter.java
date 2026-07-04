/*
 * Copyright (c) 2024-2026 balugaq
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

package com.balugaq.jeg.api.recipe_complete;

import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.StackUtils;
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
                4,  13, 22, 31, 40, 49, 3,  5,  2,
                6,  1,  7,  0,  8,  9,  17, 18, 26,
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
        blockMenu.replaceExistingItem(slot, ItemStackUtil.getCleanItem(Models.JEG_RECIPE_COMPLETE_BUTTON));
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
