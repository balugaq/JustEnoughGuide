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

package com.balugaq.jeg.implementation.items;

import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.KeyUtil;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.SlimefunGuideItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

/**
 * @author balugaq
 * @since 1.8
 */
@SuppressWarnings("SameReturnValue")
public class GroupTierEditorGuide extends SlimefunGuideItem {
    public static final NamespacedKey KEY = KeyUtil.newKey("group_tier_editor");
    public static final GroupTierEditorGuide instance = new GroupTierEditorGuide();

    public GroupTierEditorGuide() {
        super(GuideUtil.getSlimefunGuide(SlimefunGuideMode.CHEAT_MODE), "&a物品组调位书");

        ItemMeta meta = getItemMeta();
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);
        setItemMeta(meta);
    }

    public static GroupTierEditorGuide instance() {
        return instance;
    }

    public static boolean isGroupTierEditor(@Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        return meta.getPersistentDataContainer().has(KEY, PersistentDataType.BOOLEAN);
    }
}
