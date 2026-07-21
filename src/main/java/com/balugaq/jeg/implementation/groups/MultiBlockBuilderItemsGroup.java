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

import com.balugaq.jeg.api.groups.MixedGroup;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.1
 */
@NotDisplayInSurvivalMode
@NullMarked
public class MultiBlockBuilderItemsGroup extends MixedGroup<MultiBlockBuilderItemsGroup> {
    public MultiBlockBuilderItemsGroup(final NamespacedKey key, final ItemStack icon) {
        super(key, icon);
    }
}
