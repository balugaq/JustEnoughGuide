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

import com.balugaq.jeg.core.profiler.JEGProfiler.TimingsSnapshot;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 把 {@link TimingsSnapshot} 渲染成 Paper Adventure 的消息组件。
 * <p>
 * 整体版式对齐 Slimefun 的 {@code /sf timings}：
 * <pre>
 * ===== JEG 性能监视器 =====
 * Tick 总用时：1.4ms
 * Ticker 运行周期：0.5s (10 ticks)
 * Tick Freeze: √
 * 性能评分：■■■□□□□□□□□□□□□□□□□ - 良好 (12%)
 * 方块 | top 20 blocks (将鼠标放置到此处以查看详情)
 * 机器 | 295 blocks (将鼠标放置到此处以查看详情)
 * 区块 | 23 chunks (将鼠标放置到此处以查看详情)
 * 插件 | 8 plugins (将鼠标放置到此处以查看详情)
 * </pre>
 * 注意：这里全程使用 Adventure 的 {@link Component}，没有用 {@code §} 颜色码——
 * Adventure 的组件不会解析旧的 Bukkit 颜色码，直接拼字符串会全部退化成默认色。
 *
 * @author balugaq
 * @since 2.2
 */
@NullMarked
public final class ComponentReport {

    private static final String TITLE = "===== JEG 性能监视器 =====";
    private static final String HOVER_HINT = " (将鼠标放置到此处以查看详情)";
    private static final int BAR_WIDTH = 20;

    private final List<Component> components;

    private ComponentReport(List<Component> components) {
        this.components = components;
    }

    /**
     * 取出渲染好的消息组件列表。
     *
     * @return 组件列表
     */
    public List<Component> components() {
        return components;
    }

    /**
     * 依据快照构建报告。
     *
     * @param snapshot 采样快照
     * @return 渲染好的报告
     */
    public static ComponentReport of(TimingsSnapshot snapshot) {
        Aggregates agg = Aggregates.of(snapshot);

        List<Component> lines = new ArrayList<>();
        lines.add(Component.text(TITLE, NamedTextColor.GREEN));
        lines.add(label("Tick 总用时：", JEGProfiler.asMillis(snapshot.totalNanos())));
        lines.add(label("Ticker 运行周期：", periodText(snapshot.period())));
        lines.add(freezeLine(snapshot.frozen()));
        lines.add(ratingLine(snapshot.totalNanos()));
        lines.add(blocksLine(agg));
        lines.add(aggregateLine("机器", "blocks", agg.byItem, agg.itemCounts));
        lines.add(aggregateLine("区块", "chunks", agg.byChunk, agg.chunkCounts));
        lines.add(aggregateLine("插件", "plugins", agg.byPlugin, agg.pluginCounts));
        return new ComponentReport(List.copyOf(lines));
    }

    private static Component label(String key, String value) {
        return Component.text()
                .append(Component.text(key, NamedTextColor.GOLD))
                .append(Component.text(value, NamedTextColor.YELLOW))
                .build();
    }

    private static String periodText(int period) {
        if (period <= 0) {
            return "未知";
        }
        return round(period / 20.0) + "s (" + period + " ticks)";
    }

    private static Component freezeLine(boolean frozen) {
        return Component.text()
                .append(Component.text("Tick Freeze: ", NamedTextColor.GOLD))
                .append(Component.text(frozen ? "√" : "❌", frozen ? NamedTextColor.RED : NamedTextColor.GREEN))
                .build();
    }

    private static Component ratingLine(long totalNanos) {
        float percentage = JEGProfiler.percentageOfTick(totalNanos);
        float clamped = Math.min(percentage, 100.0F);
        NamedTextColor color = scoreColor(100.0F - clamped);

        int filled = (int) clamped;
        Component result = Component.text("性能评分：", NamedTextColor.GOLD);
        result = result.append(Component.text("■".repeat(Math.max(0, filled)), color));
        result = result.append(Component.text("■".repeat(Math.max(0, BAR_WIDTH - filled)), NamedTextColor.DARK_GRAY));
        result = result.append(Component.text(" - ", NamedTextColor.GRAY));
        result = result.append(Component.text(ratingName(percentage), color));
        result = result.append(Component.text(" (" + round(percentage) + "%)", NamedTextColor.GRAY));
        return result;
    }

