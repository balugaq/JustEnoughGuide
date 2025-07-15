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

import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
@Getter
public class PatchEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final @NotNull PatchScope patchScope;
    private final @NotNull Player player;

    @Setter
    private @Nullable ItemStack itemStack;

    public PatchEvent(
            final @NotNull PatchScope patchScope, final @NotNull Player player, final @Nullable ItemStack itemStack) {
        super(!Bukkit.isPrimaryThread());
        this.patchScope = patchScope;
        this.player = player;
        this.itemStack = itemStack;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    public static @Nullable ItemStack patch(
            final @NotNull PatchScope patchScope, final @NotNull Player player, final @Nullable ItemStack itemStack) {
        PatchEvent event = new PatchEvent(patchScope, player, Converter.getItem(ItemStackUtil.getCleanItem(itemStack)));
        try {
            Bukkit.getPluginManager().callEvent(event);
        } catch (Throwable e) {
            Debug.trace(e);
        }
        return event.itemStack;
    }

    @Override
    public final @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
