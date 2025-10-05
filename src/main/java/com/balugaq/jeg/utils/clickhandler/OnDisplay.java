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

package com.balugaq.jeg.utils.clickhandler;

import city.norain.slimefun4.VaultIntegration;
import com.balugaq.jeg.api.interfaces.CustomIconDisplay;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.VanillaItemShade;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.utils.JEGVersionedItemFlag;
import com.balugaq.jeg.utils.LocalHelper;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.RequiredArgsConstructor;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * OnDisplay.display(player, item, OnClick.Normal/ItemMark/BookMark/Search).at(menu, slot, guide, page);
 *
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings("deprecation")
@NullMarked
public interface OnDisplay {
    enum DisplayType {
        Normal, ItemMark, BookMark, Search
    }

    DisplayType Normal = DisplayType.Normal, ItemMark = DisplayType.ItemMark, BookMark = DisplayType.BookMark, Search = DisplayType.Search;

    void at(ChestMenu menu, int slot, JEGSlimefunGuideImplementation guide, int page);

    static OnDisplay Vanilla(Player player, ItemStack itemStack, boolean vanillaShade) {
        return new Vanilla(player, itemStack, vanillaShade);
    }

    static OnDisplay Normal(Player player, SlimefunItem item) {
        return new Normal(player, item);
    }

    static OnDisplay ItemMark(Player player, SlimefunItem item) {
        return new ItemMark(player, item);
    }

    static OnDisplay BookMark(Player player, SlimefunItem item) {
        return new BookMark(player, item);
    }

    static OnDisplay Search(Player player, SlimefunItem item) {
        return new Search(player, item);
    }

    static OnDisplay Research(Player player, SlimefunItem item) {
        return new Research(player, item);
    }

    @RequiredArgsConstructor
    class Research implements OnDisplay {
        private final Player player;
        private final SlimefunItem item;

        @Override
        public void at(ChestMenu menu, int slot, JEGSlimefunGuideImplementation guide, int page) {
            io.github.thebusybiscuit.slimefun4.api.researches.Research research = item.getResearch();
            if (research == null) return;

            PatchScope.Research.patch(player, Converter.getItem(
                    ChestMenuUtils.getNoPermissionItem(),
                    "&f" + ItemUtils.getItemName(item.getItem()),
                    "&7" + item.getId(),
                    "&4&l" + Slimefun.getLocalization().getMessage(player, "guide.locked"),
                    "",
                    "&a> 单击解锁",
                    "",
                    "&7需要 &b",
                    VaultIntegration.isEnabled()
                            ? String.format("%.2f", research.getCurrencyCost()) + " 游戏币"
                            : research.getLevelCost() + " 级经验"
            ));
        }
    }

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    @RequiredArgsConstructor
    class Search implements OnDisplay {
        private final Player player;
        private final SlimefunItem item;

        @Override
        public void at(ChestMenu menu, int slot, JEGSlimefunGuideImplementation guide, int page) {
            ItemGroup itemGroup = item.getItemGroup();
            List<String> additionLore = List.of(
                    "",
                    ChatColors.color(String.format("&8\u21E8 &f%s&f - %s", LocalHelper.getAddonName(itemGroup, item.getId()), LocalHelper.getDisplayName(itemGroup, player)))
            );

            ItemStack icon = item instanceof CustomIconDisplay cid ? cid.getCustomIcon() : item.getItem();
            icon.editMeta(meta -> {
                List<String> lore = meta.getLore();
                if (lore == null) lore = new ArrayList<>();
                lore.addAll(additionLore);
                meta.setLore(lore);

                meta.addItemFlags(
                        ItemFlag.HIDE_ATTRIBUTES,
                        ItemFlag.HIDE_ENCHANTS,
                        JEGVersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            });

            menu.addItem(
                    slot,
                    PatchScope.SearchItem.patch(player, icon),
                    OnClick.Item.Normal.create(guide, menu, page));
        }
    }

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    @RequiredArgsConstructor
    class BookMark implements OnDisplay {
        private final Player player;
        private final SlimefunItem item;

