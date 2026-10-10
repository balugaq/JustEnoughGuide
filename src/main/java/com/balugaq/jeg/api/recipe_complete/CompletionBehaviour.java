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

package com.balugaq.jeg.api.recipe_complete;

import com.balugaq.jeg.api.objects.enums.ClickSide;
import com.balugaq.jeg.core.lang.Lang;
import com.balugaq.jeg.implementation.option.RecipeCompletionGuideOption;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.1
 */
@NullMarked
@Getter
public enum CompletionBehaviour {
    SINGLE("single", Material.RED_STAINED_GLASS_PANE),
    STACK("stack", Material.YELLOW_STAINED_GLASS_PANE),
    STACK_64("stack-64", Material.GREEN_STAINED_GLASS_PANE),
    CUSTOM("custom", Material.BLUE_STAINED_GLASS_PANE);

    private final String name;
    private final ItemStack icon;

    CompletionBehaviour(String name, Material material) {
        this.name = name;
        this.icon = Converter.getItem(
            material,
            Lang.t("behaviour.icon.name", display()),
            Lang.t("behaviour.icon.lore")
        );
    }

    /** 语言键后缀：{@code behaviour.<name>.display} */
    public String display() {
        return Lang.t("behaviour." + name + ".display");
    }

    public ItemStack icon() {
        return icon;
    }

    public ItemStack icon(CompletionBehaviour curr) {
        if (this != curr) {
            return icon();
        }

        return ItemStackUtil.doGlow(icon().clone());
    }

    public ChestMenu.MenuClickHandler onClick(ClickSide side) {
        return (p, s, i, a) -> {
            RecipeCompletionGuideOption.set(p, side, this);
            return false;
        };
    }

    public String timesString(Player p, ClickSide side) {
        if (this == CompletionBehaviour.STACK) {
            return Lang.t("behaviour.times.once");
        }
        return RecipeCompletionGuideOption.get(p, null, side) + Lang.t("behaviour.times.suffix");
    }

    public static String timesString0(Player p, ClickSide side) {
        return RecipeCompletionGuideOption.get(p, side).timesString(p, side);
    }
}
