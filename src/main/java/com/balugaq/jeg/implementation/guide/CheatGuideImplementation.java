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

package com.balugaq.jeg.implementation.guide;

import city.norain.slimefun4.VaultIntegration;
import com.balugaq.jeg.api.editor.GroupResorter;
import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.interfaces.BookmarkRelocation;
import com.balugaq.jeg.api.interfaces.CustomIconDisplay;
import com.balugaq.jeg.api.interfaces.DisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.VanillaItemShade;
import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.core.listeners.GroupTierEditorListener;
import com.balugaq.jeg.core.listeners.GuideListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.items.GroupTierEditorGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.LocalHelper;
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.SpecialMenuProvider;
import com.balugaq.jeg.utils.clickhandler.BeginnerUtils;
import com.balugaq.jeg.utils.clickhandler.GroupLinker;
import com.balugaq.jeg.utils.clickhandler.NamePrinter;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.compatibility.Sounds;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import com.balugaq.jeg.utils.formatter.RecipeDisplayFormat;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.LockedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SeasonalItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlock;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.CheatSheetSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AsyncRecipeChoiceTask;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.libraries.dough.recipes.MinecraftRecipe;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.SlimefunGuideItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.MenuClickHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is JEG's implementation of the Cheat Guide.
 * It extends {@link CheatSheetSlimefunGuide} to compatibly
 * with the Slimefun API and other plugins.
 * It also implements the {@link JEGSlimefunGuideImplementation}
 * to provide a common interface for both
 * {@link SurvivalGuideImplementation} and {@link CheatGuideImplementation}.
 * <p>
 * Also used to editor group tiers in {@link GroupTierEditorGuide}
 *
 * @author TheBusyBiscuit
 * @author balugaq
 * @see SlimefunGuide
 * @see SlimefunGuideImplementation
 * @see SurvivalSlimefunGuide
 * @see CheatSheetSlimefunGuide
 * @see JEGSlimefunGuideImplementation
 * @see SurvivalGuideImplementation
 * @see GroupTierEditorGuide
 * @see GroupTierEditorListener
 * @since 1.0
 */
@SuppressWarnings({"deprecation", "unused", "UnnecessaryUnicodeEscape", "ConstantValue"})
public class CheatGuideImplementation extends CheatSheetSlimefunGuide implements JEGSlimefunGuideImplementation {
    @Deprecated
    public static final int MAX_ITEM_GROUPS = SurvivalGuideImplementation.MAX_ITEM_GROUPS;

    @Deprecated
    public static final int MAX_ITEMS = SurvivalGuideImplementation.MAX_ITEMS;

    @Deprecated
    public static final int RTS_SLOT = 6;

    @Deprecated
    public static final ItemStack RTS_ITEM = Models.RTS_ITEM;

    @Deprecated
    public static final int SPECIAL_MENU_SLOT = 26;

    @Deprecated
    public static final ItemStack SPECIAL_MENU_ITEM = Models.SPECIAL_MENU_ITEM;

    @Deprecated
    public final int[] recipeSlots = SurvivalGuideImplementation.recipeSlots;

    public final @NotNull ItemStack item;

    public CheatGuideImplementation() {
        ItemMeta meta = SlimefunGuide.getItem(getMode()).getItemMeta();
        String name = "";
        if (meta != null) {
            name = meta.getDisplayName();
        }
        item = new SlimefunGuideItem(this, name);
    }

    // fallback
    @Deprecated
    public CheatGuideImplementation(boolean v1, boolean v2) {
        ItemMeta meta = SlimefunGuide.getItem(getMode()).getItemMeta();
        String name = "";
        if (meta != null) {
            name = meta.getDisplayName();
        }
        item = new SlimefunGuideItem(this, name);
    }

    @ParametersAreNonnullByDefault
    public static @NotNull ItemStack getDisplayItem(Player p, boolean isSlimefunRecipe, ItemStack item) {
        if (isSlimefunRecipe) {
            SlimefunItem slimefunItem = SlimefunItem.getByItem(item);

            if (slimefunItem == null) {
                return item;
            }

            ItemGroup itemGroup = slimefunItem.getItemGroup();
            String lore = hasPermission(p, slimefunItem)
                    ? String.format(
                    "&f需要在 %s 中解锁",
                    (LocalHelper.getAddonName(itemGroup, slimefunItem.getId())) + ChatColor.WHITE + " - "
                            + LocalHelper.getDisplayName(itemGroup, p))
                    : "&f无权限";
            return ItemStackUtil.getCleanItem(
                    slimefunItem.canUse(p, false)
                            ? item
                            : Converter.getItem(
                            Material.BARRIER,
                            ItemUtils.getItemName(item),
                            "&4&l" + Slimefun.getLocalization().getMessage(p, "guide.locked"),
                            "",
                            lore));
        } else {
            return item;
        }
    }

    @ParametersAreNonnullByDefault
    public static boolean hasPermission(Player p, SlimefunItem item) {
        return Slimefun.getPermissionsService().hasPermission(p, item);
    }

    public static boolean isTaggedGroupType(@NotNull ItemGroup itemGroup) {
        Class<?> clazz = itemGroup.getClass();
        return clazz == ItemGroup.class
                || clazz == SubItemGroup.class
                || clazz == NestedItemGroup.class
                || clazz == LockedItemGroup.class
                || clazz == SeasonalItemGroup.class
                || clazz == SearchGroup.class
                || itemGroup instanceof BookmarkRelocation
                || clazz.getName().equalsIgnoreCase("me.voper.slimeframe.implementation.groups.ChildGroup")
                || clazz.getName().endsWith("DummyItemGroup")
                || clazz.getName().endsWith("SubGroup");
    }

    @Override
    public @NotNull SlimefunGuideMode getMode() {
        return SlimefunGuideMode.CHEAT_MODE;
    }

