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

package com.balugaq.jeg.api.objects.enums;

import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.objects.collection.Pair;
import com.balugaq.jeg.utils.LocalHelper;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"ConstantValue", "deprecation"})
@NullMarked
@Getter
public enum FilterType {
    BY_FULL_NAME(
        Flag.prefix("!!"),
        SearchGroup::isFullNameApplicable
    ),
    BY_RECIPE_ITEM_NAME(Set.of(Flag.prefix("#"), Flag.suffix("能做")), (player, item, filterValue, pinyin) -> {
        ItemStack[] recipe = item.getRecipe();
        if (recipe == null) {
            return false;
        }

        for (ItemStack itemStack : recipe) {
            if (SearchGroup.isSearchFilterApplicable(itemStack, filterValue, false)) {
                return true;
            }
        }

        return false;
    }),
    BY_RECIPE_TYPE_NAME(Flag.prefix("$"), (player, item, filterValue, pinyin) -> {
        ItemStack recipeTypeIcon = item.getRecipeType().getItem(player);
        if (recipeTypeIcon == null) {
            return false;
        }

        return SearchGroup.isSearchFilterApplicable(recipeTypeIcon, filterValue, false);
    }),
    BY_DISPLAY_ITEM_NAME(Set.of(Flag.prefix("%"), Flag.suffix("能产")), (player, item, filterValue, pinyin) -> {
        // ignore pinyin here, since it lags much more if pinyin is applied.

        // Use the pre-built name cache populated during SearchGroup.init().
        // This avoids calling getDisplayRecipes() at search time, which would
        // clone SlimefunItemStacks, construct CraftMetaSkull/CraftPlayerProfile
        // objects, and ultimately fire Mojang sessionserver HTTP requests.
        List<String> cached = SearchGroup.DISPLAY_ITEM_NAMES_CACHE.get(item.getId());
        if (cached != null) {
            for (String name : cached) {
                if (SearchGroup.isSearchFilterApplicable(name, filterValue, false)) return true;
            }
        }

        // SPECIAL_CACHE: addons may register extra searchable strings at runtime.
        String id = item.getId();

        Set<String> cache = SearchGroup.SPECIAL_CACHE.get(id);
        if (cache != null) {
            for (String s : cache) {
                if (SearchGroup.isSearchFilterApplicable(s, filterValue, false)) {
                    return true;
                }
            }
        }

        return false;
    }),
    BY_ADDON_NAME(Flag.prefix("@"), (player, item, filterValue, pinyin) -> {
        SlimefunAddon addon = item.getAddon();
        String localAddonName = LocalHelper.getAddonName(addon, item.getId()).toLowerCase(Locale.ROOT);
        String originModName = (addon == null ? "Slimefun" : addon.getName()).toLowerCase(Locale.ROOT);
        return localAddonName.contains(filterValue) || originModName.contains(filterValue);
    }),
    BY_ITEM_NAME(
        Flag.prefix("!"), SearchGroup::isSearchFilterApplicable
    ),
    BY_ITEM_LORE(
        Flag.prefix("^"), (player, item, filterValue, pinyin) -> {
        ItemMeta meta = item.getItem().getItemMeta();
        if (meta == null) return false;
        List<String> s = meta.getLore();
        if (s == null) return false;
        for (String lore : s) {
            if (SearchGroup.isSearchFilterApplicable(lore, filterValue, pinyin)) {
                return true;
            }
        }
        return false;
    }),
    BY_MATERIAL_NAME(
        Flag.prefix("~"),
        (player, item, filterValue, pinyin) ->
            item.getItem().getType().name().toLowerCase(Locale.ROOT).contains(filterValue)
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

    private final Set<Flag> flags;
    private final Filter filter;

    FilterType(Flag symbol, Filter filter) {
        this(Set.of(symbol), filter);
    }

    /**
     * Constructs a new FilterType instance with the specified flag and filter function.
     *
     * @param flags The flags that represent the filter type.
     * @param filter  The filter function to determine whether an item matches the filter.
     */
    FilterType(Set<Flag> flags, Filter filter) {
        this.flags = flags;
        this.filter = filter;
    }

    @Unmodifiable
    public static List<FilterType> lengthSortedValues() {
        return lengthSortedValues;
    }

    public static String quoteFlags(String str) {
        for (FilterType filterType : FilterType.values()) {
            for (var symbol : filterType.getSymbols()) {
                // Quote the flag to be used as a literal replacement
                str = str.replaceAll(Pattern.quote(symbol), Matcher.quoteReplacement(symbol));
            }
        }

        return str;
    }

    public Set<String> getSymbols() {
        return flags.stream().map(Flag::flag).collect(Collectors.toSet());
    }

    @Deprecated(forRemoval = true)
    public String getFlag() {
        return getSymbol();
    }

    @Deprecated(forRemoval = true)
    public String getSymbol() {
        return getFirstSymbol();
    }

    @ApiStatus.Obsolete
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public String getFirstSymbol() {
        return getSymbols().stream().findFirst().get();
    }

    public String apply(String raw) {
        return flags.stream().findFirst().get().apply(raw);
    }

    /**
     * @author balugaq
     * @since 1.1
     */
    @NullMarked
    @ApiStatus.Obsolete
    public interface Filter {
        boolean apply(Player player, SlimefunItem item, String filterValue, boolean pinyin);
    }

    /**
     * @author balugaq
     * @since 2.1
     */
    @NullMarked
    @ApiStatus.Obsolete
    public interface Flag {
        Type type();
        String flag();
        String apply(String raw);

        default int length() {
            return flag().length();
        }

        @ApiStatus.Obsolete
        static PrefixFlag prefix(String flag) {
            return new PrefixFlag(flag);
        }

        @ApiStatus.Obsolete
        static SuffixFlag suffix(String flag) {
            return new SuffixFlag(flag);
        }

        /**
         * @author balugaq
         * @since 2.1
         */
        @NullMarked
        @ApiStatus.Obsolete
        record PrefixFlag(String flag) implements Flag {
            @Override
            public Type type() {
                return Type.PREFIX;
            }

            @Override
            public String apply(String raw) {
                return flag + raw;
            }
        }

        /**
         * @author balugaq
         * @since 2.1
         */
        @NullMarked
        @ApiStatus.Obsolete
        record SuffixFlag(String flag) implements Flag {
            @Override
            public Type type() {
                return Type.PREFIX;
            }

            @Override
            public String apply(String raw) {
                return raw + flag;
            }
        }

        /**
         * @author balugaq
         * @since 2.1
         */
        @NullMarked
        @ApiStatus.Obsolete
        enum Type {
            PREFIX,
            SUFFIX
        }
    }
}
