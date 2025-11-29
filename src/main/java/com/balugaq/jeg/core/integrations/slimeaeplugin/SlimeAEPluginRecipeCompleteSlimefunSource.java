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
import com.balugaq.jeg.api.recipe_complete.source.base.SlimefunSource;
import com.balugaq.jeg.utils.BlockMenuUtil;
import me.ddggdd135.guguslimefunlib.items.ItemKey;
import me.ddggdd135.slimeae.api.interfaces.IStorage;
import me.ddggdd135.slimeae.api.items.ItemRequest;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@NullMarked
public class SlimeAEPluginRecipeCompleteSlimefunSource implements SlimefunSource {
    @SuppressWarnings("deprecation")
    @Override
    public boolean handleable(
            BlockMenu blockMenu,
            Player player,
            ClickAction clickAction,
            int[] ingredientSlots,
            boolean unordered,
            int recipeDepth) {
        return SlimeAEPluginIntegrationMain.findNearbyIStorage(blockMenu.getLocation()) != null;
    }

    @Override
    public boolean completeRecipeWithGuide(
            BlockMenu blockMenu,
            GuideEvents.ItemButtonClickEvent event,
            int[] ingredientSlots,
            boolean unordered,
            int recipeDepth) {
        IStorage networkStorage = SlimeAEPluginIntegrationMain.findNearbyIStorage(blockMenu.getLocation());
        if (networkStorage == null) {
            return false;
        }

        Player player = event.getPlayer();

        ItemStack clickedItem = event.getClickedItem();
        if (clickedItem == null) {
            return false;
        }

        List<@Nullable RecipeChoice> choices = getRecipe(clickedItem);
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
                ItemStack existing = blockMenu.getItemInSlot(ingredientSlots[i]);
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
                    ItemStack received = getItemStack(networkStorage, player, itemStack);
                    if (received != null && received.getType() != Material.AIR) {
                        if (unordered) {
                            BlockMenuUtil.pushItem(blockMenu, received, ingredientSlots);
                        } else {
                            BlockMenuUtil.pushItem(blockMenu, received, ingredientSlots[i]);
                        }
                    } else {
                        if (depthInRange(player, recipeDepth + 1)) {
                            completeRecipeWithGuide(
                                    blockMenu,
                                    new GuideEvents.ItemButtonClickEvent(
                                            event.getPlayer(), itemStack,
                                            event.getClickedSlot(),
                                            event.getClickAction(), event.getMenu(),
                                            event.getGuide()
                                    ),
                                    ingredientSlots,
                                    unordered,
                                    recipeDepth + 1
                            );
                        } else {
                            sendMissingMaterial(player, itemStack);
                        }
                    }
                }
            } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
                for (ItemStack itemStack : exactChoice.getChoices()) {
                    ItemStack received = getItemStack(networkStorage, player, itemStack);
                    if (received != null && received.getType() != Material.AIR) {
                        if (unordered) {
                            BlockMenuUtil.pushItem(blockMenu, received, ingredientSlots);
                        } else {
                            BlockMenuUtil.pushItem(blockMenu, received, ingredientSlots[i]);
                        }
                    } else {
                        if (depthInRange(player, recipeDepth + 1)) {
                            completeRecipeWithGuide(
                                    blockMenu,
                                    new GuideEvents.ItemButtonClickEvent(
                                            event.getPlayer(), itemStack,
                                            event.getClickedSlot(),
                                            event.getClickAction(), event.getMenu(),
                                            event.getGuide()
                                    ),
                                    ingredientSlots,
                                    unordered,
                                    recipeDepth + 1
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

    @Nullable
    private ItemStack getItemStack(
            IStorage networkStorage, Player player, ItemStack itemStack) {
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
        return SlimeAEPluginIntegrationMain.getPlugin();
    }
}
