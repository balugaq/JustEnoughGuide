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

package com.balugaq.jeg.implementation.guide;

import com.balugaq.jeg.api.editor.GroupResorter;
import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.interfaces.DisplayInSurvivalMode;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.interfaces.VanillaItemShade;
import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.core.listeners.GuideListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.SpecialMenuProvider;
import com.balugaq.jeg.utils.clickhandler.OnDisplay;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.compatibility.Sounds;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import com.balugaq.jeg.utils.formatter.RecipeDisplayFormat;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
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
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlock;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.CheatSheetSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AsyncRecipeChoiceTask;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.recipes.MinecraftRecipe;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.SlimefunGuideItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;

/**
 * This is JEG's implementation of the Survival Guide. It extends {@link SurvivalSlimefunGuide} to compatibly with the
 * Slimefun API and other plugins. It also implements the {@link JEGSlimefunGuideImplementation} to provide a common
 * interface for both {@link SurvivalGuideImplementation} and {@link CheatGuideImplementation}.
 *
 * @author TheBusyBiscuit
 * @author balugaq
 * @see SlimefunGuide
 * @see SlimefunGuideImplementation
 * @see SurvivalSlimefunGuide
 * @see CheatSheetSlimefunGuide
 * @see JEGSlimefunGuideImplementation
 * @see CheatGuideImplementation
 * @since 1.0
 */
@SuppressWarnings({"deprecation", "unused", "UnnecessaryUnicodeEscape", "DataFlowIssue", "ConstantValue"})
@NullMarked
public class SurvivalGuideImplementation extends SurvivalSlimefunGuide implements JEGSlimefunGuideImplementation {
    @Deprecated
    public static final int RTS_SLOT = 6;

    @Deprecated
    public static final ItemStack RTS_ITEM = Models.RTS_ITEM;

    @Deprecated
    public static final int SPECIAL_MENU_SLOT = 26;

    @Deprecated
    public static final ItemStack SPECIAL_MENU_ITEM = Models.SPECIAL_MENU_ITEM;

    @Deprecated
    public static final int[] recipeSlots = Formats.recipe.getChars('r').stream()
            .sorted()
            .limit(9)
            .mapToInt(i -> i)
            .toArray();

    public static final int MAX_ITEM_GROUPS = Formats.main.getChars('G').size();
    public static final int MAX_ITEMS = Formats.sub.getChars('i').size();

    public final ItemStack item;

    public SurvivalGuideImplementation() {
        ItemMeta meta = SlimefunGuide.getItem(getMode()).getItemMeta();
        String name = "";
        if (meta != null) {
            name = meta.getDisplayName();
        }
        item = new SlimefunGuideItem(this, name);
    }

