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

package com.balugaq.jeg.utils;

import com.balugaq.jeg.api.recipe_complete.source.ContainerInteractor;
import com.balugaq.jeg.api.recipe_complete.source.RecipeCompleteProvider;
import com.balugaq.jeg.implementation.option.NoticeMissingMaterialGuideOption;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author balugaq
 * @since 2.1
 */
public class RecipeCompleteUtils {
    public static List<ItemStack> trimItems(List<@Nullable ItemStack> origin) {
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack item : origin) {
            if (item != null && item.getType() != Material.AIR) {
                list.add(item);
            }
        }
        return list;
    }

    public static @Nullable List<@Nullable RecipeChoice> getSpecialRecipe(Player player, ItemStack itemStack, @Nullable SlimefunItem sf) {
        for (var handler : RecipeCompleteProvider.getSpecialRecipeHandlers()) {
            var r = handler.get(player, itemStack, sf);
            if (r != null) return r;
        }
        return null;
    }

    @SuppressWarnings("ConstantValue")
    public static @Nullable List<@Nullable RecipeChoice> getRecipe(Player player, @Nullable SlimefunItem origin, ItemStack itemStack) {
        SlimefunItem sf = origin == null ? SlimefunItem.getByItem(itemStack) : origin;
        var r = getSpecialRecipe(player, itemStack, sf);
        if (r != null) return r;
        if (sf != null) {
            List<@Nullable RecipeChoice> raw = new ArrayList<>(
                Arrays.stream(sf.getRecipe())
                    .map(item -> item == null ? null : new RecipeChoice.ExactChoice(item))
                    .toList()
            );
            for (int i = raw.size(); i < 9; i++) {
                raw.add(null);
            }

            return raw;
        } else {
            Recipe[] recipes = Slimefun.getMinecraftRecipeService().getRecipesFor(itemStack);
            for (Recipe recipe : recipes) {
                if (recipe instanceof ShapedRecipe shapedRecipe) {
                    List<@Nullable RecipeChoice> choices = new ArrayList<>(9);
                    String[] shape = shapedRecipe.getShape();

                    for (int i = 0; i < 3; i++) {
                        String line = i < shape.length ? shape[i] : "   ";
                        for (int j = 0; j < 3; j++) {
                            if (j >= line.length()) {
                                choices.add(null);
                            } else {
                                choices.add(shapedRecipe.getChoiceMap().get(line.charAt(j)));
                            }
                        }
                    }

                    for (int i = choices.size(); i < 9; i++) {
                        choices.add(null);
                    }

                    return choices;
                } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                    List<@Nullable RecipeChoice> raw = new ArrayList<>(shapelessRecipe.getChoiceList());

                    for (int i = raw.size(); i < 9; i++) {
                        raw.add(null);
                    }

                    return raw;
                }
            }
        }

        return null;
    }

    public static void sendMissingMaterial(Player player, ItemStack itemStack, long amt) {
        if (!NoticeMissingMaterialGuideOption.isEnabled(player)) return;
        ReflectionUtil.missingMaterials.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>());

        var mp = ReflectionUtil.missingMaterials.get(player.getUniqueId());
        mp.put(StackUtils.getAsQuantity(itemStack, 1), amt);
    }

    public static List<ItemStack> toItemStacks(@Nullable RecipeChoice choice) {
        return switch (choice) {
            case RecipeChoice.MaterialChoice mt -> mt.getChoices().stream().map(ItemStack::new).toList();
            case RecipeChoice.ExactChoice ex -> ex.getChoices();
            case null, default -> Collections.emptyList();
        };
    }

    /**
     * 计算最多能存入多少份 stacks
     *
     * @param stacks         单份配方（可能含重复类型，需要聚合）
     * @param existingMap    现有库存：物品类型 -> 总数量
     * @return 最多能存入的份数
     */
    public static int maxCraftableUnordered(
        List<ItemStack> stacks,
        Map<ItemStack, Long> existingMap,
        int freeSlots
    ) {
        // 1. 聚合单份配方的需求：类型 -> 每份所需数量
        Map<ItemStack, Long> perCraft = new HashMap<>();
        for (ItemStack stack : stacks) {
            if (stack == null || stack.getType() == Material.AIR) continue;
            ItemStack key = StackUtils.getAsQuantity(stack, 1);
            perCraft.merge(key, (long) stack.getAmount(), Long::sum);
        }

        if (perCraft.isEmpty()) {
            // 配方为空，理论上可以无限存（视为 0）
            return 0;
        }

        // 3. 对每种类型计算：单份需求 need，已有 stock
        //    先尽量用库存抵扣；抵扣不够的部分，每一份都要占用 1 个空槽（且该类型一旦占用，后续份数可复用）
        long maxByItems = Long.MAX_VALUE;

        for (var entry : perCraft.entrySet()) {
            ItemStack type = entry.getKey();
            long needPerCraft = entry.getValue();          // 每份需要多少
            long stock = existingMap.getOrDefault(type, 0L); // 已有多少

            // 已有库存能覆盖多少份（向下取整）
            long coveredByStock = stock / needPerCraft;

            // 库存覆盖之后，还需要用空槽补充的份数
            // 每补充一份，需要占用 1 个空槽（因为该类型必须新开一个槽放）
            // 因此该类型最多支持：coveredByStock + 可用空槽数
            // 但注意：空槽是全类型共享的，不能简单相加，需要全局考虑
            maxByItems = Math.min(maxByItems, coveredByStock);
        }

        // 4. 上面只算了"库存能覆盖的份数"，剩余不足的需要空槽
        //    正确的做法是：先看纯库存能撑多少份，再逐份尝试用空槽补差
        return tryWithEmptySlots(perCraft, existingMap, freeSlots);
    }

    private static int tryWithEmptySlots(
        Map<ItemStack, Long> perCraft,
        Map<ItemStack, Long> existingMap,
        int freeSlots
    ) {
        // 先算纯库存（不消耗空槽）能支持多少份
        long base = Long.MAX_VALUE;
        for (var e : perCraft.entrySet()) {
            long stock = existingMap.getOrDefault(e.getKey(), 0L);
            base = Math.min(base, stock / e.getValue());
        }
        if (base == Long.MAX_VALUE) base = 0;

        // 从 base 开始，逐份尝试，看能否用空槽补足差额
        long result = base;
        while (freeSlots > 0) {
            long next = result + 1; // 尝试做第 next 份
            int slotsNeeded = 0;
            boolean possible = true;

            for (var e : perCraft.entrySet()) {
                ItemStack type = e.getKey();
                long need = e.getValue() * next;              // 做 next 份总共需要
                long stock = existingMap.getOrDefault(type, 0L);
                long shortage = need - stock;
                if (shortage > 0) {
                    // 缺的部分需要新开一个空槽放这种物品
                    // 但一个空槽能放多少？取决于该槽最终能堆多少——这里假设一个槽能放满该物品
                    // 如果 shortage <= 单槽最大堆叠数，则占 1 个空槽即可
                    int maxStack = type.getMaxStackSize();
                    int slotsForType = (int) ((shortage + maxStack - 1) / maxStack);
                    slotsNeeded += slotsForType;
                    if (slotsNeeded > freeSlots) {
                        possible = false;
                        break;
                    }
                }
            }

            if (!possible) break;
            freeSlots -= slotsNeeded;
            result = next;
        }
        return (int) result;
    }

    public static int maxCraftable(int maxTimes, boolean unordered, int[] ingredientSlots, ContainerInteractor interactor, List<@Nullable RecipeChoice> choices) {
        if (unordered) {
            // slot to occupied
            int freeSlots = 0;
            Map<ItemStack, Long> existingMap = new HashMap<>();
            for (var slot : ingredientSlots) {
                ItemStack stack = interactor.getExistingStack(slot);
                if (stack == null || stack.getType() == Material.AIR) {
                    freeSlots++;
                } else {
                    existingMap.compute(StackUtils.getAsQuantity(stack, 1), (k, v) -> v == null ? stack.getAmount() : v + stack.getAmount());
                }
            }
            Map<ItemStack, Integer> batchMap = new HashMap<>();
            for (var choice : choices) {
                var stack = toItemStacks(choice).getFirst();
                batchMap.compute(StackUtils.getAsQuantity(stack, 1), (k, v) -> v == null ? stack.getAmount() : v + stack.getAmount());
            }
            List<ItemStack> batch = batchMap.entrySet().stream().map(e -> StackUtils.getAsQuantity(e.getKey(), e.getValue())).toList();
            maxTimes = Math.min(maxTimes, maxCraftableUnordered(batch, existingMap, freeSlots));
            return maxTimes;
        }

        // ordered recipe
        for (var slot : ingredientSlots) {
            ItemStack stack = interactor.getExistingStack(slot);
            for (var choice : choices) {
                var template = toItemStacks(choice).getFirst();
                if (stack != null && stack.getType() != Material.AIR) {
                    if (!StackUtils.itemsMatch(stack, template) || stack.getAmount() >= template.getMaxStackSize()) {
                        // 无法放置
                        return 0;
                    }

                    maxTimes = Math.min(maxTimes, (template.getMaxStackSize() - stack.getAmount()) / template.getAmount());
                }
            }
        }
        return maxTimes;
    }
}
