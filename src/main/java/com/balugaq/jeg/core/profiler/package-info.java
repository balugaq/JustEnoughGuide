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

/**
 * This package contains the JEG performance monitor.
 * <p>
 * {@link com.balugaq.jeg.core.profiler.JEGProfiler} replaces Slimefun's own profiler via reflection and
 * measures machine execution time at the {@code tick()} call site through
 * {@link com.balugaq.jeg.core.profiler.TimedBlockTicker}, which avoids the scheduling delay that
 * {@code /sf timings} folds into its numbers.
 *
 * @author balugaq
 * @since 2.2
 */
