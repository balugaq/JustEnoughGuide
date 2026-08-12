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
import com.balugaq.jeg.utils.clickhandler.BaseAction;
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.balugaq.jeg.utils.clickhandler.PermissibleAction;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
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
public class ActionSelectGroup extends BaseGroup<ActionSelectGroup> {
    private final BaseAction keybind;
    private final List<? extends BaseAction> actions;

    public ActionSelectGroup(Player player, OnClick keybind, BaseAction from) {
        super();
        this.page = 1;
        this.keybind = from;
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
        ChestMenu menu = new ChestMenu("&6点击选择切换的按键");

        Format format = Formats.actionSelect;
        int pages = (actions.size() - 1) / format.getChars(Formats.Char.CONTENT).size() + 1;
        GuideUtil.commonRender(menu, format, profile, player, this, this.page, pages);

        int i = 0;
        for (int s : format.getChars(Formats.Char.CONTENT)) {
            int k = format.getChars(Formats.Char.CONTENT).size() * (page - 1) + i++;
            if (k >= actions.size()) {
                menu.addItem(s, PatchScope.Background.patch(player, ChestMenuUtils.getBackground()));
                menu.addMenuClickHandler(s, ChestMenuUtils.getEmptyClickHandler());
                continue;
            }

            BaseAction act = actions.get(k);
            menu.addItem(s, PatchScope.Action.patch(player, GuideUtil.getActionIcon(act)));
            menu.addMenuClickHandler(
                s, (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.ActionButtonClickEvent(
                    pl,
                    item, slot, action, menu, GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE)
                )).ifSuccess(() -> {
                    BaseAction.redirect(pl, act.parent(), keybind, act);
                    pl.closeInventory();
                    pl.sendMessage(ChatColors.color("&a已设置 " + keybind.name() + " -> " + act.name()));
                    GuideUtil.removeLastEntry(profile);
                    GuideUtil.getProfile(profile).getGuideHistory().openLastEntry(GuideUtil.getGuide(pl, slimefunGuideMode));
                    return false;
                })
            );
        }

        return menu;
    }
}
