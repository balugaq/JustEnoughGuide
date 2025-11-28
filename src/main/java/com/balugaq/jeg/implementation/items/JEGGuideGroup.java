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
    public static final ItemStack HEADER = Converter.getItem(
            Material.BEACON, "&bJEG 使用指南", "&b作者: 大香蕉", "&bJEG 优化了粘液科技的指南，使其更人性化。", "&b查看以下指南书以快速上手 JEG 增加的功能。");
    public static final int[] GUIDE_SLOTS =
            Formats.helper.getChars('h').stream().mapToInt(i -> i).toArray();

    public static final int[] BORDER_SLOTS =
            Formats.helper.getChars('B').stream().mapToInt(i -> i).toArray();

    @SuppressWarnings("SameParameterValue")
    protected JEGGuideGroup(NamespacedKey key, ItemStack icon) {
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
        doIf(
                JustEnoughGuide.getConfigManager().isPinyinSearch(),
                () -> addGuide(
                        GUIDE_SLOTS[index.getAndIncrement()],
                        Converter.getItem(Material.CLOCK, "&b功能: 拼音搜索", "&b介绍: 你可以通过拼音搜索指南来快速找到你想要的物品。", "&b点击尝试功能。"),
                        (p, s, i, a) -> {
                            try {
                                p.performCommand("sf search ding");
                            } catch (Exception e) {
                                p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                                Debug.trace(e);
                            }
                            return false;
                        }
                )
        );

        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Converter.getItem(Material.NAME_TAG, "&b功能: 搜索翻页", "&b介绍: 你可以在搜索中翻页来浏览更多搜索结果。", "&b点击尝试功能。"),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search a");
                    } catch (Exception e) {
                        p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                        Debug.trace(e);
                    }
                    return false;
                }
        );

        doIf(
                JustEnoughGuide.getConfigManager().isBookmark(),
                () -> addGuide(
                        GUIDE_SLOTS[index.getAndIncrement()],
                        Converter.getItem(
                                Material.BOOK,
                                "&b功能: 标记物品",
                                "&b介绍: 你可以打开一个物品组，对于支持的附属。",
                                "&b      你可以点击物品组界面下方的“书”图标以进入标记状态。",
                                "&a      点击返回按钮以退出标记状态。",
                                "&b点击尝试功能。"
                        ),
                        (p, s, i, a) -> {
                            try {
                                if (Slimefun.instance() == null) {
                                    p.sendMessage("§c无法获取 Slimefun 实例，无法使用此功能。");
                                }

                                SlimefunGuideImplementation guide =
                                        GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);

                                if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                                    p.sendMessage("§c功能未启用，无法使用此功能。");
                                    return false;
                                }

                                PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                                if (profile == null) {
                                    p.sendMessage("§c无法获取玩家资料，请检查是否正确安装 Slimefun。");
                                    return false;
                                }

                                for (ItemGroup itemGroup :
                                        Slimefun.getRegistry().getAllItemGroups()) {
                                    if (itemGroup
                                            .getKey()
                                            .equals(new NamespacedKey(Slimefun.instance(), "basic_machines"))) {
                                        jegGuide.openItemMarkGroup(itemGroup, p, profile);
                                        return false;
                                    }
                                }
                            } catch (Exception e) {
                                p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
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
                        Converter.getItem(
                                Material.NETHER_STAR,
                                "&b功能: 查阅标记物品",
                                "&b介绍: 你可以查看你标记过的物品。",
                                "&b      你可以点击物品组界面下方的“下界之星”图标以查看标记过的物品。",
                                "&a      点击返回按钮以退出查看状态。",
                                "&b点击尝试功能。"
                        ),
                        (p, s, i, a) -> {
                            try {
                                if (Slimefun.instance() == null) {
                                    p.sendMessage("§c无法获取 Slimefun 实例，无法使用此功能。");
                                }

                                SlimefunGuideImplementation guide =
                                        GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                                if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                                    p.sendMessage("§c功能未启用，无法使用此功能。");
                                    return false;
                                }

                                PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                                if (profile == null) {
                                    p.sendMessage("§c无法获取玩家资料，请检查是否正确安装 Slimefun。");
                                    return false;
                                }

                                jegGuide.openBookMarkGroup(p, profile);
                            } catch (Exception e) {
                                p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                                Debug.trace(e);
                            }
                            return false;
                        }
                )
        );

        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Converter.getItem(
                        Material.CRAFTING_TABLE,
                        "&b功能: 跳转物品组",
                        "&b介绍: 当你在查阅一个物品的配方时，你可以快速跳转到所需物品所属的物品组。",
                        "&b      你可以 Shift + 左键 点击所需物品，以快速跳转到该物品所属的物品组。",
                        "&b点击尝试功能。"
                ),
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
                }
        );

        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Converter.getItem(
                        Material.NAME_TAG,
                        "&b功能: 快速搜索",
                        "&b介绍: 当你在查阅一个物品的配方时，你可以快速搜索物品、材料、配方类型的名字",
                        "&b      你可以 Shift + 右键 点击所需物品，你可以快速搜索物品、材料、配方类型的名字",
                        "&b点击尝试功能。"
                ),
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
                }
        );

        doIf(
                Slimefun.getConfigManager().isResearchingEnabled(),
                () -> addGuide(
                        GUIDE_SLOTS[index.getAndIncrement()],
                        Converter.getItem(
                                Material.ENCHANTED_BOOK,
                                "&b功能: 便携研究",
                                "&b介绍: 你可以当你在查看物品的配方时，如果有未解锁的物品，可以点击以快速解锁。",
                                "&b点击尝试功能。"
                        ),
                        (p, s, i, a) -> {
                            try {
                                if (Slimefun.instance() == null) {
                                    p.sendMessage("§c无法获取 Slimefun 实例，无法使用此功能。");
                                    return false;
                                }

                                SlimefunGuideImplementation guide =
                                        GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                                if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                                    p.sendMessage("§c功能未启用，无法使用此功能。");
                                    return false;
                                }

                                PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                                if (profile == null) {
                                    p.sendMessage("§c无法获取玩家资料，请检查是否正确安装 Slimefun。");
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
                        }
                )
        );

        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Converter.getItem(
                        Material.COMPARATOR,
                        "&b功能: 智能搜索",
                        "&b介绍: 当你使用搜索时，会自动搜索相关的机器，并添加到显示列表中",
                        "&c     不支持拼音搜索。",
                        "&b点击尝试功能。"
                ),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search 硫酸盐");
                    } catch (Exception e) {
                        p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                        Debug.trace(e);
                    }
                    return false;
                }
        );

        String flag_recipe_item_name = FilterType.BY_RECIPE_ITEM_NAME.getSymbol();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Converter.getItem(
                        Material.LODESTONE,
                        "&b功能: 搜索拓展",
                        "&b介绍: 你可以通过在开头添加 " + flag_recipe_item_name + "<recipe_item_name> 来指定搜索范围",
                        "&b      例如: " + flag_recipe_item_name + "电池 附加搜索 配方使用的物品的名字包含\"电池\" 的物品",
                        "&c      不支持拼音搜索。",
                        "&c      附加搜索会组合生效",
                        "&b点击尝试功能。"
                ),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_recipe_item_name + "电池");
                    } catch (Exception e) {
                        p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                        Debug.trace(e);
                    }
                    return false;
                }
        );

        String flag_recipe_type_name = FilterType.BY_RECIPE_TYPE_NAME.getSymbol();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Converter.getItem(
                        Material.LODESTONE,
                        "&b功能: 搜索拓展",
                        "&b介绍: 你可以在开头添加 " + flag_recipe_type_name + "<recipe_type_name> 来指定搜索范围",
                        "&b      例如: " + flag_recipe_type_name + "工作台 附加搜索 配方类型名称包含\"工作台\" 的物品",
                        "&c      不支持拼音搜索。",
                        "&c      附加搜索会组合生效",
                        "&b点击尝试功能。"
                ),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_recipe_type_name + "工作台");
                    } catch (Exception e) {
                        p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                        Debug.trace(e);
                    }
                    return false;
                }
        );

        String flag_display_item_name = FilterType.BY_DISPLAY_ITEM_NAME.getSymbol();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Converter.getItem(
                        Material.LODESTONE,
                        "&b功能: 搜索拓展",
                        "&b介绍: 你可以在开头添加 " + flag_display_item_name + "<display_item_name> 来指定搜索范围",
                        "&b      例如: " + flag_display_item_name + "铜粉 附加搜索 配方展示涉及的物品的名字包含\"铜粉\" 的物品",
                        "&c      不支持拼音搜索。",
                        "&c      附加搜索会组合生效",
                        "&b点击尝试功能。"
                ),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_display_item_name + "铜粉");
                    } catch (Exception e) {
                        p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                        Debug.trace(e);
                    }
                    return false;
                }
        );

        String flag_addon_name = FilterType.BY_ADDON_NAME.getSymbol();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Converter.getItem(
                        Material.LODESTONE,
                        "&b功能: 搜索拓展",
                        "&b介绍: 你可以在开头添加 " + flag_addon_name + "<addon_name> 来指定搜索范围",
                        "&b      例如: " + flag_addon_name + "粘液科技 附加搜索 附属名称包含\"粘液科技\" 的物品",
                        "&c      不支持拼音搜索。",
                        "&c      附加搜索会组合生效",
                        "&b点击尝试功能。"
                ),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_addon_name + "粘液科技");
                    } catch (Exception e) {
                        p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                        Debug.trace(e);
                    }
                    return false;
                }
        );

        String flag_item_name = FilterType.BY_ITEM_NAME.getSymbol();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Converter.getItem(
                        Material.LODESTONE,
                        "&b功能: 搜索拓展",
                        "&b介绍: 你可以在开头添加 " + flag_item_name + "<item_name> 来指定搜索范围",
                        "&b      例如: " + flag_item_name + "电池 附加搜索 物品名称包含\"电池\" 的物品",
                        "&b      支持拼音搜索。",
                        "&c      附加搜索会组合生效",
                        "&b点击尝试功能。"
                ),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_item_name + "电池");
                    } catch (Exception e) {
                        p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                        Debug.trace(e);
                    }
                    return false;
                }
        );

        String flag_item_lore = FilterType.BY_ITEM_LORE.getSymbol();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Converter.getItem(
                        Material.LODESTONE,
                        "&b功能: 搜索拓展",
                        "&b介绍: 你可以在开头添加 " + flag_item_lore + "<item_lore> 来指定搜索范围",
                        "&b      例如: " + flag_item_lore + "胡萝卜 附加搜索 物品描述包含\"胡萝卜\" 的物品",
                        "&b      支持拼音搜索。",
                        "&c      附加搜索会组合生效",
                        "&b点击尝试功能。"
                ),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_item_lore + "胡萝卜");
                    } catch (Exception e) {
                        p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                        Debug.trace(e);
                    }
                    return false;
                }
        );

        String flag_material_name = FilterType.BY_MATERIAL_NAME.getSymbol();
        addGuide(
                GUIDE_SLOTS[index.getAndIncrement()],
                Converter.getItem(
                        Material.LODESTONE,
                        "&b功能: 搜索拓展",
                        "&b介绍: 你可以在开头添加 " + flag_material_name + "<material_name> 来指定搜索范围",
                        "&b      例如: " + flag_material_name + "iron 附加搜索 物品材质名称包含\"iron\" 的物品",
                        "&c      不支持拼音搜索。",
                        "&c      附加搜索会组合生效",
                        "&b点击尝试功能。"
                ),
                (p, s, i, a) -> {
                    try {
                        p.performCommand("sf search " + flag_material_name + "iron");
                    } catch (Exception e) {
                        p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                        Debug.trace(e);
                    }
                    return false;
                }
        );

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
