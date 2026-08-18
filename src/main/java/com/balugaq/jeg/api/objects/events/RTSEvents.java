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

package com.balugaq.jeg.api.objects.events;

import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import lombok.Getter;
import lombok.Setter;
import net.wesjd.anvilgui.AnvilGUI;
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
        private final AnvilGUI.StateSnapshot stateSnapshot;

        public CloseRTSEvent(Player player, AnvilGUI.StateSnapshot stateSnapshot) {
            super(player, !Bukkit.isPrimaryThread());
            this.player = player;
            this.stateSnapshot = stateSnapshot;
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

        public OpenRTSEvent(
            Player player,
            AnvilInventory openingInventory) {
            super(player, !Bukkit.isPrimaryThread());
            this.player = player;
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
        private final Object inventoryView; // Use Object to avoid InventoryView compatibility issues
        private final AnvilInventory openingInventory;
        private final @Nullable String oldSearchTerm;
        private final String newSearchTerm;
        private final SlimefunGuideMode guideMode;

        public SearchTermChangeEvent(
            Player player,
            Object inventoryView,
            AnvilInventory openingInventory,
            @Nullable String oldSearchTerm,
            String newSearchTerm,
            SlimefunGuideMode guideMode) {
            super(player);
            this.inventoryView = inventoryView;
            this.openingInventory = openingInventory;
            this.oldSearchTerm = oldSearchTerm;
            this.newSearchTerm = newSearchTerm;
            this.guideMode = guideMode;
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
    public static class ClickAnvilItemEvent extends PlayerEvent implements Cancellable {
        private static final HandlerList HANDLERS = new HandlerList();
        private final AnvilGUI.StateSnapshot stateSnapshot;
        private final int slot;
        private @Setter boolean cancelled;

        public ClickAnvilItemEvent(
            Player player, AnvilGUI.StateSnapshot stateSnapshot, int slot) {
            super(player, !Bukkit.isPrimaryThread());
            this.player = player;
            this.stateSnapshot = stateSnapshot;
            this.slot = slot;
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
        private @Setter boolean cancelled;

        public PageChangeEvent(
            Player player, AnvilInventory openingInventory, int oldPage, int newPage, SlimefunGuideMode guideMode) {
            super(!Bukkit.isPrimaryThread());
            this.player = player;
            this.openingInventory = openingInventory;
            this.oldPage = oldPage;
            this.newPage = newPage;
            this.guideMode = guideMode;
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