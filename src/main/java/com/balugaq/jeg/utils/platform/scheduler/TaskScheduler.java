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
import com.balugaq.jeg.utils.platform.PlatformUtil;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * @author balugaq
 * @since 2.0
 */
public interface TaskScheduler {
    static @NotNull Server getServer() {
        return Bukkit.getServer();
    }

    static @NotNull TaskScheduler create() {
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

    default @NotNull Plugin getPlugin() {
        return JustEnoughGuide.getInstance();
    }

    void runNextTick(@NotNull Runnable runnable);

    void runLater(@NotNull Runnable runnable, long delay);

    void runLaterAsync(@NotNull Runnable runnable, long delay);

    void runTimer(@NotNull Runnable runnable, long delay, long period);

    void runTimerAsync(@NotNull Runnable runnable, long delay, long period);

}
