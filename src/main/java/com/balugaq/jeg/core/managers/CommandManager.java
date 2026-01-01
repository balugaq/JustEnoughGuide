/*
 * Copyright (c) 2024-2026 balugaq
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

package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.core.commands.CacheCommand;
import com.balugaq.jeg.core.commands.CategoriesCommand;
import com.balugaq.jeg.core.commands.DisableCommand;
import com.balugaq.jeg.core.commands.GTEGCommand;
import com.balugaq.jeg.core.commands.HelpCommand;
import com.balugaq.jeg.core.commands.JEGCommands;
import com.balugaq.jeg.core.commands.ReloadCommand;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
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

    private final JustEnoughGuide plugin;
    private final JEGCommands commands;

    public CommandManager(JustEnoughGuide plugin) {
        this.plugin = plugin;
        this.commands = new JEGCommands(plugin);
        this.commands.addCommand(new HelpCommand(plugin));
        this.commands.addCommand(new ReloadCommand(plugin));
        this.commands.addCommand(new CacheCommand(plugin));
        this.commands.addCommand(new GTEGCommand(plugin));
        this.commands.addCommand(new DisableCommand(plugin));
        this.commands.addCommand(new CategoriesCommand(plugin));
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
