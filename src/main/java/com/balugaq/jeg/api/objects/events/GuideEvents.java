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

package com.balugaq.jeg.api.objects.events;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.7
 */
@SuppressWarnings({"deprecation", "unused"})
@NullMarked
public class GuideEvents {
    /**
     * @author balugaq
     * @since 1.7
     */
    @NullMarked
    public interface JEGGuideEvent {
        @Nullable ItemStack getClickedItem();

        @Range(from = 0, to = 53)
        int getClickedSlot();

        ClickAction getClickAction();

        ChestMenu getMenu();

        SlimefunGuideImplementation getGuide();
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class GroupLinkButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public GroupLinkButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class BeginnerButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public BeginnerButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class SettingsButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public SettingsButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class CollectItemEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public CollectItemEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 2.0
     */
    @Getter
    @NullMarked
    public static class CollectItemGroupEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();
        private final ItemGroup itemGroup;

        public CollectItemGroupEvent(
                final Player who,
                final ItemGroup itemGroup,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, itemGroup.getItem(who), clickedSlot, clickAction, menu, guide);
            this.itemGroup = itemGroup;
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class ResearchItemEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public ResearchItemEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class SearchItemEvent extends Event implements Cancellable {
        private static final HandlerList HANDLERS = new HandlerList();
        private final Player player;
        private final String searchTerm;
        private boolean cancelled = false;

        public SearchItemEvent(final Player player, final String searchTerm) {
            super(false);
            this.player = player;
            this.searchTerm = searchTerm;
        }

        public static HandlerList getHandlerList() {
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

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class FeatureButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public FeatureButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class AuthorInformationButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public AuthorInformationButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class BigRecipeButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public BigRecipeButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class RecipeTypeButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public RecipeTypeButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class WikiButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public WikiButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class BackButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public BackButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class ItemMarkButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public ItemMarkButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class BookMarkButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public BookMarkButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class NextButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public NextButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class PreviousButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public PreviousButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class ItemButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public ItemButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class ItemGroupButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public ItemGroupButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class RTSButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public RTSButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class SearchButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public SearchButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.9
     */
    @Getter
    @NullMarked
    public static class CerButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public CerButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 2.0
     */
    @Getter
    @NullMarked
    public static class KeybindsButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public KeybindsButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 2.0
     */
    @Getter
    @NullMarked
    public static class KeybindButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public KeybindButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 2.0
     */
    @Getter
    @NullMarked
    public static class ActionButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public ActionButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 2.0
     */
    @Getter
    @NullMarked
    public static class SubKeybindsButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public SubKeybindsButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public static class UnknownButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public UnknownButtonClickEvent(
                final Player who,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    @NullMarked
    public abstract static class GuideEvent extends Event implements Cancellable {
        private final Player player;
        private final @Nullable ItemStack clickedItem;
        private final @Range(from = 0, to = 53) int clickedSlot;
        private final ClickAction clickAction;
        private final ChestMenu menu;
        private final SlimefunGuideImplementation guide;
        public boolean cancelled = false;

        public GuideEvent(
                final Player player,
                final @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                final ClickAction clickAction,
                final ChestMenu menu,
                final SlimefunGuideImplementation guide) {
            super(false);
            this.player = player;
            this.clickedItem = clickedItem;
            this.clickedSlot = clickedSlot;
            this.clickAction = clickAction;
            this.menu = menu;
            this.guide = guide;
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
}
