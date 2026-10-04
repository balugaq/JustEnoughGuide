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
import com.balugaq.jeg.libraries.fliptables.FlipTable;
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
import java.util.Arrays;
import java.util.Comparator;
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
    private static final String TITLE = "===== JEG - Slimefun 性能分析器 =====";
    private static final int BAR_WIDTH = 20;

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
            lines.add(blocksLine(snapshot.topBlock()));
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

    /**
     * 「方块」行：单方块耗时榜，点击可传送到最耗时方块。
     * <p>
     * {@code topBlocks} 是小顶堆——只保证「留下最大的 N 个」（淘汰最小），
     * 但 {@code peek()} 拿到的是堆里<b>最小</b>的那个；展示前必须自己排成降序，
     * 排第一的才是真正的「最耗时方块」。
     *
     * @param topBlocks 耗时最高的若干个样本（容量上限 MAX_TOP_ITEMS）
     * @return 组件
     */
    private static Component blocksLine(PriorityQueue<TimedSample> topBlocks) {
        if (topBlocks.isEmpty()) {
            return text()
                .append(text("方块 | ", NamedTextColor.YELLOW))
                .append(text("0 blocks", NamedTextColor.GRAY))
                .build();
        }

        List<TimedSample> sorted = new ArrayList<>(topBlocks);
        sorted.sort(Comparator.comparingLong(TimedSample::nanos).reversed());
        TimedSample topBlock = sorted.getFirst();

        String[][] data = new String[sorted.size()][];
        for (int i = 0; i < sorted.size(); i++) {
            data[i] = new String[]{sorted.get(i).item().getId(), ms(sorted.get(i).nanos())};
        }

        List<Component> hover = renderTable(
                new String[]{"机器 ID", "机器耗时 (ms)"},
                data,
                new NamedTextColor[]{NamedTextColor.YELLOW, NamedTextColor.GREEN});
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

        String[] headers;
        NamedTextColor[] colors;
        if (verbose) {
            headers = new String[]{idHeader, "方块数", totalLabel + "(ms)", "avg", "min", "med", "95%ile", "max"};
            colors = new NamedTextColor[]{
                NamedTextColor.YELLOW, NamedTextColor.GOLD, NamedTextColor.GREEN, NamedTextColor.GRAY,
                NamedTextColor.GRAY, NamedTextColor.GRAY, NamedTextColor.YELLOW, NamedTextColor.RED};
        } else {
            headers = new String[]{idHeader, "方块数", totalLabel + "(ms)", "avg"};
            colors = new NamedTextColor[]{
                NamedTextColor.YELLOW, NamedTextColor.GOLD, NamedTextColor.GREEN, NamedTextColor.GRAY};
        }

        String[][] data = new String[visible.size()][headers.length];
        for (int i = 0; i < visible.size(); i++) {
            ClassifiedGroup group = visible.get(i);
            String[] row = new String[headers.length];
            row[0] = group.key();
            row[1] = String.valueOf(group.count());
            row[2] = ms(group.totalNanos());
            row[3] = ms(group.avgNanos());
            if (verbose) {
                row[4] = ms(group.minNanos());
                row[5] = ms(group.displayMedian());
                row[6] = ms(group.p95Nanos());
                row[7] = ms(group.maxNanos());
            }
            data[i] = row;
        }

        List<Component> hover = renderTable(headers, data, colors);
        Component note = hiddenNote(groups.size(), JEGProfiler.MAX_ITEMS);
        if (note != null) {
            hover.add(note);
        }
        return hover;
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

    /**
     * 用本地魔改版 {@link FlipTable}（无边框、无分隔线的纯对齐文本）渲染 hover 表格，
     * 再按列重新着色。
     * <p>
     * FlipTable 只输出纯文本，而 Adventure 组件需要逐列颜色，
     * 所以布局交给库算，这里按列界把每行拆回单元格重新上色：
     * 列分隔符统一暗灰、表头水色、数据行按列着色（列色由调用方传入）。
     * <p>
     * 宽度适配：FlipTable 按 {@link String#length()} 计宽，而 Minecraft 里
     * 中文/全角字符渲染占两格，见 {@link #mcFit(String)}。
     *
     * @param headers    表头（列数需与每行数据一致）
     * @param data       数据行
     * @param dataColors 数据行各列颜色（长度等于列数）
     * @return 逐行的组件
     */
    private static List<Component> renderTable(String[] headers, String[][] data, NamedTextColor[] dataColors) {
        String[] fittedHeaders = new String[headers.length];
        for (int c = 0; c < headers.length; c++) {
            fittedHeaders[c] = mcFit(headers[c]);
        }

        String[][] fittedData = new String[data.length][];
        for (int r = 0; r < data.length; r++) {
            if (data[r].length != headers.length) {
                throw new IllegalArgumentException(
                        "table row " + r + " has " + data[r].length + " columns, expected " + headers.length);
            }
            fittedData[r] = new String[headers.length];
            for (int c = 0; c < headers.length; c++) {
                fittedData[r][c] = mcFit(data[r][c]);
            }
        }

        // 输出结构：lines[0]=表头，lines[1..]=数据行（无边框、无分隔线）
        String[] lines = FlipTable.of(fittedHeaders, fittedData).split("\n");
        int[] columns = columnDividers(lines[0]);

        List<Component> out = new ArrayList<>(lines.length);
        for (int i = 0; i < lines.length; i++) {
            if (i == 0) {
                out.add(colorizeRow(lines[i], columns, headerColors(headers.length)));
            } else if (data.length == 0) {
                // "(empty)" 占位行与列边界不对齐，直接灰显兜底
                out.add(text(lines[i], NamedTextColor.GRAY));
            } else {
                out.add(colorizeRow(lines[i], columns, dataColors));
            }
        }
        return out;
    }

    /**
     * 从表头行解析各列边界的字符下标：
     * 表头里每个 {@code │} 就是列界，与数据行 {@code │} 的下标一致；
     * 第 0 列无前置边框，从下标 0 开始。
     * <p>
     * 前提：表头单元格自身不含 {@code │}（当前所有表头均为固定文案，满足）。
     *
     * @param headerLine 表头行
     * @return 列界下标（长度 = 列数），第 0 位固定为 0
     */
    private static int[] columnDividers(String headerLine) {
        List<Integer> marks = new ArrayList<>();
        marks.add(0);
        for (int i = 0; i < headerLine.length(); i++) {
            if (headerLine.charAt(i) == '│') {
                marks.add(i);
            }
        }

        int[] result = new int[marks.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = marks.get(i);
        }
        return result;
    }

    /**
     * 把一行按列区间拆开重新上色：列分隔符统一暗灰，单元格用传入的列色。
     * <p>
     * 无边框版式下，第 0 列从行首直接开始；其余列以 {@code │} 起始（暗灰），
     * 最后一列延伸到行尾（含尾随补白）。
     *
     * @param line    表格行
     * @param columns 列界下标（长度 = 列数）
     * @param colors  各列颜色（长度 = 列数）
     * @return 行组件
     */
    private static Component colorizeRow(String line, int[] columns, NamedTextColor[] colors) {
        Component result = text("", NamedTextColor.WHITE);
        for (int c = 0; c < colors.length; c++) {
            int start = columns[c];
            if (c != 0) {
                result = result.append(text(line.substring(start, start + 1), NamedTextColor.DARK_GRAY));
                start++;
            }
            int end = c + 1 < colors.length ? columns[c + 1] : line.length();
            result = result.append(text(line.substring(start, end), colors[c]));
        }
        return result;
    }

    private static NamedTextColor[] headerColors(int columns) {
        NamedTextColor[] colors = new NamedTextColor[columns];
        Arrays.fill(colors, NamedTextColor.AQUA);
        return colors;
    }

    /**
     * FlipTable 按 {@link String#length()} 计宽，而 Minecraft 字体里
     * 中文/全角字符渲染占两格；给含宽字符的文本补足尾随空格，
     * 让库算出的列宽与游戏内的显示宽度一致。
     *
     * @param text 单元格文本
     * @return 宽度对齐后的文本
     */
    private static String mcFit(String text) {
        int extra = displayWidth(text) - text.length();
        return extra <= 0 ? text : text + " ".repeat(extra);
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
        Component hint = text(" (悬停查看详情)", NamedTextColor.GRAY);
        hover.add(text(tip));
        hint = hint.hoverEvent(HoverEvent.showText(Component.join(JoinConfiguration.newlines(), hover)));

        if (target != null && !tip.isEmpty()) {
            hint = hint.clickEvent(ClickEvent.callback(audience -> {
                Player viewer = audience instanceof Player p ? p : null;
                if (viewer != null) {
                    if (viewer.isOp()) {
                        viewer.teleport(target);
                    } else {
                        viewer.sendMessage(text("你没有权限使用此指令!", NamedTextColor.RED));
                    }
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
     * 计算文本在 Minecraft 字体下的显示宽度：中文/全角字符占两格。
     * <p>
     * 目前只被 {@link #mcFit(String)} 使用：FlipTable 内部按
     * {@link String#length()} 计宽，这里负责把差值补齐。
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
