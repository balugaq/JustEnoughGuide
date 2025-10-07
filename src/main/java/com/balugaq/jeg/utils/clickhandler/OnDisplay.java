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
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.JEGVersionedItemFlag;
import com.balugaq.jeg.utils.LocalHelper;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.LockedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * OnDisplay.display(player, item, OnClick.Normal/ItemMark/Bookmark/Search).at(menu, slot, guide, page);
 *
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings({"deprecation", "ClassCanBeRecord"})
@NullMarked
public interface OnDisplay {
    interface ItemGroup extends OnDisplay {
        DisplayType Normal = DisplayType.Normal, Bookmark = DisplayType.Bookmark;

        static ItemGroup Normal(Player player, io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup, JEGSlimefunGuideImplementation guide) {
            return new Normal(player, itemGroup, guide);
        }

        static ItemGroup Bookmark(Player player, io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup, JEGSlimefunGuideImplementation guide) {
            return new Bookmark(player, itemGroup, guide);
        }

        static ItemGroup Locked(Player player, LockedItemGroup itemGroup, JEGSlimefunGuideImplementation guide) {
            return new Locked(player, itemGroup, guide);
        }

        static ItemGroup NoPermission(Player player, io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup, JEGSlimefunGuideImplementation guide) {
            return new NoPermission(player, itemGroup, guide);
        }

        static ItemGroup display(Player player, io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup, DisplayType type, SlimefunGuideImplementation guide) {
            if (guide instanceof JEGSlimefunGuideImplementation jeg) return display(player, itemGroup, type, jeg);
            return display(player, itemGroup, type, GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE));
        }

        static ItemGroup display(Player player, io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup, DisplayType type, JEGSlimefunGuideImplementation guide) {
            // You're supposed to precheck it before displaying
            if (guide.getMode() == SlimefunGuideMode.SURVIVAL_MODE && (!itemGroup.isVisible(player) || itemGroup.isHidden(player) || !itemGroup.isAccessible(player))) {
                return NoPermission(player, itemGroup, guide);
            }

            if (!(itemGroup instanceof LockedItemGroup locked)) {
                if (type == Bookmark) {
                    return Bookmark(player, itemGroup, guide);
                }
                return Normal(player, itemGroup, guide);
            }

            if (player.isOp()) {
                return Normal(player, itemGroup, guide);
            }

            if (guide.getMode() == SlimefunGuideMode.CHEAT_MODE) {
                return Normal(player, itemGroup, guide);
            }

            PlayerProfile profile = PlayerProfile.find(player).orElse(null);
            if (profile == null) return Normal(player, itemGroup, guide);

            if (locked.hasUnlocked(player, profile)) {
                if (type == Bookmark) {
                    return Bookmark(player, itemGroup, guide);
                }
                return Normal(player, itemGroup, guide);
            }

            return Locked(player, locked, guide);
        }

        void at(ChestMenu menu, int slot, int page);

        enum DisplayType {
            Normal, Bookmark
        }

        @RequiredArgsConstructor
        @Data
        class Normal implements ItemGroup {
            private final Player player;
            private final io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup;
            private final JEGSlimefunGuideImplementation guide;

            @Override
            public void at(ChestMenu menu, int slot, int page) {
                menu.addItem(slot, PatchScope.ItemGroup.patch(player, itemGroup.getItem(player)), OnClick.ItemGroup.Normal.create(guide, menu, itemGroup));
            }
        }

        @SuppressWarnings("UnnecessaryUnicodeEscape")
        @RequiredArgsConstructor
        @Data
        class Bookmark implements ItemGroup {
            private final Player player;
            private final io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup;
            private final JEGSlimefunGuideImplementation guide;

            @Override
            public void at(ChestMenu menu, int slot, int page) {
                List<String> additionLore = List.of(
                        "",
                        (itemGroup instanceof FlexItemGroup || itemGroup.getItems().isEmpty())
                                ? ChatColors.color("&8\u21E8 &f" + LocalHelper.getAddonName(itemGroup.getAddon()))
                                : ChatColors.color("&8\u21E8 &f" + LocalHelper.getAddonName(itemGroup.getAddon(), itemGroup.getItems().get(0).getId())),
                        ChatColors.color("&e右键以取消收藏物品组")
                );

                ItemStack icon = ItemStackUtil.getCleanItem(Converter.getItem(itemGroup.getItem(player)));
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

                menu.addItem(slot, PatchScope.ItemGroup.patch(player, icon), OnClick.ItemGroup.Bookmark.create(guide, menu, itemGroup));
            }
        }

