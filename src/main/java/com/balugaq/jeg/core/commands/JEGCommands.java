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

package com.balugaq.jeg.core.commands;

import com.balugaq.jeg.api.interfaces.JEGCommand;
import com.balugaq.jeg.utils.Lang;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public class JEGCommands implements TabExecutor {
    private final JavaPlugin plugin;
    private final List<JEGCommand> commands = new ArrayList<>();
    private final @NotNull JEGCommand defaultCommand;

    public JEGCommands(JavaPlugin plugin) {
        this.plugin = plugin;
        this.defaultCommand = new HelpCommand(this.plugin);
    }

    public void addCommand(JEGCommand command) {
        this.commands.add(command);
    }

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final @NotNull String @NotNull [] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Lang.getCommandMessage("no-permission"));
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Lang.getCommandMessage("unknown-command"));
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

    public @NotNull List<String> onTabCompleteRaw(@NotNull CommandSender sender, @NotNull String[] args) {
        List<String> result = new ArrayList<>();
        for (JEGCommand jegCommand : this.commands) {
            List<String> partial = jegCommand.onTabCompleteRaw(sender, args);
            if (partial != null) {
                result.addAll(partial);
            }
        }

        return result;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final @NotNull String @NotNull [] args) {
        if (sender.isOp()) {
            List<String> raw = onTabCompleteRaw(sender, args);
            return StringUtil.copyPartialMatches(args[args.length - 1], raw, new ArrayList<>());
        } else {
            return List.of();
        }
    }
}
