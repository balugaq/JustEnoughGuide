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
import com.balugaq.jeg.utils.clickhandler.OnClick;
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
@SuppressWarnings({"unused", "deprecation"})
@NullMarked
public interface SlimefunSource extends Source {
    @SuppressWarnings("deprecation")
    @Override
    default boolean openGuide(
            RecipeCompleteSession session,
            @Nullable Runnable callback) {
        Player player = session.getPlayer();
        ClickAction clickAction = session.getClickAction();

        var p = GuideUtil.updatePlayer(player);
        if (p == null) return false;
        session.setPlayer(p);
        GuideEvents.ItemButtonClickEvent lastEvent = RecipeCompletableListener.getLastEvent(p);
        if (clickAction.isRightClicked() && lastEvent != null) {
            handleSession(session, lastEvent, clickAction, false, callback);
            return true;
        }

        GuideUtil.openGuide(player);
        RecipeCompletableListener.addCallback(
                player.getUniqueId(), ((event, profile) ->
                        handleSession(session, event, event.getClickAction(), true, callback)
                )
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
        return completeRecipeWithGuide(
                session,
                (slot) -> {
                    if (slot < blockMenu.getSize()) {
                        return blockMenu.getItemInSlot(slot);
                    }
                    return null;
                },
                (template, i) ->
                        BlockMenuUtil.fits(blockMenu, template, unordered ? ingredientSlots : new int[] {ingredientSlots[i]}),
                (received, i) ->
                        BlockMenuUtil.pushItem(blockMenu, received, unordered ? ingredientSlots : new int[] {ingredientSlots[i]})
        );
    }

    default void handleSession(RecipeCompleteSession session, GuideEvents.ItemButtonClickEvent event, ClickAction clickAction, boolean reopenMenu, @Nullable Runnable callback) {
        BlockMenu blockMenu = session.getMenu();

        session.setEvent(event);
        int times = 1;
        if (reopenMenu ? clickAction.isRightClicked() : clickAction.isShiftClicked()) {
            times = 64;
        }

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

        if (event.getMenu().getMenuClickHandler(event.getClickedSlot()) instanceof OnClick.Item.ClickHandler data) {
            session.setSlimefunItem(data.getSlimefunItem());
        }
        session.setMenu(actualMenu);
        session.setTarget(actualMenu.getLocation());
        session.setTimes(times);
        if (!session.canStart()) {
            if (reopenMenu) actualMenu.open(session.getPlayer());
            if (callback != null) callback.run();
            return;
        }
        for (int i = 0; i < session.getTimes(); i++) {
            completeRecipeWithGuide(session);
        }

        if (reopenMenu) actualMenu.open(session.getPlayer());
        if (callback != null) callback.run();
        RecipeCompleteSession.complete(session);
    }
}
