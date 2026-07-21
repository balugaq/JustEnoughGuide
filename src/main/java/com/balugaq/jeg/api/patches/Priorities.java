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

package com.balugaq.jeg.api.patches;

/**
 * @author balugaq
 * @since 2.1
 */
public class Priorities {
    public static int
    // Usually used
    GuideModeOption                               = 100,
    FireworksOption                               = 110,
    LearningAnimationOption                       = 120,
    KeybindsSettingsGuideOption                   = 130,

    // SlimeHUD
    HUDMachineInfoLocationGuideOption             = 200,
    HUDReachBlockGuideOption                      = 210,
    VanillaBlockHUDDisplayGuideOption             = 220,

    // Extra item information
    MachineStackableDisplayGuideOption            = 300,
    SlimefunIdDisplayGuideOption                  = 310,
    EMCValueDisplayGuideOption                    = 320,
    FinalTechValueDisplayGuideOption              = 330,
    FinalTECHValueDisplayGuideOption              = 340,

    // Recipe complete
    // 0. common
    RecipeCompleteOpenModeGuideOption             = 400,
    RecipeFillingWithNearbyContainerGuideOption   = 410,
    NoticeMissingMaterialGuideOption              = 420,
    RecursiveRecipeFillingGuideOption             = 430,
    // 1. Momotech
    MomotechNoneRecipeSettingsGuideOption         = 500,
    MomotechCreativeItemRecipeSettingsGuideOption = 510,
    MomotechQuantityItemRecipeSettingsGuideOption = 520,
    // 2. LogiTech
    LogiTechTrueRecipeSettingsGuideOption         = 600,
    LogiTechFalseRecipeSettingsGuideOption        = 610,

    // Rarely used
    ShareInGuideOption                            = 900,
    ShareOutGuideOption                           = 910,
    BeginnersGuideOption                          = 920,
    CerPathGuideOption                            = 930,
    PlayerLanguageOption                          = 940;
}