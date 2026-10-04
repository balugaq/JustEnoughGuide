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

import org.jspecify.annotations.NullMarked;

/**
 * 区块统计的键：世界名 + 区块坐标。
 * <p>
 * 同时承担两个职责：{@link #displayName()} 用于报告里展示，
 * 坐标本身用于「点击传送到最耗时区块中心」——不再需要从样本里反查。
 * <p>
 * 这里刻意<b>不持有</b> {@link org.bukkit.World} 引用，只保留世界名，
 * 避免统计表把世界对象钉在内存里；渲染时再按名字解析回 {@link org.bukkit.World}。
 *
 * @param worldName 世界名称
 * @param chunkX    区块 X
 * @param chunkZ    区块 Z
 * @author balugaq
 * @since 2.2
 */
@NullMarked
public record ChunkKey(String worldName, int chunkX, int chunkZ) {

    /**
     * 区块展示名，形如 {@code world (12,-3)}。
     *
     * @return 展示名
     */
    public String displayName() {
        return worldName + " (" + chunkX + ',' + chunkZ + ')';
    }
}
