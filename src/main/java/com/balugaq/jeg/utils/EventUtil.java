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

package com.balugaq.jeg.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author balugaq
 * @since 1.6
 */
public class EventUtil {
    public static <T extends Event> @NotNull EventBuilder<T> callEvent(@NotNull T event) {
        return new EventBuilder<>(event);
    }

    @SuppressWarnings({"ClassCanBeRecord", "SameReturnValue", "unused"})
    @Getter
    public static class EventBuilder<T extends Event> {
        private final @NotNull T event;

        public EventBuilder(@NotNull T event) {
            this.event = event;
            try {
                Bukkit.getPluginManager().callEvent(event);
            } catch (Exception e) {
                Debug.trace(e);
            }
        }

        public boolean ifSuccess(@NotNull Runnable runnable) {
            if (event instanceof Cancellable cancellable) {
                if (cancellable.isCancelled()) {
                    return true;
                } else {
                    runnable.run();
                    return false;
                }
            } else {
                return true;
            }
        }

        public boolean ifCancelled(@NotNull Runnable runnable) {
            if (event instanceof Cancellable cancellable) {
                if (cancellable.isCancelled()) {
                    runnable.run();
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        public boolean thenRun(@NotNull Runnable runnable) {
            runnable.run();
            return true;
        }

        public boolean ifSuccess(@NotNull Consumer<T> consumer) {
            if (event instanceof Cancellable cancellable) {
                if (cancellable.isCancelled()) {
                    return true;
                } else {
                    consumer.accept(event);
                    return false;
                }
            } else {
                return true;
            }
        }

        public boolean ifCancelled(@NotNull Consumer<T> consumer) {
            if (event instanceof Cancellable cancellable) {
                if (cancellable.isCancelled()) {
                    consumer.accept(event);
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        public boolean thenRun(@NotNull Consumer<T> consumer) {
            consumer.accept(event);
            return true;
        }

        public boolean thenRun(@NotNull Function<T, Boolean> function) {
            return function.apply(event);
        }

        public boolean ifSuccess(@NotNull Function<T, Boolean> function) {
            if (event instanceof Cancellable cancellable) {
                if (cancellable.isCancelled()) {
                    return false;
                } else {
                    return function.apply(event);
                }
            } else {
                return function.apply(event);
            }
        }

        public boolean ifCancelled(@NotNull Function<T, Boolean> function) {
            if (event instanceof Cancellable cancellable) {
                if (cancellable.isCancelled()) {
                    return function.apply(event);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        public boolean ifSuccess(@NotNull Supplier<Boolean> callable) {
            if (event instanceof Cancellable cancellable) {
                if (cancellable.isCancelled()) {
                    return false;
                } else {
                    return callable.get();
                }
            } else {
                return callable.get();
            }
        }

        public boolean ifCancelled(@NotNull Supplier<Boolean> callable) {
            if (event instanceof Cancellable cancellable) {
                if (cancellable.isCancelled()) {
                    return callable.get();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        public boolean ifSuccess(boolean result) {
            if (event instanceof Cancellable cancellable) {
                if (cancellable.isCancelled()) {
                    return false;
                } else {
                    return result;
                }
            } else {
                return result;
            }
        }

        public boolean ifCancelled(boolean result) {
            if (event instanceof Cancellable cancellable) {
                if (cancellable.isCancelled()) {
                    return result;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
