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
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This is the implementation of the "/jeg help" command.
 * It shows the list of available commands and their usage.
 * <p>
 * This command is also the default command when no other command is specified.
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"ClassCanBeRecord", "deprecation", "SwitchStatementWithTooFewBranches"})
@Getter
public class HelpCommand implements JEGCommand {
    private final Plugin plugin;

    public HelpCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<String> onTabCompleteRaw(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
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
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final @NotNull String @NotNull [] args) {
        if (sender.isOp()) {
            if (args.length == 1) {
                return "help".equalsIgnoreCase(args[0]);
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
        onHelp(sender);
    }

    private void onHelp(@NotNull CommandSender sender) {
        sender.sendMessage(Lang.getCommandSuccess("help"));
    }
}