        @RequiredArgsConstructor
        @Data
        class Locked implements ItemGroup {
            private final Player player;
            private final LockedItemGroup itemGroup;
            private final JEGSlimefunGuideImplementation guide;

            @Override
            public void at(ChestMenu menu, int slot, int page) {
                List<String> lore = new ArrayList<>();
                lore.add("");

                for (String line : Slimefun.getLocalization().getMessages(player, "guide.locked-itemgroup")) {
                    lore.add(ChatColor.WHITE + line);
                }
                lore.add("");

                for (io.github.thebusybiscuit.slimefun4.api.items.ItemGroup parent : itemGroup.getParents()) {
                    lore.add(ItemStackHelper.getDisplayName(parent.getItem(player)));
                }

                ItemStack icon = PatchScope.LockedItemGroup.patch(player, Converter.getItem(
                        Material.BARRIER,
                        "&4" + Slimefun.getLocalization().getMessage(player, "guide.locked") + " &7- &f" + ItemStackHelper.getDisplayName(itemGroup.getItem(player)),
                        lore.toArray(new String[0])
                ));
                menu.addItem(slot, icon, OnClick.ClickHandler.deny());
            }
        }

        @RequiredArgsConstructor
        @Data
        class NoPermission implements ItemGroup {
            private final Player player;
            private final io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup;
            private final JEGSlimefunGuideImplementation guide;

