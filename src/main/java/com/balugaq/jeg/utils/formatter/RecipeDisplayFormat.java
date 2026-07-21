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

package com.balugaq.jeg.utils.formatter;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.6
 */
@NullMarked
public class RecipeDisplayFormat extends Format {
    public static List<Integer> fenceShuffle(List<Integer> list) {
        int size = list.size();
        int splitPoint = (size + 1) / 2;

        List<Integer> firstHalf = new ArrayList<>(list.subList(0, splitPoint));
        List<Integer> secondHalf = new ArrayList<>(list.subList(splitPoint, size));

        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < secondHalf.size(); i++) {
            result.add(firstHalf.get(i));
            result.add(secondHalf.get(i));
        }

        if (firstHalf.size() > secondHalf.size()) {
            result.add(firstHalf.get(firstHalf.size() - 1));
        }

        return result;
    }

    @Override
    public void loadMapping() {
        loadMapping(JustEnoughGuide.getConfigManager().getRecipeDisplayFormat());
    }
}
