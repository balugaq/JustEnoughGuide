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

package com.balugaq.jeg.utils.compatibility;

import com.balugaq.jeg.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.6
 */
@SuppressWarnings("deprecation")
@NullMarked
public class Sounds {
    public static final Sound GUIDE_BUTTON_CLICK_SOUND =
        byKeyOrDefault("item.book.page_turn", byName("ITEM_BOOK_PAGE_TURN"));
    public static final Sound COLLECTED_ITEM = byKeyOrDefault("entity.player.levelup", byName("ENTITY_PLAYER_LEVELUP"));

    public static @Nullable Sound byName(String name) {
        return ReflectionUtil.getStaticValue(Sound.class, name, Sound.class);
    }

    public static Sound byKeyOrDefault(String key, @UnknownNullability Sound def) {
        Sound sound = byKey(key);
        return sound == null ? def : sound;
    }

    public static @Nullable Sound byKey(String key) {
        Registry<Sound> registry = Bukkit.getRegistry(Sound.class);
        if (registry == null) {
            return null;
        }

        return registry.get(NamespacedKey.minecraft(key));
    }

    public static void playFor(Player player, @Nullable Sound sound) {
        playFor(player, sound, 1.0F, 1.0F);
    }

    public static void playFor(Player player, @Nullable Sound sound, float volume, float pitch) {
        if (sound == null) {
            return;
        }

        player.playSound(player.getEyeLocation(), sound, SoundCategory.PLAYERS, volume, pitch);
    }
}
