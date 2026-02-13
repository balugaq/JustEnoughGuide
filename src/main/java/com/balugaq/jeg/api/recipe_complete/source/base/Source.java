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

import com.balugaq.jeg.api.objects.SimpleRecipeChoice;
import com.balugaq.jeg.api.objects.menu.VanillaInventoryWrapper;
import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.implementation.option.NoticeMissingMaterialGuideOption;
import com.balugaq.jeg.implementation.option.RecipeFillingWithNearbyContainerGuideOption;
import com.balugaq.jeg.implementation.option.RecursiveRecipeFillingGuideOption;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.StackUtils;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
@NullMarked
public interface Source {
    int RECIPE_DEPTH_THRESHOLD = 8;

    static boolean depthInRange(Player player, int depth) {
        return depth <= RecursiveRecipeFillingGuideOption.getDepth(player) && depth <= RECIPE_DEPTH_THRESHOLD;
    }

    JavaPlugin plugin();

    boolean handleable(RecipeCompleteSession session);

    @Nullable ItemStack getItemStack(RecipeCompleteSession session, ItemStack itemStack);

    @CanIgnoreReturnValue
    boolean completeRecipeWithGuide(RecipeCompleteSession session);

    @CanIgnoreReturnValue
    default boolean openGuide(RecipeCompleteSession session) {
        return openGuide(session, null);
    }

    @CanIgnoreReturnValue
    boolean openGuide(RecipeCompleteSession session, @Nullable Runnable callback);

    /**
     * The handle level of the source. The lower the level, the earlier items will try to be gotten from.
     *
     * @return the handle level
     */
    default int handleLevel() {
        return 20;
    }

    default @Nullable List<@Nullable RecipeChoice> getSpecialRecipe(Player player, ItemStack itemStack, @Nullable SlimefunItem sf) {
        for (var handler : RecipeCompleteProvider.getSpecialRecipeHandlers()) {
            var r = handler.get(player, itemStack, sf);
            if (r != null) return r;
        }
        return null;
    }

