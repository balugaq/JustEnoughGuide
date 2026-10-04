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

import com.balugaq.jeg.api.objects.events.SlimefunTickEndEvent;
import com.balugaq.jeg.api.objects.events.SlimefunTickStartEvent;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.services.profiler.PerformanceRating;
import io.github.thebusybiscuit.slimefun4.core.services.profiler.SlimefunProfiler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.TickerTask;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author balugaq
 * @since 2.2
 */
@NullMarked
public class JEGProfiler extends SlimefunProfiler {
    /**
     * hover 里最多展示的条目数。
     */
    public static final int MAX_ITEMS = 20;
    public static final int MAX_TOP_ITEMS = 20;

    private static @Nullable JEGProfiler instance;

    private final Map<SlimefunItem, LongList> itemStats = new ConcurrentHashMap<>();
    private final Map<SlimefunAddon, LongList> addonStats = new ConcurrentHashMap<>();
    private final Map<ChunkKey, LongList> chunkStats = new ConcurrentHashMap<>();

    private final AtomicLong totalNanos = new AtomicLong();

    private final Object2BooleanOpenHashMap<CommandSender> waiting = new Object2BooleanOpenHashMap<>();
    private final PriorityQueue<TimedSample> topBlocks = new PriorityQueue<>();

    private JEGProfiler() {
        super();
    }

    public static @Nullable JEGProfiler install() {
        Slimefun plugin = Slimefun.instance();
        if (plugin == null) return null;

        JEGProfiler profiler = new JEGProfiler();

        if (!ReflectionUtil.setValue(plugin, "profiler", profiler)) {
            JustEnoughGuide.getInstance().getLogger()
                .warning("[JEG] 无法替换 Slimefun 的 Profiler");
            return null;
        }

        // 回读校验：final 字段在部分 JVM 上会拒绝写入或被常量折叠，不能只信 setValue 的返回值
        Object actual = ReflectionUtil.getValue(plugin, "profiler");
        if (actual != profiler) {
            JustEnoughGuide.getInstance().getLogger()
                .warning("[JEG] Profiler 替换未生效");
            return null;
        }

        instance = profiler;
        return profiler;
    }

    public static @Nullable JEGProfiler getInstance() {
        return instance;
    }

    /**
     * 任意机器每次 tick 都会调用
     */
    public void record(Location location, SlimefunItem item, long nanos) {
        // todo，由于粘液加速器存在，record可能是被异步执行的，需要考虑并发。
        // todo，只加到queue里以最快速度结束 record，避免影响 /sf timings 的计时
        World world = location.getWorld();

        TimedSample sample = new TimedSample(location, item, nanos);
        pushSample(itemStats, item, sample.nanos());
        pushSample(addonStats, item.getAddon(), sample.nanos());

        ChunkKey chunkKey = new ChunkKey(world.getName(),
                location.getBlockX() >> 4, location.getBlockZ() >> 4);
        pushSample(chunkStats, chunkKey, sample.nanos());
        topBlocks.add(sample);
        if (topBlocks.size() > MAX_TOP_ITEMS) {
            topBlocks.poll();
        }

        totalNanos.addAndGet(nanos);
    }

    private static <K> void pushSample(Map<K, LongList> table, K key, long value) {
        var bucket = table.computeIfAbsent(key, k -> new LongArrayList(64));
        synchronized (bucket) {
            bucket.add(value);
        }
    }


    /**
     * @param verbose 是否输出详细统计（分位数等）
     */
    public void requestReport(CommandSender sender, boolean verbose) {
        waiting.put(sender, verbose);
    }

    @Override
    public void start() {
        super.start();
        resetTimings();
        new SlimefunTickStartEvent().callEvent();
    }

    @Override
    public void stop() {
        super.stop();
        new SlimefunTickEndEvent().callEvent();

        if (waiting.isEmpty()) return;
        TimingsSnapshot snapshot = snapshot();
        JustEnoughGuide.runLaterAsync(() -> dispatch(snapshot, waiting), 1L);
    }

    public void resetTimings() {
        itemStats.clear();
        chunkStats.clear();
        addonStats.clear();
        topBlocks.clear();
        totalNanos.set(0L);
    }

    public static boolean isTickFreeze() {
        try {
            TickerTask ticker = Slimefun.getTickerTask();
            Boolean frozen = ReflectionUtil.getValue(ticker, "tickFreeze", Boolean.class);
            return frozen != null && frozen;
        } catch (Exception | LinkageError x) {
            return false;
        }
    }

    public static int getTickerRate() {
        return Slimefun.getTickerTask().getTickRate();
    }

    private void dispatch(TimingsSnapshot snapshot, Object2BooleanOpenHashMap<CommandSender> requesters) {
        if (requesters.isEmpty()) return;

        for (var request : requesters.object2BooleanEntrySet()) {
            var sender = request.getKey();
            var verbose = request.getBooleanValue();
            if (!sender.isOp() && verbose) {
                sender.sendMessage(Component.text("[JustEnoughGuide] 你没有权限使用 --verbose 参数！", NamedTextColor.RED));
                continue;
            }

            // 不耗时，先就这样吧
            ComponentReport report = ComponentReport.of(snapshot, verbose);
            for (Component component : report.components()) {
                sender.sendMessage(component);
            }
        }
        requesters.clear();
    }

    public TimingsSnapshot snapshot() {
        return new TimingsSnapshot(
            viewOf(itemStats),
            viewOf(addonStats),
            viewOf(chunkStats),
            topBlocks,
            totalNanos.get(),
            getTickerRate(),
            isTickFreeze());
    }

    public static <K> Map<K, LongList> viewOf(Map<K, LongList> table) {
        Map<K, LongList> copy = new LinkedHashMap<>(table.size());
        for (var entry : table.entrySet()) {
            var bucket = entry.getValue();
            synchronized (bucket) {
                copy.put(entry.getKey(), new LongArrayList(bucket));
            }
        }
        return copy;
    }

    /**
     * 计算耗时占一个 tick 周期的百分比。
     *
     * @param totalNanos 总耗时（纳秒）
     * @return 百分比
     */
    public static float percentageOfTick(long totalNanos) {
        float millis = totalNanos / 1000000.0F;
        float fraction = (millis * 100.0F) / (1000.0F / getTickerRate());
        return Math.round((fraction * 100.0F) / 100.0F);
    }

    public static PerformanceRating performanceOf(long totalNanos) {
        float percentage = percentageOfTick(totalNanos);

        for (PerformanceRating rating : PerformanceRating.values()) {
            if (rating.test(percentage)) {
                return rating;
            }
        }

        return PerformanceRating.UNKNOWN;
    }

    public static String ratingName(PerformanceRating rating) {
        return ChatUtils.humanize(rating.name());
    }

    public static String asMillis(long nanos) {
        return NumberUtils.getAsMillis(nanos);
    }

}
