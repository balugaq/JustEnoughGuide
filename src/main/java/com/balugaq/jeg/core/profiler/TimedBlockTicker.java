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

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalData;
import io.github.thebusybiscuit.slimefun4.api.exceptions.IncompatibleItemHandlerException;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import org.bukkit.block.Block;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * 包裹原始 {@link BlockTicker} 的计时代理，思路取自 SlimefunTimeit。
 * <p>
 * 关键点在于打点位置：Slimefun 自带 profiler 的时间戳在任务<b>入队前</b>就取好了，
 * 对同步机器而言测出来的是「排队 + 执行」；而这里在 {@code tick()} 调用
 * <b>紧邻前后</b>取 {@link System#nanoTime()}，把调度延迟完全排除在外。
 * <p>
 * 该代理只做计时与转发，不改变任何机器的行为语义；
 * {@link #tick(Block, SlimefunItem, SlimefunBlockData)} 等方法全部原样委托给被包裹的 ticker。
 *
 * @author balugaq
 * @since 2.2
 */
@NullMarked
public class TimedBlockTicker extends BlockTicker {

    private final BlockTicker ticker;

    private TimedBlockTicker(BlockTicker ticker) {
        // universal 标记是 final 的，必须透传，否则 TickerTask 会走错 tick 分支
        super(ticker.isUniversal());
        this.ticker = ticker;
    }

    /**
     * 把一个 {@link BlockTicker} 包成计时代理。已经是代理的则原样返回，避免重复包裹。
     *
     * @param ticker 原始 ticker
     * @return 计时代理
     */
    public static TimedBlockTicker wrap(BlockTicker ticker) {
        if (ticker instanceof TimedBlockTicker timed) {
            return timed;
        }
        return new TimedBlockTicker(ticker);
    }

    /**
     * 取回被包裹的原始 ticker。
     *
     * @return 原始 ticker
     */
    public BlockTicker originTicker() {
        return ticker;
    }

    @Override
    public void update() {
        ticker.update();
    }

    @Override
    public Optional<IncompatibleItemHandlerException> validate(SlimefunItem item) {
        return ticker.validate(item);
    }

    @Override
    public boolean isSynchronized() {
        return ticker.isSynchronized();
    }

    @Override
    public void uniqueTick() {
        ticker.uniqueTick();
    }

    @Override
    public void startNewTick() {
        // update() 已被完全覆盖，本代理自身的 unique 字段永远不会被读到，
        // 因此只需把标志转回被包裹的 ticker，否则它的 uniqueTick() 再也不会触发
        ticker.startNewTick();
    }

    @Override
    public void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
        long start = System.nanoTime();
        try {
            ticker.tick(b, item, data);
        } finally {
            JEGProfiler profiler = JEGProfiler.getInstance();
            if (profiler != null) {
                profiler.record(b.getLocation(), item, System.nanoTime() - start);
            }
        }
    }

    @Override
    public void tick(Block b, SlimefunItem item, SlimefunUniversalData data) {
        long start = System.nanoTime();
        try {
            ticker.tick(b, item, data);
        } finally {
            JEGProfiler profiler = JEGProfiler.getInstance();
            if (profiler != null) {
                profiler.record(b.getLocation(), item, System.nanoTime() - start);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(Block b, SlimefunItem item, Config data) {
        long start = System.nanoTime();
        try {
            ticker.tick(b, item, data);
        } finally {
            JEGProfiler profiler = JEGProfiler.getInstance();
            if (profiler != null) {
                profiler.record(b.getLocation(), item, System.nanoTime() - start);
            }
        }
    }

    @Override
    public Class<? extends ItemHandler> getIdentifier() {
        return ticker.getIdentifier();
    }
}
