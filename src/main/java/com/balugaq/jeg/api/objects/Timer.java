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

package com.balugaq.jeg.api.objects;

import com.balugaq.jeg.utils.Debug;
import org.jspecify.annotations.NullMarked;

/**
 * A simple timer class.
 *
 * @author balugaq
 * @since 1.2
 */
@SuppressWarnings("unused")
@NullMarked
public class Timer {
    public static long start;
    public
    final String name;
    public long starts;

    /**
     * Constructs a new timer instance.
     *
     * @param name The name of the timer.
     */
    public Timer(String name) {
        this.name = name;
    }

    /**
     * Starts the timer.
     */
    public static void start() {
        start = System.nanoTime();
    }

    /**
     * Logs the time elapsed since the last start.
     */
    public static void log() {
        Debug.debug("[Static] Time elapsed: " + stop() / 1_000_000.0F + "ms");
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
     * Starts the timer.
     */
    public void starts() {
        this.starts = System.nanoTime();
    }

    /**
     * Logs the time elapsed since the last start.
     */
    public void logs() {
        Debug.debug("[" + this.name + "] Time elapsed: " + stops() / 1_000_000.0F + "ms");
    }

    /**
     * Stops the timer.
     *
     * @return The time elapsed in nanoseconds.
     */
    public long stops() {
        return System.nanoTime() - this.starts;
    }
}
