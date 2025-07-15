/*
 * Copyright (c) 2024-2025 balugaq
 *
 * This file is part of JustEnoughGuide, available under MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The author's name (balugaq or 大香蕉) and project name (JustEnoughGuide or JEG) shall not be
 *   removed or altered from any source distribution or documentation.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.balugaq.jeg.api.objects.collection.cooldown;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author balugaq
 * @since 1.7
 */
@Data
public class FrequencyWatcher<Key> {
    private final TimeUnit periodUnit;
    private final long maxFrequencyPerPeriod;
    private final Map<Key, Long> frequencyMap = new ConcurrentHashMap<>();
    private final Map<Key, Long> lastUpdateTime = new ConcurrentHashMap<>();
    private final @NotNull CooldownPool<Key> pool;
    private int periods;

    public FrequencyWatcher(int periods, TimeUnit periodUnit, long maxFrequencyPerPeriod, long cooldownMillis) {
        this.periods = periods;
        this.periodUnit = periodUnit;
        this.maxFrequencyPerPeriod = maxFrequencyPerPeriod;
        this.pool = new CooldownPool<>(cooldownMillis);
    }

    public @NotNull Result checkCooldown(Key key) {
        updateFrequency(key);

        long currentFrequency = frequencyMap.getOrDefault(key, 0L);
        if (currentFrequency >= maxFrequencyPerPeriod) {
            return Result.TOO_FREQUENT;
        }

        frequencyMap.put(key, currentFrequency + 1);
        if (!pool.checkCooldown(key)) {
            return Result.CANCEL;
        }

        return Result.SUCCESS;
    }

    public void updateFrequency(Key key) {
        long now = System.currentTimeMillis();
        if (now - lastUpdateTime.getOrDefault(key, 0L) > periodUnit.toMillis(1)) {
            frequencyMap.clear();
            lastUpdateTime.put(key, now);
        }
    }

    public enum Result {
        TOO_FREQUENT,
        CANCEL,
        SUCCESS
    }
}
