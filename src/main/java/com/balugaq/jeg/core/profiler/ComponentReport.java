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

import com.balugaq.jeg.core.profiler.TimingsAggregator.Aggregates;
import com.balugaq.jeg.core.profiler.TimingsAggregator.ClassifiedGroup;
import io.github.thebusybiscuit.slimefun4.core.services.profiler.PerformanceRating;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import static net.kyori.adventure.text.Component.text;

/**
 * 把 {@link TimingsSnapshot} 渲染成 Paper Adventure 的消息组件。
 *
 * <h2>版式</h2>
 * <pre>
 * ===== JEG 性能监视器 =====
 * Tick 总用时：12.4ms
 * Ticker 运行周期：0.5s (10 ticks)
 * Tick Freeze: ❌
 * 性能评分: ::::::::::::------- - Good (12.4%)
 * 方块 | top 20 blocks (悬停查看详情)
 * 机器 | 128 blocks (悬停查看详情)
 * 区块 | 23 chunks (悬停查看详情)
 * 插件 | 8 plugins (悬停查看详情)
 * </pre>
 *
 * <h2>两种 hover 版式</h2>
 * <p>
 * 默认只给总量视图（{@code ID | 方块数 | 总耗时 | avg}）；
 * {@code --verbose} 下追加 {@code min | med | 95%ile | max} 分位列。
 * 所有 hover 都用等宽表格呈现，便于横向对比。
 *
 * @author balugaq
 * @since 2.2
 */
@SuppressWarnings("deprecation")
@NullMarked
public final class ComponentReport {
    public static final String[] BLOCK_LABELS = new String[]{"机器 ID", "方块数", "机器耗时 (ms)"};
    private static final String TITLE = "===== JEG - Slimefun 性能分析器 =====";
    private static final int BAR_WIDTH = 20;

    /**
     * 表格数字列宽（Minecraft 默认字体下英文与数字近似等宽，中文占两格）。
     * 首列（ID / 位置 / 插件名）宽度自适应，见 {@link #idWidthOf}。
     */
    private static final int W_COUNT = 6;
    private static final int W_TOTAL = 9;
    private static final int W_STAT = 8;

    private final List<Component> components;

    private ComponentReport(List<Component> components) {
        this.components = components;
    }

    public List<Component> components() {
        return components;
    }

    private static Component label(String key, String value) {
        return text()
                .append(text(key, NamedTextColor.GOLD))
                .append(text(value, NamedTextColor.YELLOW))
                .build();
    }

    private static String periodText(int period) {
        return round(period / 20.0) + "s (" + period + " ticks)";
    }

    private static Component freezeLine(boolean frozen) {
        return text()
                .append(text("Tick Freeze: ", NamedTextColor.GOLD))
                .append(text(frozen ? "√" : "❌", frozen ? NamedTextColor.RED : NamedTextColor.GREEN))
                .build();
    }

    /**
     * 性能评分行
     */
    private static Component ratingLine(long totalNanos) {
        float percentage = JEGProfiler.percentageOfTick(totalNanos);
        float clamped = Math.min(percentage, 100.0F);
        PerformanceRating rating = JEGProfiler.performanceOf(totalNanos);

        int rest = BAR_WIDTH;
        StringBuilder bar = new StringBuilder();
        for (int i = (int) clamped; i >= 5; i -= 5) {
            bar.append(':');
            rest--;
        }

        Component result = text("性能评分: ", NamedTextColor.GOLD);
        result = result.append(text(bar.toString(),
                colorOf(NumberUtils.getColorFromPercentage(100.0F - clamped))));
        result = result.append(text(":".repeat(Math.max(0, rest)), NamedTextColor.DARK_GRAY));
        result = result.append(text(" - ", NamedTextColor.DARK_GRAY));
        result = result.append(text(JEGProfiler.ratingName(rating), colorOf(rating.getColor())));
        result = result.append(text(" (" + NumberUtils.roundDecimalNumber(percentage) + "%)",
                NamedTextColor.GRAY));
        return result;
    }

