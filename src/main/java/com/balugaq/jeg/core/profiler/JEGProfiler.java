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

import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.services.profiler.SlimefunProfiler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.TickerTask;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * JEG 的性能分析器，替换 Slimefun 自带的 {@link SlimefunProfiler}。
 *
 * <h2>为什么要自己造一个？</h2>
 * <p>
 * Slimefun 自带的 profiler 依赖 {@code newEntry()} / {@code closeEntry()} 这对 API，
 * 而 TickerTask 对<b>同步</b>机器的处理是：先取时间戳，再把任务 {@code runSync} 丢给主线程，
 * 真正执行要等到下一个 MC tick。于是 {@code /sf timings} 测出的耗时 = 排队等待 + 实际执行，
 * 误差可达 50ms 起步；异步机器则共用同一条 ticker 线程，队头机器的耗时会被算进队尾机器的账上。
 *
 * <h2>本类怎么做？</h2>
 * <p>
 * 计时口径改为 SlimefunTimeit 的做法：在 {@link TimedBlockTicker} 里于 {@code tick()} 调用
 * <b>紧邻前后</b>打点，测的是纯粹的机器执行时间，不含调度延迟。
 * 同时覆写父类的 {@link #start()} / {@link #stop()}，用它们感知「一轮 tick 开始 / 结束」，
 * 从而在轮次收尾时把结果推给 {@code /jeg timings} 的等待者。
 *
 * @author balugaq
 * @since 2.2
 */
@NullMarked
public class JEGProfiler extends SlimefunProfiler {

    /**
     * 一个 Minecraft tick 是 50ms，而 Slimefun 的 ticker 会跨两个 tick 执行（同步 + 异步），
     * 这里沿用 Slimefun 的 100ms 作为「一个 tick 周期」的参考值。
     */
    private static final int MAX_TICK_DURATION = 100;

    /**
     * 低于该纳秒数的条目在 hover 里视为噪声，但前 {@link #MIN_ITEMS} 条豁免。
     */
    private static final int VISIBILITY_THRESHOLD = 260_000;

    /**
     * 无论耗时多少都至少展示这么多条，避免玩家误以为机器没跑。
     */
    private static final int MIN_ITEMS = 6;

    /**
     * hover 里最多展示的条目数。
     */
    private static final int MAX_ITEMS = 20;

    private static volatile @Nullable JEGProfiler instance;

    /**
     * 本轮 tick 内每个方块累计的耗时（纳秒）。
     */
    private final Map<ProfiledSample, Long> samples = new ConcurrentHashMap<>();

    /**
     * 本轮 tick 内所有方块的耗时总和（纳秒）。
     */
    private final AtomicLong totalNanos = new AtomicLong();

    /**
     * 等待本轮结果的攻击者。
     */
    private final Map<UUID, Player> waiting = new ConcurrentHashMap<>();

    /**
     * 标记当前是否已排过一次推送任务，避免同一轮内重复排程。
     */
    private final AtomicBoolean reportScheduled = new AtomicBoolean(false);

    private JEGProfiler() {
        super();
    }

    /**
     * 创建并安装一个 {@link JEGProfiler}，替换掉 Slimefun 实例上的原生 profiler。
     * <p>
     * Slimefun 的 {@code profiler} 字段是 {@code private final}，只能靠反射强写；
     * 这里用 {@link ReflectionUtil#setValue(Object, String, Object)} 而非裸反射，
     * 与仓库内其它 patch 保持同一套约定。
     * <p>
     * 该字段是<b>构造期赋值</b>的实例 final 字段（不是编译期常量），因此反射写入有效；
     * 但 JVM 正在推动封禁 final 字段反射写入，所以写入后必须回读校验，
     * 失败则放弃安装并让 {@code /jeg timings} 退回 Slimefun 原生实现。
     *
     * @return 新安装的 profiler；Slimefun 尚未就绪或替换失败时返回 {@code null}
     */
    public static @Nullable JEGProfiler install() {
        Slimefun plugin = Slimefun.instance();
        if (plugin == null) {
            return null;
        }

        JEGProfiler profiler = new JEGProfiler();

        if (!ReflectionUtil.setValue(plugin, "profiler", profiler)) {
            JustEnoughGuide.getInstance().getLogger()
                    .warning("[JEG] 无法替换 Slimefun 的 Profiler，/jeg timings 将退回 Slimefun 原生实现");
            return null;
        }

        // 回读校验：final 字段在部分 JVM 上会拒绝写入或被常量折叠，不能只信 setValue 的返回值
        Object actual = ReflectionUtil.getValue(plugin, "profiler");
        if (actual != profiler) {
            JustEnoughGuide.getInstance().getLogger()
                    .warning("[JEG] Profiler 替换未被生效（JVM 拒绝 final 字段写入），/jeg timings 将退回 Slimefun 原生实现");
            return null;
        }

        instance = profiler;
        return profiler;
    }

    /**
     * 取出当前生效的 {@link JEGProfiler}。
     *
     * @return 当前 profiler；未安装时返回 {@code null}
     */
    public static @Nullable JEGProfiler getInstance() {
        return instance;
    }

    /**
     * 记录一次方块耗时。调用方为 {@link TimedBlockTicker}，在 {@code tick()} 前后打点。
     *
     * @param location 方块位置
     * @param item     对应的物品
     * @param nanos    本次执行的纳秒数
     */
    public void record(@Nullable Location location, @Nullable SlimefunItem item, long nanos) {
        if (location == null || item == null || nanos <= 0) {
            return;
        }

        World world = location.getWorld();
        if (world == null) {
            return;
        }

        samples.merge(new ProfiledSample(location, item.getId()), nanos, Long::sum);
        totalNanos.addAndGet(nanos);
    }

    /**
     * 玩家请求一份 timings 报告。数据会在下一轮 tick 收尾时推送。
     *
     * @param player 请求者
     */
    public void requestReport(Player player) {
        waiting.put(player.getUniqueId(), player);

        /*
         * 正常路径是等 stop() 推送；但若 Ticker 此刻处于 halted / paused，stop() 不会再来，
         * 玩家就会一直干等。这里补一个兜底：按 Ticker 周期再多等一会儿，确保新一轮已经收尾。
         */
        if (reportScheduled.compareAndSet(false, true)) {
            JustEnoughGuide.runLaterAsync(this::flushFallback, fallbackDelay());
        }
    }

    /**
     * 兜底推送：Ticker 停摆导致 {@link #stop()} 未被调用时使用。
     */
    private void flushFallback() {
        try {
            if (waiting.isEmpty()) {
                return;
            }

            List<Player> targets = new ArrayList<>(waiting.values());
            waiting.clear();
            dispatch(snapshot(), targets);
        } catch (Exception | LinkageError x) {
            JustEnoughGuide.getInstance().getLogger().warning("[JEG] timings 兜底推送失败: " + x);
        } finally {
            reportScheduled.set(false);
        }
    }

    /**
     * 兜底推送的延迟（tick 数）。
     * <p>
     * 取 Ticker 周期的两倍再加余量，保证即便请求恰好落在某轮收尾之后，
     * 下一轮也已经跑完并触发了 {@link #stop()}。
     *
     * @return 延迟 tick 数
     */
    private static long fallbackDelay() {
        return Math.max(2L, getTickerPeriod() * 2L + 1L);
    }

    /**
     * 一轮 tick 开始。清理上一轮的采样数据，让本轮从零开始统计。
     */
    @Override
    public void start() {
        super.start();
        samples.clear();
        totalNanos.set(0L);
    }

    /**
     * 一轮 tick 结束。把本轮结果推给所有等待中的请求者。
     * <p>
     * 注意这里必须<b>同步</b>冻结快照：{@code stop()} 跑在 Ticker 的异步线程上，
     * 若把取样推迟到下一个异步任务，下一轮的 {@link #start()} 可能已经把 {@code samples} 清空，
     * 玩家会拿到一份空报告。
     */
    @Override
    public void stop() {
        super.stop();

        if (waiting.isEmpty()) {
            return;
        }

        TimingsSnapshot snapshot = snapshot();
        List<Player> targets = new ArrayList<>(waiting.values());
        waiting.clear();
        reportScheduled.set(false);

        JustEnoughGuide.runLaterAsync(() -> dispatch(snapshot, targets), 1L);
    }

    /**
     * 读取 TickerTask 的 tickFreeze 字段。
     * <p>
     * 该字段并非所有 Slimefun 版本都有（当前 2025.1.2 就<b>没有</b>），
     * 因此必须走反射并且容错，取不到时按 {@code false} 处理。
     *
     * @return 当前是否处于 tick freeze 状态
     */
    public static boolean isTickFreeze() {
        try {
            TickerTask ticker = Slimefun.getTickerTask();
            if (ticker == null) {
                return false;
            }

            Boolean frozen = ReflectionUtil.getValue(ticker, "tickFreeze", Boolean.class);
            return frozen != null && frozen;
        } catch (Exception | LinkageError x) {
            return false;
        }
    }

    /**
     * 取出 Ticker 的运行周期（tick 数）。
     *
     * @return 周期 tick 数；取不到时返回 0
     */
    public static int getTickerPeriod() {
        try {
            TickerTask ticker = Slimefun.getTickerTask();
            return ticker == null ? 0 : ticker.getTickRate();
        } catch (Exception | LinkageError x) {
            return 0;
        }
    }

    private void dispatch(TimingsSnapshot snapshot, List<Player> targets) {
        try {
            if (targets.isEmpty()) {
                return;
            }

            ComponentReport report = ComponentReport.of(snapshot);

            for (Player player : targets) {
                if (!player.isOnline()) {
                    continue;
                }

                for (Component component : report.components()) {
                    player.sendMessage(component);
                }
            }
        } catch (Exception | LinkageError x) {
            JustEnoughGuide.getInstance().getLogger().warning("[JEG] 生成 timings 报告时发生异常: " + x);
        }
    }

    private TimingsSnapshot snapshot() {
        return new TimingsSnapshot(totalNanos.get(), samples.size(), getTickerPeriod(), isTickFreeze(),
                Map.copyOf(samples));
    }

    /**
     * 计算耗时占一个 tick 周期的百分比。
     *
     * @param totalNanos 总耗时（纳秒）
     * @return 百分比
     */
    public static float percentageOfTick(long totalNanos) {
        float millis = totalNanos / 1000000.0F;
        float fraction = (millis * 100.0F) / MAX_TICK_DURATION;
        return Math.round((fraction * 100.0F) / 100.0F);
    }

    /**
     * 把纳秒格式化成人类可读的耗时字符串。
     *
     * @param nanos 纳秒
     * @return 格式化结果
     */
    public static String asMillis(long nanos) {
        return NumberUtils.getAsMillis(nanos);
    }

    /**
     * hover 内容最多展示多少条。
     *
     * @return 上限
     */
    public static int maxItems() {
        return MAX_ITEMS;
    }

    /**
     * hover 内容至少展示多少条。
     *
     * @return 下限
     */
    public static int minItems() {
        return MIN_ITEMS;
    }

    /**
     * hover 内容的噪声阈值。
     *
     * @return 纳秒
     */
    public static int visibilityThreshold() {
        return VISIBILITY_THRESHOLD;
    }

    /**
     * 一轮 tick 的采样结果快照。
     *
     * @param totalNanos 本轮总耗时（纳秒）
     * @param blocks     本轮参与计时的方块数
     * @param period     Ticker 运行周期（tick 数）
     * @param frozen     本轮收尾时是否处于 tick freeze
     * @param samples    方块 -> 耗时（纳秒）
     */
    public record TimingsSnapshot(long totalNanos, int blocks, int period, boolean frozen,
            Map<ProfiledSample, Long> samples) {
    }
}
