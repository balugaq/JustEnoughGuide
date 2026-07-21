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
import com.balugaq.jeg.utils.clickhandler.OnClick;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import lombok.Getter;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * This is the implementation of the "/jeg share" command.
 *
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings("SwitchStatementWithTooFewBranches")
@Getter
@NullMarked
public class ShareCommand implements JEGCommand {
    @Override
    public List<String> onTabCompleteRaw(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return List.of("share");
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
            return "share".equalsIgnoreCase(args[0]);
        }
        return false;
    }

    @Override
    public void onCommand(
        final CommandSender sender,
        Command command,
        String label,
        String[] args) {
        onShare(sender);
    }

    @SuppressWarnings("ConstantValue")
    private void onShare(CommandSender sender) {
        if (sender instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType().isAir()) {
                item = player.getInventory().getItemInOffHand();
            }
            if (item == null || item.getType().isAir()) {
                player.sendMessage(ChatColors.color("&c请将物品放在手上"));
                return;
            }
            OnClick.share(player, ItemStackHelper.getDisplayName(item).trim());
        } else {
            sender.sendMessage(Slimefun.getLocalization().getMessage("messages.only-players"));
        }
    }
}
