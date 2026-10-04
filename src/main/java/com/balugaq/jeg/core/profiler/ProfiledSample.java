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

package com.balugaq.jeg.core.profiler;

import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;

/**
 * 一次采样所对应的方块坐标与物品标识。
 * <p>
 * 这里刻意<b>不持有</b> {@link org.bukkit.World} 引用，只保留世界名，
 * 避免 profiler 的采样表把世界对象钉在内存里。
 *
 * @param worldName 世界名称
 * @param x         方块 X 坐标
 * @param y         方块 Y 坐标
 * @param z         方块 Z 坐标
 * @param itemId    该位置上运行的 SlimefunItem ID
 * @author balugaq
 * @since 2.2
 */
@NullMarked
public record ProfiledSample(String worldName, int x, int y, int z, String itemId) {

    /**
     * 由 {@link Location} 与物品 ID 构造一个采样键。
     *
     * @param location 方块位置
     * @param itemId   物品 ID
     */
    public ProfiledSample(Location location, String itemId) {
        this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), itemId);
    }

    /**
     * 取出该方块所属区块的展示名，形如 {@code world (12,-3)}。
     *
     * @return 区块展示名
     */
    public String chunkName() {
        return worldName + " (" + (x >> 4) + ',' + (z >> 4) + ')';
    }

    /**
     * 取出该方块的坐标展示名，形如 {@code 12, 64, -3}。
     *
     * @return 坐标展示名
     */
    public String positionName() {
        return x + ", " + y + ", " + z;
    }
}
