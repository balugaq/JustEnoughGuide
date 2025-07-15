/*
 * Copyright (c) 2024-2025 balugaq
 *
 * This file is part of JustEnoughGuide, available under MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The author's name (balugaq or 大香蕉) and project name (JustEnoughGuide or JEG) shall not be
 *   removed or altered from any source distribution or documentation.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.balugaq.jeg.utils;

import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import java.util.function.Consumer;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author balugaq
 * @since 1.7
 */
@SuppressWarnings({"deprecation", "unused"})
@UtilityClass
public class ClipboardUtil {
    @ParametersAreNonnullByDefault
    public static void send(Player player, String display, String hover, String text) {
        player.spigot().sendMessage(makeComponent(display, hover, text));
    }

    @ParametersAreNonnullByDefault
    public static void send(Player player, TextComponent component) {
        player.spigot().sendMessage(component);
    }

    @ParametersAreNonnullByDefault
    public static @NotNull TextComponent makeComponent(String display, String hover, String text) {
        return makeComponent(display, hover, text, null);
    }

    @ParametersAreNonnullByDefault
    public static @NotNull TextComponent makeComponent(
            String display, String hover, String text, @Nullable Consumer<TextComponent> consumer) {
        TextComponent msg = new TextComponent(ChatColors.color(display));
        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColors.color(hover))));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, ChatColors.color(text)));
        if (consumer != null) {
            consumer.accept(msg);
        }

        return msg;
    }
}
