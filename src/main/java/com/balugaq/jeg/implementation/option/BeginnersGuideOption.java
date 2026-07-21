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
            "&b新手指引: &" + (enabled ? "a启用" : "4禁用"),
            "",
            "&7你现在可以选择是否",
            "&7在查阅一个物品的时候",
            "&7Shift+右键点击搜索这个物品的名字.",
            "",
            "&7\u21E8 &e点击 " + (enabled ? "禁用" : "启用") + " 新手指引"
        );
    }

    public String key0() {
        return "beginners_guide";
    }
}
