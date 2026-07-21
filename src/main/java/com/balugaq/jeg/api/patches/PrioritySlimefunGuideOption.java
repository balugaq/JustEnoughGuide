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

package com.balugaq.jeg.api.patches;

import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;

/**
 * @author balugaq
 * @since 2.1
 */
public interface PrioritySlimefunGuideOption<T> extends SlimefunGuideOption<T> {
    int DEFAULT_PRIORITY = 1000;

    /**
     * The priority of the option. The higher the priority, the earlier the option will be shown.
     * For any other PrioritySlimefunGuideOption that has the same priority, the order is sorted by {@link SlimefunGuideOption#getKey()}
     * For any other SlimefunGuideOption that has no priority, uses {@link #DEFAULT_PRIORITY}.
     */
    default int priority() {
        return DEFAULT_PRIORITY;
    }
}
