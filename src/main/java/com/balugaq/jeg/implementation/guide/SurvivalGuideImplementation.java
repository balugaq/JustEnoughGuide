package com.balugaq.jeg.implementation.guide;

import com.balugaq.jeg.api.groups.RTSSearchGroup;
import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.interfaces.BookmarkRelocation;
import com.balugaq.jeg.api.interfaces.DisplayInSurvivalMode;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.objects.events.RTSEvents;
import com.balugaq.jeg.core.listeners.GuideListener;
import com.balugaq.jeg.core.listeners.RTSListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.Lang;
import com.balugaq.jeg.utils.LocalHelper;
import com.balugaq.jeg.utils.SlimefunOfficialSupporter;
import com.balugaq.jeg.utils.SpecialMenuProvider;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerPreResearchEvent;
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
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlock;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AsyncRecipeChoiceTask;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.libraries.dough.recipes.MinecraftRecipe;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.SlimefunGuideItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.MenuClickHandler;
import net.wesjd.anvilgui.AnvilGUI;
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
import org.jetbrains.annotations.NotNull;

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
    private static final int RTS_SLOT = 6;
    @Deprecated
    private static final ItemStack RTS_ITEM = Lang.getIcon("guide.real-time-search", Material.ANVIL);
    private static final NamespacedKey UNLOCK_ITEM_KEY = new NamespacedKey(JustEnoughGuide.getInstance(), "unlock_item");
    private static final int MAX_ITEM_GROUPS = 36;
    private static final int SPECIAL_MENU_SLOT = 26;
    private static final ItemStack SPECIAL_MENU_ITEM = Lang.getIcon("guide.special-menu", Material.COMPASS);

    private final int[] recipeSlots = {3, 4, 5, 12, 13, 14, 21, 22, 23};
    private final @NotNull ItemStack item;

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
                return ItemStackUtil.getCleanItem(new CustomItemStack(
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
                                : new CustomItemStack(new CustomItemStack(
                                Material.BARRIER,
                                ItemUtils.getItemName(item),
                                "&4&l" + Slimefun.getLocalization().getMessage(p, "guide.locked"),
                                "",
                                lore), meta -> {
                            meta.getPersistentDataContainer().set(UNLOCK_ITEM_KEY, PersistentDataType.STRING, slimefunItem.getId());
                        }));
            } else {
                return ItemStackUtil.getCleanItem(
                        slimefunItem.canUse(p, false)
                                ? item
                                : new CustomItemStack(new CustomItemStack(
                                Material.BARRIER,
                                ItemUtils.getItemName(item),
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
    protected @NotNull List<ItemGroup> getVisibleItemGroups(@NotNull Player p, @NotNull PlayerProfile profile) {
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

        ChestMenu menu = create(p);
        List<ItemGroup> itemGroups = getVisibleItemGroups(p, profile);

        int index = 9;
        createHeader(p, profile, menu);

        int target = (MAX_ITEM_GROUPS * (page - 1)) - 1;

        while (target < (itemGroups.size() - 1) && index < MAX_ITEM_GROUPS + 9) {
            target++;

            ItemGroup group = itemGroups.get(target);
            showItemGroup(menu, p, profile, group, index);

            index++;
        }

        int pages = target == itemGroups.size() - 1 ? page : (itemGroups.size() - 1) / MAX_ITEM_GROUPS + 1;

        menu.addItem(46, ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(p, page, pages)));
        menu.addMenuClickHandler(46, (pl, slot, item, action) -> {
            int next = page - 1;

            if (next != page && next > 0) {
                openMainMenu(profile, next);
            }

            return false;
        });

        menu.addItem(52, ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(p, page, pages)));
        menu.addMenuClickHandler(52, (pl, slot, item, action) -> {
            int next = page + 1;

            if (next != page && next <= pages) {
                openMainMenu(profile, next);
            }

            return false;
        });

        if (JustEnoughGuide.getConfigManager().isBookmark()) {
            menu.addItem(
                    49,
                    ItemStackUtil.getCleanItem(GuideUtil.getBookMarkMenuButton()),
                    (pl, slot, itemstack, action) -> {
                        openBookMarkGroup(pl, profile);
                        return false;
                    });
        }

        GuideListener.guideModeMap.put(p, getMode());

        menu.open(p);
    }

    private void showItemGroup(
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
                    ItemStackUtil.getCleanItem(new CustomItemStack(
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

        if (itemGroup instanceof FlexItemGroup flexItemGroup) {
            flexItemGroup.open(p, profile, getMode());
            return;
        }

        if (isSurvivalMode()) {
            profile.getGuideHistory().add(itemGroup, page);
        }

        ChestMenu menu = create(p);
        createHeader(p, profile, menu, itemGroup);

        addBackButton(menu, 1, p, profile);

        int pages = (itemGroup.getItems().size() - 1) / MAX_ITEM_GROUPS + 1;

        menu.addItem(46, ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(p, page, pages)));
        menu.addMenuClickHandler(46, (pl, slot, item, action) -> {
            int next = page - 1;

            if (next != page && next > 0) {
                openItemGroup(profile, itemGroup, next);
            }

            return false;
        });

        menu.addItem(52, ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(p, page, pages)));
        menu.addMenuClickHandler(52, (pl, slot, item, action) -> {
            int next = page + 1;

            if (next != page && next <= pages) {
                openItemGroup(profile, itemGroup, next);
            }

            return false;
        });

        int index = 9;
        int itemGroupIndex = MAX_ITEM_GROUPS * (page - 1);

        for (int i = 0; i < MAX_ITEM_GROUPS; i++) {
            int target = itemGroupIndex + i;

            if (target >= itemGroup.getItems().size()) {
                break;
            }

            SlimefunItem sfitem = itemGroup.getItems().get(target);

            if (!sfitem.isDisabledIn(p.getWorld())) {
                displaySlimefunItem(menu, itemGroup, p, profile, sfitem, page, index);
                index++;
            }
        }

        menu.open(p);
    }

    private void displaySlimefunItem(
            @NotNull ChestMenu menu,
            @NotNull ItemGroup itemGroup,
            @NotNull Player p,
            @NotNull PlayerProfile profile,
            @NotNull SlimefunItem sfitem,
            int page,
            int index) {
        Research research = sfitem.getResearch();

        if (isSurvivalMode() && !hasPermission(p, sfitem)) {
            List<String> message = Slimefun.getPermissionsService().getLore(sfitem);
            menu.addItem(
                    index,
                    ItemStackUtil.getCleanItem(new CustomItemStack(
                            ChestMenuUtils.getNoPermissionItem(),
                            sfitem.getItemName(),
                            message.toArray(new String[0]))));
            menu.addMenuClickHandler(index, ChestMenuUtils.getEmptyClickHandler());
        } else if (isSurvivalMode() && research != null && !profile.hasUnlocked(research)) {
            menu.addItem(
                    index,
                    ItemStackUtil.getCleanItem(new CustomItemStack(
                            ChestMenuUtils.getNoPermissionItem(),
                            "&f" + ItemUtils.getItemName(sfitem.getItem()),
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
                    printErrorMessage(pl, sfitem, x);
                }

                return false;
            });
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void openSearch(PlayerProfile profile, String input, boolean addToHistory) {
        openSearch(profile, input, 0, addToHistory);
    }

    @ParametersAreNonnullByDefault
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
    public void displayItem(PlayerProfile profile, ItemStack item, int index, boolean addToHistory) {
        Player p = profile.getPlayer();

        if (p == null || item == null || item.getType() == Material.AIR) {
            return;
        }

        SlimefunItem sfItem = SlimefunItem.getByItem(item);

        if (sfItem != null) {
            displayItem(profile, sfItem, addToHistory);
            return;
        }

        if (!SlimefunOfficialSupporter.isShowVanillaRecipes()) {
            return;
        }

        Recipe[] recipes = Slimefun.getMinecraftRecipeService().getRecipesFor(item);

        if (recipes.length == 0) {
            return;
        }

        showMinecraftRecipe(recipes, index, item, profile, p, addToHistory);
    }

    private void showMinecraftRecipe(
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
            showRecipeChoices(recipe, recipeItems, task);

            recipeType = new RecipeType(optional.get());
            result = recipe.getResult();
        } else {
            recipeItems = new ItemStack[]{
                    null,
                    null,
                    null,
                    null,
                    ItemStackUtil.getCleanItem(
                            new CustomItemStack(Material.BARRIER, Lang.getError("unknown-recipe"))),
                    null,
                    null,
                    null,
                    null
            };
        }

        ChestMenu menu = create(p);

        if (addToHistory) {
            profile.getGuideHistory().add(item, index);
        }

        displayItem(menu, profile, p, item, result, recipeType, recipeItems, task);

        if (recipes.length > 1) {
            for (int i = 27; i < 36; i++) {
                menu.addItem(
                        i,
                        ItemStackUtil.getCleanItem(ChestMenuUtils.getBackground()),
                        ChestMenuUtils.getEmptyClickHandler());
            }

            menu.addItem(
                    28,
                    ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(p, index + 1, recipes.length)),
                    (pl, slot, action, stack) -> {
                        if (index > 0) {
                            showMinecraftRecipe(recipes, index - 1, item, profile, p, true);
                        }
                        return false;
                    });

            menu.addItem(
                    34,
                    ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(p, index + 1, recipes.length)),
                    (pl, slot, action, stack) -> {
                        if (index < recipes.length - 1) {
                            showMinecraftRecipe(recipes, index + 1, item, profile, p, true);
                        }
                        return false;
                    });
        }

        menu.open(p);

        if (!task.isEmpty()) {
            task.start(menu.toInventory());
        }
    }

    private <T extends Recipe> void showRecipeChoices(
            @NotNull T recipe, ItemStack[] recipeItems, @NotNull AsyncRecipeChoiceTask task) {
        RecipeChoice[] choices = Slimefun.getMinecraftRecipeService().getRecipeShape(recipe);

        if (choices.length == 1 && choices[0] instanceof MaterialChoice materialChoice) {
            recipeItems[4] = new ItemStack(materialChoice.getChoices().get(0));

            if (materialChoice.getChoices().size() > 1) {
                task.add(recipeSlots[4], materialChoice);
            }
        } else {
            for (int i = 0; i < choices.length; i++) {
                if (choices[i] instanceof MaterialChoice materialChoice) {
                    recipeItems[i] = new ItemStack(materialChoice.getChoices().get(0));

                    if (materialChoice.getChoices().size() > 1) {
                        task.add(recipeSlots[i], materialChoice);
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

    @ParametersAreNonnullByDefault
    public void displayItem(PlayerProfile profile, SlimefunItem item, boolean addToHistory, boolean maybeSpecial) {
        Player p = profile.getPlayer();

        if (p == null) {
            return;
        }

        ChestMenu menu = create(p);
        Optional<String> wiki = item.getWikipage();

        if (wiki.isPresent()) {
            menu.addItem(
                    8,
                    ItemStackUtil.getCleanItem(new CustomItemStack(
                            Material.KNOWLEDGE_BOOK,
                            ChatColor.WHITE + Slimefun.getLocalization().getMessage(p, "guide.tooltips.wiki"),
                            "",
                            ChatColor.GRAY
                                    + "\u21E8 "
                                    + ChatColor.GREEN
                                    + Slimefun.getLocalization().getMessage(p, "guide.tooltips.open-itemgroup"))));
            menu.addMenuClickHandler(8, (pl, slot, itemstack, action) -> {
                pl.closeInventory();
                ChatUtils.sendURL(pl, wiki.get());
                return false;
            });
        }

        AsyncRecipeChoiceTask task = new AsyncRecipeChoiceTask();

        if (addToHistory) {
            profile.getGuideHistory().add(item);
        }

        ItemStack result = item.getRecipeOutput();
        RecipeType recipeType = item.getRecipeType();
        ItemStack[] recipe = item.getRecipe();

        displayItem(menu, profile, p, item, result, recipeType, recipe, task);

        if (item instanceof RecipeDisplayItem recipeDisplayItem) {
            displayRecipes(p, profile, menu, recipeDisplayItem, 0);
        }

        if (maybeSpecial && SpecialMenuProvider.isSpecialItem(item)) {
            menu.addItem(SPECIAL_MENU_SLOT, SPECIAL_MENU_ITEM, (pl, slot, itemstack, action) -> {
                try {
                    SpecialMenuProvider.open(profile.getPlayer(), profile, getMode(), item);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return false;
            });
        }

        menu.open(p);

        if (!task.isEmpty()) {
            task.start(menu.toInventory());
        }
    }

    private void displayItem(
            @NotNull ChestMenu menu,
            @NotNull PlayerProfile profile,
            @NotNull Player p,
            Object item,
            ItemStack output,
            @NotNull RecipeType recipeType,
            ItemStack[] recipe,
            @NotNull AsyncRecipeChoiceTask task) {
        addBackButton(menu, 0, p, profile);

        MenuClickHandler clickHandler = (pl, slot, itemstack, action) -> {
            try {
                if (!action.isRightClicked() && action.isShiftClicked()) {
                    // Open the item's item group if exists
                    final SlimefunItem sfItem = SlimefunItem.getByItem(itemstack);
                    if (sfItem != null) {
                        final ItemGroup itemGroup = sfItem.getItemGroup();
                        if (itemGroup != null) {
                            int page = 1;
                            if (isTaggedGroupType(itemGroup)) {
                                page = (itemGroup.getItems().indexOf(sfItem) / 36) + 1;
                            }
                            openItemGroup(profile, itemGroup, page);
                            return false;
                        }
                    }
                }
                if (itemstack != null && itemstack.getType() != Material.AIR) {
                    String id = itemstack.getItemMeta().getPersistentDataContainer().get(UNLOCK_ITEM_KEY, PersistentDataType.STRING);
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
                                            displayItem(profile, slimefunItem, true, true);
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
                                                        displayItem(profile, slimefunItem, true, true);
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
                printErrorMessage(pl, x);
            }
            return false;
        };

        boolean isSlimefunRecipe = item instanceof SlimefunItem;

        for (int i = 0; i < 9; i++) {
            ItemStack recipeItem = getDisplayItem(p, isSlimefunRecipe, recipe[i]);
            menu.addItem(recipeSlots[i], ItemStackUtil.getCleanItem(recipeItem), clickHandler);

            if (recipeItem != null && item instanceof MultiBlockMachine) {
                for (Tag<Material> tag : MultiBlock.getSupportedTags()) {
                    if (tag.isTagged(recipeItem.getType())) {
                        task.add(recipeSlots[i], tag);
                        break;
                    }
                }
            }
        }

        menu.addItem(10, ItemStackUtil.getCleanItem(recipeType.getItem(p)), ChestMenuUtils.getEmptyClickHandler());
        menu.addItem(16, ItemStackUtil.getCleanItem(output), ChestMenuUtils.getEmptyClickHandler());
    }

    @ParametersAreNonnullByDefault
    public void createHeader(Player p, PlayerProfile profile, ChestMenu menu) {
        for (int i = 0; i < 9; i++) {
            menu.addItem(
                    i,
                    ItemStackUtil.getCleanItem(ChestMenuUtils.getBackground()),
                    ChestMenuUtils.getEmptyClickHandler());
        }

        // Settings Panel
        menu.addItem(1, ItemStackUtil.getCleanItem(ChestMenuUtils.getMenuButton(p)));
        menu.addMenuClickHandler(1, (pl, slot, item, action) -> {
            SlimefunGuideSettings.openSettings(pl, pl.getInventory().getItemInMainHand());
            return false;
        });

        if (JustEnoughGuide.getConfigManager().isRTSSearch()) {
            menu.addItem(RTS_SLOT, ItemStackUtil.getCleanItem(Lang.RTS_ITEM), (pl, slot, itemstack, action) -> {
                RTSSearchGroup.newRTSInventoryFor(pl, getMode(), (s, stateSnapshot) -> {
                    if (s == AnvilGUI.Slot.INPUT_LEFT) {
                        // back button clicked
                        GuideHistory history = profile.getGuideHistory();
                        if (action.isShiftClicked()) {
                            openMainMenu(profile, profile.getGuideHistory().getMainMenuPage());
                        } else {
                            history.goBack(this);
                        }
                        return;
                    } else if (s == AnvilGUI.Slot.INPUT_RIGHT) {
                        // previous page button clicked
                        SearchGroup rts = RTSSearchGroup.RTS_SEARCH_GROUPS.get(pl);
                        if (rts != null) {
                            int oldPage = RTSSearchGroup.RTS_PAGES.getOrDefault(pl, 1);
                            int newPage = Math.max(1, oldPage - 1);
                            RTSEvents.PageChangeEvent event = new RTSEvents.PageChangeEvent(pl, RTSSearchGroup.RTS_PLAYERS.get(pl), oldPage, newPage, getMode());
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                synchronized (RTSSearchGroup.RTS_PAGES) {
                                    RTSSearchGroup.RTS_PAGES.put(pl, newPage);
                                }
                            }
                        }
                    } else if (s == AnvilGUI.Slot.OUTPUT) {
                        // next page button clicked
                        SearchGroup rts = RTSSearchGroup.RTS_SEARCH_GROUPS.get(pl);
                        if (rts != null) {
                            int oldPage = RTSSearchGroup.RTS_PAGES.getOrDefault(pl, 1);
                            int newPage = Math.min((rts.slimefunItemList.size() - 1) / RTSListener.FILL_ORDER.length + 1, oldPage + 1);
                            RTSEvents.PageChangeEvent event = new RTSEvents.PageChangeEvent(pl, RTSSearchGroup.RTS_PLAYERS.get(pl), oldPage, newPage, getMode());
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                synchronized (RTSSearchGroup.RTS_PAGES) {
                                    RTSSearchGroup.RTS_PAGES.put(pl, newPage);
                                }
                            }
                        }
                    }
                }, new int[]{AnvilGUI.Slot.INPUT_LEFT, AnvilGUI.Slot.INPUT_RIGHT, AnvilGUI.Slot.OUTPUT}, null);
                return false;
            });
        }

        // Search feature!
        menu.addItem(7, ItemStackUtil.getCleanItem(ChestMenuUtils.getSearchButton(p)));
        menu.addMenuClickHandler(7, (pl, slot, item, action) -> {
            pl.closeInventory();

            Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
            ChatInput.waitForPlayer(
                    JustEnoughGuide.getInstance(), pl, msg -> openSearch(profile, msg, isSurvivalMode()));

            return false;
        });

        for (int i = 45; i < 54; i++) {
            menu.addItem(
                    i,
                    ItemStackUtil.getCleanItem(ChestMenuUtils.getBackground()),
                    ChestMenuUtils.getEmptyClickHandler());
        }

        if (JustEnoughGuide.getConfigManager().isBookmark()) {
            menu.addItem(
                    49,
                    ItemStackUtil.getCleanItem(GuideUtil.getBookMarkMenuButton()),
                    (pl, slot, itemstack, action) -> {
                        openBookMarkGroup(pl, profile);
                        return false;
                    });
        }
    }

    @ParametersAreNonnullByDefault
    public void createHeader(Player p, PlayerProfile profile, ChestMenu menu, ItemGroup itemGroup) {
        for (int i = 0; i < 9; i++) {
            menu.addItem(
                    i,
                    ItemStackUtil.getCleanItem(ChestMenuUtils.getBackground()),
                    ChestMenuUtils.getEmptyClickHandler());
        }

        // Settings Panel
        menu.addItem(1, ItemStackUtil.getCleanItem(ChestMenuUtils.getMenuButton(p)));
        menu.addMenuClickHandler(1, (pl, slot, item, action) -> {
            SlimefunGuideSettings.openSettings(pl, pl.getInventory().getItemInMainHand());
            return false;
        });

        // Search feature!
        menu.addItem(7, ItemStackUtil.getCleanItem(ChestMenuUtils.getSearchButton(p)));
        menu.addMenuClickHandler(7, (pl, slot, item, action) -> {
            pl.closeInventory();

            Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
            ChatInput.waitForPlayer(
                    JustEnoughGuide.getInstance(), pl, msg -> openSearch(profile, msg, isSurvivalMode()));

            return false;
        });

        for (int i = 45; i < 54; i++) {
            menu.addItem(i, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        if (JustEnoughGuide.getConfigManager().isBookmark()) {
            BookmarkRelocation b = null;
            if (itemGroup instanceof BookmarkRelocation bookmarkRelocation) {
                b = bookmarkRelocation;
            }

            menu.addItem(
                    b != null ? b.getBookMark(this, p) : 49,
                    ItemStackUtil.getCleanItem(GuideUtil.getBookMarkMenuButton()),
                    (pl, slot, itemstack, action) -> {
                        openBookMarkGroup(pl, profile);
                        return false;
                    });

            if (isTaggedGroupType(itemGroup)) {
                menu.addItem(
                        b != null ? b.getItemMark(this, p) : 48,
                        ItemStackUtil.getCleanItem(GuideUtil.getItemMarkMenuButton()),
                        (pl, slot, itemstack, action) -> {
                            openItemMarkGroup(itemGroup, pl, profile);
                            return false;
                        });
            }
        }
    }

    private boolean isTaggedGroupType(@NotNull ItemGroup itemGroup) {
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

    private void addBackButton(@NotNull ChestMenu menu, int slot, @NotNull Player p, @NotNull PlayerProfile profile) {
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

    @ParametersAreNonnullByDefault
    private void displayRecipes(Player p, PlayerProfile profile, ChestMenu menu, RecipeDisplayItem sfItem, int page) {
        List<ItemStack> recipes = sfItem.getDisplayRecipes();

        if (!recipes.isEmpty()) {
            menu.addItem(53, ItemStackUtil.getCleanItem(null));

            if (page == 0) {
                for (int i = 27; i < 36; i++) {
                    menu.replaceExistingItem(
                            i,
                            ItemStackUtil.getCleanItem(new CustomItemStack(
                                    ChestMenuUtils.getBackground(), sfItem.getRecipeSectionLabel(p))));
                    menu.addMenuClickHandler(i, ChestMenuUtils.getEmptyClickHandler());
                }
            }

            int pages = (recipes.size() - 1) / 18 + 1;

            menu.replaceExistingItem(
                    28, ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(p, page + 1, pages)));
            menu.addMenuClickHandler(28, (pl, slot, itemstack, action) -> {
                if (page > 0) {
                    displayRecipes(pl, profile, menu, sfItem, page - 1);
                    SoundEffect.GUIDE_BUTTON_CLICK_SOUND.playFor(pl);
                }

                return false;
            });

            menu.replaceExistingItem(34, ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(p, page + 1, pages)));
            menu.addMenuClickHandler(34, (pl, slot, itemstack, action) -> {
                if (recipes.size() > (18 * (page + 1))) {
                    displayRecipes(pl, profile, menu, sfItem, page + 1);
                    SoundEffect.GUIDE_BUTTON_CLICK_SOUND.playFor(pl);
                }

                return false;
            });

            int inputs = 36;
            int outputs = 45;

            for (int i = 0; i < 18; i++) {
                int slot;

                if (i % 2 == 0) {
                    slot = inputs;
                    inputs++;
                } else {
                    slot = outputs;
                    outputs++;
                }

                addDisplayRecipe(menu, profile, recipes, slot, i, page);
            }
        }
    }

    private void addDisplayRecipe(
            @NotNull ChestMenu menu,
            @NotNull PlayerProfile profile,
            @NotNull List<ItemStack> recipes,
            int slot,
            int i,
            int page) {
        if ((i + (page * 18)) < recipes.size()) {
            ItemStack displayItem = recipes.get(i + (page * 18));

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
            }
        } else {
            menu.replaceExistingItem(slot, ItemStackUtil.getCleanItem(null));
            menu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
        }
    }

    @NotNull
    public ChestMenu create(@NotNull Player p) {
        ChestMenu menu = new ChestMenu(JustEnoughGuide.getConfigManager().getSurvivalGuideTitle());

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(SoundEffect.GUIDE_BUTTON_CLICK_SOUND::playFor);
        return menu;
    }

    @ParametersAreNonnullByDefault
    private void printErrorMessage(Player p, Throwable x) {
        p.sendMessage(Lang.getError("internal-error"));
        JustEnoughGuide.getInstance().getLogger().log(Level.SEVERE, Lang.getError("error-occurred"), x);
        JustEnoughGuide.getInstance().getLogger().warning(Lang.getError("trying-fix-guide", "player_name", p.getName()));
        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.removeLastEntry(profile.getGuideHistory());
    }

    @ParametersAreNonnullByDefault
    private void printErrorMessage(Player p, SlimefunItem item, Throwable x) {
        p.sendMessage(Lang.getError("internal-error"));
        item.error(Lang.getError("item-error"), x);
        JustEnoughGuide.getInstance()
                .getLogger()
                .warning(Lang.getError("trying-fix-guide", "player_name", p.getName()));
        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.removeLastEntry(profile.getGuideHistory());
    }
}