    @Override
    public @NotNull ItemStack getItem() {
        return item;
    }

    /**
     * Returns a {@link List} of visible {@link ItemGroup} instances that the {@link SlimefunGuide} would display.
     *
     * @param p       The {@link Player} who opened his {@link SlimefunGuide}
     * @param profile The {@link PlayerProfile} of the {@link Player}
     * @return a {@link List} of visible {@link ItemGroup} instances
     */
    @Override
    public @NotNull List<ItemGroup> getVisibleItemGroups(@NotNull Player p, @NotNull PlayerProfile profile) {
        return getVisibleItemGroups(p, profile, false);
    }

    /**
     * Returns a {@link List} of visible {@link ItemGroup} instances that the {@link SlimefunGuide} would display.
     *
     * @param p             The {@link Player} who opened his {@link SlimefunGuide}
     * @param profile       The {@link PlayerProfile} of the {@link Player}
     * @param guideTierMode Whether the guide is in tier mode
     * @return a {@link List} of visible {@link ItemGroup} instances
     */
    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    public @NotNull List<ItemGroup> getVisibleItemGroups(
            @NotNull Player p, @NotNull PlayerProfile profile, boolean guideTierMode) {
        List<ItemGroup> groups = new LinkedList<>();
        List<ItemGroup> specialGroups = new LinkedList<>();
        for (ItemGroup group : Slimefun.getRegistry().getAllItemGroups()) {
            try {
                if (group.getClass().isAnnotationPresent(NotDisplayInCheatMode.class)) {
                    continue;
                }
                if (group.getClass().isAnnotationPresent(DisplayInCheatMode.class)) {
                    groups.add(group);
                    continue;
                }

                if (!guideTierMode && GuideUtil.isForceHidden(group)) {
                    continue;
                }

                if (DisplayInCheatMode.Checker.contains(group)) {
                    if (DisplayInCheatMode.Checker.isSpecial(group)) {
                        specialGroups.add(group);
                        continue;
                    } else {
                        groups.add(group);
                        continue;
                    }
                } else if (NotDisplayInCheatMode.Checker.contains(group)) {
                    continue;
                }

                if (group instanceof SeasonalItemGroup) {
                    specialGroups.add(group);
                } else {
                    if (!group.isHidden(p)) {
                        groups.add(group);
                    } else {
                        specialGroups.add(group);
                    }
                }
            } catch (Exception | LinkageError x) {
                SlimefunAddon addon = group.getAddon();
                Logger logger = addon != null
                        ? addon.getLogger()
                        : JustEnoughGuide.getInstance().getLogger();
                logger.log(Level.SEVERE, x, () -> "Could not display item group: " + group);
            }
        }

        GroupResorter.sort(groups);
        groups.addAll(specialGroups);

        return groups;
    }

    /**
     * Opens the main menu of the guide for a player.
     *
     * @param profile The player profile associated with the player.
     * @param page    The page number to open.
     */
    @Override
    public void openMainMenu(@NotNull PlayerProfile profile, int page) {
        Player p = profile.getPlayer();

        if (p == null) {
            return;
        }

        GuideHistory history = profile.getGuideHistory();
        history.clear();
        history.setMainMenuPage(page);

        ChestMenu menu = create0(p);
        List<ItemGroup> itemGroups = getVisibleItemGroups(p, profile, GroupResorter.isSelecting(p));

        createHeader(p, profile, menu, Formats.main);

        int target = (SurvivalGuideImplementation.MAX_ITEM_GROUPS * (page - 1)) - 1;
        int pages = target == itemGroups.size() - 1
                ? page
                : (itemGroups.size() - 1) / SurvivalGuideImplementation.MAX_ITEM_GROUPS + 1;
        if (page > pages) {
            page = pages;
        }

        List<Integer> indexes = Formats.main.getChars('G');
        int index = 0;

        while (target < (itemGroups.size() - 1) && index < SurvivalGuideImplementation.MAX_ITEM_GROUPS) {
            target++;

            ItemGroup group = itemGroups.get(target);
            showItemGroup0(menu, p, profile, group, indexes.get(index), page);

            index++;
        }

        for (int s : Formats.main.getChars('P')) {
            menu.addItem(s, PatchScope.PreviousPage.patch(p, ChestMenuUtils.getPreviousButton(p, page, pages)));
            int finalPage1 = page;
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> {
                int previous = finalPage1 - 1;

                if (previous > 0) {
                    openMainMenu(profile, previous);
                }

                return false;
            });
        }

