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

package com.balugaq.jeg.core.integrations.momotech;

import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.implementation.option.ItemSettingsGuideOption;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings({"SameReturnValue", "deprecation"})
@NullMarked
public class MomotechNoneRecipeSettingsGuideOption extends ItemSettingsGuideOption {
    public static final MomotechNoneRecipeSettingsGuideOption instance = new MomotechNoneRecipeSettingsGuideOption();

    public static MomotechNoneRecipeSettingsGuideOption instance() {
        return instance;
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        ItemStack item = Converter.getItem(
                Material.BLACK_WOOL,
                "&a单击打开NONE配方补全配置界面"
        );
        return Optional.of(item);
    }

    @Override
    public NamespacedKey getKey() {
        return key0();
    }

    public static NamespacedKey key0() {
        return KeyUtil.newKey("momotech_none_recipe_settings");
    }

    @Override
    public ChestMenu getMenu(Player p) {
        ChestMenu menu = new ChestMenu("&aNONE配方补全配置", 18);
        for (int i = 0; i < 9; i++) {
            menu.addItem(i, PatchScope.Background.patch(p, ChestMenuUtils.getBackground()), ChestMenuUtils.getEmptyClickHandler());
        }
        menu.addMenuClickHandler(
                1, (pl, s, is, action) -> EventUtil.callEvent(
                                new GuideEvents.BackButtonClickEvent(pl, is, s, action, menu, GuideUtil.getLastGuide(pl)))
                        .ifSuccess(() -> {
                            PlayerProfile playerProfile = PlayerProfile.find(pl).orElse(null);
                            if (playerProfile == null) {
                                return false;
                            }
                            GuideHistory guideHistory = playerProfile.getGuideHistory();
                            if (action.isShiftClicked()) {
                                SlimefunGuide.openMainMenu(
                                        playerProfile, GuideUtil.getLastGuide(pl).getMode(), guideHistory.getMainMenuPage());
                            } else {
                                GuideUtil.goBack(guideHistory);
                            }
                            return false;
                        })
        );
        return menu;
    }

    @Override
    public int[] getItemSlots() {
        return new int[] {9, 10, 11, 12, 13, 14, 15, 16, 17};
    }

    public static @Nullable ItemStack[] getItems(Player player) {
        @Nullable ItemStack[] items = new ItemStack[9];
        for (int i = 9; i < 18; i++) {
            ItemStack itemStack = ItemSettingsGuideOption.getItem(player, key0(), i);
            items[i - 9] = itemStack;
        }
        return items;
    }
}
