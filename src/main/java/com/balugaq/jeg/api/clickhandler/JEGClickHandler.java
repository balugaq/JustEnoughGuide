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

package com.balugaq.jeg.api.clickhandler;

import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author balugaq
 * @since 1.5
 */
@SuppressWarnings({"deprecation", "unused"})
public interface JEGClickHandler extends ChestMenu.AdvancedMenuClickHandler, GuideClickHandler {

    @ParametersAreNonnullByDefault
    @NotNull
    static JEGClickHandler of(
            final SlimefunGuideImplementation guide, final ChestMenu menu, final @Range(from = 0, to = 53) int slot) {
        ChestMenu.MenuClickHandler current = menu.getMenuClickHandler(slot);
        if (current instanceof JEGClickHandler ech) {
            return ech;
        }

        return new JEGClickHandler() {
            private final Map<Class<? extends Processor>, Processor> processors = new HashMap<>();

            @NotNull
            public ChestMenu.MenuClickHandler getOrigin() {
                return Optional.ofNullable(current).orElse(ChestMenuUtils.getEmptyClickHandler());
            }

            @NotNull
            public SlimefunGuideImplementation getGuide() {
                return guide;
            }

            @NotNull
            public ChestMenu getMenu() {
                return menu;
            }

            @NotNull
            public Map<Class<? extends Processor>, Processor> getProcessors() {
                return processors;
            }
        };
    }

    @NotNull
    default ChestMenu.MenuClickHandler getOrigin() {
        return ChestMenuUtils.getEmptyClickHandler();
    }

    @NotNull SlimefunGuideImplementation getGuide();

    @NotNull ChestMenu getMenu();

    @NotNull Map<Class<? extends Processor>, Processor> getProcessors();

    @NotNull
    default JEGClickHandler addProcessor(final @NotNull Processor processor) {
        getProcessors().put(processor.getClass(), processor);
        return this;
    }

    @NotNull
    default Collection<Processor> getProcessor(final @NotNull Processor.Strategy strategy) {
        return getProcessors().values().stream()
                .filter(processor -> processor.getStrategy() == strategy)
                .toList();
    }

    // Our implement
    @Override
    default boolean onClick(
            final @NotNull InventoryClickEvent event,
            final @NotNull Player player,
            final @Range(from = 0, to = 53) int clickedSlot,
            final ItemStack cursor,
            final @NotNull ClickAction clickAction) {
        ItemStack itemStack = getMenu().getItemInSlot(clickedSlot);
        for (Processor processor : getProcessor(Processor.Strategy.HEAD)) {
            if (!processor.process(getGuide(), getMenu(), event, player, clickedSlot, itemStack, clickAction, null)) {
                return false;
            }
        }

        ChestMenu.MenuClickHandler origin = getOrigin();
        if (origin instanceof ChestMenu.AdvancedMenuClickHandler amch) {
            return amch.onClick(event, player, clickedSlot, itemStack, clickAction);
        }

        boolean result = origin.onClick(player, clickedSlot, itemStack, clickAction);
        for (Processor processor : getProcessor(Processor.Strategy.TAIL)) {
            if (!processor.process(getGuide(), getMenu(), event, player, clickedSlot, itemStack, clickAction, result)) {
                return false;
            }
        }

        return result;
    }

    // Fallback
    @Override
    default boolean onClick(
            final Player player,
            final @Range(from = 0, to = 53) int clickedSlot,
            final ItemStack itemStack,
            final ClickAction clickAction) {
        return getOrigin().onClick(player, clickedSlot, itemStack, clickAction);
    }
}
