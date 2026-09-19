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

package com.balugaq.jeg.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.annotation.Values;
import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.items.GroupTierEditorGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.clickhandler.OnClick;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import net.guizhanss.minecraft.guizhanlib.gugu.minecraft.helpers.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * JEG 命令系统的 ACF 根类（合并单 root 类）。
 * <p>
 * 路由 / 权限 / help / tab 补全 全部交给 ACF：
 * <ul>
 *   <li>路由：{@code @Subcommand("xxx")}，不再手写 canCommand 抢活</li>
 *   <li>权限：{@code @CommandPermission("jeg.xxx")}，节点在 plugin.yml 声明（默认 op / 玩家 true）</li>
 *   <li>help ：{@code @Default} + {@code @CatchUnknown} 兜底，ACF 同时自动生成 /jeg help 列表</li>
 *   <li>补全：{@code @Completion("sfitems")}/{@code @Completion("cachekey")} 引用 CommandManager 注册的补全器</li>
 *   <li>玩家命令：方法首参声明 Player，ACF 自动拒绝 console 并提示</li>
 * </ul>
 *
 * @author balugaq
 * @since 1.1
 */
@CommandAlias("justenoughguide|jeg")
@SuppressWarnings({"unused", "deprecation", "ConstantValue"})
@NullMarked
public class JEGCommands extends BaseCommand {

