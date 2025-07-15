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

import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.interfaces.JEGCommand;
import com.balugaq.jeg.utils.Debug;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This is the implementation of the "/jeg disable" command.
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"ClassCanBeRecord", "deprecation", "SwitchStatementWithTooFewBranches"})
@Getter
public class DisableCommand implements JEGCommand {
    private final Plugin plugin;

    public DisableCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<String> onTabCompleteRaw(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        switch (args.length) {
            case 1 -> {
                return List.of("disable");
            }

            default -> {
                return List.of();
            }
        }
    }

    @Override
    public boolean canCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final @NotNull String @NotNull [] args) {
        if (sender.isOp()) {
            if (args.length == 1) {
                return "disable".equalsIgnoreCase(args[0]);
            }
        }
        return false;
    }

    @Override
    public void onCommand(
            final @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {
        onReload(sender);
    }

    private void onReload(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Disabling plugin...");
        try {
            if (plugin == null) {
                sender.sendMessage(ChatColor.RED + "Failed to disable plugin.");
                return;
            }

            plugin.onDisable();
            SearchGroup.LOADED = false;
            sender.sendMessage(ChatColor.GREEN + "plugin has been disabled.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Failed to disable plugin.");
            Debug.trace(e);
        }
    }
}
