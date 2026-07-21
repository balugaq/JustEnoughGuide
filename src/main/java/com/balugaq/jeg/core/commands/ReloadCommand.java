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
import com.balugaq.jeg.implementation.JustEnoughGuide;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * This is the implementation of the "/jeg reload" command. It reloads the JEG plugin configuration.
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"ClassCanBeRecord", "SwitchStatementWithTooFewBranches"})
@Getter
@NullMarked
public class ReloadCommand implements JEGCommand {
    private final Plugin plugin;

    public ReloadCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabCompleteRaw(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return List.of("reload");
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
                return "reload".equalsIgnoreCase(args[0]);
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
        JustEnoughGuide.reload(plugin, sender);
    }
}
