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

package com.balugaq.jeg.core.integrations.momotech;

import com.balugaq.jeg.implementation.option.ItemSettingsGuideOption;
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
@SuppressWarnings({"SameReturnValue"})
@NullMarked
public class MomotechCreativeItemRecipeSettingsGuideOption extends ItemSettingsGuideOption {
    public static final MomotechCreativeItemRecipeSettingsGuideOption instance = new MomotechCreativeItemRecipeSettingsGuideOption();
    public static final ItemStack DEFAULT_ITEM = new ItemStack(Material.COBBLESTONE);

    public static MomotechCreativeItemRecipeSettingsGuideOption instance() {
        return instance;
    }

    public static ItemStack getItem(Player player) {
        ItemStack itemStack = ItemSettingsGuideOption.getItem(player, key0(), 13);
        if (itemStack == null) return DEFAULT_ITEM;
        return itemStack;
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        ItemStack item = Converter.getItem(
                Material.END_CRYSTAL,
                "&a单击打开原始物质配方补全配置界面"
        );
        return Optional.of(item);
    }

    @Override
    public NamespacedKey getKey() {
        return key0();
    }

    public static NamespacedKey key0() {
        return KeyUtil.newKey("momotech_creative_item_recipe_settings");
    }

    @Override
    public String getTitle() {
        return "&a原始物质配方补全配置";
    }

    @Override
    public int getSize() {
        return 18;
    }

    @Override
    public int[] getItemSlots() {
        return new int[] {13};
    }
}