    @Override
    public SlimefunGuideMode getMode() {
        return SlimefunGuideMode.SURVIVAL_MODE;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    /**
     * Returns a {@link List} of visible {@link ItemGroup} instances that the {@link SlimefunGuide} would display.
     *
     * @param p
     *         The {@link Player} who opened his {@link SlimefunGuide}
     * @param profile
     *         The {@link PlayerProfile} of the {@link Player}
     *
     * @return a {@link List} of visible {@link ItemGroup} instances
     */
    @Override
    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    public List<ItemGroup> getVisibleItemGroups(Player p, PlayerProfile profile) {
        List<ItemGroup> groups = new LinkedList<>();

        for (ItemGroup group : Slimefun.getRegistry().getAllItemGroups()) {
            try {
                if (group.getClass().isAnnotationPresent(NotDisplayInSurvivalMode.class)) {
                    continue;
                }
                if (group.getClass().isAnnotationPresent(DisplayInSurvivalMode.class)) {
                    groups.add(group);
                    continue;
                }
                if (GuideUtil.isForceHidden(group)) {
                    continue;
                }
                if (group instanceof FlexItemGroup flexItemGroup) {
                    if (flexItemGroup.isVisible(p, profile, getMode())) {
                        groups.add(group);
                    }
                } else if (!group.isHidden(p)) {
                    groups.add(group);
                }
            } catch (Exception | LinkageError x) {
                SlimefunAddon addon = group.getAddon();

                if (addon != null) {
                    addon.getLogger().log(Level.SEVERE, x, () -> "Could not display item group: " + group);
                } else {
                    JustEnoughGuide.getInstance()
                            .getLogger()
                            .log(Level.SEVERE, x, () -> "Could not display item group: " + group);
                }
            }
        }
        GroupResorter.sort(groups);

        return groups;
    }

    @Override
    public void openMainMenu(PlayerProfile profile, int page) {
        Player p = profile.getPlayer();

        if (p == null) {
            return;
        }

        GuideHistory history = profile.getGuideHistory();
        history.clear();
        history.setMainMenuPage(page);

        ChestMenu menu = create0(p);
        List<ItemGroup> itemGroups = getVisibleItemGroups(p, profile);

        createHeader(p, profile, menu, Formats.main);

        int target = (MAX_ITEM_GROUPS * (page - 1)) - 1;
        int pages = target == itemGroups.size() - 1 ? page : (itemGroups.size() - 1) / MAX_ITEM_GROUPS + 1;
        if (page > pages) {
            page = pages;
        }

        List<Integer> indexes = Formats.main.getChars('G');
        int index = 0;

        while (target < (itemGroups.size() - 1) && index < MAX_ITEM_GROUPS) {
            target++;

            ItemGroup group = itemGroups.get(target);
            showItemGroup0(menu, p, profile, group, indexes.get(index));

            index++;
        }

        for (int s : Formats.main.getChars('P')) {
            menu.addItem(s, PatchScope.PreviousPage.patch(profile, ChestMenuUtils.getPreviousButton(p, page, pages)));
            int finalPage1 = page;
            menu.addMenuClickHandler(
                    s, (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.PreviousButtonClickEvent(
                            pl,
                            item,
                            slot,
                            action, menu, this
                    )).ifSuccess(() -> {
                        int previous = finalPage1 - 1;

                        if (previous > 0) {
                            openMainMenu(profile, previous);
                        }

                        return false;
                    })
            );
        }

        for (int s : Formats.main.getChars('N')) {
            menu.addItem(s, PatchScope.NextPage.patch(profile, ChestMenuUtils.getNextButton(p, page, pages)));
            int finalPage = page;
            menu.addMenuClickHandler(
                    s, (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.NextButtonClickEvent(
                            pl, item,
                            slot,
                            action,
                            menu,
                            this
                    )).ifSuccess(() -> {
                        int next = finalPage + 1;

                        if (next <= pages) {
                            openMainMenu(profile, next);
                        }

                        return false;
                    })
            );
        }

        GuideListener.guideModeMap.put(p, getMode());

        menu.open(p);
    }

    @Override
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

        int pages = (itemGroup.getItems().size() - 1) / MAX_ITEMS + 1;

        for (int s : Formats.sub.getChars('P')) {
            menu.addItem(s, PatchScope.PreviousPage.patch(profile, ChestMenuUtils.getPreviousButton(p, page, pages)));
            menu.addMenuClickHandler(
                    s, (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.PreviousButtonClickEvent(
                            pl,
                            item,
                            slot,
                            action, menu, this
                    )).ifSuccess(() -> {
                        int previous = page - 1;

                        if (previous > 0) {
                            openItemGroup(profile, itemGroup, previous);
                        }

                        return false;
                    })
            );
        }

        for (int s : Formats.sub.getChars('N')) {
            menu.addItem(s, PatchScope.NextPage.patch(profile, ChestMenuUtils.getNextButton(p, page, pages)));
            menu.addMenuClickHandler(
                    s, (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.NextButtonClickEvent(
                            pl, item,
                            slot,
                            action,
                            menu,
                            this
                    )).ifSuccess(() -> {
                        int next = page + 1;

                        if (next <= pages) {
                            openItemGroup(profile, itemGroup, next);
                        }

                        return false;
                    })
            );
        }

        List<Integer> indexes = Formats.sub.getChars('i');
        int itemGroupIndex = MAX_ITEMS * (Math.max(page, 1) - 1);

        for (int i = 0; i < MAX_ITEMS; i++) {
            int target = itemGroupIndex + i;

            if (target < 0 || target >= itemGroup.getItems().size()) {
                break;
            }

            SlimefunItem sfitem = itemGroup.getItems().get(target);

            if (!sfitem.isDisabledIn(p.getWorld())) {
                displaySlimefunItem0(menu, itemGroup, p, profile, sfitem, Math.max(page, 1), indexes.get(i));
            }
        }

        GuideUtil.addRTSButton(menu, p, profile, Formats.sub, getMode(), this);
        GuideUtil.addBookMarkButton(menu, p, profile, Formats.sub, this, itemGroup);
        GuideUtil.addItemMarkButton(menu, p, profile, Formats.sub, this, itemGroup);

        menu.open(p);
    }

