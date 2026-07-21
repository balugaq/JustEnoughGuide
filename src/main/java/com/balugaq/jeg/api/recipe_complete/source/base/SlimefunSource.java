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

package com.balugaq.jeg.api.recipe_complete.source.base;

import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.utils.BlockMenuUtil;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
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
        Debug.debug(session + " open guide for " + session.getPlayer().getName());
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
                BlockMenuUtil.fits(blockMenu, template, unordered ? ingredientSlots : new int[]{ingredientSlots[i]}),
            (received, i) ->
                BlockMenuUtil.pushItem(blockMenu, received, unordered ? ingredientSlots : new int[]{ingredientSlots[i]})
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
            if (callback != null) callback.run();
            session.cancel();
            return;
        }

        if (!actualMenu.getPreset().getID().equals(blockMenu.getPreset().getID())) {
            if (callback != null) callback.run();
            session.cancel();
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
            session.cancel();
            return;
        }
        completeRecipeWithGuide(session);
        if (reopenMenu) actualMenu.open(session.getPlayer());
        if (callback != null) callback.run();
        session.complete();
    }
}
