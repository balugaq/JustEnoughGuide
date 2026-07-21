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

package com.balugaq.jeg.core.integrations.nexcavate;

import com.balugaq.jeg.api.groups.BaseGroup;
import com.balugaq.jeg.api.interfaces.DisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.balugaq.jeg.utils.clickhandler.OnDisplay;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * This class used to create groups to display all the Nexcavate items in the guide.
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"deprecation", "unused"})
@DisplayInCheatMode
@NotDisplayInSurvivalMode
@NullMarked
public class NexcavateItemsGroup extends BaseGroup<NexcavateItemsGroup> {
    private final List<SlimefunItem> slimefunItemList;

    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    public NexcavateItemsGroup(NamespacedKey key, ItemStack icon) {
        super(key, icon);
        this.page = 1;
        List<SlimefunItem> slimefunItemList = new ArrayList<>();
        for (SlimefunItem item : new ArrayList<>(Slimefun.getRegistry().getAllSlimefunItems())) {
            if ("nexcavate".equalsIgnoreCase(item.getAddon().getName())) {
                slimefunItemList.add(item);
            }
        }
        this.slimefunItemList = slimefunItemList;
        this.pageMap.put(1, this);
    }

    protected NexcavateItemsGroup(NexcavateItemsGroup nexcavateItemsGroup, int page) {
        super(nexcavateItemsGroup.key, Models.NEXCAVATE_ITEMS_GROUP);
        this.page = page;
        this.slimefunItemList = nexcavateItemsGroup.slimefunItemList;
        this.pageMap.put(page, this);
    }

    @Override
    public ChestMenu generateMenu(
        final Player player,
        final PlayerProfile playerProfile,
        final SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu("文明复兴物品");

        Format format = Formats.sub;
        List<Integer> contentSlots = format.getChars('i');
        int maxPage = (this.slimefunItemList.size() - 1) / contentSlots.size() + 1;
        GuideUtil.commonRender(chestMenu, format, playerProfile, player, this, page, maxPage);

        var impl = GuideUtil.getLastJEGGuide(player);
        if (impl == null) return chestMenu;

        for (int i = 0; i < contentSlots.size(); i++) {
            int index = i + this.page * contentSlots.size() - contentSlots.size();
            if (index < this.slimefunItemList.size()) {
                SlimefunItem slimefunItem = slimefunItemList.get(index);
                OnDisplay.Item.display(player, slimefunItem, OnDisplay.Item.Normal, impl)
                    .at(chestMenu, contentSlots.get(i), page);
            }
        }

        return chestMenu;
    }

    @Override
    public int getTier() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isCrossAddonItemGroup() {
        return true;
    }
}
