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
    SINGLE("1 个物品", Material.RED_STAINED_GLASS_PANE),
    STACK("1 组物品", Material.YELLOW_STAINED_GLASS_PANE),
    STACK_64("64 个物品", Material.GREEN_STAINED_GLASS_PANE),
    CUSTOM("自定义个物品", Material.BLUE_STAINED_GLASS_PANE);

    private final String name;
    private final ItemStack icon;

    CompletionBehaviour(String name, Material material) {
        this.name = name;
        this.icon = Converter.getItem(
            material,
            "&a单次补全 " + string(),
            "&a点击以设置"
        );
    }

    public String string() {
        return name;
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
            return "1 组";
        }
        return RecipeCompletionGuideOption.get(p, null, side) + " 次";
    }

    public static String timesString0(Player p, ClickSide side) {
        return RecipeCompletionGuideOption.get(p, side).timesString(p, side);
    }
}
