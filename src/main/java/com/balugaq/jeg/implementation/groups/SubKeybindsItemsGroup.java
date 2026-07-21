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
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
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

        OnClick.preset(menu);

        SlimefunGuideImplementation implementation = GuideUtil.getSlimefunGuide(slimefunGuideMode);

        for (int ss : Formats.keybinds.getChars('B')) {
            menu.addItem(ss, PatchScope.Background.patch(playerProfile, ChestMenuUtils.getBackground()));
            menu.addMenuClickHandler(ss, ChestMenuUtils.getEmptyClickHandler());
        }

        for (int ss : Formats.keybinds.getChars('b')) {
            menu.addItem(ss, PatchScope.Back.patch(player, ChestMenuUtils.getBackButton(player)));
            menu.addMenuClickHandler(
                ss, (pl, s, is, action) -> EventUtil.callEvent(
                        new GuideEvents.BackButtonClickEvent(pl, is, s, action, menu, implementation))
                    .ifSuccess(() -> {
                        GuideHistory guideHistory = playerProfile.getGuideHistory();
                        if (action.isShiftClicked()) {
                            SlimefunGuide.openMainMenu(
                                playerProfile, slimefunGuideMode, guideHistory.getMainMenuPage());
                        } else {
                            GuideUtil.goBack(guideHistory);
                        }
                        return false;
                    })
            );
        }

        int pages = (OnClick.keybindSets().size() - 1) / Formats.keybinds.getChars('i').size() + 1;
        int i = 0;
        for (int s : Formats.keybinds.getChars('i')) {
            int k = Formats.keybinds.getChars('i').size() * (page - 1) + i++;
            if (k < subKeybindsSet.size()) {
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
        }

        for (int s : Formats.keybinds.getChars('P')) {
            menu.addItem(
                s, PatchScope.PreviousPage.patch(
                    player, ChestMenuUtils.getPreviousButton(
                        player, page,
                        pages
                    )
                )
            );
            menu.addMenuClickHandler(
                s, (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.PreviousButtonClickEvent(
                    pl,
                    item,
                    slot,
                    action, menu, GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE)
                )).ifSuccess(() -> {
                    if (page - 1 > 0) {
                        getByPage(page - 1).open(pl, playerProfile, slimefunGuideMode);
                    }

                    return false;
                })
            );
        }

        for (int s : Formats.keybinds.getChars('N')) {
            menu.addItem(s, PatchScope.NextPage.patch(player, ChestMenuUtils.getNextButton(player, page, pages)));
            menu.addMenuClickHandler(
                s, (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.NextButtonClickEvent(
                    pl, item,
                    slot,
                    action,
                    menu,
                    GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE)
                )).ifSuccess(() -> {
                    int next = page + 1;

                    if (page + 1 <= pages) {
                        getByPage(page + 1).open(pl, playerProfile, slimefunGuideMode);
                    }

                    return false;
                })
            );
        }
        Formats.keybinds.renderCustom(menu);

        return menu;
    }
}
