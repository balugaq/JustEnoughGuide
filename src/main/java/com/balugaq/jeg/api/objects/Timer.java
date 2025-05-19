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

package com.balugaq.jeg.api.objects;

import com.balugaq.jeg.utils.Debug;

import javax.annotation.Nonnull;

/**
 * A simple timer class.
 *
 * @author balugaq
 * @since 1.2
 */
public class Timer {
    public static long start;
    public @Nonnull
    final String name;
    public long starts;

    /**
     * Constructs a new timer instance.
     *
     * @param name The name of the timer.
     */
    public Timer(@Nonnull String name) {
        this.name = name;
    }

    /**
     * Starts the timer.
     */
    public static void start() {
        start = System.nanoTime();
    }

    /**
     * Stops the timer.
     *
     * @return The time elapsed in nanoseconds.
     */
    public static long stop() {
        return System.nanoTime() - start;
    }

    /**
     * Logs the time elapsed since the last start.
     */
    public static void log() {
        Debug.debug("[Static] Time elapsed: " + stop() / 1_000_000.0F + "ms");
    }

    /**
     * Starts the timer.
     */
    public void starts() {
        this.starts = System.nanoTime();
    }

    /**
     * Stops the timer.
     *
     * @return The time elapsed in nanoseconds.
     */
    public long stops() {
        return System.nanoTime() - this.starts;
    }

    /**
     * Logs the time elapsed since the last start.
     */
    public void logs() {
        Debug.debug("[" + this.name + "] Time elapsed: " + stops() / 1_000_000.0F + "ms");
    }
}
