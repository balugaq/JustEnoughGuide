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

package com.balugaq.jeg.core.listeners;

import com.balugaq.jeg.implementation.items.ReplacementCardAdapter;
import com.balugaq.jeg.utils.ClipboardUtil;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author balugaq
 * @since 2.1
 */
@NullMarked
public class ReplacementCardAdaptItemListener implements Listener {
    private static final Set<UUID> noticedPlayers = new HashSet<>();
    private static boolean noticedConsole = false;

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        preprocessEvent(e, e.getPlayer(), e.getMessage().substring(1));
    }

    @EventHandler
    public void onCommand(ServerCommandEvent e) {
        preprocessEvent(e, e.getSender(), e.getCommand());
    }

    public void preprocessEvent(Cancellable event, CommandSender sender, String command) {
        if (command.startsWith("sf banitem") || command.startsWith("slimefun banitem")) {
            banitem(sender, command);
        } else if (command.startsWith("sf unbanitem") || command.startsWith("slimefun unbanitem")) {
            unbanitem(sender, command);
        }
    }

    private void sendNotice(CommandSender sender, String command) {
        sender.sendMessage(ChatColors.color("&e[JustEnoughGuide] JustEnoughGuide 已接管 " + command + " 指令的执行。如出现 bug，请反馈至 https://github.com/balugaq/JustEnoughGuide"));
    }

    public void unbanitem(CommandSender sender, String command) {
        if (sender instanceof Player player) {
            if (!noticedPlayers.contains(player.getUniqueId())) {
                sendNotice(sender, "/sf unbanitem");
                noticedPlayers.add(player.getUniqueId());
            }
        } else {
            if (!noticedConsole) {
                sendNotice(sender, "/sf unbanitem");
                noticedConsole = true;
            }
        }

        if (!sender.isOp() && !sender.hasPermission("slimefun.command.unbanitem") && !(sender instanceof ConsoleCommandSender)) {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            return;
        }

        String[] args = command.split(" ");

        if (args.length == 2) {
            // no id provided
            if (sender instanceof Player player) {
                SlimefunItem item = SlimefunItem.getByItem(player.getInventory().getItemInMainHand());
                if (item == null) {
                    Slimefun.getLocalization().sendMessage(sender, "invalid-item-in-hand", true);
                    return;
                }
                unbanitem(sender, item);
            } else {
                sendUsage(sender, "unbanitem");
            }
        } else if (args.length == 3) {
            // provided id
            SlimefunItem item = SlimefunItem.getById(args[2]);
            if (item == null) {
                Slimefun.getLocalization().sendMessage(
                    sender,
                    "messages.invalid-item",
                    true,
                    msg -> msg.replace("%item%", args[1]));
                return;
            }
            unbanitem(sender, item);
        } else {
            sendUsage(sender, "unbanitem");
        }
    }

    public void banitem(CommandSender sender, String command) {
        if (sender instanceof Player player) {
            if (!noticedPlayers.contains(player.getUniqueId())) {
                sendNotice(sender, "/sf banitem");
                noticedPlayers.add(player.getUniqueId());
            }
        } else {
            if (!noticedConsole) {
                sendNotice(sender, "/sf banitem");
                noticedConsole = true;
            }
        }

        if (!sender.isOp() && !sender.hasPermission("slimefun.command.banitem") && !(sender instanceof ConsoleCommandSender)) {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            return;
        }

        String[] args = command.split(" ");

        if (args.length == 2) {
            // no id provided
            if (sender instanceof Player player) {
                SlimefunItem item = SlimefunItem.getByItem(player.getInventory().getItemInMainHand());
                if (item == null) {
                    Slimefun.getLocalization().sendMessage(sender, "invalid-item-in-hand", true);
                    return;
                }
                banitem(sender, item);
            } else {
                sendUsage(sender, "banitem");
            }
        } else if (args.length == 3) {
            // provided id
            SlimefunItem item = SlimefunItem.getById(args[2]);
            if (item == null) {
                Slimefun.getLocalization().sendMessage(
                    sender,
                    "messages.invalid-item",
                    true,
                    msg -> msg.replace("%item%", args[1]));
                return;
            }
            banitem(sender, item);
        } else {
            sendUsage(sender, "banitem");
        }
    }

    public void unbanitem(SlimefunItem slimefunItem) {
        slimefunItem.enable();
        Slimefun.getItemCfg().setValue(slimefunItem.getId() + ".enabled", true);
        Slimefun.getItemCfg().save();
    }

    public void banitem(SlimefunItem slimefunItem) {
        slimefunItem.disable();
        Slimefun.getItemCfg().setValue(slimefunItem.getId() + ".enabled", false);
        Slimefun.getItemCfg().save();
    }

    public void unbanitem(CommandSender sender, SlimefunItem item) {
        String formattedName = item.getItemName() + " &r&c(" + item.getId() + ")";
        Set<SlimefunItem> items = ReplacementCardAdapter.getAdaptedItems(item);

        int count = 0;
        if (items != null) {
            for (SlimefunItem sf2 : items) {
                if (sf2.isDisabled()) {
                    unbanitem(sf2);
                    if (PaperLib.isPaper()) {
                        sender.sendMessage(Component.text().color(NamedTextColor.GREEN)
                            .append(Component.text("[JustEnoughGuide] 已解禁配方补全伴生物品 "))
                            .append(ClipboardUtil.makeComponentPaper(
                                Component.text(sf2.getId()),
                                sf2.getId()
                            ))
                            .append(Component.text(".")
                            ));
                    } else {
                        sender.sendMessage(ChatColors.color("&a[JustEnoughGuide] 已解禁配方补全伴生物品 " + sf2.getId() + "."));
                    }
                    count++;
                }
            }
        }

        if (count != 0) {
            sender.sendMessage(ChatColors.color("&a[JustEnoughGuide] 已解禁 " + count + " 个配方补全伴生物品."));
        } else {
            if (items != null) {
                sender.sendMessage(ChatColors.color("&c[JustEnoughGuide] " + items.size() + " 个配方补全伴生物品均已是解禁状态."));
            }
        }

        if (!item.isDisabled()) {
            if (PaperLib.isPaper()) {
                sender.sendMessage(Component.text().color(NamedTextColor.GREEN).append(Component.text("[JustEnoughGuide] 物品 "))
                    .append(ClipboardUtil.makeComponentPaper(
                        Component.text(item.getItemName()),
                        ChatColor.stripColor(item.getItemName())
                    ))
                    .append(ClipboardUtil.makeComponentPaper(
                        Component.text(" (" + item.getId() + ") "),
                        item.getId()
                    ))
                    .append(Component.text(" 已是解禁状态."))
                );
            } else {
                sender.sendMessage(ChatColors.color("&c[JustEnoughGuide] 物品 " + formattedName + " 已是解禁状态."));
            }
            return;
        } else {
            unbanitem(item);
            if (PaperLib.isPaper()) {
                sender.sendMessage(Component.text().color(NamedTextColor.GREEN).append(Component.text("[JustEnoughGuide] 已解禁物品 "))
                    .append(ClipboardUtil.makeComponentPaper(
                        Component.text(item.getItemName()),
                        ChatColor.stripColor(item.getItemName())
                    ))
                    .append(ClipboardUtil.makeComponentPaper(
                        Component.text(" (" + item.getId() + ") "),
                        item.getId()
                    ))
                    .append(Component.text("."))
                );
            } else {
                sender.sendMessage(ChatColors.color("&a[JustEnoughGuide] 已解禁物品 " + formattedName + "."));
            }
        }
    }

    public void banitem(CommandSender sender, SlimefunItem item) {
        String formattedName = item.getItemName() + " &r&c(" + item.getId() + ")";
        Set<SlimefunItem> items = ReplacementCardAdapter.getAdaptedItems(item);

        int count = 0;
        if (items != null) {
            for (SlimefunItem sf2 : items) {
                if (!sf2.isDisabled()) {
                    banitem(sf2);
                    if (PaperLib.isPaper()) {
                        sender.sendMessage(Component.text().color(NamedTextColor.GREEN)
                            .append(Component.text("[JustEnoughGuide] 已禁用配方补全伴生物品 "))
                            .append(ClipboardUtil.makeComponentPaper(
                                Component.text(sf2.getId()),
                                sf2.getId()
                            ))
                            .append(Component.text("."))
                        );
                    } else {
                        sender.sendMessage(ChatColors.color("&a[JustEnoughGuide] 已禁用配方补全伴生物品 " + sf2.getId() + "."));
                    }
                    count++;
                }
            }
        }

        if (count != 0) {
            sender.sendMessage(ChatColors.color("&a[JustEnoughGuide] 已禁用 " + count + " 个配方补全伴生物品."));
        } else {
            if (items != null) {
                sender.sendMessage(ChatColors.color("&c[JustEnoughGuide] " + items.size() + " 个配方补全伴生物品均已是禁用状态."));
            }
        }

        if (item.isDisabled()) {
            if (PaperLib.isPaper()) {
                sender.sendMessage(Component.text().color(NamedTextColor.RED).append(Component.text("[JustEnoughGuide] 物品 "))
                    .append(ClipboardUtil.makeComponentPaper(
                        Component.text(item.getItemName()),
                        ChatColor.stripColor(item.getItemName())
                    ))
                    .append(ClipboardUtil.makeComponentPaper(
                        Component.text(" (" + item.getId() + ") "),
                        item.getId()
                    ))
                    .append(Component.text(" 已经是禁用状态."))
                );
            } else {
                sender.sendMessage(ChatColors.color("&c[JustEnoughGuide] 物品 " + formattedName + " 已经是禁用状态."));
            }
            return;
        } else {
            banitem(item);
            if (PaperLib.isPaper()) {
                sender.sendMessage(Component.text().color(NamedTextColor.GREEN).append(Component.text("[JustEnoughGuide] 已禁用物品 "))
                    .append(ClipboardUtil.makeComponentPaper(
                        Component.text(item.getItemName()),
                        ChatColor.stripColor(item.getItemName())
                    ))
                    .append(ClipboardUtil.makeComponentPaper(
                        Component.text(" (" + item.getId() + ") "),
                        item.getId()
                    ))
                    .append(Component.text("."))
                );
            } else {
                sender.sendMessage(ChatColors.color("&a[JustEnoughGuide] 已禁用物品 " + formattedName + "."));
            }
        }
    }

    public void sendUsage(CommandSender sender, String command) {
        Slimefun.getLocalization().sendMessage(
            sender,
            "messages.usage",
            true,
            msg -> msg.replace("%usage%", "/sf " + command + " <Slimefun Item ID>")
        );
    }
}
