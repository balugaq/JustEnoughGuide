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
import com.balugaq.jeg.utils.RecipeCompletionUtils;
import com.balugaq.jeg.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import it.unimi.dsi.fastutil.ints.IntSet;
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
public interface ItemSource {
    JavaPlugin plugin();

    // 返回这个 session 是否可以从这个 source 获取物品
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
        RecipeCompletionUtils.handleMissingMaterial(session);
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
        event.setCancelled(true);

        // involves amounts in choices
        List<@Nullable RecipeChoice> choices = RecipeCompletionUtils.getRecipe(player, sf, targetItem);
        if (choices == null) {
            RecipeCompletionUtils.sendMissingMaterial(player, targetItem, session.getTimes());
            return false;
        }

        int maxTimes = session.getTimes();
        for (var choice : choices) {
            if (choice == null) continue;
            var template = RecipeCompletionUtils.toItemStacks(choice).getFirst();
            long cnt = RecipeCompleteProvider.countAmount(session, template);
            maxTimes = Math.min(maxTimes, (int) cnt / template.getAmount());
        }

        if (maxTimes <= 0) {
            // 无法放置
            player.sendMessage(ChatColors.color("&c[配方补全] 没有足够的材料！"));
            return false;
        }

        var craftResult = RecipeCompletionUtils.maxCraftable(maxTimes, unordered, ingredientSlots, interactor, choices);
        if (craftResult.leftInt() <= 0) {
            // 无法放置
            player.sendMessage(ChatColors.color("&c[配方补全] 没有足够的位置放置材料！"));
            return false;
        }

        if (craftResult.leftInt() < maxTimes) {
            // 可供放置的位置不足，这部分另外提醒
            player.sendMessage(ChatColors.color("&e[配方补全] 可供放置材料的位置不足！至多放置 " + session.getTimes() + " -> " + craftResult.leftInt() + " 份材料！"));
        }

        maxTimes = craftResult.leftInt();

        // 获取物品并推送
        Map<ItemStack, Integer> missingMap = new HashMap<>();
        Map<ItemStack, Integer> pushFailed = new HashMap<>();
        for (int i = 0; i < choices.size(); i++) {
            if (i >= ingredientSlots.length) break;

            RecipeChoice choice = choices.get(i);
            if (choice == null) continue;

            ItemStack itemStack = RecipeCompletionUtils.toItemStacks(choice).getFirst();
            // Issue #64
            // 防止出现目标容器在计算期间容量变化导致无法推送物品
            if (!interactor.fits(itemStack, i)) continue;

            int amt = itemStack.getAmount() * maxTimes;
            int receivedAmount = (int) RecipeCompleteProvider.getItemStack(session, itemStack, amt);
            if (receivedAmount < amt) {
                if (session.isExpired() || !RecipeCompletionUtils.depthInRange(player, recipeDepth + 1)) {
                    RecipeCompletionUtils.sendMissingMaterial(player, itemStack, amt - receivedAmount);
                } else {
                    // schedule -> 补全材料的材料配方
                    missingMap.compute(StackUtils.getAsQuantity(itemStack, 1), (k, v) -> v == null ? amt - receivedAmount : v + amt - receivedAmount);
                }
            }

            if (receivedAmount > 0) {
                var stk = StackUtils.getAsQuantity(itemStack, receivedAmount);
                var key = StackUtils.getAsQuantity(stk, 1);
                interactor.pushItem(stk, i, craftResult.right().getOrDefault(key, IntSet.of()));
                // 防止出现目标容器在计算期间容量变化导致无法推送物品
                session.setPushed(session.getPushed() + receivedAmount - stk.getAmount());
                if (stk.getAmount() > 0) {
                    pushFailed.compute(key, (k, v) -> v == null ? stk.getAmount() : stk.getAmount() + v);
                }
            }
        }

        if (!missingMap.isEmpty()) {
            if (RecipeCompletionUtils.depthInRange(player, recipeDepth + 1)) {
                session.setRecipeDepth(session.getRecipeDepth() + 1);
                for (var e : missingMap.entrySet()) {
                    SlimefunItem sf2 = SlimefunItem.getByItem(e.getKey());
                    if (sf2 == null) {
                        // 暂不支持非粘液物品配方补全
                        RecipeCompletionUtils.sendMissingMaterial(player, e.getKey(), e.getValue());
                    } else {
                        completeRecipeWithGuide(session, e.getKey(), sf2, e.getValue(), interactor);
                    }
                }
            } else {
                for (var e : missingMap.entrySet()) {
                    RecipeCompletionUtils.sendMissingMaterial(player, e.getKey(), e.getValue());
                }
            }
        }

        if (!pushFailed.isEmpty()) {
            for (var e : pushFailed.entrySet()) {
                player.sendMessage(ChatColors.color("&c[配方补全] 无法放置物品: " + RecipeCompletionUtils.getAmountString(e.getKey(), e.getValue())));
                player.getWorld().dropItemNaturally(player.getLocation(), StackUtils.getAsQuantity(e.getKey(), e.getValue()));
            }
        }

        return true;
    }

}
