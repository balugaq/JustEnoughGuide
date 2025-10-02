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

package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.core.integrations.alchimiavitae.AlchimiaVitaeIntegrationMain;
import com.balugaq.jeg.core.integrations.bedrocktechnology.BedrockTechnologyIntegrationMain;
import com.balugaq.jeg.core.integrations.claytech.ClayTechIntegrationMain;
import com.balugaq.jeg.core.integrations.cultivation.CultivationIntegrationMain;
import com.balugaq.jeg.core.integrations.danktech2.DankTech2IntegrationMain;
import com.balugaq.jeg.core.integrations.def.DefaultPlayerInventoryRecipeCompleteSlimefunSource;
import com.balugaq.jeg.core.integrations.def.DefaultPlayerInventoryRecipeCompleteVanillaSource;
import com.balugaq.jeg.core.integrations.elementmanipulation.ElementManipulationIntegrationMain;
import com.balugaq.jeg.core.integrations.emctech.EMCTechIntegrationMain;
import com.balugaq.jeg.core.integrations.fastmachines.FastMachinesIntegrationMain;
import com.balugaq.jeg.core.integrations.finaltechs.finalTECHChangedv3.FinalTECHChangedIntegrationMain;
import com.balugaq.jeg.core.integrations.finaltechs.finalTECHv2.FinalTECHIntegrationMain;
import com.balugaq.jeg.core.integrations.finaltechs.finaltechv1.FinalTechIntegrationMain;
import com.balugaq.jeg.core.integrations.fluffymachines.FluffyMachinesIntegrationMain;
import com.balugaq.jeg.core.integrations.galacitfun.GalactifunIntegrationMain;
import com.balugaq.jeg.core.integrations.gastronomicon.GastronomiconIntegrationMain;
import com.balugaq.jeg.core.integrations.infinitycompress.InfinityCompressIntegrationMain;
import com.balugaq.jeg.core.integrations.infinityexpansion.InfinityExpansionIntegrationMain;
import com.balugaq.jeg.core.integrations.infinityexpansion2.InfinityExpansion2IntegrationMain;
import com.balugaq.jeg.core.integrations.logitech.LogitechIntegrationMain;
import com.balugaq.jeg.core.integrations.magicexpansion.MagicExpansionIntegrationMain;
import com.balugaq.jeg.core.integrations.networks.NetworksIntegrationMain;
import com.balugaq.jeg.core.integrations.networksexpansion.NetworksExpansionIntegrationMain;
import com.balugaq.jeg.core.integrations.nexcavate.NexcavateIntegrationMain;
import com.balugaq.jeg.core.integrations.obsidianexpansion.ObsidianExpansionIntegrationMain;
import com.balugaq.jeg.core.integrations.rykenslimefuncustomizer.RykenSlimefunCustomizerIntegrationMain;
import com.balugaq.jeg.core.integrations.simpleutils.SimpleUtilsIntegrationMain;
import com.balugaq.jeg.core.integrations.slimeaeplugin.SlimeAEPluginIntegrationMain;
import com.balugaq.jeg.core.integrations.slimefuntranslation.SlimefunTranslationPluginIntegrationMain;
import com.balugaq.jeg.core.integrations.slimehud.SlimeHUDIntegrationMain;
import com.balugaq.jeg.core.integrations.slimetinker.SlimeTinkerIntegrationMain;
import com.balugaq.jeg.core.integrations.tsingshantechnology.TsingshanTechnologyIntegrationMain;
import com.balugaq.jeg.core.integrations.wildernether.WilderNetherIntegrationMain;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class is responsible for managing integrations with other plugins.
 *
 * @author balugaq
 * @since 1.2
 */
@SuppressWarnings({"unused", "LombokGetterMayBeUsed"})
@Getter
public class IntegrationManager extends AbstractManager {
    private final @NotNull List<Integration> integrations = new ArrayList<>();
    private final @NotNull JavaPlugin plugin;

    @Deprecated
    private final boolean hasRecipeCompletableWithGuide = false;

    private boolean enabledAlchimiaVitae;
    private boolean enabledBedrockTechnology;
    private boolean enabledClayTech;
    private boolean enabledClayTechFixed;
    private boolean enabledCultivation;
    private boolean enabledCMILib;
    private boolean enabledDankTech2;
    private boolean enabledElementManipulation;
    private boolean enabledEMCTech;
    private boolean enabledFastMachines;
    private boolean enabledFinalTech;
    private boolean enabledFinalTECH;
    private boolean enabledFinalTECH_Changed;
    private boolean enabledFluffyMachines;
    private boolean enabledGalactifun;
    private boolean enabledGastronomicon;
    private boolean enabledGuguSlimefunLib;
    private boolean enabledInfinityCompress;
    private boolean enabledInfinityExpansion;
    private boolean enabledInfinityExpansion2;
    private boolean enabledInfinityExpansion_Changed;
    private boolean enabledLogiTech;
    private boolean enabledMagicExpansion;
    private boolean enabledNetworks;
    private boolean enabledNetworksExpansion;
    private boolean enabledNexcavate;
    private boolean enabledObsidianExpansion;
    private boolean enabledOreWorkshop;
    private boolean enabledRykenSlimefunCustomizer;
    private boolean enabledSimpleUtils;
    private boolean enabledSlimeAEPlugin;
    private boolean enabledSlimeFrame;
    private boolean enabledSlimefunTranslation;
    private boolean enabledSlimeHUD;
    private boolean enabledSlimeHUDPlus;
    private boolean enabledSlimeTinker;
    private boolean enabledTsingshanTechnology;
    private boolean enabledTsingshanTechnology_Fixed;
    private boolean enabledWilderNether;

