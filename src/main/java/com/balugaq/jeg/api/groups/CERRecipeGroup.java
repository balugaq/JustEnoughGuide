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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"deprecation", "unused"})
@NotDisplayInSurvivalMode
@NotDisplayInCheatMode
@NullMarked
public class CERRecipeGroup extends BaseGroup<CERRecipeGroup> {
    public static final DecimalFormat FORMAT = new DecimalFormat("#.###");
    public static final ChestMenu.MenuClickHandler subMenuOpen = (p, s, i, a) -> {
        // todo
        return false;
    };
    private final SlimefunGuideImplementation implementation;
    private final List<Pair<ItemStack, ChestMenu.MenuClickHandler>> icons;

    public CERRecipeGroup(
            final SlimefunGuideImplementation implementation,
            final Player player,
            final SlimefunItem machine,
            final List<RecipeWrapper> recipes) {
        super();
        this.page = 1;
        this.implementation = implementation;
        this.pageMap.put(1, this);
        this.icons = getDisplayIcons(player, machine, recipes);
    }

    public static List<Pair<ItemStack, ChestMenu.MenuClickHandler>> getDisplayIcons(Player p, SlimefunItem machine,
                                                                                    List<RecipeWrapper> wrappers) {
        try {
            List<Pair<ItemStack, ChestMenu.MenuClickHandler>> list = new ArrayList<>();
            for (int i = 0; i < wrappers.size(); i++) {
                RecipeWrapper recipe = wrappers.get(i);

                @Nullable ItemStack @Nullable [] in = recipe.getInput();
                @Nullable ItemStack @Nullable [] out = recipe.getOutput();
                long e = recipe.getTotalEnergyCost();
                list.add(new Pair<>(
                        PatchScope.CerRecipe.patch(
                                p, Converter.getItem(
                                        Material.GREEN_STAINED_GLASS_PANE,
                                        "&a配方#" + (i + 1),
                                        "&a机器制作难度: " + ValueTable.getValue(machine),
                                        "&a耗时: " + recipe.getTicks(),
                                        "&a" + (e == 0 ? "耗电: 无" : e > 0 ? "耗电: " + e : "产电: " + (-e))
                                )
                        ),
                        ChestMenuUtils.getEmptyClickHandler()
                ));

                if (in != null && in.length > 0) {
                    list.add(new Pair<>(
                            PatchScope.CerRecipeBorderInput.patch(
                                    p, Converter.getItem(
                                            Material.BLUE_STAINED_GLASS_PANE,
                                            "&a输入 →"
                                    )
                            ),
                            ChestMenuUtils.getEmptyClickHandler()
                    ));

                    for (ItemStack input : in) {
                        if (input != null) {
                            list.add(new Pair<>(
                                    PatchScope.CerRecipeInput.patch(
                                            p,
                                            Converter.getItem(ItemStackUtil.getCleanItem(input))
                                    ),
                                    subMenuOpen
                            ));
                        }
                    }

                    if (out != null && out.length > 0) {
                        list.add(new Pair<>(
                                PatchScope.CerRecipeBorderInputOutput.patch(
                                        p, Converter.getItem(
                                                Material.ORANGE_STAINED_GLASS_PANE,
                                                "&a← 输入",
                                                "&6输出 →"
                                        )
                                ),
                                ChestMenuUtils.getEmptyClickHandler()
                        ));
                    }
                } else {
                    if (out != null && out.length > 0) {
                        list.add(new Pair<>(
                                PatchScope.CerRecipeBorderOutput.patch(
                                        p, Converter.getItem(
                                                Material.ORANGE_STAINED_GLASS_PANE,
                                                "&6输出 →"
                                        )
                                ),
                                ChestMenuUtils.getEmptyClickHandler()
                        ));
                    }
                }

                if (out != null) {
                    for (ItemStack output : out) {
                        if (output != null) {
                            ItemStack display = output.clone();
                            ItemMeta meta = display.getItemMeta();

                            List<String> lore = new ArrayList<>();
                            List<String> o = meta.getLore();
                            if (o != null) lore.addAll(o);

                            double cer = CERCalculator.getCER(machine, ItemStackHelper.getDisplayName(output));
                            lore.add(" ");
                            lore.add(ChatColors.color("&a性价比: " + FORMAT.format(cer)));
                            meta.setLore(lore);
                            display.setItemMeta(meta);
                            list.add(new Pair<>(
                                    PatchScope.CerRecipeOutput.patch(p, display),
                                    subMenuOpen
                            ));
                        }
                    }
                }
            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean isVisible(
            final Player player,
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode) {
        return false;
    }

    @Override
    public ChestMenu generateMenu(
            final Player player,
            final PlayerProfile playerProfile,
            final SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu("&a性价比预览（仅供参考）");

        OnClick.preset(chestMenu);

        for (int ss : Formats.sub.getChars('b')) {
            chestMenu.addItem(ss, PatchScope.Back.patch(playerProfile, ChestMenuUtils.getBackButton(player)));
            chestMenu.addMenuClickHandler(
                    ss, (pl, s, is, action) -> EventUtil.callEvent(
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
                            })
            );
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
                                CERRecipeGroup CERRecipeGroup = this.getByPage(Math.max(this.page - 1, 1));
                                CERRecipeGroup.open(player, playerProfile, slimefunGuideMode);
                                return false;
                            })
            );
        }

        for (int ss : Formats.sub.getChars('S')) {
            chestMenu.addItem(ss, PatchScope.Search.patch(playerProfile, ChestMenuUtils.getSearchButton(player)));
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
                                        JustEnoughGuide.getInstance(),
                                        pl,
                                        msg -> implementation.openSearch(
                                                playerProfile,
                                                msg,
                                                implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE
                                        )
                                );

                                return false;
                            })
            );
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
                                CERRecipeGroup CERRecipeGroup = this.getByPage(Math.min(
                                        this.page + 1,
                                        (iconsLength() - 1)
                                                / Formats.sub.getChars('i').size()
                                                + 1
                                ));
                                CERRecipeGroup.open(player, playerProfile, slimefunGuideMode);
                                return false;
                            })
            );
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

    public int iconsLength() {
        return icons.size();
    }

    /**
     * @author balugaq
     * @since 1.9
     */
    @SuppressWarnings("ClassCanBeRecord")
    @Data
    @NullMarked
    public static class RecipeWrapper {
        private final @Nullable ItemStack @Nullable [] input;
        private final @Nullable ItemStack @Nullable [] output;
        private final long ticks;
        private final long totalEnergyCost;
    }
}
