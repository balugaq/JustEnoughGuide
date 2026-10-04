package com.balugaq.jeg.core.profiler;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import it.unimi.dsi.fastutil.longs.LongList;

import java.util.Map;
import java.util.PriorityQueue;

/**
 * 统计结果快照。
 *
 * @param itemStats       机器 -> 原始样本
 * @param addonStats      附属 -> 原始样本
 * @param chunkStats      区块键 -> 原始样本
 * @param roundTotalNanos 本轮总耗时（纳秒）
 * @param period          Ticker 运行周期（tick 数）
 * @param frozen          快照时是否处于 tick freeze
 * @author balugaq
 * @since 2.2
 */
public record TimingsSnapshot(
    Map<SlimefunItem, LongList> itemStats,
    Map<SlimefunAddon, LongList> addonStats,
    Map<ChunkKey, LongList> chunkStats,
    PriorityQueue<TimedSample> topBlock,
    long roundTotalNanos,
    int period,
    boolean frozen) {

    public int totalBlocks() {
        return itemStats().values().stream().mapToInt(LongList::size).sum();
    }
}