    public IntegrationManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        JustEnoughGuide
                .runLater(
                        () -> {
                            PluginManager pm = Bukkit.getPluginManager();
                            try {
                                Class.forName("com.ytdd9527.networksexpansion.implementation.ExpansionItems");
                                enabledNetworksExpansion = true;
                            } catch (ClassNotFoundException e) {
                                enabledNetworksExpansion = false;
                            }

                            this.enabledAlchimiaVitae = pm.isPluginEnabled("AlchimiaVitae");
                            this.enabledBedrockTechnology = pm.isPluginEnabled("BedrockTechnology");
                            this.enabledClayTechFixed = pm.isPluginEnabled("ClayTech-Fixed");
                            this.enabledClayTech = enabledClayTechFixed || pm.isPluginEnabled("ClayTech");
                            this.enabledCMILib = pm.isPluginEnabled("CMILib");
                            this.enabledCultivation = pm.isPluginEnabled("Cultivation");
                            this.enabledDankTech2 = pm.isPluginEnabled("DankTech2");
                            this.enabledElementManipulation = pm.isPluginEnabled("ElementManipulation");
                            this.enabledEMCTech = pm.isPluginEnabled("EMCTech");
                            this.enabledFastMachines = pm.isPluginEnabled("FastMachines");
                            this.enabledFinalTech = pm.isPluginEnabled("FinalTech") && classExists("io.taraxacum.finaltech.api.factory.ItemValueTable");
                            this.enabledFinalTECH_Changed = pm.isPluginEnabled("FinalTECH-Changed");
                            this.enabledFinalTECH = enabledFinalTECH_Changed
                                    || (pm.isPluginEnabled("FinalTECH") && classExists("io.taraxacum.libs.slimefun.dto.ItemValueTable"));
                            this.enabledFluffyMachines = pm.isPluginEnabled("FluffyMachines");
                            this.enabledGalactifun = pm.isPluginEnabled("Galactifun");
                            this.enabledGastronomicon = pm.isPluginEnabled("Gastronomicon");
                            this.enabledGuguSlimefunLib = pm.isPluginEnabled("GuguSlimefunLib");
                            this.enabledInfinityCompress = pm.isPluginEnabled("InfinityCompress");
                            this.enabledInfinityExpansion_Changed = pm.isPluginEnabled("InfinityExpansion-Changed");
                            this.enabledInfinityExpansion =
                                    enabledInfinityExpansion_Changed || pm.isPluginEnabled("InfinityExpansion");
                            this.enabledInfinityExpansion2 = pm.isPluginEnabled("InfinityExpansion2");
                            this.enabledLogiTech = pm.isPluginEnabled("LogiTech");
                            this.enabledMagicExpansion = pm.isPluginEnabled("magicexpansion");
                            this.enabledNetworks = enabledNetworksExpansion || pm.isPluginEnabled("Networks");
                            this.enabledNexcavate = pm.isPluginEnabled("Nexcavate");
                            this.enabledObsidianExpansion = pm.isPluginEnabled("ObsidianExpansion");
                            this.enabledOreWorkshop = pm.isPluginEnabled("OreWorkshop");
                            this.enabledRykenSlimefunCustomizer = pm.isPluginEnabled("RykenSlimefunCustomizer");
                            this.enabledSimpleUtils = pm.isPluginEnabled("SimpleUtils");
                            this.enabledSlimeAEPlugin = pm.isPluginEnabled("SlimeAEPlugin");
                            this.enabledSlimeHUDPlus = pm.isPluginEnabled("SlimeHUDPlus");
                            this.enabledSlimeHUD = enabledSlimeHUD || pm.isPluginEnabled("SlimeHUD");
                            this.enabledSlimeFrame = pm.isPluginEnabled("SlimeFrame");
                            this.enabledSlimefunTranslation = pm.isPluginEnabled("SlimefunTranslation");
                            this.enabledSlimeTinker = pm.isPluginEnabled("SlimeTinker");
                            this.enabledTsingshanTechnology_Fixed = pm.isPluginEnabled("TsingshanTechnology-Fixed");
                            this.enabledTsingshanTechnology = enabledTsingshanTechnology_Fixed || pm.isPluginEnabled("TsingshanTechnology");
                            this.enabledWilderNether = pm.isPluginEnabled("WilderNether");

                            addIntegration(enabledAlchimiaVitae, AlchimiaVitaeIntegrationMain::new);
                            addIntegration(enabledBedrockTechnology, BedrockTechnologyIntegrationMain::new);
                            addIntegration(enabledClayTech, ClayTechIntegrationMain::new);
                            addIntegration(enabledCultivation, CultivationIntegrationMain::new);
                            addIntegration(enabledDankTech2, DankTech2IntegrationMain::new);
                            addIntegration(enabledElementManipulation, ElementManipulationIntegrationMain::new);
                            addIntegration(enabledEMCTech, EMCTechIntegrationMain::new);
                            addIntegration(enabledFastMachines, FastMachinesIntegrationMain::new);
                            addIntegration(enabledFinalTech, FinalTechIntegrationMain::new);
                            addIntegration(enabledFinalTECH, FinalTECHIntegrationMain::new);
                            addIntegration(enabledFinalTECH_Changed, FinalTECHChangedIntegrationMain::new);
                            addIntegration(enabledFluffyMachines, FluffyMachinesIntegrationMain::new);
                            addIntegration(enabledGalactifun, GalactifunIntegrationMain::new);
                            addIntegration(enabledGastronomicon, GastronomiconIntegrationMain::new);
                            addIntegration(enabledInfinityCompress, InfinityCompressIntegrationMain::new);
                            addIntegration(enabledInfinityExpansion, InfinityExpansionIntegrationMain::new);
                            addIntegration(enabledInfinityExpansion2, InfinityExpansion2IntegrationMain::new);
                            addIntegration(enabledLogiTech, LogitechIntegrationMain::new);
                            addIntegration(enabledMagicExpansion, MagicExpansionIntegrationMain::new);
                            addIntegration(enabledNexcavate, NexcavateIntegrationMain::new);
                            addIntegration(enabledNetworks, NetworksIntegrationMain::new);
                            addIntegration(enabledNetworksExpansion, NetworksExpansionIntegrationMain::new);
                            addIntegration(enabledObsidianExpansion, ObsidianExpansionIntegrationMain::new);
                            addIntegration(enabledRykenSlimefunCustomizer, RykenSlimefunCustomizerIntegrationMain::new);
                            addIntegration(enabledSimpleUtils, SimpleUtilsIntegrationMain::new);
                            addIntegration(enabledSlimeAEPlugin, SlimeAEPluginIntegrationMain::new);
                            addIntegration(enabledSlimefunTranslation, SlimefunTranslationPluginIntegrationMain::new);
                            addIntegration(enabledSlimeHUD, SlimeHUDIntegrationMain::new);
                            addIntegration(enabledSlimeTinker, SlimeTinkerIntegrationMain::new);
                            addIntegration(enabledTsingshanTechnology, TsingshanTechnologyIntegrationMain::new);
                            addIntegration(enabledWilderNether, WilderNetherIntegrationMain::new);

                            startupIntegrations();

                            RecipeCompleteProvider.addSource(new DefaultPlayerInventoryRecipeCompleteSlimefunSource());
                            RecipeCompleteProvider.addSource(new DefaultPlayerInventoryRecipeCompleteVanillaSource());
                        },
                        1L);
    }

    public static boolean classExists(@NotNull String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void scheduleRun(@NotNull Runnable runnable) {
        JustEnoughGuide.runLater(runnable, 2L);
    }

    public static void scheduleRunAsync(@NotNull Runnable runnable) {
        JustEnoughGuide.runLaterAsync(runnable, 2L);
    }

    @Deprecated
    public boolean hasRecipeCompletableWithGuide() {
        return hasRecipeCompletableWithGuide;
    }

    private void startupIntegrations() {
        for (Integration integration : integrations) {
            plugin.getLogger().info("Hooking " + integration.getHookPlugin());
            try {
                integration.onEnable();
            } catch (Throwable e) {
                Debug.trace(e);
            }
        }
    }

    public void shutdownIntegrations() {
        for (Integration integration : integrations) {
            plugin.getLogger().info("Unhooking " + integration.getHookPlugin());
            try {
                integration.onDisable();
            } catch (Throwable e) {
                Debug.trace(e);
            }
        }
    }

    public boolean isEnabledFinalTech() {
        return enabledFinalTech;
    }

    public boolean isEnabledFinalTECH() {
        return enabledFinalTECH;
    }

    public boolean isEnabledFinalTECH_Changed() {
        return enabledFinalTECH_Changed;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Run addIntegration(boolean enabled, @NotNull Supplier<Integration> supplier) {
        if (enabled) {
            Integration integration = supplier.get();
            integrations.add(integration);
            return Run.success();
        } else {
            return Run.failure();
        }
    }

    @Data
    @RequiredArgsConstructor
    public static class Run implements Cloneable {
        public static final Run SUCCESS = new Run(true);
        public static final Run FAILURE = new Run(false);
        private final boolean success;

        public static Run success() {
            return SUCCESS.clone();
        }

        public static Run failure() {
            return FAILURE.clone();
        }

        public Run or(@NotNull Supplier<Run> callable) {
            if (!success) {
                return callable.get();
            } else {
                return this;
            }
        }

        @Override
        public Run clone() {
            try {
                return (Run) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
