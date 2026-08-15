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

package com.balugaq.jeg.core.integrations.rykenslimefuncustomizer;

import com.balugaq.jeg.api.SearchGroupLoader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.lins.mmmjjkx.rykenslimefuncustomizer.events.AddonDisableEvent;
import org.lins.mmmjjkx.rykenslimefuncustomizer.events.AddonEnableEvent;
import org.lins.mmmjjkx.rykenslimefuncustomizer.events.AddonLoadEvent;

/**
 * @author balugaq
 * @since 2.1
 */
public class SearchGroupIndexRebuildListener implements Listener {
    @EventHandler
    public void onEnable(AddonEnableEvent event) {
        rebuildSearchIndex();
    }

    @EventHandler
    public void onDisable(AddonDisableEvent event) {
        rebuildSearchIndex();
    }

    private static void rebuildSearchIndex() {
        SearchGroupLoader.load();
    }
}
