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

    public PlatformScheduler getPlatformScheduler() {
        return foliaLib.getScheduler();
    }

    public void runNextTick(Runnable runnable) {
        getPlatformScheduler().runNextTick(tsk -> runnable.run());
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
