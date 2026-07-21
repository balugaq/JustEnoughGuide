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
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author balugaq
 * @since 2.1
 */
@NullMarked
public class JustEnoughGuideIntegrationMain implements Integration {
    @Override
    public String getHookPlugin() {
        return JustEnoughGuide.getInstance().getRepo();
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

        Debug.log("正在加载指南选项...");
        JEGGuideSettings.patchSlimefun();
        if (JustEnoughGuide.getConfigManager().isSlimefunIdDisplay()) {
            JEGGuideSettings.addOption(SlimefunIdDisplayGuideOption.instance());
        }
        JEGGuideSettings.addOption(KeybindsSettingsGuideOption.instance());
        if (JustEnoughGuide.getConfigManager().isBeginnerOption()) {
            JEGGuideSettings.addOption(BeginnersGuideOption.instance());
        }
        JEGGuideSettings.addOption(CerPatchGuideOption.instance());
        JEGGuideSettings.addOption(ShareInGuideOption.instance());
        JEGGuideSettings.addOption(ShareOutGuideOption.instance());
        JEGGuideSettings.addOption(RecursiveRecipeFillingGuideOption.instance());
        JEGGuideSettings.addOption(NoticeMissingMaterialGuideOption.instance());
        JEGGuideSettings.addOption(RecipeFillingWithNearbyContainerGuideOption.instance());
        JEGGuideSettings.addOption(RecipeCompleteOpenModeGuideOption.instance());
        Debug.log("指南选项加载完毕！");

        if (JustEnoughGuide.getConfigManager().isAutoAddRecipeCompleteButton()) {
            Debug.log("正在自动添加 JustEnoughGuide 配方补全按钮");
            Debug.debug("Added RecipeComplete Buttons at: ");
            int count = 0;
            for (var entry : new HashMap<>(Slimefun.getRegistry().getMenuPresets()).entrySet()) {
                BlockMenuPreset preset = entry.getValue();

                SlimefunItem sf = SlimefunItem.getById((preset.getID()));
                if (sf == null || !RecipeCompletableRegistry.getAllRecipeCompletableBlocks().contains(sf)) {
                    continue;
                }

                if (JustEnoughGuide.getConfigManager().getNoAutoAddRecipeCompleteBlocks().contains(sf.getId())
                    || JustEnoughGuide.getConfigManager().getNoAutoAddRecipeCompleteAddons().contains(sf.getAddon().getName())) {
                    continue;
                }

                new JEGBlockMenuPreset(preset);
                Debug.debug(sf);

                count++;
            }
            Debug.log("已为 " + count + " 个机器添加配方补全按钮");
        }
    }

    @Override
    public void onDisable() {
    }
}
