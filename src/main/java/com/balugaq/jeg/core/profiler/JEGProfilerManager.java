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
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * {@link JEGProfiler} 的安装 / 卸载入口。
 *
 * <h2>为什么要包装 BlockTicker？</h2>
 * <p>
 * Slimefun 没有对外暴露任何 profiler 接口，{@code TickerTask} 内部直接调
 * {@code newEntry()} / {@code closeEntry()}，我们既改不了它的打点位置，也拿不到 tick 的真实耗时。
 * 唯一的插手点是 {@code SlimefunItem} 上的 {@code blockTicker} 字段——
 * 把它换成 {@link TimedBlockTicker} 代理，就能在 {@code tick()} 前后拿到精确的耗时。
 * 这个手法与 SlimefunTimeit 一致。
 *
 * <h2>时序</h2>
 * <p>
 * 物品注册发生在 Slimefun 的 {@code PostSetup} 阶段，早于 JEG 的 {@code onEnable}，
 * 因此安装必须<b>异步延后</b>执行，等所有附属（含 JEG 自身）的物品都注册完再遍历包装。
 *
 * @author balugaq
 * @since 2.2
 */
@NullMarked
public final class JEGProfilerManager {

    private static volatile boolean installed = false;

    private JEGProfilerManager() {
    }

    /**
     * 安装 profiler 并包装所有已注册的 BlockTicker。需在物品注册完成后调用。
     */
    public static void install() {
        if (installed) {
            return;
        }

        if (JEGProfiler.install() == null) {
            return;
        }

        int wrapped = 0;
        for (SlimefunItem item : Slimefun.getRegistry().getAllSlimefunItems()) {
            if (wrap(item)) {
                wrapped++;
            }
        }

        installed = true;
        JustEnoughGuide.getInstance().getLogger()
                .info("[JEG] 性能监视器已启用，已包装 " + wrapped + " 个机器计时器");
    }

    /**
     * 还原所有被包装的 BlockTicker，并卸载 profiler。
     */
    public static void uninstall() {
        if (!installed) {
            return;
        }

        int restored = 0;
        for (SlimefunItem item : Slimefun.getRegistry().getAllSlimefunItems()) {
            if (unwrap(item)) {
                restored++;
            }
        }

        installed = false;
        JustEnoughGuide.getInstance().getLogger()
                .info("[JEG] 性能监视器已关闭，已还原 " + restored + " 个机器计时器");
    }

    private static boolean wrap(SlimefunItem item) {
        BlockTicker ticker = item.getBlockTicker();
        if (ticker == null || ticker instanceof TimedBlockTicker) {
            return false;
        }

        return ReflectionUtil.setValue(item, "blockTicker", TimedBlockTicker.wrap(ticker));
    }

    private static boolean unwrap(SlimefunItem item) {
        if (!(item.getBlockTicker() instanceof TimedBlockTicker timed)) {
            return false;
        }

        return ReflectionUtil.setValue(item, "blockTicker", timed.originTicker());
    }

    /**
     * 当前性能监视器是否处于安装状态。
     *
     * @return 是否已安装
     */
    public static boolean isInstalled() {
        return installed;
    }

    /**
     * 取得当前的 {@link JEGProfiler}，未安装时返回 {@code null}。
     *
     * @return 当前 profiler
     */
    public static @Nullable JEGProfiler profiler() {
        return JEGProfiler.getInstance();
    }

    /**
     * 供命令层判断能否使用精确计时。
     *
     * @return Slimefun 是否可用
     */
    public static boolean isAvailable() {
        Plugin plugin = JustEnoughGuide.getInstance();
        return plugin != null && plugin.isEnabled();
    }
}
