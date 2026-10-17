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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link SearchMode} 的语言判定与循环切换测试（Issue #125）。
 *
 * @author balugaq
 * @since 2.1
 */
class SearchModeTest {
    @Test
    @DisplayName("含 zh/jp/ko 的语言默认 CJK，否则拉丁")
    void fromLanguage() {
        assertEquals(SearchMode.CJK, SearchMode.fromLanguage("zh_CN"));
        assertEquals(SearchMode.CJK, SearchMode.fromLanguage("zh_TW"));
        assertEquals(SearchMode.CJK, SearchMode.fromLanguage("ja_JP"));
        assertEquals(SearchMode.CJK, SearchMode.fromLanguage("ko_KR"));
        assertEquals(SearchMode.LATIN, SearchMode.fromLanguage("en_US"));
        assertEquals(SearchMode.LATIN, SearchMode.fromLanguage("fr_FR"));
        assertEquals(SearchMode.LATIN, SearchMode.fromLanguage(null));
    }

    @Test
    @DisplayName("语言判定大小写不敏感")
    void fromLanguageCaseInsensitive() {
        assertEquals(SearchMode.CJK, SearchMode.fromLanguage("ZH_CN"));
        assertEquals(SearchMode.CJK, SearchMode.fromLanguage("Ja_JP"));
        assertEquals(SearchMode.LATIN, SearchMode.fromLanguage("EN_us"));
    }

    @Test
    @DisplayName("仅中文支持拼音搜索")
    void languageSupportsPinyin() {
        assertEquals(true, SearchMode.languageSupportsPinyin("zh_CN"));
        assertEquals(true, SearchMode.languageSupportsPinyin("zh_TW"));
        assertEquals(false, SearchMode.languageSupportsPinyin("ja_JP"));
        assertEquals(false, SearchMode.languageSupportsPinyin("ko_KR"));
        assertEquals(false, SearchMode.languageSupportsPinyin("en_US"));
        assertEquals(false, SearchMode.languageSupportsPinyin(null));
    }

    @Test
    @DisplayName("模式循环切换")
    void next() {
        assertEquals(SearchMode.LATIN, SearchMode.CJK.next());
        assertEquals(SearchMode.NAIVE, SearchMode.LATIN.next());
        assertEquals(SearchMode.CJK, SearchMode.NAIVE.next());
    }
}
