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

import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.compatibility.Converter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
@Getter
@NullMarked
public class PatchEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final PatchScope patchScope;
    private final Player player;

    @Setter
    private ItemStack itemStack;

    public PatchEvent(
        final PatchScope patchScope, final Player player, final ItemStack itemStack) {
        super(!Bukkit.isPrimaryThread());
        this.patchScope = patchScope;
        this.player = player;
        this.itemStack = itemStack;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static ItemStack patch(
        final PatchScope patchScope, final Player player, @Nullable final ItemStack itemStack) {
        PatchEvent event = new PatchEvent(patchScope, player, Converter.getItem(itemStack));
        try {
            Bukkit.getPluginManager().callEvent(event);
        } catch (Throwable e) {
            Debug.trace(e);
        }
        return event.itemStack;
    }

    @Override
    public final HandlerList getHandlers() {
        return handlers;
    }
}
