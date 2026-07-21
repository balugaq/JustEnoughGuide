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

package com.balugaq.jeg.implementation.guide;

import com.balugaq.jeg.api.editor.GroupResorter;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.utils.GuideUtil;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.SlimefunGuideItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings({"deprecation", "unused"})
@NullMarked
public class SurvivalGuideImplementation extends SurvivalSlimefunGuide implements JEGSlimefunGuideImplementation {
    public final ItemStack item;

    public SurvivalGuideImplementation() {
        ItemMeta meta = SlimefunGuide.getItem(getMode()).getItemMeta();
        String name = "";
        if (meta != null) {
            name = meta.getDisplayName();
        }
        item = new SlimefunGuideItem(this, name);
    }

    // fallback
    @Deprecated
    public SurvivalGuideImplementation(boolean v1, boolean v2) {
        this();
    }

    @Override
    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    public List<ItemGroup> getVisibleItemGroups(Player p, PlayerProfile profile) {
        return GuideUtil.getVisibleItemGroupsSurvival(p, profile, GroupResorter.isSelecting(p));
    }

    @Override
    public void openMainMenu(PlayerProfile profile, int page) {
        JEGSlimefunGuideImplementation.super.openMainMenu(profile, page);
    }

    @Override
    public void openItemGroup(PlayerProfile profile, ItemGroup itemGroup, int page) {
        JEGSlimefunGuideImplementation.super.openItemGroup(profile, itemGroup, page);
    }

    @Override
    public void openSearch(PlayerProfile profile, String input, boolean addToHistory) {
        JEGSlimefunGuideImplementation.super.openSearch(profile, input, addToHistory);
    }

    @Override
    public void displayItem(PlayerProfile profile, @Nullable ItemStack item, int index, boolean addToHistory) {
        JEGSlimefunGuideImplementation.super.displayItem(profile, item, index, addToHistory);
    }

    @Override
    public void displayItem(PlayerProfile profile, SlimefunItem item, boolean addToHistory) {
        JEGSlimefunGuideImplementation.super.displayItem(profile, item, addToHistory);
    }

    @Override
    public void createHeader(Player p, PlayerProfile profile, ChestMenu menu) {
        JEGSlimefunGuideImplementation.super.createHeader(p, profile, menu);
    }

    @Override
    public SlimefunGuideMode getMode() {
        return SlimefunGuideMode.SURVIVAL_MODE;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }
}