    /**
     * 依据快照构建报告。
     *
     * @param snapshot 采样快照
     * @param verbose  是否输出详细分位统计
     * @return 渲染好的报告
     */
    public static ComponentReport of(TimingsSnapshot snapshot, boolean verbose) {
        Aggregates agg = TimingsAggregator.aggregate(snapshot);

        List<Component> lines = new ArrayList<>();
        lines.add(text(TITLE, NamedTextColor.GREEN));
        lines.add(label("Tick 总用时：", JEGProfiler.asMillis(snapshot.roundTotalNanos())));
        lines.add(label("Ticker 运行周期：", periodText(snapshot.period())));
        lines.add(freezeLine(snapshot.frozen()));
        lines.add(ratingLine(snapshot.roundTotalNanos()));
        if (!snapshot.topBlock().isEmpty()) {
            lines.add(blocksLine(snapshot.topBlock(), verbose));
        }
        lines.add(machinesLine(agg.byItem(), verbose, snapshot.totalBlocks()));

        ChunkKey hotChunk = agg.byChunk().isEmpty() ? null : agg.byChunk().getFirst().chunk();
        lines.add(chunksLine(agg.byChunk(), verbose, hotChunk));
        lines.add(pluginsLine(agg.byPlugin(), verbose));
        return new ComponentReport(List.copyOf(lines));
    }
    
    private static Component machinesLine(List<ClassifiedGroup> groups, boolean verbose, int totalBlocks) {
        if (groups.isEmpty()) {
            return text()
                .append(text("机器 | ", NamedTextColor.YELLOW))
                .append(text("0 blocks", NamedTextColor.GRAY))
                .build();
        }

        List<Component> hover = buildHover("机器 ID", "机器总耗时", groups, verbose);
        return text()
            .append(text("机器 | ", NamedTextColor.YELLOW))
            .append(text(plural(totalBlocks, "block"), NamedTextColor.YELLOW))
            .append(hint(hover))
            .build();
    }

    private static Component chunksLine(List<ClassifiedGroup> groups, boolean verbose, @Nullable ChunkKey topChunk) {
        if (topChunk == null) {
            return text()
                .append(text("区块 | ", NamedTextColor.YELLOW))
                .append(text("0 chunks", NamedTextColor.GRAY))
                .build();
        }
        
        List<Component> hover = buildHover("区块位置", "区块总耗时", groups, verbose);
        hover.add(teleportNote("最耗时区块", topChunk.displayName() + " 中心"));

        return text()
            .append(text("区块 | ", NamedTextColor.YELLOW))
            .append(text(plural(groups.size(), "chunk"), NamedTextColor.YELLOW))
            .append(hint(hover, chunkCenterOf(topChunk), "点击传送到最耗时区块"))
            .build();
    }

    private static Component pluginsLine(List<ClassifiedGroup> groups, boolean verbose) {
        if (groups.isEmpty()) {
            return text()
                .append(text("插件 | ", NamedTextColor.YELLOW))
                .append(text("0 plugins", NamedTextColor.GRAY))
                .build();
        }

        List<Component> hover = buildHover("插件 ID", "插件总耗时", groups, verbose);

        return text()
            .append(text("插件 | ", NamedTextColor.YELLOW))
            .append(text(plural(groups.size(), "plugin"), NamedTextColor.YELLOW))
            .append(hint(hover))
            .build();
    }

    private static Component blocksLine(PriorityQueue<TimedSample> topBlocks, boolean verbose) {
        if (topBlocks.isEmpty()) {
            return text()
                .append(text("方块 | ", NamedTextColor.YELLOW))
                .append(text("0 blocks", NamedTextColor.GRAY))
                .build();
        }

        int idWidth = idWidthOf("机器 ID", topBlocks.stream().map(t -> t.item().getItemName()).toList());
        List<Component> hover = new ArrayList<>();
        hover.add(blockTableHeader(idWidth));
        TimedSample topBlock = topBlocks.peek();
        while (!topBlocks.isEmpty()) {
            hover.add(row(topBlocks.poll(), idWidth));
        }

        hover.add(teleportNote("最耗时方块", topBlock.worldName() + " " + topBlock.positionName()));

        return text()
            .append(text("方块 | ", NamedTextColor.YELLOW))
            .append(text("top " + JEGProfiler.MAX_TOP_ITEMS + " blocks", NamedTextColor.YELLOW))
            .append(hint(hover, locationOf(topBlock), "点击传送到最耗时机器"))
            .build();
    }

    /**
     * hover 里实际展示的行（最多 maxItems 条）。
     *
     * @param groups 聚合结果
     * @return 可见行
     */
    private static List<ClassifiedGroup> visibleGroups(List<ClassifiedGroup> groups) {
        int max = JEGProfiler.MAX_ITEMS;
        return groups.size() <= max ? groups : groups.subList(0, max);
    }

