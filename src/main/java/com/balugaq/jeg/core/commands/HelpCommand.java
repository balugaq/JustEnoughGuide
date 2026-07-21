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
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * This is the implementation of the "/jeg help" command. It shows the list of available commands and their usage.
 * <p>
 * This command is also the default command when no other command is specified.
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"deprecation", "SwitchStatementWithTooFewBranches"})
@Getter
@NullMarked
public class HelpCommand implements JEGCommand {
    @Override
    public List<String> onTabCompleteRaw(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return List.of("help");
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
                return "help".equalsIgnoreCase(args[0]);
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
        onHelp(sender);
    }

    private void onHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "JEG Commands:");
        sender.sendMessage(ChatColor.GREEN + "/jeg help - Show this help message");
        sender.sendMessage(ChatColor.GREEN + "/jeg reload - Reload JEG plugin");
        sender.sendMessage(ChatColor.GREEN + "/jeg cache <section> <key>");
        sender.sendMessage(ChatColor.GREEN + "/jeg disable - Disable JEG plugin");
        sender.sendMessage(ChatColor.GREEN + "/jeg gteg - Get Guide Tier Editor");
        sender.sendMessage(ChatColor.GREEN + "/jeg categories - View all the groups");
        sender.sendMessage(ChatColor.GREEN + "/jeg share - Share the item on your hand");
        sender.sendMessage(ChatColor.GREEN + "/jeg viewitem <Slimefun Item> - View Slimefun item");
    }
}
