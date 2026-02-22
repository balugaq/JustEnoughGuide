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

package com.balugaq.jeg.core.integrations.logitech;

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
public class LogitechFalseRecipeSettingsGuideOption extends AbstractItemSettingsGuideOption {
    public static final LogitechFalseRecipeSettingsGuideOption instance = new LogitechFalseRecipeSettingsGuideOption();

    public static LogitechFalseRecipeSettingsGuideOption instance() {
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

    public static NamespacedKey key0() {
        return KeyUtil.newKey("logitech_false_recipe_settings");
    }

    @Override
    public String getTitle() {
        return "&aFALSE配方补全配置";
    }

    @Override
    public int getSize() {
        return 18;
    }

    @Override
    public int[] getItemSlots() {
        return new int[] {9, 10, 11, 12};
    }
}
