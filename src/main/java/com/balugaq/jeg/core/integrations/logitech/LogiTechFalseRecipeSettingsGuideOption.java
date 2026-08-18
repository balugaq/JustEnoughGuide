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

package com.balugaq.jeg.core.integrations.logitech;

import com.balugaq.jeg.api.patches.Priorities;
import com.balugaq.jeg.implementation.option.AbstractItemSettingsGuideOption;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * @author balugaq
 * @since 2.1
 */
@SuppressWarnings({"SameReturnValue"})
@NullMarked
public class LogiTechFalseRecipeSettingsGuideOption extends AbstractItemSettingsGuideOption {
    public static final LogiTechFalseRecipeSettingsGuideOption instance = new LogiTechFalseRecipeSettingsGuideOption();

    public static LogiTechFalseRecipeSettingsGuideOption instance() {
        return instance;
    }

    public static @Nullable ItemStack[] getItems(Player player) {
        @Nullable ItemStack[] items = new ItemStack[4];
        for (int i = 9; i < 13; i++) {
            ItemStack itemStack = AbstractItemSettingsGuideOption.getItem(player, key0(), i);
            items[i - 9] = itemStack;
        }
        return items;
    }

    public static NamespacedKey key0() {
        return KeyUtil.newKey("logitech_false_recipe_settings");
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        var sf = SlimefunItem.getById("LOGITECH_FALSE_");
        ItemStack item = sf != null ? Converter.getItem(
            sf.getItem(),
            "&a单击打开" + getTitle()
        ) : Converter.getItem(
            Material.MUSIC_DISC_5,
            "&a单击打开" + getTitle()
        );
        return Optional.of(item);
    }

    @Override
    public NamespacedKey getKey() {
        return key0();
    }

    @Override
    public String getTitle() {
        return "&aFALSE 配方补全配置";
    }

    @Override
    public int getSize() {
        return 18;
    }

    @Override
    public int[] getItemSlots() {
        return new int[]{9, 10, 11, 12};
    }

    @Override
    public int priority() {
        return Priorities.LogiTechFalseRecipeSettingsGuideOption;
    }
}
