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
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the implementation of the "/jeg cache" command.
 * It allows the server administrator to check the validity of the cache for a given character.
 *
 * @author balugaq
 * @since 1.5
 */
@SuppressWarnings({"ClassCanBeRecord", "deprecation", "ConstantValue"})
@Getter
@NullMarked
public class CacheCommand implements JEGCommand {
    private final Plugin plugin;

    /**
     * Constructs a new CacheCommand instance.
     *
     * @param plugin The plugin instance.
     */
    public CacheCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabCompleteRaw(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return List.of("cache");
            }

            case 2 -> {
                return List.of("1", "2");
            }

            case 3 -> {
                switch (args[1]) {
                    case "1" -> {
                        List<String> result = new ArrayList<>(SearchGroup.CACHE.keySet().stream()
                                .sorted()
                                .map(String::valueOf)
                                .toList());
                        result.add("clear");
                        return result;
                    }
                    case "2" -> {
                        List<String> result = new ArrayList<>(SearchGroup.CACHE2.keySet().stream()
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
        Map<Character, Reference<Set<SlimefunItem>>> cache;
        String command = args[2];
        switch (section) {
            case "1" -> cache = SearchGroup.CACHE;
            case "2" -> cache = SearchGroup.CACHE2;
            default -> {
                sender.sendMessage(ChatColor.RED + "Invalid section number. Please choose 1 or 2.");
                return;
            }
        }

        if (cache != null) {
            if ("clear".equalsIgnoreCase(command)) {
                cache.clear();
                sender.sendMessage(ChatColor.GREEN + "Cache " + section + " cleared.");
                return;
            }

            Character key = command.charAt(0);
            sender.sendMessage(ChatColor.GREEN + "Checking cache " + section + " for " + key + "...");
            if (cache.containsKey(key)) {
                Integer size = null;
                Reference<Set<SlimefunItem>> ref = cache.get(key);
                if (ref != null) {
                    Set<SlimefunItem> set = ref.get();
                    if (set != null) {
                        size = set.size();
                        sender.sendMessage(ChatColor.GREEN + "Items: ");
                        for (SlimefunItem item : set) {
                            sender.sendMessage(ChatColor.GREEN + " - " + item.getItemName());
                        }
                    }
                }

                sender.sendMessage(ChatColor.GREEN + "Cache for " + key + " is valid.");
                sender.sendMessage(ChatColor.GREEN + "Cache size: " + cache.size());
                if (size != null) {
                    sender.sendMessage(ChatColor.GREEN + "Character set size: " + size);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Cache for " + key + " is invalid.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid section number. Please choose 1 or 2.");
        }
    }
}
