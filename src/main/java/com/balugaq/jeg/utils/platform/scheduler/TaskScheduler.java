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

package com.balugaq.jeg.utils.platform.scheduler;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.platform.PlatformUtil;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.function.Supplier;

/**
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings("unused")
@NullMarked
public interface TaskScheduler {
    static Server getServer() {
        return Bukkit.getServer();
    }

    static TaskScheduler create() {
        if (PlatformUtil.isLeaves()) return new LeavesTaskScheduler();
        if (PlatformUtil.isLeaf()) return new LeafTaskScheduler();
        if (PlatformUtil.isFolia()) return new FoliaTaskScheduler();
        if (PlatformUtil.isPurpur()) return new PurpurTaskScheduler();
        if (PlatformUtil.isPufferfish()) return new PufferfishTaskScheduler();
        if (PlatformUtil.isPaper()) return new PaperTaskScheduler();
        if (PlatformUtil.isSpigot()) return new SpigotTaskScheduler();
        if (PlatformUtil.isBukkit()) return new BukkitTaskScheduler();

        throw new IllegalStateException("Unknown Platform, unable to create TaskScheduler");
    }

    default Plugin getPlugin() {
        return JustEnoughGuide.getInstance();
    }

    default void runNextTick(Supplier<?> callable) {
        runNextTick(() -> {
            callable.get();
        });
    }

    void runNextTick(Runnable runnable);

    default void runLater(Supplier<?> callable, long delay) {
        runLater(() -> {
            callable.get();
        }, delay);
    }

    void runLater(Runnable runnable, long delay);

    default void runLaterAsync(Supplier<?> callable, long delay) {
        runLaterAsync(() -> {
            callable.get();
        }, delay);
    }

    void runLaterAsync(Runnable runnable, long delay);

    default void runTimer(Supplier<?> callable, long delay, long period) {
        runTimer(() -> {
            callable.get();
        }, delay, period);
    }

    void runTimer(Runnable runnable, long delay, long period);

    default void runTimerAsync(Supplier<?> callable, long delay, long period) {
        runTimerAsync(() -> {
            callable.get();
        }, delay, period);
    }

    void runTimerAsync(Runnable runnable, long delay, long period);
}
