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
 * This class is used to represent the option to show the beginner's guide. which is editable in the settings menu.
 *
 * @author balugaq
 * @since 1.5
 */
@SuppressWarnings({"UnnecessaryUnicodeEscape", "SameReturnValue"})
@NullMarked
public class BeginnersGuideOption extends AbstractBooleanGuideOption {
    private static final BeginnersGuideOption instance = new BeginnersGuideOption();

    public static BeginnersGuideOption instance() {
        return instance;
    }

    @Override
    public int priority() {
        return Priorities.BeginnersGuideOption;
    }

    @Override
    public ItemStack getDisplayItem(Player p, ItemStack guide, boolean enabled) {
        return Converter.getItem(
            isEnabled(p) ? Material.KNOWLEDGE_BOOK : Material.BOOK,
            Lang.t("option.beginners-guide.name", enabled ? Lang.t("common.state-on") : Lang.t("common.state-off")),
            Lang.lines("option.beginners-guide.lore", enabled ? Lang.t("common.disable") : Lang.t("common.enable"))
                .toArray(String[]::new)
        );
    }

    public String key0() {
        return "beginners_guide";
    }
}
