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

package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This is the base class for all guide groups.
 *
 * @author balugaq
 * @since 1.3
 */
@SuppressWarnings({"deprecation", "unused", "UnusedReturnValue", "ConstantValue"})
@Getter
@NotDisplayInCheatMode
@NullMarked
public abstract class GuideGroup extends FlexItemGroup {
    private final Map<Integer, Set<Integer>> slots = new HashMap<>();
    private final Map<Integer, Map<Integer, ItemStack>> contents = new HashMap<>();
    private final Map<Integer, Map<Integer, ChestMenu.MenuClickHandler>> clickHandlers = new HashMap<>();

    protected GuideGroup(NamespacedKey key, ItemStack icon) {
        super(key, icon);
    }

    protected GuideGroup(NamespacedKey key, ItemStack icon, int tier) {
        super(key, icon, tier);
    }

    public GuideGroup addGuide(@Range(from = 0, to = 53) int slot, final ChestMenu.MenuClickHandler handler) {
        return addGuide(1, slot, handler);
    }

    public GuideGroup addGuide(
            @Range(from = 1, to = Byte.MAX_VALUE) int page,
            @Range(from = 0, to = 53) int slot,
            final ChestMenu.MenuClickHandler handler) {

        slots.computeIfAbsent(page, k -> new HashSet<>()).add(slot);
        contents.computeIfAbsent(page, k -> new LinkedHashMap<>()).put(slot, item);
        clickHandlers.computeIfAbsent(page, k -> new LinkedHashMap<>()).put(slot, handler);
        return this;
    }

    @Nullable
    public ChestMenu.MenuClickHandler getMenuClickHandler(@Range(from = 0, to = 53) int slot) {
        return getMenuClickHandler(1, slot);
    }

    @Nullable
    public ChestMenu.MenuClickHandler getMenuClickHandler(
            @Range(from = 1, to = Byte.MAX_VALUE) int page, @Range(from = 0, to = 53) int slot) {
        return Optional.ofNullable(clickHandlers.get(page))
                .orElse(new HashMap<>())
                .get(slot);
    }

    public GuideGroup addGuide(@Range(from = 0, to = 53) int slot, ItemStack itemStack) {
        return addGuide(slot, itemStack, ChestMenuUtils.getEmptyClickHandler());
    }

    public GuideGroup addGuide(
            @Range(from = 0, to = 53) int slot,
            final ItemStack itemStack,
            final ChestMenu.MenuClickHandler handler) {
        return addGuide(1, slot, itemStack, handler);
    }

    public GuideGroup addGuide(
            @Range(from = 1, to = Byte.MAX_VALUE) int page,
            @Range(from = 0, to = 53) int slot,
            final ItemStack itemStack,
            final ChestMenu.MenuClickHandler handler) {

        slots.computeIfAbsent(page, k -> new HashSet<>()).add(slot);
        contents.computeIfAbsent(page, k -> new LinkedHashMap<>()).put(slot, itemStack);
        clickHandlers.computeIfAbsent(page, k -> new LinkedHashMap<>()).put(slot, handler);
        return this;
    }

    public GuideGroup addGuide(ItemStack itemStack, @Range(from = 1, to = Byte.MAX_VALUE) int page) {
        return addGuide(itemStack, ChestMenuUtils.getEmptyClickHandler(), page);
    }

    public GuideGroup addGuide(
            ItemStack itemStack,
            ChestMenu.MenuClickHandler handler,
            @Range(from = 1, to = Byte.MAX_VALUE) int page) {
        return addGuide(findFirstEmptySlot(page), itemStack, handler);
    }

    private int findFirstEmptySlot(int page) {
        for (int i = 0; i < 54; i++) {
            if (!slots.getOrDefault(page, new HashSet<>()).contains(i)) {
                return i;
            }
        }
        return -1;
    }

    public GuideGroup replaceICon(@Range(from = 0, to = 53) int slot, ItemStack icon) {
        return replaceIcon(1, slot, icon);
    }

    public GuideGroup replaceIcon(
            @Range(from = 1, to = Byte.MAX_VALUE) int page,
            @Range(from = 0, to = 53) int slot,
            ItemStack itemStack) {
        slots.computeIfAbsent(page, k -> new HashSet<>()).add(slot);
        contents.computeIfAbsent(page, k -> new LinkedHashMap<>()).put(slot, itemStack);
        return this;
    }

