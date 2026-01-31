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

package com.balugaq.jeg.api.recipe_complete.source.base;

import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.utils.BlockMenuUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * Target block has {@link BlockMenu}
 *
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
@NullMarked
public interface SlimefunSource extends Source {
    @SuppressWarnings("deprecation")
    @Override
    default boolean openGuide(
            RecipeCompleteSession session,
            @Nullable Runnable callback) {
        BlockMenu blockMenu = session.getMenu();
        Player player = session.getPlayer();
        ClickAction clickAction = session.getClickAction();
        int[] ingredientSlots = session.getIngredientSlots();
        boolean unordered = session.isUnordered();
        int recipeDepth = session.getRecipeDepth();

        var p = GuideUtil.updatePlayer(player);
        if (p == null) return false;
        GuideEvents.ItemButtonClickEvent lastEvent = RecipeCompletableListener.getLastEvent(p);
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
            session.setMenu(actualMenu);
            session.setEvent(lastEvent);
            for (int i = 0; i < times; i++) {
                completeRecipeWithGuide(session);
            }
            if (callback != null) {
                callback.run();
            }
            return true;
        }

        GuideUtil.openMainMenuAsync(player, SlimefunGuideMode.SURVIVAL_MODE, 1);
        RecipeCompletableListener.addCallback(
                player.getUniqueId(), ((event, profile) -> {
                    session.setEvent(event);
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

                    session.setMenu(actualMenu);
                    for (int i = 0; i < times; i++) {
                        completeRecipeWithGuide(session);
                    }

                    player.updateInventory();
                    actualMenu.open(player);
                    if (callback != null) {
                        callback.run();
                    }
                })
        );
        RecipeCompletableListener.tagGuideOpen(player);
        return true;
    }

    @CanIgnoreReturnValue
    default boolean completeRecipeWithGuide(
            RecipeCompleteSession session) {
        BlockMenu blockMenu = session.getMenu();
        boolean unordered = session.isUnordered();
        int[] ingredientSlots = session.getIngredientSlots();
        session.setTarget(blockMenu.getLocation());
        return completeRecipeWithGuide(
                session,
                blockMenu::getItemInSlot,
                (received, i) ->
                        BlockMenuUtil.pushItem(blockMenu, received, unordered ? ingredientSlots : new int[]{ingredientSlots[i]})
        );
    }
}
