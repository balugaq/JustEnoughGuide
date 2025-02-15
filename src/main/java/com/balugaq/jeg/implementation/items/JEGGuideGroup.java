package com.balugaq.jeg.implementation.items;

import com.balugaq.jeg.api.groups.ClassicGuideGroup;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.objects.enums.FilterType;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.Lang;
import com.balugaq.jeg.utils.SlimefunOfficialSupporter;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * An implementation of the ClassicGuideGroup for JEG.
 *
 * @author balugaq
 * @since 1.3
 */
@Getter
@NotDisplayInCheatMode
public class JEGGuideGroup extends ClassicGuideGroup {
    private static final ItemStack HEADER = Lang.getGuideGroupIcon("header", Material.BEACON);
    private static final int[] GUIDE_SLOTS = {
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private static final int[] BORDER_SLOTS = {
            9, 17,
            18, 26,
            27, 35,
            36, 44,
    };

    protected JEGGuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon) {
        super(key, icon, Integer.MAX_VALUE);
        for (int slot : BORDER_SLOTS) {
            addGuide(slot, ChestMenuUtils.getBackground());
        }
        addGuide(13, HEADER);
        final AtomicInteger index = new AtomicInteger(0);

        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-search-paging", Material.NAME_TAG),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search a");
                    } catch (Throwable e) {
                        p.sendMessage("§cAn error occurred when clicked in JEGGuideGroup");
                        e.printStackTrace();
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
                            e.printStackTrace();
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
                            e.printStackTrace();
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

                        jegGuide.displayItem(profile, exampleItem, true);
                    } catch (Throwable e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        e.printStackTrace();
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

                            jegGuide.displayItem(profile, exampleItem, true);
                        } catch (Throwable e) {
                            p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                            e.printStackTrace();
                        }
                        return false;
                    });
        });

        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-smart-search", Material.COMPARATOR), (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search sulfate");
                    } catch (Throwable e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        e.printStackTrace();
                    }
                    return false;
                });

        String flag_recipe_item_name = FilterType.BY_RECIPE_ITEM_NAME.getFlag();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-search-expansion-by-recipe-item-name", Material.LODESTONE, "flag", flag_recipe_item_name),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_recipe_item_name + "battery");
                    } catch (Throwable e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        e.printStackTrace();
                    }
                    return false;
                });

        String flag_recipe_type_name = FilterType.BY_RECIPE_TYPE_NAME.getFlag();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-search-expansion-by-recipe-type-name", Material.LODESTONE, "flag", flag_recipe_type_name),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_recipe_type_name + "crafting table");
                    } catch (Throwable e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        e.printStackTrace();
                    }
                    return false;
                });

        String flag_display_item_name = FilterType.BY_DISPLAY_ITEM_NAME.getFlag();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-search-expansion-by-display-item-name", Material.LODESTONE, "flag", flag_display_item_name),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_display_item_name + "copper.dust");
                    } catch (Throwable e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        e.printStackTrace();
                    }
                    return false;
                });

        String flag_addon_name = FilterType.BY_ADDON_NAME.getFlag();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-search-expansion-by-addon-name", Material.LODESTONE, "flag", flag_addon_name),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_addon_name + "Slimefun");
                    } catch (Throwable e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        e.printStackTrace();
                    }
                    return false;
                });

        String flag_item_name = FilterType.BY_ITEM_NAME.getFlag();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-search-expansion-by-item-name", Material.LODESTONE, "flag", flag_item_name),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_item_name + "Battery");
                    } catch (Throwable e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        e.printStackTrace();
                    }
                    return false;
                });

        String flag_material_name = FilterType.BY_MATERIAL_NAME.getFlag();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Lang.getGuideGroupIcon("feature-search-expansion-by-material-name", Material.LODESTONE, "flag", flag_material_name),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_material_name + "iron");
                    } catch (Throwable e) {
                        p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                        e.printStackTrace();
                    }
                    return false;
                });
    }

    public static void doIf(boolean expression, @NotNull Runnable runnable) {
        if (expression) {
            runnable.run();
        }
    }
}
