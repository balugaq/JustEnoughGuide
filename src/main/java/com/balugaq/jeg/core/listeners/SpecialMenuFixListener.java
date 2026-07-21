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

import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.SpecialMenuProvider;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jspecify.annotations.NullMarked;

import java.util.Deque;
import java.util.Optional;

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
        Optional<PlayerProfile> optional = PlayerProfile.find(player);
        if (optional.isPresent()) {
            PlayerProfile profile = optional.get();
            try {
                @SuppressWarnings("unchecked")
                Deque<Object> queue = (Deque<Object>) ReflectionUtil.getValue(profile.getGuideHistory(), "queue");
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
                        // remove the last entry from the queue, which is the random search term
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
}
