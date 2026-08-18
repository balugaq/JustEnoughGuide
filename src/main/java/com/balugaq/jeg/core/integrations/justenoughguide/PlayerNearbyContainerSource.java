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
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.implementation.option.RecipeFillingWithNearbyContainerGuideOption;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.0
 */
@NullMarked
public interface PlayerNearbyContainerSource extends JEGSource {
    @Override
    default boolean handleable(RecipeCompleteSession session) {
        return RecipeFillingWithNearbyContainerGuideOption.getRadiusDistance(session.getPlayer()) > 0;
    }

    @Override
    @Nullable
    default ItemStack getItemStack(RecipeCompleteSession session, ItemStack itemStack) {
        return getItemStackFromNearbyContainer(session.getPlayer(), session.getTarget(), itemStack);
    }

    @Override
    default int handleLevel() {
        return RecipeCompleteProvider.PLAYER_NEARBY_CONTAINER_HANDLE_LEVEL;
    }
}
