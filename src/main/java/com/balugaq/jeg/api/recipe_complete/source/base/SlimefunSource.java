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

package com.balugaq.jeg.api.recipe_complete.source.base;

import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.utils.GuideUtil;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Target block has {@link BlockMenu}
 *
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
public interface SlimefunSource extends Source {
    @SuppressWarnings("deprecation")
    boolean handleable(
            @NotNull BlockMenu blockMenu,
            @NotNull Player player,
            @NotNull ClickAction clickAction,
            @Range(from = 0, to = 53) int @NotNull [] ingredientSlots,
            boolean unordered,
            int recipeDepth);

    @CanIgnoreReturnValue
    @SuppressWarnings("deprecation")
    default boolean openGuide(
            @NotNull BlockMenu blockMenu,
            @NotNull Player player,
            @NotNull ClickAction clickAction,
            @Range(from = 0, to = 53) int @NotNull [] ingredientSlots,
            boolean unordered,
            int recipeDepth) {
        return openGuide(blockMenu, player, clickAction, ingredientSlots, unordered, recipeDepth, null);
    }

    @SuppressWarnings("deprecation")
    default boolean openGuide(
            @NotNull BlockMenu blockMenu,
            @NotNull Player player,
            @NotNull ClickAction clickAction,
            int @NotNull [] ingredientSlots,
            boolean unordered,
            int recipeDepth,
            @Nullable Runnable callback) {
        GuideEvents.ItemButtonClickEvent lastEvent = RecipeCompletableListener.getLastEvent(player.getUniqueId());
        if (clickAction.isRightClicked() && lastEvent != null) {
            int times = 1;
            if (clickAction.isShiftClicked()) {
                times = 64;
            }

            BlockMenu actualMenu = StorageCacheUtils.getMenu(blockMenu.getLocation());
            if (actualMenu == null) {
                if (callback != null) {
                    callback.run();
                }
                return false;
            }

            if (!actualMenu.getPreset().getID().equals(blockMenu.getPreset().getID())) {
                if (callback != null) {
                    callback.run();
                }
                return false;
            }

            // I think it is runnable
            for (int i = 0; i < times; i++) {
                completeRecipeWithGuide(actualMenu, lastEvent, ingredientSlots, unordered, recipeDepth);
            }
            if (callback != null) {
                callback.run();
            }
            return true;
        }

        GuideUtil.openMainMenuAsync(player, SlimefunGuideMode.SURVIVAL_MODE, 1);
        RecipeCompletableListener.addCallback(player.getUniqueId(), ((event, profile) -> {
            BlockMenu actualMenu = StorageCacheUtils.getMenu(blockMenu.getLocation());
            if (actualMenu == null) {
                if (callback != null) {
                    callback.run();
                }
                return;
            }

            if (!actualMenu.getPreset().getID().equals(blockMenu.getPreset().getID())) {
                if (callback != null) {
                    callback.run();
                }
                return;
            }

            int times = 1;
            if (event.getClickAction().isRightClicked()) {
                times = 64;
            }

            for (int i = 0; i < times; i++) {
                completeRecipeWithGuide(actualMenu, event, ingredientSlots, unordered, recipeDepth);
            }

            player.updateInventory();
            actualMenu.open(player);
            if (callback != null) {
                callback.run();
            }
        }));
        RecipeCompletableListener.tagGuideOpen(player);
        return true;
    }

    @CanIgnoreReturnValue
    boolean completeRecipeWithGuide(
            @NotNull BlockMenu blockMenu,
            GuideEvents.@NotNull ItemButtonClickEvent event,
            @Range(from = 0, to = 53) int @NotNull [] ingredientSlots,
            boolean unordered,
            int recipeDepth);
}
