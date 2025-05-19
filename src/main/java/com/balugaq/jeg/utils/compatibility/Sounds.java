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

package com.balugaq.jeg.utils.compatibility;

import com.balugaq.jeg.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * @author balugaq
 * @since 1.6
 */
public class Sounds {
    public static final Sound GUIDE_BUTTON_CLICK_SOUND = byKeyOr("item.book.page_turn", byName("ITEM_BOOK_PAGE_TURN"));
    public static final Sound COLLECTED_ITEM = byKeyOr("entity.player.levelup", byName("ENTITY_PLAYER_LEVELUP"));

    public static Sound byName(String name) {
        return ReflectionUtil.getStaticValue(Sound.class, name, Sound.class);
    }

    public static Sound byKey(String key) {
        var registry = Bukkit.getRegistry(Sound.class);
        if (registry == null) {
            return null;
        }

        return registry.get(NamespacedKey.minecraft(key));
    }

    public static Sound byKeyOr(String key, Sound def) {
        var sound = byKey(key);
        return sound == null ? def : sound;
    }

    public static void playFor(@NotNull Player player, @Nullable Sound sound) {
        playFor(player, sound, 1.0F, 1.0F);
    }

    public static void playFor(@Nonnull Player player, @Nullable Sound sound, float volume, float pitch) {
        if (sound == null) {
            return;
        }

        player.playSound(player.getEyeLocation(), sound, SoundCategory.PLAYERS, volume, pitch);
    }
}