    @SuppressWarnings("ConstantValue")
    default @Nullable List<@Nullable RecipeChoice> getRecipe(Player player, ItemStack itemStack) {
        SlimefunItem sf = SlimefunItem.getByItem(itemStack);
        var r = getSpecialRecipe(player, itemStack, sf);
        if (r != null) return r;
        if (sf != null) {
            List<@Nullable RecipeChoice> raw = new ArrayList<>(
                    Arrays.stream(sf.getRecipe())
                            .map(item -> item == null ? null : new SimpleRecipeChoice(item))
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

    default @Nullable ItemStack getItemStackFromPlayerInventory(Player player, ItemStack itemStack) {
        return getItemStackFromPlayerInventory(player, itemStack, Math.max(1, Math.min(itemStack.getAmount(), itemStack.getMaxStackSize())));
    }

    default @Nullable ItemStack getItemStackFromPlayerInventory(Player player, ItemStack itemStack, int amount) {
        int total = amount;

        // get from player inventory
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack itemStack1 = player.getInventory().getItem(i);

            if (itemStack1 != null
                    && itemStack1.getType() != Material.AIR
                    && StackUtils.itemsMatch(itemStack1, itemStack)) {

                int existing = itemStack1.getAmount();

                if (existing <= amount) {
                    amount -= existing;
                    player.getInventory().clear(i);
                } else {
                    itemStack1.setAmount(existing - amount);
                    player.getInventory().setItem(i, itemStack1);
                    amount = 0;
                }

                if (amount <= 0) {
                    ItemStack clone = itemStack.clone();
                    clone.setAmount(total);
                    return clone;
                }
            }
        }

        return null;
    }

    default @Nullable ItemStack getItemStackFromNearbyContainer(Player player, Location target, ItemStack itemStack) {
        return getItemStackFromNearbyContainer(player, target, itemStack, Math.max(1, Math.min(itemStack.getAmount(), itemStack.getMaxStackSize())));
    }

    @SuppressWarnings("unchecked")
    default @Nullable ItemStack getItemStackFromNearbyContainer(Player player, Location target, ItemStack itemStack, int amount) {
        int total = amount;

        int d = RecipeFillingWithNearbyContainerGuideOption.getRadiusDistance(player);
        if (d <= 0) {
            return null;
        }

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

                            int existing = itemStack1.getAmount();

                            if (existing <= amount) {
                                amount -= existing;
                                menu.replaceExistingItem(slot, null);
                            } else {
                                itemStack1.setAmount(existing - amount);
                                menu.replaceExistingItem(slot, itemStack1);
                                amount = 0;
                            }

                            if (amount <= 0) {
                                ItemStack clone = itemStack.clone();
                                clone.setAmount(total);
                                return clone;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private int[] mergeSlots(int[]... slots) {
        IntOpenHashSet set = new IntOpenHashSet();
        for (int[] slot : slots)
            for (int i : slot) set.add(i);
        return set.toIntArray();
    }

    default void sendMissingMaterial(Player player, ItemStack itemStack) {
        if (NoticeMissingMaterialGuideOption.isEnabled(player)) {
            var k = GuideUtil.updatePlayer(player);
            if (k == null) return;

            if (!RecipeCompletableListener.missingMaterials.containsKey(k)) {
                RecipeCompletableListener.missingMaterials.put(k, new ArrayList<>());
            }

            var v = RecipeCompletableListener.missingMaterials.get(k);
            synchronized (v) {
                v.add(itemStack);
            }
        }
    }

    default boolean completeRecipeWithGuide(
            RecipeCompleteSession session,
            ItemGetter itemGetter,
            ItemFitter itemFitter,
            ItemPusher itemPusher) {
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

        List<@Nullable RecipeChoice> choices = getRecipe(player, clickedItem);
        if (choices == null) {
            sendMissingMaterial(player, clickedItem);
            return false;
        }

        for (int i = 0; i < choices.size(); i++) {
            if (i >= ingredientSlots.length) {
                break;
            }

            RecipeChoice choice = choices.get(i);
            if (choice == null) {
                continue;
            }

            if (!unordered) {
                ItemStack existing = itemGetter.get(ingredientSlots[i]);
                if (existing != null && existing.getType() != Material.AIR) {
                    if (existing.getAmount() >= existing.getMaxStackSize()) {
                        continue;
                    }

                    if (!choice.test(existing)) {
                        continue;
                    }
                }
            }

            if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
                List<ItemStack> itemStacks =
                        materialChoice.getChoices().stream().map(ItemStack::new).toList();
                for (ItemStack itemStack : itemStacks) {
                    // Issue #64
                    if (!itemFitter.fits(itemStack, i)) {
                        continue;
                    }
                    ItemStack received = RecipeCompleteProvider.getItemStack(session, itemStack);
                    if (received != null && received.getType() != Material.AIR) {
                        session.setPushed(session.getPushed() + received.getAmount());
                        itemPusher.push(received, i);
                    } else {
                        if (session.isExpired()) {
                            session.setRecipeDepth(recipeDepth + 1);
                            completeRecipeWithGuide(
                                    session,
                                    itemGetter, itemFitter, itemPusher
                            );
                        } else {
                            sendMissingMaterial(player, itemStack);
                        }
                    }
                }
            } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
                for (ItemStack itemStack : exactChoice.getChoices()) {
                    // Issue #64
                    if (!itemFitter.fits(itemStack, i)) {
                        continue;
                    }
                    ItemStack received = RecipeCompleteProvider.getItemStack(session, itemStack);
                    if (received != null && received.getType() != Material.AIR) {
                        session.setPushed(session.getPushed() + received.getAmount());
                        itemPusher.push(received, i);
                    } else {
                        if (session.isExpired()) {
                            session.setRecipeDepth(recipeDepth + 1);
                            completeRecipeWithGuide(
                                    session,
                                    itemGetter, itemFitter, itemPusher
                            );
                        } else {
                            sendMissingMaterial(player, itemStack);
                        }
                    }
                }
            }
        }

        event.setCancelled(true);
        return true;
    }

    /**
     * @author balugaq
     * @since 2.0
     */
    @SuppressWarnings("unused")
    @FunctionalInterface
    interface ItemGetter {
        @Nullable ItemStack get(int slot);
    }

    /**
     * @author balugaq
     * @since 2.0
     */
    @FunctionalInterface
    interface ItemPusher {
        void push(ItemStack itemStack, int ingredientIndex);
    }

    /**
     * @author balugaq
     * @since 2.0
     */
    @FunctionalInterface
    interface ItemFitter {
        boolean fits(ItemStack itemStack, int ingredientIndex);
    }
}
