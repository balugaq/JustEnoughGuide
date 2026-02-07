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

package com.balugaq.jeg.api.recipe_complete;

import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.api.objects.events.RecipeCompleteEvents;
import com.balugaq.jeg.api.recipe_complete.source.base.Source;
import com.balugaq.jeg.utils.GuideUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author balugaq
 * @since 2.0
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@NullMarked
@SuppressWarnings("deprecation")
public class RecipeCompleteSession {
    private static Map<Player, RecipeCompleteSession> SESSIONS = new ConcurrentHashMap<>();
    private final Map<Source, Object> cache = new HashMap<>();
    private final Set<Source> notHandleable = new HashSet<>();
    private Player player;
    private GuideEvents.ItemButtonClickEvent event;
    private Location target;
    private Block block;
    private Inventory inventory;
    private BlockMenu menu;
    private ClickAction clickAction;
    private @Range(from = 0, to = 53) int[] ingredientSlots;
    private boolean unordered;
    private @Positive int recipeDepth;
    private @NonNegative int pushed;
    private boolean expired;

    @Nullable
    public static RecipeCompleteSession create(BlockMenu menu, Player player, ClickAction clickAction, @Range(from = 0, to = 53) int[] ingredientSlots, boolean unordered, int recipeDepth) {
        player = GuideUtil.updatePlayer(player);
        if (player == null) return null;
        var session = new RecipeCompleteSession();
        session.player = player;
        session.menu = menu;
        session.clickAction = clickAction;
        session.ingredientSlots = ingredientSlots;
        session.unordered = unordered;
        session.recipeDepth = recipeDepth;
        return fireEvent(session);
    }

    @Nullable
    public static RecipeCompleteSession create(Block block, Inventory inventory, Player player, ClickAction clickAction, @Range(from = 0, to = 53) int[] ingredientSlots, boolean unordered, int recipeDepth) {
        player = GuideUtil.updatePlayer(player);
        if (player == null) return null;
        var session = new RecipeCompleteSession();
        session.player = player;
        session.block = block;
        session.inventory = inventory;
        session.clickAction = clickAction;
        session.ingredientSlots = ingredientSlots;
        session.unordered = unordered;
        session.recipeDepth = recipeDepth;
        return fireEvent(session);
    }

    @Nullable
    private static RecipeCompleteSession fireEvent(RecipeCompleteSession session) {
        var event = new RecipeCompleteEvents.SessionCreateEvent(session);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return null;
        SESSIONS.put(session.getPlayer(), session);
        return event.getSession();
    }

    @Nullable
    public <T> T getCache(Source source, Class<T> clazz) {
        var obj = cache.get(source);
        return clazz.isInstance(obj) ? clazz.cast(obj) : null;
    }

    public void setCache(Source source, Object obj) {
        cache.put(source, obj);
    }

    @SuppressWarnings("ConstantValue")
    public Location getLocation() {
        return block != null ? block.getLocation() : menu.getLocation();
    }

    public boolean isNotHandleable(Source source) {
        return notHandleable.contains(source);
    }

    public void setNotHandleable(Source source) {
        notHandleable.add(source);
    }

    public boolean isExpired() {
        return expired || pushed > 3456 || !Source.depthInRange(player, recipeDepth);
    }

    @Nullable
    public static RecipeCompleteSession getSession(Player player) {
        return SESSIONS.get(player);
    }

    public static void setExpired(Player player) {
        var session = getSession(player);
        if (session != null) session.setExpired(true);
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
        if (expired) {
            SESSIONS.remove(getPlayer());
        } else {
            SESSIONS.put(getPlayer(), this);
        }
    }

    public static void complete(Player player) {
        var session = getSession(player);
        if (session == null) return;
        complete(session);
    }

    public static void complete(RecipeCompleteSession session) {
        Bukkit.getPluginManager().callEvent(new RecipeCompleteEvents.SessionCompleteEvent(session));
        session.setExpired(true);
    }

    public static void cancel(Player player) {
        var session = getSession(player);
        if (session == null) return;
        cancel(session);
    }

    public static void cancel(RecipeCompleteSession session) {
        Bukkit.getPluginManager().callEvent(new RecipeCompleteEvents.SessionCancelEvent(session));
        session.setExpired(true);
    }

    public ClickAction getClickAction() {
        if (event != null) return event.getClickAction();
        return clickAction;
    }
}
