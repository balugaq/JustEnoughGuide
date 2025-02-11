package com.balugaq.jeg.implementation.items;

import com.balugaq.jeg.api.groups.ClassicGuideGroup;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.objects.enums.FilterType;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.GuideUtil;
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
    private static final ItemStack HEADER = new CustomItemStack(
            Material.BEACON,
            "&bJEG Guide",
            "&bAuthor: balugaq",
            "&bJEG improved Slimefun's guides, make it humanizer and more efficient.",
            "&bLook up the following guides to get started with JEG's enhanced features.",
            "&bIssue Tracker: https://github.com/balugaq/JustEnoughGuide/issues",
            "&bSuggested languages to report issues: ",
            "&b      English",
            "&b      Simplified Chinese",
            "&b      Traditional Chinese"
    );
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
        /*
        doIf(false, () -> {
            addGuide(
                    GUIDE_SLOTS[index.getAndIncrement()],
                    new CustomItemStack(Material.CLOCK,
                            "&bFeature: 拼音搜索",
                            "&bIntroduction: 你可以通过拼音搜索指南来快速找到你想要的物品。",
                            "&bClick to try!"
                    ), (p, s, i, a) -> {
                        try {
                            p.performCommand("sf search ding");
                        } catch (Throwable e) {
                            p.sendMessage("§cAn error occured when clicked in JEGGuideGroup");
                            e.printStackTrace();
                        }
                        return false;
                    });
        });
        
         */

        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                new CustomItemStack(
                        Material.NAME_TAG,
                        "&bFeature: Search Paging",
                        "&bIntroduction: You can look up more search results by paging",
                        "&bClick to try!"
                ), (p, s, i, a) -> {
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
                    new CustomItemStack(
                            Material.BOOK,
                            "&bFeature: Collect Items",
                            "&bIntroduction: When viewing a category, as for the supported addons:",
                            "&b      You can click the \"Book\" icon to collect items in this category.",
                            "&a      Click back button to exit.",
                            "&bClick to try!"
                    ), (p, s, i, a) -> {
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
                    new CustomItemStack(
                            Material.NETHER_STAR,
                            "&bFeature: View Collected",
                            "&bIntroduction: You can view the items you have collected",
                            "&b      You can click the \"Nether Star\" icon to view the collected items.",
                            "&a      Click back button to exit.",
                            "&bClick to try!"
                    ), (p, s, i, a) -> {
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
                new CustomItemStack(
                        Material.CRAFTING_TABLE,
                        "&bFeature: Jump Category",
                        "&bIntroduction: When you view a recipe，You can jump the material's category by simple clicking.",
                        "&b      Like... Shift+Left-click on the material's icon",
                        "&bClick to try!"
                ), (p, s, i, a) -> {
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
                    new CustomItemStack(
                            Material.ENCHANTED_BOOK,
                            "&bFeature: Quick Research",
                            "&bIntroduction: You can research items anywhere",
                            "&bClick to try!"
                    ), (p, s, i, a) -> {
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
                new CustomItemStack(
                        Material.COMPARATOR,
                        "&bFeature: Smart Searching",
                        "&bIntroduction: You can look up a item's producer by searching its name.",
                        "&bClick to try!"
                ), (p, s, i, a) -> {
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
                new CustomItemStack(
                        Material.LODESTONE,
                        "&bFeature: Searching Expansion",
                        "&bIntroduction: You can prefix text with " + flag_recipe_item_name + "<recipe_item_name> to limit the search to a specific range.",
                        "&b      Ex: " + flag_recipe_item_name + "Battery can search the item which its recipe contains \"Battery\"",
                        "&c      Allowed to combine different expansions.",
                        "&bClick to try!"
                ), (p, s, i, a) -> {
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
                new CustomItemStack(
                        Material.LODESTONE,
                        "&bFeature: Searching Expansion",
                        "&bIntroduction: You can prefix text with " + flag_recipe_type_name + "<recipe_type_name> to limit the search to a specific range.",
                        "&b      Ex: " + flag_recipe_type_name + "crafting.table can search the item which its recipe type name contains \"crafting table\"",
                        
                        "&c      Allowed to combine different expansions.",
                        "&bClick to try!"
                ), (p, s, i, a) -> {
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
                new CustomItemStack(
                        Material.LODESTONE,
                        "&bFeature: Searching Expansion",
                        "&bIntroduction: You can prefix text with " + flag_display_item_name + "<display_item_name> to limit the search to a specific range.",
                        "&b      Ex: " + flag_display_item_name + "copper.dust can search the item which its recipe display items' name contains \"copper dust\"",
                        
                        "&c      Allowed to combine different expansions.",
                        "&bClick to try!"
                ), (p, s, i, a) -> {
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
                new CustomItemStack(
                        Material.LODESTONE,
                        "&bFeature: Searching Expansion",
                        "&bIntroduction: You can prefix text with " + flag_addon_name + "<addon_name> to limit the search to a specific range.",
                        "&b      Ex: " + flag_addon_name + "slimefun can search the item which is from \"Slimefun\"",
                        
                        "&c      Allowed to combine different expansions.",
                        "&bClick to try!"
                ), (p, s, i, a) -> {
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
                new CustomItemStack(
                        Material.LODESTONE,
                        "&bFeature: Searching Expansion",
                        "&bIntroduction: You can prefix text with " + flag_item_name + "<item_name> to limit the search to a specific range.",
                        "&b      Ex: " + flag_item_name + "Battery can search the item which its name contains \"Battery\"",
                        "&c      Allowed to combine different expansions.",
                        "&bClick to try!"
                ), (p, s, i, a) -> {
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
                new CustomItemStack(
                        Material.LODESTONE,
                        "&bFeature: Searching Expansion",
                        "&bIntroduction: You can prefix text with " + flag_material_name + "<material_name> to limit the search to a specific range.",
                        "&b      Ex: " + flag_material_name + "iron can search the item which its type name contains \"iron\"",
                        "&c      Allowed to combine different expansions.",
                        "&bClick to try!"
                ), (p, s, i, a) -> {
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
