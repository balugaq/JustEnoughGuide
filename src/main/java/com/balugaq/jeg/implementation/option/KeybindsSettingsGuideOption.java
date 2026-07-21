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
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
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
@SuppressWarnings({"SameReturnValue"})
@NullMarked
public class KeybindsSettingsGuideOption extends AbstractCustomActionGuideOption {
    public static final ItemStack DEFAULT_ICON = Converter.getItem(
        Material.COMPASS,
        "&a单击打开指南书按键控制界面"
    );;

    @Override
    public int priority() {
        return Priorities.KeybindsSettingsGuideOption;
    }

    private static final KeybindsSettingsGuideOption instance = new KeybindsSettingsGuideOption();

    public static KeybindsSettingsGuideOption instance() {
        return instance;
    }

    @Override
    public ItemStack getDisplayItem(Player p, ItemStack guide, boolean unused) {
        return DEFAULT_ICON;
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        GuideUtil.openKeybindsGui(p);
    }

    @Override
    public String key0() {
        return "keybinds_settings";
    }
}
