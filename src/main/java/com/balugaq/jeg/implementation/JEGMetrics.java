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

package com.balugaq.jeg.implementation;

import lombok.Data;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

/**
 * @author balugaq
 * @since 2.1
 */
@Data
public class JEGMetrics {
    private final Metrics metrics;

    public JEGMetrics() {
        this.metrics = new Metrics(JustEnoughGuide.getInstance(), 32108);
        metrics.addCustomChart(
            new SimplePie("chart_id", () -> "My value")
        );
    }

    public void shutdown() {
        metrics.shutdown();
    }
}
