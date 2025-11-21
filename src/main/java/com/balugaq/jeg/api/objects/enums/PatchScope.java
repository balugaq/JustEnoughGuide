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

package com.balugaq.jeg.api.objects.enums;

import com.balugaq.jeg.api.objects.events.PatchEvent;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
@NullMarked
public enum PatchScope {
    Background,
    Back,
    Settings,
    RealTimeSearch,
    Search,
    BookMark,
    ItemMark,
    ItemGroup,
    PreviousPage,
    NextPage,
    SlimefunItem,
    VanillaItem,
    ItemRecipeIngredient,
    ItemWiki,
    ItemRecipeType,
    ItemRecipeOut,
    BigRecipe,
    RecipeDisplay,
    FeatureDisplay,
    SettingsContributors,
    SlimefunVersion,
    SlimefunSourceCode,
    SlimefunWiki,
    GuideOption,
    AddonCount,
    UnofficialTips,
    UnknownFeature,
    Contributor,
    LockedItemGroup,
    NoPermission,
    LockedItem,
    BookMarkItem,
    ItemMarkItem,
    SearchItem,
    Cer,
    CerRecipe,
    CerRecipeInput,
    CerRecipeOutput,
    CerRecipeBorderInput,
    CerRecipeBorderOutput,
    CerRecipeBorderInputOutput,
    Research,
    KeybindsSet,
    SubKeybindsSet,
    Keybind,
    KeybindActionBorder,
    Action;

    public ItemStack patch(PlayerProfile profile, ItemStack itemStack) {
        Player player = profile.getPlayer();
        if (player == null) {
            return itemStack;
        }
        return patch(player, itemStack);
    }

    public ItemStack patch(Player player, ItemStack itemStack) {
        return PatchEvent.patch(this, player, itemStack);
    }
}
