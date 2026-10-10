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
            Lang.t("option.recipe-complete-open-mode.name",
                enabled ? Lang.t("option.recipe-complete-open-mode.mode-inherit") : Lang.t("option.recipe-complete-open-mode.mode-new")),
            "",
            Lang.t("option.recipe-complete-open-mode.click",
                enabled ? Lang.t("option.recipe-complete-open-mode.target-new") : Lang.t("option.recipe-complete-open-mode.target-inherit"))
        );
    }

    public String key0() {
        return "recipe_complete_open_mode";
    }
}
