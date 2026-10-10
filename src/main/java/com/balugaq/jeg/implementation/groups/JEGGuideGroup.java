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

package com.balugaq.jeg.implementation.groups;

import com.balugaq.jeg.api.groups.ClassicGuideGroup;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.objects.enums.FilterType;
import com.balugaq.jeg.api.objects.exceptions.ArgumentMissingException;
import com.balugaq.jeg.core.lang.Lang;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.option.BeginnersGuideOption;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An implementation of the ClassicGuideGroup for JEG.
 *
 * @author balugaq
 * @since 1.3
 */
@Getter
@NotDisplayInCheatMode
@NullMarked
public class JEGGuideGroup extends ClassicGuideGroup {
    public static final int[] GUIDE_SLOTS =
        Formats.helper.getChars('h').stream().mapToInt(i -> i).toArray();

    public static final int[] BORDER_SLOTS =
        Formats.helper.getChars('B').stream().mapToInt(i -> i).toArray();

    /**
     * 从语言文件构建指南书功能条目图标（{@code guidebook.features.<key>} 节，name + lore）。
     */
    private static ItemStack icon(Material material, String key, Object... args) {
        return Converter.getItem(material, Lang.t("guidebook.features." + key + ".name"),
            Lang.lines("guidebook.features." + key + ".lore", args).toArray(String[]::new));
    }

