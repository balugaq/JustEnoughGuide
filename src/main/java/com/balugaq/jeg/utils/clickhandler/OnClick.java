/*
 * Copyright (c) 2024-2026 balugaq
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

package com.balugaq.jeg.utils.clickhandler;

import com.balugaq.jeg.api.editor.GroupResorter;
import com.balugaq.jeg.api.groups.BookmarkGroup;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.objects.collection.cooldown.FrequencyWatcher;
import com.balugaq.jeg.api.objects.enums.FilterType;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.option.ShareInGuideOption;
import com.balugaq.jeg.implementation.option.ShareOutGuideOption;
import com.balugaq.jeg.utils.ClipboardUtil;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.StackUtils;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.compatibility.Sounds;
import com.balugaq.jeg.utils.platform.PlatformUtil;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings({"deprecation"})
@NullMarked
public interface OnClick {
    // @formatter:off
    MessageFormat SHARED_ITEM_MESSAGE = new MessageFormat(ChatColors.color("&a{0} &e分享了 &7[{1}&r&7]&e <点击搜索>"));
    String CLICK_TO_SEARCH = ChatColors.color("&e点击搜索");
    FrequencyWatcher<UUID> SHARING_WATCHER = new FrequencyWatcher<>(1, TimeUnit.MINUTES, 10, 5000);
    ObjectImmutableList<? extends OnClick> keybindSets = ObjectImmutableList.of(
            Holder.Item, Holder.ItemGroup, Holder.RecipeType
    );

    @SuppressWarnings("RedundantIfStatement")
    static void preset(ChestMenu menu) {
        menu.setEmptySlotsClickable(false);
        menu.addPlayerInventoryClickHandler((p, s, i, a) ->
                                                    p.isOp() || p.hasPermission("slimefun.cheat.items")
        );
        menu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sounds.GUIDE_BUTTON_CLICK_SOUND, 1, 1));
        menu.addMenuClickHandler(-999, (p, s, i, a) -> {
            // it called when the player clicks outside the inventory
            if (p.isOp() || p.hasPermission("slimefun.cheat.items")) {
                // op or permissible players are allowed to drop item
                return true;
            }
            return false;
        });
    }

    static void share(Player player, String itemName) {
        if (!checkShareCooldown(player)) return;
        if (!ShareOutGuideOption.instance().isEnabled(player)) return;

        String s = itemName;
        while (s.contains(" ")) s = s.substring(0, itemName.indexOf(" "));
        if (s.isEmpty()) return;
        String playerName = player.getName();

        String sharedMessage = SHARED_ITEM_MESSAGE.format(new Object[] {playerName, ChatColors.color(itemName)});
        if (PlatformUtil.isPaper()) {

            Component base = LegacyComponentSerializer.legacySection().deserialize(sharedMessage)
                    .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(Component.text(CLICK_TO_SEARCH)));
            Component clickToSearch =
                    base.clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/sf search " + ChatColor.stripColor(s)));
            Component clickToCopy =
                    base.clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.COPY_TO_CLIPBOARD, itemName));
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (ShareInGuideOption.instance().isEnabled(p)) {
                    if (p.hasPermission("slimefun.command.search")) {
                        p.sendMessage(clickToSearch);
                    } else {
                        p.sendMessage(clickToCopy);
                    }
                }
            });
        } else {
            TextComponent msg = new TextComponent(sharedMessage);
            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(CLICK_TO_SEARCH)));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sf search " + ChatColor.stripColor(s)));

            Bukkit.getOnlinePlayers().forEach(p -> {
                if (ShareInGuideOption.instance().isEnabled(p)) {
                    if (p.hasPermission("slimefun.command.search")) {
                        ClipboardUtil.send(p, msg);
                    } else {
                        ClipboardUtil.send(p, ClipboardUtil.makeComponent(sharedMessage, CLICK_TO_SEARCH, itemName));
                    }
                }
            });
        }
    }

    static boolean checkShareCooldown(Player player) {
        FrequencyWatcher.Result result = SHARING_WATCHER.checkCooldown(player.getUniqueId());
        if (result == FrequencyWatcher.Result.TOO_FREQUENT) {
            player.sendMessage(ChatColor.RED + "你的使用频率过高，请稍后使用!");
            return false;
        }

        if (result == FrequencyWatcher.Result.CANCEL) {
            player.sendMessage(ChatColor.RED + "这个功能正在冷却中...");
            return false;
        }

        return true;
    }

    @SuppressWarnings("SameReturnValue")
    static ObjectImmutableList<? extends OnClick> keybindSets() {
        return keybindSets;
    }

    static <T extends BaseAction> ObjectImmutableList<T> merge(ObjectImmutableList<T> overridable,
                                                               ObjectImmutableList<T> list) {
        ArrayList<T> merge = new ArrayList<>(overridable);
        for (T action : list) {
            boolean set = false;
            for (int i = 0; i < merge.size(); i++) {
                T o = merge.get(i);
                if (action.getKey().equals(o.getKey())) {
                    merge.set(i, action);
                    set = true;
                    break;
                }
            }

            if (!set) merge.add(action);
        }

        return new ObjectImmutableList<>(merge);
    }

    Material material();

    String name();

    <T extends BaseAction> T findAction(Player player, String key);

    ObjectImmutableList<? extends OnClick> subKeybinds();

    ObjectImmutableList<? extends BaseAction> listActions();

    /**
     * 点击物品组时:
     *   - (彩蛋) 如果是在 4 月 1 日，有 114 / 514 的几率打开Never gonna give you up页面（在聊天栏弹出链接，当天只会弹出一次）
     *   - 在书签中:
     *     - 左键: 打开物品组
     *     - 右键: 取消书签
     *   - 在标记书签中:
     *     - 左键: 标记书签
     *   - 在交换物品组时:
     *     - 点击的是特殊物品组: (FlexItemGroup)
     *       - 左键: 打开物品组
     *       - 右键: 选择物品组
     *     - 点击的是普通物品组: (!FlexItemGroup)
     *       - 左键: 选择物品组
     *   - OP时:
     *     - Shift+左键: 复制物品组的key (namespace:key)
     *     - 若安装了 RSCE:
     *       - Shift+右键: 获取对应的物品组占位符
     *   - 左键: 打开物品组
     *   - 右键: 收藏物品组
     *
     * @author balugaq
     * @since 2.0
     */
    @SuppressWarnings("unchecked")
    @NullMarked
    interface ItemGroup extends OnClick {
        ActionKey ACTION_KEY = ActionKey.of(() -> Holder.ItemGroup, "item-group");

        ItemGroup Normal = new Normal();
        ItemGroup Bookmark = new Bookmark();
        Set<UUID> easterredPlayer = ConcurrentHashMap.newKeySet();

        ObjectImmutableList<ItemGroup> subKeybinds = ObjectImmutableList.of(Normal, Bookmark);

        @Override
        default String name() {
            return "物品组";
        }

        default Action findAction(Player player, String key) {
            for (Action action : listActions()) {
                String k = action.getKey().getKey();
                if (k.equals(key)) {
                    if (JustEnoughGuide.getConfigManager().isAllowActionRedirect()) {
                        String remap = ACTION_KEY.get(player, k);
                        if (remap != null) {
                            for (Action act : listActions()) {
                                 if (act.getKey().getKey().equals(remap)) {
                                     return act;
                                 }
                            }
                        }
                    }
                    return action;
                }
            }

            return new Action() {
                @Override
                public Material material() {
                    return Material.BARRIER;
                }

                @Override
                public String name() {
                    return "empty";
                }

                @Override
                public boolean click(JEGSlimefunGuideImplementation guide, InventoryClickEvent event, Player player,
                                     int slot, io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup,
                                     ClickAction clickAction, ChestMenu menu, int page) {
                    player.sendMessage(ChatColors.color("&c未找到按键: " + key));
                    return false;
                }

                @Override
                public NamespacedKey getKey() {
                    return KeyUtil.newKey(key);
                }
            };
        }

        default ObjectImmutableList<? extends OnClick> subKeybinds() {
            return subKeybinds;
        }

        default ObjectImmutableList<Action> listActions() {
            return ObjectImmutableList.of();
        }

        default ClickHandler create(JEGSlimefunGuideImplementation guide, ChestMenu menu,
                                    io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup) {
            return withGroup(itemGroup, (event, player, slot, cursor, action) -> EventUtil.callEvent(new GuideEvents.RecipeTypeButtonClickEvent(player, event.getCurrentItem(), slot, action, menu, guide)).ifSuccess(() -> {
                if (!easterredPlayer.contains(player.getUniqueId())) {
                    LocalDate date = LocalDate.now();
                    if (date.getMonth() == Month.APRIL && date.getDayOfMonth() == 1) {
                        if (ThreadLocalRandom.current().nextInt(514) < 114) {
                            ChatUtils.sendURL(player, "https://www.bilibili.com/video/BV1GJ411x7h7");
                            player.closeInventory();
                            easterredPlayer.add(player.getUniqueId());
                        }
                    }
                }

                ClickType clickType = event.getClick();
                if (clickType == ClickType.RIGHT) {
                    return findAction(player, "right-click").click(
                            guide, event, player, slot, itemGroup, action,
                            menu, 1
                    );
                }

                if (clickType == ClickType.SHIFT_LEFT) {
                    return findAction(player, "shift-left-click").click(
                            guide, event, player, slot, itemGroup, action
                            , menu, 1
                    );
                }

                if (clickType == ClickType.SHIFT_RIGHT) {
                    return findAction(player, "shift-right-click").click(
                            guide, event, player, slot, itemGroup,
                            action, menu, 1
                    );
                }

                return findAction(player, "default").click(guide, event, player, slot, itemGroup, action, menu, 1);
            }));
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @SuppressWarnings("unused")
        @FunctionalInterface
        @NullMarked
        interface ActionHandle {
            void click(JEGSlimefunGuideImplementation guide, InventoryClickEvent event, Player player, int slot,
                       io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup, ClickAction clickAction,
                       ChestMenu menu, int page);
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @NullMarked
        interface OpAction extends Action, PermissibleAction {
            static OpAction of(String key, String name, Material material, ActionHandle handle) {
                return new OpAction() {
                    @Override
                    public Material material() {
                        return material;
                    }

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public NamespacedKey getKey() {
                        return KeyUtil.newKey(key);
                    }

                    @Override
                    public boolean click(JEGSlimefunGuideImplementation guide, InventoryClickEvent event,
                                         Player player, int slot,
                                         io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup,
                                         ClickAction clickAction, ChestMenu menu, int page) {
                        handle.click(guide, event, player, slot, itemGroup, clickAction, menu, page);
                        return false;
                    }
                };
            }

            @Override
            default boolean hasPermission(Player player) {
                return player.isOp() || player.hasPermission("slimefun.cheat.items");
            }
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @SuppressWarnings("unused")
        @NullMarked
        interface Action extends BaseAction {
            static Action of(String key, String name, Material material, ActionHandle handle) {
                return new Action() {
                    @Override
                    public Material material() {
                        return material;
                    }

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public NamespacedKey getKey() {
                        return KeyUtil.newKey(key);
                    }

                    @Override
                    public boolean click(JEGSlimefunGuideImplementation guide, InventoryClickEvent event,
                                         Player player, int slot,
                                         io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup,
                                         ClickAction clickAction, ChestMenu menu, int page) {
                        handle.click(guide, event, player, slot, itemGroup, clickAction, menu, page);
                        return false;
                    }
                };
            }

            @Override
            default ActionKey parent() {
                return ACTION_KEY;
            }

            @SuppressWarnings("SameReturnValue")
            boolean click(JEGSlimefunGuideImplementation guide, InventoryClickEvent event, Player player, int slot,
                          io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup, ClickAction clickAction,
                          ChestMenu menu, int page);
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @NullMarked
        class Normal implements ItemGroup {
            final ObjectImmutableList<Action> listActions = ObjectImmutableList.of(
                    OpAction.of(
                            "shift-right-click", "作弊模式 - 获取对应的物品组占位符", Material.DECORATED_POT, (guide, event, player,
                                                                                                        slot,
                                                                                                        itemGroup,
                                                                                                        action, menu,
                                                                                                        page) -> {
                                if (!player.isOp()) {
                                    return;
                                }

                                if (!JustEnoughGuide.getIntegrationManager().isEnabledRSCEditor()) {
                                    return;
                                }

                                NamespacedKey key = itemGroup.getKey();
                                String id =
                                        "RSC_EDITOR_ITEM_GROUP_" + key.getNamespace().toUpperCase() + "_" + key.getKey().toUpperCase();
                                SlimefunItem slimefunItem = SlimefunItem.getById(id);
                                if (slimefunItem == null) {
                                    return;
                                }

                                player.getInventory().addItem(ItemStackUtil.getCleanItem(Converter.getItem(slimefunItem.getItem())));
                            }
                    ),
                    OpAction.of(
                            "shift-left-click", "作弊模式 - 复制物品组的key", Material.TRIPWIRE_HOOK, (guide, event, player, slot, itemGroup, action, menu, page) -> {
                                if (!player.isOp()) {
                                    return;
                                }

                                NamespacedKey key = itemGroup.getKey();
                                String s = key.toString();
                                ClipboardUtil.send(player, "&e点击复制物品组的key", s, s);
                            }
                    ),
                    OpAction.of(
                            "copy-full-class", "作弊模式 - 复制物品组的class", Material.COMMAND_BLOCK, (guide, event, player, slot,
                                                                                                itemGroup, action,
                                                                                                menu, page) -> {
                                if (!player.isOp()) {
                                    return;
                                }

                                String s = itemGroup.getClass().getName();
                                ClipboardUtil.send(player, "&e点击复制物品组的class", s, s);
                            }
                    ),
                    Action.of(
                            "right-click", "收藏物品组/选择待交换的物品组", Material.KNOWLEDGE_BOOK, (guide, event, player, slot,
                                                                                                      itemGroup,
                                                                                                      action, menu,
                                                                                                      page) -> {
                                if (GroupResorter.isSelecting(player)) {
                                    if (itemGroup instanceof FlexItemGroup) {
                                        io.github.thebusybiscuit.slimefun4.api.items.ItemGroup selected =
                                                GroupResorter.getSelectedGroup(player);
                                        if (selected == null) {
                                            player.sendMessage(ChatColors.color("&a已选择待交换的物品组: &e" + itemGroup.getDisplayName(player)));
                                            GroupResorter.setSelectedGroup(player, itemGroup);
                                        } else {
                                            GroupResorter.swap(selected, itemGroup);
                                            GroupResorter.setSelectedGroup(player, null);
                                            player.sendMessage(ChatColors.color("&a已交换物品组排序: &e" + selected.getDisplayName(player) + " &7<-> &e" + itemGroup.getDisplayName(player)));
                                            PlayerProfile profile = PlayerProfile.find(player).orElse(null);
                                            if (profile == null) return;
                                            guide.openItemGroup(profile, itemGroup, page);
                                        }
                                    }
                                }

                                JustEnoughGuide.getBookmarkManager().addBookmark(player, itemGroup);
                                player.sendMessage(ChatColors.color("&a已收藏物品组: &e" + itemGroup.getDisplayName(player)));
                            }
                    ),
                    Action.of(
                            "default", "默认", Material.COMPASS, (guide, event, player, slot, itemGroup, action, menu,
                                                                  page) -> {
                                PlayerProfile profile = PlayerProfile.find(player).orElse(null);
                                if (profile == null) return;

                                if (GroupResorter.isSelecting(player)) {
                                    if (!(itemGroup instanceof FlexItemGroup)) {
                                        io.github.thebusybiscuit.slimefun4.api.items.ItemGroup selected =
                                                GroupResorter.getSelectedGroup(player);
                                        if (selected == null) {
                                            player.sendMessage(ChatColors.color("&a已选择待交换的物品组: &e" + itemGroup.getDisplayName(player)));
                                            GroupResorter.setSelectedGroup(player, itemGroup);
                                        } else {
                                            GroupResorter.swap(selected, itemGroup);
                                            GroupResorter.setSelectedGroup(player, null);
                                            player.sendMessage(ChatColors.color("&a已交换物品组排序: &e" + selected.getDisplayName(player) + " &7<-> &e" + itemGroup.getDisplayName(player)));
                                            guide.openItemGroup(profile, itemGroup, page);
                                        }
                                    }
                                }

                                guide.openItemGroup(profile, itemGroup, page);
                            }
                    ),
                    Action.of("none", "无操作", Material.BARRIER, (guide, event, player, slot, group, clickAction, menu, page) -> {
                    })
            );

            @Override
            public Material material() {
                return Material.MOSS_BLOCK;
            }

            @Override
            public String name() {
                return "常规";
            }

            @Override
            public ObjectImmutableList<Action> listActions() {
                return listActions;
            }
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @NullMarked
        class Bookmark extends Normal {
            final ObjectImmutableList<Action> listActions = ObjectImmutableList.of(
                    Action.of(
                            "right-click", "删除标记的物品组", Material.BARREL, (guide, event, player, slot, itemGroup,
                                                                                 action, menu, page) -> EventUtil.callEvent(new GuideEvents.CollectItemGroupEvent(player, itemGroup, slot, action, menu, guide)).ifSuccess(() -> {
                                PlayerProfile playerProfile = PlayerProfile.find(player).orElse(null);
                                if (playerProfile == null) return;
                                GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                                JustEnoughGuide.getBookmarkManager().removeBookmark(player, itemGroup);

                                List<com.balugaq.jeg.api.objects.collection.data.Bookmark> items =
                                        JustEnoughGuide.getBookmarkManager().getBookmarkedItems(player);
                                if (items == null || items.isEmpty()) {
                                    player.closeInventory();
                                    return;
                                }
                                new BookmarkGroup(guide, items).open(player, playerProfile, guide.getMode());
                            })
                    )
            );

            @Override
            public Material material() {
                return Material.BOOKSHELF;
            }

            @Override
            public String name() {
                return "书签";
            }

            @Override
            public ObjectImmutableList<Action> listActions() {
                return merge(super.listActions(), listActions);
            }

            @Override
            public ClickHandler create(JEGSlimefunGuideImplementation guide, ChestMenu menu,
                                       io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup) {
                return withGroup(itemGroup, (event, player, slot, cursor, action) -> EventUtil.callEvent(new GuideEvents.ItemGroupButtonClickEvent(player, event.getCurrentItem(), slot, action, menu, guide)).ifSuccess(() -> {
                    ClickType clickType = event.getClick();
                    // 注入右键
                    if (clickType == ClickType.RIGHT) {
                        return findAction(player, "right-click").click(
                                guide, event, player, slot, itemGroup, action,
                                menu, 1
                        );
                    }

                    return super.create(guide, menu, itemGroup).onClick(event, player, slot, cursor, action);
                }));
            }
        }

        /**
         * @author balugaq
         * @since 2.1
         */
        @NullMarked
        interface ClickHandler extends BaseClickHandler {
            io.github.thebusybiscuit.slimefun4.api.items.ItemGroup getItemGroup();
        }

        static ClickHandler withGroup(io.github.thebusybiscuit.slimefun4.api.items.ItemGroup group, BaseClickHandler base) {
            return new ClickHandler() {
                @Override
                public io.github.thebusybiscuit.slimefun4.api.items.ItemGroup getItemGroup() {
                    return group;
                }

                @Override
                public boolean onClick(final InventoryClickEvent inventoryClickEvent, final Player player, final int i, final ItemStack itemStack, final ClickAction clickAction) {
                    return base.onClick(inventoryClickEvent, player, i, itemStack, clickAction);
                }
            };
        }
    }

    /**
     * 点击配方类型时:
     *   - Q建: 分享配方类型
     *   - 右键: 查找使用此配方类型的物品: 搜索: $名字
     *   - Shift左键: 打开配方类型所在物品组（若有）
     *   - Shift右键: 查找相关物品/机器: 搜索: 名字
     *
     * @author balugaq
     * @since 2.0
     */
    @SuppressWarnings("unchecked")
    @NullMarked
    interface RecipeType extends OnClick {
        ActionKey ACTION_KEY = ActionKey.of(() -> Holder.RecipeType, "recipe-type");
        RecipeType Normal = new Normal();

        ObjectImmutableList<RecipeType> subKeybinds = ObjectImmutableList.of(Normal);

        @Override
        default String name() {
            return "配方类型";
        }

        default Action findAction(Player player, String key) {
            for (Action action : listActions()) {
                String k = action.getKey().getKey();
                if (k.equals(key)) {
                    if (JustEnoughGuide.getConfigManager().isAllowActionRedirect()) {
                        String remap = ACTION_KEY.get(player, k);
                        if (remap != null) {
                            for (Action act : listActions()) {
                                if (act.getKey().getKey().equals(remap)) {
                                    return act;
                                }
                            }
                        }
                    }
                    return action;
                }
            }

            return new Action() {
                @Override
                public Material material() {
                    return Material.BARRIER;
                }

                @Override
                public String name() {
                    return "empty";
                }

                @Override
                public boolean click(JEGSlimefunGuideImplementation guide, Player player, int slot,
                                     io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType recipeType,
                                     ClickAction clickAction, ChestMenu menu, int page) {
                    player.sendMessage(ChatColors.color("&c未找到按键: " + key));
                    return false;
                }

                @Override
                public NamespacedKey getKey() {
                    return KeyUtil.newKey(key);
                }
            };
        }

        default ObjectImmutableList<? extends OnClick> subKeybinds() {
            return subKeybinds;
        }

        default ObjectImmutableList<Action> listActions() {
            return ObjectImmutableList.of();
        }

        default ClickHandler create(JEGSlimefunGuideImplementation guide, ChestMenu menu,
                                    io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType recipeType) {
            return withType(recipeType, (event, player, slot, cursor, action) -> EventUtil.callEvent(new GuideEvents.RecipeTypeButtonClickEvent(player, event.getCurrentItem(), slot, action, menu, guide)).ifSuccess(() -> {
                ItemStack item = event.getCurrentItem();
                if (item == null) return false;
                if (event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP) {
                    return findAction(player, "q").click(guide, player, slot, recipeType, action, menu, 1);
                }

                if (event.getClick() == ClickType.RIGHT) {
                    return findAction(player, "right-click").click(guide, player, slot, recipeType, action, menu, 1);
                }

                if (event.getClick() == ClickType.SHIFT_LEFT) {
                    return findAction(player, "shift-left").click(guide, player, slot, recipeType, action, menu, 1);
                }

                if (event.getClick() == ClickType.SHIFT_RIGHT) {
                    return findAction(player, "shift-right").click(guide, player, slot, recipeType, action, menu, 1);
                }

                return findAction(player, "default").click(guide, player, slot, recipeType, action, menu, 1);
            }));
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @NullMarked
        @SuppressWarnings("unused")
        @FunctionalInterface
        interface ActionHandle {
            void click(JEGSlimefunGuideImplementation guide, Player player, int slot,
                       io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType recipeType, ClickAction clickAction,
                       ChestMenu menu, int page);
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @NullMarked
        @SuppressWarnings("unused")
        interface Action extends BaseAction {
            static Action of(String key, String name, Material material, ActionHandle handle) {
                return new Action() {
                    @Override
                    public Material material() {
                        return material;
                    }

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public NamespacedKey getKey() {
                        return KeyUtil.newKey(key);
                    }

                    @Override
                    public boolean click(JEGSlimefunGuideImplementation guide, Player player, int slot,
                                         io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType recipeType,
                                         ClickAction clickAction, ChestMenu menu, int page) {
                        handle.click(guide, player, slot, recipeType, clickAction, menu, page);
                        return false;
                    }
                };
            }

            @Override
            default ActionKey parent() {
                return ACTION_KEY;
            }

            @SuppressWarnings("SameReturnValue")
            boolean click(JEGSlimefunGuideImplementation guide, Player player, int slot,
                          io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType recipeType,
                          ClickAction clickAction, ChestMenu menu, int page);
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @NullMarked
        class Normal implements RecipeType {
            final ObjectImmutableList<Action> listActions = ObjectImmutableList.of(
                    Action.of(
                            "q", "分享配方类型", Material.CLOCK, (guide, player, slot, recipeType, action, menu, page) -> {
                                String recipeTypeName = ItemStackHelper.getDisplayName(recipeType.getItem(player));
                                share(player, recipeTypeName);
                            }
                    ),
                    Action.of(
                            "right-click", "查找使用此配方类型的物品", Material.NAME_TAG, (guide, player, slot, recipeType,
                                                                                           action, menu, page) -> {
                                String recipeTypeName = ItemStackHelper.getDisplayName(recipeType.getItem(player));
                                player.chat("/sf search " + FilterType.BY_RECIPE_TYPE_NAME.getFirstSymbol() + ChatColor.stripColor(recipeTypeName));
                            }
                    ),
                    Action.of(
                            "shift-left", "打开配方类型所在物品组", Material.CAULDRON, (guide, player, slot, recipeType, action,
                                                                                        menu, page) -> {
                                SlimefunItem machine = recipeType.getMachine();
                                if (machine != null) {
                                    PlayerProfile profile = PlayerProfile.find(player).orElse(null);
                                    if (profile == null) return;
                                    guide.openItemGroup(profile, machine.getItemGroup(), 1);
                                }
                            }
                    ),
                    Action.of(
                            "shift-right", "查找相关物品/机器", Material.ANVIL, (guide, player, slot, recipeType, action,
                                                                                 menu, page) -> {
                                String recipeTypeName = ItemStackHelper.getDisplayName(recipeType.getItem(player));
                                player.chat("/sf search " + ChatColor.stripColor(recipeTypeName));
                            }
                    ),
                    OpAction.of(
                            "copy-id", "作弊模式 - 复制配方类型ID", Material.MAGENTA_GLAZED_TERRACOTTA, (guide, player, slot, recipeType, action, menu, page) -> {
                                if (!player.isOp()) {
                                    return;
                                }

                                String s = recipeType.getKey().asString();
                                ClipboardUtil.send(player, "&e点击复制配方类型的ID", s, s);
                            }
                    ),
                    Action.of(
                            "default", "默认", Material.COMPASS,
                            (guide, player, slot, recipeType, action, menu, page) -> {
                            }
                    ),
                    Action.of("none", "无操作", Material.BARRIER, (guide, player, slot, recipeType, clickAction, menu, page) -> {
                    })
            );

            @Override
            public Material material() {
                return Material.MOSS_BLOCK;
            }

            @Override
            public String name() {
                return "常规";
            }

            @Override
            @SuppressWarnings("SameReturnValue")
            public ObjectImmutableList<Action> listActions() {
                return listActions;
            }
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @NullMarked
        interface OpAction extends Action, PermissibleAction {
            static OpAction of(String key, String name, Material material, ActionHandle handle) {
                return new OpAction() {
                    @Override
                    public Material material() {
                        return material;
                    }

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public NamespacedKey getKey() {
                        return KeyUtil.newKey(key);
                    }

                    @Override
                    public boolean click(JEGSlimefunGuideImplementation guide,
                                         Player player, int slot,
                                         io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType recipeType,
                                         ClickAction clickAction, ChestMenu menu, int page) {
                        handle.click(guide, player, slot, recipeType, clickAction, menu, page);
                        return false;
                    }
                };
            }

            @Override
            default boolean hasPermission(Player player) {
                return player.isOp() || player.hasPermission("slimefun.cheat.items");
            }
        }

        /**
         * @author balugaq
         * @since 2.1
         */
        @NullMarked
        interface ClickHandler extends BaseClickHandler {
            io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType getRecipeType();
        }

        static ClickHandler withType(io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType type, BaseClickHandler base) {
            return new ClickHandler() {
                @Override
                public io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType getRecipeType() {
                    return type;
                }

                @Override
                public boolean onClick(final InventoryClickEvent inventoryClickEvent, final Player player, final int i, final ItemStack itemStack, final ClickAction clickAction) {
                    return base.onClick(inventoryClickEvent, player, i, itemStack, clickAction);
                }
            };
        }
    }

    /**
     * 点击物品时:
     *   - 物品未解锁时: 解锁物品
     *   - F键: 搜索配方展示物品的名字涉及此物品的名字的物品: 搜索: %名字
     *   - Q键: 分享物品
     *   - 在书签中:
     *     左键:
     *     - 在作弊书: 给予物品
     *     - 在生存书: 显示配方界面
     *     右键: 取消书签
     *   - 在标记书签中:
     *     - 左键: 标记书签
     *     - 右键: 查找物品用途: 搜索: #名字
     *     - Shift左键: 打开物品所在物品组
     *     - Shift右键: 查找相关物品/机器: 搜索: 名字
     *     - 有作弊权限:
     *       - 点击中键并且光标为空: 放光标上
     *       - 正在打开作弊书或光标有物品: 放背包里
     *   - 显示配方界面
     *
     * @author balugaq
     * @since 2.0
     */
    @NullMarked
    @SuppressWarnings({"ConstantValue", "unchecked"})
    interface Item extends OnClick {
        ActionKey ACTION_KEY = ActionKey.of(() -> Holder.Item, "item");
        Normal Normal = new Normal();
        ItemMark ItemMark = new ItemMark();
        Bookmark Bookmark = new Bookmark();
        Research Research = new Research();

        ObjectImmutableList<Item> subKeybinds = ObjectImmutableList.of(Normal, ItemMark, Bookmark, Research);

        @Override
        default String name() {
            return "物品";
        }

        default Action findAction(Player player, String key) {
            for (Action action : listActions()) {
                String k = action.getKey().getKey();
                if (k.equals(key)) {
                    if (JustEnoughGuide.getConfigManager().isAllowActionRedirect()) {
                        String remap = ACTION_KEY.get(player, k);
                        if (remap != null) {
                            for (Action act : listActions()) {
                                if (act.getKey().getKey().equals(remap)) {
                                    return act;
                                }
                            }
                        }
                    }
                    return action;
                }
            }

            return new Action() {
                @Override
                public Material material() {
                    return Material.BARRIER;
                }

                @Override
                public String name() {
                    return "empty";
                }

                @Override
                public boolean click(JEGSlimefunGuideImplementation guide, Player player, int slot,
                                     @Nullable SlimefunItem slimefunItem, ItemStack itemStack,
                                     ClickAction clickAction, ChestMenu menu, int page) {
                    return false;
                }

                @Override
                public NamespacedKey getKey() {
                    return KeyUtil.newKey(key);
                }
            };
        }

        default ObjectImmutableList<? extends OnClick> subKeybinds() {
            return subKeybinds;
        }

        default ObjectImmutableList<Action> listActions() {
            return ObjectImmutableList.of();
        }

        default ClickHandler create(JEGSlimefunGuideImplementation guide, ChestMenu menu, int page) {
            return create(guide, menu, page, null);
        }

        default ClickHandler create(JEGSlimefunGuideImplementation guide, ChestMenu menu, int page,
                                    @Nullable SlimefunItem sf) {
            return withItem(sf, (event, player, slot, s, action) -> EventUtil.callEvent(new GuideEvents.ItemButtonClickEvent(player, event.getCurrentItem(), slot, action, menu, guide)).ifSuccess(() -> {
                ItemStack item = event.getCurrentItem();
                if (item == null) return false;
                SlimefunItem slimefunItem = sf == null ? SlimefunItem.getByItem(item) : sf;
                ClickType clickType = event.getClick();
                if (clickType == ClickType.DOUBLE_CLICK) return false;
                // F键
                if (clickType == ClickType.SWAP_OFFHAND) {
                    return findAction(player, "f").click(guide, player, slot, slimefunItem, item, action, menu, page);
                }
                // Q键
                if (clickType == ClickType.DROP || clickType == ClickType.CONTROL_DROP) {
                    return findAction(player, "q").click(guide, player, slot, slimefunItem, item, action, menu, page);
                }
                // 右键
                if (clickType == ClickType.RIGHT) {
                    return findAction(player, "right-click").click(
                            guide, player, slot, slimefunItem, item, action,
                            menu, page
                    );
                }
                // Shift+左键
                if (clickType == ClickType.SHIFT_LEFT && !RecipeCompletableListener.isSelectingItemStackToRecipeComplete(player)) {
                    return findAction(player, "shift-left-click").click(
                            guide, player, slot, slimefunItem, item,
                            action, menu, page
                    );
                }
                // Shift+右键
                if (clickType == ClickType.SHIFT_RIGHT) {
                    return findAction(player, "shift-right-click").click(
                            guide, player, slot, slimefunItem, item,
                            action, menu, page
                    );
                }
                // 有cheat权限
                if (!RecipeCompletableListener.isSelectingItemStackToRecipeComplete(player) && (player.isOp() || player.hasPermission("slimefun.cheat.items"))) {
                    ItemStack cursor = event.getCursor();
                    if (event.getClick() == ClickType.MIDDLE && (cursor == null || cursor.getType() == Material.AIR)) {
                        return findAction(player, "clone-item").click(
                                guide, player, slot, slimefunItem, item, action
                                , menu, page
                        );
                    }
                    if (guide.getMode() == SlimefunGuideMode.CHEAT_MODE || (cursor != null && cursor.getType() != Material.AIR)) {
                        return findAction(player, "take-item").click(
                                guide, player, slot, slimefunItem, item, action,
                                menu, page
                        );
                    }
                }

                return findAction(player, "default").click(guide, player, slot, slimefunItem, item, action, menu, page);
            }));
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @NullMarked
        @FunctionalInterface
        interface ActionHandle {
            void click(JEGSlimefunGuideImplementation guide, Player player, int slot,
                       @Nullable SlimefunItem slimefunItem, ItemStack itemStack, ClickAction clickAction,
                       ChestMenu menu, int page);
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @NullMarked
        interface OpAction extends Action, PermissibleAction {
            static OpAction of(String key, String name, Material material, ActionHandle handle) {
                return new OpAction() {
                    @Override
                    public Material material() {
                        return material;
                    }

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public NamespacedKey getKey() {
                        return KeyUtil.newKey(key);
                    }

                    @Override
                    public boolean click(JEGSlimefunGuideImplementation guide, Player player, int slot,
                                         @Nullable SlimefunItem slimefunItem, ItemStack itemStack,
                                         ClickAction clickAction, ChestMenu menu, int page) {
                        handle.click(guide, player, slot, slimefunItem, itemStack, clickAction, menu, page);
                        return false;
                    }
                };
            }

            @Override
            default boolean hasPermission(Player player) {
                return player.isOp() || player.hasPermission("slimefun.cheat.items");
            }
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @NullMarked
        @SuppressWarnings("unused")
        interface Action extends BaseAction {
            static Action of(String key, String name, Material material, ActionHandle handle) {
                return new Action() {
                    @Override
                    public Material material() {
                        return material;
                    }

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public NamespacedKey getKey() {
                        return KeyUtil.newKey(key);
                    }

                    @Override
                    public boolean click(JEGSlimefunGuideImplementation guide, Player player, int slot,
                                         @Nullable SlimefunItem slimefunItem, ItemStack itemStack,
                                         ClickAction clickAction, ChestMenu menu, int page) {
                        handle.click(guide, player, slot, slimefunItem, itemStack, clickAction, menu, page);
                        return false;
                    }
                };
            }

            @Override
            default ActionKey parent() {
                return ACTION_KEY;
            }

            @SuppressWarnings("SameReturnValue")
            boolean click(JEGSlimefunGuideImplementation guide, Player player, int slot,
                          @Nullable SlimefunItem slimefunItem, ItemStack itemStack, ClickAction clickAction,
                          ChestMenu menu, int page);
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @NullMarked
        class Bookmark extends Normal {
            public static final ObjectImmutableList<Action> listActions = ObjectImmutableList.of(
                    Action.of(
                            "right-click", "删除标记的物品", Material.BARREL, (guide, player, slot, slimefunItem, item,
                                                                               action, menu, page) -> {
                                PlayerProfile playerProfile = PlayerProfile.find(player).orElse(null);
                                if (playerProfile == null) return;
                                if (slimefunItem == null) slimefunItem = SlimefunItem.getByItem(item);
                                if (slimefunItem == null) return;

                                GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                                JustEnoughGuide.getBookmarkManager().removeBookmark(player, slimefunItem);

                                List<com.balugaq.jeg.api.objects.collection.data.Bookmark> items =
                                        JustEnoughGuide.getBookmarkManager().getBookmarkedItems(player);
                                if (items == null || items.isEmpty()) {
                                    player.closeInventory();
                                    return;
                                }
                                new BookmarkGroup(guide, items).open(player, playerProfile, guide.getMode());
                            }
                    )
            );

            @Override
            public Material material() {
                return Material.BOOKSHELF;
            }

            @Override
            public String name() {
                return "书签";
            }

            @Override
            public ObjectImmutableList<Action> listActions() {
                return merge(super.listActions(), listActions);
            }

            @Override
            public ClickHandler create(JEGSlimefunGuideImplementation guide, ChestMenu menu, int page,
                                       @Nullable SlimefunItem slimefunItem) {
                return withItem(slimefunItem, (event, player, slot, s, action) -> {
                    ItemStack item = event.getCurrentItem();
                    if (item == null) return false;
                    ClickType clickType = event.getClick();
                    if (clickType == ClickType.DOUBLE_CLICK) return false;
                    // 注入右键
                    if (clickType == ClickType.RIGHT) {
                        return findAction(player, "right-click").click(
                                guide, player, slot, slimefunItem, item,
                                action, menu, page
                        );
                    }

                    return super.create(guide, menu, page, slimefunItem).onClick(event, player, slot, item, action);
                });
            }
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @NullMarked
        class ItemMark extends Normal {
            public static final ObjectImmutableList<Action> listActions = ObjectImmutableList.of(
                    Action.of(
                            "left-click", "物品标记", Material.WRITABLE_BOOK, (guide, player, slot, slimefunItem, item,
                                                                               action, menu, page) -> {
                                if (slimefunItem == null) slimefunItem = SlimefunItem.getByItem(item);
                                if (slimefunItem == null) return;
                                SlimefunItem finalSlimefunItem = slimefunItem;
                                EventUtil.callEvent(new GuideEvents.CollectItemEvent(
                                        player, item, slot, action, menu
                                        , guide
                                )).ifSuccess(() -> {
                                    JustEnoughGuide.getBookmarkManager().addBookmark(player, finalSlimefunItem);
                                    player.sendMessage(ChatColor.GREEN + "已添加到收藏列表!");
                                    player.playSound(player.getLocation(), Sounds.COLLECTED_ITEM, 1f, 1f);

                                    return false;
                                });
                            }
                    )
            );

            @Override
            public Material material() {
                return Material.PAPER;
            }

            @Override
            public String name() {
                return "标记物品";
            }

            @Override
            public ObjectImmutableList<Action> listActions() {
                return merge(super.listActions(), listActions);
            }

            @Override
            public ClickHandler create(JEGSlimefunGuideImplementation guide, ChestMenu menu, int page,
                                       @Nullable SlimefunItem slimefunItem) {
                return withItem(slimefunItem, (event, player, slot, cursor, action) -> {
                    ItemStack item = event.getCurrentItem();
                    if (item == null) return false;
                    ClickType clickType = event.getClick();
                    if (clickType == ClickType.DOUBLE_CLICK) return false;
                    // 注入左键
                    if (clickType == ClickType.LEFT) {
                        return findAction(player, "left-click").click(
                                guide, player, slot, slimefunItem, item, action
                                , menu, page
                        );
                    }

                    return Normal.create(guide, menu, page, slimefunItem).onClick(event, player, slot, item, action);
                });
            }
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @NullMarked
        class Research implements Item {
            public static final ObjectImmutableList<Action> listActions = ObjectImmutableList.of(
                    Action.of(
                            "default", "研究物品", Material.ENCHANTED_BOOK, (guide, player, slot, sf, item, action, menu,
                                                                             page) -> {
                                String id =
                                        item.getItemMeta().getPersistentDataContainer().get(JEGSlimefunGuideImplementation.UNLOCK_ITEM_KEY, PersistentDataType.STRING);
                                if (id == null) return;
                                SlimefunItem slimefunItem = SlimefunItem.getById(id);
                                if (slimefunItem == null) return;
                                io.github.thebusybiscuit.slimefun4.api.researches.Research research =
                                        slimefunItem.getResearch();
                                if (research == null) return;
                                PlayerProfile playerProfile = PlayerProfile.find(player).orElse(null);
                                if (playerProfile == null) return;

                                research.unlockFromGuide(
                                        guide,
                                        player,
                                        playerProfile,
                                        slimefunItem,
                                        slimefunItem.getItemGroup(),
                                        findPage(slimefunItem)
                                );
                            }
                    )
            );

            @Override
            public Material material() {
                return Material.BARRIER;
            }

            @Override
            public String name() {
                return "研究";
            }

            @Override
            public ObjectImmutableList<Action> listActions() {
                return listActions;
            }

            @Override
            public ClickHandler create(JEGSlimefunGuideImplementation guide, ChestMenu menu, int page,
                                       @Nullable SlimefunItem slimefunItem) {
                return withItem(slimefunItem, (event, player, slot, cursor, action) -> EventUtil.callEvent(new GuideEvents.ResearchItemEvent(player, event.getCurrentItem(), slot, action, menu, guide)).ifSuccess(() -> {
                    ItemStack item = event.getCurrentItem();
                    if (item == null) return false;
                    if (event.getClick() == ClickType.DOUBLE_CLICK) return false;
                    return findAction(player, "default").click(
                            guide, player, slot, slimefunItem, item, action, menu,
                            page
                    );
                }));
            }

            private static int findPage(SlimefunItem slimefunItem) {
                var group = slimefunItem.getItemGroup();
                if (!(group instanceof FlexItemGroup)) {
                    var items = group.getItems();
                    int idx = items.indexOf(slimefunItem);
                    if (idx == -1) return 1;
                    return idx / 36 + 1;
                }

                return 1;
            }
        }

        /**
         * @author balugaq
         * @since 2.0
         */
        @NullMarked
        @SuppressWarnings("CodeBlock2Expr")
        class Normal implements Item {
            public static final ObjectImmutableList<Action> listActions = ObjectImmutableList.of(
                    Action.of(
                            "f", "搜索配方展示物品的名字涉及此物品的名字的物品", Material.FURNACE, (guide, player, slot, slimefunItem, item
                                    , clickAction, menu, page) -> {
                                String itemName = ItemStackHelper.getDisplayName(item).trim();
                                while (itemName.contains(" ")) itemName = itemName.substring(0, itemName.indexOf(" "));

                                player.chat("/sf search " + FilterType.BY_DISPLAY_ITEM_NAME.getFirstSymbol() + ChatColor.stripColor(itemName));
                            }
                    ),
                    Action.of(
                            "q", "分享物品", Material.CLOCK, (guide, player, slot, slimefunItem, item, clickAction, menu,
                                                              page) -> {
                                share(player, ItemStackHelper.getDisplayName(item).trim());
                            }
                    ),
                    Action.of(
                            "right-click", "搜索物品作用", Material.LIGHT, (guide, player, slot, slimefunItem, item,
                                                                            clickAction, menu, page) -> {
                                String itemName = ItemStackHelper.getDisplayName(item).trim();
                                while (itemName.contains(" ")) itemName = itemName.substring(0, itemName.indexOf(" "));

                                player.chat("/sf search " + FilterType.BY_RECIPE_ITEM_NAME.getFirstSymbol() + ChatColor.stripColor(itemName));
                            }
                    ),
                    Action.of(
                            "shift-left-click", "打开物品所在物品组/OP: 取下物品", Material.CAULDRON, (guide, player, slot, slimefunItem,
                                                                                          item, clickAction, menu,
                                                                                          p2) -> {
                                if (slimefunItem == null) slimefunItem = SlimefunItem.getByItem(item);
                                if (slimefunItem == null) return;

                                if (player.isOp() || player.hasPermission("slimefun.cheat.items")) {
                                    int amount = 1;
                                    if (clickAction.isShiftClicked()) amount = item.getMaxStackSize();

                                    ItemStack itemStack = slimefunItem == null ? item :
                                            Converter.getItem(slimefunItem.getItem());
                                    player.getInventory().addItem(StackUtils.getAsQuantity(itemStack, amount));
                                    return;
                                }

                                final io.github.thebusybiscuit.slimefun4.api.items.ItemGroup itemGroup =
                                        slimefunItem.getItemGroup();
                                AtomicInteger page = new AtomicInteger(1);
                                if (GuideUtil.isTaggedGroupType(itemGroup)) {
                                    page.set((itemGroup.getItems().indexOf(slimefunItem) / 36) + 1);
                                }
                                EventUtil.callEvent(new GuideEvents.GroupLinkButtonClickEvent(
                                        player, item, slot,
                                        clickAction, menu,
                                        guide
                                )).ifSuccess(() -> {
                                    PlayerProfile.get(
                                            player, profile -> guide.openItemGroup(
                                                    profile, itemGroup,
                                                    page.get()
                                            )
                                    );
                                    return false;
                                });
                            }
                    ),
                    Action.of(
                            "shift-right-click", "查找相关物品", Material.NAME_TAG, (guide, player, slot, slimefunItem,
                                                                                     item, clickAction, menu, page) -> {
                                String itemName = ItemStackHelper.getDisplayName(item).trim();
                                while (itemName.contains(" ")) itemName = itemName.substring(0, itemName.indexOf(" "));
                                player.chat("/sf search " + ChatColor.stripColor(itemName));
                            }
                    ),
                    OpAction.of(
                            "clone-item", "作弊模式 - 复制物品", Material.COMMAND_BLOCK, (guide, player, slot, slimefunItem,
                                                                                          item, clickAction, menu,
                                                                                          page) -> {
                                ItemStack cursor = player.getItemOnCursor();
                                if (cursor == null || cursor.getType() == Material.AIR) {
                                    if (slimefunItem instanceof MultiBlockMachine) {
                                        Slimefun.getLocalization().sendMessage(player, "guide.cheat.no-multiblocks");
                                        return;
                                    }

                                    ItemStack itemStack = slimefunItem == null ? item :
                                            Converter.getItem(slimefunItem.getItem());
                                    player.setItemOnCursor(StackUtils.getAsQuantity(
                                            itemStack,
                                            itemStack.getMaxStackSize()
                                    ));
                                }
                            }
                    ),
                    OpAction.of(
                            "take-item", "作弊模式 - 取出物品", Material.STRUCTURE_BLOCK, (guide, player, slot, slimefunItem,
                                                                                           item, clickAction, menu,
                                                                                           page) -> {
                                int amount = 1;
                                if (clickAction.isShiftClicked()) amount = item.getMaxStackSize();

                                ItemStack itemStack = slimefunItem == null ? item :
                                        Converter.getItem(slimefunItem.getItem());
                                player.getInventory().addItem(StackUtils.getAsQuantity(itemStack, amount));
                            }
                    ),
                    OpAction.of(
                            "copy-sf-id", "作弊模式 - 复制粘液物品ID", Material.MAGENTA_GLAZED_TERRACOTTA, (guide, player, slot, slimefunItem, item, clickAction, menu, page) -> {
                                if (slimefunItem == null) {
                                    return;
                                }
                                String s = slimefunItem.getId();
                                ClipboardUtil.send(player, "&e点击复制粘液物品的ID", s, s);
                            }
                    ),
                    Action.of(
                            "default", "默认", Material.COMPASS, (guide, player, slot, slimefunItem, item, clickAction,
                                                                  menu, page) -> {
                                PlayerProfile profile = PlayerProfile.find(player).orElse(null);
                                if (profile == null) return;
                                if (slimefunItem != null) {
                                    guide.displayItem(profile, slimefunItem, true);
                                } else {
                                    guide.displayItem(profile, item, 0, true);
                                }
                            }
                    ),
                    Action.of("none", "无操作", Material.BARRIER, (guide, player, slot, slimefunItem, item, clickAction, menu, page) -> {
                    })
            );

            @Override
            public Material material() {
                return Material.MOSS_BLOCK;
            }

            @Override
            public String name() {
                return "常规";
            }

            @Override
            public ObjectImmutableList<Action> listActions() {
                return listActions;
            }
        }

        /**
         * @author balugaq
         * @since 2.1
         */
        @NullMarked
        interface ClickHandler extends BaseClickHandler {
            @Nullable SlimefunItem getSlimefunItem();
        }

        static ClickHandler withItem(@Nullable SlimefunItem sf, BaseClickHandler base) {
            return new ClickHandler() {
                @Override
                @Nullable
                public SlimefunItem getSlimefunItem() {
                    return sf;
                }

                @Override
                public boolean onClick(final InventoryClickEvent inventoryClickEvent, final Player player, final int i, final ItemStack itemStack, final ClickAction clickAction) {
                    return base.onClick(inventoryClickEvent, player, i, itemStack, clickAction);
                }
            };
        }
    }

    /**
     * @author balugaq
     * @since 2.0
     */
    @NullMarked
    @FunctionalInterface
    interface BaseClickHandler extends ChestMenu.AdvancedMenuClickHandler {
        static BaseClickHandler deny() {
            return (event, player, slot, item, action) -> false;
        }

        @Override
        default boolean onClick(Player player, int i, ItemStack itemStack, ClickAction clickAction) {
            return false;
        }
    }

    final class Holder {
        public static final Item Item = () -> Material.ITEM_FRAME;
        public static final ItemGroup ItemGroup = () -> Material.CAMPFIRE;
        public static final RecipeType RecipeType = () -> Material.CRAFTING_TABLE;
    }
    // @formatter:on
}