    // ---- 帮助：/jeg 与 /jeg <未知> 走这里；ACF 同时自动生成 /jeg help 列表 ----
    @Default
    @CatchUnknown
    @Description("Show JEG command help")
    public void onHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "JEG Commands:");
        sender.sendMessage(ChatColor.GREEN + "/jeg help - Show this help message");
        sender.sendMessage(ChatColor.GREEN + "/jeg reload - Reload JEG plugin");
        sender.sendMessage(ChatColor.GREEN + "/jeg cache <section> <key>");
        sender.sendMessage(ChatColor.GREEN + "/jeg disable - Disable JEG plugin");
        sender.sendMessage(ChatColor.GREEN + "/jeg gteg - Get Guide Tier Editor");
        sender.sendMessage(ChatColor.GREEN + "/jeg categories - View all the groups");
        sender.sendMessage(ChatColor.GREEN + "/jeg share - Share the item on your hand");
        sender.sendMessage(ChatColor.GREEN + "/jeg viewitem <Slimefun Item> - View Slimefun item");
        sender.sendMessage(ChatColor.GREEN + "/jeg search [item name] - Search item on hand or search your input");
    }

    // ---- op 专用后台命令 ----

    @Subcommand("reload")
    @CommandPermission("jeg.reload")
    @Description("Reload JEG plugin")
    public void onReload(CommandSender sender) {
        JustEnoughGuide.reload(sender);
    }

    @Subcommand("disable")
    @CommandPermission("jeg.disable")
    @Description("Disable JEG plugin")
    public void onDisable(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Disabling plugin...");
        try {
            JustEnoughGuide.getInstance().unloadInternal();
            sender.sendMessage(ChatColor.GREEN + "plugin has been disabled.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Failed to disable plugin.");
            Debug.trace(e);
        }
    }

    @Subcommand("cache")
    @CommandPermission("jeg.cache")
    @Description("Inspect JEG search caches")
    public void onCache(
        CommandSender sender,
        @Values("1|2|kw|keyword|dr|display_recipes") String section,
        @Single @Syntax("@completion[cachekey]") String key) {
        Map<Character, Set<SlimefunItem>> cache;
        switch (section) {
            case "1", "kw", "keyword" -> cache = SearchGroup.KEYWORD_CACHE;
            case "2", "dr", "display_recipes" -> cache = SearchGroup.DISPLAY_RECIPES_CACHE;
            default -> {
                sender.sendMessage(ChatColor.RED + "Invalid section number. Please choose 1 or 2.");
                return;
            }
        }

        if (cache == null) {
            sender.sendMessage(ChatColor.RED + "Invalid cache.");
            return;
        }

        if ("clear".equalsIgnoreCase(key)) {
            cache.clear();
            sender.sendMessage(ChatColor.GREEN + "Cache " + section + " cleared.");
            return;
        }

        char cacheKey = key.charAt(0);
        sender.sendMessage(ChatColor.GREEN + "Checking cache " + section + " for " + cacheKey + "...");
        if (!cache.containsKey(cacheKey)) {
            sender.sendMessage(ChatColor.RED + "Cache for " + cacheKey + " is invalid.");
            return;
        }

        int size = -1;
        Set<SlimefunItem> set = cache.get(cacheKey);
        if (set != null) {
            size = set.size();
            sender.sendMessage(ChatColor.GREEN + "Items: ");
            for (SlimefunItem item : set) {
                sender.sendMessage(ChatColor.GREEN + " - " + item.getItemName());
            }
        }

        sender.sendMessage(ChatColor.GREEN + "Cache for " + cacheKey + " is valid.");
        sender.sendMessage(ChatColor.GREEN + "Cache size: " + cache.size());
        if (size != -1) {
            sender.sendMessage(ChatColor.GREEN + "Character set size: " + size);
        }
    }

    // ---- op 专用玩家命令 ----

    @Subcommand("gteg")
    @CommandPermission("jeg.gteg")
    @Description("Get the Guide Tier Editor item")
    public void onGteg(Player player) {
        player.getInventory().addItem(GroupTierEditorGuide.instance().clone());
    }

    @Subcommand("categories")
    @CommandPermission("jeg.categories")
    @Description("View all Slimefun item groups")
    public void onCategories(Player player) {
        ChestMenu menu = new ChestMenu("&6物品组大全");
        menu.setSize(54);
        populateCategoryMenu(menu, new ArrayList<>(Slimefun.getRegistry().getAllItemGroups()), 1, player);
        menu.setPlayerInventoryClickable(false);
        menu.open(player);
    }

    @Subcommand("search")
    @CommandPermission("jeg.search")
    @Description("Search for items")
    public void onSearch(Player player, @Optional String query) {
        if (query == null) {
            ItemStack stack = player.getInventory().getItemInMainHand();
            if (stack == null || stack.getType().isAir()) {
                stack = player.getInventory().getItemInOffHand();
            }
            if (stack == null || stack.getType().isAir()) {
                player.sendMessage(ChatColor.RED + "你必须手持一个物品在手上");
                return;
            }

            String itemName = ItemStackHelper.getDisplayName(stack).trim();
            player.chat("/sf search " + ChatColor.stripColor(itemName));
            return;
        }

        player.chat("/sf search " + ChatColor.stripColor(query));
    }

    // ---- 任意玩家可用命令（无 @CommandPermission） ----

    @Subcommand("share")
    @Description("Share the item in hand")
    public void onShare(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            item = player.getInventory().getItemInOffHand();
        }
        if (item == null || item.getType().isAir()) {
            player.sendMessage(ChatColors.color("&c请将物品放在手上"));
            return;
        }
        OnClick.share(player, ItemStackHelper.getDisplayName(item).trim());
    }

    @Subcommand("viewitem")
    @Description("View a Slimefun item by id")
    @Syntax("@completion[sfitems]")
    public void onViewItem(Player player, @Single String id) {
        SlimefunItem slimefunItem = SlimefunItem.getById(id.toUpperCase(Locale.ROOT));
        if (slimefunItem == null || (!player.isOp() && slimefunItem.isDisabledIn(player.getWorld()))) {
            player.sendMessage(ChatColors.color("&c无法查看 ID 为 " + id + "物品"));
            return;
        }
        PlayerProfile profile = PlayerProfile.find(player).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.getLastGuide(player).displayItem(profile, slimefunItem, true);
    }

    // ---- Categories 命令的 GUI 辅助（原 CategoriesCommand 静态逻辑平移）----

    @SuppressWarnings("deprecation")
    private static void populateCategoryMenu(
        ChestMenu menu, List<ItemGroup> groups, @Range(from = 1, to = Integer.MAX_VALUE) int page, Player p) {
        for (int i = 0; i < 54; i++) {
            menu.addMenuClickHandler(i, ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i = 45; i < 54; i++) {
            menu.replaceExistingItem(i, ChestMenuUtils.getBackground());
        }

        for (int i = 0; i < 45; i++) {
            int groupIndex = i + 1 + (page - 1) * 45;
            ItemGroup group = getItemGroupOrNull(groups, groupIndex);
            if (group != null) {
                ItemStack catItem = group.getItem(p).clone();
                ItemMeta catMeta = catItem.getItemMeta();
                List<String> categoryLore = catMeta.getLore();

                String id = group.getKey().getNamespace() + ":" + group.getKey().getKey();
                String className = group.getClass().getName();
                if (categoryLore == null) {
                    categoryLore = new ArrayList<>(2);
                }
                categoryLore.set(
                    categoryLore.size() - 1, ChatColors.color("&6ID: " + id)); // Replaces the "Click to Open" line
                categoryLore.add(ChatColors.color("&6class: " + className));
                categoryLore.add(ChatColors.color("&a点击复制到聊天栏"));
                catMeta.setLore(categoryLore);
                catItem.setItemMeta(catMeta);
                menu.replaceExistingItem(i, catItem);
                menu.addMenuClickHandler(
                    i, (p1, s1, i1, a1) -> {
                        com.balugaq.jeg.utils.ClipboardUtil.send(p1, "&d点击复制: " + id, "&d点击复制", id);
                        com.balugaq.jeg.utils.ClipboardUtil.send(p1, "&d点击复制: " + className, "&d点击复制", className);
                        return false;
                    }
                );
            } else {
                menu.replaceExistingItem(i, Converter.getItem());
            }
        }

        if (page > 1) {
            menu.replaceExistingItem(46, Converter.getItem(Material.LIME_STAINED_GLASS_PANE, "&a上一页"));
            menu.addMenuClickHandler(
                46, (pl, s, is, action) -> {
                    populateCategoryMenu(menu, groups, page - 1, p);
                    return false;
                }
            );
        }

        if (getItemGroupOrNull(groups, 45 * page + 1) != null) {
            menu.replaceExistingItem(52, Converter.getItem(Material.LIME_STAINED_GLASS_PANE, "&a下一页"));
            menu.addMenuClickHandler(
                52, (pl, s, is, action) -> {
                    populateCategoryMenu(menu, groups, page + 1, p);
                    return false;
                }
            );
        }
    }

    private static @Nullable ItemGroup getItemGroupOrNull(List<ItemGroup> groups, int index) {
        return index < groups.size() ? groups.get(index) : null;
    }
}
