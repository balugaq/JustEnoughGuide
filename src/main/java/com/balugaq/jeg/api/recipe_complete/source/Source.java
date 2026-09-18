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

package com.balugaq.jeg.api.recipe_complete.source;

import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.RecipeCompleteUtils;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
@NullMarked
public interface Source {

    JavaPlugin plugin();

    boolean handleable(RecipeCompleteSession session);

    /**
     * @return gotten
     */
    @NonNegative
    long getItemStack(RecipeCompleteSession session, ItemStack itemStack, long need);

    @NonNegative
    long countAmount(RecipeCompleteSession session, ItemStack template);

    /**
     * The handle level of the source. The lower the level, the earlier items will try to be gotten from.
     *
     * @return the handle level
     */
    default int handleLevel() {
        return 20;
    }

    static boolean completeRecipeWithGuide(RecipeCompleteSession session, ContainerInteractor interactor) {
        Debug.debug("handling " + session + " :completeRecipeWithGuide");
        var event = session.getEvent();
        var ingredientSlots = session.getIngredientSlots();
        var unordered = session.isUnordered();
        var recipeDepth = session.getRecipeDepth();

        Player player = GuideUtil.updatePlayer(event.getPlayer());
        if (player == null) {
            return false;
        }

        ItemStack clickedItem = event.getClickedItem();
        if (clickedItem == null) {
            return false;
        }

        var success = completeRecipeWithGuide(session, clickedItem, session.getSlimefunItem(), session.getTimes(), interactor);
        ReflectionUtil.handleMissingMaterial(session);
        return success;
    }

    static boolean completeRecipeWithGuide(RecipeCompleteSession session, ItemStack targetItem, @Nullable SlimefunItem sf, int times, ContainerInteractor interactor) {
        Debug.debug("handling " + session + " :completeRecipeWithGuide#sub");
        var event = session.getEvent();
        var ingredientSlots = session.getIngredientSlots();
        var unordered = session.isUnordered();
        var recipeDepth = session.getRecipeDepth();

        Player player = GuideUtil.updatePlayer(event.getPlayer());
        if (player == null) return false;

        // involves amounts in choices
        List<@Nullable RecipeChoice> choices = RecipeCompleteUtils.getRecipe(player, sf, targetItem);
        if (choices == null) {
            RecipeCompleteUtils.sendMissingMaterial(player, targetItem, session.getTimes());
            return false;
        }

        int maxTimes = session.getTimes();
        for (var choice : choices) {
            if (choice == null) continue;
            var template = RecipeCompleteUtils.toItemStacks(choice).getFirst();
            long cnt = RecipeCompleteProvider.countAmount(session, template);
            maxTimes = Math.min(maxTimes, (int) cnt / template.getAmount());
        }

        // maxCraftable 有点耗时，所以先检测一下
        if (maxTimes == 0) {
            // 无法放置
            player.sendMessage(ChatColors.color("&c[配方补全] 没有足够的位置放置材料！"));
            return false;
        }

        maxTimes = RecipeCompleteUtils.maxCraftable(maxTimes, unordered, ingredientSlots, interactor, choices);
        if (maxTimes == 0) {
            // 无法放置
            player.sendMessage(ChatColors.color("&c[配方补全] 没有足够的位置放置材料！"));
            return false;
        }

        if (maxTimes < session.getTimes()) {
            // 可供放置的位置不足，这部分另外提醒
            player.sendMessage(ChatColors.color("&e[配方补全] 可供放置材料的位置不足！至多合成 " + session.getTimes() + " -> " + maxTimes + " 次！"));
        }

        Map<ItemStack, Integer> missingMap = new HashMap<>();
        for (int i = 0; i < choices.size(); i++) {
            if (i >= ingredientSlots.length) break;

            RecipeChoice choice = choices.get(i);
            if (choice == null) continue;

            ItemStack itemStack = RecipeCompleteUtils.toItemStacks(choice).getFirst();
//            // Issue #64
//            if (!interactor.fits(itemStack, i)) continue;
            int amt = itemStack.getAmount() * maxTimes;
            int receivedAmount = (int) RecipeCompleteProvider.getItemStack(session, itemStack, amt);
            if (receivedAmount < amt) {
                if (session.isExpired()) {
                    RecipeCompleteUtils.sendMissingMaterial(player, itemStack, amt - receivedAmount);
                } else {
                    // schedule -> 补全材料的材料配方
                    missingMap.compute(StackUtils.getAsQuantity(itemStack, 1), (k, v) -> v == null ? amt - receivedAmount : v + amt - receivedAmount);
                }
            }

            if (receivedAmount > 0) {
                session.setPushed(session.getPushed() + receivedAmount);
                interactor.pushItem(StackUtils.getAsQuantity(itemStack, receivedAmount), i);
            }
        }

        if (!missingMap.isEmpty()) {
            session.setRecipeDepth(session.getRecipeDepth() + 1);
            for (var e : missingMap.entrySet()) {
                SlimefunItem sf2 = SlimefunItem.getByItem(e.getKey());
                if (sf2 == null) continue;
                completeRecipeWithGuide(session, e.getKey(), sf2, e.getValue(), interactor);
            }
        }

        event.setCancelled(true);
        return true;
    }

}
