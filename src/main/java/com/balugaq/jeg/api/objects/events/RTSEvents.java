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

import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.AnvilInventory;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.3
 */
@NullMarked
public class RTSEvents {
    /**
     * Represents the event when the RTS is closed.
     *
     * @author balugaq
     * @since 1.3
     */
    @SuppressWarnings("unused")
    @Getter
    @NullMarked
    public static class CloseRTSEvent extends PlayerEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public CloseRTSEvent(Player player) {
            super(player);
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
     * Represents the event when the RTS is opened.
     *
     * @author balugaq
     * @since 1.3
     */
    @SuppressWarnings("unused")
    @Getter
    @NullMarked
    public static class OpenRTSEvent extends PlayerEvent implements Cancellable {
        private static final HandlerList HANDLERS = new HandlerList();
        private final AnvilInventory openingInventory;
        private @Setter boolean cancelled = false;

        public OpenRTSEvent(Player player, AnvilInventory openingInventory) {
            super(player);
            this.openingInventory = openingInventory;
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
     * Represents the event when the search term changes in the RTS.
     *
     * @author balugaq
     * @since 1.3
     */
    @SuppressWarnings("unused")
    @Getter
    @NullMarked
    public static class SearchTermChangeEvent extends PlayerEvent {
        private static final HandlerList HANDLERS = new HandlerList();
        private final @Nullable String oldSearchTerm;
        private final String newSearchTerm;

        public SearchTermChangeEvent(
                Player player,
                @Nullable String oldSearchTerm,
                String newSearchTerm) {
            super(player);
            this.oldSearchTerm = oldSearchTerm;
            this.newSearchTerm = newSearchTerm;
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
     * Represents the event when an item in the anvil GUI is clicked.
     *
     * @author balugaq
     * @since 1.3
     */
    @SuppressWarnings("unused")
    @Getter
    @NullMarked
    public static class ClickAnvilItemEvent extends Event implements Cancellable {
        private static final HandlerList HANDLERS = new HandlerList();
        private final Player player;
        private final int slot;
        private boolean cancelled;

        /**
         * Constructs a new ClickAnvilItemEvent.
         *
         * @param player
         *         The player who clicked the anvil item.
         * @param slot
         *         The slot that was clicked.
         * @param guideMode
         *         The guide mode.
         */
        public ClickAnvilItemEvent(
                Player player, int slot, SlimefunGuideMode guideMode) {
            super(!Bukkit.isPrimaryThread());
            this.player = player;
            this.slot = slot;
        }

        /**
         * Returns the handler list.
         *
         * @return the handler list
         */
        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        /**
         * Returns the handler list.
         *
         * @return the handler list
         */
        @Override
        public final HandlerList getHandlers() {
            return HANDLERS;
        }

        /**
         * Checks if the event is cancelled.
         *
         * @return true if the event is cancelled, false otherwise
         */
        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        /**
         * Sets the cancellation state of the event.
         *
         * @param cancelled
         *         the cancellation state
         */
        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }

    /**
     * Represents the event when the page changes in the RTS.
     *
     * @author balugaq
     * @since 1.3
     */
    @SuppressWarnings("unused")
    @Getter
    @NullMarked
    public static class PageChangeEvent extends Event implements Cancellable {
        private static final HandlerList HANDLERS = new HandlerList();
        private final Player player;
        private final AnvilInventory openingInventory;
        private final SlimefunGuideMode guideMode;
        private final int oldPage;
        private final int newPage;
        private boolean cancelled;

        /**
         * Constructs a new PageChangeEvent.
         *
         * @param player
         *         The player who changed the page.
         * @param openingInventory
         *         The opening inventory.
         * @param oldPage
         *         The old page number.
         * @param newPage
         *         The new page number.
         * @param guideMode
         *         The guide mode.
         */
        public PageChangeEvent(
                Player player, AnvilInventory openingInventory, int oldPage, int newPage, SlimefunGuideMode guideMode) {
            super(!Bukkit.isPrimaryThread());
            this.player = player;
            this.openingInventory = openingInventory;
            this.oldPage = oldPage;
            this.newPage = newPage;
            this.guideMode = guideMode;
        }

        /**
         * Returns the handler list.
         *
         * @return the handler list
         */
        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        /**
         * Returns the handler list.
         *
         * @return the handler list
         */
        @Override
        public final HandlerList getHandlers() {
            return HANDLERS;
        }

        /**
         * Sets the cancellation state of the event.
         *
         * @param cancelled
         *         the cancellation state
         */
        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }
}
