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

import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NullMarked
public class RSCCustomMaterialGeneratorData extends MachineData {
    private final int tickRate;
    private final int per;
    private final List<ItemStack> generation;
    private final List<Integer> chances;
    private final boolean chooseOne;

    @Override
    public List<CERRecipeGroup.RecipeWrapper> wrap() {
        return List.of(new CERRecipeGroup.RecipeWrapper(
            null,
            generation.toArray(new ItemStack[0]),
            tickRate,
            (long) tickRate * per
        ));
    }
}
