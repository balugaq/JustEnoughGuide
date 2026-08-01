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

import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author balugaq
 * @since 2.1
 */
@UtilityClass
@NullMarked
public class StringUtil {
    /**
     * Matches hex color codes like {@code &#FFAA00} or {@code #FFAA00}.
     */
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("(?:&|§)?#([0-9a-fA-F]{6})");

    /**
     * Converts hex color codes ({@code &#RRGGBB} / {@code #RRGGBB}) into the legacy
     * {@code &x&R&R&G&G&B&B} format and immediately applies all color codes via
     * {@link ChatColors#color(String)} in a single step.
     */
    public String translateHexColors(String input) {
        if (!input.contains("#")) {
            return ChatColors.color(input);
        }
        Matcher matcher = HEX_COLOR_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            StringBuilder replacement = new StringBuilder("&x");
            for (char c : matcher.group(1).toLowerCase().toCharArray()) {
                replacement.append('&').append(c);
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement.toString()));
        }
        matcher.appendTail(sb);
        return ChatColors.color(sb.toString());
    }
}
