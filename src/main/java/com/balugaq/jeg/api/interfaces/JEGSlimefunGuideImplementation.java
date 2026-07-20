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

package com.balugaq.jeg.api.interfaces;

import com.balugaq.jeg.api.editor.GroupResorter;
import com.balugaq.jeg.api.groups.BookmarkGroup;
import com.balugaq.jeg.api.groups.ItemMarkGroup;
import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.objects.collection.data.Bookmark;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.core.integrations.slimefuntranslation.SlimefunTranslationIntegrationMain;
import com.balugaq.jeg.core.listeners.GroupTierEditorListener;
import com.balugaq.jeg.core.listeners.GuideListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.guide.CheatGuideImplementation;
import com.balugaq.jeg.implementation.guide.SurvivalGuideImplementation;
import com.balugaq.jeg.implementation.items.GroupTierEditorGuide;
import com.balugaq.jeg.implementation.option.delegate.LearningAnimationOption;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.LocalHelper;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.clickhandler.OnDisplay;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.compatibility.Sounds;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import com.balugaq.jeg.utils.formatter.RecipeDisplayFormat;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlock;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AsyncRecipeChoiceTask;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.recipes.MinecraftRecipe;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * This is JEG's guide implementation, providing a common
 * interface for both {@link SurvivalGuideImplementation} and {@link CheatGuideImplementation},
 * also used to editor group tiers in {@link GroupTierEditorGuide}
 *
 * @author TheBusyBiscuit
 * @author balugaq
 * @see SlimefunGuideImplementation
 * @see CheatGuideImplementation
 * @see GroupTierEditorGuide
 * @see GroupTierEditorListener
 * @since 1.0
 */
@SuppressWarnings({"deprecation", "unused", "UnnecessaryUnicodeEscape"})
@NullMarked
public interface JEGSlimefunGuideImplementation extends SlimefunGuideImplementation {
    NamespacedKey UNLOCK_ITEM_KEY = new NamespacedKey(JustEnoughGuide.getInstance(), "unlock_item");
    int MAX_ITEMS_PER_PAGE = Formats.sub.getChars('i').size();
    int MAX_ITEM_GROUPS_PER_PAGE = Formats.main.getChars('G').size();

    @Nullable
    static ItemStack getDisplayItem(Player p, boolean isSlimefunRecipe, @Nullable ItemStack item) {
        if (isSlimefunRecipe) {
            SlimefunItem slimefunItem = SlimefunItem.getByItem(item);

            if (slimefunItem == null) {
                return item;
            }

            ItemGroup itemGroup = slimefunItem.getItemGroup();
            String lore = hasPermission0(p, slimefunItem)
                ? String.format(
                "&f需要在 %s 中解锁",
                (LocalHelper.getAddonName(itemGroup, slimefunItem.getId())) + ChatColor.WHITE + " - "
                + LocalHelper.getDisplayName(itemGroup, p)
            )
                : "&f无权限";
            return slimefunItem.canUse(p, false)
                ? Converter.getItem(item)
                : Converter.getItem(
                Material.BARRIER,
                SlimefunTranslationIntegrationMain.getTranslatedItemName(p, slimefunItem),
                "&4&l" + Slimefun.getLocalization().getMessage(p, "guide.locked"),
                "",
                lore);
        } else {
            return item;
        }
    }

    static boolean hasPermission0(Player p, SlimefunItem item) {
        return Slimefun.getPermissionsService().hasPermission(p, item);
    }

