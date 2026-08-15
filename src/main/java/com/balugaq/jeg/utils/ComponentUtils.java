/*
 * MittelLib
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.balugaq.jeg.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * Source: https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/utils/StringUtils.java
 *
 * @author lijinhong11
 * @author balugaq
 */
@UtilityClass
public class ComponentUtils {
//    private static final TagResolver COMBINE_TAG_RESOLVER;

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private static final Component RESET = Component.empty().decoration(TextDecoration.ITALIC, false);

//    static {
//        if (Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")) {
//            COMBINE_TAG_RESOLVER = TagResolver.resolver(
//                    MiniMessage.miniMessage().tags(),
//                    MiniPlaceholders.globalPlaceholders(),
//                    MiniPlaceholders.audiencePlaceholders());
//        } else {
//            COMBINE_TAG_RESOLVER = MiniMessage.miniMessage().tags();
//        }
//    }

    private static String legacyReplacement(char code) {
        return switch (code) {
            case '0' -> "<black>";
            case '1' -> "<dark_blue>";
            case '2' -> "<dark_green>";
            case '3' -> "<dark_aqua>";
            case '4' -> "<dark_red>";
            case '5' -> "<dark_purple>";
            case '6' -> "<gold>";
            case '7' -> "<gray>";
            case '8' -> "<dark_gray>";
            case '9' -> "<blue>";
            case 'a' -> "<green>";
            case 'b' -> "<aqua>";
            case 'c' -> "<red>";
            case 'd' -> "<light_purple>";
            case 'e' -> "<yellow>";
            case 'f' -> "<white>";
            case 'k' -> "<magic>";
            case 'l' -> "<bold>";
            case 'm' -> "<strikethrough>";
            case 'n' -> "<underline>";
            case 'o' -> "<italic>";
            case 'r' -> "<reset>";
            default -> null;
        };
    }

    /**
     * Deserialize a string to {@link Component}
     *
     * @param input the string
     * @return a {@link Component}
     */
    public static Component legacyDeserialize(String input) {
//        return deserialize(null, input);
//    }
//
//    /**
//     * Deserialize a string to {@link Component}
//     * @param cs the sender (used for placeholder parsing)
//     * @param input the string
//     * @return a {@link Component}
//     */
//    public static Component deserialize(@Nullable CommandSender cs, String input) {
        if (input == null) {
            return Component.empty();
        }

//        input = StringUtils.parsePlaceholders(cs, input);

        input = fromLegacy(input, "&");
        input = fromLegacy(input, "§");

//        if (cs != null) {
//            return RESET.append(MINI.deserialize(input, cs, COMBINE_TAG_RESOLVER));
//        } else {
//            return RESET.append(MINI.deserialize(input, COMBINE_TAG_RESOLVER));
//        }
        return MINI.deserialize(input);
    }

    public static String fromLegacy(String legacy, String character) {
        legacy = HexColorParser.parse(legacy);
        StringBuilder sb = new StringBuilder(legacy.length());
        char prefix = character.charAt(0);

        for (int i = 0; i < legacy.length(); i++) {
            char c = legacy.charAt(i);

            if (c == prefix && i + 1 < legacy.length()) {
                char next = legacy.charAt(i + 1);

                String replace = legacyReplacement(next);
                if (replace != null) {
                    sb.append(replace);
                    i++;
                    continue;
                }
            }

            sb.append(c);
        }

        return sb.toString();
    }

    public static Component text(String input) {
        return RESET.append(Component.text(input));
    }

//    public static String serialize(Component component) {
//        return MINI.serialize(component);
//    }
}