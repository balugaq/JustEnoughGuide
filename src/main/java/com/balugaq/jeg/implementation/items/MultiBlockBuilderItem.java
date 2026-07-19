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

package com.balugaq.jeg.implementation.items;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.groups.GroupSetup;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlock;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
@NullMarked
public class MultiBlockBuilderItem extends JEGSlimefunItem {
    private static final Map<MultiBlockMachine, MultiBlockBuilderItem> items = new HashMap<>();
    private final MultiBlock multiBlock;

    public MultiBlockBuilderItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, MultiBlock multiBlock) {
        super(itemGroup, item, recipeType, recipe);
        this.multiBlock = multiBlock;
    }

    public static void setup() {
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
                item.register(JustEnoughGuide.getInstance());
                items.put(mb, item);
            }
        }
    }

    public static MultiBlockBuilderItem get(MultiBlockMachine multiBlock) {
        return items.get(multiBlock);
    }

    @Override
    public void postRegister() {
        addItemHandler((ItemUseHandler) e -> {
            e.getClickedBlock().ifPresent(block -> {
                Player player = e.getPlayer();
                boolean alongX = player.getFacing() == BlockFace.NORTH || player.getFacing() == BlockFace.SOUTH;
                if (buildMultiblock(multiBlock, player.getLocation().clone().add(player.getFacing().getDirection()), alongX, true)) {
                    if (!player.isOp() && player.getGameMode() != GameMode.CREATIVE) {
                        e.getItem().setAmount(e.getItem().getAmount() - 1);
                    }
                    player.sendMessage(ChatColors.color("&a多方块 " + multiBlock.getSlimefunItem().getItemName() + " 搭建成功！"));
                } else {
                    player.sendMessage(ChatColors.color("&c多方块 " + multiBlock.getSlimefunItem().getItemName() + " 搭建失败！"));
                }
            });
        });
    }

    public static boolean buildMultiblock(MultiBlock multiBlock, Location location, boolean alongX, boolean ignoreExistingBlocks) {
        Map<Location, Material> blockMap = new HashMap<>();
        for (int i = 0; i < multiBlock.getStructure().length; i++) {
            int a = (alongX ? location.getBlockX() : location.getBlockZ()) - 1 + i % 3;
            int y = location.getBlockY() + i / 3;
            Location blockLocation = location.clone().add(
                alongX ? a : location.getBlockX(),
                y,
                alongX ? location.getBlockZ() : a
            );
            Material material = multiBlock.getStructure()[i];
            blockMap.put(blockLocation, material);
        }

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
            blockLocation.getBlock().setType(material);
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
            return MultiBlockBuilderItem.get(mbm).getItem();
        if (slimefunItem != null)
            return slimefunItem.getItem();
        return null;
    }
}
