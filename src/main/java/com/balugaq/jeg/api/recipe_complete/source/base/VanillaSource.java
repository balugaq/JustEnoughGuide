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
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Target block has {@link Inventory}
 * ex. {@link Dispenser}
 *
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"SameReturnValue", "unused"})
public interface VanillaSource extends Source {
    @SuppressWarnings("deprecation")
    boolean handleable(
            @NotNull Block block,
            @NotNull Inventory inventory,
            @NotNull Player player,
            @NotNull ClickAction clickAction,
            @Range(from = 0, to = 53) int @NotNull [] ingredientSlots,
            boolean unordered,
            int recipeDepth);

    @SuppressWarnings({"deprecation", "UnusedReturnValue"})
    default boolean openGuide(
            @NotNull Block block,
            @NotNull Inventory inventory,
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

            for (int i = 0; i < times; i++) {
                completeRecipeWithGuide(block, inventory, lastEvent, ingredientSlots, unordered, recipeDepth);
            }
            if (callback != null) {
                callback.run();
            }
            return true;
        }

        GuideUtil.openMainMenuAsync(player, SlimefunGuideMode.SURVIVAL_MODE, 1);
        RecipeCompletableListener.addCallback(player.getUniqueId(), ((event, profile) -> {
            int times = 1;
            if (event.getClickAction().isRightClicked()) {
                times = 64;
            }

            // I think it is runnable
            for (int i = 0; i < times; i++) {
                completeRecipeWithGuide(block, inventory, event, ingredientSlots, unordered, recipeDepth);
            }

            player.updateInventory();
            player.openInventory(inventory);
            if (callback != null) {
                callback.run();
            }
        }));
        RecipeCompletableListener.tagGuideOpen(player);
        return true;
    }

    @SuppressWarnings("UnusedReturnValue")
    boolean completeRecipeWithGuide(
            @NotNull Block block,
            @NotNull Inventory inventory,
            GuideEvents.@NotNull ItemButtonClickEvent event,
            @Range(from = 0, to = 53) int @NotNull [] ingredientSlots,
            boolean unordered,
            int recipeDepth);
}