    private static Component blocksLine(Aggregates agg) {
        int count = agg.totalBlocks;
        if (count <= 0) {
            return Component.text()
                    .append(Component.text("方块 | ", NamedTextColor.YELLOW))
                    .append(Component.text("0 blocks", NamedTextColor.GRAY))
                    .build();
        }

        List<Component> hover = new ArrayList<>();
        int shown = 0;
        int hidden = 0;

        for (ProfiledSample sample : agg.blockOrder) {
            long nanos = agg.samples.getOrDefault(sample, 0L);
            if (shown < JEGProfiler.maxItems()
                    && (shown < JEGProfiler.minItems() || nanos > JEGProfiler.visibilityThreshold())) {
                hover.add(Component.text(
                        sample.itemId() + " @ " + sample.positionName() + " (" + sample.worldName() + ") - "
                                + JEGProfiler.asMillis(nanos),
                        NamedTextColor.YELLOW));
                shown++;
            } else {
                hidden++;
            }
        }
        appendHidden(hover, hidden);

        return Component.text()
                .append(Component.text("方块 | ", NamedTextColor.YELLOW))
                .append(Component.text("top " + JEGProfiler.maxItems() + " blocks", NamedTextColor.YELLOW))
                .append(hint(hover))
                .build();
    }

    private static Component aggregateLine(String category, String unit, Map<String, Long> byKey,
            Map<String, Integer> counts) {
        if (byKey.isEmpty()) {
            return Component.text()
                    .append(Component.text(category + " | ", NamedTextColor.YELLOW))
                    .append(Component.text("0 " + unit, NamedTextColor.GRAY))
                    .build();
        }

        List<Map.Entry<String, Long>> order = new ArrayList<>(byKey.entrySet());
        order.sort(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()));

        List<Component> hover = new ArrayList<>();
        int shown = 0;
        int hidden = 0;

        for (Map.Entry<String, Long> entry : order) {
            long nanos = entry.getValue();
            if (shown < JEGProfiler.maxItems()
                    && (shown < JEGProfiler.minItems() || nanos > JEGProfiler.visibilityThreshold())) {
                int count = counts.getOrDefault(entry.getKey(), 0);
                hover.add(Component.text(
                        entry.getKey() + " - " + count + " " + unit + " (" + JEGProfiler.asMillis(nanos) + ")",
                        NamedTextColor.YELLOW));
                shown++;
            } else {
                hidden++;
            }
        }
        appendHidden(hover, hidden);

