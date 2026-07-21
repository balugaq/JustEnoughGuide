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
import com.balugaq.jeg.utils.GuideUtil;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * This is the implementation of the "/jeg viewitem" command.
 *
 * @author balugaq
 * @since 2.1
 */
@SuppressWarnings("SwitchStatementWithTooFewBranches")
@Getter
@NullMarked
public class ViewItemCommand implements JEGCommand {
    @Override
    public List<String> onTabCompleteRaw(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return List.of("viewitem");
            }

            case 2 -> {
                return Slimefun.getRegistry().getEnabledSlimefunItems().stream().map(SlimefunItem::getId).toList();
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
        if (args.length == 1) {
            return "viewitem".equalsIgnoreCase(args[0]);
        }
        return false;
    }

    @Override
    public void onCommand(
        final CommandSender sender,
        Command command,
        String label,
        String[] args) {
        if (sender instanceof Player player) {
            if (args.length >= 2) {
                String id = args[1];
                SlimefunItem slimefunItem = SlimefunItem.getById(id.toUpperCase());
                if (slimefunItem == null || slimefunItem.isDisabledIn(player.getWorld())) {
                    player.sendMessage(ChatColors.color("&c无法查看 ID 为 " + id + "物品"));
                    return;
                }
                PlayerProfile profile = PlayerProfile.find(player).orElse(null);
                if (profile == null) return;
                GuideUtil.getLastGuide(player).displayItem(profile, slimefunItem, true);
            }
        } else {
            sender.sendMessage(Slimefun.getLocalization().getMessage("messages.only-players"));
        }
    }
}
