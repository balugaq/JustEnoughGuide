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
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 把 {@link TimingsSnapshot} 里三个维度的原始样本，整理成可直接渲染的分组统计。
 *
 * <h2>为什么原始样本就够用了？</h2>
 * <p>
 * 我们只关心<b>分位数、求和、计数</b>三种口径，而这三者都能从原始观测值直接算出：
 * 求和是遍历、计数是 size、中位数与 95 分位排序后取秩即可。
 * 因此没有必要在采集阶段维护滑动窗口或累加器——存原始 List 最省事，也最灵活
 * （以后想加 p99 只要改这里，不用动采集端）。
 *
 * <h2>「方块数量」的口径</h2>
 * <p>
 * 注意本类的「方块数量」指的是<b>样本条数</b>（即该机器被 tick 了多少次），
 * 而不是「地图上有多少台机器」。这两个概念在滑液里通常接近，
 * 但机器被拆除、区块卸载时会出现偏差，展示时按「样本数」理解即可。
 *
 * @author balugaq
 * @since 2.2
 */
@NullMarked
public final class TimingsAggregator {

    private TimingsAggregator() {
    }

    /**
     * 一个分组的统计结果。
     *
     * @param key        分组键（机器 ID / 区块名 / 插件名）
     * @param count      样本条数
     * @param totalNanos 样本耗时之和（纳秒）
     * @param avgNanos   均值（纳秒）
     * @param minNanos   最小值（纳秒）
     * @param medNanos   中位数（纳秒）
     * @param p95Nanos   95 分位（纳秒）
     * @param maxNanos   最大值（纳秒）
     * @param chunk      区块键（仅区块维度分组有值），供点击传送到区块中心
     */
    public record ClassifiedGroup(String key, int count, long totalNanos, long avgNanos, long minNanos, long medNanos,
                                  long p95Nanos, long maxNanos, @Nullable ChunkKey chunk) implements Comparable<ClassifiedGroup> {

        /**
         * 样本不足 2 个时中位数没有统计意义，退化为均值展示。
         *
         * @return 展示用中位数
         */
        public long displayMedian() {
            return count <= 1 ? avgNanos : medNanos;
        }

        @Override
        public int compareTo(ClassifiedGroup o) {
            return Long.compare(this.totalNanos, o.totalNanos);
        }
    }

    /**
     * 三个维度的聚合结果，各自按总耗时降序。
     *
     * @param byItem   按机器聚合
     * @param byChunk  按区块聚合
     * @param byPlugin 按插件聚合
     */
    public record Aggregates(List<ClassifiedGroup> byItem, List<ClassifiedGroup> byChunk, List<ClassifiedGroup> byPlugin) {
    }

    /**
     * 执行聚合。
     *
     * @param snapshot 采样快照
     * @return 三维度聚合结果
     */
    public static Aggregates aggregate(TimingsSnapshot snapshot) {
        return new Aggregates(
                byItem(snapshot),
                byChunk(snapshot),
                byPlugin(snapshot));
    }

    private static List<ClassifiedGroup> byItem(TimingsSnapshot snapshot) {
        List<ClassifiedGroup> groups = new ArrayList<>(snapshot.itemStats().size());

        for (var entry : snapshot.itemStats().entrySet()) {
            SlimefunItem item = entry.getKey();
            groups.add(group(item.getId(), entry.getValue(), null));
        }

        groups.sort(Comparator.comparingLong(ClassifiedGroup::totalNanos).reversed());
        return groups;
    }

    private static List<ClassifiedGroup> byChunk(TimingsSnapshot snapshot) {
        List<ClassifiedGroup> groups = new ArrayList<>(snapshot.chunkStats().size());
        for (var entry : snapshot.chunkStats().entrySet()) {
            ChunkKey key = entry.getKey();
            groups.add(group(key.displayName(), entry.getValue(), key));
        }

        groups.sort(Comparator.comparingLong(ClassifiedGroup::totalNanos).reversed());
        return groups;
    }

    private static List<ClassifiedGroup> byPlugin(TimingsSnapshot snapshot) {
        Map<String, LongList> merged = new LinkedHashMap<>();

        for (var entry : snapshot.addonStats().entrySet()) {
            merged.computeIfAbsent(entry.getKey().getName(), k -> new LongArrayList()).addAll(entry.getValue());
        }

        List<ClassifiedGroup> groups = new ArrayList<>(merged.size());
        for (var entry : merged.entrySet()) {
            groups.add(group(entry.getKey(), entry.getValue(), null));
        }

        groups.sort(Comparator.comparingLong(ClassifiedGroup::totalNanos).reversed());
        return groups;
    }

    /**
     * 把一个样本列表压成统计结果。
     * <p>
     * 「耗时最高」直接对样本列表取 max：耗时与坐标同时拿到，
     * 不需要任何并行的热点记录。
     *
     * @param key     分组键
     * @param samples 原始样本
     * @param chunk   区块键，仅区块维度分组有值，可为 null
     * @return 统计结果
     */
    private static ClassifiedGroup group(String key, LongList samples, @Nullable ChunkKey chunk) {
        if (samples.isEmpty()) {
            return new ClassifiedGroup(key, 0, 0L, 0L, 0L, 0L, 0L, 0L, chunk);
        }

        long[] sorted = samples.longStream().sorted().toArray();
        long sum = samples.longStream().sum();

        int n = sorted.length;
        return new ClassifiedGroup(key, n, sum, sum / n, sorted[0], percentile(sorted, 0.5D), percentile(sorted, 0.95D), sorted[n - 1], chunk);
    }

    /**
     * 取分位数（最近秩法）。
     *
     * @param sorted 已升序排列的样本
     * @param p      分位，取值 0..1
     * @return 分位值
     */
    private static long percentile(long[] sorted, double p) {
        int n = sorted.length;
        if (n <= 1) {
            return n == 0 ? 0L : sorted[0];
        }

        int rank = (int) Math.ceil(p * n) - 1;
        return sorted[Math.max(0, Math.min(rank, n - 1))];
    }
}
