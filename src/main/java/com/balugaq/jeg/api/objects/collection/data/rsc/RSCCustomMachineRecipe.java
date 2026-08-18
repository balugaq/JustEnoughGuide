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

import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@Getter
@NullMarked
public class RSCCustomMachineRecipe extends MachineRecipe {
    private final List<Integer> chances;
    private final IntList noConsume; // probably not exist, empty list by default

    private final boolean chooseOneIfHas;
    private final boolean forDisplay;
    private final boolean hide;

    public RSCCustomMachineRecipe(
        int seconds,
        ItemStack[] input,
        ItemStack[] output,
        List<Integer> chances,
        boolean chooseOneIfHas,
        boolean forDisplay,
        boolean hide,
        @Nullable IntList noConsumeIndexes) {
        super(seconds, input.clone(), output.clone());

        this.chances = chances;
        this.chooseOneIfHas = chooseOneIfHas;
        this.forDisplay = forDisplay;
        this.hide = hide;
        if (noConsumeIndexes == null) {
            this.noConsume = IntList.of();
        } else {
            this.noConsume = noConsumeIndexes;
        }
    }
}
