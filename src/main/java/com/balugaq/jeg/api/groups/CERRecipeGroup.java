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

import com.balugaq.jeg.api.cost.CERCalculator;
import com.balugaq.jeg.api.cost.ValueTable;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.objects.collection.Pair;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Data;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"deprecation", "unused"})
@NotDisplayInSurvivalMode
@NotDisplayInCheatMode
public class CERRecipeGroup extends FlexItemGroup {
    public static final DecimalFormat format = new DecimalFormat("#.###");
    public static final ChestMenu.MenuClickHandler subMenuOpen = (p, s, i, a) -> {
        // ?
        return false;
    };
    private static final JavaPlugin JAVA_PLUGIN = JustEnoughGuide.getInstance();
    private final SlimefunGuideImplementation implementation;
    private final Player player;
    private final int page;
    private final SlimefunItem machine;
    private final List<RecipeWrapper> recipes;
    private final List<Pair<ItemStack, ChestMenu.MenuClickHandler>> icons;
    private Map<Integer, CERRecipeGroup> pageMap = new LinkedHashMap<>();

    /**
     * Constructor of CERRecipeGroup.
     *
     * @param implementation The Slimefun guide implementation.
     * @param player         The player who opened the group.
     * @param machine        The machine
     * @param recipes        The list of marked items.
     */
    @ParametersAreNonnullByDefault
    public CERRecipeGroup(
            final @NotNull SlimefunGuideImplementation implementation,
            final @NotNull Player player,
            final @NotNull SlimefunItem machine,
            final @NotNull List<RecipeWrapper> recipes) {
        super(
                new NamespacedKey(JAVA_PLUGIN, "jeg_cer_recipe_group_" + UUID.randomUUID()),
                new ItemStack(Material.BARRIER));
        this.page = 1;
        this.player = player;
        this.implementation = implementation;
        this.recipes = recipes;
        this.pageMap.put(1, this);
        this.machine = machine;
        this.icons = getDisplayIcons(player, machine, recipes);
    }

    /**
     * Constructor of CERRecipeGroup.
     *
     * @param cer  The CERRecipeGroup to copy.
     * @param page The page number to display.
     */
    protected CERRecipeGroup(@NotNull CERRecipeGroup cer, int page) {
        super(cer.key, new ItemStack(Material.BARRIER));
        this.page = page;
        this.player = cer.player;
        this.implementation = cer.implementation;
        this.recipes = cer.recipes;
        this.icons = cer.icons;
        this.machine = cer.machine;
        this.pageMap.put(page, this);
    }

