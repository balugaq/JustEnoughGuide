package com.balugaq.jeg.api.objects.events;

import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import lombok.Getter;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RTSEvents {
    @Getter
    public static class CloseRTSEvent extends Event {
        private static final HandlerList HANDLERS = new HandlerList();
        private final Player player;
        private final AnvilGUI.StateSnapshot stateSnapshot;
        private final SlimefunGuideMode guideMode;

        public CloseRTSEvent(Player player, AnvilGUI.StateSnapshot stateSnapshot, SlimefunGuideMode guideMode) {
            super(!Bukkit.isPrimaryThread());
            this.player = player;
            this.stateSnapshot = stateSnapshot;
            this.guideMode = guideMode;
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @NotNull
        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    @Getter
    public static class OpenRTSEvent extends Event {
        private static final HandlerList HANDLERS = new HandlerList();
        private final Player player;
        private final AnvilInventory openingInventory;
        private final SlimefunGuideMode guideMode;
        private @Nullable
        final String presetSearchTerm;

        public OpenRTSEvent(Player player, AnvilInventory openingInventory, SlimefunGuideMode guideMode) {
            this(player, openingInventory, guideMode, null);
        }

        public OpenRTSEvent(Player player, AnvilInventory openingInventory, SlimefunGuideMode guideMode, String presetSearchTerm) {
            super(!Bukkit.isPrimaryThread());
            this.player = player;
            this.openingInventory = openingInventory;
            this.guideMode = guideMode;
            this.presetSearchTerm = presetSearchTerm;
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @NotNull
        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    @Getter
    public static class SearchTermChangeEvent extends Event {
        private static final HandlerList HANDLERS = new HandlerList();
        private final Player player;
        private final InventoryView inventoryView;
        private final AnvilInventory openingInventory;
        private final String oldSearchTerm;
        private final String newSearchTerm;
        private final SlimefunGuideMode guideMode;

        public SearchTermChangeEvent(Player player, InventoryView inventoryView, AnvilInventory openingInventory, String oldSearchTerm, String newSearchTerm, SlimefunGuideMode guideMode) {
            super(false);
            this.player = player;
            this.inventoryView = inventoryView;
            this.openingInventory = openingInventory;
            this.oldSearchTerm = oldSearchTerm;
            this.newSearchTerm = newSearchTerm;
            this.guideMode = guideMode;
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @NotNull
        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    @Getter
    public static class ClickAnvilItemEvent extends Event implements Cancellable {
        private static final HandlerList HANDLERS = new HandlerList();
        private final Player player;
        private final AnvilGUI.StateSnapshot stateSnapshot;
        private final SlimefunGuideMode guideMode;
        private final int slot;
        private boolean cancelled;

        public ClickAnvilItemEvent(Player player, AnvilGUI.StateSnapshot stateSnapshot, int slot, SlimefunGuideMode guideMode) {
            super(!Bukkit.isPrimaryThread());
            this.player = player;
            this.stateSnapshot = stateSnapshot;
            this.slot = slot;
            this.guideMode = guideMode;
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @NotNull
        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }

    @Getter
    public static class PageChangeEvent extends Event implements Cancellable {
        private static final HandlerList HANDLERS = new HandlerList();
        private final Player player;
        private final AnvilInventory openingInventory;
        private final SlimefunGuideMode guideMode;
        private final int oldPage;
        private final int newPage;
        private boolean cancelled;

        public PageChangeEvent(Player player, AnvilInventory openingInventory, int oldPage, int newPage, SlimefunGuideMode guideMode) {
            super(!Bukkit.isPrimaryThread());
            this.player = player;
            this.openingInventory = openingInventory;
            this.oldPage = oldPage;
            this.newPage = newPage;
            this.guideMode = guideMode;
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @NotNull
        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }
}
