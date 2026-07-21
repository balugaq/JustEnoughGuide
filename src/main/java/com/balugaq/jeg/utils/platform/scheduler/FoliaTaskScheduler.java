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
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.0
 */
@Getter
@NullMarked
public class FoliaTaskScheduler extends PaperTaskScheduler {
    private final FoliaLib foliaLib;

    public FoliaTaskScheduler() {
        foliaLib = new FoliaLib(JustEnoughGuide.getInstance());
    }

    public void runNextTick(Runnable runnable) {
        getPlatformScheduler().runNextTick(tsk -> runnable.run());
    }

    public PlatformScheduler getPlatformScheduler() {
        return foliaLib.getScheduler();
    }

    public void runLater(Runnable runnable, long delay) {
        getPlatformScheduler().runLater(tsk -> runnable.run(), delay);
    }

    public void runLaterAsync(Runnable runnable, long delay) {
        getPlatformScheduler().runLaterAsync(tsk -> runnable.run(), delay);
    }

    public void runTimer(Runnable runnable, long delay, long period) {
        getPlatformScheduler().runTimer(tsk -> runnable.run(), delay, period);
    }

    public void runTimerAsync(Runnable runnable, long delay, long period) {
        getPlatformScheduler().runTimerAsync(tsk -> runnable.run(), delay, period);
    }
}