    public static List<Pair<ItemStack, ChestMenu.MenuClickHandler>> getDisplayIcons(Player p, SlimefunItem machine, List<RecipeWrapper> wrappers) {
        List<Pair<ItemStack, ChestMenu.MenuClickHandler>> list = new ArrayList<>();
        for (int i = 0; i < wrappers.size(); i++) {
            RecipeWrapper recipe = wrappers.get(i);

            var in = recipe.getInput();
            var out = recipe.getOutput();
            var e = recipe.getTotalEnergyCost();
            list.add(new Pair<>(
                    PatchScope.CerRecipe.patch(p, Converter.getItem(
                            Material.GREEN_STAINED_GLASS_PANE,
                            "&a配方#" + (i + 1),
                            "&a机器制作难度: " + ValueTable.getValue(machine),
                            "&a耗时: " + recipe.getTicks(),
                            "&a" + (e == 0 ? "耗电: 无" : e > 0 ? "耗电: " + e : "产电: " + (-e))
                    )),
                    ChestMenuUtils.getEmptyClickHandler()
            ));

            if (in != null && in.length > 0) {
                list.add(new Pair<>(
                        PatchScope.CerRecipeBorderInput.patch(p, Converter.getItem(
                                Material.BLUE_STAINED_GLASS_PANE,
                                "&a输入 →"
                        )),
                        ChestMenuUtils.getEmptyClickHandler()
                ));

                for (ItemStack input : in) {
                    list.add(new Pair<>(
                            PatchScope.CerRecipeInput.patch(p, Converter.getItem(ItemStackUtil.getCleanItem(input))),
                            subMenuOpen
                    ));
                }

                if (out != null && out.length > 0) {
                    list.add(new Pair<>(
                            PatchScope.CerRecipeBorderInputOutput.patch(p, Converter.getItem(
                                    Material.ORANGE_STAINED_GLASS_PANE,
                                    "&a← 输入",
                                    "&6输出 →"
                            )),
                            ChestMenuUtils.getEmptyClickHandler()
                    ));
                }
            } else {
                if (out != null && out.length > 0) {
                    list.add(new Pair<>(
                            PatchScope.CerRecipeBorderOutput.patch(p, Converter.getItem(
                                    Material.ORANGE_STAINED_GLASS_PANE,
                                    "&6输出 →"
                            )),
                            ChestMenuUtils.getEmptyClickHandler()
                    ));
                }
            }

            if (out != null) {
                for (ItemStack output : out) {
                    ItemStack display = output.clone();
                    ItemMeta meta = display.getItemMeta();

                    List<String> lore = new ArrayList<>();
                    List<String> o = meta.getLore();
                    if (o != null) lore.addAll(o);

                    double cer = CERCalculator.getCER(machine, ItemStackHelper.getDisplayName(output));
                    lore.add(" ");
                    lore.add(ChatColors.color("&a性价比: " + format.format(cer)));
                    meta.setLore(lore);
                    display.setItemMeta(meta);
                    list.add(new Pair<>(
                            PatchScope.CerRecipeOutput.patch(p, display),
                            subMenuOpen
                    ));
                }
            }
        }

        return list;
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
        return false;
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
        ChestMenu chestMenu = new ChestMenu("&a性价比预览（仅供参考）");

        OnClick.preset(chestMenu);

        for (int ss : Formats.sub.getChars('b')) {
            chestMenu.addItem(ss, PatchScope.Back.patch(playerProfile, ChestMenuUtils.getBackButton(player)));
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

        for (int ss : Formats.sub.getChars('P')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.PreviousPage.patch(
                            playerProfile,
                            ChestMenuUtils.getPreviousButton(
                                    player,
                                    this.page,
                                    (iconsLength() - 1)
                                            / Formats.sub.getChars('i').size()
                                            + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.PreviousButtonClickEvent(p, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                        CERRecipeGroup CERRecipeGroup = this.getByPage(Math.max(this.page - 1, 1));
                        CERRecipeGroup.open(player, playerProfile, slimefunGuideMode);
                        return false;
                    }));
        }

        // Search feature!
        for (int ss : Formats.sub.getChars('S')) {
            chestMenu.addItem(ss, PatchScope.Search.patch(playerProfile, ChestMenuUtils.getSearchButton(player)));
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

        for (int ss : Formats.sub.getChars('N')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.NextPage.patch(
                            playerProfile,
                            ChestMenuUtils.getNextButton(
                                    player,
                                    this.page,
                                    (iconsLength() - 1)
                                            / Formats.sub.getChars('i').size()
                                            + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.NextButtonClickEvent(p, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                        CERRecipeGroup CERRecipeGroup = this.getByPage(Math.min(
                                this.page + 1,
                                (iconsLength() - 1)
                                        / Formats.sub.getChars('i').size()
                                        + 1));
                        CERRecipeGroup.open(player, playerProfile, slimefunGuideMode);
                        return false;
                    }));
        }

        for (int ss : Formats.sub.getChars('B')) {
            chestMenu.addItem(ss, PatchScope.Background.patch(playerProfile, ChestMenuUtils.getBackground()));
            chestMenu.addMenuClickHandler(ss, ChestMenuUtils.getEmptyClickHandler());
        }

        List<Integer> contentSlots = Formats.sub.getChars('i');
        for (int i = 0; i < contentSlots.size(); i++) {
            var m = (page - 1) * contentSlots.size() + i;
            if (m < iconsLength()) {
                chestMenu.addItem(contentSlots.get(i), icons.get(m).getFirst(), icons.get(m).getSecond());
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
     * Gets the CERRecipeGroup by page.
     *
     * @param page The page number.
     * @return The CERRecipeGroup by page.
     */
    @NotNull
    private CERRecipeGroup getByPage(int page) {
        if (this.pageMap.containsKey(page)) {
            return this.pageMap.get(page);
        } else {
            synchronized (this.pageMap.get(1)) {
                if (this.pageMap.containsKey(page)) {
                    return this.pageMap.get(page);
                }

                CERRecipeGroup cer = new CERRecipeGroup(this, page);
                cer.pageMap = this.pageMap;
                this.pageMap.put(page, cer);
                return cer;
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
        return Slimefun.getConfigManager().isShowHiddenItemGroupsInSearch()
                || slimefunItem.getItemGroup().isAccessible(p);
    }

    /**
     * Prints an error message to the player.
     *
     * @param p The player.
     * @param x The exception.
     */
    @ParametersAreNonnullByDefault
    private void printErrorMessage(@NotNull Player p, @NotNull Throwable x) {
        p.sendMessage("&4服务器发生了一个内部错误. 请联系管理员处理.");
        JAVA_PLUGIN.getLogger().log(Level.SEVERE, "在打开指南书里的 Slimefun 物品时发生了意外!", x);
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

    public int iconsLength() {
        return icons.size();
    }

    /**
     * @author balugaq
     * @since 1.9
     */
    @SuppressWarnings("ClassCanBeRecord")
    @Data
    public static class RecipeWrapper {
        private final @Nullable ItemStack[] input;
        private final ItemStack[] output;
        private final long ticks;
        private final long totalEnergyCost;
    }
}
