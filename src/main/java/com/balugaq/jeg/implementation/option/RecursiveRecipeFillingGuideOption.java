/*
 * Copyright (c) 2024-2026 balugaq
 *
 * This file is part of JustEnoughGuide, available under MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The author's name (balugaq or 大香蕉) and project name (JustEnoughGuide or JEG) shall not be
 *   removed or altered from any source distribution or documentation.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.balugaq.jeg.implementation.option;

import com.balugaq.jeg.api.patches.JEGGuideSettings;
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
public class RecursiveRecipeFillingGuideOption implements SlimefunGuideOption<Integer> {
    private static final RecursiveRecipeFillingGuideOption instance = new RecursiveRecipeFillingGuideOption();

    public static RecursiveRecipeFillingGuideOption instance() {
        return instance;
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

    public static NamespacedKey key0() {
        return KeyUtil.newKey("recursive_recipe_filling");
    }

    public static int getDepth(Player p) {
        return PersistentDataAPI.getInt(p, key0(), 1);
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
