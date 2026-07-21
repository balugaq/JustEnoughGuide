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

/**
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings({"UnnecessaryUnicodeEscape", "SameReturnValue"})
@NullMarked
public class RecipeFillingWithNearbyContainerGuideOption implements PrioritySlimefunGuideOption<Integer> {
    public static final int MAX_REACH_LENGTH = 2; // 2*2+1=5, 5*5*5=125 blocks
    private static final RecipeFillingWithNearbyContainerGuideOption instance = new RecipeFillingWithNearbyContainerGuideOption();

    @Override
    public int priority() {
        return Priorities.RecipeFillingWithNearbyContainerGuideOption;
    }

    public static RecipeFillingWithNearbyContainerGuideOption instance() {
        return instance;
    }

    public static NamespacedKey key0() {
        return KeyUtil.newKey("recipe_filling_with_nearby_container");
    }

    public static int getRadiusDistance(Player p) {
        return PersistentDataAPI.getInt(p, key0(), 2);
    }

    @Override
    public SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        int value = getSelectedOption(p, guide).orElse(2);
        if (value > MAX_REACH_LENGTH) {
            value = MAX_REACH_LENGTH;
            PersistentDataAPI.setInt(p, key0(), value);
        }

        ItemStack item = Converter.getItem(
            Material.ENDER_CHEST,
            "&a配方补全自动抓取",
            "&7配方补全自动抓取，即在配方补全获取材料时",
            "&7从周围的粘液容器中获取原材料",
            "&e仅支持粘液容器",
            "&7当前半径范围: " + value + " (限制范围: 0~" + MAX_REACH_LENGTH + ")",
            "&7\u21E8 &e点击设置配方补全自动抓取范围"
        );
        return Optional.of(item);
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        p.closeInventory();
        p.sendMessage(ChatColors.color("&a请输入配方补全自动抓取范围"));
        ChatInput.waitForPlayer(
            JustEnoughGuide.getInstance(), p, s -> {
                try {
                    int value = Calculator.calculate(s).intValue();
                    if (value < 0 || value > MAX_REACH_LENGTH) {
                        p.sendMessage("请输入 0 ~ " + MAX_REACH_LENGTH + " 之间的正整数");
                        return;
                    }

                    setSelectedOption(p, guide, value);
                    JEGGuideSettings.openSettings(p, guide);
                } catch (NumberFormatException ignored) {
                    p.sendMessage("请输入 0 ~ " + MAX_REACH_LENGTH + " 之间的正整数");
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
        return Optional.of(getRadiusDistance(p));
    }

    @Override
    public void setSelectedOption(Player p, ItemStack guide, Integer value) {
        PersistentDataAPI.setInt(p, getKey(), value);
    }
}
