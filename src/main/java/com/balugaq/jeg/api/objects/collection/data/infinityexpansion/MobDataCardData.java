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
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;

/**
 * @author balugaq
 * @since 1.9
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NullMarked
public class MobDataCardData extends MachineData {
    private final SlimefunItem chamber;
    private final int chamberEnergy;
    private final int chamberInterval;
    private final Map<ItemStack, Float> itemStackDoubleMap;

    @Override
    public List<CERRecipeGroup.RecipeWrapper> wrap() {
        return List.of(new CERRecipeGroup.RecipeWrapper(
            null,
            itemStackDoubleMap.keySet().toArray(new ItemStack[0]),
            chamberInterval,
            (long) chamberEnergy * chamberInterval
        ));
    }
}