        return Component.text()
                .append(Component.text(category + " | ", NamedTextColor.YELLOW))
                .append(Component.text(byKey.size() + " " + unit, NamedTextColor.YELLOW))
                .append(hint(hover))
                .build();
    }

    private static void appendHidden(List<Component> hover, int hidden) {
        if (hidden <= 0) {
            return;
        }

        hover.add(Component.empty());
        hover.add(Component.text()
                .append(Component.text("+ ", NamedTextColor.RED))
                .append(Component.text(String.valueOf(hidden), NamedTextColor.GOLD))
                .append(Component.text(" more", NamedTextColor.GOLD))
                .build());
    }

    private static Component hint(List<Component> hover) {
        return Component.text(HOVER_HINT, NamedTextColor.GRAY)
                .hoverEvent(HoverEvent.showText(Component.join(JoinConfiguration.newlines(), hover)));
    }

    private static NamedTextColor scoreColor(float percentage) {
        if (percentage < 16.0F) {
            return NamedTextColor.DARK_RED;
        } else if (percentage < 32.0F) {
            return NamedTextColor.RED;
        } else if (percentage < 48.0F) {
            return NamedTextColor.GOLD;
        } else if (percentage < 64.0F) {
            return NamedTextColor.YELLOW;
        } else if (percentage < 80.0F) {
            return NamedTextColor.DARK_GREEN;
        }
        return NamedTextColor.GREEN;
    }

    private static String ratingName(float percentage) {
        if (percentage <= 10.0F) {
            return "优秀";
        } else if (percentage <= 20.0F) {
            return "良好";
        } else if (percentage <= 30.0F) {
            return "尚可";
        } else if (percentage <= 55.0F) {
            return "一般";
        } else if (percentage <= 85.0F) {
            return "严重";
        } else if (percentage <= 500.0F) {
            return "有害";
        }
        return "糟糕";
    }

    private static String round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * 一次性算好所有聚合视图，避免同一个快照被反复遍历。
     */
    private static final class Aggregates {

        private final Map<ProfiledSample, Long> samples;
        private final int totalBlocks;
        private final List<ProfiledSample> blockOrder;
        private final Map<String, Long> byItem;
        private final Map<String, Integer> itemCounts;
        private final Map<String, Long> byChunk;
        private final Map<String, Integer> chunkCounts;
        private final Map<String, Long> byPlugin;
        private final Map<String, Integer> pluginCounts;

        private Aggregates(Map<ProfiledSample, Long> samples, List<ProfiledSample> blockOrder, Map<String, Long> byItem,
                Map<String, Integer> itemCounts, Map<String, Long> byChunk, Map<String, Integer> chunkCounts,
                Map<String, Long> byPlugin, Map<String, Integer> pluginCounts) {
            this.samples = samples;
            this.totalBlocks = samples.size();
            this.blockOrder = blockOrder;
            this.byItem = byItem;
            this.itemCounts = itemCounts;
            this.byChunk = byChunk;
            this.chunkCounts = chunkCounts;
            this.byPlugin = byPlugin;
            this.pluginCounts = pluginCounts;
        }

        static Aggregates of(TimingsSnapshot snapshot) {
            Map<ProfiledSample, Long> samples = snapshot.samples();
            List<ProfiledSample> blockOrder = new ArrayList<>(samples.keySet());
            blockOrder.sort(Comparator.comparingLong((ProfiledSample s) -> samples.getOrDefault(s, 0L)).reversed());

            Map<String, Long> byItem = new HashMap<>();
            Map<String, Integer> itemCounts = new HashMap<>();
            Map<String, Long> byChunk = new HashMap<>();
            Map<String, Integer> chunkCounts = new HashMap<>();
            Map<String, Long> byPlugin = new HashMap<>();
            Map<String, Integer> pluginCounts = new HashMap<>();

            for (Map.Entry<ProfiledSample, Long> entry : samples.entrySet()) {
                ProfiledSample sample = entry.getKey();
                long nanos = entry.getValue();

                byItem.merge(sample.itemId(), nanos, Long::sum);
                itemCounts.merge(sample.itemId(), 1, Integer::sum);

                byChunk.merge(sample.chunkName(), nanos, Long::sum);
                chunkCounts.merge(sample.chunkName(), 1, Integer::sum);

                String plugin = pluginOf(sample);
                byPlugin.merge(plugin, nanos, Long::sum);
                pluginCounts.merge(plugin, 1, Integer::sum);
            }

            return new Aggregates(samples, blockOrder, byItem, itemCounts, byChunk, chunkCounts, byPlugin,
                    pluginCounts);
        }

        private static String pluginOf(ProfiledSample sample) {
            SlimefunItem item = SlimefunItem.getById(sample.itemId());
            if (item == null) {
                return "Unknown";
            }

            try {
                return item.getAddon().getName();
            } catch (Exception | LinkageError x) {
                // 物品尚未注册 / 附属已卸载
                return "Unknown";
            }
        }
    }
}
