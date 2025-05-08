package com.balugaq.jeg.api.objects.enums;

import com.balugaq.jeg.api.groups.SearchGroup;
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

import javax.annotation.Nonnull;
import java.lang.ref.Reference;
import java.util.List;
import java.util.Set;

@Getter
public enum FilterType {
    BY_RECIPE_ITEM_NAME("#", (player, item, lowerFilterValue, pinyin) -> {
        ItemStack[] recipe = item.getRecipe();
        if (recipe == null) {
            return false;
        }

        for (ItemStack itemStack : recipe) {
            if (SearchGroup.isSearchFilterApplicable(player, itemStack, lowerFilterValue, false)) {
                return true;
            }
        }

        return false;
    }),
    BY_RECIPE_TYPE_NAME("$", (player, item, lowerFilterValue, pinyin) -> {
        ItemStack recipeTypeIcon = item.getRecipeType().getItem(player);
        if (recipeTypeIcon == null) {
            return false;
        }

        return SearchGroup.isSearchFilterApplicable(player, recipeTypeIcon, lowerFilterValue, false);
    }),
    BY_DISPLAY_ITEM_NAME("%", (player, item, lowerFilterValue, pinyin) -> {
        List<ItemStack> display = null;
        if (item instanceof AContainer ac) {
            display = ac.getDisplayRecipes();
        } else if (item instanceof MultiBlockMachine mb) {
            // Fix: Fix NullPointerException occurred when searching items from SlimeFood
            try {
                display = mb.getDisplayRecipes();
            } catch (Throwable e) {
                Debug.trace(e, "searching");
                return false;
            }
        } else {
            try {
                if (SpecialMenuProvider.ENABLED_LogiTech && SpecialMenuProvider.classLogiTech_CustomSlimefunItem != null && SpecialMenuProvider.classLogiTech_CustomSlimefunItem.isInstance(item)) {
                    RecipeDisplayItem csi = (RecipeDisplayItem) item;
                    display = csi.getDisplayRecipes();
                }
            } catch (Throwable e) {
                Debug.trace(e, "searching");
                return false;
            }
        }
        if (display != null) {
            try {
                for (ItemStack itemStack : display) {
                    if (SearchGroup.isSearchFilterApplicable(player, itemStack, lowerFilterValue, false)) {
                        return true;
                    }
                }
            } catch (Throwable ignored) {
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
    }),
    BY_ADDON_NAME("@", (player, item, lowerFilterValue, pinyin) -> {
        SlimefunAddon addon = item.getAddon();
        String localAddonName = LocalHelper.getAddonName(addon, item.getId()).toLowerCase();
        String originModName = (addon == null ? "Slimefun" : addon.getName()).toLowerCase();
        if (localAddonName.contains(lowerFilterValue) || originModName.contains(lowerFilterValue)) {
            return true;
        }
        return false;
    }),
    BY_ITEM_NAME("!", (player, item, lowerFilterValue, pinyin) -> {
        if (SearchGroup.isSearchFilterApplicable(player, item, lowerFilterValue, pinyin)) {
            return true;
        }
        return false;
    }),
    BY_ITEM_NAME("!", (player, item, lowerFilterValue, pinyin) -> SearchGroup.isSearchFilterApplicable(item, lowerFilterValue, pinyin)),
    BY_MATERIAL_NAME("~", (player, item, lowerFilterValue, pinyin) -> item.getItem().getType().name().toLowerCase().contains(lowerFilterValue));

    private @Nonnull
    final String symbol;
    private @Nonnull
    final DiFunction<Player, SlimefunItem, String, Boolean, Boolean> filter;

    /**
     * Constructs a new FilterType instance with the specified flag and filter function.
     *
     * @param symbol The string symbol of the filter type.
     * @param filter The filter function to determine whether an item matches the filter.
     */
    FilterType(@Nonnull String symbol, @Nonnull DiFunction<Player, SlimefunItem, String, Boolean, Boolean> filter) {
        this.symbol = symbol;
        this.filter = filter;
    }

    @Deprecated
    public String getFlag() {
        return symbol;
    }

    public interface DiFunction<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);
    }
}
