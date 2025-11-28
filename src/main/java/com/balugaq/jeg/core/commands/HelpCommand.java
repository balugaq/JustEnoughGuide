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
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
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
@SuppressWarnings({"ClassCanBeRecord", "deprecation", "SwitchStatementWithTooFewBranches"})
@Getter
@NullMarked
public class HelpCommand implements JEGCommand {
    private final Plugin plugin;

    public HelpCommand(Plugin plugin) {
        this.plugin = plugin;
    }

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
    }
}
