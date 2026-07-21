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

package com.balugaq.jeg.core.integrations.momotech;

import com.balugaq.jeg.api.patches.Priorities;
import com.balugaq.jeg.implementation.option.AbstractItemSettingsGuideOption;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
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
@SuppressWarnings({"SameReturnValue", "unused"})
@NullMarked
public class MomotechQuantityItemRecipeSettingsGuideOption extends AbstractItemSettingsGuideOption {
    public static final MomotechQuantityItemRecipeSettingsGuideOption instance = new MomotechQuantityItemRecipeSettingsGuideOption();
    public static final ItemStack DEFAULT_ITEM = new ItemStack(Material.COBBLESTONE);

    public static MomotechQuantityItemRecipeSettingsGuideOption instance() {
        return instance;
    }

    public static ItemStack getItem(Player player) {
        ItemStack itemStack = AbstractItemSettingsGuideOption.getItem(player, key0(), 13);
        if (itemStack == null) return DEFAULT_ITEM;
        return itemStack;
    }

    public static NamespacedKey key0() {
        return KeyUtil.newKey("momotech_quantity_item_recipe_settings");
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        ItemStack item = Converter.getItem(
            Material.PURPLE_DYE,
            "&a单击打开量化插件配方补全配置界面"
        );
        return Optional.of(item);
    }

    @Override
    public NamespacedKey getKey() {
        return key0();
    }

    @Override
    public String getTitle() {
        return "&a量化插件配方补全配置";
    }

    @Override
    public int getSize() {
        return 18;
    }

    @Override
    public int[] getItemSlots() {
        return new int[]{13};
    }

    @Override
    public int priority() {
        return Priorities.MomotechQuantityItemRecipeSettingsGuideOption;
    }
}
