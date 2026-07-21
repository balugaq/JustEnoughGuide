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

package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.core.commands.CacheCommand;
import com.balugaq.jeg.core.commands.CategoriesCommand;
import com.balugaq.jeg.core.commands.DisableCommand;
import com.balugaq.jeg.core.commands.GTEGCommand;
import com.balugaq.jeg.core.commands.HelpCommand;
import com.balugaq.jeg.core.commands.JEGCommands;
import com.balugaq.jeg.core.commands.ReloadCommand;
import com.balugaq.jeg.core.commands.ShareCommand;
import com.balugaq.jeg.core.commands.ViewItemCommand;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * This class is responsible for managing the commands of JEG.
 *
 * @author balugaq
 * @since 1.0
 */
@Getter
@NullMarked
public class CommandManager extends AbstractManager {

    private final JavaPlugin plugin;
    private final JEGCommands commands;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.commands = new JEGCommands(plugin);
        this.commands.addCommand(new HelpCommand());
        this.commands.addCommand(new ReloadCommand(plugin));
        this.commands.addCommand(new CacheCommand());
        this.commands.addCommand(new GTEGCommand());
        this.commands.addCommand(new DisableCommand(plugin));
        this.commands.addCommand(new CategoriesCommand());
        this.commands.addCommand(new ShareCommand());
        this.commands.addCommand(new ViewItemCommand());
    }

    public boolean registerCommands() {
        PluginCommand command = plugin.getCommand("justenoughguide");
        if (command != null) {
            command.setAliases(List.of("justenoughguide", "jeg"));
            command.setExecutor(commands);
        } else {
            return false;
        }

        return true;
    }
}