    @Override
    public void openSearch(PlayerProfile profile, String input, boolean addToHistory) {
        openSearch(profile, input, 0, addToHistory);
    }

    @Override
    public void displayItem(PlayerProfile profile, @Nullable ItemStack item, int index, boolean addToHistory) {
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
    public void displayItem(PlayerProfile profile, SlimefunItem item, boolean addToHistory) {
        displayItem(profile, item, addToHistory, true);
    }

    @Override
    public void createHeader(Player p, PlayerProfile profile, ChestMenu menu) {
        createHeader(p, profile, menu, Formats.main);
    }

    // fallback
    @Deprecated
    public SurvivalGuideImplementation(boolean v1, boolean v2) {
        ItemMeta meta = SlimefunGuide.getItem(getMode()).getItemMeta();
        String name = "";
        if (meta != null) {
            name = meta.getDisplayName();
        }
        item = new SlimefunGuideItem(this, name);
    }

    @Override
    public void showItemGroup0(
            final ChestMenu menu,
            Player p,
            PlayerProfile profile,
            ItemGroup group,
            int index) {
        OnDisplay.ItemGroup.display(p, group, OnDisplay.ItemGroup.Normal, this)
                .at(menu, index, 1);
    }

    @Override
    public void openNestedItemGroup(
            Player p, PlayerProfile profile, NestedItemGroup nested, int page) {
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
                    menu.addMenuClickHandler(
                            ss.get(t),
                            (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.ItemGroupButtonClickEvent(pl, item, slot, action, menu, this)).ifSuccess(() -> {
                                SlimefunGuide.openItemGroup(profile, subGroup, getMode(), 1);
                                return false;
                            })
                    );
                    t += 1;
                }
            }

            int pages = target == subGroups.size() - 1 ? page : (subGroups.size() - 1) / groupsPerPage + 1;
            for (int s : Formats.nested.getChars('P')) {
                menu.addItem(s, PatchScope.PreviousPage.patch(p, ChestMenuUtils.getPreviousButton(p, page, pages)));
                menu.addMenuClickHandler(
                        s,
                        (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.PreviousButtonClickEvent(
                                pl,
                                item
                                , slot, action, menu, this
                        )).ifSuccess(() -> {
                            int next = page - 1;
                            if (next > 0) {
                                this.openNestedItemGroup(p, profile, nested, next);
                            }

                            return false;
                        })
                );
            }

            for (int s : Formats.nested.getChars('N')) {
                menu.addItem(s, PatchScope.NextPage.patch(p, ChestMenuUtils.getNextButton(p, page, pages)));
                menu.addMenuClickHandler(
                        s, (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.NextButtonClickEvent(
                                pl,
                                item,
                                slot,
                                action, menu, this
                        )).ifSuccess(() -> {
                            int next = page + 1;
                            if (next <= pages) {
                                this.openNestedItemGroup(p, profile, nested, next);
                            }

                            return false;
                        })
                );
            }

