/*
 * Copyright (c) 2024-2026 balugaq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.cost.please_set_cer_patch_to_false_in_config_when_you_see_this.CERCalculator;
import com.balugaq.jeg.api.cost.please_set_cer_patch_to_false_in_config_when_you_see_this.ValueTable;
import com.balugaq.jeg.api.objects.collection.Pair;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.formatter.Format;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
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
@NullMarked
public class CERRecipeGroup extends BaseGroup<CERRecipeGroup> {
    public static final DecimalFormat FORMAT = new DecimalFormat("#.###");
    public static final ChestMenu.MenuClickHandler subMenuOpen = (p, s, i, a) -> {
        // todo
        return false;
    };
    private final List<Pair<ItemStack, ChestMenu.MenuClickHandler>> icons;

    public CERRecipeGroup(
        final Player player,
        final SlimefunItem machine,
        final List<RecipeWrapper> recipes) {
        super();
        this.page = 1;
        this.pageMap.put(1, this);
        this.icons = getDisplayIcons(player, machine, recipes.stream().limit(64).toList());
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
                                    Converter.getItem(input)
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

        Format format = Formats.sub;
        int maxPage = (iconsLength() - 1) / format.getChars('i').size() + 1;
        GuideUtil.commonRender(chestMenu, format, playerProfile, player, this, this.page, maxPage);

        List<Integer> contentSlots = Formats.sub.getChars('i');
        for (int i = 0; i < contentSlots.size(); i++) {
            var m = (page - 1) * contentSlots.size() + i;
            if (m < iconsLength()) {
                chestMenu.addItem(contentSlots.get(i), icons.get(m).getFirst(), icons.get(m).getSecond());
            }
        }

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
