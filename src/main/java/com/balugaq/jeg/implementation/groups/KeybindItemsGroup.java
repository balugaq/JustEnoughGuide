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
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.clickhandler.BaseAction;
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.balugaq.jeg.utils.clickhandler.PermissibleAction;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings({"deprecation", "unused"})
@NullMarked
public class KeybindItemsGroup extends BaseGroup<KeybindItemsGroup> {
    private final OnClick keybind;
    private final List<? extends BaseAction> actions;

    public KeybindItemsGroup(Player player, OnClick keybind) {
        super();
        this.page = 1;
        this.keybind = keybind;
        List<BaseAction> filtered = new ArrayList<>();
        for (BaseAction action : keybind.listActions()) {
            if (action instanceof PermissibleAction pm && !pm.hasPermission(player)) continue;
            filtered.add(action);
        }
        this.actions = filtered;
        this.pageMap.put(1, this);
    }

    @Override
    public ChestMenu generateMenu(
        final Player player,
        final PlayerProfile profile,
        final SlimefunGuideMode slimefunGuideMode) {
        ChestMenu menu = new ChestMenu("&6选择你要编辑的按键控制");

        Format format = Formats.keybind;
        int max = Math.min(
            format.getChars(Formats.Char.ACTION_KEY).size(), Math.min(
                format.getChars(Formats.Char.KEY_ACTION_GAP).size(),
                format.getChars(Formats.Char.ACTION).size()
            )
        );
        int pages = (actions.size() - 1) / max + 1;
        GuideUtil.commonRender(menu, format, profile, player, this, this.page, pages);

        for (int i = 0; i < max; i++) {
            int k = max * (page - 1) + i;
            int x = format.getChars(Formats.Char.ACTION_KEY).get(i);
            int y = format.getChars(Formats.Char.KEY_ACTION_GAP).get(i);
            int z = format.getChars(Formats.Char.ACTION).get(i);
            if (k < actions.size()) {
                BaseAction action = actions.get(k);
                BaseAction mappedAction = BaseAction.remap(player, keybind, action);
                menu.addItem(x, PatchScope.Keybind.patch(player, GuideUtil.getLeftActionIcon(action)));
                menu.addMenuClickHandler(
                    x,
                    (pl, slot, item, a) -> EventUtil.callEvent(new GuideEvents.KeybindButtonClickEvent(pl, item, slot, a, menu, GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE))).ifSuccess(() -> false)
                );
                menu.addItem(y, PatchScope.KeybindActionBorder.patch(player, Models.KEYBIND_ACTION_BORDER));
                menu.addMenuClickHandler(y, ChestMenuUtils.getEmptyClickHandler());
                menu.addItem(z, PatchScope.Action.patch(player, GuideUtil.getActionIcon(mappedAction)));
                menu.addMenuClickHandler(
                    z, (pl, slot, item, a) -> EventUtil.callEvent(new GuideEvents.ActionButtonClickEvent(
                        pl, item
                        , slot, a, menu, GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE)
                    )).ifSuccess(() -> {
                        GuideUtil.openActionSelectGui(pl, keybind, action);
                        return false;
                    })
                );
            } else {
                menu.addItem(x, PatchScope.Background.patch(player, ChestMenuUtils.getBackground()), ChestMenuUtils.getEmptyClickHandler());
                menu.addItem(y, PatchScope.Background.patch(player, ChestMenuUtils.getBackground()), ChestMenuUtils.getEmptyClickHandler());
                menu.addItem(z, PatchScope.Background.patch(player, ChestMenuUtils.getBackground()), ChestMenuUtils.getEmptyClickHandler());
            }
        }

        return menu;
    }
}
