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

package com.balugaq.jeg.utils;

import com.balugaq.jeg.api.objects.annotaions.Warn;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Deprecated
@Warn(reason = "No longer using it in EN version")
/**
 * @author balugaq
 * @since 1.3
 */
public class Models {
    public static final String RECIPE_COMPLETE_BOOK_MECHANISM_1 = "&a左键&e点击打开配方书";
    public static final String RECIPE_COMPLETE_BOOK_MECHANISM_2 = "&a右键&e点击可再次补全";
    public static final String RECIPE_COMPLETE_BOOK_MECHANISM_3 = "&e补全后再次&aShift+右键&e点击配方书补全64次";
    public static final String RECIPE_COMPLETE_GUI_MECHANISM_1 = "&a左键&e点击物品补全1次";
    public static final String RECIPE_COMPLETE_GUI_MECHANISM_2 = "&a右键&e点击物品补全64次";
    public static final ItemStack RTS_ITEM =
            Converter.getItem(new SlimefunItemStack("_UI_RTS_ICON", Converter.getItem(Material.ANVIL, "&b实时搜索", "")));
    public static final ItemStack SPECIAL_MENU_ITEM = Converter.getItem(new SlimefunItemStack(
            "_UI_SPECIAL_MENU_ICON", Converter.getItem(Material.COMPASS, "&b超大配方", "", "&a点击打开超大配方(若有)")));
    public static final ItemStack INPUT_TEXT_ICON = Converter.getItem(new SlimefunItemStack(
            "_UI_RTS_INPUT_TEXT_ICON",
            Converter.getItem(
                    Material.PAPER,
                    "&f搜索: &7在上方输入搜索词",
                    "&fTips:",
                    "&7 - &e左侧物品为返回键",
                    "&7 - &e中间物品为按键上一页",
                    "&7 - &e右侧物品为按键下一页")));
    public static final ItemStack JEG_GUIDE_GROUP = Converter.getItem(
            new SlimefunItemStack("JEG_JEG_GUIDE_GROUP", Converter.getItem(Material.KNOWLEDGE_BOOK, "&bJEG 使用指南")));
    public static final ItemStack HIDDEN_ITEMS_GROUP = Converter.getItem(
            new SlimefunItemStack("JEG_HIDDEN_ITEMS_GROUP", Converter.getItem(Material.BARRIER, "&c隐藏物品")));
    public static final ItemStack NEXCAVATE_ITEMS_GROUP = Converter.getItem(new SlimefunItemStack(
            "JEG_NEXCAVATE_ITEMS_GROUP_ICON", Converter.getItem(Material.BLACKSTONE, "&b文明复兴物品")));
    public static final ItemStack VANILLA_ITEMS_GROUP = Converter.getItem(
            new SlimefunItemStack("JEG_VANILLA_ITEMS_GROUP", Converter.getItem(Material.CRAFTING_TABLE, "&7原版物品")));
    public static final ItemStack JEG_ITEMS_GROUP = Converter.getItem(
            new SlimefunItemStack("JEG_JEG_ITEMS_GROUP", Converter.getItem(Material.BOOK, "&bJEG 物品")));
    public static final SlimefunItemStack RECIPE_COMPLETE_GUIDE = new SlimefunItemStack(
            "JEG_RECIPE_COMPLETE_BOOK",
            Converter.getItem(
                    Material.SLIME_BALL,
                    "&b配方补全书",
                    "",
                    "&f点击进行配方补全",
                    RECIPE_COMPLETE_BOOK_MECHANISM_1,
                    RECIPE_COMPLETE_BOOK_MECHANISM_2,
                    RECIPE_COMPLETE_BOOK_MECHANISM_3));
    public static final SlimefunItemStack USAGE_INFO = new SlimefunItemStack(
            "JEG_RECIPE_COMPLETE_USAGE_INFO",
            Converter.getItem(
                    Material.PAPER,
                    "&a使用方法",
                    "",
                    "&f1. &e将配方补全书放到物品栏里",
                    "&f2. &e打开任意一个适配配方补全的机器（如快捷机器）",
                    "&f3. &e左键点击配方补全书"));
    public static final SlimefunItemStack MECHANISM = new SlimefunItemStack(
            "JEG_RECIPE_COMPLETE_MECHANISM",
            Converter.getItem(
                    Material.PAPER,
                    "&a机制",
                    "",
                    "&7优先使用玩家背包中的物品进行补全配方",
                    "&7如果连接了网络，会尝试在网络中获取配方材料（仅网络拓展有效）",
                    "&7如果连接了AE网络，会尝试在AE网络中获取配方材料",
                    "&7连接解释: ",
                    "&7被进行配方补全的机器界面所对应的机器的东西南北上下任一方向紧贴的方块连接了网络/AE网络",
                    "&7即视为被进行配方补全的机器界面所对应的机器连接了网络/AE网络（不占用网络/AE网络节点）",
                    "",
                    "&9===配方书点击机制===",
                    RECIPE_COMPLETE_BOOK_MECHANISM_1,
                    RECIPE_COMPLETE_BOOK_MECHANISM_2,
                    RECIPE_COMPLETE_BOOK_MECHANISM_3,
                    "&9===补全界面点击机制===",
                    RECIPE_COMPLETE_GUI_MECHANISM_1,
                    RECIPE_COMPLETE_GUI_MECHANISM_2));
    public static final SlimefunItemStack SUPPORTED_ADDONS_INFO = new SlimefunItemStack(
            "JEG_RECIPE_COMPLETE_SUPPORTED_ADDONS_INFO",
            Converter.getItem(
                    Material.PAPER,
                    "&a对以下附属的部分机器适配了配方补全",
                    "&7如需适配更多可在 JustEnoughGuide GitHub 提交 issue",
                    "",
                    "&7- &a快捷机器",
                    "&7- &a乱序技艺 2.0-Preview",
                    "&7- &a乱序技艺 2.0",
                    "&7- &a乱序技艺 2.0 改版",
                    "&7- &a无尽贪婪",
                    "&7- &a无尽贪婪2",
                    "&7- &a逻辑工艺",
                    "&7- &a网络",
                    "&7- &a网络拓展",
                    "&7- &a黑曜石科技",
                    "&7- &a粘液AE",
                    "&7- &a蓬松机器",
                    "&7- &a粘液匠魂",
                    "&7- &a星系",
                    "&7- &a美食家",
                    "&7- &aRyken自定义附属",
                    "&7- &a基岩科技",
                    "&7- &a炼金术自传",
                    "&7- &a粘土科技",
                    "&7- &a无底存储"));
}
