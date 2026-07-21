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

import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.api.patches.Priorities;
import com.balugaq.jeg.api.patches.PrioritySlimefunGuideOption;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Calculator;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

import static com.balugaq.jeg.api.recipe_complete.source.base.Source.RECIPE_DEPTH_THRESHOLD;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"UnnecessaryUnicodeEscape", "SameReturnValue"})
@NullMarked
public class RecursiveRecipeFillingGuideOption implements PrioritySlimefunGuideOption<Integer> {
    private static final RecursiveRecipeFillingGuideOption instance = new RecursiveRecipeFillingGuideOption();

    public static RecursiveRecipeFillingGuideOption instance() {
        return instance;
    }

    @Override
    public int priority() {
        return Priorities.RecursiveRecipeFillingGuideOption;
    }

    public static NamespacedKey key0() {
        return KeyUtil.newKey("recursive_recipe_filling");
    }

    public static int getDepth(Player p) {
        return PersistentDataAPI.getInt(p, key0(), 1);
    }

    @Override
    public SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        int value = getSelectedOption(p, guide).orElse(1);
        if (value > RECIPE_DEPTH_THRESHOLD) {
            value = RECIPE_DEPTH_THRESHOLD;
            PersistentDataAPI.setInt(p, key0(), value);
        }

        ItemStack item = Converter.getItem(
            Material.FURNACE,
            "&a配方补全深度",
            "&7配方补全深度越大，需要的时间越长",
            "&7如果遇到一个材料不存在，会尝试补全",
            "&7这个材料的材料，以此类推，此过程视为一层深度",
            "&e&l此功能为实验性功能，谨慎使用",
            "&c&l此功能容易造成错误",
            "",
            "&7当前深度: " + value + " (限制范围: 1~" + RECIPE_DEPTH_THRESHOLD + ")",
            "&7\u21E8 &e点击设置深度"
        );
        return Optional.of(item);
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        p.closeInventory();
        p.sendMessage(ChatColors.color("&a请输入配方补全深度"));
        ChatInput.waitForPlayer(
            JustEnoughGuide.getInstance(), p, s -> {
                try {
                    int value = Calculator.calculate(s).intValue();
                    if (value < 1 || value > RECIPE_DEPTH_THRESHOLD) {
                        p.sendMessage("请输入 1 ~ " + RECIPE_DEPTH_THRESHOLD + " 之间的正整数");
                        return;
                    }

                    setSelectedOption(p, guide, value);
                    JEGGuideSettings.openSettings(p, guide);
                } catch (NumberFormatException ignored) {
                    p.sendMessage("请输入 1 ~ " + RECIPE_DEPTH_THRESHOLD + " 之间的正整数");
                }
            }
        );
    }

    @Override
    public NamespacedKey getKey() {
        return key0();
    }

    @Override
    public Optional<Integer> getSelectedOption(Player p, ItemStack guide) {
        return Optional.of(getDepth(p));
    }

    @Override
    public void setSelectedOption(Player p, ItemStack guide, Integer value) {
        PersistentDataAPI.setInt(p, getKey(), value);
    }
}
