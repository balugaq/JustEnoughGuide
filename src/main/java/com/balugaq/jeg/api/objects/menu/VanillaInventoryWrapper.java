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

package com.balugaq.jeg.api.objects.menu;

import com.balugaq.jeg.utils.MinecraftVersion;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("removal")
@Getter
@NullMarked
public class VanillaInventoryWrapper extends BlockMenu {
    private static final int[] EMPTY = new int[0];
    private final BlockState blockState;

    public VanillaInventoryWrapper(final Inventory inv, final BlockState blockState) {
        super(createPreset(inv, blockState), blockState.getLocation() == null ? inv.getLocation() : blockState.getLocation(), inv);
        this.blockState = blockState;
        for (int slot = 0; slot < inv.getSize(); slot++) {
            addItem(slot, inv.getItem(slot));
        }
    }

    private static BlockMenuPreset createPreset(Inventory inv, BlockState blockState) {
        return new BlockMenuPreset("VANILLA_INVENTORY_WRAPPER", "VANILLA_INVENTORY_WRAPPER") {
            @Override // never called
            public void init() {}

            @Override // never called
            public boolean canOpen(final Block block, final Player player) {
                return false;
            }

            @Override // never called
            public int[] getSlotsAccessedByItemTransport(final ItemTransportFlow itemTransportFlow) {
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(final DirtyChestMenu menu, final ItemTransportFlow flow, final ItemStack item) {
                return VanillaInventoryWrapper.getSlotsAccessedByItemTransport(inv, blockState, flow, item);
            }
        };
    }

    private static int[] getSlotsAccessedByItemTransport(final Inventory inv, final BlockState blockState, final ItemTransportFlow flow, final ItemStack itemStack) {
        if (inv instanceof BrewerInventory) {
            if (flow == ItemTransportFlow.WITHDRAW) {
                if (!(blockState instanceof BrewingStand bs)) return EMPTY;
                if (bs.getBrewingTime() > 0) return EMPTY;

                IntList list = new IntArrayList();
                for (int i = 0; i < 3; i++) {
                    final ItemStack stack = inv.getContents()[i];
                    if (stack != null && stack.getType() != Material.AIR) {
                        final PotionMeta potionMeta = (PotionMeta) stack.getItemMeta();
                        if (MinecraftVersion.current().isAtLeast(MinecraftVersion.V1_20_5)) {
                            if (potionMeta.getBasePotionType() != PotionType.WATER) {
                                list.add(i);
                            }
                        } else {
                            PotionData bpd = potionMeta.getBasePotionData();
                            if (bpd != null && bpd.getType() != PotionType.WATER) {
                                list.add(i);
                            }
                        }
                    }
                }
                return list.toIntArray(); // potion slots
            } else {
                // insert
                if (itemStack.getType() == Material.BLAZE_POWDER) {
                    return new int[]{4, 3}; // fuel and ingredient slot
                } else if (itemStack.getType() == Material.POTION || itemStack.getType() == Material.SPLASH_POTION || itemStack.getType() == Material.LINGERING_POTION) {
                    return generateArray(3); // potion slots
                } else {
                    return new int[]{4}; // ingredient slot
                }
            }
        }

        else if (inv instanceof FurnaceInventory) {
            if (flow == ItemTransportFlow.WITHDRAW) {
                return new int[]{2}; // result slot
            } else {
                if (itemStack.getType().isFuel()) {
                    return new int[]{1, 0}; // fuel and smelting slot
                } else {
                    return new int[] {0, 1}; // smelting and fuel slot
                }
            }
        }

        else if (inv instanceof PlayerInventory) {
            return generateArray(36);
        }

        else {
            return generateArray(inv.getSize()); // all slot
        }
    }

    private static int[] generateArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = i;
        }
        return array;
    }

    @Override
    public void reset(boolean update) {
    }
}
