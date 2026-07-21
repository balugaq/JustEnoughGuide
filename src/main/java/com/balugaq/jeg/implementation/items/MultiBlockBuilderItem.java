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

import com.balugaq.jeg.api.multiblock.MultiBlockBuilder;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlock;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@Getter
@NullMarked
public class MultiBlockBuilderItem extends JEGSlimefunItem implements NotPlaceable {
    private final MultiBlock multiBlock;

    public MultiBlockBuilderItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, MultiBlock multiBlock) {
        super(itemGroup, item, recipeType, recipe);
        this.multiBlock = multiBlock;

        addItemHandler((ItemUseHandler) e -> {
            e.getClickedBlock().ifPresent(block -> {
                Player player = e.getPlayer();
                boolean alongX = player.getFacing() == BlockFace.NORTH || player.getFacing() == BlockFace.SOUTH;
                if (MultiBlockBuilder.buildMultiblock(
                    multiBlock,
                    block.getLocation().clone().add(0, 1, 0),
                    player.getFacing().getOppositeFace(),
                    alongX,
                    true
                )) {
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
}
