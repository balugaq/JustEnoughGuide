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
import com.balugaq.jeg.api.interfaces.VanillaItemShade;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.items.GroupSetup;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.SlimefunOfficialSupporter;
import com.balugaq.jeg.utils.compatibility.Sounds;
import com.balugaq.jeg.utils.formatter.Formats;
import com.google.common.base.Preconditions;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * This class used to create groups to display all the vanilla items in the guide.
 * Display for JEG recipe complete in NetworksExpansion / SlimeAEPlugin
 *
 * @author balugaq
 * @see JustEnoughGuide#vanillaItemsGroupDisplayableFor(Player, boolean)
 * @see JustEnoughGuide#vanillaItemsGroupIsDisplayableFor(Player)
 * @since 1.7
 */
@SuppressWarnings({"deprecation", "unused", "ConstantValue"})
@NotDisplayInCheatMode
public class VanillaItemsGroup extends FlexItemGroup {
    public static final List<SlimefunItem> slimefunItems = new ArrayList<>();

    @Deprecated
    private static final @NotNull Set<Player> displayableFor = ConcurrentHashMap.newKeySet();

    private static final JavaPlugin JAVA_PLUGIN = JustEnoughGuide.getInstance();

    static {
        JustEnoughGuide.runLater(
                () -> {
                    boolean before = JustEnoughGuide.disableAutomaticallyLoadItems();
                    try {
                        for (Material material : Material.values()) {
                            if (!material.isAir() && material.isItem() && !material.isLegacy()) {
                                slimefunItems.add(createSlimefunItem(material));
                            }
                        }
                    } catch (Exception e) {
                        Debug.trace(e);
                    } finally {
                        JustEnoughGuide.setAutomaticallyLoadItems(before);
                    }
                },
                1L);
    }

    private final int page;
    private Map<Integer, VanillaItemsGroup> pageMap = new LinkedHashMap<>();

    @ParametersAreNonnullByDefault
    public VanillaItemsGroup(NamespacedKey key, ItemStack icon) {
        super(key, icon, Integer.MAX_VALUE);
        this.page = 1;
        this.pageMap.put(1, this);
    }

    /**
     * Constructor of hiddenItemsGroup.
     *
     * @param hiddenItemsGroup The hiddenItemsGroup to copy.
     * @param page             The page number to display.
     */
    protected VanillaItemsGroup(@NotNull VanillaItemsGroup hiddenItemsGroup, int page) {
        super(hiddenItemsGroup.key, new ItemStack(Material.BARRIER));
        this.page = page;
        this.pageMap.put(page, this);
    }

    /**
     * @see JustEnoughGuide#vanillaItemsGroupDisplayableFor(Player, boolean)
     */
    @Deprecated
    public static void displayableFor(@NotNull Player player, boolean displayable) {
        if (displayable) {
            displayableFor.add(player);
        } else {
            displayableFor.remove(player);
        }
    }

    /**
     * @see JustEnoughGuide#vanillaItemsGroupIsDisplayableFor(Player)
     */
    @Deprecated
    public static boolean isDisplayableFor(@NotNull Player player) {
        return displayableFor.contains(player);
    }

    @ParametersAreNonnullByDefault
    private static @NotNull VanillaItem createSlimefunItem(Material material) {
        Preconditions.checkArgument(material != null, "The material cannot be null.");
        Preconditions.checkArgument(!material.isAir(), "The material cannot be air.");
        Preconditions.checkArgument(material.isItem(), "The material must be an item.");
        Preconditions.checkArgument(!material.isLegacy(), "The material cannot be legacy.");

        VanillaItem vi = VanillaItem.create(material);
        vi.register(JustEnoughGuide.getInstance());
        return vi;
    }

    /**
     * Always returns false.
     *
     * @param player            The player who opened the group.
     * @param playerProfile     The player's profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     * @return false.
     */
    @Override
    public boolean isVisible(
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode) {
        return true;
    }

