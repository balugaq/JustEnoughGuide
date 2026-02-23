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

/**
 * @author balugaq
 * @since 1.9
 */
package com.balugaq.jeg.core.integrations.justenoughguide;

import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry;
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.option.BeginnersGuideOption;
import com.balugaq.jeg.implementation.option.CerPatchGuideOption;
import com.balugaq.jeg.implementation.option.KeybindsSettingsGuideOption;
import com.balugaq.jeg.implementation.option.NoticeMissingMaterialGuideOption;
import com.balugaq.jeg.implementation.option.RecipeCompleteOpenModeGuideOption;
import com.balugaq.jeg.implementation.option.RecipeFillingWithNearbyContainerGuideOption;
import com.balugaq.jeg.implementation.option.RecursiveRecipeFillingGuideOption;
import com.balugaq.jeg.implementation.option.ShareInGuideOption;
import com.balugaq.jeg.implementation.option.ShareOutGuideOption;
import com.balugaq.jeg.implementation.option.SlimefunIdDisplayGuideOption;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;

/**
 * @author balugaq
 * @since 2.1
 */
@NullMarked
public class JustEnoughGuideIntegrationMain implements Integration {
    @Override
    public String getHookPlugin() {
        return "JustEnoughGuide";
    }

    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    @Override
    public void onEnable() {
        RecipeCompleteProvider.addSource(new DefaultPlayerNearbyContainerRecipeCompleteSlimefunSource());
        RecipeCompleteProvider.addSource(new DefaultPlayerNearbyContainerRecipeCompleteVanillaSource());
        RecipeCompleteProvider.addSource(new DefaultPlayerInventoryRecipeCompleteSlimefunSource());
        RecipeCompleteProvider.addSource(new DefaultPlayerInventoryRecipeCompleteVanillaSource());

        if (!JustEnoughGuide.getConfigManager().isDisabledBundleInteraction()
                && MinecraftVersion.current().isAtLeast(MinecraftVersion.V1_17)) {
            RecipeCompletableRegistry.registerPlayerInventoryItemGetter(new BundlePlayerInventoryItemSeeker());
        }
        RecipeCompletableRegistry.registerPlayerInventoryItemGetter(new ShulkerBoxPlayerInventoryItemSeeker());

        try {
            ItemStack easterEgg = new CustomItemStack(
                    Material.GLOWSTONE_DUST,
                    "&6&l彩蛋",
                    "&6&l爱来自 JustEnoughGuide"
            );
            if (SlimefunItems.ELECTRIC_INGOT_FACTORY_2.getItem() instanceof AContainer ac) {
                ac.registerRecipe(114514, easterEgg, easterEgg);
            }
        } catch (Exception ignored) {
        }

        for (SlimefunItem slimefunItem : new ArrayList<>(Slimefun.getRegistry().getAllSlimefunItems())) {
            if (slimefunItem instanceof RecipeCompletableListener.NotApplicable) {
                RecipeCompletableRegistry.addNotApplicableItem(slimefunItem);
            }
        }

        if (JustEnoughGuide.getConfigManager().isBeginnerOption()) {
            Debug.log("正在加载指南选项...");
            JEGGuideSettings.patchSlimefun();
            if (JustEnoughGuide.getConfigManager().isSlimefunIdDisplay()) {
                JEGGuideSettings.addOption(SlimefunIdDisplayGuideOption.instance());
            }
            JEGGuideSettings.addOption(KeybindsSettingsGuideOption.instance());
            JEGGuideSettings.addOption(BeginnersGuideOption.instance());
            JEGGuideSettings.addOption(CerPatchGuideOption.instance());
            JEGGuideSettings.addOption(ShareInGuideOption.instance());
            JEGGuideSettings.addOption(ShareOutGuideOption.instance());
            JEGGuideSettings.addOption(RecursiveRecipeFillingGuideOption.instance());
            JEGGuideSettings.addOption(NoticeMissingMaterialGuideOption.instance());
            JEGGuideSettings.addOption(RecipeFillingWithNearbyContainerGuideOption.instance());
            JEGGuideSettings.addOption(RecipeCompleteOpenModeGuideOption.instance());
            Debug.log("指南选项加载完毕！");
        }
    }

    @Override
    public void onDisable() {
    }
}
