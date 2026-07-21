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

package com.balugaq.jeg.implementation.groups;

import com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry;
import com.balugaq.jeg.core.managers.IntegrationManager;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.SlimefunRegistryUtil;

/**
 * This class is responsible for registering all the JEG groups.
 *
 * @author balugaq
 * @since 1.3
 */
public class GroupSetup {
    public static JEGGuideGroup guideGroup;
    public static HiddenItemsGroup hiddenItemsGroup;
    public static VanillaItemsGroup vanillaItemsGroup;
    public static ReplacementCardsGroup replacementCardsGroup;
    public static RecipeCompletableGroup recipeCompletableGroup;
    public static BannedItemsGroup bannedItemGroup;
    public static JEGItemsGroup jegItemsGroup;
    public static MultiBlockBuilderItemsGroup multiBlockBuilderItemsGroup;

    /**
     * Registers all the JEG groups.
     */
    public static void setup() {
        guideGroup = new JEGGuideGroup(KeyUtil.newKey("jeg_guide_group"), Models.JEG_GUIDE_GROUP);
        guideGroup.setTier(Integer.MAX_VALUE);
        guideGroup.register(JustEnoughGuide.getInstance());

        hiddenItemsGroup = new HiddenItemsGroup(KeyUtil.newKey("hidden_items_group"), Models.HIDDEN_ITEMS_GROUP);
        hiddenItemsGroup.setTier(Integer.MAX_VALUE);
        hiddenItemsGroup.register(JustEnoughGuide.getInstance());

        vanillaItemsGroup = new VanillaItemsGroup(KeyUtil.newKey("vanilla_items_group"), Models.VANILLA_ITEMS_GROUP);
        vanillaItemsGroup.setTier(Integer.MAX_VALUE);
        vanillaItemsGroup.register(JustEnoughGuide.getInstance());

        replacementCardsGroup = new ReplacementCardsGroup(KeyUtil.newKey("replacement_cards_group"), Models.REPLACEMENT_CARDS_GROUP);
        replacementCardsGroup.setTier(Integer.MAX_VALUE);
        replacementCardsGroup.register(JustEnoughGuide.getInstance());

        recipeCompletableGroup = new RecipeCompletableGroup(KeyUtil.newKey("recipe_completable_group"), Models.RECIPE_COMPLETABLE_GROUP);
        recipeCompletableGroup.setTier(Integer.MAX_VALUE);
        recipeCompletableGroup.register(JustEnoughGuide.getInstance());

        bannedItemGroup = new BannedItemsGroup(KeyUtil.newKey("banned_items_group"), Models.BANNED_ITEMS_GROUP);
        bannedItemGroup.setTier(Integer.MAX_VALUE);
        bannedItemGroup.register(JustEnoughGuide.getInstance());

        jegItemsGroup = new JEGItemsGroup(KeyUtil.newKey("jeg_items_group"), Models.JEG_ITEMS_GROUP);
        jegItemsGroup.setTier(Integer.MAX_VALUE);
        jegItemsGroup.register(JustEnoughGuide.getInstance());

        multiBlockBuilderItemsGroup = new MultiBlockBuilderItemsGroup(KeyUtil.newKey("multi_block_builder_items_group"), Models.MULTI_BLOCK_BUILDER_ITEMS_GROUP);
        multiBlockBuilderItemsGroup.setTier(Integer.MAX_VALUE);
        multiBlockBuilderItemsGroup.register(JustEnoughGuide.getInstance());

        IntegrationManager.scheduleRun(() ->
            RecipeCompletableRegistry.getAllRecipeCompletableBlocks().forEach(block ->
                recipeCompletableGroup.addItem(block.getItem())
            )
        );
    }

    /**
     * Unregisters all the JEG groups.
     */
    public static void shutdown() {
        SlimefunRegistryUtil.unregisterItemGroups(JustEnoughGuide.getInstance());
    }
}
