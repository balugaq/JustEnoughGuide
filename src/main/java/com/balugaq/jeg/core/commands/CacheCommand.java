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

import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.interfaces.JEGCommand;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the implementation of the "/jeg cache" command. It allows the server operators to check the validity of
 * the cache for a given character.
 *
 * @author balugaq
 * @since 1.5
 */
@SuppressWarnings({"deprecation", "ConstantValue"})
@Getter
@NullMarked
public class CacheCommand implements JEGCommand {
    @Override
    public List<String> onTabCompleteRaw(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return List.of("cache");
            }

            case 2 -> {
                return List.of("1", "2", "kw", "keyword", "dr", "display_recipes");
            }

            case 3 -> {
                switch (args[1]) {
                    case "1", "kw", "keyword" -> {
                        List<String> result = new ArrayList<>(SearchGroup.KEYWORD_CACHE.keySet().stream()
                            .sorted()
                            .map(String::valueOf)
                            .toList());
                        result.add("clear");
                        return result;
                    }
                    case "2", "dr", "display_recipes" -> {
                        List<String> result = new ArrayList<>(SearchGroup.DISPLAY_RECIPES_CACHE.keySet().stream()
                            .sorted()
                            .map(String::valueOf)
                            .toList());
                        result.add("clear");
                        return result;
                    }
                    default -> {
                        return List.of();
                    }
                }
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
            if (args.length >= 1) {
                return "cache".equalsIgnoreCase(args[0]);
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
        onCheck(sender, args);
    }

    private void onCheck(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /jeg cache <section> <key>");
            return;
        }
        String section = args[1];
        Map<Character, Set<SlimefunItem>> cache;
        String command = args[2];
        switch (section) {
            case "1", "kw", "keyword" -> cache = SearchGroup.KEYWORD_CACHE;
            case "2", "dr", "display_recipes" -> cache = SearchGroup.DISPLAY_RECIPES_CACHE;
            default -> {
                sender.sendMessage(ChatColor.RED + "Invalid section number. Please choose 1 or 2.");
                return;
            }
        }

        if (cache == null) {
            sender.sendMessage(ChatColor.RED + "Invalid cache.");
            return;
        }

        if ("clear".equalsIgnoreCase(command)) {
            cache.clear();
            sender.sendMessage(ChatColor.GREEN + "Cache " + section + " cleared.");
            return;
        }

        char key = command.charAt(0);
        sender.sendMessage(ChatColor.GREEN + "Checking cache " + section + " for " + key + "...");
        if (!cache.containsKey(key)) {
            sender.sendMessage(ChatColor.RED + "Cache for " + key + " is invalid.");
            return;
        }
        int size = -1;
        Set<SlimefunItem> set = cache.get(key);
        if (set != null) {
            size = set.size();
            sender.sendMessage(ChatColor.GREEN + "Items: ");
            for (SlimefunItem item : set) {
                sender.sendMessage(ChatColor.GREEN + " - " + item.getItemName());
            }
        }

        sender.sendMessage(ChatColor.GREEN + "Cache for " + key + " is valid.");
        sender.sendMessage(ChatColor.GREEN + "Cache size: " + cache.size());
        if (size != -1) {
            sender.sendMessage(ChatColor.GREEN + "Character set size: " + size);
        }
    }
}
