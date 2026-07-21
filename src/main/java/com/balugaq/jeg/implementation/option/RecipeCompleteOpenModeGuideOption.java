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

import com.balugaq.jeg.api.objects.enums.RecipeCompleteOpenMode;
import com.balugaq.jeg.api.patches.Priorities;
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
public class RecipeCompleteOpenModeGuideOption extends AbstractBooleanGuideOption {
    private static final RecipeCompleteOpenModeGuideOption instance = new RecipeCompleteOpenModeGuideOption();

    public static RecipeCompleteOpenModeGuideOption instance() {
        return instance;
    }

    @Override
    public int priority() {
        return Priorities.RecipeCompleteOpenModeGuideOption;
    }

    public RecipeCompleteOpenMode get(Player player) {
        return isEnabled(player) ? RecipeCompleteOpenMode.INHERIT : RecipeCompleteOpenMode.NEW;
    }

    @Override
    public ItemStack getDisplayItem(Player p, ItemStack guide, boolean enabled) {
        return Converter.getItem(
            isEnabled(p) ? Material.ENCHANTED_BOOK : Material.KNOWLEDGE_BOOK,
            "&b配方补全打开模式: &" + (enabled ? "4继承上一次打开的界面" : "a打开新界面"),
            "",
            "&7\u21E8 &e点击切换配方补全打开模式为 " + (enabled ? "打开新界面" : "继承上一次打开的界面")
        );
    }

    public String key0() {
        return "recipe_complete_open_mode";
    }
}
