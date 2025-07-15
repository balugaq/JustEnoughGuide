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

package com.balugaq.jeg.implementation.items;

import com.balugaq.jeg.api.groups.ClassicGuideGroup;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.objects.enums.FilterType;
import com.balugaq.jeg.api.objects.exceptions.ArgumentMissingException;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.option.BeginnersGuideOption;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.Lang;
import com.balugaq.jeg.utils.SlimefunOfficialSupporter;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of the ClassicGuideGroup for JEG.
 *
 * @author balugaq
 * @since 1.3
 */
@Getter
@NotDisplayInCheatMode
public class JEGGuideGroup extends ClassicGuideGroup {
    public static final int[] GUIDE_SLOTS = Formats.helper.getChars('h').stream().mapToInt(i -> i).toArray();
    public static final int[] BORDER_SLOTS = Formats.helper.getChars('B').stream().mapToInt(i -> i).toArray();
    private static final ItemStack HEADER = Lang.getGuideGroupIcon("header", Material.BEACON);

    @SuppressWarnings("SameParameterValue")
    protected JEGGuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon) {
        super(key, icon, Integer.MAX_VALUE);
        for (int slot : BORDER_SLOTS) {
            addGuide(slot, ChestMenuUtils.getBackground());
        }
        boolean loaded = false;
        for (int s : Formats.helper.getChars('A')) {
            addGuide(s, HEADER);
            loaded = true;
        }

        if (!loaded) {
            // Well... the user removed my author information
            throw new ArgumentMissingException(
                    "You're not supposed to remove symbol 'A'... Which means Author Information. " + "format="
                            + Formats.helper);
        }

        final AtomicInteger index = new AtomicInteger(0);

        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-search-paging", Material.NAME_TAG),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search a");
                    } catch (Exception e) {
                        p.sendMessage("§cAn error occurred when clicked in JEGGuideGroup");
                        Debug.trace(e);
                    }
                    return false;
                });

        doIf(JustEnoughGuide.getConfigManager().isBookmark(), () -> {
            addGuide(
                    GUIDE_SLOTS[index.getAndIncrement()],
                    Lang.getGuideGroupIcon("feature-bookmark-item", Material.BOOK),
                    (p, s, i, a) -> {
                        try {
                            if (Slimefun.instance() == null) {
                                p.sendMessage("§cSlimefun disabled. (impossible!)");
                            }

                            SlimefunGuideImplementation guide = GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                            if (guide == null) {
                                p.sendMessage("§cNo SlimefunGuideImplementation found! (impossible!)");
                                return false;
                            }

                            if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                                p.sendMessage("§cFeature disabled.");
                                return false;
                            }

                            PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                            if (profile == null) {
                                p.sendMessage("§cNo PlayerProfile found!");
                                return false;
                            }

                            for (ItemGroup itemGroup : Slimefun.getRegistry().getAllItemGroups()) {
                                if (itemGroup
                                        .getKey()
                                        .equals(new NamespacedKey(Slimefun.instance(), "basic_machines"))) {
                                    jegGuide.openItemMarkGroup(itemGroup, p, profile);
                                    return false;
                                }
                            }
                        } catch (Throwable e) {
                            p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                            Debug.trace(e);
                        }
                        return false;
                    });
        });

        doIf(JustEnoughGuide.getConfigManager().isBookmark(), () -> {
            addGuide(
                    GUIDE_SLOTS[index.getAndIncrement()],
                    Lang.getGuideGroupIcon("feature-view-bookmarked-items", Material.NETHER_STAR), (p, s, i, a) -> {
                        try {
                            if (Slimefun.instance() == null) {
                                p.sendMessage("§cSlimefun disabled. (impossible!)");
                            }

                            SlimefunGuideImplementation guide = GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                            if (guide == null) {
                                p.sendMessage("§cNo SlimefunGuideImplementation found! (impossible!)");
                                return false;
                            }

                            if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                                p.sendMessage("§cFeature disabled.");
                                return false;
                            }

                            PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                            if (profile == null) {
                                p.sendMessage("§cNo PlayerProfile found!");
                                return false;
                            }

                            jegGuide.openBookMarkGroup(p, profile);
                        } catch (Throwable e) {
                            p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                            Debug.trace(e);
                        }
                        return false;
                    });
        });

        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-jump-category", Material.CRAFTING_TABLE),
                (p, s, i, a) -> {
                    try {
                        if (Slimefun.instance() == null) {
                            p.sendMessage("§cSlimefun disabled. (impossible!)");
                            return false;
                        }

                        SlimefunGuideImplementation guide = GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                        if (guide == null) {
                            p.sendMessage("§cNo SlimefunGuideImplementation found! (impossible!)");
                            return false;
                        }

                        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                        if (profile == null) {
                            p.sendMessage("§cNo PlayerProfile found!");
                            return false;
                        }

                        SlimefunItem exampleItem = SlimefunItems.ELECTRIC_DUST_WASHER_3.getItem();
                        if (exampleItem == null) {
                            p.sendMessage("§cExample item not found! (weird)");
                            return false;
                        }

                        if (exampleItem.isDisabledIn(p.getWorld())) {
                            p.sendMessage("§c该物品已被禁用，无法展示示例");
                            return false;
                        }

                        guide.displayItem(profile, exampleItem, true);
                    } catch (Exception e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        Debug.trace(e);
                    }
                    return false;
                });

        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-quick-search", Material.NAME_TAG), (p, s, i, a) -> {
                    try {
                        if (Slimefun.instance() == null) {
                            p.sendMessage("§cSlimefun disabled. (impossible!)");
                            return false;
                        }

                        SlimefunGuideImplementation guide = GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                        if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                            p.sendMessage("§cFeature disabled.");
                            return false;
                        }

                        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                        if (profile == null) {
                            p.sendMessage("§cNo PlayerProfile found!");
                            return false;
                        }

                        if (!BeginnersGuideOption.isEnabled(p)) {
                            p.sendMessage("§cYou should enable Beginners Guide in guide settings!");
                            return false;
                        }

                        SlimefunItem exampleItem = SlimefunItems.ELECTRIC_DUST_WASHER_3.getItem();
                        if (exampleItem == null) {
                            p.sendMessage("§cExample item not found! (weird)");
                            return false;
                        }

                        if (exampleItem.isDisabledIn(p.getWorld())) {
                            p.sendMessage("§c该物品已被禁用，无法展示示例");
                            return false;
                        }

                        jegGuide.displayItem(profile, exampleItem, true);
                    } catch (Exception e) {
                        p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                        Debug.trace(e);
                    }
                    return false;
                });

        doIf(SlimefunOfficialSupporter.isEnableResearching(), () -> {
            addGuide(
                    GUIDE_SLOTS[index.getAndIncrement()],
                    Lang.getGuideGroupIcon("feature-quick-research", Material.ENCHANTED_BOOK),
                    (p, s, i, a) -> {
                        try {
                            if (Slimefun.instance() == null) {
                                p.sendMessage("§cSlimefun disabled. (impossible!)");
                                return false;
                            }

                            SlimefunGuideImplementation guide = GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                            if (guide == null) {
                                p.sendMessage("§cNo SlimefunGuideImplementation found! (impossible!)");
                                return false;
                            }

                            if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                                p.sendMessage("§cFeature disabled.");
                                return false;
                            }

                            PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                            if (profile == null) {
                                p.sendMessage("§cNo PlayerProfile found!");
                                return false;
                            }

                            SlimefunItem exampleItem = SlimefunItems.ELECTRIC_DUST_WASHER_3.getItem();
                            if (exampleItem == null) {
                                p.sendMessage("§cExample item not found! (weird)");
                                return false;
                            }

                            if (exampleItem.isDisabledIn(p.getWorld())) {
                                p.sendMessage("§c该物品已被禁用，无法展示示例");
                                return false;
                            }

                            jegGuide.displayItem(profile, exampleItem, true);
                        } catch (Exception e) {
                            p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                            Debug.trace(e);
                        }
                        return false;
                    });
        });

        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-smart-search", Material.COMPARATOR), (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search sulfate");
                    } catch (Exception e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        Debug.trace(e);
                    }
                    return false;
                });

        String flag_recipe_item_name = FilterType.BY_RECIPE_ITEM_NAME.getSymbol();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-search-expansion-by-recipe-item-name", Material.LODESTONE, "flag", flag_recipe_item_name),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_recipe_item_name + "battery");
                    } catch (Exception e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        Debug.trace(e);
                    }
                    return false;
                });

        String flag_recipe_type_name = FilterType.BY_RECIPE_TYPE_NAME.getSymbol();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-search-expansion-by-recipe-type-name", Material.LODESTONE, "flag", flag_recipe_type_name),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_recipe_type_name + "crafting table");
                    } catch (Exception e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        Debug.trace(e);
                    }
                    return false;
                });

        String flag_display_item_name = FilterType.BY_DISPLAY_ITEM_NAME.getSymbol();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-search-expansion-by-display-item-name", Material.LODESTONE, "flag", flag_display_item_name),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_display_item_name + "copper.dust");
                    } catch (Exception e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        Debug.trace(e);
                    }
                    return false;
                });

        String flag_addon_name = FilterType.BY_ADDON_NAME.getSymbol();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-search-expansion-by-addon-name", Material.LODESTONE, "flag", flag_addon_name),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_addon_name + "Slimefun");
                    } catch (Throwable e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        Debug.trace(e);
                    }
                    return false;
                });

        String flag_item_name = FilterType.BY_ITEM_NAME.getSymbol();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-search-expansion-by-item-name", Material.LODESTONE, "flag", flag_item_name),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_item_name + "Battery");
                    } catch (Exception e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        Debug.trace(e);
                    }
                    return false;
                });

        String flag_material_name = FilterType.BY_MATERIAL_NAME.getSymbol();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-search-expansion-by-material-name", Material.LODESTONE, "flag", flag_material_name),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_material_name + "iron");
                    } catch (Exception e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        Debug.trace(e);
                    }
                    return false;
                });

        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Converter.getItem(
                        Material.STONE_PICKAXE, "&b功能: 名称打印", "&b介绍: 你可以在任意物品上按 Q 键，以将此物品分享给其他玩家", "&b点击尝试功能"),
                (p, s, i, a) -> {
                    try {
                        if (Slimefun.instance() == null) {
                            p.sendMessage("§c无法获取 Slimefun 实例，无法使用此功能。");
                            return false;
                        }

                        SlimefunGuideImplementation guide = GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                        if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                            p.sendMessage("§c功能未启用，无法使用此功能。");
                            return false;
                        }

                        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                        if (profile == null) {
                            p.sendMessage("§c无法获取玩家资料，请检查是否正确安装 Slimefun。");
                            return false;
                        }

                        if (!BeginnersGuideOption.isEnabled(p)) {
                            p.sendMessage("§c此功能需要您在设置中启用新手指引。");
                            return false;
                        }

                        SlimefunItem exampleItem = SlimefunItems.ELECTRIC_DUST_WASHER_3.getItem();
                        if (exampleItem == null) {
                            p.sendMessage("§c无法获取示例物品，请检查是否正确安装 Slimefun。");
                            return false;
                        }

                        if (exampleItem.isDisabledIn(p.getWorld())) {
                            p.sendMessage("§c该物品已被禁用，无法展示示例");
                            return false;
                        }

                        jegGuide.displayItem(profile, exampleItem, true);
                    } catch (Exception e) {
                        p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                        Debug.trace(e);
                    }
                    return false;
                });

        Formats.helper.renderCustom(this);
    }

    public static void doIf(boolean expression, @NotNull Runnable runnable) {
        if (expression) {
            try {
                runnable.run();
            } catch (Exception e) {
                Debug.trace(e, "loading guide group");
            }
        }
    }
}
