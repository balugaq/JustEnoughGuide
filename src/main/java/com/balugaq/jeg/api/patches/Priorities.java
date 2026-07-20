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