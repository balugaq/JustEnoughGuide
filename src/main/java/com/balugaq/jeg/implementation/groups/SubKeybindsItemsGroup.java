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

package com.balugaq.jeg.implementation.groups;

import com.balugaq.jeg.api.groups.BaseGroup;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings({"deprecation", "unused"})
@NullMarked
public class SubKeybindsItemsGroup extends BaseGroup<SubKeybindsItemsGroup> {
    private final ObjectImmutableList<? extends OnClick> subKeybindsSet;

    public SubKeybindsItemsGroup(OnClick keybinds) {
        super();
        this.page = 1;
        this.subKeybindsSet = keybinds.subKeybinds();
        this.pageMap.put(1, this);
    }

    @Override
    public ChestMenu generateMenu(
        final Player player,
        final PlayerProfile playerProfile,
        final SlimefunGuideMode slimefunGuideMode) {
        ChestMenu menu = new ChestMenu("&6选择你要编辑的按键控制子集");

        Format format = Formats.keybinds;
        int pages = (OnClick.keybindSets().size() - 1) / format.getChars(Formats.Char.CONTENT).size() + 1;
        GuideUtil.commonRender(menu, format, playerProfile, player, this, this.page, pages);

        int i = 0;
        for (int s : format.getChars(Formats.Char.CONTENT)) {
            int k = format.getChars(Formats.Char.CONTENT).size() * (page - 1) + i++;
            if (k >= subKeybindsSet.size()) break;
            OnClick keybinds = subKeybindsSet.get(k);
            menu.addItem(s, PatchScope.SubKeybindsSet.patch(player, GuideUtil.getKeybindIcon(keybinds)));
            menu.addMenuClickHandler(
                s,
                (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.SubKeybindsButtonClickEvent(pl, item, slot, action, menu, GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE))).ifSuccess(() -> {
                    GuideUtil.openKeybindGui(player, keybinds);
                    return false;
                })
            );
        }

        return menu;
    }
}
