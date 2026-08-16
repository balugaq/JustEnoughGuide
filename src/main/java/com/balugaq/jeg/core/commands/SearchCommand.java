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

package com.balugaq.jeg.core.commands;

import com.balugaq.jeg.api.interfaces.JEGCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.Getter;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * This is the implementation of the "/jeg search" command.
 * @author balugaq
 * @since 2.1
 */
@SuppressWarnings({"deprecation", "SwitchStatementWithTooFewBranches"})
@Getter
@NullMarked
public class SearchCommand implements JEGCommand {
    @Override
    public List<String> onTabCompleteRaw(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return List.of("search");
            }

            default -> {
                return List.of();
            }
        }
    }

    @Override
    public boolean canCommand(
        final CommandSender sender,
        final Command command,
        final String label,
        final String[] args) {
        if (sender.isOp()) {
            if (args.length == 1) {
                return "search".equalsIgnoreCase(args[0]);
            }
        }
        return false;
    }

    @Override
    public void onCommand(
        final CommandSender sender,
        Command command,
        String label,
        String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Slimefun.getLocalization().getMessage("messages.only-players"));
            return;
        }

        if (args.length == 1) {
            ItemStack stack = player.getInventory().getItemInMainHand();
            if (stack == null || stack.getType().isAir()) {
                stack = player.getInventory().getItemInOffHand();
            }
            if (stack == null || stack.getType().isAir()) {
                sender.sendMessage(ChatColor.RED + "你必须手持一个物品在手上");
                return;
            }

            String itemName = ItemStackHelper.getDisplayName(stack).trim();
            player.chat("/sf search " + ChatColor.stripColor(itemName));
            return;
        }

        StringBuilder itemName = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            itemName.append(args[i]);
        }

        player.chat("/sf search " + ChatColor.stripColor(itemName.toString()));
    }
}
