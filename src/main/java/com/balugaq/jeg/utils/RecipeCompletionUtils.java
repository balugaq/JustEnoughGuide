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

import com.balugaq.jeg.api.objects.menu.VanillaInventoryWrapper;
import com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry;
import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.api.recipe_complete.source.ConsumeResult;
import com.balugaq.jeg.api.recipe_complete.source.ContainerConsumer;
import com.balugaq.jeg.api.recipe_complete.source.ContainerInteractor;
import com.balugaq.jeg.api.recipe_complete.source.RecipeCompleteProvider;
import com.balugaq.jeg.api.recipe_complete.source.ItemSource;
import com.balugaq.jeg.implementation.option.NoticeMissingMaterialGuideOption;
import com.balugaq.jeg.implementation.option.RecipeFillingWithNearbyContainerGuideOption;
import com.balugaq.jeg.implementation.option.RecursiveRecipeFillingGuideOption;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author balugaq
 * @since 2.1
 */
public class RecipeCompletionUtils {
    // @formatter:off
    public static final int[] PLAYER_INVENTORY_AVAILABLE_SLOTS = new int[] {
            0,  1,  2,  3,  4,  5,  6,  7,  8, // storage slots
            9,  10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            40 // offhand slot
    };
    // @formatter:on
    public static final int RECIPE_DEPTH_THRESHOLD = 8;
    public static final ConcurrentHashMap<UUID, Map<ItemStack, Long>> missingMaterials = new ConcurrentHashMap<>();

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
        missingMaterials.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>());

        var mp = missingMaterials.get(player.getUniqueId());
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

    public static String getAmountString(ItemStack itemStack, long amount) {
        long stacks = amount / Math.max(1, itemStack.getMaxStackSize());
        long left = amount - stacks * Math.max(1, itemStack.getMaxStackSize());
        String amountString = "" + amount;
        if (amount > itemStack.getMaxStackSize()) {
            amountString += " ( " + stacks + " 组";
            if (left > 0) {
                amountString += " + " + left + " 个";
            }
            amountString += ")";
        }
        return amountString;
    }

    public static int[] mergeSlots(int[]... slots) {
        IntOpenHashSet set = new IntOpenHashSet();
        for (int[] slot : slots)
            for (int i : slot) set.add(i);
        return set.toIntArray();
    }

    public static void handleMissingMaterial(RecipeCompleteSession session) {
        var p = session.getPlayer();
        if (!p.isOnline()) {
            missingMaterials.clear();
            return;
        }

        var uuid = p.getUniqueId();
        var v = missingMaterials.get(uuid);
        if (v == null || v.isEmpty()) return;

        for (var entry : v.entrySet()) {
            ItemStack itemStack = entry.getKey();
            String amountString = getAmountString(itemStack, entry.getValue());
            var builder = Component.text().color(NamedTextColor.RED).append(Component.text("缺少 "));
            var itemBuilder = Component.text(ItemStackHelper.getDisplayName(itemStack));
            SlimefunItem sf = SlimefunItem.getByItem(itemStack);
            if (sf != null) {
                itemBuilder = itemBuilder
                    .hoverEvent(HoverEvent.showText(Component.text().color(NamedTextColor.YELLOW).append(Component.text("点击查看"))))
                    .clickEvent(ClickEvent.runCommand("/jeg viewitem " + sf.getId()));
            }
            builder.color(NamedTextColor.GRAY).append(itemBuilder);
            builder.append(Component.text().color(NamedTextColor.GREEN).append(Component.text(" x")).append(Component.text(amountString)));
            p.sendMessage(builder);
        }
    }

    public static boolean depthInRange(Player player, int depth) {
        return depth <= RecursiveRecipeFillingGuideOption.getDepth(player) && depth <= RECIPE_DEPTH_THRESHOLD;
    }

    public static @NonNegative long getItemStackFromPlayerInventory(RecipeCompleteSession session, ItemStack target, long need) {
        Player player = session.getPlayer();
        long gotten = 0;

        // get from player inventory
        for (int i : PLAYER_INVENTORY_AVAILABLE_SLOTS) {
            ItemStack existingStack = player.getInventory().getItem(i);

            if (existingStack != null && existingStack.getType() != Material.AIR) {
                if (StackUtils.itemsMatch(existingStack, target)) {
                    int existingAmount = existingStack.getAmount();
                    if (existingAmount <= need) {
                        need -= existingAmount;
                        gotten += existingAmount;
                        player.getInventory().clear(i);
                    } else {
                        gotten += need;
                        existingStack.setAmount((int) (existingAmount - need));
                        player.getInventory().setItem(i, existingStack);
                        need = 0;
                    }

                    if (need <= 0) return gotten;
                } else {
                    for (var itemGetter : RecipeCompletableRegistry.getPlayerInventoryItemGetters()) {
                        long gt = itemGetter.getItemStack(session, target, existingStack, need);
                        need -= gt;
                        if (need <= 0) return gotten;
                    }
                }
            }
        }

        return gotten;
    }

    public static long countAmountFromPlayerInventory(RecipeCompleteSession session, ItemStack target) {
        Player player = session.getPlayer();
        long total = 0;

        // get from player inventory
        for (int i : PLAYER_INVENTORY_AVAILABLE_SLOTS) {
            ItemStack existingStack = player.getInventory().getItem(i);

            if (existingStack != null && existingStack.getType() != Material.AIR) {
                if (StackUtils.itemsMatch(existingStack, target)) {
                    int existingAmount = existingStack.getAmount();
                    total += existingAmount;
                } else {
                    for (var itemGetter : RecipeCompletableRegistry.getPlayerInventoryItemGetters()) {
                        long gotten = itemGetter.countItemStack(session, target, existingStack);
                        total += gotten;
                    }
                }
            }
        }

        return total;
    }

    public static <T> @Nullable T forEachNearbyContainer(Player player, Location target, int d, ItemStack itemStack, ContainerConsumer<T> consumer) {
        // get from nearby container
        for (int x = -d; x <= d; x++) {
            for (int y = -d; y <= d; y++) {
                for (int z = -d; z <= d; z++) {
                    Location bloc = player.getLocation().clone().add(x, y, z);
                    if (bloc.getBlockX() == target.getBlockX() && bloc.getBlockY() == target.getBlockY() && bloc.getBlockZ() == target.getBlockZ())
                        continue; // never include itself

                    if (!Slimefun.getProtectionManager().hasPermission(player, bloc, Interaction.INTERACT_BLOCK))
                        continue;

                    BlockMenu menu = StorageCacheUtils.getMenu(bloc);
                    if (menu == null) {
                        // check if it is vanilla container
                        BlockState state = bloc.getBlock().getState();
                        if (state instanceof Container container) {
                            menu = new VanillaInventoryWrapper(container.getInventory(), state);
                        } else {
                            continue;
                        }
                    }
                    int[] slots = mergeSlots(
                        menu.getPreset().getSlotsAccessedByItemTransport(menu, ItemTransportFlow.WITHDRAW, itemStack),
                        menu.getPreset().getSlotsAccessedByItemTransport(menu, ItemTransportFlow.INSERT, itemStack)
                    );
                    if (slots.length == 0) {
                        try {
                            var handlers = ReflectionUtil.getValue(menu, "handlers", Map.class);
                            if (handlers == null) continue;
                            var set = handlers.keySet();
                            if (set.isEmpty()) continue;
                            var list = new IntArrayList(menu.getSize());
                            for (int i = 0; i < menu.getSize(); i++) list.add(i);
                            list.removeAll((Set<Integer>) set);
                            slots = list.toIntArray();
                        } catch (Exception e) {
                            continue;
                        }
                    }
                    for (int slot : slots) {
                        ItemStack itemStack1 = menu.getItemInSlot(slot);

                        if (itemStack1 != null
                            && itemStack1.getType() != Material.AIR
                            && StackUtils.itemsMatch(itemStack1, itemStack)) {
                            var rs = consumer.accept(slot, itemStack1, menu);
                            if (rs.breakLoop()) {
                                return rs.result();
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    @NonNegative
    public static long getItemStackFromNearbyContainer(Player player, Location target, ItemStack itemStack, long total) {
        AtomicLong atomicAmount = new AtomicLong(total);

        int d = RecipeFillingWithNearbyContainerGuideOption.getRadiusDistance(player);
        if (d <= 0) return 0;

        var rs = forEachNearbyContainer(player, target, d, itemStack, (slot, itemStack1, menu) -> {
            int existing = itemStack1.getAmount();

            if (existing <= atomicAmount.get()) {
                atomicAmount.updateAndGet(a -> a - existing);
                menu.replaceExistingItem(slot, null);
            } else {
                itemStack1.setAmount((int) (existing - atomicAmount.get()));
                menu.replaceExistingItem(slot, itemStack1);
                atomicAmount.set(0);
            }

            if (atomicAmount.get() <= 0) {
                return new ConsumeResult<>(true, total);
            }

            return ConsumeResult.continueIt();
        });

        if (rs != null) return rs;
        return total - atomicAmount.get();
    }

    @NonNegative
    public static long countAmountFromNearbyContainer(Player player, Location target, ItemStack itemStack) {
        int d = RecipeFillingWithNearbyContainerGuideOption.getRadiusDistance(player);
        if (d <= 0) return 0;

        AtomicLong total = new AtomicLong(0);
        forEachNearbyContainer(player, target, d, itemStack, (slot, itemStack1, menu) -> {
            int existing = itemStack1.getAmount();
            total.addAndGet(existing);

            return ConsumeResult.continueIt();
        });

        return total.get();
    }

    @CanIgnoreReturnValue
    public static boolean completeRecipeWithGuide(RecipeCompleteSession session) {
        BlockMenu blockMenu = session.getMenu();
        boolean unordered = session.isUnordered();
        int[] ingredientSlots = session.getIngredientSlots();
        return ItemSource.completeRecipeWithGuide(session, ContainerInteractor.slimefun(blockMenu, unordered, ingredientSlots));
    }
}
