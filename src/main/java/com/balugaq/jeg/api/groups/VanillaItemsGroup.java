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
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.balugaq.jeg.utils.clickhandler.OnDisplay;
import com.balugaq.jeg.utils.formatter.Formats;
import com.google.common.base.Preconditions;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * This class used to create groups to display all the vanilla items in the guide. Display for JEG recipe complete in
 * NetworksExpansion / SlimeAEPlugin
 *
 * @author balugaq
 * @since 1.7
 */
@SuppressWarnings({"deprecation", "unused", "ConstantValue"})
@NotDisplayInCheatMode
@NullMarked
public class VanillaItemsGroup extends BaseGroup<VanillaItemsGroup> {
    public static final List<SlimefunItem> slimefunItems = new ArrayList<>();

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
                }, 1L
        );
    }

    public VanillaItemsGroup(NamespacedKey key, ItemStack icon) {
        super(key, icon, Integer.MAX_VALUE);
        this.page = 1;
        this.pageMap.put(1, this);
    }

    private static VanillaItem createSlimefunItem(Material material) {
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
     * @param player
     *         The player who opened the group.
     * @param playerProfile
     *         The player's profile.
     * @param slimefunGuideMode
     *         The Slimefun guide mode.
     *
     * @return false.
     */
    @Override
    public boolean isVisible(
            final Player player,
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode) {
        return true;
    }

    /**
     * Opens the group for the player.
     *
     * @param player
     *         The player who opened the group.
     * @param playerProfile
     *         The player's profile.
     * @param slimefunGuideMode
     *         The Slimefun guide mode.
     */
    @Override
    public void open(
            final Player player,
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode) {
        playerProfile.getGuideHistory().add(this, this.page);
        this.generateMenu(player, playerProfile, slimefunGuideMode).open(player);
    }

    @Override
    public ChestMenu generateMenu(
            final Player player,
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu("原版物品");

        OnClick.preset(chestMenu);
        SlimefunGuideImplementation implementation = Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode);

        for (int ss : Formats.sub.getChars('b')) {
            chestMenu.addItem(ss, PatchScope.Back.patch(player, ChestMenuUtils.getBackButton(player)));
            chestMenu.addMenuClickHandler(
                    ss, (pl, s, is, action) -> EventUtil.callEvent(
                                    new GuideEvents.BackButtonClickEvent(pl, is, s, action, chestMenu, implementation))
                            .ifSuccess(() -> {
                                GuideHistory guideHistory = playerProfile.getGuideHistory();
                                if (action.isShiftClicked()) {
                                    SlimefunGuide.openMainMenu(
                                            playerProfile, slimefunGuideMode, guideHistory.getMainMenuPage());
                                } else {
                                    GuideUtil.goBack(guideHistory);
                                }
                                return false;
                            })
            );
        }

        for (int ss : Formats.sub.getChars('S')) {
            chestMenu.addItem(ss, PatchScope.Search.patch(player, ChestMenuUtils.getSearchButton(player)));
            chestMenu.addMenuClickHandler(
                    ss, (pl, slot, item, action) -> EventUtil.callEvent(
                                    new GuideEvents.SearchButtonClickEvent(
                                            pl, item, slot, action, chestMenu,
                                            implementation
                                    ))
                            .ifSuccess(() -> {
                                pl.closeInventory();

                                Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                                ChatInput.waitForPlayer(
                                        JAVA_PLUGIN,
                                        pl,
                                        msg -> implementation.openSearch(
                                                playerProfile,
                                                msg,
                                                true
                                        )
                                );

                                return false;
                            })
            );
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
                                            + 1
                            )
                    )
            );
            chestMenu.addMenuClickHandler(
                    ss, (p, slot, item, action) -> EventUtil.callEvent(
                                    new GuideEvents.PreviousButtonClickEvent(
                                            p, item, slot, action, chestMenu,
                                            implementation
                                    ))
                            .ifSuccess(() -> {
                                GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                                VanillaItemsGroup hiddenItemsGroup = this.getByPage(Math.max(this.page - 1, 1));
                                hiddenItemsGroup.open(player, playerProfile, slimefunGuideMode);
                                return false;
                            })
            );
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
                                            + 1
                            )
                    )
            );
            chestMenu.addMenuClickHandler(
                    ss, (p, slot, item, action) -> EventUtil.callEvent(
                                    new GuideEvents.NextButtonClickEvent(
                                            p, item, slot, action, chestMenu,
                                            implementation
                                    ))
                            .ifSuccess(() -> {
                                GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                                VanillaItemsGroup hiddenItemsGroup = this.getByPage(Math.min(
                                        this.page + 1,
                                        (slimefunItems.size() - 1)
                                                / Formats.sub.getChars('i').size()
                                                + 1
                                ));
                                hiddenItemsGroup.open(player, playerProfile, slimefunGuideMode);
                                return false;
                            })
            );
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
                OnDisplay.Item.display(player, slimefunItem, OnDisplay.Item.Normal, implementation)
                        .at(chestMenu, contentSlots.get(i), page);
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
     * Reopens the menu for the player.
     *
     * @param player
     *         The player who opened the group.
     * @param playerProfile
     *         The player's profile.
     * @param slimefunGuideMode
     *         The Slimefun guide mode.
     */
    public void refresh(
            final Player player,
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode) {
        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
        this.open(player, playerProfile, slimefunGuideMode);
    }

    @Override
    public int getTier() {
        return Integer.MAX_VALUE;
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class VanillaItem extends SlimefunItem implements VanillaItemShade {
        private final ItemStack customIcon;

        public VanillaItem(SlimefunItemStack item, ItemStack customIcon) {
            super(GroupSetup.vanillaItemsGroup, item, RecipeType.NULL, new ItemStack[0], customIcon);
            this.customIcon = customIcon.clone();
        }

        public static VanillaItem create(Material material) {
            ItemStack icon = new ItemStack(material);
            try {
                // against ID machine
                return new VanillaItem(new SlimefunItemStack("αJEG_VANILLA_" + material.name(), icon.clone()), icon);
            } catch (Exception ignored) {
                return new VanillaItem(new SlimefunItemStack("JEG_VANILLA_" + material.name(), icon.clone()), icon);
            }
        }
    }
}
