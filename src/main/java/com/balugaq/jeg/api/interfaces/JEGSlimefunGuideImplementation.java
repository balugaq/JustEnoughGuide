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

package com.balugaq.jeg.api.interfaces;

import com.balugaq.jeg.api.groups.BookmarkGroup;
import com.balugaq.jeg.api.groups.ItemMarkGroup;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.Lang;
import com.balugaq.jeg.utils.LocalHelper;
import com.balugaq.jeg.utils.SlimefunOfficialSupporter;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.compatibility.Sounds;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AsyncRecipeChoiceTask;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings({"deprecation", "unused"})
public interface JEGSlimefunGuideImplementation extends SlimefunGuideImplementation {
    NamespacedKey UNLOCK_ITEM_KEY = new NamespacedKey(JustEnoughGuide.getInstance(), "unlock_item");

    @ParametersAreNonnullByDefault
    static @NotNull ItemStack getDisplayItem(Player p, boolean isSlimefunRecipe, ItemStack item) {
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
            String lore = hasPermission0(p, slimefunItem)
                    ? Lang.getGuideMessage("locked-item", "addon_name", LocalHelper.getAddonName(itemGroup, slimefunItem.getId()), "category_name", itemGroup.getDisplayName(p))
                    : Lang.getGuideMessage("no-permission");
            Research research = slimefunItem.getResearch();
            if (research == null) {
                return ItemStackUtil.getCleanItem(
                        slimefunItem.canUse(p, false)
                                ? item
                                : Converter.getItem(
                                Converter.getItem(
                                        Material.BARRIER,
                                        ItemUtils.getItemName(item),
                                        "&4&l"
                                                + Slimefun.getLocalization()
                                                .getMessage(p, "guide.locked"),
                                        "",
                                        lore),
                                meta -> meta.getPersistentDataContainer()
                                        .set(
                                                UNLOCK_ITEM_KEY,
                                                PersistentDataType.STRING,
                                                slimefunItem.getId())));
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
    static boolean hasPermission0(Player p, SlimefunItem item) {
        return Slimefun.getPermissionsService().hasPermission(p, item);
    }

    @ParametersAreNonnullByDefault
    void showItemGroup0(ChestMenu menu, Player p, PlayerProfile profile, ItemGroup group, int index);

    @NotNull
    default ChestMenu create0(@NotNull Player p) {
        ChestMenu menu = new ChestMenu(JustEnoughGuide.getConfigManager().getSurvivalGuideTitle());

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(pl -> Sounds.playFor(pl, Sounds.GUIDE_BUTTON_CLICK_SOUND));
        return menu;
    }

    /**
     * Opens the bookmark group for the player.
     *
     * @param player  The player.
     * @param profile The player profile.
     */
    @ParametersAreNonnullByDefault
    default void openBookMarkGroup(Player player, PlayerProfile profile) {
        List<SlimefunItem> items = JustEnoughGuide.getBookmarkManager().getBookmarkedItems(player);
        if (items == null || items.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You haven't collected any items yet!");
            return;
        }
        new BookmarkGroup(this, player, items).open(player, profile, getMode());
    }

    /**
     * Opens the item mark group for the player.
     *
     * @param itemGroup The item group.
     * @param player    The player.
     * @param profile   The player profile.
     */
    @ParametersAreNonnullByDefault
    default void openItemMarkGroup(ItemGroup itemGroup, Player player, PlayerProfile profile) {
        new ItemMarkGroup(this, itemGroup, player).open(player, profile, getMode());
    }

    @ParametersAreNonnullByDefault
    void openNestedItemGroup(Player p, PlayerProfile profile, NestedItemGroup nested, int page);

    @ParametersAreNonnullByDefault
    void displaySlimefunItem0(
            ChestMenu menu,
            ItemGroup itemGroup,
            Player p,
            PlayerProfile profile,
            SlimefunItem sfitem,
            int page,
            int index);

    @ParametersAreNonnullByDefault
    void openSearch(PlayerProfile profile, String input, int page, boolean addToHistory);

    void showMinecraftRecipe0(
            Recipe @NotNull [] recipes,
            int index,
            final @NotNull ItemStack item,
            final @NotNull PlayerProfile profile,
            final @NotNull Player p,
            boolean addToHistory);

    <T extends Recipe> void showRecipeChoices0(
            final @NotNull T recipe, ItemStack[] recipeItems, @NotNull AsyncRecipeChoiceTask task);

    @ParametersAreNonnullByDefault
    default void displayItem(PlayerProfile profile, SlimefunItem item, boolean addToHistory, boolean maybeSpecial) {
        displayItem(
                profile,
                item,
                addToHistory,
                maybeSpecial,
                item instanceof RecipeDisplayItem ? Formats.recipe_display : Formats.recipe);
    }

    @ParametersAreNonnullByDefault
    void displayItem(
            PlayerProfile profile, SlimefunItem item, boolean addToHistory, boolean maybeSpecial, Format format);

    void displayItem0(
            final @NotNull ChestMenu menu,
            final @NotNull PlayerProfile profile,
            final @NotNull Player p,
            Object item,
            ItemStack output,
            final @NotNull RecipeType recipeType,
            ItemStack[] recipe,
            final @NotNull AsyncRecipeChoiceTask task);

    void displayItem(
            final @NotNull ChestMenu menu,
            final @NotNull PlayerProfile profile,
            final @NotNull Player p,
            Object item,
            ItemStack output,
            final @NotNull RecipeType recipeType,
            ItemStack[] recipe,
            final @NotNull AsyncRecipeChoiceTask task,
            Format format);

    @ParametersAreNonnullByDefault
    void createHeader(Player p, PlayerProfile profile, ChestMenu menu, Format format);

    @ParametersAreNonnullByDefault
    void createHeader(Player p, PlayerProfile profile, ChestMenu menu, ItemGroup itemGroup);

    @ParametersAreNonnullByDefault
    void addBackButton0(ChestMenu menu, @Range(from = 0, to = 53) int slot, Player p, PlayerProfile profile);

    @ParametersAreNonnullByDefault
    void displayRecipes0(Player p, PlayerProfile profile, ChestMenu menu, RecipeDisplayItem sfItem, int page);

    @ParametersAreNonnullByDefault
    void addDisplayRecipe0(
            ChestMenu menu,
            PlayerProfile profile,
            List<ItemStack> recipes,
            @Range(from = 0, to = 53) int slot,
            int index,
            int page);

    @ParametersAreNonnullByDefault
    void printErrorMessage0(Player p, Throwable x);

    @ParametersAreNonnullByDefault
    void printErrorMessage0(Player p, SlimefunItem item, Throwable x);
}
