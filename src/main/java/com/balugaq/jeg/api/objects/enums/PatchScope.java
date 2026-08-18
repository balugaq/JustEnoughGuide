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

import com.balugaq.jeg.api.objects.events.PatchEvent;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
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
    Action,
    SlimefunRecipeEdit;

    @Contract("_, null -> null")
    @Nullable
    public ItemStack patch(final PlayerProfile profile, @Nullable final ItemStack itemStack) {
        Player player = profile.getPlayer();
        if (player == null) {
            return itemStack;
        }
        return patch(player, itemStack);
    }

    public ItemStack patch(Player player, @Nullable ItemStack itemStack) {
        return PatchEvent.patch(this, player, itemStack);
    }
}
