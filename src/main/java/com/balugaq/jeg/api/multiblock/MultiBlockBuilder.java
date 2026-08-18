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

package com.balugaq.jeg.api.multiblock;

import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.core.managers.IntegrationManager;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.groups.GroupSetup;
import com.balugaq.jeg.implementation.items.MultiBlockBuilderItem;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlock;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MultiBlockBuilder {
    private static final Map<MultiBlockMachine, MultiBlockBuilderItem> items = new HashMap<>();

    public static void load() {
        IntegrationManager.scheduleRunPostRegistryFinalized(MultiBlockBuilder::loadInternal);
    }

    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    private static void loadInternal() {
        for (SlimefunItem sf : new ArrayList<>(Slimefun.getRegistry().getAllSlimefunItems())) {
            if (sf instanceof MultiBlockMachine mb) {
                MultiBlockBuilderItem item = new MultiBlockBuilderItem(
                    GroupSetup.multiBlockBuilderItemsGroup,
                    new SlimefunItemStack(
                        "JEG_MULTI_BLOCK_BUILDER_ITEM_" + sf.getId(),
                        Converter.getItem(
                            sf.getItem(),
                            "&a" + sf.getItemName() + "建造器"
                        )
                    ),
                    RecipeType.NULL,
                    new ItemStack[0],
                    mb.getMultiBlock()
                );
                // don't use setHidden(boolean), otherwise the item won't show in item group.
                ReflectionUtil.setValue(item, "hidden", true);

                item.register(JustEnoughGuide.getInstance());
                items.put(mb, item);
            }
        }
    }

    public static Map<Location, @Nullable Material> getBlockMap(MultiBlock multiBlock, Location location, boolean alongX) {
        Map<Location, @Nullable Material> blockMap = new HashMap<>();
        for (int i = 0; i < multiBlock.getStructure().length; i++) {
            int a = (alongX ? location.getBlockX() : location.getBlockZ()) - 1 + i % 3;
            int y = location.getBlockY() + 1 - i / 3;
            Location blockLocation = new Location(
                location.getWorld(),
                alongX ? a : location.getBlockX(),
                y,
                alongX ? location.getBlockZ() : a
            );
            Material material = multiBlock.getStructure()[i];
            blockMap.put(blockLocation, material);
        }
        return blockMap;
    }

    public static boolean buildMultiblock(MultiBlock multiBlock, Location location, BlockFace towards, boolean alongX, boolean ignoreExistingBlocks) {
        Map<Location, @Nullable Material> blockMap = getBlockMap(multiBlock, location, alongX);

        if (!ignoreExistingBlocks) {
            // check positions
            for (Location blockLocation : blockMap.keySet()) {
                if (!blockLocation.getBlock().getType().isAir()) {
                    return false;
                }
            }
        }

        // place blocks
        for (var entry : blockMap.entrySet()) {
            Location blockLocation = entry.getKey();
            Material material = entry.getValue();
            if (material != null) {
                Block block = blockLocation.getBlock();
                block.setType(material);
                if (block.getBlockData() instanceof Directional directional) {
                    directional.setFacing(towards);
                    block.setBlockData(directional);
                }
            }
        }

        return true;
    }

    public static ItemStack getItem(ItemStack item) {
        ItemStack itemStack = getItem(SlimefunItem.getByItem(item));
        if (itemStack != null) return itemStack;
        return item;
    }

    @Contract("null -> null; !null -> !null")
    @Nullable
    public static ItemStack getItem(@Nullable SlimefunItem slimefunItem) {
        if (slimefunItem instanceof MultiBlockMachine mbm)
            return getItem(mbm).getItem();
        if (slimefunItem != null)
            return slimefunItem.getItem();
        return null;
    }

    public static MultiBlockBuilderItem getItem(MultiBlockMachine multiBlock) {
        return items.get(multiBlock);
    }

}
