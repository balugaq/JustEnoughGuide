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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link DamerauLevenshtein} 的距离与相关度测试（Issue #125 拉丁语种搜索）。
 *
 * @author balugaq
 * @since 2.1
 */
class DamerauLevenshteinTest {
    @Test
    @DisplayName("经典距离用例")
    void classicDistances() {
        assertEquals(3, DamerauLevenshtein.distance("kitten", "sitting"));
        assertEquals(3, DamerauLevenshtein.distance("sitting", "kitten"));
        assertEquals(2, DamerauLevenshtein.distance("flaw", "lawn"));
        assertEquals(3, DamerauLevenshtein.distance("ca", "abc"));
    }

    @Test
    @DisplayName("相邻换位记 1 次编辑（Damerau 特性）")
    void transpositionCountsAsOne() {
        assertEquals(1, DamerauLevenshtein.distance("ab", "ba"));
        assertEquals(1, DamerauLevenshtein.distance("abcd", "acbd"));
        // OSA 受限版：非相邻换位不算 1 次
        assertEquals(3, DamerauLevenshtein.distance("ca", "abc"));
    }

    @Test
    @DisplayName("空串与相同串")
    void emptyAndIdentical() {
        assertEquals(0, DamerauLevenshtein.distance("", ""));
        assertEquals(0, DamerauLevenshtein.distance("same", "same"));
        assertEquals(4, DamerauLevenshtein.distance("", "test"));
        assertEquals(4, DamerauLevenshtein.distance("test", ""));
    }

    @Test
    @DisplayName("大小写敏感，由调用方统一小写")
    void caseSensitive() {
        assertEquals(3, DamerauLevenshtein.distance("ABC", "abc"));
    }

    @Test
    @DisplayName("null 入参直接抛异常")
    void nullRejected() {
        assertThrows(IllegalArgumentException.class, () -> DamerauLevenshtein.distance(null, "a"));
        assertThrows(IllegalArgumentException.class, () -> DamerauLevenshtein.distance("a", null));
    }

    @Test
    @DisplayName("相关度打分 0-100")
    void fitScores() {
        assertEquals(100, DamerauLevenshtein.fit("hello", "hello"));
        assertEquals(80, DamerauLevenshtein.fit("hello", "hell"));
        assertEquals(0, DamerauLevenshtein.fit("", "abc"));
        // 完全不同且长度相同的串，相关度按距离衰减但不为负
        int fit = DamerauLevenshtein.fit("abc", "xyz");
        assertEquals(0, fit);
    }
}
