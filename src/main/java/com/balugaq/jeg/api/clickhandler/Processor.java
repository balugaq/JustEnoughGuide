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
     * @param guide            the guide
     * @param menu             the menu
     * @param event            the event
     * @param player           the player
     * @param clickedSlot      the clicked slot
     * @param clickedItemStack the clicked item stack
     * @param clickAction      the click action
     * @param processedResult  the processed result, null if the {@link Processor#getStrategy()} is {@link Processor.Strategy#HEAD}.
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
