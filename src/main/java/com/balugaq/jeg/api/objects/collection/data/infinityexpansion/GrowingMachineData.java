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

package com.balugaq.jeg.api.objects.collection.data.infinityexpansion;

import com.balugaq.jeg.api.groups.CERRecipeGroup;
import com.balugaq.jeg.api.objects.collection.data.MachineData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.EnumMap;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NullMarked
public class GrowingMachineData extends MachineData {
    private final EnumMap<Material, ItemStack[]> recipes;
    private final int ticksPerOutput;
    private final int energyPerTick;

    @Override
    public List<CERRecipeGroup.RecipeWrapper> wrap() {
        return recipes.entrySet().stream()
            .map(entry -> new CERRecipeGroup.RecipeWrapper(
                new ItemStack[]{new ItemStack(entry.getKey())},
                entry.getValue(),
                ticksPerOutput,
                (long) ticksPerOutput * energyPerTick
            ))
            .toList();
    }
}
