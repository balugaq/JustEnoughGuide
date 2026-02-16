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
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.InventoryUtil;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
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
                        InventoryUtil.fits(inventory, received, unordered ? ingredientSlots : new int[] {ingredientSlots[i]}),
                (received, i) ->
                        InventoryUtil.pushItem(inventory, received, unordered ? ingredientSlots : new int[] {ingredientSlots[i]})
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
            return;
        }
        for (int i = 0; i < session.getTimes(); i++) {
            completeRecipeWithGuide(session);
        }

        if (reopenInventory) session.getPlayer().openInventory(session.getInventory());
        if (callback != null) callback.run();
        RecipeCompleteSession.complete(session);
    }
}
