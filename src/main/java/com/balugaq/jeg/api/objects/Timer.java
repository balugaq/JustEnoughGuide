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