            @Override
            public void at(ChestMenu menu, int slot, int page) {
                menu.addItem(slot, PatchScope.NoPermission.patch(player, Converter.getItem(
                        ChestMenuUtils.getNoPermissionItem(),
                        ItemStackHelper.getDisplayName(itemGroup.getItem(player))
                )), OnClick.ClickHandler.deny());
            }
        }
    }

    interface RecipeType extends OnDisplay {

        static RecipeType Normal(Player player, io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType recipeType, ItemStack itemStack, JEGSlimefunGuideImplementation guide) {
            return new Normal(player, recipeType, itemStack, guide);
        }

        static RecipeType display(Player player, io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType recipeType, ItemStack itemStack, SlimefunGuideImplementation guide) {
            if (guide instanceof JEGSlimefunGuideImplementation jeg) return display(player, recipeType, itemStack, jeg);
            return display(player, recipeType, itemStack, GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE));
        }

        static RecipeType display(Player player, io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType recipeType, ItemStack itemStack, JEGSlimefunGuideImplementation guide) {
            return Normal(player, recipeType, itemStack, guide);
        }

        void at(ChestMenu menu, int slot, int page);

        @RequiredArgsConstructor
        @Data
        class Normal implements RecipeType {
            private final Player player;
            private final io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType recipeType;
            private final ItemStack itemStack;
            private final JEGSlimefunGuideImplementation guide;

            @Override
            public void at(ChestMenu menu, int slot, int page) {
                menu.addItem(
                        slot,
                        itemStack,
                        OnClick.RecipeType.create(guide, menu, recipeType));
            }
        }
    }

    interface Item extends OnDisplay {
        DisplayType Normal = DisplayType.Normal, ItemMark = DisplayType.ItemMark, Bookmark = DisplayType.Bookmark, Search = DisplayType.Search;

        static Item Vanilla(Player player, @Nullable SlimefunItem slimefunItem, ItemStack itemStack, JEGSlimefunGuideImplementation guide) {
            return new Vanilla(player, slimefunItem, itemStack, guide);
        }

        static Item Normal(Player player, SlimefunItem item, ItemStack itemStack, JEGSlimefunGuideImplementation guide) {
            return new Normal(player, item, itemStack, guide);
        }

        static Item ItemMark(Player player, SlimefunItem item, JEGSlimefunGuideImplementation guide) {
            return new ItemMark(player, item, guide);
        }

        static Item Bookmark(Player player, SlimefunItem item, JEGSlimefunGuideImplementation guide) {
            return new Bookmark(player, item, guide);
        }

        static Item Search(Player player, SlimefunItem item, JEGSlimefunGuideImplementation guide) {
            return new Search(player, item, guide);
        }

        static Item Research(Player player, SlimefunItem item, JEGSlimefunGuideImplementation guide) {
            return new Research(player, item, guide);
        }

        static Item NoPermission(Player player, SlimefunItem item, JEGSlimefunGuideImplementation guide) {
            return new NoPermission(player, item, guide);
        }

        static Item display(Player player, SlimefunItem item, DisplayType type, SlimefunGuideImplementation guide) {
            if (guide instanceof JEGSlimefunGuideImplementation jeg) return display(player, item, type, jeg);
            return display(player, item.getItem(), type, GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE));
        }

        static Item display(Player player, SlimefunItem item, DisplayType type, JEGSlimefunGuideImplementation guide) {
            return display(player, item, Converter.getItem(item.getItem()), type, guide);
        }

        static Item display(Player player, SlimefunItem slimefunItem, ItemStack itemStack, DisplayType type, JEGSlimefunGuideImplementation guide) {
            if (!JEGSlimefunGuideImplementation.hasPermission0(player, slimefunItem)) {
                return NoPermission(player, slimefunItem, guide);
            }

            if (type == DisplayType.ItemMark) {
                return ItemMark(player, slimefunItem, guide);
            }

            if (type == DisplayType.Bookmark) {
                return Bookmark(player, slimefunItem, guide);
            }

            if (type == DisplayType.Search) {
                return Search(player, slimefunItem, guide);
            }

            if (!Slimefun.getConfigManager().isResearchingEnabled()) {
                return Normal(player, slimefunItem, itemStack, guide);
            }

            io.github.thebusybiscuit.slimefun4.api.researches.Research research = slimefunItem.getResearch();
            if (research == null || !research.isEnabled()) {
                return Normal(player, slimefunItem, itemStack, guide);
            }

            PlayerProfile profile = PlayerProfile.find(player).orElse(null);
            if (profile == null) return Normal(player, slimefunItem, itemStack, guide);

            if (profile.getResearches().contains(research)) {
                if (slimefunItem instanceof VanillaItemShade vis) {
                    return Vanilla(player, slimefunItem, vis.getCustomIcon(), guide);
                }

                return Normal(player, slimefunItem, itemStack, guide);
            }

            if (guide.getMode() == SlimefunGuideMode.CHEAT_MODE) {
                return Normal(player, slimefunItem, itemStack, guide);
            }

            if (player.getGameMode() != GameMode.CREATIVE) {
                return Research(player, slimefunItem, guide);
            }

            if (Slimefun.getConfigManager().isFreeCreativeResearchingEnabled()) {
                return Normal(player, slimefunItem, itemStack, guide);
            }

            return Research(player, slimefunItem, guide);
        }

        static Item display(Player player, ItemStack itemStack, DisplayType type, SlimefunGuideImplementation guide) {
            if (guide instanceof JEGSlimefunGuideImplementation jeg) return display(player, itemStack, type, jeg);
            return display(player, itemStack, type, GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE));
        }

        static Item display(Player player, ItemStack itemStack, DisplayType type, JEGSlimefunGuideImplementation guide) {
            SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
            if (slimefunItem == null) {
                return Vanilla(player, null, itemStack, guide);
            }

            return display(player, slimefunItem, itemStack, type, guide);
        }

        void at(ChestMenu menu, int slot, int page);

        enum DisplayType {
            Normal, ItemMark, Bookmark, Search
        }

        @RequiredArgsConstructor
        @Data
        class Research implements Item {
            private final Player player;
            private final SlimefunItem item;
            private final JEGSlimefunGuideImplementation guide;

            @Override
            public void at(ChestMenu menu, int slot, int page) {
                io.github.thebusybiscuit.slimefun4.api.researches.Research research = item.getResearch();
                if (research == null) return;

                ItemStack icon = Converter.getItem(
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
                                : research.getLevelCost() + " 级经验");

                ItemMeta meta = icon.getItemMeta();
                meta.getPersistentDataContainer().set(JEGSlimefunGuideImplementation.UNLOCK_ITEM_KEY, PersistentDataType.STRING, item.getId());
                icon.setItemMeta(meta);

                menu.addItem(slot, PatchScope.Research.patch(player, icon), OnClick.Item.Research.create(guide, menu, page));
            }
        }

        @SuppressWarnings("UnnecessaryUnicodeEscape")
        @RequiredArgsConstructor
        @Data
        class Search implements Item {
            private final Player player;
            private final SlimefunItem item;
            private final JEGSlimefunGuideImplementation guide;

            @Override
            public void at(ChestMenu menu, int slot, int page) {
                io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup = item.getItemGroup();
                List<String> additionLore = List.of(
                        "",
                        ChatColors.color(String.format("&8\u21E8 &f%s&f - %s", LocalHelper.getAddonName(itemGroup, item.getId()), LocalHelper.getDisplayName(itemGroup, player)))
                );

                ItemStack icon = ItemStackUtil.getCleanItem(Converter.getItem(item instanceof CustomIconDisplay cid ? cid.getCustomIcon() : item.getItem()));
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
                        OnClick.Item.Normal.create(guide, menu, page, item));
            }
        }

        @SuppressWarnings("UnnecessaryUnicodeEscape")
        @RequiredArgsConstructor
        @Data
        class Bookmark implements Item {
            private final Player player;
            private final SlimefunItem item;
            private final JEGSlimefunGuideImplementation guide;

            @Override
            public void at(ChestMenu menu, int slot, int page) {
                io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup = item.getItemGroup();
                List<String> additionLore = List.of(
                        "",
                        ChatColors.color(String.format("&8\u21E8 &f%s&f - %s", LocalHelper.getAddonName(itemGroup, item.getId()), LocalHelper.getDisplayName(itemGroup, player))),
                        ChatColors.color("&e右键以取消收藏物品")
                );

                ItemStack icon = ItemStackUtil.getCleanItem(Converter.getItem(item instanceof CustomIconDisplay cid ? cid.getCustomIcon() : item.getItem()));
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
                        OnClick.Item.Bookmark.create(guide, menu, page));
            }
        }

        @SuppressWarnings("UnnecessaryUnicodeEscape")
        @RequiredArgsConstructor
        @Data
        class ItemMark implements Item {
            private final Player player;
            private final SlimefunItem item;
            private final JEGSlimefunGuideImplementation guide;

            @Override
            public void at(ChestMenu menu, int slot, int page) {
                io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup = item.getItemGroup();
                List<String> additionLore = List.of(
                        "",
                        ChatColors.color(String.format("&8\u21E8 &f%s&f - %s", LocalHelper.getAddonName(itemGroup, item.getId()), LocalHelper.getDisplayName(itemGroup, player))),
                        ChatColors.color("&e左键点击以收藏物品")
                );

                ItemStack icon = ItemStackUtil.getCleanItem(Converter.getItem(item instanceof CustomIconDisplay cid ? cid.getCustomIcon() : item.getItem()));
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
        @Data
        class Normal implements Item {
            private final Player player;
            private final SlimefunItem item;
            private final ItemStack itemStack;
            private final JEGSlimefunGuideImplementation guide;

            @Override
            public void at(ChestMenu menu, int slot, int page) {
                menu.addItem(
                        slot,
                        PatchScope.SlimefunItem.patch(player, ItemStackUtil.getCleanItem(Converter.getItem(item instanceof CustomIconDisplay cid ? cid.getCustomIcon() : itemStack))),
                        OnClick.Item.Normal.create(guide, menu, page, item));
            }
        }

        @RequiredArgsConstructor
        @Data
        class Vanilla implements Item {
            private final Player player;
            @Nullable private final SlimefunItem slimefunItem;
            private final ItemStack itemStack;
            private final JEGSlimefunGuideImplementation guide;

            @Override
            public void at(ChestMenu menu, int slot, int page) {
                menu.addItem(
                        slot,
                        PatchScope.VanillaItem.patch(player, itemStack),
                        OnClick.Item.Normal.create(guide, menu, page, slimefunItem));
            }
        }

        @RequiredArgsConstructor
        @Data
        class NoPermission implements Item {
            private final Player player;
            private final SlimefunItem item;
            private final JEGSlimefunGuideImplementation guide;

            @Override
            public void at(ChestMenu menu, int slot, int page) {
                List<String> message = Slimefun.getPermissionsService().getLore(item);
                menu.addItem(
                        slot,
                        PatchScope.NoPermission.patch(player, Converter.getItem(
                                ChestMenuUtils.getNoPermissionItem(),
                                item.getItemName(),
                                message.toArray(new String[0])
                        )),
                        OnClick.ClickHandler.deny());
            }
        }
    }
}
