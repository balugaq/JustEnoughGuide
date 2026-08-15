/*
 * RykenSlimefunCustomizer
 * Copyright (C) 2026 lijinhong11(mmmjjjkx) and balugaq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.balugaq.jeg.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author balugaq
 */
public class HexColorParser {

    // 构建一个正则表达式匹配所有格式（使用 | 或）
    // 注意顺序：从最具体到最通用
    private static final Pattern ALL_PATTERNS = Pattern.compile(
        "&x(&[0-9a-fA-F]){6}" +      // &x&R&R&G&G&B&B
            "|&#([0-9a-fA-F]{6})&?" +     // &#RRGGBB 或 &#RRGGBB&
            "|&\\{#([0-9a-fA-F]{6})\\}" + // &{#RRGGBB}
            "§x(&[0-9a-fA-F]){6}" +      // §x§R§R§G§G§B§B
            "|§#([0-9a-fA-F]{6})&?" +     // §#RRGGBB 或 §#RRGGBB§
            "|§\\{#([0-9a-fA-F]{6})\\}" + // §{#RRGGBB}
            "|\\{#([0-9a-fA-F]{6})\\}" +  // {#RRGGBB}
            "|<#([0-9a-fA-F]{6})>" +      // <#RRGGBB>
            "|#([0-9a-fA-F]{6})" +        // #RRGGBB
            "|\\[([0-9a-fA-F]{6})\\]",    // [RRGGBB]
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Convert hex color codes in input text to MiniMessage format
     * @param input Raw text
     * @return Converted text
     */
    public static String parse(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuffer sb = new StringBuffer();
        Matcher matcher = ALL_PATTERNS.matcher(input);

        while (matcher.find()) {
            String matched = matcher.group();
            String hexCode = extractHexCode(matched);
            String replacement = "<#" + hexCode + ">";
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * Extract pure hex color code from matched string
     */
    private static String extractHexCode(String matched) {
        // Handle &x&R&R&G&G&B&B format
        if (matched.startsWith("&x") || matched.startsWith("§x")) {
            // Remove "&x" and all "&" characters
            String hex = matched.substring(2).replaceAll("[&§]", "");
            return hex.toLowerCase();
        }

        // Handle &#RRGGBB& format (trailing &)
        if ((matched.startsWith("&#") && matched.endsWith("&") || matched.startsWith("§#") && matched.endsWith("§")) && matched.length() == 8) {
            return matched.substring(2, 8).toLowerCase();
        }

        // Handle &#RRGGBB format
        if ((matched.startsWith("&#") || matched.startsWith("§#")) && matched.length() == 8) {
            return matched.substring(2, 8).toLowerCase();
        }

        // Handle &{#RRGGBB} format
        if (matched.startsWith("&{#") || matched.startsWith("§{#")) {
            return matched.substring(3, 9).toLowerCase();
        }

        // Handle {#RRGGBB} format
        if (matched.startsWith("{#")) {
            return matched.substring(2, 8).toLowerCase();
        }

        // Handle <#RRGGBB> format
        if (matched.startsWith("<#")) {
            return matched.substring(2, 8).toLowerCase();
        }

        // Handle #RRGGBB format
        if (matched.startsWith("#")) {
            return matched.substring(1, 7).toLowerCase();
        }

        // Handle [RRGGBB] format
        if (matched.startsWith("[")) {
            return matched.substring(1, 7).toLowerCase();
        }

        // Fallback: try to extract any 6-digit hex
        java.util.regex.Matcher hexMatcher = Pattern.compile("[0-9a-fA-F]{6}").matcher(matched);
        if (hexMatcher.find()) {
            return hexMatcher.group().toLowerCase();
        }

        return "ffffff";
    }
}