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

/**
 * @author balugaq
 * @since 1.7
 */
@Data
@NullMarked
public class CooldownPool<Key> {
    private final Map<Key, Long> pool = new ConcurrentHashMap<>();
    private final long cooldownMillis;

    public CooldownPool(long cooldownMillis) {
        this.cooldownMillis = cooldownMillis;
    }

    /**
     * Check cooldown
     *
     * @param key key
     * @return true if cooldown is over, false otherwise
     */
    public boolean checkCooldown(Key key) {
        Long lastTime = pool.get(key);
        if (lastTime == null) {
            pool.put(key, System.currentTimeMillis());
            return true;
        }

        long now = System.currentTimeMillis();
        if (now - lastTime >= cooldownMillis) {
            pool.put(key, now);
            return true;
        }

        return false;
    }
}