    private static List<Component> buildHover(
        String idHeader,
        String totalLabel,
        List<ClassifiedGroup> groups,
        boolean verbose
    ) {
        List<ClassifiedGroup> visible = visibleGroups(groups);
        int idWidth = idWidthOf(idHeader, visible.stream().map(ClassifiedGroup::key).toList());

        List<Component> hover = new ArrayList<>();
        hover.add(tableHeader(idHeader, totalLabel, verbose, idWidth));
        for (ClassifiedGroup group : visible) {
            hover.add(row(group, verbose, idWidth));
        }

        Component note = hiddenNote(groups.size(), JEGProfiler.MAX_ITEMS);
        if (note != null) {
            hover.add(note);
        }
        return hover;
    }

    /**
     * 首列宽度自适应：机器 ID / 区块位置 / 插件名长度都不固定，
     * 取表头与所有可见行里最宽的显示宽度，再留两格余量。
     *
     * @param header 首列表头
     * @param ids      显示名
     * @return 首列显示宽度
     */
    private static int idWidthOf(String header, List<String> ids) {
        int width = displayWidth(header);
        for (var displayName : ids) {
            width = Math.max(width, displayWidth(displayName));
        }
        return width + 2;
    }

    private static @Nullable Component hiddenNote(int total, int max) {
        int hidden = total - max;
        if (hidden <= 0) return null;

        return text()
                .append(text("+ ", NamedTextColor.RED))
                .append(text(String.valueOf(hidden), NamedTextColor.GOLD))
                .append(text(" more", NamedTextColor.GOLD))
                .build();
    }

    private static Component blockTableHeader(int idWidth) {
        String[] labels = BLOCK_LABELS;
        int[] widths = {idWidth, W_COUNT, W_TOTAL, W_STAT, W_STAT, W_STAT, W_STAT, W_STAT};

        Component result = text("", NamedTextColor.WHITE);
        for (int i = 0; i < labels.length; i++) {
            result = result.append(text(pad(labels[i], widths[i]), NamedTextColor.AQUA));
            if (i < labels.length - 1) {
                result = result.append(text(" ", NamedTextColor.DARK_GRAY));
            }
        }
        return result;
    }

    private static Component tableHeader(String idLabel, String totalLabel, boolean verbose, int idWidth) {
        String[] labels = verbose
                ? new String[]{idLabel, "方块数", totalLabel + "(ms)", "avg", "min", "med", "95%ile", "max"}
                : new String[]{idLabel, "方块数", totalLabel + "(ms)", "avg"};
        int[] widths = {idWidth, W_COUNT, W_TOTAL, W_STAT, W_STAT, W_STAT, W_STAT, W_STAT};

        Component result = text("", NamedTextColor.WHITE);
        for (int i = 0; i < labels.length; i++) {
            result = result.append(text(pad(labels[i], widths[i]), NamedTextColor.AQUA));
            if (i < labels.length - 1) {
                result = result.append(text(" ", NamedTextColor.DARK_GRAY));
            }
        }
        return result;
    }

    private static Component row(TimedSample sample, int idWidth) {
        List<Component> cells = new ArrayList<>();
        cells.add(text(pad(sample.item().getId(), idWidth), NamedTextColor.YELLOW));
        cells.add(text(pad(ms(sample.nanos()), W_TOTAL), NamedTextColor.GREEN));

        Component result = text("", NamedTextColor.WHITE);
        for (int i = 0; i < cells.size(); i++) {
            result = result.append(cells.get(i));
            if (i < cells.size() - 1) {
                result = result.append(text(" ", NamedTextColor.DARK_GRAY));
            }
        }
        return result;
    }

    private static Component row(ClassifiedGroup group, boolean verbose, int idWidth) {
        List<Component> cells = new ArrayList<>();
        cells.add(text(pad(group.key(), idWidth), NamedTextColor.YELLOW));
        cells.add(text(pad(String.valueOf(group.count()), W_COUNT), NamedTextColor.GOLD));
        cells.add(text(pad(ms(group.totalNanos()), W_TOTAL), NamedTextColor.GREEN));

        cells.add(text(pad(ms(group.avgNanos()), W_STAT), NamedTextColor.GRAY));
        if (verbose) {
            cells.add(text(pad(ms(group.minNanos()), W_STAT), NamedTextColor.GRAY));
            cells.add(text(pad(ms(group.displayMedian()), W_STAT), NamedTextColor.GRAY));
            cells.add(text(pad(ms(group.p95Nanos()), W_STAT), NamedTextColor.YELLOW));
            cells.add(text(pad(ms(group.maxNanos()), W_STAT), NamedTextColor.RED));
        }

        Component result = text("", NamedTextColor.WHITE);
        for (int i = 0; i < cells.size(); i++) {
            result = result.append(cells.get(i));
            if (i < cells.size() - 1) {
                result = result.append(text(" ", NamedTextColor.DARK_GRAY));
            }
        }
        return result;
    }

