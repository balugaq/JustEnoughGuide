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

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;

/**
 * 一次机器执行的原始样本：耗时 + 发生位置。
 * <p>
 * 之所以把坐标直接放进样本里，是因为「耗时最高的那台机器在哪」这类问题
 * 直接对样本列表取 max 就能同时拿到耗时和坐标，不需要再维护一套并行的热点表。
 * <p>
 * 这里刻意<b>不持有</b> {@link org.bukkit.World} 引用，只保留世界名，
 * 避免 profiler 的采样表把世界对象钉在内存里。
 *
 * @param nanos     本次执行的纳秒数
 * @param worldName 世界名称
 * @param x         方块 X 坐标
 * @param y         方块 Y 坐标
 * @param z         方块 Z 坐标
 * @author balugaq
 * @since 2.2
 */
@NullMarked
public record TimedSample(long nanos, SlimefunItem item, String worldName, int x, int y, int z) implements Comparable<TimedSample> {

    /**
     * 由 {@link Location} 与耗时构造一个样本。
     *
     * @param location 方块位置
     * @param nanos    本次执行的纳秒数
     */
    public TimedSample(Location location, SlimefunItem item, long nanos) {
        this(nanos, item, location.getWorld().getName(),
                location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * 取出该方块的坐标展示名，形如 {@code 12, 64, -3}。
     *
     * @return 坐标展示名
     */
    public String positionName() {
        return x + ", " + y + ", " + z;
    }

    @Override
    public int compareTo(TimedSample o) {
        return Long.compare(nanos, o.nanos);
    }
}
