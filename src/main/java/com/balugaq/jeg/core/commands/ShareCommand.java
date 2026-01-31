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
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * This is the implementation of the "/jeg share" command.
 *
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings({"ClassCanBeRecord", "deprecation", "SwitchStatementWithTooFewBranches"})
@Getter
@NullMarked
public class ShareCommand implements JEGCommand {
    private final Plugin plugin;

    public ShareCommand(Plugin plugin) {
        this.plugin = plugin;
    }

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
