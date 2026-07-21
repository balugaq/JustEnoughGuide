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
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"UnnecessaryUnicodeEscape", "SameReturnValue"})
@NullMarked
public class NoticeMissingMaterialGuideOption implements PrioritySlimefunGuideOption<Boolean> {
    private static final NoticeMissingMaterialGuideOption instance = new NoticeMissingMaterialGuideOption();

    public static NoticeMissingMaterialGuideOption instance() {
        return instance;
    }

    @Override
    public int priority() {
        return Priorities.NoticeMissingMaterialGuideOption;
    }

    public static boolean isEnabled(Player p) {
        return getSelectedOption(p);
    }

    public static boolean getSelectedOption(Player p) {
        return !PersistentDataAPI.hasByte(p, key0()) || PersistentDataAPI.getByte(p, key0()) == (byte) 1;
    }

    public static NamespacedKey key0() {
        return KeyUtil.newKey("notice_missing_material");
    }

    @Override
    public SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        boolean enabled = getSelectedOption(p, guide).orElse(false);
        ItemStack item = Converter.getItem(
            isEnabled(p) ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK,
            "&b告知缺失的材料: &" + (enabled ? "a启用" : "4禁用"),
            "",
            "&7你现在可以选择",
            "&7当你使用配方补全时",
            "&7如果材料不足",
            "&7是否告知缺失的材料",
            "&e&l此功能为实验性功能，谨慎使用",
            "&c&l此功能容易误报",
            "",
            "&7\u21E8 &e点击 " + (enabled ? "禁用" : "启用") + " 告知缺失的材料"
        );
        return Optional.of(item);
    }

    @Override
    public NamespacedKey getKey() {
        return key0();
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        setSelectedOption(p, guide, !getSelectedOption(p, guide).orElse(false));
        JEGGuideSettings.openSettings(p, guide);
    }

    @Override
    public Optional<Boolean> getSelectedOption(Player p, ItemStack guide) {
        NamespacedKey key = getKey();
        boolean value = !PersistentDataAPI.hasByte(p, key) || PersistentDataAPI.getByte(p, key) == (byte) 1;
        return Optional.of(value);
    }

    @Override
    public void setSelectedOption(Player p, ItemStack guide, Boolean value) {
        PersistentDataAPI.setByte(p, getKey(), value ? (byte) 1 : (byte) 0);
    }
}
