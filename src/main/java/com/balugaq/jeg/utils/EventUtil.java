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

package com.balugaq.jeg.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author balugaq
 * @since 1.6
 */
@NullMarked
public class EventUtil {
    public static <T extends Event> EventBuilder<T> callEvent(T event) {
        return new EventBuilder<>(event);
    }

    @SuppressWarnings({"ClassCanBeRecord", "SameReturnValue", "unused"})
    @Getter
    @NullMarked
    public static class EventBuilder<T extends Event> {
        private final T event;

        public EventBuilder(T event) {
            this.event = event;
            try {
                Bukkit.getPluginManager().callEvent(event);
            } catch (Exception e) {
                Debug.trace(e);
            }
        }

        public boolean ifSuccess(Runnable runnable) {
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

        public boolean ifCancelled(Runnable runnable) {
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

        public boolean thenRun(Runnable runnable) {
            runnable.run();
            return true;
        }

        public boolean ifSuccess(Consumer<T> consumer) {
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

        public boolean ifCancelled(Consumer<T> consumer) {
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

        public boolean thenRun(Consumer<T> consumer) {
            consumer.accept(event);
            return true;
        }

        public boolean thenRun(Function<T, Boolean> function) {
            return function.apply(event);
        }

        public boolean ifSuccess(Function<T, Boolean> function) {
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

        public boolean ifCancelled(Function<T, Boolean> function) {
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

        public boolean ifSuccess(Supplier<Boolean> callable) {
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

        public boolean ifCancelled(Supplier<Boolean> callable) {
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
