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
