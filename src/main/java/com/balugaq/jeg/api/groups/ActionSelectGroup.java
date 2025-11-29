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

package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.clickhandler.BaseAction;
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.balugaq.jeg.utils.clickhandler.PermissibleAction;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
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
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode) {
        ChestMenu menu = new ChestMenu("&6点击选择切换的按键");

        OnClick.preset(menu);

        SlimefunGuideImplementation implementation = Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode);

        for (int ss : Formats.actionSelect.getChars('B')) {
            menu.addItem(ss, PatchScope.Background.patch(playerProfile, ChestMenuUtils.getBackground()));
            menu.addMenuClickHandler(ss, ChestMenuUtils.getEmptyClickHandler());
        }

        for (int ss : Formats.actionSelect.getChars('b')) {
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
                                    guideHistory.goBack(Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode));
                                }
                                return false;
                            })
            );
        }

        int pages = (actions.size() - 1) / Formats.actionSelect.getChars('i').size() + 1;
        int i = 0;
        for (int s : Formats.actionSelect.getChars('i')) {
            int k = Formats.actionSelect.getChars('i').size() * (page - 1) + i++;
            if (k < actions.size()) {
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
                            GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                            playerProfile.getGuideHistory().openLastEntry(GuideUtil.getGuide(pl, slimefunGuideMode));
                            return false;
                        })
                );
            } else {
                menu.addItem(s, PatchScope.Background.patch(player, ChestMenuUtils.getBackground()));
                menu.addMenuClickHandler(s, ChestMenuUtils.getEmptyClickHandler());
            }
        }

        for (int s : Formats.actionSelect.getChars('P')) {
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
                            getByPage(page - 1).open(player, playerProfile, slimefunGuideMode);
                        }

                        return false;
                    })
            );
        }

        for (int s : Formats.actionSelect.getChars('N')) {
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
                            getByPage(page + 1).open(player, playerProfile, slimefunGuideMode);
                        }

                        return false;
                    })
            );
        }
        Formats.actionSelect.renderCustom(menu);

        menu.open(player);

        return menu;
    }
}
