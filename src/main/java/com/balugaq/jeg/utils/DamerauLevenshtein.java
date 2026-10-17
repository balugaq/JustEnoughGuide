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

package com.balugaq.jeg.utils;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Damerau-Levenshtein 距离（最优字符串对齐受限版，OSA）：
 * 在经典 Levenshtein 的插入/删除/替换之外，额外允许相邻字符换位记 1 次编辑，
 * 用于拉丁语种搜索的整串相关度计算（Issue #125）。
 *
 * @author balugaq
 * @since 2.1
 */
@NullMarked
public final class DamerauLevenshtein {
    private DamerauLevenshtein() {
    }

    public static int distance(@Nullable String s1, @Nullable String s2) {
        if (s1 == null || s2 == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        if (s1.equals(s2)) {
            return 0;
        }

        int n = s1.length();
        int m = s2.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                d[i][j] = Math.min(
                    Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1),
                    d[i - 1][j - 1] + cost);

                if (i > 1 && j > 1
                    && s1.charAt(i - 1) == s2.charAt(j - 2)
                    && s1.charAt(i - 2) == s2.charAt(j - 1)) {
                    d[i][j] = Math.min(d[i][j], d[i - 2][j - 2] + 1);
                }
            }
        }

        return d[n][m];
    }

    /**
     * 相关度打分（0-100）：距离越小分越高，长度差异也计入惩罚。
     */
    public static int fit(String a, String b) {
        int distance = distance(a, b);
        int maxLen = Math.max(a.length(), b.length());
        if (maxLen == 0) {
            return 100;
        }

        return (int) (100 * (1 - (double) distance / maxLen));
    }
}
