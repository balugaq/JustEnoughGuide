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
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.InventoryUtil;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * Target block has {@link Inventory} ex. {@link Dispenser}
 *
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"SameReturnValue", "unused", "deprecation"})
@NullMarked
public interface VanillaSource extends Source {
    @SuppressWarnings({"deprecation", "UnusedReturnValue"})
    @Override
    default boolean openGuide(RecipeCompleteSession session, @Nullable Runnable callback) {
        Debug.debug(session + " open guide for " + session.getPlayer().getName());
        Player player = session.getPlayer();
        ClickAction clickAction = session.getClickAction();

        var p = GuideUtil.updatePlayer(player);
        if (p == null) return false;
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

    @Override
    default boolean completeRecipeWithGuide(RecipeCompleteSession session) {
        Inventory inventory = session.getInventory();
        int[] ingredientSlots = session.getIngredientSlots();
        boolean unordered = session.isUnordered();
        return completeRecipeWithGuide(
            session,
            (slot) -> {
                if (slot < inventory.getSize()) {
                    return inventory.getItem(slot);
                }
                return null;
            },
            (received, i) ->
                InventoryUtil.fits(inventory, received, unordered ? ingredientSlots : new int[]{ingredientSlots[i]}),
            (received, i) ->
                InventoryUtil.pushItem(inventory, received, unordered ? ingredientSlots : new int[]{ingredientSlots[i]})
        );
    }

    default void handleSession(RecipeCompleteSession session, GuideEvents.ItemButtonClickEvent event, ClickAction clickAction, boolean reopenInventory, @Nullable Runnable callback) {
        session.setEvent(event);
        int times = 1;
        if (reopenInventory ? clickAction.isRightClicked() : clickAction.isShiftClicked()) {
            times = 64;
        }

        session.setTarget(session.getBlock().getLocation());
        session.setTimes(times);
        if (!session.canStart()) {
            if (reopenInventory) session.getPlayer().openInventory(session.getInventory());
            if (callback != null) callback.run();
            session.cancel();
            return;
        }
        for (int i = 0; i < session.getTimes(); i++) {
            completeRecipeWithGuide(session);
        }

        if (reopenInventory) session.getPlayer().openInventory(session.getInventory());
        if (callback != null) callback.run();
        session.complete();
    }
}
