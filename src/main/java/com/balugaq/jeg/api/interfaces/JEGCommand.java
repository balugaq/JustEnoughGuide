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

package com.balugaq.jeg.api.interfaces;

import com.balugaq.jeg.core.commands.JEGCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * This interface is used to define a command that can be executed by JEG. Used by {@link JEGCommands}.
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings("unused")
@NullMarked
public interface JEGCommand {
    /**
     * This method is used to define a tab complete for a command.
     *
     * @param sender The sender of the command.
     * @param args   The arguments of the command.
     * @return The tab complete of the command.
     */
    default List<String> onTabCompleteRaw(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    /**
     * This method is used to define if a command can be executed by a player.
     *
     * @param sender  The sender of the command.
     * @param command The command that is being executed.
     * @param label   The label of the command.
     * @param args    The arguments of the command.
     * @return If the command can be executed.
     */
    default boolean canCommand(
        final CommandSender sender,
        Command command,
        String label,
        String[] args) {
        return false;
    }

    /**
     * This method is used to define what happens when a command is executed.
     *
     * @param sender  The sender of the command.
     * @param command The command that is being executed.
     * @param label   The label of the command.
     * @param args    The arguments of the command.
     */
    void onCommand(
        final CommandSender sender,
        Command command,
        String label,
        String[] args);
}
