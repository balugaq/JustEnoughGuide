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
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the command system of JEG. It handles all the commands and tab-completions.
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"unused", "deprecation", "ConstantValue"})
@Getter
@NullMarked
public class JEGCommands implements TabExecutor {
    private final JavaPlugin plugin;
    private final List<JEGCommand> commands = new ArrayList<>();
    private final JEGCommand defaultCommand;

    public JEGCommands(JavaPlugin plugin) {
        this.plugin = plugin;
        this.defaultCommand = new HelpCommand();
    }

    public void addCommand(JEGCommand command) {
        this.commands.add(command);
    }

    @Override
    public boolean onCommand(
        final CommandSender sender,
        final Command command,
        final String label,
        final String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Unknown command. Type /jeg help");
            return true;
        }

        // Player or console
        for (JEGCommand jegCommand : this.commands) {
            if (jegCommand.canCommand(sender, command, label, args)) {
                jegCommand.onCommand(sender, command, label, args);
                return true;
            }
        }

        this.defaultCommand.onCommand(sender, command, label, args);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
        final CommandSender sender,
        final Command command,
        final String label,
        final String[] args) {
        if (sender.isOp()) {
            List<String> raw = onTabCompleteRaw(sender, args);
            return StringUtil.copyPartialMatches(args[args.length - 1], raw, new ArrayList<>());
        } else {
            return List.of();
        }
    }

    public List<String> onTabCompleteRaw(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        for (JEGCommand jegCommand : this.commands) {
            List<String> partial = jegCommand.onTabCompleteRaw(sender, args);
            if (partial != null) {
                result.addAll(partial);
            }
        }

        return result;
    }
}
