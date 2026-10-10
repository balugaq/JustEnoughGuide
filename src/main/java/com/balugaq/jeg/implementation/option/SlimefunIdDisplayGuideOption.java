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

package com.balugaq.jeg.implementation.option;

import com.balugaq.jeg.api.patches.Priorities;
import com.balugaq.jeg.core.lang.Lang;
import com.balugaq.jeg.utils.compatibility.Converter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings({"UnnecessaryUnicodeEscape", "SameReturnValue"})
@NullMarked
public class SlimefunIdDisplayGuideOption extends AbstractBooleanGuideOption {
    private static final SlimefunIdDisplayGuideOption instance = new SlimefunIdDisplayGuideOption();

    public static SlimefunIdDisplayGuideOption instance() {
        return instance;
    }

    @Override
    public int priority() {
        return Priorities.SlimefunIdDisplayGuideOption;
    }

    @Override
    public ItemStack getDisplayItem(Player p, ItemStack guide, boolean enabled) {
        return Converter.getItem(
            isEnabled(p) ? Material.GLOWSTONE : Material.REDSTONE_LAMP,
            Lang.t("option.slimefun-id-display.name", enabled ? Lang.t("common.state-on") : Lang.t("common.state-off")),
            Lang.lines("option.slimefun-id-display.lore", enabled ? Lang.t("common.disable") : Lang.t("common.enable"))
                .toArray(String[]::new)
        );
    }

    @Override
    public String key0() {
        return "slimefun_id_display";
    }
}
