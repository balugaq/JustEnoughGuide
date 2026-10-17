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

package com.balugaq.jeg.api.objects.enums;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

/**
 * 搜索匹配模式（Issue #125）。
 *
 * <ul>
 * <li>{@link #CJK}：中日韩搜索——沿用 {@code SearchGroup} 现有的字符索引模式，
 * 中文可在此基础上额外启用拼音搜索；</li>
 * <li>{@link #LATIN}：拉丁语种搜索——整串（含空格）与物品名做
 * Damerau-Levenshtein 相关度计算；</li>
 * <li>{@link #NAIVE}：朴素搜索——仅做 contains 匹配。</li>
 * </ul>
 *
 * @author balugaq
 * @since 2.1
 */
@NullMarked
public enum SearchMode {
    CJK,
    LATIN,
    NAIVE;

    public SearchMode next() {
        SearchMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    /**
     * 按配置的 {@code language} 推导默认搜索模式：
     * 语言含 {@code zh}/{@code jp}/{@code ko} 时默认 CJK，否则默认拉丁语种搜索。
     */
    public static SearchMode fromLanguage(@Nullable String language) {
        if (language == null) {
            return LATIN;
        }

        String lang = language.toLowerCase(Locale.ROOT);
        if (lang.contains("zh") || lang.contains("jp") || lang.contains("ko")) {
            return CJK;
        }

        return LATIN;
    }

    /**
     * 该语言是否支持拼音搜索（仅中文）。
     */
    public static boolean languageSupportsPinyin(@Nullable String language) {
        return language != null && language.toLowerCase(Locale.ROOT).contains("zh");
    }
}
