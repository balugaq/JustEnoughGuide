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

package com.balugaq.jeg.implementation.groups;

import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

/**
 * @author balugaq
 * @since 2.1
 */
@NotDisplayInSurvivalMode
public class ReplacementCardsGroup extends ItemGroup {
    public ReplacementCardsGroup(NamespacedKey key, ItemStack item) {
        super(key, item);
    }
}