    @SuppressWarnings("SameParameterValue")
    protected JEGGuideGroup(NamespacedKey key, ItemStack icon) {
        super(key, icon, Integer.MAX_VALUE);
        for (int slot : BORDER_SLOTS) {
            addGuide(slot, ChestMenuUtils.getBackground());
        }
        ItemStack header = icon(Material.BEACON, "header");
        boolean loaded = false;
        for (int s : Formats.helper.getChars('A')) {
            addGuide(s, header);
            loaded = true;
        }

        if (!loaded) {
            // Well... the user removed my author information
            throw new ArgumentMissingException(
                "You're not supposed to remove symbol 'A'... Which means Author Information. " + "format="
                    + Formats.helper);
        }

        final AtomicInteger index = new AtomicInteger(0);
        doIf(
            JustEnoughGuide.getConfigManager().isPinyinSearch(),
            () -> addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                icon(Material.CLOCK, "pinyin"),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search ding");
                    } catch (Exception e) {
                        Lang.sendMessage(p, "guidebook.no-slimefun");
                        Debug.trace(e);
                    }
                    return false;
                }
            )
        );

        addGuide(
            GUIDE_SLOTS[index.getAndIncrement()],
            icon(Material.NAME_TAG, "search-page"),
            (p, s, i, a) -> {
                try {
                    p.performCommand("sf search a");
                } catch (Exception e) {
                    Lang.sendMessage(p, "guidebook.no-slimefun");
                    Debug.trace(e);
                }
                return false;
            }
        );

        doIf(
            JustEnoughGuide.getConfigManager().isBookmark(),
            () -> addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                icon(
                    Material.BOOK,
                    "bookmark"
                ),
                (p, s, i, a) -> {
                    try {
                        if (Slimefun.instance() == null) {
                            Lang.sendMessage(p, "guidebook.no-slimefun-instance");
                        }

                        SlimefunGuideImplementation guide =
                            GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);

                        if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                            Lang.sendMessage(p, "guidebook.feature-disabled");
                            return false;
                        }

                        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                        if (profile == null) {
                            Lang.sendMessage(p, "guidebook.no-profile");
                            return false;
                        }

                        for (ItemGroup itemGroup :
                            new ArrayList<>(Slimefun.getRegistry().getAllItemGroups())) {
                            if (itemGroup
                                .getKey()
                                .equals(new NamespacedKey(Slimefun.instance(), "basic_machines"))) {
                                jegGuide.openItemMarkGroup(itemGroup, p, profile);
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        Lang.sendMessage(p, "guidebook.no-slimefun");
                        Debug.trace(e);
                    }
                    return false;
                }
            )
        );

        doIf(
            JustEnoughGuide.getConfigManager().isBookmark(),
            () -> addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                icon(
                    Material.NETHER_STAR,
                    "bookmark-view"
                ),
                (p, s, i, a) -> {
                    try {
                        if (Slimefun.instance() == null) {
                            Lang.sendMessage(p, "guidebook.no-slimefun-instance");
                        }

                        SlimefunGuideImplementation guide =
                            GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                        if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                            Lang.sendMessage(p, "guidebook.feature-disabled");
                            return false;
                        }

                        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                        if (profile == null) {
                            Lang.sendMessage(p, "guidebook.no-profile");
                            return false;
                        }

                        jegGuide.openBookMarkGroup(p, profile);
                    } catch (Exception e) {
                        Lang.sendMessage(p, "guidebook.no-slimefun");
                        Debug.trace(e);
                    }
                    return false;
                }
            )
        );

        addGuide(
            GUIDE_SLOTS[index.getAndIncrement()],
            icon(
                Material.CRAFTING_TABLE,
                "jump-group"
            ),
            (p, s, i, a) -> {
                try {
                    if (Slimefun.instance() == null) {
                        Lang.sendMessage(p, "guidebook.no-slimefun-instance");
                        return false;
                    }

                    SlimefunGuideImplementation guide = GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                    if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                        Lang.sendMessage(p, "guidebook.feature-disabled");
                        return false;
                    }

                    PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                    if (profile == null) {
                        Lang.sendMessage(p, "guidebook.no-profile");
                        return false;
                    }

                    SlimefunItem exampleItem = SlimefunItems.ELECTRIC_DUST_WASHER_3.getItem();
                    if (exampleItem == null) {
                        Lang.sendMessage(p, "guidebook.no-example-item");
                        return false;
                    }

                    if (exampleItem.isDisabledIn(p.getWorld())) {
                        Lang.sendMessage(p, "guidebook.item-disabled");
                        return false;
                    }

                    jegGuide.displayItem(profile, exampleItem, true);
                } catch (Exception e) {
                    Lang.sendMessage(p, "guidebook.no-slimefun");
                    Debug.trace(e);
                }
                return false;
            }
        );

        addGuide(
            GUIDE_SLOTS[index.getAndIncrement()],
            icon(
                Material.NAME_TAG,
                "quick-search"
            ),
            (p, s, i, a) -> {
                try {
                    if (Slimefun.instance() == null) {
                        Lang.sendMessage(p, "guidebook.no-slimefun-instance");
                        return false;
                    }

                    SlimefunGuideImplementation guide = GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                    if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                        Lang.sendMessage(p, "guidebook.feature-disabled");
                        return false;
                    }

                    PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                    if (profile == null) {
                        Lang.sendMessage(p, "guidebook.no-profile");
                        return false;
                    }

                    if (!BeginnersGuideOption.instance().isEnabled(p)) {
                        Lang.sendMessage(p, "guidebook.need-beginner");
                        return false;
                    }

                    SlimefunItem exampleItem = SlimefunItems.ELECTRIC_DUST_WASHER_3.getItem();
                    if (exampleItem == null) {
                        Lang.sendMessage(p, "guidebook.no-example-item");
                        return false;
                    }

                    if (exampleItem.isDisabledIn(p.getWorld())) {
                        Lang.sendMessage(p, "guidebook.item-disabled");
                        return false;
                    }

                    jegGuide.displayItem(profile, exampleItem, true);
                } catch (Exception e) {
                    Lang.sendMessage(p, "guidebook.no-slimefun");
                    Debug.trace(e);
                }
                return false;
            }
        );

        doIf(
            Slimefun.getConfigManager().isResearchingEnabled(),
            () -> addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                icon(
                    Material.ENCHANTED_BOOK,
                    "portable-research"
                ),
                (p, s, i, a) -> {
                    try {
                        if (Slimefun.instance() == null) {
                            Lang.sendMessage(p, "guidebook.no-slimefun-instance");
                            return false;
                        }

                        SlimefunGuideImplementation guide =
                            GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                        if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                            Lang.sendMessage(p, "guidebook.feature-disabled");
                            return false;
                        }

                        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                        if (profile == null) {
                            Lang.sendMessage(p, "guidebook.no-profile");
                            return false;
                        }

                        SlimefunItem exampleItem = SlimefunItems.ELECTRIC_DUST_WASHER_3.getItem();
                        if (exampleItem == null) {
                            Lang.sendMessage(p, "guidebook.no-example-item");
                            return false;
                        }

                        if (exampleItem.isDisabledIn(p.getWorld())) {
                            Lang.sendMessage(p, "guidebook.item-disabled");
                            return false;
                        }

                        jegGuide.displayItem(profile, exampleItem, true);
                    } catch (Exception e) {
                        Lang.sendMessage(p, "guidebook.no-slimefun");
                        Debug.trace(e);
                    }
                    return false;
                }
            )
        );

        addGuide(
            GUIDE_SLOTS[index.getAndIncrement()],
            icon(
                Material.COMPARATOR,
                "smart-search"
            ),
            (p, s, i, a) -> {
                try {
                    p.performCommand("sf search 硫酸盐");
                } catch (Exception e) {
                    Lang.sendMessage(p, "guidebook.no-slimefun");
                    Debug.trace(e);
                }
                return false;
            }
        );

        String flag_recipe_item_name = FilterType.BY_RECIPE_ITEM_NAME.getFirstSymbol();
        addGuide(
            GUIDE_SLOTS[index.getAndIncrement()],
            icon(
                Material.LODESTONE,
                "filter-recipe-item",
                flag_recipe_item_name,
                FilterType.BY_RECIPE_ITEM_NAME.apply("电池")
            ),
            (p, s, i, a) -> {
                try {
                    p.performCommand("sf search " + FilterType.BY_RECIPE_ITEM_NAME.apply("电池"));
                } catch (Exception e) {
                    Lang.sendMessage(p, "guidebook.no-slimefun");
                    Debug.trace(e);
                }
                return false;
            }
        );

        String flag_recipe_type_name = FilterType.BY_RECIPE_TYPE_NAME.getFirstSymbol();
        addGuide(
            GUIDE_SLOTS[index.getAndIncrement()],
            icon(
                Material.LODESTONE,
                "filter-recipe-type",
                flag_recipe_type_name,
                FilterType.BY_RECIPE_TYPE_NAME.apply("工作台")
            ),
            (p, s, i, a) -> {
                try {
                    p.performCommand("sf search " + FilterType.BY_RECIPE_TYPE_NAME.apply("工作台"));
                } catch (Exception e) {
                    Lang.sendMessage(p, "guidebook.no-slimefun");
                    Debug.trace(e);
                }
                return false;
            }
        );

        String flag_display_item_name = FilterType.BY_DISPLAY_ITEM_NAME.getFirstSymbol();
        addGuide(
            GUIDE_SLOTS[index.getAndIncrement()],
            icon(
                Material.LODESTONE,
                "filter-display-item",
                flag_display_item_name,
                FilterType.BY_DISPLAY_ITEM_NAME.apply("铜粉")
            ),
            (p, s, i, a) -> {
                try {
                    p.performCommand("sf search " + FilterType.BY_DISPLAY_ITEM_NAME.apply("铜粉"));
                } catch (Exception e) {
                    Lang.sendMessage(p, "guidebook.no-slimefun");
                    Debug.trace(e);
                }
                return false;
            }
        );

        String flag_addon_name = FilterType.BY_ADDON_NAME.getFirstSymbol();
        addGuide(
            GUIDE_SLOTS[index.getAndIncrement()],
            icon(
                Material.LODESTONE,
                "filter-addon",
                flag_addon_name,
                FilterType.BY_ADDON_NAME.apply("粘液科技")
            ),
            (p, s, i, a) -> {
                try {
                    p.performCommand("sf search " + FilterType.BY_ADDON_NAME.apply("粘液科技"));
                } catch (Exception e) {
                    Lang.sendMessage(p, "guidebook.no-slimefun");
                    Debug.trace(e);
                }
                return false;
            }
        );

        String flag_item_name = FilterType.BY_ITEM_NAME.getFirstSymbol();
        addGuide(
            GUIDE_SLOTS[index.getAndIncrement()],
            icon(
                Material.LODESTONE,
                "filter-item-name",
                flag_item_name,
                FilterType.BY_ITEM_NAME.apply("电池")
            ),
            (p, s, i, a) -> {
                try {
                    p.performCommand("sf search " + FilterType.BY_ITEM_NAME.apply("电池"));
                } catch (Exception e) {
                    Lang.sendMessage(p, "guidebook.no-slimefun");
                    Debug.trace(e);
                }
                return false;
            }
        );

        String flag_item_lore = FilterType.BY_ITEM_LORE.getFirstSymbol();
        addGuide(
            GUIDE_SLOTS[index.getAndIncrement()],
            icon(
                Material.LODESTONE,
                "filter-item-lore",
                flag_item_lore,
                FilterType.BY_ITEM_LORE.apply("胡萝卜")
            ),
            (p, s, i, a) -> {
                try {
                    p.performCommand("sf search " + FilterType.BY_ITEM_LORE.apply("胡萝卜"));
                } catch (Exception e) {
                    Lang.sendMessage(p, "guidebook.no-slimefun");
                    Debug.trace(e);
                }
                return false;
            }
        );

        String flag_material_name = FilterType.BY_MATERIAL_NAME.getFirstSymbol();
        addGuide(
            GUIDE_SLOTS[index.getAndIncrement()],
            icon(
                Material.LODESTONE,
                "filter-material",
                flag_material_name,
                FilterType.BY_MATERIAL_NAME.apply("iron")
            ),
            (p, s, i, a) -> {
                try {
                    p.performCommand("sf search " + FilterType.BY_MATERIAL_NAME.apply("iron"));
                } catch (Exception e) {
                    Lang.sendMessage(p, "guidebook.no-slimefun");
                    Debug.trace(e);
                }
                return false;
            }
        );

        String flag_full_name = FilterType.BY_FULL_NAME.getFirstSymbol();
        addGuide(
            GUIDE_SLOTS[index.getAndIncrement()],
            icon(
                Material.LODESTONE,
                "filter-full-name",
                flag_full_name,
                FilterType.BY_MATERIAL_NAME.apply("铝锭")
            ),
            (p, s, i, a) -> {
                try {
                    p.performCommand("sf search " + FilterType.BY_MATERIAL_NAME.apply("铝锭"));
                } catch (Exception e) {
                    Lang.sendMessage(p, "guidebook.no-slimefun");
                    Debug.trace(e);
                }
                return false;
            }
        );

        addGuide(
            GUIDE_SLOTS[index.getAndIncrement()],
            icon(
                Material.STONE_PICKAXE, "name-print"),
            (p, s, i, a) -> {
                try {
                    if (Slimefun.instance() == null) {
                        Lang.sendMessage(p, "guidebook.no-slimefun-instance");
                        return false;
                    }

                    SlimefunGuideImplementation guide = GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                    if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                        Lang.sendMessage(p, "guidebook.feature-disabled");
                        return false;
                    }

                    PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                    if (profile == null) {
                        Lang.sendMessage(p, "guidebook.no-profile");
                        return false;
                    }

                    if (!BeginnersGuideOption.instance().isEnabled(p)) {
                        Lang.sendMessage(p, "guidebook.need-beginner");
                        return false;
                    }

                    SlimefunItem exampleItem = SlimefunItems.ELECTRIC_DUST_WASHER_3.getItem();
                    if (exampleItem == null) {
                        Lang.sendMessage(p, "guidebook.no-example-item");
                        return false;
                    }

                    if (exampleItem.isDisabledIn(p.getWorld())) {
                        Lang.sendMessage(p, "guidebook.item-disabled");
                        return false;
                    }

                    jegGuide.displayItem(profile, exampleItem, true);
                } catch (Exception e) {
                    Lang.sendMessage(p, "guidebook.no-slimefun");
                    Debug.trace(e);
                }
                return false;
            }
        );

        Formats.helper.renderCustom(this);
    }

    public static void doIf(boolean expression, Runnable runnable) {
        if (expression) {
            try {
                runnable.run();
            } catch (Exception e) {
                Debug.trace(e, "loading guide group");
            }
        }
    }
}
