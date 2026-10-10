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

package com.balugaq.jeg.utils;

import com.balugaq.jeg.core.lang.Lang;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 静态图标 / 注册物品的文案模型。
 * <p>
 * 所有带文案的图标均为<b>懒加载</b>（首次调用对应方法时才构建），
 * 因为类的静态初始化可能早于 {@link Lang#load}，静态 final 常量会在语言文件加载前
 * 构建出「键名物品」。文案统一来自语言文件 {@code models.*} 节。
 * <p>
 * 语言文件 lore 行若<b>整行</b>形如 {@code {models.xxx}}，会在构建时替换为对应语言键的翻译，
 * 用于复用公共说明行（如配方补全的点击机制），避免多处维护同一句文案。
 *
 * @author balugaq
 * @since 1.3
 */
@NullMarked
public class Models {
    private static final String KEY = "models.";
    private static final Pattern INCLUDE_LINE = Pattern.compile("^\\{([a-z0-9.\\-]+)}$");

    /** 纯装饰边框，无文案，不涉及翻译，保持常量。 */
    public static final ItemStack KEYBIND_ACTION_BORDER = Converter.getItem(
        Material.YELLOW_STAINED_GLASS_PANE, " ",
        " "
    );

    private static @Nullable ItemStack rtsItem;
    private static @Nullable ItemStack specialMenuItem;
    private static @Nullable ItemStack inputTextIcon;
    private static @Nullable ItemStack itemMarkBackground;
    private static @Nullable ItemStack slimefunRecipeEdit;
    private static @Nullable ItemStack jegGuideGroup;
    private static @Nullable ItemStack hiddenItemsGroup;
    private static @Nullable ItemStack nexcavateItemsGroup;
    private static @Nullable ItemStack vanillaItemsGroup;
    private static @Nullable ItemStack recipeCompletableGroup;
    private static @Nullable ItemStack jegItemsGroup;
    private static @Nullable ItemStack replacementCardsGroup;
    private static @Nullable ItemStack bannedItemsGroup;
    private static @Nullable ItemStack multiBlockBuilderItemsGroup;
    private static @Nullable SlimefunItemStack recipeCompleteGuide;
    private static @Nullable SlimefunItemStack usageInfo;
    private static @Nullable SlimefunItemStack mechanism;
    private static @Nullable SlimefunItemStack supportedAddonsInfo;
    private static @Nullable SlimefunItemStack jegRecipeCompleteButton;
    private static @Nullable SlimefunItemStack customLagBlock;

    public static ItemStack rtsItem() {
        ItemStack item = rtsItem;
        if (item == null) {
            item = Converter.getItem(new SlimefunItemStack(
                "_UI_RTS_ICON", Converter.getItem(Material.ANVIL, Lang.t(KEY + "rts-item.name"), "")));
            rtsItem = item;
        }
        return item;
    }

    public static ItemStack specialMenuItem() {
        ItemStack item = specialMenuItem;
        if (item == null) {
            item = Converter.getItem(new SlimefunItemStack(
                "_UI_SPECIAL_MENU_ICON", icon(Material.COMPASS, "special-menu-item")));
            specialMenuItem = item;
        }
        return item;
    }

    public static ItemStack inputTextIcon() {
        ItemStack item = inputTextIcon;
        if (item == null) {
            item = Converter.getItem(new SlimefunItemStack(
                "_UI_RTS_INPUT_TEXT_ICON", icon(Material.PAPER, "input-text-icon")));
            inputTextIcon = item;
        }
        return item;
    }

    public static ItemStack itemMarkBackground() {
        ItemStack item = itemMarkBackground;
        if (item == null) {
            item = icon(Material.GREEN_STAINED_GLASS_PANE, "item-mark-background");
            itemMarkBackground = item;
        }
        return item;
    }

    public static ItemStack slimefunRecipeEdit() {
        ItemStack item = slimefunRecipeEdit;
        if (item == null) {
            item = icon(Material.DIAMOND, "slimefun-recipe-edit");
            slimefunRecipeEdit = item;
        }
        return item;
    }

    public static ItemStack jegGuideGroup() {
        ItemStack item = jegGuideGroup;
        if (item == null) {
            item = Converter.getItem(new SlimefunItemStack(
                "JEG_JEG_GUIDE_GROUP", Converter.getItem(Material.KNOWLEDGE_BOOK, Lang.t(KEY + "jeg-guide-group.name"))));
            jegGuideGroup = item;
        }
        return item;
    }

    public static ItemStack hiddenItemsGroup() {
        ItemStack item = hiddenItemsGroup;
        if (item == null) {
            item = Converter.getItem(new SlimefunItemStack(
                "JEG_HIDDEN_ITEMS_GROUP", Converter.getItem(Material.BARRIER, Lang.t(KEY + "hidden-items-group.name"))));
            hiddenItemsGroup = item;
        }
        return item;
    }

    public static ItemStack nexcavateItemsGroup() {
        ItemStack item = nexcavateItemsGroup;
        if (item == null) {
            item = Converter.getItem(new SlimefunItemStack(
                "JEG_NEXCAVATE_ITEMS_GROUP_ICON", Converter.getItem(Material.BLACKSTONE, Lang.t(KEY + "nexcavate-items-group.name"))));
            nexcavateItemsGroup = item;
        }
        return item;
    }

    public static ItemStack vanillaItemsGroup() {
        ItemStack item = vanillaItemsGroup;
        if (item == null) {
            item = Converter.getItem(new SlimefunItemStack(
                "JEG_VANILLA_ITEMS_GROUP", Converter.getItem(Material.CRAFTING_TABLE, Lang.t(KEY + "vanilla-items-group.name"))));
            vanillaItemsGroup = item;
        }
        return item;
    }

    public static ItemStack recipeCompletableGroup() {
        ItemStack item = recipeCompletableGroup;
        if (item == null) {
            item = Converter.getItem(new SlimefunItemStack(
                "JEG_RECIPE_COMPLETABLE_GROUP", Converter.getItem(Material.CRAFTING_TABLE, Lang.t(KEY + "recipe-completable-group.name"))));
            recipeCompletableGroup = item;
        }
        return item;
    }

    public static ItemStack jegItemsGroup() {
        ItemStack item = jegItemsGroup;
        if (item == null) {
            item = Converter.getItem(new SlimefunItemStack(
                "JEG_JEG_ITEMS_GROUP", Converter.getItem(Material.BOOK, Lang.t(KEY + "jeg-items-group.name"))));
            jegItemsGroup = item;
        }
        return item;
    }

    public static ItemStack replacementCardsGroup() {
        ItemStack item = replacementCardsGroup;
        if (item == null) {
            item = Converter.getItem(new SlimefunItemStack(
                "JEG_REPLACEMENT_CARDS_GROUP", Converter.getItem(Material.PAPER, Lang.t(KEY + "replacement-cards-group.name"))));
            replacementCardsGroup = item;
        }
        return item;
    }

    public static ItemStack bannedItemsGroup() {
        ItemStack item = bannedItemsGroup;
        if (item == null) {
            item = Converter.getItem(new SlimefunItemStack(
                "JEG_BANNED_ITEMS_GROUP", Converter.getItem(Material.COMMAND_BLOCK, Lang.t(KEY + "banned-items-group.name"))));
            bannedItemsGroup = item;
        }
        return item;
    }

    public static ItemStack multiBlockBuilderItemsGroup() {
        ItemStack item = multiBlockBuilderItemsGroup;
        if (item == null) {
            item = Converter.getItem(new SlimefunItemStack(
                "MULTI_BLOCK_BUILDER_ITEMS_GROUP", Converter.getItem(Material.BRICKS, Lang.t(KEY + "multi-block-builder-items-group.name"))));
            multiBlockBuilderItemsGroup = item;
        }
        return item;
    }

    public static SlimefunItemStack recipeCompleteGuide() {
        SlimefunItemStack item = recipeCompleteGuide;
        if (item == null) {
            item = sfIcon("JEG_RECIPE_COMPLETE_BOOK", Material.SLIME_BALL, "recipe-complete-guide");
            recipeCompleteGuide = item;
        }
        return item;
    }

    public static SlimefunItemStack usageInfo() {
        SlimefunItemStack item = usageInfo;
        if (item == null) {
            item = sfIcon("JEG_RECIPE_COMPLETE_USAGE_INFO", Material.PAPER, "usage-info");
            usageInfo = item;
        }
        return item;
    }

    public static SlimefunItemStack mechanism() {
        SlimefunItemStack item = mechanism;
        if (item == null) {
            item = sfIcon("JEG_RECIPE_COMPLETE_MECHANISM", Material.PAPER, "mechanism");
            mechanism = item;
        }
        return item;
    }

    public static SlimefunItemStack supportedAddonsInfo() {
        SlimefunItemStack item = supportedAddonsInfo;
        if (item == null) {
            item = sfIcon("JEG_RECIPE_COMPLETE_SUPPORTED_ADDONS_INFO", Material.PAPER, "supported-addons-info");
            supportedAddonsInfo = item;
        }
        return item;
    }

    public static SlimefunItemStack jegRecipeCompleteButton() {
        SlimefunItemStack item = jegRecipeCompleteButton;
        if (item == null) {
            item = sfIcon("JEG_RECIPE_COMPLETE_BUTTON", Material.KNOWLEDGE_BOOK, "jeg-recipe-complete-button");
            jegRecipeCompleteButton = item;
        }
        return item;
    }

    public static SlimefunItemStack customLagBlock() {
        SlimefunItemStack item = customLagBlock;
        if (item == null) {
            item = sfIcon("JEG_CUSTOM_LAG_BLOCK", Material.BEDROCK, "custom-lag-block");
            customLagBlock = item;
        }
        return item;
    }

    /**
     * 构建 {@code models.<key>} 定义的图标：name 取 {@code <key>.name}，lore 取 {@code <key>.lore}
     * （含 {@code {models.xxx}} 行内引用展开）。
     */
    private static ItemStack icon(Material material, String key) {
        return Converter.getItem(material, Lang.t(KEY + key + ".name"), lore(KEY + key + ".lore"));
    }

    /** 构建注册用 {@link SlimefunItemStack}（物品 ID 保持与历史版本一致）。 */
    private static SlimefunItemStack sfIcon(String id, Material material, String key) {
        return new SlimefunItemStack(id, icon(material, key));
    }

    /**
     * 取 lore 列表并展开行内引用：整行形如 {@code {models.xxx}} 的行会被替换为对应语言键的翻译。
     */
    private static List<String> lore(String key) {
        List<String> out = new ArrayList<>();
        for (String line : Lang.lines(key)) {
            Matcher matcher = INCLUDE_LINE.matcher(line);
            out.add(matcher.matches() ? Lang.t(matcher.group(1)) : line);
        }
        return out;
    }
}
