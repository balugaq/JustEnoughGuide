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

import com.balugaq.jeg.api.patches.JEGGuideHistory;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.SpecialMenuProvider;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jspecify.annotations.NullMarked;

import java.util.Deque;

/**
 * @author balugaq
 * @see SpecialMenuProvider
 * @since 1.3
 */
@SuppressWarnings("unused")
@NullMarked
public class SpecialMenuFixListener implements Listener {
    /**
     * Fixes the bug where the special menu is not closed properly.
     *
     * @param event The event.
     */
    @EventHandler
    public void onSpecialMenuClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        PlayerProfile profile = GuideUtil.getProfile(player);
        if (profile == null) return;

        GuideHistory history = profile.getGuideHistory();
        if (history instanceof JEGGuideHistory jeg) {
            jeg.removeTailPlaceholders();
            return;
        }

        try {
            @SuppressWarnings("unchecked")
            Deque<?> queue = (Deque<Object>) ReflectionUtil.getValue(history, "queue");
            if (queue == null || queue.isEmpty()) {
                return;
            }

            do {
                for (Object entry : queue) {
                    Object object = ReflectionUtil.getValue(entry, "object");
                }

                Object entry = queue.getLast();
                Object object = ReflectionUtil.getValue(entry, "object");
                if (!(object instanceof String string)) {
                    return;
                }
                if (SpecialMenuProvider.PLACEHOLDER_SEARCH_TERM.equals(string)) {
                    queue.removeLast();
                } else {
                    return;
                }
            } while (!queue.isEmpty());
        } catch (Exception e) {
            Debug.debug(e);
        }
    }
}