    @Override
    public boolean isVisible(
            final Player player,
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode) {
        return true;
    }

    @Override
    public void open(
            final Player player,
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode) {
        open(player, playerProfile, slimefunGuideMode, 1);
    }

    public void open(
            final Player player,
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode,
            @Range(from = 1, to = Byte.MAX_VALUE) int page) {
        if (page < 1 || page > this.contents.size()) {
            // Do nothing if the page is out of range.
            return;
        }

        SlimefunGuideImplementation guide = GuideUtil.getGuide(player, slimefunGuideMode);
        playerProfile.getGuideHistory().add(this, page);
        if (!(guide instanceof JEGSlimefunGuideImplementation jeg)) {
            player.sendMessage("§cJEG 模块未启用。你不能打开 JEG 使用指南。");
            return;
        }

        ChestMenu menu = new ChestMenu(getDisplayName(player));
        menu.setSize(getSize());
        if (isClassic()) {
            jeg.createHeader(player, playerProfile, menu, Formats.helper);
        }
        for (int ss : Formats.helper.getChars('b')) {
            menu.addItem(ss, PatchScope.Back.patch(player, ChestMenuUtils.getBackButton(player)));
            menu.addMenuClickHandler(
                    ss, (pl, s, is, action) -> EventUtil.callEvent(
                                    new GuideEvents.BackButtonClickEvent(pl, is, s, action, menu, guide))
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

        for (Map.Entry<Integer, ItemStack> entry :
                contents.getOrDefault(page, new LinkedHashMap<>()).entrySet()) {
            menu.addItem(entry.getKey(), PatchScope.FeatureDisplay.patch(player, entry.getValue()));
        }

        for (Map.Entry<Integer, ChestMenu.MenuClickHandler> entry :
                clickHandlers.getOrDefault(page, new LinkedHashMap<>()).entrySet()) {
            menu.addMenuClickHandler(
                    entry.getKey(), (p, s, i, a) -> EventUtil.callEvent(
                                    new GuideEvents.FeatureButtonClickEvent(p, i, s, a, menu, guide))
                            .ifSuccess(() -> entry.getValue().onClick(p, s, i, a))
            );
        }

        for (int s : Formats.helper.getChars('P')) {
            menu.addItem(
                    s,
                    PatchScope.PreviousPage.patch(
                            player,
                            ChestMenuUtils.getPreviousButton(player, page, (this.contents.size() - 1) / 36 + 1)
                    )
            );
            menu.addMenuClickHandler(
                    s, (p, slot, item, action) -> EventUtil.callEvent(
                                    new GuideEvents.PreviousButtonClickEvent(p, item, slot, action, menu, guide))
                            .ifSuccess(() -> {
                                if (page - 1 < 1) {
                                    return false;
                                }
                                GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                                open(player, playerProfile, slimefunGuideMode, Math.max(1, page - 1));
                                return false;
                            })
            );
        }

        for (int s : Formats.helper.getChars('N')) {
            menu.addItem(
                    s,
                    PatchScope.NextPage.patch(
                            player,
                            ChestMenuUtils.getNextButton(player, page, (this.contents.size() - 1) / 36 + 1)
                    )
            );
            menu.addMenuClickHandler(
                    s, (p, slot, item, action) -> EventUtil.callEvent(
                                    new GuideEvents.NextButtonClickEvent(p, item, slot, action, menu, guide))
                            .ifSuccess(() -> {
                                if (page + 1 > this.contents.size()) {
                                    return false;
                                }
                                GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                                open(
                                        player, playerProfile, slimefunGuideMode, Math.min(
                                                this.contents.size(),
                                                page + 1
                                        )
                                );
                                return false;
                            })
            );
        }

        GuideUtil.addRTSButton(menu, player, playerProfile, Formats.sub, slimefunGuideMode, guide);
        GuideUtil.addBookMarkButton(menu, player, playerProfile, Formats.sub, jeg, this);
        GuideUtil.addItemMarkButton(menu, player, playerProfile, Formats.sub, jeg, this);

        menu.open(player);
    }

    public abstract int getSize();

    public abstract boolean isClassic();
}