            menu.open(p);
        } catch (Exception e) {
            Debug.trace(e);
        }
    }

    @Override
    public void displaySlimefunItem0(
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
    public void showMinecraftRecipe0(
            Recipe[] recipes,
            int index,
            final ItemStack item,
            final PlayerProfile profile,
            final Player p,
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
            recipeItems = new ItemStack[] {
                    null,
                    null,
                    null,
                    null,
                    ItemStackUtil.getCleanItem(Converter.getItem(Material.BARRIER, "&4我们不知道如何展示该配方 :/")),
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
                        ChestMenuUtils.getEmptyClickHandler()
                );
            }

            for (int s : Formats.recipe_vanilla.getChars('P')) {
                menu.addItem(
                        s,
                        PatchScope.PreviousPage.patch(
                                p, ChestMenuUtils.getPreviousButton(p, index + 1, recipes.length)),
                        (pl, slot, stack, action) -> EventUtil.callEvent(new GuideEvents.PreviousButtonClickEvent(
                                pl,
                                stack, slot, action, menu, this
                        )).ifSuccess(() -> {
                            if (index > 0) {
                                showMinecraftRecipe0(recipes, index - 1, item, profile, p, true);
                            }
                            return false;
                        })
                );
            }

            for (int s : Formats.recipe_vanilla.getChars('N')) {
                menu.addItem(
                        s,
                        PatchScope.NextPage.patch(p, ChestMenuUtils.getNextButton(p, index + 1, recipes.length)),
                        (pl, slot, stack, action) -> EventUtil.callEvent(new GuideEvents.NextButtonClickEvent(
                                pl,
                                stack,
                                slot,
                                action,
                                menu,
                                this
                        )).ifSuccess(() -> {
                            if (index < recipes.length - 1) {
                                showMinecraftRecipe0(recipes, index + 1, item, profile, p, true);
                            }
                            return false;
                        })
                );
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
            final T recipe, ItemStack[] recipeItems, AsyncRecipeChoiceTask task) {
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
    public void displayItem(PlayerProfile profile, SlimefunItem item, boolean addToHistory, boolean maybeSpecial) {
        displayItem(
                profile,
                item,
                addToHistory,
                true,
                item instanceof RecipeDisplayItem ? Formats.recipe_display : Formats.recipe
        );
    }

    @Override
    public void displayItem(
            PlayerProfile profile, SlimefunItem item, boolean addToHistory, boolean maybeSpecial, Format format) {
        Player p = profile.getPlayer();

        if (p == null) {
            return;
        }

        if (item instanceof VanillaItemShade vis) {
            displayItem(profile, vis.getCustomIcon(), 0, true);
            return;
        }

        ChestMenu menu = create0(p);
        Optional<String> wiki = item.getWikipage();

        if (wiki.isPresent()) {
            for (int s : format.getChars('w')) {
                menu.addItem(
                        s, PatchScope.ItemWiki.patch(
                                p, Converter.getItem(
                                        Material.KNOWLEDGE_BOOK,
                                        ChatColors.color("&f" + Slimefun.getLocalization().getMessage(
                                                p, "guide" +
                                                        ".tooltips.wiki"
                                        )),
                                        "",
                                        ChatColors.color("&7\u21E8 &a" + Slimefun.getLocalization().getMessage(
                                                p,
                                                "guide.tooltips.open-itemgroup"
                                        ))
                                )
                        )
                );
                menu.addMenuClickHandler(
                        s,
                        (pl, slot, itemstack, action) -> EventUtil.callEvent(new GuideEvents.WikiButtonClickEvent(
                                pl,
                                itemstack, slot, action, menu, this
                        )).ifSuccess(() -> {
                            pl.closeInventory();
                            ChatUtils.sendURL(pl, wiki.get());
                            return false;
                        })
                );
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
                        (pl, slot, itemstack, action) -> EventUtil.callEvent(new GuideEvents.BigRecipeButtonClickEvent(pl, itemstack, slot, action, menu, this)).ifSuccess(() -> {
                            try {
                                SpecialMenuProvider.open(profile.getPlayer(), profile, getMode(), item);
                            } catch (InstantiationException
                                     | IllegalAccessException
                                     | InvocationTargetException e) {
                                Debug.trace(e);
                            }
                            return false;
                        })
                );
            }
        }

        GuideUtil.addCerButton(menu, p, profile, item, this, format);

        format.renderCustom(menu);

        menu.open(p);

        if (!task.isEmpty()) {
            task.start(menu.toInventory());
        }
    }

    @Override
    public void displayItem0(
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

    @SuppressWarnings("ConstantValue")
    @Override
    public void displayItem(
            final ChestMenu menu,
            final PlayerProfile profile,
            final Player p,
            Object item,
            ItemStack output,
            final RecipeType recipeType,
            ItemStack[] recipe,
            final AsyncRecipeChoiceTask task,
            final Format format) {
        for (int s : format.getChars('b')) {
            addBackButton0(menu, s, p, profile);
        }

        boolean isSlimefunRecipe = item instanceof SlimefunItem && !(item instanceof VanillaItemShade);

        List<Integer> recipeSlots = format.getChars('r');
        for (int i = 0; i < 9; i++) {
            ItemStack recipeItem = recipe[i];
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
            menu.addItem(
                    s,
                    PatchScope.ItemRecipeType.patch(profile, recipeType.getItem(p)),
                    (pl, slot, itemStack, action) -> EventUtil.callEvent(new GuideEvents.RecipeTypeButtonClickEvent(pl, itemStack, slot, action, menu, this)).ifSuccess(false)
            );
            OnDisplay.RecipeType.display(
                            p, recipeType, PatchScope.ItemRecipeType.patch(
                                    profile,
                                    recipeType.getItem(p)
                            ), this
                    )
                    .at(menu, s, 1);
        }
        for (int s : format.getChars('i')) {
            OnDisplay.Item.display(p, PatchScope.ItemRecipeOut.patch(profile, output), OnDisplay.Item.Normal, this)
                    .at(menu, s, 1);
        }
    }

    @Override
    @ApiStatus.Experimental
    public void createHeader(Player p, PlayerProfile profile, ChestMenu menu, Format format) {
        for (int s : format.getChars('B')) {
            menu.addItem(
                    s,
                    PatchScope.Background.patch(p, ChestMenuUtils.getBackground()),
                    ChestMenuUtils.getEmptyClickHandler()
            );
        }

        for (int s : format.getChars('b')) {
            addBackButton0(menu, s, p, profile);
        }

        // Settings Panel
        for (int s : format.getChars('T')) {
            menu.addItem(s, PatchScope.Settings.patch(profile, ChestMenuUtils.getMenuButton(p)));
            menu.addMenuClickHandler(
                    s, (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.SettingsButtonClickEvent(
                            pl,
                            item,
                            slot,
                            action, menu, this
                    )).ifSuccess(() -> {
                        JEGGuideSettings.openSettings(pl, pl.getInventory().getItemInMainHand());
                        return false;
                    })
            );
        }

        // Search feature!
        for (int s : format.getChars('S')) {
            menu.addItem(s, PatchScope.Search.patch(profile, ChestMenuUtils.getSearchButton(p)));
            menu.addMenuClickHandler(
                    s, (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.SearchButtonClickEvent(
                            pl,
                            item,
                            slot,
                            action,
                            menu,
                            this
                    )).ifSuccess(() -> {
                        pl.closeInventory();

                        Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                        ChatInput.waitForPlayer(
                                JustEnoughGuide.getInstance(), pl, msg -> openSearch(profile, msg, isSurvivalMode()));

                        return false;
                    })
            );
        }

        GuideUtil.addRTSButton(menu, p, profile, format, getMode(), this);
        GuideUtil.addBookMarkButton(menu, p, profile, format, this, null);
        GuideUtil.addItemMarkButton(menu, p, profile, format, this, null);

        format.renderCustom(menu);
    }

    @Override
    @ApiStatus.Experimental
    public void createHeader(Player p, PlayerProfile profile, ChestMenu menu, ItemGroup itemGroup) {
        for (int s : Formats.main.getChars('B')) {
            menu.addItem(
                    s,
                    PatchScope.Background.patch(p, ChestMenuUtils.getBackground()),
                    ChestMenuUtils.getEmptyClickHandler()
            );
        }

        // Settings Panel
        for (int s : Formats.main.getChars('T')) {
            menu.addItem(s, PatchScope.Settings.patch(profile, ChestMenuUtils.getMenuButton(p)));
            menu.addMenuClickHandler(
                    s, (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.SettingsButtonClickEvent(
                            pl,
                            item,
                            slot,
                            action, menu, this
                    )).ifSuccess(() -> {
                        JEGGuideSettings.openSettings(pl, pl.getInventory().getItemInMainHand());
                        return false;
                    })
            );
        }

        // Search feature!
        for (int s : Formats.main.getChars('S')) {
            menu.addItem(s, PatchScope.Search.patch(profile, ChestMenuUtils.getSearchButton(p)));
            menu.addMenuClickHandler(
                    s, (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.SearchButtonClickEvent(
                            pl,
                            item,
                            slot,
                            action,
                            menu,
                            this
                    )).ifSuccess(() -> {
                        pl.closeInventory();

                        Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                        ChatInput.waitForPlayer(
                                JustEnoughGuide.getInstance(), pl, msg -> openSearch(profile, msg, isSurvivalMode()));

                        return false;
                    })
            );
        }

        GuideUtil.addRTSButton(menu, p, profile, Formats.main, getMode(), this);
        GuideUtil.addBookMarkButton(menu, p, profile, Formats.main, this, itemGroup);
        GuideUtil.addItemMarkButton(menu, p, profile, Formats.main, this, itemGroup);
    }

    @Override
    public void addBackButton0(ChestMenu menu, int slot, Player p, PlayerProfile profile) {
        GuideHistory history = profile.getGuideHistory();

        if (isSurvivalMode() && history.size() > 1) {
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
                            history.goBack(this);
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

    @Override
    public void displayRecipes0(Player p, PlayerProfile profile, ChestMenu menu, RecipeDisplayItem sfItem, int page) {
        List<ItemStack> recipes = sfItem.getDisplayRecipes();

        if (!recipes.isEmpty()) {
            // setSize
            menu.addItem(Formats.recipe_display.getSize() - 1, ItemStackUtil.getCleanItem(null));

            if (page == 0) {
                for (int s : Formats.recipe_display.getChars('B')) {
                    menu.replaceExistingItem(
                            s,
                            PatchScope.RecipeDisplay.patch(
                                    p,
                                    Converter.getItem(
                                            ChestMenuUtils.getBackground(), sfItem.getRecipeSectionLabel(p))
                            )
                    );
                    menu.addMenuClickHandler(s, ChestMenuUtils.getEmptyClickHandler());
                }
            }

            List<Integer> ds = Formats.recipe_display.getChars('d');
            int l = ds.size();
            int pages = (recipes.size() - 1) / l + 1;

            for (int s : Formats.recipe_display.getChars('P')) {
                menu.replaceExistingItem(
                        s, PatchScope.PreviousPage.patch(p, ChestMenuUtils.getPreviousButton(p, page + 1, pages)));
                menu.addMenuClickHandler(
                        s,
                        (pl, slot, itemstack, action) -> EventUtil.callEvent(new GuideEvents.PreviousButtonClickEvent(pl, itemstack, slot, action, menu, this)).ifSuccess(() -> {
                            if (page > 0) {
                                displayRecipes0(pl, profile, menu, sfItem, page - 1);
                                Sounds.playFor(pl, Sounds.GUIDE_BUTTON_CLICK_SOUND);
                            }

                            return false;
                        })
                );
            }

            for (int s : Formats.recipe_display.getChars('N')) {
                menu.replaceExistingItem(
                        s, PatchScope.NextPage.patch(p, ChestMenuUtils.getNextButton(p, page + 1, pages)));
                menu.addMenuClickHandler(
                        s,
                        (pl, slot, itemstack, action) -> EventUtil.callEvent(new GuideEvents.NextButtonClickEvent(
                                pl,
                                itemstack, slot, action, menu, this
                        )).ifSuccess(() -> {
                            if (recipes.size() > (l * (page + 1))) {
                                displayRecipes0(pl, profile, menu, sfItem, page + 1);
                                Sounds.playFor(pl, Sounds.GUIDE_BUTTON_CLICK_SOUND);
                            }

                            return false;
                        })
                );
            }

            List<Integer> fds = RecipeDisplayFormat.fenceShuffle(ds);
            for (int i = 0; i < l; i++) {
                addDisplayRecipe0(menu, profile, recipes, fds.get(i), i, page);
            }
        }
    }

    @Override
    public void addDisplayRecipe0(
            final ChestMenu menu,
            final PlayerProfile profile,
            final List<ItemStack> recipes,
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
                displayItem = PatchScope.RecipeDisplay.patch(profile, Converter.getItem(displayItem).clone());
            }

            OnDisplay.Item.display(
                            profile.getPlayer(), ItemStackUtil.getCleanItem(displayItem),
                            OnDisplay.Item.Normal, this
                    )
                    .at(menu, slot, page);
        } else {
            menu.replaceExistingItem(slot, PatchScope.RecipeDisplay.patch(profile, ItemStackUtil.getCleanItem(null)));
            menu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
        }
    }

    @Override
    public void printErrorMessage0(Player p, Throwable x) {
        p.sendMessage(ChatColor.DARK_RED + "服务器发生了一个内部错误. 请联系管理员处理.");
        JustEnoughGuide.getInstance().getLogger().log(Level.SEVERE, "在打开指南书里的 Slimefun 物品时发生了意外!", x);
        JustEnoughGuide.getInstance().getLogger().warning("我们正在尝试恢复玩家 \"" + p.getName() + "\" 的指南...");
        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.removeLastEntry(profile.getGuideHistory());
    }

    @Override
    public void printErrorMessage0(Player p, SlimefunItem item, Throwable x) {
        p.sendMessage(ChatColor.DARK_RED
                              + "An internal server error has occurred. Please inform an admin, check the console for"
                              + " further info.");
        item.error(
                "This item has caused an error message to be thrown while viewing it in the Slimefun" + " guide.", x);
        JustEnoughGuide.getInstance()
                .getLogger()
                .warning("We are trying to recover the player \"" + p.getName() + "\"'s guide...");
        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.removeLastEntry(profile.getGuideHistory());
    }
}