    /**
     * Opens the group for the player.
     *
     * @param player            The player who opened the group.
     * @param playerProfile     The player's profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     */
    @Override
    public void open(
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode) {
        playerProfile.getGuideHistory().add(this, this.page);
        this.generateMenu(player, playerProfile, slimefunGuideMode).open(player);
    }

    /**
     * Reopens the menu for the player.
     *
     * @param player            The player who opened the group.
     * @param playerProfile     The player's profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     */
    public void refresh(
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode) {
        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
        this.open(player, playerProfile, slimefunGuideMode);
    }

    /**
     * Generates the menu for the player.
     *
     * @param player            The player who opened the group.
     * @param playerProfile     The player's profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     * @return The generated menu.
     */
    @NotNull
    private ChestMenu generateMenu(
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu("Vanilla Items");

        chestMenu.setEmptySlotsClickable(false);
        chestMenu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sounds.GUIDE_BUTTON_CLICK_SOUND, 1, 1));

        SlimefunGuideImplementation implementation = Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode);

        for (int ss : Formats.sub.getChars('b')) {
            chestMenu.addItem(ss, PatchScope.Back.patch(player, ChestMenuUtils.getBackButton(player)));
            chestMenu.addMenuClickHandler(ss, (pl, s, is, action) -> EventUtil.callEvent(
                            new GuideEvents.BackButtonClickEvent(pl, is, s, action, chestMenu, implementation))
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

        // Search feature!
        for (int ss : Formats.sub.getChars('S')) {
            chestMenu.addItem(ss, PatchScope.Search.patch(player, ChestMenuUtils.getSearchButton(player)));
            chestMenu.addMenuClickHandler(ss, (pl, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.SearchButtonClickEvent(pl, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        pl.closeInventory();

                        Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                        ChatInput.waitForPlayer(
                                JAVA_PLUGIN,
                                pl,
                                msg -> implementation.openSearch(
                                        playerProfile,
                                        msg,
                                        implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE));

                        return false;
                    }));
        }

        for (int ss : Formats.sub.getChars('P')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.PreviousPage.patch(
                            player,
                            ChestMenuUtils.getPreviousButton(
                                    player,
                                    this.page,
                                    (slimefunItems.size() - 1)
                                            / Formats.sub.getChars('i').size()
                                            + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.PreviousButtonClickEvent(p, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                        VanillaItemsGroup hiddenItemsGroup = this.getByPage(Math.max(this.page - 1, 1));
                        hiddenItemsGroup.open(player, playerProfile, slimefunGuideMode);
                        return false;
                    }));
        }

        for (int ss : Formats.sub.getChars('N')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.NextPage.patch(
                            player,
                            ChestMenuUtils.getNextButton(
                                    player,
                                    this.page,
                                    (slimefunItems.size() - 1)
                                            / Formats.sub.getChars('i').size()
                                            + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.NextButtonClickEvent(p, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                        VanillaItemsGroup hiddenItemsGroup = this.getByPage(Math.min(
                                this.page + 1,
                                (slimefunItems.size() - 1)
                                        / Formats.sub.getChars('i').size()
                                        + 1));
                        hiddenItemsGroup.open(player, playerProfile, slimefunGuideMode);
                        return false;
                    }));
        }

        for (int ss : Formats.sub.getChars('B')) {
            chestMenu.addItem(ss, PatchScope.Background.patch(player, ChestMenuUtils.getBackground()));
            chestMenu.addMenuClickHandler(ss, ChestMenuUtils.getEmptyClickHandler());
        }

        List<Integer> contentSlots = Formats.sub.getChars('i');
        for (int i = 0; i < contentSlots.size(); i++) {
            int index = i + this.page * contentSlots.size() - contentSlots.size();
            if (index < slimefunItems.size()) {
                SlimefunItem slimefunItem = slimefunItems.get(index);
                ItemStack itemStack;
                if (slimefunItem instanceof VanillaItemShade vis) {
                    itemStack = vis.getCustomIcon();
                } else {
                    itemStack = slimefunItem.getItem();
                }
                chestMenu.addItem(
                        contentSlots.get(i),
                        PatchScope.SlimefunItem.patch(player, itemStack),
                        (pl, slot, itm, action) -> EventUtil.callEvent(new GuideEvents.ItemButtonClickEvent(
                                        pl, itm, slot, action, chestMenu, implementation))
                                .ifSuccess(() -> {
                                    try {
                                        if (implementation.getMode() != SlimefunGuideMode.SURVIVAL_MODE
                                                && (pl.isOp() || pl.hasPermission("slimefun.cheat.items"))) {
                                            pl.getInventory().addItem(itemStack.clone());
                                        } else {
                                            implementation.displayItem(playerProfile, slimefunItem, true);
                                        }
                                    } catch (Exception | LinkageError x) {
                                        printErrorMessage(pl, slimefunItem, x);
                                    }

                                    return false;
                                }));
            }
        }

        GuideUtil.addRTSButton(chestMenu, player, playerProfile, Formats.sub, slimefunGuideMode, implementation);
        if (implementation instanceof JEGSlimefunGuideImplementation jeg) {
            GuideUtil.addBookMarkButton(chestMenu, player, playerProfile, Formats.sub, jeg, this);
            GuideUtil.addItemMarkButton(chestMenu, player, playerProfile, Formats.sub, jeg, this);
        }

        Formats.sub.renderCustom(chestMenu);
        return chestMenu;
    }

    /**
     * Gets the hiddenItemsGroup by page.
     *
     * @param page The page number.
     * @return The hiddenItemsGroup by page.
     */
    @NotNull
    private VanillaItemsGroup getByPage(int page) {
        if (this.pageMap.containsKey(page)) {
            return this.pageMap.get(page);
        } else {
            synchronized (this.pageMap.get(1)) {
                if (this.pageMap.containsKey(page)) {
                    return this.pageMap.get(page);
                }

                VanillaItemsGroup hiddenItemsGroup = new VanillaItemsGroup(this, page);
                hiddenItemsGroup.pageMap = this.pageMap;
                this.pageMap.put(page, hiddenItemsGroup);
                return hiddenItemsGroup;
            }
        }
    }

    /**
     * Checks if the item group is accessible for the player.
     *
     * @param p            The player.
     * @param slimefunItem The Slimefun item.
     * @return True if the item group is accessible for the player.
     */
    @ParametersAreNonnullByDefault
    private boolean isItemGroupAccessible(@NotNull Player p, @NotNull SlimefunItem slimefunItem) {
        return SlimefunOfficialSupporter.isShowHiddenItemGroups()
                || slimefunItem.getItemGroup().isAccessible(p);
    }

    /**
     * Prints an error message to the player.
     *
     * @param p    The player.
     * @param item The Slimefun item.
     * @param x    The exception.
     */
    @ParametersAreNonnullByDefault
    private void printErrorMessage(@NotNull Player p, @NotNull SlimefunItem item, @NotNull Throwable x) {
        p.sendMessage(ChatColor.DARK_RED
                + "An internal server error has occurred. Please inform an admin, check the console for"
                + " further info.");
        item.error(
                "This item has caused an error message to be thrown while viewing it in the Slimefun" + " guide.", x);
    }

    @Override
    public int getTier() {
        return Integer.MAX_VALUE;
    }

    @Getter
    public static class VanillaItem extends SlimefunItem implements VanillaItemShade {
        private final @NotNull ItemStack customIcon;

        public VanillaItem(@NotNull SlimefunItemStack item, @NotNull ItemStack customIcon) {
            super(GroupSetup.vanillaItemsGroup, item, RecipeType.NULL, new ItemStack[0], customIcon);
            this.customIcon = customIcon.clone();
        }

        @NotNull
        public static VanillaItem create(@NotNull Material material) {
            ItemStack icon = new ItemStack(material);
            return new VanillaItem(new SlimefunItemStack("JEG_VANILLA_" + material.name(), icon.clone()), icon);
        }
    }
}