    default void openItemGroup(PlayerProfile profile, ItemGroup itemGroup, int page) {
        Player p = profile.getPlayer();

        if (p == null) {
            return;
        }

        if (itemGroup instanceof NestedItemGroup nested && (itemGroup.getClass() == NestedItemGroup.class || (itemGroup.getClass().getSuperclass() == NestedItemGroup.class && itemGroup.getClass().isAnonymousClass()))) {
            openNestedItemGroup(p, profile, nested, page);
            return;
        }

        if (itemGroup instanceof FlexItemGroup flexItemGroup) {
            try {
                flexItemGroup.open(p, profile, getMode());
            } catch (Exception e) {
                printErrorMessage0(p, e);
            }
            return;
        }

        profile.getGuideHistory().add(itemGroup, page);

        ChestMenu menu = create0(p);

        int pages = (itemGroup.getItems().size() - 1) / getMaxItemsPerPage() + 1;
        Format format = Formats.sub;
        GuideUtil.commonRender(menu, format, profile, p, itemGroup, page, pages, np -> {
            openItemGroup(profile, itemGroup, np);
        });

        List<Integer> indexes = Formats.sub.getChars('i');
        int itemGroupIndex = getMaxItemsPerPage() * (Math.max(page, 1) - 1);

        for (int i = 0; i < getMaxItemsPerPage(); i++) {
            int target = itemGroupIndex + i;

            if (target < 0 || target >= itemGroup.getItems().size()) {
                break;
            }

            SlimefunItem sfitem = itemGroup.getItems().get(target);

            if (!sfitem.isDisabledIn(p.getWorld())) {
                displaySlimefunItem0(menu, itemGroup, p, profile, sfitem, Math.max(page, 1), indexes.get(i));
            }
        }

        menu.open(p);
    }

    default void displayItem(
        final ChestMenu menu,
        final PlayerProfile profile,
        final Player p,
        Object item,
        @Nullable ItemStack output,
        final RecipeType recipeType,
        @Nullable ItemStack[] recipe,
        final AsyncRecipeChoiceTask task,
        final Format format) {
        boolean isSlimefunRecipe = item instanceof SlimefunItem && !(item instanceof VanillaItemShade);

        List<Integer> recipeSlots = format.getChars('r');
        for (int i = 0; i < recipe.length; i++) {
            ItemStack recipeItem = getDisplayItem(p, isSlimefunRecipe, recipe[i]);
            OnDisplay.Item.display(p, PatchScope.ItemRecipeIngredient.patch(p, recipeItem), OnDisplay.Item.Normal, this)
                .at(menu, recipeSlots.get(i), 1);

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
            OnDisplay.RecipeType.display(p, recipeType, PatchScope.ItemRecipeType.patch(p, recipeType.getItem(p)), this)
                .at(menu, s, 1);
        }
        for (int s : format.getChars('i')) {
            OnDisplay.Item.display(p, PatchScope.ItemRecipeOut.patch(p, output), OnDisplay.Item.Normal, this)
                .at(menu, s, 1);
        }
    }

    default ChestMenu create0(Player p) {
        return new ChestMenu(GuideUtil.getGuideTitle(getMode()));
    }

    /**
     * Opens the bookmark group for the player.
     *
     * @param player  The player.
     * @param profile The player profile.
     */
    default void openBookMarkGroup(Player player, PlayerProfile profile) {
        List<Bookmark> items = JustEnoughGuide.getBookmarkManager().getBookmarkedItems(player);
        if (items == null || items.isEmpty()) {
            player.sendMessage(ChatColor.RED + "你还没有收藏任何物品!");
            return;
        }
        new BookmarkGroup(this, items).open(player, profile, getMode());
    }

    /**
     * Opens the item mark group for the player.
     *
     * @param itemGroup The item group.
     * @param player    The player.
     * @param profile   The player profile.
     */
    default void openItemMarkGroup(ItemGroup itemGroup, Player player, PlayerProfile profile) {
        new ItemMarkGroup(this, itemGroup, player).open(player, profile, getMode());
    }

    default void openNestedItemGroup(
        Player p, PlayerProfile profile, NestedItemGroup nested, int page) {
        GuideHistory history = profile.getGuideHistory();

        history.add(nested, page);

        ChestMenu menu = create0(p);

        menu.setEmptySlotsClickable(false);

        menu.addMenuOpeningHandler(p2 -> {
            try {
                Sounds.playFor(p2, Sounds.GUIDE_BUTTON_CLICK_SOUND);
            } catch (Exception ignored) {
            }
        });

        Format format = Formats.nested;
        List<Integer> ss = format.getChars('G');
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
                    menu.addMenuClickHandler(
                        ss.get(t),
                        (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.ItemGroupButtonClickEvent(pl, item, slot, action, menu, this)).ifSuccess(() -> {
                            if (pl.isOp() && GroupResorter.isSelecting(pl)) {
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
                            this.openItemGroup(profile, subGroup, 1);
                            return false;
                        })
                    );
                    t += 1;
                }
            }

            int pages = target == subGroups.size() - 1 ? page : (subGroups.size() - 1) / groupsPerPage + 1;
            GuideUtil.commonRender(menu, format, profile, p, nested, page, pages, np -> {
                openNestedItemGroup(p, profile, nested, np);
            });

