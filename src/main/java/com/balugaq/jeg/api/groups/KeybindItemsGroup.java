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
import com.balugaq.jeg.utils.Models;
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
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode) {
        ChestMenu menu = new ChestMenu("&6选择你要编辑的按键控制");

        OnClick.preset(menu);

        SlimefunGuideImplementation implementation = Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode);

        for (int ss : Formats.keybind.getChars('B')) {
            menu.addItem(ss, PatchScope.Background.patch(playerProfile, ChestMenuUtils.getBackground()));
            menu.addMenuClickHandler(ss, ChestMenuUtils.getEmptyClickHandler());
        }

        for (int ss : Formats.keybind.getChars('b')) {
            menu.addItem(ss, PatchScope.Back.patch(player, ChestMenuUtils.getBackButton(player)));
            menu.addMenuClickHandler(ss, (pl, s, is, action) -> EventUtil.callEvent(
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
                    }));
        }

        int max = Math.min(Formats.keybind.getChars('x').size(), Math.min(Formats.keybind.getChars('y').size(), Formats.keybind.getChars('z').size()));
        int pages = (actions.size() - 1) / max + 1;
        for (int i = 0; i < max; i++) {
            int k = max * (page - 1) + i;
            int x = Formats.keybind.getChars('x').get(i);
            int y = Formats.keybind.getChars('y').get(i);
            int z = Formats.keybind.getChars('z').get(i);
            if (k < actions.size()) {
                BaseAction action = actions.get(k);
                BaseAction mappedAction = BaseAction.remap(player, keybind, action);
                menu.addItem(x, PatchScope.Keybind.patch(player, GuideUtil.getLeftActionIcon(action)));
                menu.addMenuClickHandler(x, (pl, slot, item, a) -> EventUtil.callEvent(new GuideEvents.KeybindButtonClickEvent(pl, item, slot, a, menu, GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE))).ifSuccess(() -> false));
                menu.addItem(y, PatchScope.KeybindActionBorder.patch(player, Models.KEYBIND_ACTION_BORDER));
                menu.addMenuClickHandler(y, ChestMenuUtils.getEmptyClickHandler());
                menu.addItem(z, PatchScope.Action.patch(player, GuideUtil.getActionIcon(mappedAction)));
                menu.addMenuClickHandler(z, (pl, slot, item, a) -> EventUtil.callEvent(new GuideEvents.ActionButtonClickEvent(pl, item, slot, a, menu, GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE))).ifSuccess(() -> {
                    GuideUtil.openActionSelectGui(pl, keybind, action);
                    return false;
                }));
            } else {
                menu.addItem(x, PatchScope.Background.patch(player, ChestMenuUtils.getBackground()));
                menu.addItem(y, PatchScope.Background.patch(player, ChestMenuUtils.getBackground()));
                menu.addItem(z, PatchScope.Background.patch(player, ChestMenuUtils.getBackground()));
                menu.addMenuClickHandler(x, ChestMenuUtils.getEmptyClickHandler());
                menu.addMenuClickHandler(y, ChestMenuUtils.getEmptyClickHandler());
                menu.addMenuClickHandler(z, ChestMenuUtils.getEmptyClickHandler());
            }
        }

        for (int s : Formats.keybind.getChars('P')) {
            menu.addItem(s, PatchScope.PreviousPage.patch(player, ChestMenuUtils.getPreviousButton(player, page, pages)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.PreviousButtonClickEvent(pl, item, slot, action, menu, GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE))).ifSuccess(() -> {
                if (page - 1 > 0) {
                    getByPage(page - 1).open(player, playerProfile, slimefunGuideMode);
                }

                return false;
            }));
        }

        for (int s : Formats.keybind.getChars('N')) {
            menu.addItem(s, PatchScope.NextPage.patch(player, ChestMenuUtils.getNextButton(player, page, pages)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.NextButtonClickEvent(pl, item, slot, action, menu, GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE))).ifSuccess(() -> {
                int next = page + 1;

                if (page + 1 <= pages) {
                    getByPage(page + 1).open(player, playerProfile, slimefunGuideMode);
                }

                return false;
            }));
        }
        Formats.keybind.renderCustom(menu);

        menu.open(player);

        return menu;
    }
}
