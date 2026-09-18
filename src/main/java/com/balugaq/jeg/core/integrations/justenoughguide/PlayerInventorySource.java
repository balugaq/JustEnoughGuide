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

package com.balugaq.jeg.core.integrations.justenoughguide;

import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.api.recipe_complete.source.RecipeCompleteProvider;
import com.balugaq.jeg.utils.ReflectionUtil;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.0
 */
@NullMarked
public interface PlayerInventorySource extends JEGSource {
    @Override
    default boolean handleable(RecipeCompleteSession session) {
        // Always available
        return true;
    }

    @Override
    @NonNegative
    default long getItemStack(RecipeCompleteSession session, ItemStack itemStack, long need) {
        return ReflectionUtil.getItemStackFromPlayerInventory(session, itemStack, need);
    }

    @Override
    @NonNegative
    default long countAmount(RecipeCompleteSession session, ItemStack template) {
        return ReflectionUtil.countAmountFromPlayerInventory(session, template);
    }

    @Override
    default int handleLevel() {
        return RecipeCompleteProvider.PLAYER_INVENTORY_HANDLE_LEVEL;
    }
}
