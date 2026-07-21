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

package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.KeyUtil;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings("deprecation")
@NullMarked
public abstract class BaseGroup<T extends BaseGroup<T>> extends FlexItemGroup implements Cloneable {
    protected final Int2ObjectOpenHashMap<T> pageMap = new Int2ObjectOpenHashMap<>();
    @Getter
    @Setter
    protected boolean hidden;
    protected int page;

    protected BaseGroup() {
        this(KeyUtil.random(), ItemStackUtil.barrier());
    }

    protected BaseGroup(NamespacedKey key, ItemStack item) {
        super(key, item);
    }

    protected BaseGroup(NamespacedKey key, ItemStack item, int tier) {
        super(key, item, tier);
    }

    @Override
    public boolean isVisible(
        final Player player,
        final PlayerProfile playerProfile,
        final SlimefunGuideMode slimefunGuideMode) {
        return !isHidden();
    }

    @Override
    public void open(
        final Player player,
        final PlayerProfile playerProfile,
        final SlimefunGuideMode slimefunGuideMode) {
        playerProfile.getGuideHistory().add(this, page);
        ChestMenu menu = generateMenu(player, playerProfile, slimefunGuideMode);
        if (menu != null) {
            menu.open(player);
        }
    }

    @Nullable
    protected abstract ChestMenu generateMenu(
        final Player player,
        final PlayerProfile playerProfile,
        final SlimefunGuideMode slimefunGuideMode);

    public T getByPage(int page) {
        if (pageMap.containsKey(page)) {
            return pageMap.get(page);
        } else {
            synchronized (pageMap) {
                if (pageMap.containsKey(page)) {
                    return pageMap.get(page);
                }

                T group = clone();
                group.page = page;
                pageMap.put(page, group);
                return group;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T clone() {
        try {
            return (T) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
