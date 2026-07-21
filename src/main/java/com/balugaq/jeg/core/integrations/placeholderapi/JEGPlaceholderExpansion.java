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

package com.balugaq.jeg.core.integrations.placeholderapi;

import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.GuideUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author balugaq
 * @since 2.1
 */
public class JEGPlaceholderExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return JustEnoughGuide.getInstance().getRepo().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return JustEnoughGuide.getInstance().getAuthor();
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        Player p = player.getPlayer();
        if (p == null) return null;
        var option = JEGGuideSettings.getOption(params.toLowerCase());
        if (option == null) return null;
        Optional<?> ov = option.getSelectedOption(p, GuideUtil.getLastGuide(p).getItem());
        return ov.map(Object::toString).orElse(null);
    }
}