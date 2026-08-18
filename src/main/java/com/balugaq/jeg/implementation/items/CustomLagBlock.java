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

package com.balugaq.jeg.implementation.items;

import com.balugaq.jeg.utils.compatibility.Converter;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

@NullMarked
public class CustomLagBlock extends JEGSlimefunItem {
    public static final String BS_MS = "MilliSeconds";
    public CustomLagBlock(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, @Nullable ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemHandler(new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return false;
            }

            @Override
            public void tick(Block block, SlimefunItem sf, SlimefunBlockData data) {
                var loc = block.getLocation();

                try {
                    TimeUnit.MILLISECONDS.sleep(getCurrentStatus(loc));
                } catch (InterruptedException ignored) {
                }
            }
        });

        new BlockMenuPreset(getId(), getItemName()) {
            @Override
            public void init() {
            }

            @Override
            public void newInstance(BlockMenu menu, Block b) {
                menu.addItem(0, getOperationItem(-1000), operate(b.getLocation(), -1000));
                menu.addItem(1, getOperationItem(-100), operate(b.getLocation(), -100));
                menu.addItem(2, getOperationItem(-10), operate(b.getLocation(), -10));
                menu.addItem(3, getOperationItem(-1), operate(b.getLocation(), -1));
                menu.addItem(4, getStatusItem(b.getLocation()), operate(b.getLocation(), -((int)1e9)));
                menu.addItem(5, getOperationItem(1), operate(b.getLocation(), 1));
                menu.addItem(6, getOperationItem(10), operate(b.getLocation(), 10));
                menu.addItem(7, getOperationItem(100), operate(b.getLocation(), 100));
                menu.addItem(8, getOperationItem(1000), operate(b.getLocation(), 1000));
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                return p.isOp() || p.hasPermission("slimefun.inventory.cheat");
//                    || (canUse(p, true) && Slimefun.getProtectionManager().hasPermission(p, b, Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };
    }

    public static ItemStack getStatusItem(Location location) {
        return Converter.getItem(
            Material.YELLOW_STAINED_GLASS_PANE,
            "&e当前延时: " + getCurrentStatus(location) + " ms",
            "&e点击以清除延时"
        );
    }

    public static ItemStack getOperationItem(int amt) {
        if (amt > 0) {
            return Converter.getItem(
                Material.GREEN_STAINED_GLASS_PANE,
                "&a点击增加 " + amt + " ms 延时"
            );
        } else {
            return Converter.getItem(
                Material.RED_STAINED_GLASS_PANE,
                "&c点击减少 " + amt + " ms 延时"
            );
        }
    }

    public static int getCurrentStatus(Location location) {
        var mss = StorageCacheUtils.getData(location, BS_MS);
        if (mss == null) return 0;
        return Integer.parseInt(mss);
    }

    public static ChestMenu.MenuClickHandler operate(Location location, int amt) {
        return (p, s, i, a) -> {
            StorageCacheUtils.setData(location, BS_MS, "" + Math.max(0, getCurrentStatus(location) + amt));
            return false;
        };
    }
}
