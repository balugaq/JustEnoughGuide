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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This is the base class for all guide groups.
 * It provides a simple way to add guides to a group and open them in a chest menu.
 * The group can be customized by overriding the following methods:
 * - getSize() - returns the size of the chest menu
 * - isClassic() - returns whether the chest menu should be in classic mode
 * - getContentSlots() - returns the slots where the guides should be placed
 * - getBackSlot() - returns the slot where the back button should be placed
 *
 * @author balugaq
 * @since 1.3
 */
@SuppressWarnings({"deprecation", "unused", "UnusedReturnValue", "ConstantValue"})
@Getter
@NotDisplayInCheatMode
public abstract class GuideGroup extends FlexItemGroup {
    private final Map<Integer, Set<Integer>> slots = new HashMap<>();
    private final Map<Integer, Map<Integer, ItemStack>> contents = new HashMap<>();
    private final Map<Integer, Map<Integer, ChestMenu.MenuClickHandler>> clickHandlers = new HashMap<>();

    /**
     * Creates a new guide group with the given key and icon.
     *
     * @param key  The key of the group.
     * @param icon The icon of the group.
     */
    protected GuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon) {
        super(key, icon);
    }

    /**
     * Creates a new guide group with the given key, icon, and tier.
     *
     * @param key  The key of the group.
     * @param icon The icon of the group.
     * @param tier The tier of the group.
     */
    protected GuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon, int tier) {
        super(key, icon, tier);
    }

    /**
     * Adds a guide to the group.
     *
     * @param page      The page where the guide should be placed.
     * @param slot      The slot where the guide should be placed.
     * @param itemStack The item stack representing the guide.
     * @param handler   The click handler for the guide.
     * @return The group itself.
     */
    @NotNull
    public GuideGroup addGuide(
            @Range(from = 1, to = Byte.MAX_VALUE) int page,
            @Range(from = 0, to = 53) int slot,
            final @NotNull ItemStack itemStack,
            final @NotNull ChestMenu.MenuClickHandler handler) {

        slots.computeIfAbsent(page, k -> new HashSet<>()).add(slot);
        contents.computeIfAbsent(page, k -> new LinkedHashMap<>()).put(slot, itemStack);
        clickHandlers.computeIfAbsent(page, k -> new LinkedHashMap<>()).put(slot, handler);
        return this;
    }

    /**
     * Add a {@link ChestMenu.MenuClickHandler} to the group
     *
     * @param page    the page
     * @param slot    the slot
     * @param handler the handler
     * @return the group itself
     */
    @NotNull
    public GuideGroup addGuide(
            @Range(from = 1, to = Byte.MAX_VALUE) int page,
            @Range(from = 0, to = 53) int slot,
            final @NotNull ChestMenu.MenuClickHandler handler) {

        slots.computeIfAbsent(page, k -> new HashSet<>()).add(slot);
        contents.computeIfAbsent(page, k -> new LinkedHashMap<>()).put(slot, item);
        clickHandlers.computeIfAbsent(page, k -> new LinkedHashMap<>()).put(slot, handler);
        return this;
    }

    /**
     * Add a {@link ChestMenu.MenuClickHandler} to the group
     *
     * @param slot    the slot
     * @param handler the handler
     * @return the group itself
     */
    @NotNull
    public GuideGroup addGuide(@Range(from = 0, to = 53) int slot, final @NotNull ChestMenu.MenuClickHandler handler) {
        return addGuide(1, slot, handler);
    }

    /**
     * Adds a guide
     *
     * @param slot slot
     * @return the guide itself
     */
    @Nullable
    public ChestMenu.MenuClickHandler getMenuClickHandler(@Range(from = 0, to = 53) int slot) {
        return getMenuClickHandler(1, slot);
    }

    /**
     * Adds a guide
     *
     * @param page page number
     * @param slot slot
     * @return the guide itself
     */
    @Nullable
    public ChestMenu.MenuClickHandler getMenuClickHandler(
            @Range(from = 1, to = Byte.MAX_VALUE) int page, @Range(from = 0, to = 53) int slot) {
        return Optional.ofNullable(clickHandlers.get(page))
                .orElse(new HashMap<>())
                .get(slot);
    }

    /**
     * Adds a guide to the group.
     *
     * @param slot      The slot where the guide should be placed.
     * @param itemStack The item stack representing the guide.
     * @param handler   The click handler for the guide.
     * @return The group itself.
     */
    @NotNull
    public GuideGroup addGuide(
            @Range(from = 0, to = 53) int slot,
            final @NotNull ItemStack itemStack,
            final @NotNull ChestMenu.MenuClickHandler handler) {
        return addGuide(1, slot, itemStack, handler);
    }

    /**
     * Adds a guide to the group.
     *
     * @param slot      The slot where the guide should be placed.
     * @param itemStack The item stack representing the guide.
     * @return The group itself.
     */
    @NotNull
    public GuideGroup addGuide(@Range(from = 0, to = 53) int slot, @NotNull ItemStack itemStack) {
        return addGuide(slot, itemStack, ChestMenuUtils.getEmptyClickHandler());
    }

    /**
     * Adds a guide to the group.
     *
     * @param itemStack The item stack representing the guide.
     * @param handler   The click handler for the guide.
     * @return The group itself.
     */
    @NotNull
    public GuideGroup addGuide(
            @NotNull ItemStack itemStack,
            @NotNull ChestMenu.MenuClickHandler handler,
            @Range(from = 1, to = Byte.MAX_VALUE) int page) {
        return addGuide(findEmptySlot(page), itemStack, handler);
    }

    /**
     * Adds a guide to the group.
     *
     * @param itemStack The item stack representing the guide.
     * @return The group itself.
     */
    @NotNull
    public GuideGroup addGuide(@NotNull ItemStack itemStack, @Range(from = 1, to = Byte.MAX_VALUE) int page) {
        return addGuide(itemStack, ChestMenuUtils.getEmptyClickHandler(), page);
    }

    /**
     * Replace the icon.
     *
     * @param slot The slot where the icon should be placed.
     * @param icon The icon to place.
     * @return The group itself.
     */
    @NotNull
    public GuideGroup replaceICon(@Range(from = 0, to = 53) int slot, @NotNull ItemStack icon) {
        return replaceIcon(1, slot, icon);
    }

    /**
     * Replace the icon.
     *
     * @param page      The page where the icon should be placed.
     * @param slot      The slot where the icon should be placed.
     * @param itemStack The icon to place
     * @return The group itself.
     */
    @NotNull
    public GuideGroup replaceIcon(
            @Range(from = 1, to = Byte.MAX_VALUE) int page,
            @Range(from = 0, to = 53) int slot,
            @NotNull ItemStack itemStack) {
        slots.computeIfAbsent(page, k -> new HashSet<>()).add(slot);
        contents.computeIfAbsent(page, k -> new LinkedHashMap<>()).put(slot, itemStack);
        return this;
    }

    /**
     * Finds an empty slot in the chest menu.
     *
     * @param page The page to search in.
     * @return An empty slot in the chest menu, or -1 if no slot is available.
     */
    private int findEmptySlot(int page) {
        for (int i = 0; i < 54; i++) {
            if (!slots.getOrDefault(page, new HashSet<>()).contains(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns whether the group is visible to the given player.
     *
     * @param player            The player to check.
     * @param playerProfile     The player profile of the player.
     * @param slimefunGuideMode The guide mode of the player.
     * @return Whether the group is visible to the given player.
     */
    @Override
    public boolean isVisible(
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode) {
        return true;
    }

    @Override
    public void open(
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode) {
        open(player, playerProfile, slimefunGuideMode, 1);
    }

    /**
     * Opens the customized group for the given player.
     *
     * @param player            The player to open the group for.
     * @param playerProfile     The player profile of the player.
     * @param slimefunGuideMode The guide mode of the player.
     * @param page              The page to open.
     */
    public void open(
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode,
            @Range(from = 1, to = Byte.MAX_VALUE) int page) {
        if (page < 1 || page > this.contents.size()) {
            // Do nothing if the page is out of range.
            return;
        }

        SlimefunGuideImplementation guide = GuideUtil.getGuide(player, slimefunGuideMode);
        playerProfile.getGuideHistory().add(this, page);
        if (guide instanceof JEGSlimefunGuideImplementation jeg) {
            ChestMenu menu = new ChestMenu(getDisplayName(player));
            menu.setSize(getSize());
            if (isClassic()) {
                jeg.createHeader(player, playerProfile, menu, Formats.helper);
            }
            for (int ss : Formats.helper.getChars('b')) {
                menu.addItem(ss, PatchScope.Back.patch(player, ChestMenuUtils.getBackButton(player)));
                menu.addMenuClickHandler(ss, (pl, s, is, action) -> EventUtil.callEvent(
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
                        }));
            }

            for (Map.Entry<Integer, ItemStack> entry :
                    contents.getOrDefault(page, new LinkedHashMap<>()).entrySet()) {
                menu.addItem(entry.getKey(), PatchScope.FeatureDisplay.patch(player, entry.getValue()));
            }

            for (Map.Entry<Integer, ChestMenu.MenuClickHandler> entry :
                    clickHandlers.getOrDefault(page, new LinkedHashMap<>()).entrySet()) {
                menu.addMenuClickHandler(entry.getKey(), (p, s, i, a) -> EventUtil.callEvent(
                                new GuideEvents.FeatureButtonClickEvent(p, i, s, a, menu, guide))
                        .ifSuccess(() -> entry.getValue().onClick(p, s, i, a)));
            }

            for (int s : Formats.helper.getChars('P')) {
                menu.addItem(
                        s,
                        PatchScope.PreviousPage.patch(
                                player,
                                ChestMenuUtils.getPreviousButton(player, page, (this.contents.size() - 1) / 36 + 1)));
                menu.addMenuClickHandler(s, (p, slot, item, action) -> EventUtil.callEvent(
                                new GuideEvents.PreviousButtonClickEvent(p, item, slot, action, menu, guide))
                        .ifSuccess(() -> {
                            if (page - 1 < 1) {
                                return false;
                            }
                            GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                            open(player, playerProfile, slimefunGuideMode, Math.max(1, page - 1));
                            return false;
                        }));
            }

            for (int s : Formats.helper.getChars('N')) {
                menu.addItem(
                        s,
                        PatchScope.NextPage.patch(
                                player,
                                ChestMenuUtils.getNextButton(player, page, (this.contents.size() - 1) / 36 + 1)));
                menu.addMenuClickHandler(s, (p, slot, item, action) -> EventUtil.callEvent(
                                new GuideEvents.NextButtonClickEvent(p, item, slot, action, menu, guide))
                        .ifSuccess(() -> {
                            if (page + 1 > this.contents.size()) {
                                return false;
                            }
                            GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                            open(player, playerProfile, slimefunGuideMode, Math.min(this.contents.size(), page + 1));
                            return false;
                        }));
            }

            GuideUtil.addRTSButton(menu, player, playerProfile, Formats.sub, slimefunGuideMode, guide);
            GuideUtil.addBookMarkButton(menu, player, playerProfile, Formats.sub, jeg, this);
            GuideUtil.addItemMarkButton(menu, player, playerProfile, Formats.sub, jeg, this);

            menu.open(player);
        } else {
            player.sendMessage("§cJEG 模块未启用。你不能打开 JEG 使用指南。");
        }
    }

    /**
     * Returns the size of the chest menu.
     *
     * @return The size of the chest menu.
     */
    public abstract int getSize();

    /**
     * Returns whether the chest menu should be generated in classic mode.
     *
     * @return Whether the chest menu should be generated in classic mode.
     */
    public abstract boolean isClassic();

    /**
     * Returns the slots where the guides should be placed.
     *
     * @return The slots where the guides should be placed.
     */
    public abstract int[] getContentSlots();

    /**
     * Returns the slot where the back button should be placed.
     *
     * @return The slot where the back button should be placed.
     */
    @Deprecated
    public abstract int getBackSlot();
}