    private static Component hint(List<Component> hover) {
        return hint(hover, null, "");
    }

    /**
     * 构造「查看详情」提示，带 hover 与可选的点击传送。
     *
     * @param hover  hover 内容
     * @param target 传送目标；为 null 时不带点击事件
     * @param tip    点击提示文本，可为空串
     * @return 提示组件
     */
    private static Component hint(List<Component> hover, @Nullable Location target, String tip) {
        Component hint = text(" (悬停查看详情", NamedTextColor.GRAY);
        hover.add(text(tip));
        hint = hint.append(text(")", NamedTextColor.GRAY));
        hint = hint.hoverEvent(HoverEvent.showText(Component.join(JoinConfiguration.newlines(), hover)));

        if (target != null && !tip.isEmpty()) {
            hint = hint.clickEvent(ClickEvent.callback(audience -> {
                Player viewer = audience instanceof Player p ? p : null;
                if (viewer != null) {
                    viewer.teleport(target);
                }
            }));
        }
        return hint;
    }
    
    private static @Nullable Location locationOf(@Nullable TimedSample peak) {
        if (peak == null) return null;

        World world = Bukkit.getWorld(peak.worldName());
        return world == null ? null : new Location(world, peak.x(), peak.y(), peak.z());
    }
    
    private static Location chunkCenterOf(ChunkKey key) {
        World world = Bukkit.getWorld(key.worldName());
        return new Location(world, key.chunkX() * 16 + 8, 128, key.chunkZ() * 16 + 8);
    }

    private static Component teleportNote(String label, String value) {
        return text()
                .append(text("点击传送: ", NamedTextColor.DARK_GRAY))
                .append(text(label + " ", NamedTextColor.GRAY))
                .append(text(value, NamedTextColor.AQUA))
                .build();
    }

    /**
     * 处理单复数：{@code 1 block} / {@code 2 blocks}。
     */
    private static String plural(int count, String unit) {
        return count + " " + unit + (count == 1 ? "" : "s");
    }

    private static String ms(long nanos) {
        return NumberUtils.roundDecimalNumber(nanos / 1000000.0D);
    }

    /**
     * 按显示宽度右侧补空格（表格左对齐）。
     *
     * @param text  文本
     * @param width 目标显示宽度
     * @return 补齐后的文本
     */
    private static String pad(String text, int width) {
        return text + " ".repeat(Math.max(0, width - displayWidth(text)));
    }

    /**
     * 计算文本在 Minecraft 字体下的显示宽度：中文/全角字符占两格。
     *
     * @param text 文本
     * @return 显示宽度
     */
    private static int displayWidth(String text) {
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            width += isWide(text.charAt(i)) ? 2 : 1;
        }
        return width;
    }

    private static boolean isWide(char c) {
        return c >= 0x1100 && (c <= 0x115F
                || c == 0x2329 || c == 0x232A
                || (c >= 0x2E80 && c <= 0xA4CF && c != 0x303F)
                || (c >= 0xAC00 && c <= 0xD7A3)
                || (c >= 0xF900 && c <= 0xFAFF)
                || (c >= 0xFE30 && c <= 0xFE6F)
                || (c >= 0xFF00 && c <= 0xFF60)
                || (c >= 0xFFE0 && c <= 0xFFE6));
    }

    /**
     * 把 Bukkit 的 {@link ChatColor} 转成 Adventure 的 {@link NamedTextColor}。
     * <p>
     * 两边枚举的常量名（{@code DARK_RED}、{@code GOLD} …）完全一致，因此按名字映射即可。
     * 不走 {@code ChatColor#getColorValue()}——该方法在当前编译 classpath 上并不存在。
     *
     * @param color Bukkit 颜色
     * @return Adventure 颜色
     */
    private static NamedTextColor colorOf(ChatColor color) {
        NamedTextColor mapped = NamedTextColor.NAMES.value(color.name());
        return mapped == null ? NamedTextColor.WHITE : mapped;
    }

    private static String round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }
}
