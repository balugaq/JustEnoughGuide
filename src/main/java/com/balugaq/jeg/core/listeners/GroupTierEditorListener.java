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
import org.jetbrains.annotations.NotNull;

/**
 * @author balugaq
 * @since 1.7
 */
public class GroupTierEditorListener implements Listener {
    @EventHandler
    public void onExit(@NotNull PlayerItemHeldEvent event) {
        GroupResorter.exitSelecting(event.getPlayer());
    }

    @EventHandler
    public void onExit(@NotNull PlayerJoinEvent event) {
        GroupResorter.exitSelecting(event.getPlayer());
    }

    @EventHandler
    public void onExit(@NotNull PlayerQuitEvent event) {
        GroupResorter.exitSelecting(event.getPlayer());
    }

    @EventHandler
    public void onOpenGuide(@NotNull PlayerInteractEvent event) {
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
    public void onUseGroupTierEditorGuide(@NotNull SlimefunGuideOpenEvent event) {
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