        for (int s : Formats.main.getChars('N')) {
            menu.addItem(s, PatchScope.NextPage.patch(p, ChestMenuUtils.getNextButton(p, page, pages)));
            int finalPage = page;
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> {
                int next = finalPage + 1;

                if (next <= pages) {
                    openMainMenu(profile, next);
                }

                return false;
            });
        }

        GuideListener.guideModeMap.put(p, getMode());

        menu.open(p);
    }

    @Override
    public void showItemGroup0(
            final @NotNull ChestMenu menu,
            @NotNull Player p,
            @NotNull PlayerProfile profile,
            @NotNull ItemGroup group,
            int index) {
        showItemGroup0(menu, p, profile, group, index, 1);
    }

    public void showItemGroup0(
            final @NotNull ChestMenu menu,
            @NotNull Player p,
            @NotNull PlayerProfile profile,
            @NotNull ItemGroup group,
            int index,
            int page) {
        if (!(group instanceof LockedItemGroup)
                || !isSurvivalMode()
                || ((LockedItemGroup) group).hasUnlocked(p, profile)) {
            menu.addItem(index, PatchScope.ItemGroup.patch(p, group.getItem(p)));
            menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
                if (action.isRightClicked() && GroupResorter.isSelecting(pl)) {
                    ItemGroup selected = GroupResorter.getSelectedGroup(pl);
                    if (selected == null) {
                        GroupResorter.setSelectedGroup(pl, group);
                        pl.sendMessage(ChatColors.color("&a已选择物品组: &e" + group.getDisplayName(pl)));
                    } else {
                        GroupResorter.swap(selected, group);
                        GroupResorter.setSelectedGroup(pl, null);
                        pl.sendMessage(ChatColors.color("&a已交换物品组排序: &e" + selected.getDisplayName(pl) + " &7<-> &e"
                                + group.getDisplayName(pl)));
                        openMainMenu(profile, page);
                    }
                    return false;
                } else {
                    openItemGroup(profile, group, 1);
                }
                return false;
            });
        } else {
            List<String> lore = new ArrayList<>();
            lore.add("");

            for (String line : Slimefun.getLocalization().getMessages(p, "guide.locked-itemgroup")) {
                lore.add(ChatColor.WHITE + line);
            }

            lore.add("");

            for (ItemGroup parent : ((LockedItemGroup) group).getParents()) {
                ItemMeta meta = parent.getItem(p).getItemMeta();
                if (meta == null) {
                    continue;
                }
                lore.add(meta.getDisplayName());
            }

            ItemMeta meta = group.getItem(p).getItemMeta();
            if (meta == null) {
                return;
            }

            menu.addItem(
                    index,
                    PatchScope.LockedItemGroup.patch(
                            p,
                            Converter.getItem(
                                    Material.BARRIER,
                                    "&4"
                                            + Slimefun.getLocalization().getMessage(p, "guide.locked")
                                            + " &7- &f"
                                            + meta.getDisplayName(),
                                    lore.toArray(new String[0]))));
            menu.addMenuClickHandler(index, ChestMenuUtils.getEmptyClickHandler());
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void openItemGroup(PlayerProfile profile, ItemGroup itemGroup, int page) {
        Player p = profile.getPlayer();

        if (p == null) {
            return;
        }

        if (itemGroup instanceof NestedItemGroup nested && itemGroup.getClass() == NestedItemGroup.class) {
            openNestedItemGroup(p, profile, nested, page);
            return;
        }

        if (itemGroup instanceof FlexItemGroup flexItemGroup) {
            flexItemGroup.open(p, profile, getMode());
            return;
        }

        if (isSurvivalMode()) {
            profile.getGuideHistory().add(itemGroup, page);
        }

        ChestMenu menu = create0(p);
        createHeader(p, profile, menu, Formats.sub);

        int pages = (itemGroup.getItems().size() - 1) / SurvivalGuideImplementation.MAX_ITEMS + 1;

        for (int s : Formats.sub.getChars('P')) {
            menu.addItem(s, PatchScope.PreviousPage.patch(p, ChestMenuUtils.getPreviousButton(p, page, pages)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.PreviousButtonClickEvent(pl, item, slot, action, menu, this))
                    .ifSuccess(() -> {
                        int previous = page - 1;

                        if (previous > 0) {
                            openItemGroup(profile, itemGroup, previous);
                        }

                        return false;
                    }));
        }

        for (int s : Formats.sub.getChars('N')) {
            menu.addItem(s, PatchScope.NextPage.patch(p, ChestMenuUtils.getNextButton(p, page, pages)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.NextButtonClickEvent(pl, item, slot, action, menu, this))
                    .ifSuccess(() -> {
                        int next = page + 1;

                        if (next <= pages) {
                            openItemGroup(profile, itemGroup, next);
                        }

                        return false;
                    }));
        }

        List<Integer> indexes = Formats.sub.getChars('i');
        int itemGroupIndex = SurvivalGuideImplementation.MAX_ITEMS * (page - 1);

        for (int i = 0; i < SurvivalGuideImplementation.MAX_ITEMS; i++) {
            int target = itemGroupIndex + i;

            if (target >= itemGroup.getItems().size()) {
                break;
            }

            SlimefunItem sfitem = itemGroup.getItems().get(target);

            if (!sfitem.isDisabledIn(p.getWorld())) {
                displaySlimefunItem0(menu, itemGroup, p, profile, sfitem, page, indexes.get(i));
            }
        }

        GuideUtil.addRTSButton(menu, p, profile, Formats.sub, getMode(), this);
        GuideUtil.addBookMarkButton(menu, p, profile, Formats.sub, this, itemGroup);
        GuideUtil.addItemMarkButton(menu, p, profile, Formats.sub, this, itemGroup);

        menu.open(p);
    }

    @Override
    public void openNestedItemGroup(
            @NotNull Player p, @NotNull PlayerProfile profile, @NotNull NestedItemGroup nested, int page) {
        GuideHistory history = profile.getGuideHistory();

        history.add(nested, page);

        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.title.main"));

        menu.setEmptySlotsClickable(false);

        menu.addMenuOpeningHandler(p2 -> {
            try {
                Sounds.playFor(p2, Sounds.GUIDE_BUTTON_CLICK_SOUND);
            } catch (Exception ignored) {
            }
        });

        this.createHeader(p, profile, menu, Formats.nested);

        List<Integer> ss = Formats.nested.getChars('G');
        int groupsPerPage = ss.size();

        try {
            @SuppressWarnings("unchecked")
            List<SubItemGroup> subGroups = (List<SubItemGroup>) ReflectionUtil.getValue(nested, "subGroups");
            if (subGroups == null) {
                return;
            }

            int t = 0;
            int target = groupsPerPage * (page - 1) - 1;
            for (int i = 0; i < subGroups.size() && t < groupsPerPage; i++) {
                target = groupsPerPage * (page - 1) + i;
                if (target >= subGroups.size()) {
                    break;
                }

                SubItemGroup subGroup = subGroups.get(target);
                if (subGroup.isVisibleInNested(p)) {
                    menu.addItem(ss.get(t), PatchScope.ItemGroup.patch(p, subGroup.getItem(p)));
                    menu.addMenuClickHandler(ss.get(t), (pl, slot, item, action) -> EventUtil.callEvent(
                                    new GuideEvents.ItemGroupButtonClickEvent(pl, item, slot, action, menu, this))
                            .ifSuccess(() -> {
                                if (GroupResorter.isSelecting(pl)) {
                                    ItemGroup selected = GroupResorter.getSelectedGroup(pl);
                                    if (selected == null) {
                                        GroupResorter.setSelectedGroup(pl, subGroup);
                                        pl.sendMessage(ChatColors.color("&a已选择物品组: &e" + subGroup.getDisplayName(pl)));
                                    } else {
                                        GroupResorter.swap(selected, subGroup);
                                        GroupResorter.setSelectedGroup(pl, null);
                                        pl.sendMessage(ChatColors.color("&a已交换物品组排序: &e" + selected.getDisplayName(pl)
                                                + " &7<-> &e" + subGroup.getDisplayName(pl)));
                                        openMainMenu(profile, page);
                                    }
                                    return false;
                                }
                                SlimefunGuide.openItemGroup(profile, subGroup, getMode(), 1);
                                return false;
                            }));
                    t += 1;
                }
            }

            int pages = target == subGroups.size() - 1 ? page : (subGroups.size() - 1) / groupsPerPage + 1;
            for (int s : Formats.nested.getChars('P')) {
                menu.addItem(s, PatchScope.PreviousPage.patch(p, ChestMenuUtils.getPreviousButton(p, page, pages)));
                menu.addMenuClickHandler(s, (pl, slot, item, action) -> EventUtil.callEvent(
                                new GuideEvents.PreviousButtonClickEvent(pl, item, slot, action, menu, this))
                        .ifSuccess(() -> {
                            int next = page - 1;
                            if (next > 0) {
                                this.openNestedItemGroup(p, profile, nested, next);
                            }

                            return false;
                        }));
            }

            for (int s : Formats.nested.getChars('N')) {
                menu.addItem(s, PatchScope.NextPage.patch(p, ChestMenuUtils.getNextButton(p, page, pages)));
                menu.addMenuClickHandler(s, (pl, slot, item, action) -> EventUtil.callEvent(
                                new GuideEvents.NextButtonClickEvent(pl, item, slot, action, menu, this))
                        .ifSuccess(() -> {
                            int next = page + 1;
                            if (next <= pages) {
                                this.openNestedItemGroup(p, profile, nested, next);
                            }

                            return false;
                        }));
            }

            menu.open(p);
        } catch (Exception e) {
            Debug.trace(e);
        }
    }

    @Override
    public void displaySlimefunItem0(
            final @NotNull ChestMenu menu,
            final @NotNull ItemGroup itemGroup,
            final @NotNull Player p,
            final @NotNull PlayerProfile profile,
            final @NotNull SlimefunItem sfitem,
            int page,
            int index) {
        Research research = sfitem.getResearch();

        if (isSurvivalMode() && !hasPermission(p, sfitem)) {
            List<String> message = Slimefun.getPermissionsService().getLore(sfitem);
            menu.addItem(
                    index,
                    PatchScope.NoPermission.patch(
                            p,
                            Converter.getItem(
                                    ChestMenuUtils.getNoPermissionItem(),
                                    sfitem.getItemName(),
                                    message.toArray(new String[0]))));
            menu.addMenuClickHandler(index, ChestMenuUtils.getEmptyClickHandler());
        } else if (isSurvivalMode() && research != null && !profile.hasUnlocked(research)) {
            String lore;

            if (VaultIntegration.isEnabled()) {
                lore = String.format("%.2f", research.getCurrencyCost()) + " 游戏币";
            } else {
                lore = research.getLevelCost() + " 级经验";
            }

            menu.addItem(
                    index,
                    PatchScope.LockedItem.patch(
                            p,
                            Converter.getItem(
                                    ChestMenuUtils.getNoPermissionItem(),
                                    "&f" + ItemUtils.getItemName(sfitem.getItem()),
                                    "&7" + sfitem.getId(),
                                    "&4&l" + Slimefun.getLocalization().getMessage(p, "guide.locked"),
                                    "",
                                    "&a> 单击解锁",
                                    "",
                                    "&7需要 &b",
                                    lore)));
            menu.addMenuClickHandler(index, (pl, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.ResearchItemEvent(pl, item, slot, action, menu, this))
                    .ifSuccess(() -> {
                        research.unlockFromGuide(this, p, profile, sfitem, itemGroup, page);
                        return false;
                    }));
        } else {
            if (sfitem instanceof CustomIconDisplay cid) {
                menu.addItem(index, PatchScope.SlimefunItem.patch(p, cid.getCustomIcon()));
            } else {
                menu.addItem(index, PatchScope.SlimefunItem.patch(p, sfitem.getItem()));
            }
            menu.addMenuClickHandler(index, (pl, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.ItemButtonClickEvent(pl, item, slot, action, menu, this))
                    .ifSuccess(() -> {
                        try {
                            if (isSurvivalMode()) {
                                displayItem(profile, sfitem, true);
                            } else if (pl.isOp() || pl.hasPermission("slimefun.cheat.items")) {
                                if (sfitem instanceof MultiBlockMachine) {
                                    Slimefun.getLocalization().sendMessage(pl, "guide.cheat.no-multiblocks");
                                } else {
                                    ItemStack clonedItem = sfitem.getItem().clone();

                                    if (action.isShiftClicked()) {
                                        clonedItem.setAmount(clonedItem.getMaxStackSize());
                                    }

                                    pl.getInventory().addItem(clonedItem);
                                }
                            } else {
                                /*
                                 * Fixes #3548 - If for whatever reason,
                                 * an unpermitted players gets access to this guide,
                                 * this will be our last line of defense to prevent any exploit.
                                 */
                                Slimefun.getLocalization().sendMessage(pl, "messages.no-permission", true);
                            }
                        } catch (Exception | LinkageError x) {
                            printErrorMessage0(pl, sfitem, x);
                        }

                        return false;
                    }));
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void openSearch(PlayerProfile profile, String input, boolean addToHistory) {
        openSearch(profile, input, 0, addToHistory);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void openSearch(PlayerProfile profile, String input, int page, boolean addToHistory) {
        Player p = profile.getPlayer();

        if (p == null) {
            return;
        }

        String searchTerm = ChatColor.stripColor(input.toLowerCase(Locale.ROOT));
        SearchGroup.searchTerms.put(p.getUniqueId(), searchTerm);
        SearchGroup group = new SearchGroup(
                this, p, searchTerm, JustEnoughGuide.getConfigManager().isPinyinSearch(), true);
        group.open(p, profile, getMode());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void displayItem(PlayerProfile profile, ItemStack item, int index, boolean addToHistory) {
        Player p = profile.getPlayer();

        if (p == null || item == null || item.getType() == Material.AIR) {
            return;
        }

        SlimefunItem sfItem = SlimefunItem.getByItem(item);

        if (sfItem != null && !(sfItem instanceof VanillaItemShade)) {
            displayItem(profile, sfItem, addToHistory);
            return;
        }

        // Not SlimefunItem, or VanillaItemShade
        if (!Slimefun.getConfigManager().isShowVanillaRecipes()) {
            return;
        }

        Recipe[] recipes = Slimefun.getMinecraftRecipeService().getRecipesFor(item);

        if (recipes.length == 0) {
            return;
        }

        showMinecraftRecipe0(recipes, index, item, profile, p, addToHistory);
    }

    @Override
    public void showMinecraftRecipe0(
            Recipe @NotNull [] recipes,
            int index,
            final @NotNull ItemStack item,
            final @NotNull PlayerProfile profile,
            final @NotNull Player p,
            boolean addToHistory) {
        Recipe recipe = recipes[index];

        ItemStack[] recipeItems = new ItemStack[9];
        RecipeType recipeType = RecipeType.NULL;
        ItemStack result = null;

        Optional<MinecraftRecipe<? super Recipe>> optional = MinecraftRecipe.of(recipe);
        AsyncRecipeChoiceTask task = new AsyncRecipeChoiceTask();

        if (optional.isPresent()) {
            showRecipeChoices0(recipe, recipeItems, task);

            recipeType = new RecipeType(optional.get());
            result = recipe.getResult();
        } else {
            recipeItems = new ItemStack[]{
                    null,
                    null,
                    null,
                    null,
                    ItemStackUtil.getCleanItem(
                            Converter.getItem(Material.BARRIER, "&4We are somehow unable to show you this Recipe :/")),
                    null,
                    null,
                    null,
                    null
            };
        }

        ChestMenu menu = create0(p);

        if (addToHistory) {
            profile.getGuideHistory().add(item, index);
        }

        displayItem(menu, profile, p, item, result, recipeType, recipeItems, task, Formats.recipe_vanilla);

        if (recipes.length > 1) {
            for (int s : Formats.recipe_vanilla.getChars('B')) {
                menu.addItem(
                        s,
                        PatchScope.Background.patch(p, ChestMenuUtils.getBackground()),
                        ChestMenuUtils.getEmptyClickHandler());
            }

            for (int s : Formats.recipe_vanilla.getChars('P')) {
                menu.addItem(
                        s,
                        PatchScope.PreviousPage.patch(
                                p, ChestMenuUtils.getPreviousButton(p, index + 1, recipes.length)),
                        (pl, slot, stack, action) -> EventUtil.callEvent(
                                        new GuideEvents.PreviousButtonClickEvent(pl, stack, slot, action, menu, this))
                                .ifSuccess(() -> {
                                    if (index > 0) {
                                        showMinecraftRecipe0(recipes, index - 1, item, profile, p, true);
                                    }
                                    return false;
                                }));
            }

            for (int s : Formats.recipe_vanilla.getChars('N')) {
                menu.addItem(
                        s,
                        PatchScope.NextPage.patch(p, ChestMenuUtils.getNextButton(p, index + 1, recipes.length)),
                        (pl, slot, stack, action) -> EventUtil.callEvent(
                                        new GuideEvents.NextButtonClickEvent(pl, stack, slot, action, menu, this))
                                .ifSuccess(() -> {
                                    if (index < recipes.length - 1) {
                                        showMinecraftRecipe0(recipes, index + 1, item, profile, p, true);
                                    }
                                    return false;
                                }));
            }
        }

        Formats.recipe_vanilla.renderCustom(menu);

        menu.open(p);

        if (!task.isEmpty()) {
            task.start(menu.toInventory());
        }
    }

    @Override
    public <T extends Recipe> void showRecipeChoices0(
            final @NotNull T recipe, ItemStack[] recipeItems, @NotNull AsyncRecipeChoiceTask task) {
        RecipeChoice[] choices = Slimefun.getMinecraftRecipeService().getRecipeShape(recipe);

        List<Integer> recipeSlots = Formats.recipe_vanilla.getChars('r');
        if (choices.length == 1 && choices[0] instanceof MaterialChoice materialChoice) {
            recipeItems[4] = new ItemStack(materialChoice.getChoices().get(0));

            if (materialChoice.getChoices().size() > 1) {
                task.add(recipeSlots.get(4), materialChoice);
            }
        } else {
            for (int i = 0; i < choices.length; i++) {
                if (choices[i] instanceof MaterialChoice materialChoice) {
                    recipeItems[i] = new ItemStack(materialChoice.getChoices().get(0));

                    if (materialChoice.getChoices().size() > 1) {
                        task.add(recipeSlots.get(i), materialChoice);
                    }
                }
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void displayItem(PlayerProfile profile, SlimefunItem item, boolean addToHistory) {
        displayItem(profile, item, addToHistory, true);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void displayItem(PlayerProfile profile, SlimefunItem item, boolean addToHistory, boolean maybeSpecial) {
        displayItem(
                profile,
                item,
                addToHistory,
                maybeSpecial,
                item instanceof RecipeDisplayItem ? Formats.recipe_display : Formats.recipe);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void displayItem(
            PlayerProfile profile, SlimefunItem item, boolean addToHistory, boolean maybeSpecial, Format format) {
        Player p = profile.getPlayer();

        if (p == null) {
            return;
        }

        ChestMenu menu = create0(p);
        Optional<String> wiki = item.getWikipage();

        if (wiki.isPresent()) {
            for (int s : format.getChars('w')) {
                menu.addItem(
                        s,
                        PatchScope.ItemWiki.patch(
                                p,
                                Converter.getItem(
                                        Material.KNOWLEDGE_BOOK,
                                        ChatColor.WHITE
                                                + Slimefun.getLocalization().getMessage(p, "guide.tooltips.wiki"),
                                        "",
                                        ChatColor.GRAY
                                                + "\u21E8 "
                                                + ChatColor.GREEN
                                                + Slimefun.getLocalization()
                                                .getMessage(p, "guide.tooltips.open-itemgroup"))));
                menu.addMenuClickHandler(s, (pl, slot, itemstack, action) -> EventUtil.callEvent(
                                new GuideEvents.WikiButtonClickEvent(pl, itemstack, slot, action, menu, this))
                        .ifSuccess(() -> {
                            pl.closeInventory();
                            ChatUtils.sendURL(pl, wiki.get());
                            return false;
                        }));
            }
        }

        AsyncRecipeChoiceTask task = new AsyncRecipeChoiceTask();

        if (addToHistory) {
            profile.getGuideHistory().add(item);
        }

        ItemStack result = item.getRecipeOutput();
        RecipeType recipeType = item.getRecipeType();
        ItemStack[] recipe = item.getRecipe();

        displayItem(menu, profile, p, item, result, recipeType, recipe, task, format);

        if (item instanceof RecipeDisplayItem recipeDisplayItem) {
            displayRecipes0(p, profile, menu, recipeDisplayItem, 0);
        }

        if (maybeSpecial && SpecialMenuProvider.isSpecialItem(item)) {
            for (int s : format.getChars('E')) {
                menu.addItem(
                        s,
                        PatchScope.BigRecipe.patch(p, Models.SPECIAL_MENU_ITEM),
                        (pl, slot, itemstack, action) -> EventUtil.callEvent(new GuideEvents.BigRecipeButtonClickEvent(
                                        pl, itemstack, slot, action, menu, this))
                                .ifSuccess(() -> {
                                    try {
                                        SpecialMenuProvider.open(profile.getPlayer(), profile, getMode(), item);
                                    } catch (InstantiationException
                                             | IllegalAccessException
                                             | InvocationTargetException e) {
                                        Debug.trace(e);
                                    }
                                    return false;
                                }));
            }
        }

        format.renderCustom(menu);

        GuideUtil.addCerButton(menu, p, profile, item, this, format);

        menu.open(p);

        if (!task.isEmpty()) {
            task.start(menu.toInventory());
        }
    }

    @Override
    public void displayItem0(
            final @NotNull ChestMenu menu,
            final @NotNull PlayerProfile profile,
            final @NotNull Player p,
            Object item,
            ItemStack output,
            final @NotNull RecipeType recipeType,
            ItemStack[] recipe,
            final @NotNull AsyncRecipeChoiceTask task) {
        displayItem(menu, profile, p, item, output, recipeType, recipe, task, Formats.recipe);
    }

    @Override
    public void displayItem(
            final @NotNull ChestMenu menu,
            final @NotNull PlayerProfile profile,
            final @NotNull Player p,
            Object item,
            ItemStack output,
            final @NotNull RecipeType recipeType,
            ItemStack[] recipe,
            final @NotNull AsyncRecipeChoiceTask task,
            final @NotNull Format format) {
        for (int s : format.getChars('b')) {
            addBackButton0(menu, s, p, profile);
        }

        MenuClickHandler clickHandler = (pl, slot, itemstack, action) -> EventUtil.callEvent(
                        new GuideEvents.ItemButtonClickEvent(pl, itemstack, slot, action, menu, this))
                .ifSuccess(() -> {
                    try {
                        if (itemstack != null && itemstack.getType() != Material.BARRIER) {
                            displayItem(profile, itemstack, 0, true);
                        }
                    } catch (Exception | LinkageError x) {
                        printErrorMessage0(pl, x);
                    }
                    return false;
                });

        boolean isSlimefunRecipe = item instanceof SlimefunItem && !(item instanceof VanillaItemShade);

        List<Integer> recipeSlots = format.getChars('r');
        for (int i = 0; i < 9; i++) {
            ItemStack recipeItem = getDisplayItem(p, isSlimefunRecipe, recipe[i]);
            menu.addItem(recipeSlots.get(i), PatchScope.ItemRecipeIngredient.patch(p, recipeItem), clickHandler);
            BeginnerUtils.applyWith(this, menu, recipeSlots.get(i));
            GroupLinker.applyWith(this, menu, recipeSlots.get(i));
            NamePrinter.applyWith(this, menu, recipeSlots.get(i));

            if (recipeItem != null && item instanceof MultiBlockMachine) {
                for (Tag<Material> tag : MultiBlock.getSupportedTags()) {
                    if (tag.isTagged(recipeItem.getType())) {
                        task.add(recipeSlots.get(i), tag);
                        break;
                    }
                }
            }
        }

        for (int s : format.getChars('t')) {
            menu.addItem(
                    s,
                    PatchScope.ItemRecipeType.patch(p, recipeType.getItem(p)),
                    (pl, slot, itemStack, action) -> EventUtil.callEvent(
                                    new GuideEvents.RecipeTypeButtonClickEvent(pl, itemStack, slot, action, menu, this))
                            .ifSuccess(false));
            BeginnerUtils.applyWith(this, menu, s);
            GroupLinker.applyWith(this, menu, s);
            NamePrinter.applyWith(this, menu, s);
        }
        for (int s : format.getChars('i')) {
            menu.addItem(
                    s, PatchScope.ItemRecipeOut.patch(p, output), (pl, slot, itemStack, action) -> EventUtil.callEvent(
                                    new GuideEvents.ItemButtonClickEvent(pl, itemStack, slot, action, menu, this))
                            .ifSuccess(false));
            BeginnerUtils.applyWith(this, menu, s);
            GroupLinker.applyWith(this, menu, s);
            NamePrinter.applyWith(this, menu, s);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void createHeader(Player p, PlayerProfile profile, ChestMenu menu) {
        createHeader(p, profile, menu, Formats.main);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void createHeader(Player p, PlayerProfile profile, ChestMenu menu, Format format) {
        for (int s : format.getChars('B')) {
            menu.addItem(
                    s,
                    PatchScope.Background.patch(p, ChestMenuUtils.getBackground()),
                    ChestMenuUtils.getEmptyClickHandler());
        }

        for (int s : format.getChars('b')) {
            addBackButton0(menu, s, p, profile);
        }

        // Settings Panel
        for (int s : format.getChars('T')) {
            menu.addItem(s, PatchScope.Settings.patch(p, ChestMenuUtils.getMenuButton(p)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.SettingsButtonClickEvent(pl, item, slot, action, menu, this))
                    .ifSuccess(() -> {
                        JEGGuideSettings.openSettings(pl, pl.getInventory().getItemInMainHand());
                        return false;
                    }));
        }

        // Search feature!
        for (int s : format.getChars('S')) {
            menu.addItem(s, PatchScope.Search.patch(p, ChestMenuUtils.getSearchButton(p)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.SearchButtonClickEvent(pl, item, slot, action, menu, this))
                    .ifSuccess(() -> {
                        pl.closeInventory();

                        Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                        ChatInput.waitForPlayer(
                                JustEnoughGuide.getInstance(), pl, msg -> openSearch(profile, msg, isSurvivalMode()));

                        return false;
                    }));
        }

        GuideUtil.addRTSButton(menu, p, profile, format, getMode(), this);
        GuideUtil.addBookMarkButton(menu, p, profile, format, this, null);
        GuideUtil.addItemMarkButton(menu, p, profile, format, this, null);

        format.renderCustom(menu);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void createHeader(Player p, PlayerProfile profile, ChestMenu menu, ItemGroup itemGroup) {
        for (int s : Formats.main.getChars('B')) {
            menu.addItem(
                    s,
                    PatchScope.Background.patch(p, ChestMenuUtils.getBackground()),
                    ChestMenuUtils.getEmptyClickHandler());
        }

        // Settings Panel
        for (int s : Formats.main.getChars('T')) {
            menu.addItem(s, PatchScope.Settings.patch(p, ChestMenuUtils.getMenuButton(p)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.SettingsButtonClickEvent(pl, item, slot, action, menu, this))
                    .ifSuccess(() -> {
                        JEGGuideSettings.openSettings(pl, pl.getInventory().getItemInMainHand());
                        return false;
                    }));
        }

        // Search feature!
        for (int s : Formats.main.getChars('S')) {
            menu.addItem(s, PatchScope.Search.patch(p, ChestMenuUtils.getSearchButton(p)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.SearchButtonClickEvent(pl, item, slot, action, menu, this))
                    .ifSuccess(() -> {
                        pl.closeInventory();

                        Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                        ChatInput.waitForPlayer(
                                JustEnoughGuide.getInstance(), pl, msg -> openSearch(profile, msg, isSurvivalMode()));

                        return false;
                    }));
        }

        GuideUtil.addRTSButton(menu, p, profile, Formats.main, getMode(), this);
        GuideUtil.addBookMarkButton(menu, p, profile, Formats.main, this, itemGroup);
        GuideUtil.addItemMarkButton(menu, p, profile, Formats.main, this, itemGroup);
    }

    @Override
    public void addBackButton0(@NotNull ChestMenu menu, int slot, @NotNull Player p, @NotNull PlayerProfile profile) {
        GuideHistory history = profile.getGuideHistory();

        if (isSurvivalMode() && history.size() > 1) {
            menu.addItem(
                    slot,
                    PatchScope.Back.patch(
                            p, ChestMenuUtils.getBackButton(p, "", "&f左键: &7返回上一页", "&fShift + 左键: &7返回主菜单")));

            menu.addMenuClickHandler(slot, (pl, s, is, action) -> EventUtil.callEvent(
                            new GuideEvents.BackButtonClickEvent(pl, is, s, action, menu, this))
                    .ifSuccess(() -> {
                        if (action.isShiftClicked()) {
                            openMainMenu(profile, profile.getGuideHistory().getMainMenuPage());
                        } else {
                            history.goBack(this);
                        }
                        return false;
                    }));

        } else {
            menu.addItem(
                    slot,
                    PatchScope.Back.patch(
                            p,
                            ChestMenuUtils.getBackButton(
                                    p,
                                    "",
                                    ChatColor.GRAY + Slimefun.getLocalization().getMessage(p, "guide.back.guide"))));
            menu.addMenuClickHandler(slot, (pl, s, is, action) -> EventUtil.callEvent(
                            new GuideEvents.BackButtonClickEvent(pl, is, s, action, menu, this))
                    .ifSuccess(() -> {
                        openMainMenu(profile, profile.getGuideHistory().getMainMenuPage());
                        return false;
                    }));
        }
    }

    @ParametersAreNonnullByDefault
    @Override
    public void displayRecipes0(Player p, PlayerProfile profile, ChestMenu menu, RecipeDisplayItem sfItem, int page) {
        List<ItemStack> recipes = sfItem.getDisplayRecipes();

        if (!recipes.isEmpty()) {
            menu.addItem(Formats.recipe_display.getSize() - 1, ItemStackUtil.getCleanItem(null));

            if (page == 0) {
                for (int s : Formats.recipe_display.getChars('B')) {
                    menu.replaceExistingItem(
                            s,
                            PatchScope.Background.patch(
                                    p,
                                    Converter.getItem(
                                            ChestMenuUtils.getBackground(), sfItem.getRecipeSectionLabel(p))));
                    menu.addMenuClickHandler(s, ChestMenuUtils.getEmptyClickHandler());
                }
            }

            List<Integer> ds = Formats.recipe_display.getChars('d');
            int length = ds.size();
            int pages = (recipes.size() - 1) / length + 1;

            for (int s : Formats.recipe_display.getChars('P')) {
                menu.replaceExistingItem(
                        s, PatchScope.PreviousPage.patch(p, ChestMenuUtils.getPreviousButton(p, page + 1, pages)));
                menu.addMenuClickHandler(s, (pl, slot, itemstack, action) -> EventUtil.callEvent(
                                new GuideEvents.PreviousButtonClickEvent(pl, itemstack, slot, action, menu, this))
                        .ifSuccess(() -> {
                            if (page > 0) {
                                displayRecipes0(pl, profile, menu, sfItem, page - 1);
                                Sounds.playFor(pl, Sounds.GUIDE_BUTTON_CLICK_SOUND);
                            }

                            return false;
                        }));
            }

            for (int s : Formats.recipe_display.getChars('N')) {
                menu.replaceExistingItem(
                        s, PatchScope.NextPage.patch(p, ChestMenuUtils.getNextButton(p, page + 1, pages)));
                menu.addMenuClickHandler(s, (pl, slot, itemstack, action) -> EventUtil.callEvent(
                                new GuideEvents.NextButtonClickEvent(pl, itemstack, slot, action, menu, this))
                        .ifSuccess(() -> {
                            if (recipes.size() > (length * (page + 1))) {
                                displayRecipes0(pl, profile, menu, sfItem, page + 1);
                                Sounds.playFor(pl, Sounds.GUIDE_BUTTON_CLICK_SOUND);
                            }

                            return false;
                        }));
            }

            List<Integer> fds = RecipeDisplayFormat.fenceShuffle(ds);
            for (int index = 0; index < length; index++) {
                addDisplayRecipe0(menu, profile, recipes, fds.get(index), index, page);
            }
        }
    }

    @Override
    public void addDisplayRecipe0(
            final @NotNull ChestMenu menu,
            final @NotNull PlayerProfile profile,
            final @NotNull List<ItemStack> recipes,
            int slot,
            int index,
            int page) {
        int l = Formats.recipe_display.getChars('d').size();
        if ((index + (page * l)) < recipes.size()) {
            ItemStack displayItem = recipes.get(index + (page * l));

            /*
             * We want to clone this item to avoid corrupting the original
             * but we wanna make sure no stupid addon creator sneaked some nulls in here
             */
            if (displayItem != null) {
                // JEG - Fix clone SlimefunItemStack
                displayItem = Converter.getItem(ItemStackUtil.getCleanItem(displayItem))
                        .clone();
            }

            menu.replaceExistingItem(slot, PatchScope.RecipeDisplay.patch(profile, displayItem));

            if (page == 0) {
                menu.addMenuClickHandler(slot, (pl, s, itemstack, action) -> EventUtil.callEvent(
                                new GuideEvents.ItemButtonClickEvent(pl, itemstack, s, action, menu, this))
                        .ifSuccess(() -> {
                            displayItem(profile, itemstack, 0, true);
                            return false;
                        }));
                BeginnerUtils.applyWith(this, menu, slot);
                GroupLinker.applyWith(this, menu, slot);
                NamePrinter.applyWith(this, menu, slot);
            }
        } else {
            menu.replaceExistingItem(slot, PatchScope.RecipeDisplay.patch(profile, ItemStackUtil.getCleanItem(null)));
            menu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
        }
    }

    @Override
    @NotNull
    public ChestMenu create0(@NotNull Player p) {
        ChestMenu menu = new ChestMenu(JustEnoughGuide.getConfigManager().getCheatGuideTitle());

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(pl -> Sounds.playFor(pl, Sounds.GUIDE_BUTTON_CLICK_SOUND));
        return menu;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void printErrorMessage0(Player p, Throwable x) {
        p.sendMessage(ChatColor.DARK_RED + "服务器发生了一个内部错误. 请联系管理员处理.");
        JustEnoughGuide.getInstance().getLogger().log(Level.SEVERE, "在打开指南书里的 Slimefun 物品时发生了意外!", x);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void printErrorMessage0(Player p, SlimefunItem item, Throwable x) {
        p.sendMessage(ChatColor.DARK_RED
                + "An internal server error has occurred. Please inform an admin, check the console for"
                + " further info.");
        item.error(
                "This item has caused an error message to be thrown while viewing it in the Slimefun" + " guide.", x);
    }
}
