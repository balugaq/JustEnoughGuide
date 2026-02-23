/*
 * Copyright (c) 2024-2026 balugaq
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

package com.balugaq.jeg.api.objects.enums;

import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.objects.collection.Pair;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.LocalHelper;
import com.balugaq.jeg.utils.SpecialMenuProvider;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"ConstantValue", "deprecation"})
@Getter
public enum FilterType {
    BY_FULL_NAME(
            "!!",
            SearchGroup::isFullNameApplicable
    ),
    BY_RECIPE_ITEM_NAME(
            Set.of("#", "能做"), (player, item, lowerFilterValue, pinyin) -> {
        ItemStack[] recipe = item.getRecipe();
        if (recipe == null) {
            return false;
        }

        for (ItemStack itemStack : recipe) {
            if (SearchGroup.isSearchFilterApplicable(itemStack, lowerFilterValue, false)) {
                return true;
            }
        }

        return false;
    }
    ),
    BY_RECIPE_TYPE_NAME(
            "$", (player, item, lowerFilterValue, pinyin) -> {
        ItemStack recipeTypeIcon = item.getRecipeType().getItem(player);
        if (recipeTypeIcon == null) {
            return false;
        }

        return SearchGroup.isSearchFilterApplicable(recipeTypeIcon, lowerFilterValue, false);
    }
    ),
    BY_DISPLAY_ITEM_NAME(
            Set.of("%", "产"),
            (player, item, lowerFilterValue, pinyin) -> {
                List<ItemStack> display = null;
                if (item instanceof AContainer ac) {
                    // Fix: Cannot search item when SlimeCustomizer crashed
                    try {
                        display = ac.getDisplayRecipes();
                    } catch (Exception ignored) {
                    }
                } else if (item instanceof MultiBlockMachine mb) {
                    // Fix: NullPointerException occurred when searching items from SlimeFood
                    try {
                        display = mb.getDisplayRecipes();
                    } catch (Exception ignored) {
                    }
                } else {
                    try {
                        if (SpecialMenuProvider.ENABLED_LogiTech
                                && SpecialMenuProvider.classLogiTech_CustomSlimefunItem != null
                                && SpecialMenuProvider.classLogiTech_CustomSlimefunItem.isInstance(item)
                                && item instanceof RecipeDisplayItem rdi) {
                            display = rdi.getDisplayRecipes();
                        }
                    } catch (Exception e) {
                        Debug.trace(e, "searching");
                        return false;
                    }
                }
                if (display != null) {
                    try {
                        for (ItemStack itemStack : display) {
                            if (SearchGroup.isSearchFilterApplicable(itemStack, lowerFilterValue, false)) {
                                return true;
                            }
                        }
                    } catch (Exception ignored) {
                        return false;
                    }
                }

                String id = item.getId();
                Reference<Set<String>> ref = SearchGroup.SPECIAL_CACHE.get(id);
                if (ref != null) {
                    Set<String> cache = ref.get();
                    if (cache != null) {
                        for (String s : cache) {
                            if (SearchGroup.isSearchFilterApplicable(s, lowerFilterValue, false)) {
                                return true;
                            }
                        }
                    }
                }

                return false;
            }
    ),
    BY_ADDON_NAME(
            "@", (player, item, lowerFilterValue, pinyin) -> {
        SlimefunAddon addon = item.getAddon();
        String localAddonName = LocalHelper.getAddonName(addon, item.getId()).toLowerCase();
        String originModName = (addon == null ? "Slimefun" : addon.getName()).toLowerCase();
        return localAddonName.contains(lowerFilterValue) || originModName.contains(lowerFilterValue);
    }
    ),
    BY_ITEM_NAME(
            "!", SearchGroup::isSearchFilterApplicable
    ),
    BY_ITEM_LORE(
            "^", (player, item, lowerFilterValue, pinyin) -> {
        ItemMeta meta = item.getItem().getItemMeta();
        if (meta == null) return false;
        List<String> s = meta.getLore();
        if (s == null) return false;
        for (String lore : s) {
            if (SearchGroup.isSearchFilterApplicable(lore, lowerFilterValue, pinyin)) {
                return true;
            }
        }
        return false;
    }
    ),
    BY_MATERIAL_NAME(
            "~",
            (player, item, lowerFilterValue, pinyin) -> item.getItem().getType().name().toLowerCase().contains(lowerFilterValue)
    );

    @Unmodifiable
    private static final List<FilterType> lengthSortedValues;

    static {
        lengthSortedValues = Arrays.stream(values())
                .map(type -> {
                    List<Pair<String, FilterType>> list = new ArrayList<>();
                    for (var symbol : type.getSymbols()) {
                        list.add(new Pair<>(symbol, type));
                    }
                    return list;
                })
                .flatMap(Collection::stream)
                .sorted(Comparator.comparingInt(e -> -e.first.length()))
                .map(type -> type.second)
                .toList();
    }

    private final Set<String> symbols;
    private final DiFunction<Player, SlimefunItem, String, Boolean, Boolean> filter;

    FilterType(String symbol, DiFunction<Player, SlimefunItem, String, Boolean, Boolean> filter) {
        this(Set.of(symbol), filter);
    }

    /**
     * Constructs a new FilterType instance with the specified flag and filter function.
     *
     * @param symbols
     *         The string symbols that represent the filter type.
     * @param filter
     *         The filter function to determine whether an item matches the filter.
     */
    FilterType(Set<String> symbols, DiFunction<Player, SlimefunItem, String, Boolean, Boolean> filter) {
        this.symbols = symbols;
        this.filter = filter;
    }

    @Unmodifiable
    public static List<FilterType> lengthSortedValues() {
        return lengthSortedValues;
    }

    @Deprecated(forRemoval = true)
    public String getFlag() {
        return getSymbol();
    }

    @Deprecated(forRemoval = true)
    public String getSymbol() {
        return getFirstSymbol();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public String getFirstSymbol() {
        return getSymbols().stream().findFirst().get();
    }

    public interface DiFunction<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);
    }
}
