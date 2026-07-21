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

import io.github.thebusybiscuit.slimefun4.api.events.SlimefunItemRegistryFinalizedEvent;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 2.1
 */
public class SlimefunRegistryFinalizeListener implements Listener {
    @Getter
    private static final List<Runnable> tasks = new ArrayList<>();

    public static void addTask(Runnable task) {
        tasks.add(task);
    }

    public static void clearTasks() {
        tasks.clear();
    }

    @EventHandler
    public void onRegistryFinalized(SlimefunItemRegistryFinalizedEvent event) {
        tasks.forEach(Runnable::run);
        clearTasks();
    }
}
