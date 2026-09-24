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

import co.aikar.commands.PaperCommandManager;
import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.core.commands.JEGCommands;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for managing the commands of JEG.
 * <p>
 * 命令系统基于 ACF（Aikar Commands）注解驱动：{@link JEGCommands} 为根命令类，
 * 本管理器负责创建 {@link PaperCommandManager}、注册补全器与命令、并在卸载时对称反注册，
 * 保证 {@code /jeg reload} 不会残留重复命令。
 *
 * @author balugaq
 * @since 1.0
 */
@Getter
@NullMarked
public class CommandManager extends AbstractManager {

    private final JavaPlugin plugin;
    private @UnknownNullability PaperCommandManager acf;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        this.acf = new PaperCommandManager(plugin);

        // viewitem 的 Slimefun 物品 ID 补全
        acf.getCommandCompletions().registerCompletion("sfitems",
            c -> Slimefun.getRegistry().getEnabledSlimefunItems().stream()
                .map(SlimefunItem::getId)
                .toList());

        // cache <key> 的动态补全：依据上一参（section）决定列出哪个缓存的 key
        acf.getCommandCompletions().registerCompletion("cachekey", c -> {
            List<String> args = c.getArgs();
            List<String> result = new ArrayList<>();
            result.add("clear");
            if (!args.isEmpty()) {
                switch (args.getFirst()) {
                    case "1", "kw", "keyword" ->
                        SearchGroup.KEYWORD_CACHE.keySet().stream().map(String::valueOf).forEach(result::add);
                    case "2", "dr", "display_recipes" ->
                        SearchGroup.DISPLAY_RECIPES_CACHE.keySet().stream().map(String::valueOf).forEach(result::add);
                    default -> { /* 未知 section 仅给 clear */ }
                }
            }
            return result.stream().sorted().toList();
        });

        acf.registerCommand(new JEGCommands());
    }

    @Override
    public void unload() {
        if (this.acf != null) {
            this.acf.unregisterCommands();
            this.acf = null;
        }
    }
}