        @Override
        public void at(ChestMenu menu, int slot, JEGSlimefunGuideImplementation guide, int page) {
            ItemGroup itemGroup = item.getItemGroup();
            List<String> additionLore = List.of(
                    "",
                    ChatColors.color(String.format("&8\u21E8 &f%s&f - %s", LocalHelper.getAddonName(itemGroup, item.getId()), LocalHelper.getDisplayName(itemGroup, player))),
                    ChatColors.color("&e右键以取消收藏物品")
            );

            ItemStack icon = item instanceof CustomIconDisplay cid ? cid.getCustomIcon() : item.getItem();
            icon.editMeta(meta -> {
                List<String> lore = meta.getLore();
                if (lore == null) lore = new ArrayList<>();
                lore.addAll(additionLore);
                meta.setLore(lore);

                meta.addItemFlags(
                        ItemFlag.HIDE_ATTRIBUTES,
                        ItemFlag.HIDE_ENCHANTS,
                        JEGVersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            });

            menu.addItem(
                    slot,
                    PatchScope.SlimefunItem.patch(player, icon),
                    OnClick.Item.BookMark.create(guide, menu, page));
        }
    }

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    @RequiredArgsConstructor
    class ItemMark implements OnDisplay {
        private final Player player;
        private final SlimefunItem item;

        @Override
        public void at(ChestMenu menu, int slot, JEGSlimefunGuideImplementation guide, int page) {
            ItemGroup itemGroup = item.getItemGroup();
            List<String> additionLore = List.of(
                    "",
                    ChatColors.color(String.format("&8\u21E8 &f%s&f - %s", LocalHelper.getAddonName(itemGroup, item.getId()), LocalHelper.getDisplayName(itemGroup, player))),
                    ChatColors.color("&e左键点击以收藏物品")
            );

            ItemStack icon = item instanceof CustomIconDisplay cid ? cid.getCustomIcon() : item.getItem();
            icon.editMeta(meta -> {
                List<String> lore = meta.getLore();
                if (lore == null) lore = new ArrayList<>();
                lore.addAll(additionLore);
                meta.setLore(lore);

                meta.addItemFlags(
                        ItemFlag.HIDE_ATTRIBUTES,
                        ItemFlag.HIDE_ENCHANTS,
                        JEGVersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            });

            menu.addItem(
                    slot,
                    PatchScope.SlimefunItem.patch(player, icon),
                    OnClick.Item.ItemMark.create(guide, menu, page));
        }
    }

    @RequiredArgsConstructor
    class Normal implements OnDisplay {
        private final Player player;
        private final SlimefunItem item;

        @Override
        public void at(ChestMenu menu, int slot, JEGSlimefunGuideImplementation guide, int page) {
            menu.addItem(
                    slot,
                    PatchScope.SlimefunItem.patch(player, item instanceof CustomIconDisplay cid ? cid.getCustomIcon() : item.getItem()),
                    OnClick.Item.Normal.create(guide, menu, page));
        }
    }

    @RequiredArgsConstructor
    class Vanilla implements OnDisplay {
        private final Player player;
        private final ItemStack itemStack;
        private final boolean vanillaShade;

        @Override
        public void at(ChestMenu menu, int slot, JEGSlimefunGuideImplementation guide, int page) {
            menu.addItem(
                    slot,
                    PatchScope.VanillaItem.patch(player, vanillaShade ? ((VanillaItemShade) Objects.requireNonNull(SlimefunItem.getByItem(itemStack))).getCustomIcon(): itemStack),
                    OnClick.Item.Normal.create(guide, menu, page));
        }
    }

    static OnDisplay display(Player player, ItemStack itemStack, DisplayType type) {
        SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
        if (slimefunItem == null || slimefunItem instanceof VanillaItemShade) {
            return OnDisplay.Vanilla(player, itemStack, slimefunItem != null);
        }

        if (type == DisplayType.ItemMark) {
            return OnDisplay.ItemMark(player, slimefunItem);
        }

        if (type == DisplayType.BookMark) {
            return OnDisplay.BookMark(player, slimefunItem);
        }

        if (type == DisplayType.Search) {
            return OnDisplay.Normal(player, slimefunItem);
        }

        if (!Slimefun.getConfigManager().isResearchingEnabled()) {
            return OnDisplay.Normal(player, slimefunItem);
        }

        io.github.thebusybiscuit.slimefun4.api.researches.Research research = slimefunItem.getResearch();
        if (research == null || !research.isEnabled()) {
            return OnDisplay.Normal(player, slimefunItem);
        }

        PlayerProfile profile = PlayerProfile.find(player).orElse(null);
        if (profile == null) return OnDisplay.Normal(player, slimefunItem);

        if (profile.getResearches().contains(research)) {
            return OnDisplay.Normal(player, slimefunItem);
        }

        if (player.getGameMode() != GameMode.CREATIVE) {
            return OnDisplay.Research(player, slimefunItem);
        }

        if (Slimefun.getConfigManager().isFreeCreativeResearchingEnabled()) {
            return OnDisplay.Normal(player, slimefunItem);
        }

        return OnDisplay.Research(player, slimefunItem);
    }
}