            menu.open(p);
        } catch (Exception e) {
            Debug.trace(e);
        }
    }

    default void displaySlimefunItem0(
        final ChestMenu menu,
        final ItemGroup itemGroup,
        final Player p,
        final PlayerProfile profile,
        final SlimefunItem sfitem,
        int page,
        int index) {
        OnDisplay.Item.display(p, sfitem, OnDisplay.Item.Normal, this)
            .at(menu, index, page);
    }

    @Override
    default void openSearch(PlayerProfile profile, String input, boolean addToHistory) {
        openSearch(profile, input, 0, addToHistory);
    }

    default void openSearch(PlayerProfile profile, String input, int page, boolean addToHistory) {
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

    default void showMinecraftRecipe0(
        Recipe[] recipes,
        int index,
        final ItemStack item,
        final PlayerProfile profile,
        final Player p,
        boolean addToHistory) {
        Recipe recipe = recipes[index];

        @Nullable ItemStack[] recipeItems = new ItemStack[9];
        RecipeType recipeType = RecipeType.NULL;
        @Nullable ItemStack result = null;

        Optional<MinecraftRecipe<? super Recipe>> optional = MinecraftRecipe.of(recipe);
        AsyncRecipeChoiceTask task = new AsyncRecipeChoiceTask();

        if (optional.isPresent()) {
            showRecipeChoices0(recipe, recipeItems, task);

            recipeType = new RecipeType(optional.get());
            result = recipe.getResult();
        } else {
            recipeItems = new @Nullable ItemStack[]{
                null, null, null,
                null, Converter.getItem(Material.BARRIER, "&4我们不知道如何展示该配方 :/"), null,
                null, null, null
            };
        }

        Format format = Formats.recipe_vanilla;
        ChestMenu menu = create0(p);
        GuideUtil.commonRender(
            menu,
            format,
            profile,
            p,
            null,
            recipes.length > 1 ? index : 0,
            recipes.length > 1 ? recipes.length - 1 : 0,
            np -> {
                showMinecraftRecipe0(recipes, np, item, profile, p, true);
            }
        );

        if (addToHistory) {
            profile.getGuideHistory().add(item, index);
        }

        displayItem(menu, profile, p, item, result, recipeType, recipeItems, task, format);

        menu.open(p);

        if (!task.isEmpty()) {
            task.start(menu.toInventory());
        }
    }

    default void displayItem(PlayerProfile profile, SlimefunItem item, boolean addToHistory) {
        displayItem(
            profile,
            item,
            addToHistory,
            item instanceof RecipeDisplayItem ? Formats.recipe_display : Formats.recipe
        );
    }

    default void displayItem(
        PlayerProfile profile, SlimefunItem item, boolean addToHistory, Format format) {
        Player p = profile.getPlayer();

        if (p == null) {
            return;
        }

        if (item instanceof VanillaItemShade vis) {
            displayItem(profile, vis.getCustomIcon(), 0, true);
            return;
        }

        ChestMenu menu = create0(p);
        GuideUtil.commonRender(menu, format, profile, p);
        GuideUtil.addWikiButton(menu, format, p, item);

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

        GuideUtil.addBigRecipeButton(menu, format, profile, p, item);
        GuideUtil.addCerButton(menu, format, profile, p, item);
        GuideUtil.addSlimefunRecipeEditButton(menu, p, profile, item, format);

        menu.open(p);

        if (!task.isEmpty()) {
            task.start(menu.toInventory());
        }
    }

    default void displayItem0(
        final ChestMenu menu,
        final PlayerProfile profile,
        final Player p,
        Object item,
        ItemStack output,
        final RecipeType recipeType,
        ItemStack[] recipe,
        final AsyncRecipeChoiceTask task) {
        displayItem(menu, profile, p, item, output, recipeType, recipe, task, Formats.recipe);
    }

    default void createHeader(Player p, PlayerProfile profile, ChestMenu menu) {
        createHeader(p, profile, menu, Formats.main);
    }

    default void createHeader(Player p, PlayerProfile profile, ChestMenu menu, Format format) {
        GuideUtil.commonRender(menu, format, profile, p);
    }

    default void addBackButton0(ChestMenu menu, @Range(from = 0, to = 53) int slot, Player p, PlayerProfile profile) {
        GuideHistory history = profile.getGuideHistory();

        if (history.size() > 1) {
            menu.addItem(
                slot,
                PatchScope.Back.patch(
                    p, ChestMenuUtils.getBackButton(p, "", "&f左键: &7返回上一页", "&fShift + 左键: &7返回主菜单"))
            );

            menu.addMenuClickHandler(
                slot, (pl, s, is, action) -> EventUtil.callEvent(new GuideEvents.BackButtonClickEvent(
                    pl, is, s,
                    action,
                    menu, this
                )).ifSuccess(() -> {
                    if (action.isShiftClicked()) {
                        openMainMenu(profile, profile.getGuideHistory().getMainMenuPage());
                    } else {
                        GuideUtil.goBack(history);
                    }
                    return false;
                })
            );

        } else {
            menu.addItem(
                slot, PatchScope.Back.patch(
                    p, ChestMenuUtils.getBackButton(
                        p,
                        "",
                        ChatColor.GRAY + Slimefun.getLocalization().getMessage(p, "guide.back.guide")
                    )
                )
            );
            menu.addMenuClickHandler(
                slot, (pl, s, is, action) -> EventUtil.callEvent(new GuideEvents.BackButtonClickEvent(
                    pl, is, s,
                    action,
                    menu, this
                )).ifSuccess(() -> {
                    openMainMenu(profile, profile.getGuideHistory().getMainMenuPage());
                    return false;
                })
            );
        }
    }

    // 0-based page
    default void displayRecipes0(Player p, PlayerProfile profile, ChestMenu menu, RecipeDisplayItem sfItem, int page) {
        List<ItemStack> recipes = sfItem.getDisplayRecipes();

        if (!recipes.isEmpty()) {
            // setSize
            Format format = Formats.recipe_display;
            menu.addItem(format.getSize() - 1, Converter.getItem());

            if (page == 0) {
                GuideUtil.addBackgroundItems(menu, format, profile, PatchScope.RecipeDisplay.patch(p,
                    Converter.getItem(ChestMenuUtils.getBackground(), sfItem.getRecipeSectionLabel(p))));
            }

            List<Integer> ds = format.getChars('d');
            int length = ds.size();
            int pages = (recipes.size() - 1) / length + 1; // 1-based
            GuideUtil.addPageButtons(menu, format, profile, p, null, /*0-based*/ page + 1, pages, np -> {
                displayRecipes0(p, profile, menu, sfItem, np);
                Sounds.playFor(p, Sounds.GUIDE_BUTTON_CLICK_SOUND);
            });

            List<Integer> fds = RecipeDisplayFormat.fenceShuffle(ds);
            for (int index = 0; index < length; index++) {
                addDisplayRecipe0(menu, profile, recipes, fds.get(index), index, page);
            }
        }
    }

    default void addDisplayRecipe0(
        final ChestMenu menu,
        final PlayerProfile profile,
        final List<@Nullable ItemStack> recipes,
        @Range(from = 0, to = 53) int slot,
        int index,
        int page) {
        Player p = profile.getPlayer();
        if (p == null) return;
        int l = Formats.recipe_display.getChars('d').size();
        if ((index + (page * l)) < recipes.size()) {
            ItemStack displayItem = recipes.get(index + (page * l));

            /*
             * We want to clone this item to avoid corrupting the original
             * but we wanna make sure no stupid addon creator sneaked some nulls in here
             */
            if (displayItem != null) {
                // JEG - Fix clone SlimefunItemStack
                displayItem = PatchScope.RecipeDisplay.patch(profile, Converter.getItem(displayItem));
            }

            OnDisplay.Item.display(p, displayItem, OnDisplay.Item.Normal, this)
                .at(menu, slot, page);
        } else {
            menu.replaceExistingItem(slot, PatchScope.RecipeDisplay.patch(profile, Converter.getItem()));
            menu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
        }
    }

    default void printErrorMessage0(Player p, Throwable x) {
        Debug.trace(x);
        p.sendMessage(ChatColor.DARK_RED + "服务器发生了一个内部错误. 请联系管理员处理.");
        Debug.log(Level.SEVERE, "在打开指南书里的 Slimefun 物品时发生了意外!", x);
        Debug.warn("我们正在尝试恢复玩家 \"" + p.getName() + "\" 的指南...");
        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.removeLastEntry(profile.getGuideHistory());
    }

    @Override
    default void unlockItem(Player p, SlimefunItem sfitem, Consumer<Player> callback) {
        if (!Slimefun.getConfigManager().isLearningAnimationDisabled() && !LearningAnimationOption.isEnabled(p)) {
            ReflectionUtil.setValue(Slimefun.getConfigManager(), "disableLearningAnimation", true);
            JustEnoughGuide.runLaterAsync(() -> {
                ReflectionUtil.setValue(Slimefun.getConfigManager(), "disableLearningAnimation", false);
            }, 1L);
        }
        SlimefunGuideImplementation.super.unlockItem(p, sfitem, callback);
    }

    default void showItemGroup0(
        final ChestMenu menu,
        Player p,
        PlayerProfile profile,
        ItemGroup group,
        int index) {
        showItemGroup0(menu, p, profile, group, index, 1);
    }

    default void showItemGroup0(
        final ChestMenu menu,
        Player p,
        PlayerProfile profile,
        ItemGroup group,
        int index,
        int page) {
        OnDisplay.ItemGroup.display(p, group, OnDisplay.ItemGroup.Normal, this)
            .at(menu, index, page);
    }

    default JEGSlimefunGuideImplementation getImplementation() {
        return this;
    }

    default <T extends Recipe> void showRecipeChoices0(
        final T recipe, @Nullable ItemStack[] recipeItems, AsyncRecipeChoiceTask task) {
        RecipeChoice[] choices = Slimefun.getMinecraftRecipeService().getRecipeShape(recipe);

        List<Integer> recipeSlots = Formats.recipe_vanilla.getChars('r');
        if (choices.length == 1 && choices[0] instanceof RecipeChoice.MaterialChoice materialChoice) {
            recipeItems[4] = new ItemStack(materialChoice.getChoices().get(0));

            if (materialChoice.getChoices().size() > 1) {
                task.add(recipeSlots.get(4), materialChoice);
            }
        } else {
            for (int i = 0; i < choices.length; i++) {
                if (choices[i] instanceof RecipeChoice.MaterialChoice materialChoice) {
                    recipeItems[i] = new ItemStack(materialChoice.getChoices().get(0));

                    if (materialChoice.getChoices().size() > 1) {
                        task.add(recipeSlots.get(i), materialChoice);
                    }
                }
            }
        }
    }

    default void displayItem(PlayerProfile profile, @Nullable ItemStack item, int index, boolean addToHistory) {
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

    default int getMaxItemsPerPage() {
        return MAX_ITEMS_PER_PAGE;
    }

    default int getMaxItemGroupsPerPage() {
        return MAX_ITEM_GROUPS_PER_PAGE;
    }

    /**
     * Returns a {@link List} of visible {@link ItemGroup} instances that the {@link SlimefunGuide} would display.
     *
     * @param p       The {@link Player} who opened his {@link SlimefunGuide}
     * @param profile The {@link PlayerProfile} of the {@link Player}
     * @return a {@link List} of visible {@link ItemGroup} instances
     */
    List<ItemGroup> getVisibleItemGroups(Player p, PlayerProfile profile);

    @Override
    default void openMainMenu(PlayerProfile profile, int page) {
        Player p = profile.getPlayer();

        if (p == null) {
            return;
        }

        GuideHistory history = profile.getGuideHistory();
        history.clear();
        history.setMainMenuPage(page);

        ChestMenu menu = create0(p);
        List<ItemGroup> itemGroups = getVisibleItemGroups(p, profile);

        int target = (getMaxItemGroupsPerPage() * (page - 1)) - 1;
        int pages = target == itemGroups.size() - 1 ? page : (itemGroups.size() - 1) / getMaxItemGroupsPerPage() + 1;
        if (page > pages) {
            page = pages;
        }

        Format format = Formats.main;
        List<Integer> indexes = format.getChars('G');
        int index = 0;

        while (target < (itemGroups.size() - 1) && index < getMaxItemGroupsPerPage()) {
            target++;

            ItemGroup group = itemGroups.get(target);
            showItemGroup0(menu, p, profile, group, indexes.get(index));

            index++;
        }

        GuideUtil.commonRender(menu, format, profile, p, null, page, pages, np -> {
            openMainMenu(profile, np);
        });

        GuideListener.guideModeMap.put(p, getMode());

        menu.open(p);
    }
}
