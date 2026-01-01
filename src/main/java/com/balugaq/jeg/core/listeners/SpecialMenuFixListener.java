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
     * @param event
     *         The event.
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
