/*
 * Copyright (c) 2024-2026 balugaq
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

package com.balugaq.jeg.api.objects.events;

import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.0
 */
public class RecipeCompleteEvents {
    /**
     * @author balugaq
     * @since 2.0
     */
    @SuppressWarnings("unused")
    @Getter
    @NullMarked
    public static class SessionCreateEvent extends PlayerEvent implements Cancellable {
        private static final HandlerList HANDLERS = new HandlerList();
        private final RecipeCompleteSession session;

        public SessionCreateEvent(RecipeCompleteSession session) {
            super(session.getPlayer());
            this.session = session;
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public final HandlerList getHandlers() {
            return HANDLERS;
        }

        @Override
        public boolean isCancelled() {
            return session.isExpired();
        }

        @Override
        public void setCancelled(final boolean b) {
            session.setExpired(b);
        }
    }

    /**
     * @author balugaq
     * @since 2.0
     */
    @SuppressWarnings("unused")
    @Getter
    @NullMarked
    public static class SessionCancelEvent extends PlayerEvent {
        private static final HandlerList HANDLERS = new HandlerList();
        private final @Nullable RecipeCompleteSession session;

        public SessionCancelEvent(Player player) {
            super(player);
            this.session = RecipeCompleteSession.getSession(player);
        }

        public SessionCancelEvent(RecipeCompleteSession session) {
            super(session.getPlayer());
            this.session = session;
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public final HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 2.0
     */
    @SuppressWarnings("unused")
    @Getter
    @NullMarked
    public static class SessionCompleteEvent extends PlayerEvent {
        private static final HandlerList HANDLERS = new HandlerList();
        private final RecipeCompleteSession session;

        public SessionCompleteEvent(RecipeCompleteSession session) {
            super(session.getPlayer());
            this.session = session;
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public final HandlerList getHandlers() {
            return HANDLERS;
        }
    }
}
