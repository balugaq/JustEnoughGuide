package com.balugaq.jeg.core.profiler;

import com.balugaq.jeg.core.lang.Lang;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author balugaq
 * @since 2.2
 */
@RequiredArgsConstructor
@Data
public class ProfilerConsumer {
    final Queue<TimedSample> samples;
    final Map<SlimefunItem, LongList> itemStats = new HashMap<>();
    final Map<SlimefunAddon, LongList> addonStats = new HashMap<>();
    final Map<ChunkKey, LongList> chunkStats = new HashMap<>();

    long totalNanos = 0;

    final PriorityQueue<TimedSample> topBlocks = new PriorityQueue<>();

    private static <K> void pushSample(Map<K, LongList> table, K key, long value) {
        var bucket = table.computeIfAbsent(key, k -> new LongArrayList());
        bucket.add(value);
    }

    private void record(TimedSample sample) {
        pushSample(itemStats, sample.item(), sample.nanos());
        pushSample(addonStats, sample.item().getAddon(), sample.nanos());

        ChunkKey chunkKey = new ChunkKey(sample.worldName(),
            sample.x() >> 4, sample.z() >> 4);
        pushSample(chunkStats, chunkKey, sample.nanos());
        topBlocks.add(sample);
        if (topBlocks.size() > JEGProfiler.MAX_TOP_ITEMS) {
            topBlocks.poll();
        }

        totalNanos += sample.nanos();
    }

    public TimingsSnapshot snapshot() {
        return new TimingsSnapshot(
            itemStats,
            addonStats,
            chunkStats,
            topBlocks,
            totalNanos,
            JEGProfiler.getTickerRate(),
            JEGProfiler.isTickFreeze());
    }

    public void send(Object2BooleanOpenHashMap<CommandSender> waiting) {
        while (!samples.isEmpty()) {
            record(samples.poll());
        }
        TimingsSnapshot snapshot = snapshot();
        for (var request : waiting.object2BooleanEntrySet()) {
            var sender = request.getKey();
            if (sender instanceof Player player && !player.isOnline()) continue;
            var verbose = request.getBooleanValue();
            if (!sender.isOp() && verbose) {
                sender.sendMessage(Component.text(Lang.t("profiler.no-verbose-permission"), NamedTextColor.RED));
                continue;
            }

            // 不耗时，先就这样吧
            ComponentReport report = ComponentReport.of(snapshot, verbose);
            for (Component component : report.components()) {
                sender.sendMessage(component);
            }
        }
    }
}
