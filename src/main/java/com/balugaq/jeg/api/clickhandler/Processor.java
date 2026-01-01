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

package com.balugaq.jeg.api.clickhandler;

import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import lombok.Data;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.7
 */
@Getter
@SuppressWarnings({"deprecation", "unused"})
@Data
@NullMarked
public abstract class Processor {
    private final Strategy strategy;

    /**
     * A simple Mixin processor Handles the events to happen when player clicked.
     *
     * @param guide
     *         the guide
     * @param menu
     *         the menu
     * @param event
     *         the event
     * @param player
     *         the player
     * @param clickedSlot
     *         the clicked slot
     * @param clickedItemStack
     *         the clicked item stack
     * @param clickAction
     *         the click action
     * @param processedResult
     *         the processed result, null if the {@link Processor#getStrategy()} is {@link Strategy#HEAD}.
     *
     * @return false if the process is handled successfully, true and handle other {@link Processor}s otherwise.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public abstract boolean process(
            final SlimefunGuideImplementation guide,
            final ChestMenu menu,
            final InventoryClickEvent event,
            final Player player,
            final @Range(from = 0, to = 53) int clickedSlot,
            final @Nullable ItemStack clickedItemStack,
            final ClickAction clickAction,
            final @Nullable Boolean processedResult);

    /**
     * @author balugaq
     * @since 1.7
     */
    public enum Strategy {
        HEAD,
        TAIL
    }
}
