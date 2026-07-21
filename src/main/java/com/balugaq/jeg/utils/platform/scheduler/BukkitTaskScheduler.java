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

import lombok.Getter;
import org.bukkit.scheduler.BukkitScheduler;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.0
 */
@Getter
@NullMarked
public class BukkitTaskScheduler implements TaskScheduler {
    public void runNextTick(Runnable runnable) {
        getBukkitScheduler().runTask(getPlugin(), runnable);
    }

    public BukkitScheduler getBukkitScheduler() {
        return TaskScheduler.getServer().getScheduler();
    }

    public void runLater(Runnable runnable, long delay) {
        getBukkitScheduler().runTaskLater(getPlugin(), runnable, delay);
    }

    public void runLaterAsync(Runnable runnable, long delay) {
        getBukkitScheduler().runTaskLaterAsynchronously(getPlugin(), runnable, delay);
    }

    public void runTimer(Runnable runnable, long delay, long period) {
        getBukkitScheduler().runTaskTimer(getPlugin(), runnable, delay, period);
    }

    public void runTimerAsync(Runnable runnable, long delay, long period) {
        getBukkitScheduler().runTaskTimerAsynchronously(getPlugin(), runnable, delay, period);
    }
}
