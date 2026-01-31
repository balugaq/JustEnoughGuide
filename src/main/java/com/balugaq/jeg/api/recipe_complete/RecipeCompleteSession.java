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
import com.balugaq.jeg.api.recipe_complete.source.base.Source;
import com.balugaq.jeg.utils.GuideUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author balugaq
 * @since 2.0
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@NullMarked
@SuppressWarnings("deprecation")
public class RecipeCompleteSession {
    private final Player player;
    private final Map<Source, Object> cache = new HashMap<>();
    private final Set<Source> notHandleable = new HashSet<>();
    private GuideEvents.ItemButtonClickEvent event;
    private Location target;
    private Block block;
    private Inventory inventory;
    private BlockMenu menu;
    private ClickAction clickAction;
    private @Range(from = 0, to = 53) int[] ingredientSlots;
    private boolean unordered;
    private int recipeDepth;
    private int pushed;

    private RecipeCompleteSession(Player player) {
        this.player = player;
    }

    @Nullable
    public static RecipeCompleteSession create(BlockMenu menu, Player player, ClickAction clickAction, @Range(from = 0, to = 53) int[] ingredientSlots, boolean unordered, int recipeDepth) {
        player = GuideUtil.updatePlayer(player);
        if (player == null) return null;
        var session = create(player);
        session.menu = menu;
        session.clickAction = clickAction;
        session.ingredientSlots = ingredientSlots;
        session.unordered = unordered;
        session.recipeDepth = recipeDepth;
        return session;
    }

    @Nullable
    public static RecipeCompleteSession create(Block block, Inventory inventory, Player player, ClickAction clickAction, @Range(from = 0, to = 53) int[] ingredientSlots, boolean unordered, int recipeDepth) {
        player = GuideUtil.updatePlayer(player);
        if (player == null) return null;
        var session = create(player);
        session.block = block;
        session.inventory = inventory;
        session.clickAction = clickAction;
        session.ingredientSlots = ingredientSlots;
        session.unordered = unordered;
        session.recipeDepth = recipeDepth;
        return session;
    }

    public static RecipeCompleteSession create(Player player) {
        return new RecipeCompleteSession(player);
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
        return pushed > 3456 || Source.depthInRange(player, recipeDepth);
    }
}
