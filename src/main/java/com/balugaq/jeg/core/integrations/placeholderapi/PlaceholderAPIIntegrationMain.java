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

import com.balugaq.jeg.core.integrations.Integration;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.1
 */
@NullMarked
public class PlaceholderAPIIntegrationMain implements Integration {
    private @UnknownNullability JEGPlaceholderExpansion expansion;

    public String getHookPlugin() {
        return "PlaceholderAPI";
    }

    @Override
    public void onEnable() {
        expansion = new JEGPlaceholderExpansion();
        expansion.register();
    }

    @Override
    public void onDisable() {
        expansion.unregister();
    }
}
