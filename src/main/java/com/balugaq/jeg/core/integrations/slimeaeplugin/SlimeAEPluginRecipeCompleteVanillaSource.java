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

package com.balugaq.jeg.core.integrations.slimeaeplugin;

import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.api.recipe_complete.source.base.VanillaSource;
import com.balugaq.jeg.core.integrations.networksexpansion.NetworksExpansionIntegrationMain;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.InventoryUtil;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import me.ddggdd135.guguslimefunlib.items.ItemKey;
import me.ddggdd135.slimeae.api.interfaces.IStorage;
import me.ddggdd135.slimeae.api.items.ItemRequest;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
public class SlimeAEPluginRecipeCompleteVanillaSource implements VanillaSource {
    @SuppressWarnings("deprecation")
    @Override
    public boolean handleable(
            @NotNull Block block,
            @NotNull Inventory inventory,
            @NotNull Player player,
            @NotNull ClickAction clickAction,
            int @NotNull [] ingredientSlots,
            boolean unordered) {
        return SlimeAEPluginIntegrationMain.findNearbyIStorage(block.getLocation()) != null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean openGuide(
            @NotNull Block block,
            @NotNull Inventory inventory,
            @NotNull Player player,
            @NotNull ClickAction clickAction,
            int @NotNull [] ingredientSlots,
            boolean unordered,
            @Nullable Runnable callback) {
        GuideEvents.ItemButtonClickEvent lastEvent = RecipeCompletableListener.getLastEvent(player.getUniqueId());
        if (clickAction.isRightClicked() && lastEvent != null) {
            int times = 1;
            if (clickAction.isShiftClicked()) {
                times = 64;
            }

            // I think it is runnable
            for (int i = 0; i < times; i++) {
                completeRecipeWithGuide(block, inventory, lastEvent, ingredientSlots, unordered);
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

            for (int i = 0; i < times; i++) {
                completeRecipeWithGuide(block, inventory, event, ingredientSlots, unordered);
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

    @Override
    public boolean completeRecipeWithGuide(
            @NotNull Block block,
            @NotNull Inventory inventory,
            GuideEvents.@NotNull ItemButtonClickEvent event,
            int @NotNull [] ingredientSlots,
            boolean unordered) {
        IStorage root = SlimeAEPluginIntegrationMain.findNearbyIStorage(block.getLocation());
        if (root == null) {
            return false;
        }

        Player player = event.getPlayer();

        ItemStack clickedItem = event.getClickedItem();
        if (clickedItem == null) {
            return false;
        }

        // choices.size() must be 9
        List<RecipeChoice> choices = getRecipe(clickedItem);
        if (choices == null) {
            return false;
        }

        for (int i = 0; i < 9; i++) {
            if (i >= choices.size()) {
                break;
            }

            if (i >= ingredientSlots.length) {
                break;
            }

            RecipeChoice choice = choices.get(i);
            if (choice == null) {
                continue;
            }

            if (!unordered) {
                ItemStack existing = inventory.getItem(ingredientSlots[i]);
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
                    ItemStack received = getItemStack(root, player, itemStack);
                    if (received != null && received.getType() != Material.AIR) {
                        if (unordered) {
                            InventoryUtil.pushItem(inventory, received, ingredientSlots);
                        } else {
                            InventoryUtil.pushItem(inventory, received, ingredientSlots[i]);
                        }
                    }
                }
            } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
                for (ItemStack itemStack : exactChoice.getChoices()) {
                    ItemStack received = getItemStack(root, player, itemStack);
                    if (received != null && received.getType() != Material.AIR) {
                        if (unordered) {
                            InventoryUtil.pushItem(inventory, received, ingredientSlots);
                        } else {
                            InventoryUtil.pushItem(inventory, received, ingredientSlots[i]);
                        }
                    }
                }
            }
        }

        event.setCancelled(true);
        return true;
    }

    @Nullable
    private ItemStack getItemStack(
            @NotNull IStorage networkStorage, @NotNull Player player, @NotNull ItemStack itemStack) {
        ItemStack i1 = getItemStackFromPlayerInventory(player, itemStack);
        if (i1 != null) {
            return i1;
        }

        // get from networkStorage
        ItemStack[] gotten = networkStorage
                .takeItem(new ItemRequest(new ItemKey(itemStack), 1L))
                .toItemStacks();
        if (gotten.length != 0) {
            return gotten[0];
        }

        return null;
    }

    @Override
    public JavaPlugin plugin() {
        return NetworksExpansionIntegrationMain.getPlugin();
    }
}
