/*
 * Copyright (c) 2024-2025 balugaq
 *
 * This file is part of JustEnoughGuide, available under MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The author's name (balugaq or 大香蕉) and project name (JustEnoughGuide or JEG) shall not be
 *   removed or altered from any source distribution or documentation.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.balugaq.jeg.api.objects.collection.data.rsc;

import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@Getter
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
            IntList noConsumeIndexes) {
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
