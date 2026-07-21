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

import com.balugaq.jeg.api.objects.events.PatchEvent;
import com.balugaq.jeg.utils.compatibility.Converter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.9
 */
@NullMarked
public class GuideGUIFixListener implements Listener {
    /**
     * Fix Official Slimefun: SlimefunItemStack break changes Fix 1.21+ Minecraft bug:
     * SlimefunItemStack cannot cast toCraftItemStack
     *
     * @param event PatchEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void patchItem(PatchEvent event) {
        event.setItemStack(Converter.getItem(event.getItemStack()));
    }
}
