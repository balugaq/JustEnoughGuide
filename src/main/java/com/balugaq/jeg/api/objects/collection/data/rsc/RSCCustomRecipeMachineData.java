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

package com.balugaq.jeg.api.objects.collection.data.rsc;

import com.balugaq.jeg.api.groups.CERRecipeGroup;
import com.balugaq.jeg.api.objects.collection.data.MachineData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NullMarked
public class RSCCustomRecipeMachineData extends MachineData {
    private final List<RSCCustomMachineRecipe> recipes;
    private final int energyConsumption;
    private final int speed;

    @Override
    public List<CERRecipeGroup.RecipeWrapper> wrap() {
        return recipes.stream()
            .map(recipe -> new CERRecipeGroup.RecipeWrapper(
                recipe.getInput(),
                recipe.getOutput(),
                recipe.getTicks() / speed,
                (long) energyConsumption * recipe.getTicks() / speed
            ))
            .toList();
    }
}
