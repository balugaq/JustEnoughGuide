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

import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.interfaces.DisplayInSurvivalMode;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.core.listeners.GuideListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.Lang;
import com.balugaq.jeg.utils.LocalHelper;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.SlimefunOfficialSupporter;
import com.balugaq.jeg.utils.SpecialMenuProvider;
import com.balugaq.jeg.utils.clickhandler.BeginnerUtils;
import com.balugaq.jeg.utils.clickhandler.GroupLinker;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.compatibility.Sounds;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import com.balugaq.jeg.utils.formatter.RecipeDisplayFormat;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerPreResearchEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.LockedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlock;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AsyncRecipeChoiceTask;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.libraries.dough.recipes.MinecraftRecipe;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.SlimefunGuideItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.MenuClickHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;

/**
 * This is JEG's implementation of the Survival Guide.
 * It extends {@link SurvivalSlimefunGuide} to compatibly
 * with the Slimefun API and other plugins.
 * It also implements the {@link JEGSlimefunGuideImplementation}
 * to provide a common interface for both
 * {@link SurvivalGuideImplementation} and {@link CheatGuideImplementation}.
 *
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings({"deprecation", "unused"})
public class SurvivalGuideImplementation extends SurvivalSlimefunGuide implements JEGSlimefunGuideImplementation {
    @Deprecated
    public static final int[] recipeSlots = Formats.recipe.getChars('r').stream().sorted().limit(9).mapToInt(i -> i).toArray();
    public static final int MAX_ITEM_GROUPS = Formats.main.getChars('G').size();
    public static final int MAX_ITEMS = Formats.sub.getChars('i').size();
    @Deprecated
    private static final int RTS_SLOT = 6;
    @Deprecated
    private static final ItemStack RTS_ITEM = Lang.RTS_ITEM;
    @Deprecated
    private static final int SPECIAL_MENU_SLOT = 26;
    @Deprecated
    private static final ItemStack SPECIAL_MENU_ITEM = Lang.SPECIAL_MENU_ITEM;
    private static final NamespacedKey UNLOCK_ITEM_KEY = new NamespacedKey(JustEnoughGuide.getInstance(), "unlock_item");
    public final @NotNull ItemStack item;

    public SurvivalGuideImplementation() {
        super(SlimefunOfficialSupporter.isShowVanillaRecipes(), SlimefunOfficialSupporter.isShowHiddenItemGroups());
        ItemMeta meta = SlimefunGuide.getItem(getMode()).getItemMeta();
        String name = "";
        if (meta != null) {
            name = meta.getDisplayName();
        }
        item = new SlimefunGuideItem(this, name);
    }

    // fallback
    @Deprecated
    public SurvivalGuideImplementation(boolean v1, boolean v2) {
        super(v1, v2);
        ItemMeta meta = SlimefunGuide.getItem(getMode()).getItemMeta();
        String name = "";
        if (meta != null) {
            name = meta.getDisplayName();
        }
        item = new SlimefunGuideItem(this, name);
    }

    @ParametersAreNonnullByDefault
    private static @NotNull ItemStack getDisplayItem(Player p, boolean isSlimefunRecipe, ItemStack item) {
        if (isSlimefunRecipe) {
            SlimefunItem slimefunItem = SlimefunItem.getByItem(item);

            if (slimefunItem == null) {
                return item;
            }

            ItemGroup itemGroup = slimefunItem.getItemGroup();
            if (slimefunItem.isDisabledIn(p.getWorld())) {
                return ItemStackUtil.getCleanItem(Converter.getItem(
                        Material.BARRIER,
                        ItemUtils.getItemName(item),
                        Lang.getGuideMessage("disabled-item")
                ));
            }
            String lore = hasPermission(p, slimefunItem)
                    ? Lang.getGuideMessage("locked-item", "addon_name", LocalHelper.getAddonName(itemGroup, slimefunItem.getId()), "category_name", itemGroup.getDisplayName(p))
                    : Lang.getGuideMessage("no-permission");
            Research research = slimefunItem.getResearch();
            if (research == null) {
                return ItemStackUtil.getCleanItem(
                        slimefunItem.canUse(p, false)
                                ? item
                                : Converter.getItem(Converter.getItem(
                                Material.BARRIER,
                                SlimefunOfficialSupporter.getTranslatedItemName(p, slimefunItem),
                                "&4&l" + Slimefun.getLocalization().getMessage(p, "guide.locked"),
                                "",
                                lore), meta -> {
                            meta.getPersistentDataContainer().set(UNLOCK_ITEM_KEY, PersistentDataType.STRING, slimefunItem.getId());
                        }));
            } else {
                return ItemStackUtil.getCleanItem(
                        slimefunItem.canUse(p, false)
                                ? item
                                : Converter.getItem(Converter.getItem(
                                Material.BARRIER,
                                SlimefunOfficialSupporter.getTranslatedItemName(p, slimefunItem),
                                "&4&l" + Slimefun.getLocalization().getMessage(p, "guide.locked"),
                                "",
                                lore,
                                "",
                                Lang.getGuideMessage("click-to-unlock"),
                                "",
                                Lang.getGuideMessage("cost", "cost", research.getCost())), meta -> {
                            meta.getPersistentDataContainer().set(UNLOCK_ITEM_KEY, PersistentDataType.STRING, slimefunItem.getId());
                        }));
            }
        } else {
            return item;
        }
    }

    @ParametersAreNonnullByDefault
    private static boolean hasPermission(Player p, SlimefunItem item) {
        return Slimefun.getPermissionsService().hasPermission(p, item);
    }

    @Override
    public @NotNull SlimefunGuideMode getMode() {
        return SlimefunGuideMode.SURVIVAL_MODE;
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
        List<ItemGroup> groups = new LinkedList<>();

        for (ItemGroup group : Slimefun.getRegistry().getAllItemGroups()) {
            try {
                if (group.getClass().isAnnotationPresent(NotDisplayInSurvivalMode.class)) {
                    continue;
                }
                if (group.getClass().isAnnotationPresent(DisplayInSurvivalMode.class)) {
                    groups.add(group);
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
                    addon.getLogger().log(Level.SEVERE, x, () -> Lang.getError("could-not-display-item-group", "group", group));
                } else {
                    JustEnoughGuide.getInstance()
                            .getLogger()
                            .log(Level.SEVERE, x, () -> Lang.getError("could-not-display-item-group", "group", group));
                }
            }
        }

        return groups;
    }

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
        List<ItemGroup> itemGroups = getVisibleItemGroups(p, profile);

        createHeader(p, profile, menu, Formats.main);

        int target = (MAX_ITEM_GROUPS * (page - 1)) - 1;
        int pages = target == itemGroups.size() - 1 ? page : (itemGroups.size() - 1) / MAX_ITEM_GROUPS + 1;

        var indexes = Formats.main.getChars('G');
        int index = 0;

        while (target < (itemGroups.size() - 1) && index < MAX_ITEM_GROUPS) {
            target++;

            ItemGroup group = itemGroups.get(target);
            showItemGroup0(menu, p, profile, group, indexes.get(index));

            index++;
        }

        for (var s : Formats.main.getChars('P')) {
            menu.addItem(s, ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(p, page, pages)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> {
                int previous = page - 1;

                if (previous > 0) {
                    openMainMenu(profile, previous);
                }

                return false;
            });
        }

        for (var s : Formats.main.getChars('N')) {
            menu.addItem(s, ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(p, page, pages)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> {
                int next = page + 1;

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
            @NotNull ChestMenu menu, @NotNull Player p, @NotNull PlayerProfile profile, ItemGroup group, int index) {
        if (!(group instanceof LockedItemGroup)
                || !isSurvivalMode()
                || ((LockedItemGroup) group).hasUnlocked(p, profile)) {
            menu.addItem(index, ItemStackUtil.getCleanItem(group.getItem(p)));
            menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
                openItemGroup(profile, group, 1);
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
                    ItemStackUtil.getCleanItem(Converter.getItem(
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

        int pages = (itemGroup.getItems().size() - 1) / MAX_ITEMS + 1;

        for (var s : Formats.sub.getChars('P')) {
            menu.addItem(s, ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(p, page, pages)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> {
                int previous = page - 1;

                if (previous > 0) {
                    openItemGroup(profile, itemGroup, previous);
                }

                return false;
            });
        }

        for (var s : Formats.sub.getChars('N')) {
            menu.addItem(s, ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(p, page, pages)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> {
                int next = page + 1;

                if (next <= pages) {
                    openItemGroup(profile, itemGroup, next);
                }

                return false;
            });
        }

        var indexes = Formats.sub.getChars('i');
        int itemGroupIndex = MAX_ITEMS * (page - 1);

        for (int i = 0; i < MAX_ITEMS; i++) {
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
    public void openNestedItemGroup(@NotNull Player p, @NotNull PlayerProfile profile, @NotNull NestedItemGroup nested, int page) {
        GuideHistory history = profile.getGuideHistory();

        history.add(nested, page);

        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.title.main"));

        menu.setEmptySlotsClickable(false);

        menu.addMenuOpeningHandler(p2 -> {
            try {
                Sounds.playFor(p2, Sounds.GUIDE_BUTTON_CLICK_SOUND);
            } catch (Throwable ignored) {
            }
        });

        this.createHeader(p, profile, menu, Formats.nested);

        var ss = Formats.nested.getChars('G');
        var groupsPerPage = ss.size();

        try {
            @SuppressWarnings("unchecked") List<SubItemGroup> subGroups = (List<SubItemGroup>) ReflectionUtil.getValue(nested, "subGroups");
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
                    menu.addItem(ss.get(t), subGroup.getItem(p));
                    menu.addMenuClickHandler(ss.get(t), (pl, slot, item, action) -> {
                        SlimefunGuide.openItemGroup(profile, subGroup, getMode(), 1);
                        return false;
                    });
                    t += 1;
                }
            }

            int pages = target == subGroups.size() - 1 ? page : (subGroups.size() - 1) / groupsPerPage + 1;
            for (var s : Formats.nested.getChars('P')) {
                menu.addItem(s, ChestMenuUtils.getPreviousButton(p, page, pages));
                menu.addMenuClickHandler(s, (pl, slot, item, action) -> {
                    int next = page - 1;
                    if (next > 0) {
                        this.openNestedItemGroup(p, profile, nested, next);
                    }

                    return false;
                });
            }

            for (var s : Formats.nested.getChars('N')) {
                menu.addItem(s, ChestMenuUtils.getNextButton(p, page, pages));
                menu.addMenuClickHandler(s, (pl, slot, item, action) -> {
                    int next = page + 1;
                    if (next <= pages) {
                        this.openNestedItemGroup(p, profile, nested, next);
                    }

                    return false;
                });
            }

            menu.open(p);
        } catch (Throwable e) {
            Debug.trace(e);
        }
    }

    @Override
    public void displaySlimefunItem0(
            @NotNull ChestMenu menu,
            @NotNull ItemGroup itemGroup,
            @NotNull Player p,
            @NotNull PlayerProfile profile,
            @NotNull SlimefunItem sfitem,
            int page,
            int index) {
        Research research = sfitem.getResearch();

        if (isSurvivalMode() && !JEGSlimefunGuideImplementation.hasPermission0(p, sfitem)) {
            List<String> message = Slimefun.getPermissionsService().getLore(sfitem);
            menu.addItem(
                    index,
                    ItemStackUtil.getCleanItem(Converter.getItem(
                            ChestMenuUtils.getNoPermissionItem(),
                            sfitem.getItemName(),
                            message.toArray(new String[0]))));
            menu.addMenuClickHandler(index, ChestMenuUtils.getEmptyClickHandler());
        } else if (isSurvivalMode() && research != null && !profile.hasUnlocked(research)) {
            menu.addItem(
                    index,
                    ItemStackUtil.getCleanItem(Converter.getItem(
                            ChestMenuUtils.getNoPermissionItem(),
                            "&f" + SlimefunOfficialSupporter.getTranslatedItemName(p, sfitem),
                            "&7" + sfitem.getId(),
                            "&4&l" + Slimefun.getLocalization().getMessage(p, "guide.locked"),
                            "",
                            Lang.getGuideMessage("click-to-unlock"),
                            "",
                            Lang.getGuideMessage("cost", "cost", research.getCost()))));
            menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
                research.unlockFromGuide(this, p, profile, sfitem, itemGroup, page);
                return false;
            });
        } else {
            menu.addItem(index, ItemStackUtil.getCleanItem(sfitem.getItem()));
            menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
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
            });
            BeginnerUtils.applyBeginnersGuide(this, menu, index);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void openSearch(PlayerProfile profile, String input, boolean addToHistory) {
        openSearch(profile, input, 0, addToHistory);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void openSearch(PlayerProfile profile, String input, int page, boolean addToHistory) {
        Player p = profile.getPlayer();

        if (p == null) {
            return;
        }

        String searchTerm = ChatColor.stripColor(input.toLowerCase(Locale.ROOT));
        SearchGroup group = new SearchGroup(
                this, p, searchTerm, false, true);
        group.open(p, profile, getMode());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void displayItem(PlayerProfile profile, @Nullable ItemStack item, int index, boolean addToHistory) {
        Player p = profile.getPlayer();

        if (p == null || item == null || item.getType() == Material.AIR) {
            return;
        }

        SlimefunItem sfItem = SlimefunItem.getByItem(item);

        if (sfItem != null) {
            displayItem(profile, sfItem, addToHistory);
            return;
        }

        // Not SlimefunItem
        if (!SlimefunOfficialSupporter.isShowVanillaRecipes()) {
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
            @NotNull ItemStack item,
            @NotNull PlayerProfile profile,
            @NotNull Player p,
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
                            Converter.getItem(Material.BARRIER, Lang.getError("unknown-recipe"))),
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
            for (var s : Formats.recipe_vanilla.getChars('B')) {
                menu.addItem(
                        s,
                        ItemStackUtil.getCleanItem(ChestMenuUtils.getBackground()),
                        ChestMenuUtils.getEmptyClickHandler());
            }

            for (var s : Formats.recipe_vanilla.getChars('P')) {
                menu.addItem(
                        s,
                        ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(p, index + 1, recipes.length)),
                        (pl, slot, action, stack) -> {
                            if (index > 0) {
                                showMinecraftRecipe0(recipes, index - 1, item, profile, p, true);
                            }
                            return false;
                        });
            }

            for (var s : Formats.recipe_vanilla.getChars('N')) {
                menu.addItem(
                        s,
                        ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(p, index + 1, recipes.length)),
                        (pl, slot, action, stack) -> {
                            if (index < recipes.length - 1) {
                                showMinecraftRecipe0(recipes, index + 1, item, profile, p, true);
                            }
                            return false;
                        });
            }
        }

        menu.open(p);

        if (!task.isEmpty()) {
            task.start(menu.toInventory());
        }
    }

    @Override
    public <T extends Recipe> void showRecipeChoices0(
            @NotNull T recipe, ItemStack[] recipeItems, @NotNull AsyncRecipeChoiceTask task) {
        RecipeChoice[] choices = Slimefun.getMinecraftRecipeService().getRecipeShape(recipe);

        var recipeSlots = Formats.recipe_vanilla.getChars('r');
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
        displayItem(profile, item, addToHistory, true, item instanceof RecipeDisplayItem ? Formats.recipe_display : Formats.recipe);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void displayItem(PlayerProfile profile, SlimefunItem item, boolean addToHistory, boolean maybeSpecial, Format format) {
        Player p = profile.getPlayer();

        if (p == null) {
            return;
        }

        ChestMenu menu = create0(p);
        Optional<String> wiki = item.getWikipage();

        if (wiki.isPresent()) {
            for (var s : format.getChars('w')) {
                menu.addItem(
                        s,
                        ItemStackUtil.getCleanItem(Converter.getItem(
                                Material.KNOWLEDGE_BOOK,
                                ChatColor.WHITE + Slimefun.getLocalization().getMessage(p, "guide.tooltips.wiki"),
                                "",
                                ChatColor.GRAY
                                        + "\u21E8 "
                                        + ChatColor.GREEN
                                        + Slimefun.getLocalization().getMessage(p, "guide.tooltips.open-itemgroup"))));
                menu.addMenuClickHandler(s, (pl, slot, itemstack, action) -> {
                    pl.closeInventory();
                    ChatUtils.sendURL(pl, wiki.get());
                    return false;
                });
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
            for (var s : format.getChars('E')) {
                menu.addItem(s, Lang.SPECIAL_MENU_ITEM, (pl, slot, itemstack, action) -> {
                    try {
                        SpecialMenuProvider.open(profile.getPlayer(), profile, getMode(), item);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        Debug.trace(e);
                    }
                    return false;
                });
            }
        }

        menu.open(p);

        if (!task.isEmpty()) {
            task.start(menu.toInventory());
        }
    }

    @Override
    public void displayItem0(
            @NotNull ChestMenu menu,
            @NotNull PlayerProfile profile,
            @NotNull Player p,
            Object item,
            ItemStack output,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe,
            @NotNull AsyncRecipeChoiceTask task) {
        displayItem(menu, profile, p, item, output, recipeType, recipe, task, Formats.recipe);
    }

    @Override
    public void displayItem(
            @NotNull ChestMenu menu,
            @NotNull PlayerProfile profile,
            @NotNull Player p,
            Object item,
            ItemStack output,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe,
            @NotNull AsyncRecipeChoiceTask task,
            Format format) {
        for (var s : format.getChars('b')) {
            addBackButton0(menu, s, p, profile);
        }

        MenuClickHandler clickHandler = (pl, slot, itemstack, action) -> {
            try {
                if (itemstack != null && itemstack.getType() != Material.AIR) {
                    String id = itemstack.getItemMeta().getPersistentDataContainer().get(JEGSlimefunGuideImplementation.UNLOCK_ITEM_KEY, PersistentDataType.STRING);
                    if (id != null) {
                        SlimefunItem sfItem = SlimefunItem.getById(id);
                        if (sfItem != null && !sfItem.isDisabledIn(p.getWorld())) {
                            Research research = sfItem.getResearch();
                            if (research != null) {
                                // try research and re-open this page
                                if (!Slimefun.getRegistry().getCurrentlyResearchingPlayers().contains(p.getUniqueId())) {
                                    if (profile.hasUnlocked(research)) {
                                        // re-open
                                        GuideUtil.removeLastEntry(profile.getGuideHistory());
                                        if (item instanceof SlimefunItem slimefunItem) {
                                            displayItem(profile, slimefunItem, true, true, format);
                                        } else if (item instanceof ItemStack itemStack) {
                                            displayItem(profile, itemStack, 0, true);
                                        }
                                    } else {
                                        PlayerPreResearchEvent event = new PlayerPreResearchEvent(p, research, sfItem);
                                        Bukkit.getPluginManager().callEvent(event);
                                        if (!event.isCancelled()) {
                                            if (research.canUnlock(p)) {
                                                // unlock research
                                                this.unlockItem(p, sfItem, (p2) -> {
                                                    // re-open
                                                    GuideUtil.removeLastEntry(profile.getGuideHistory());
                                                    if (item instanceof SlimefunItem slimefunItem) {
                                                        displayItem(profile, slimefunItem, true, true, format);
                                                    } else if (item instanceof ItemStack itemStack) {
                                                        displayItem(profile, itemStack, 0, true);
                                                    }
                                                });
                                            } else {
                                                Slimefun.getLocalization().sendMessage(p, "messages.not-enough-xp", true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        displayItem(profile, itemstack, 0, true);
                    }
                }
            } catch (Exception | LinkageError x) {
                printErrorMessage0(pl, x);
            }
            return false;
        };

        boolean isSlimefunRecipe = item instanceof SlimefunItem;

        var recipeSlots = format.getChars('r');
        for (int i = 0; i < 9; i++) {
            ItemStack recipeItem = JEGSlimefunGuideImplementation.getDisplayItem(p, isSlimefunRecipe, recipe[i]);
            menu.addItem(recipeSlots.get(i), ItemStackUtil.getCleanItem(recipeItem), clickHandler);
            BeginnerUtils.applyBeginnersGuide(this, menu, recipeSlots.get(i));
            GroupLinker.applyGroupLinker(this, menu, recipeSlots.get(i));

            if (recipeItem != null && item instanceof MultiBlockMachine) {
                for (Tag<Material> tag : MultiBlock.getSupportedTags()) {
                    if (tag.isTagged(recipeItem.getType())) {
                        task.add(recipeSlots.get(i), tag);
                        break;
                    }
                }
            }
        }

        for (var s : format.getChars('t')) {
            menu.addItem(s, ItemStackUtil.getCleanItem(recipeType.getItem(p)), ChestMenuUtils.getEmptyClickHandler());
            BeginnerUtils.applyBeginnersGuide(this, menu, s);
            GroupLinker.applyGroupLinker(this, menu, s);
        }
        for (var s : format.getChars('i')) {
            menu.addItem(s, ItemStackUtil.getCleanItem(output), ChestMenuUtils.getEmptyClickHandler());
            BeginnerUtils.applyBeginnersGuide(this, menu, s);
            GroupLinker.applyGroupLinker(this, menu, s);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void createHeader(Player p, PlayerProfile profile, ChestMenu menu) {
        createHeader(p, profile, menu, Formats.main);
    }

    @Override
    @ParametersAreNonnullByDefault
    @ApiStatus.Experimental
    public void createHeader(Player p, PlayerProfile profile, ChestMenu menu, Format format) {
        for (var s : format.getChars('B')) {
            menu.addItem(
                    s,
                    ItemStackUtil.getCleanItem(ChestMenuUtils.getBackground()),
                    ChestMenuUtils.getEmptyClickHandler());
        }

        for (var s : format.getChars('b')) {
            addBackButton0(menu, s, p, profile);
        }

        // Settings Panel
        for (var s : format.getChars('T')) {
            menu.addItem(s, ItemStackUtil.getCleanItem(ChestMenuUtils.getMenuButton(p)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> {
                SlimefunGuideSettings.openSettings(pl, pl.getInventory().getItemInMainHand());
                return false;
            });
        }

        // Search feature!
        for (var s : format.getChars('S')) {
            menu.addItem(s, ItemStackUtil.getCleanItem(ChestMenuUtils.getSearchButton(p)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> {
                pl.closeInventory();

                Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                ChatInput.waitForPlayer(
                        JustEnoughGuide.getInstance(), pl, msg -> openSearch(profile, msg, isSurvivalMode()));

                return false;
            });
        }

        GuideUtil.addRTSButton(menu, p, profile, Formats.main, getMode(), this);
        GuideUtil.addBookMarkButton(menu, p, profile, Formats.main, this, null);
        GuideUtil.addItemMarkButton(menu, p, profile, Formats.main, this, null);
    }


    @Override
    @ParametersAreNonnullByDefault
    @ApiStatus.Experimental
    public void createHeader(Player p, PlayerProfile profile, ChestMenu menu, ItemGroup itemGroup) {
        for (var s : Formats.main.getChars('B')) {
            menu.addItem(
                    s,
                    ItemStackUtil.getCleanItem(ChestMenuUtils.getBackground()),
                    ChestMenuUtils.getEmptyClickHandler());
        }

        // Settings Panel
        for (var s : Formats.main.getChars('T')) {
            menu.addItem(s, ItemStackUtil.getCleanItem(ChestMenuUtils.getMenuButton(p)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> {
                SlimefunGuideSettings.openSettings(pl, pl.getInventory().getItemInMainHand());
                return false;
            });
        }

        // Search feature!
        for (var s : Formats.main.getChars('S')) {
            menu.addItem(s, ItemStackUtil.getCleanItem(ChestMenuUtils.getSearchButton(p)));
            menu.addMenuClickHandler(s, (pl, slot, item, action) -> {
                pl.closeInventory();

                Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                ChatInput.waitForPlayer(
                        JustEnoughGuide.getInstance(), pl, msg -> openSearch(profile, msg, isSurvivalMode()));

                return false;
            });
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
                    SlimefunOfficialSupporter.getBackButton(p));

            menu.addMenuClickHandler(slot, (pl, s, is, action) -> {
                if (action.isShiftClicked()) {
                    openMainMenu(profile, profile.getGuideHistory().getMainMenuPage());
                } else {
                    history.goBack(this);
                }
                return false;
            });

        } else {
            menu.addItem(
                    slot,
                    ItemStackUtil.getCleanItem(ChestMenuUtils.getBackButton(
                            p, "", ChatColor.GRAY + Slimefun.getLocalization().getMessage(p, "guide.back.guide"))));
            menu.addMenuClickHandler(slot, (pl, s, is, action) -> {
                openMainMenu(profile, profile.getGuideHistory().getMainMenuPage());
                return false;
            });
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void displayRecipes0(Player p, PlayerProfile profile, ChestMenu menu, RecipeDisplayItem sfItem, int page) {
        List<ItemStack> recipes = sfItem.getDisplayRecipes();

        if (!recipes.isEmpty()) {
            menu.addItem(Formats.recipe_display.getSize() - 1, ItemStackUtil.getCleanItem(null));

            if (page == 0) {
                for (var s : Formats.recipe_display.getChars('B')) {
                    menu.replaceExistingItem(
                            s,
                            ItemStackUtil.getCleanItem(Converter.getItem(
                                    ChestMenuUtils.getBackground(), sfItem.getRecipeSectionLabel(p))));
                    menu.addMenuClickHandler(s, ChestMenuUtils.getEmptyClickHandler());
                }
            }

            var ds = Formats.recipe_display.getChars('d');
            var l = ds.size();
            int pages = (recipes.size() - 1) / l + 1;

            for (var s : Formats.recipe_display.getChars('P')) {
                menu.replaceExistingItem(
                        s, ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(p, page + 1, pages)));
                menu.addMenuClickHandler(s, (pl, slot, itemstack, action) -> {
                    if (page > 0) {
                        displayRecipes0(pl, profile, menu, sfItem, page - 1);
                        Sounds.playFor(pl, Sounds.GUIDE_BUTTON_CLICK_SOUND);
                    }

                    return false;
                });
            }

            for (var s : Formats.recipe_display.getChars('N')) {
                menu.replaceExistingItem(s, ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(p, page + 1, pages)));
                menu.addMenuClickHandler(s, (pl, slot, itemstack, action) -> {
                    if (recipes.size() > (l * (page + 1))) {
                        displayRecipes0(pl, profile, menu, sfItem, page + 1);
                        Sounds.playFor(pl, Sounds.GUIDE_BUTTON_CLICK_SOUND);
                    }

                    return false;
                });
            }

            var fds = RecipeDisplayFormat.fenceShuffle(ds);
            for (int i = 0; i < l; i++) {
                addDisplayRecipe0(menu, profile, recipes, fds.get(i), i, page);
            }
        }
    }

    @Override
    public void addDisplayRecipe0(
            @NotNull ChestMenu menu,
            @NotNull PlayerProfile profile,
            @NotNull List<ItemStack> recipes,
            int slot,
            int i,
            int page) {
        var l = Formats.recipe_display.getChars('d').size();
        if ((i + (page * l)) < recipes.size()) {
            ItemStack displayItem = recipes.get(i + (page * l));

            /*
             * We want to clone this item to avoid corrupting the original
             * but we wanna make sure no stupid addon creator sneaked some nulls in here
             */
            if (displayItem != null) {
                displayItem = displayItem.clone();
            }

            menu.replaceExistingItem(slot, ItemStackUtil.getCleanItem(displayItem));

            if (page == 0) {
                menu.addMenuClickHandler(slot, (pl, s, itemstack, action) -> {
                    displayItem(profile, itemstack, 0, true);
                    return false;
                });
                BeginnerUtils.applyBeginnersGuide(this, menu, slot);
                GroupLinker.applyGroupLinker(this, menu, slot);
            }
        } else {
            menu.replaceExistingItem(slot, ItemStackUtil.getCleanItem(null));
            menu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void printErrorMessage0(Player p, Throwable x) {
        p.sendMessage(Lang.getError("internal-error"));
        JustEnoughGuide.getInstance().getLogger().log(Level.SEVERE, Lang.getError("error-occurred"), x);
        JustEnoughGuide.getInstance().getLogger().warning(Lang.getError("trying-fix-guide", "player_name", p.getName()));
        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.removeLastEntry(profile.getGuideHistory());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void printErrorMessage0(Player p, SlimefunItem item, Throwable x) {
        p.sendMessage(Lang.getError("internal-error"));
        item.error(Lang.getError("item-error"), x);
        JustEnoughGuide.getInstance().getLogger().warning(Lang.getError("trying-fix-guide", "player_name", p.getName()));
        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.removeLastEntry(profile.getGuideHistory());
    }
}