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

package com.balugaq.jeg.implementation.items;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.groups.GroupSetup;
import com.balugaq.jeg.utils.Models;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.9
 */
@NullMarked
public class ItemsSetup {
    public static final RecipeCompleteGuide RECIPE_COMPLETE_GUIDE;
    public static final JEGSlimefunItem USAGE_INFO;
    public static final JEGSlimefunItem MECHANISM;
    public static final JEGSlimefunItem SUPPORTED_ADDONS_INFO;
    public static final CustomLagBlock CUSTOM_LAG_BLOCK;
    @SuppressWarnings("unused")
    @ApiStatus.Obsolete
    public static final JEGSlimefunItem JEG_BUTTON;

    static {
        ItemStack craftingTable = new ItemStack(Material.CRAFTING_TABLE);
        ItemStack book = new ItemStack(Material.BOOK);
        var NO_RECIPE = new @Nullable ItemStack[]{
            null, null, null,
            null, null, null,
            null, null, null
        };

        RECIPE_COMPLETE_GUIDE = new RecipeCompleteGuide(
            GroupSetup.jegItemsGroup,
            Models.RECIPE_COMPLETE_GUIDE,
            RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack[] {
                craftingTable, craftingTable, craftingTable,
                craftingTable, book, craftingTable,
                craftingTable, craftingTable, craftingTable
            }
        );

        USAGE_INFO = new JEGSlimefunItem(GroupSetup.jegItemsGroup, Models.USAGE_INFO, RecipeType.NULL, NO_RECIPE);

        MECHANISM = new JEGSlimefunItem(GroupSetup.jegItemsGroup, Models.MECHANISM, RecipeType.NULL, NO_RECIPE);

        SUPPORTED_ADDONS_INFO = new JEGSlimefunItem(GroupSetup.jegItemsGroup, Models.SUPPORTED_ADDONS_INFO, RecipeType.NULL, NO_RECIPE);

        JEG_BUTTON = new JEGSlimefunItem(GroupSetup.jegItemsGroup, Models.JEG_RECIPE_COMPLETE_BUTTON, RecipeType.NULL, NO_RECIPE);

        CUSTOM_LAG_BLOCK = new CustomLagBlock(GroupSetup.jegItemsGroup, Models.CUSTOM_LAG_BLOCK, RecipeType.NULL, NO_RECIPE);
    }

    public static void setup(SlimefunAddon addon) {
        boolean before = JustEnoughGuide.disableAutomaticallyLoadItems();
        RECIPE_COMPLETE_GUIDE.register(addon);
        USAGE_INFO.register(addon);
        MECHANISM.register(addon);
        SUPPORTED_ADDONS_INFO.register(addon);
        CUSTOM_LAG_BLOCK.register(addon);
        JustEnoughGuide.setAutomaticallyLoadItems(before);
    }
}
