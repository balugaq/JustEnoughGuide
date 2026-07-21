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

import com.balugaq.jeg.utils.platform.PlatformUtil;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/**
 * @author balugaq
 * @since 1.7
 */
@SuppressWarnings({"unused", "deprecation"})
@UtilityClass
@NullMarked
public class ClipboardUtil {
    public static void send(Player player, String display, String hover, String text) {
        if (PlatformUtil.isPaper()) {
            Component base =
                Component.text(ChatColors.color(display) + ": ", TextColor.color(0x00FF00)).append(Component.text(text, TextColor.color(0xFFFF00))).hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(Component.text(ChatColors.color(hover), TextColor.color(0xFFFF00))));
            Component clickToCopy =
                base.clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.COPY_TO_CLIPBOARD, text));
            player.sendMessage(clickToCopy);
        } else {
            player.spigot().sendMessage(makeComponent(display, hover, text));
        }
    }

    public static Component makeComponentPaper(Component display, String text) {
        return makeComponentPaper(
            display,
            Component.text().color(NamedTextColor.YELLOW).append(Component.text("点击复制")).build(),
            text);
    }

    public static Component makeComponentPaper(Component display, Component hover, String text) {
        return Component.text().append(display).hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(hover))
            .clickEvent(net.kyori.adventure.text.event.ClickEvent.copyToClipboard(text))
            .build();
    }

    public static TextComponent makeComponent(String display, String hover, String text) {
        return makeComponent(display, hover, text, null);
    }

    public static TextComponent makeComponent(
        String display, String hover, String text, @Nullable Consumer<TextComponent> consumer) {
        TextComponent msg = new TextComponent(ChatColors.color(display));
        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColors.color(hover))));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text));
        if (consumer != null) {
            consumer.accept(msg);
        }

        return msg;
    }

    public static void send(Player player, TextComponent component) {
        player.spigot().sendMessage(component);
    }
}
