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
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NullMarked
public class RSCCustomTemplateMachineData extends MachineData {
    private final boolean fasterIfMoreTemplates;
    private final boolean moreOutputIfMoreTemplates;
    private final int consumption;
    private final List<RSCMachineTemplate> templates;

    @Override
    public List<CERRecipeGroup.RecipeWrapper> wrap() {
        List<CERRecipeGroup.RecipeWrapper> wrappers = new ArrayList<>();

        for (RSCMachineTemplate template : templates) {
            for (RSCCustomMachineRecipe recipe : template.recipes()) {
                wrappers.add(new CERRecipeGroup.RecipeWrapper(
                    new ArrayList<>(List.of(recipe.getInput())) {{
                        add(template.template());
                    }}.toArray(new ItemStack[0]),
                    recipe.getOutput(),
                    recipe.getTicks(),
                    (long) recipe.getTicks() * consumption
                ));
            }
        }

        return wrappers;
    }
}
