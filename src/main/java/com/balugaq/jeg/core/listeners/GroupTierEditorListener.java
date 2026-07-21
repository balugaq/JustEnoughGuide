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

import com.balugaq.jeg.api.editor.GroupResorter;
import com.balugaq.jeg.implementation.items.GroupTierEditorGuide;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunGuideOpenEvent;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.7
 */
@NullMarked
public class GroupTierEditorListener implements Listener {
    @EventHandler
    public void onExit(PlayerItemHeldEvent event) {
        GroupResorter.exitSelecting(event.getPlayer());
    }

    @EventHandler
    public void onExit(PlayerJoinEvent event) {
        GroupResorter.exitSelecting(event.getPlayer());
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        GroupResorter.exitSelecting(event.getPlayer());
    }

    @EventHandler
    public void onOpenGuide(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            if (player.isOp()) {
                ItemStack itemStack = event.getItem();
                if (GroupTierEditorGuide.isGroupTierEditor(itemStack)) {
                    Bukkit.getPluginManager()
                        .callEvent(new SlimefunGuideOpenEvent(player, itemStack, SlimefunGuideMode.CHEAT_MODE));
                }
            }
        }
    }

    @EventHandler
    public void onUseGroupTierEditorGuide(SlimefunGuideOpenEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) {
            return;
        }

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (GroupTierEditorGuide.isGroupTierEditor(mainHand)) {
            GroupResorter.enterSelecting(player);
            return;
        }

        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (GroupTierEditorGuide.isGroupTierEditor(offHand)) {
            GroupResorter.enterSelecting(player);
        }
    }
}
