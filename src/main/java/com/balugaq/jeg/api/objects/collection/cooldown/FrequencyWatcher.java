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

package com.balugaq.jeg.api.objects.collection.cooldown;

import lombok.Data;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author balugaq
 * @since 1.7
 */
@Data
@NullMarked
public class FrequencyWatcher<Key> {
    private final TimeUnit periodUnit;
    private final long maxFrequencyPerPeriod;
    private final Map<Key, Long> frequencyMap = new ConcurrentHashMap<>();
    private final Map<Key, Long> lastUpdateTime = new ConcurrentHashMap<>();
    private final CooldownPool<Key> pool;
    private int periods;

    public FrequencyWatcher(int periods, TimeUnit periodUnit, long maxFrequencyPerPeriod, long cooldownMillis) {
        this.periods = periods;
        this.periodUnit = periodUnit;
        this.maxFrequencyPerPeriod = maxFrequencyPerPeriod;
        this.pool = new CooldownPool<>(cooldownMillis);
    }

    public Result checkCooldown(Key key) {
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
