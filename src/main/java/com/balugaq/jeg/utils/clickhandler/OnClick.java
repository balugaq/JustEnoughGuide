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

package com.balugaq.jeg.utils.clickhandler;

import com.balugaq.jeg.api.groups.BookmarkGroup;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.objects.collection.cooldown.FrequencyWatcher;
import com.balugaq.jeg.api.objects.enums.FilterType;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.option.ShareInGuideOption;
import com.balugaq.jeg.implementation.option.ShareOutGuideOption;
import com.balugaq.jeg.utils.ClipboardUtil;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.StackUtils;
import com.balugaq.jeg.utils.compatibility.Sounds;
import com.balugaq.jeg.utils.platform.PlatformUtil;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"deprecation", "IfStatementWithIdenticalBranches"})
@NullMarked
public interface OnClick {
    MessageFormat SHARED_ITEM_MESSAGE = new MessageFormat(ChatColors.color("&a{0} &e分享了 &7[{1}&r&7]&e <点击搜索>"));
    String CLICK_TO_SEARCH = ChatColors.color("&e点击搜索");
    FrequencyWatcher<UUID> SHARING_WATCHER = new FrequencyWatcher<>(1, TimeUnit.MINUTES, 10, 5000);

    static void preset(ChestMenu menu) {
        menu.setEmptySlotsClickable(false);
        menu.addPlayerInventoryClickHandler((p, s, i, a) ->
                p.isOp() || p.hasPermission("slimefun.cheat.items")
        );
        menu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sounds.GUIDE_BUTTON_CLICK_SOUND, 1, 1));
    }

    static boolean checkShareCooldown(@NotNull Player player) {
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

    static void share(Player player, String itemName) {
        if (!checkShareCooldown(player)) return;
        if (!ShareOutGuideOption.isEnabled(player)) return;

        String s = itemName;
        s = s.substring(0, itemName.indexOf(" "));
        String playerName = player.getName();

        if (PlatformUtil.isPaper()) {
            String sharedMessage = SHARED_ITEM_MESSAGE.format(new Object[]{playerName, ChatColors.color(itemName)});

            Component base = LegacyComponentSerializer.legacySection().deserialize(sharedMessage)
                    .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(Component.text(CLICK_TO_SEARCH)));
            Component clickToSearch = base.clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/sf search " + s));
            Component clickToCopy = base.clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.COPY_TO_CLIPBOARD, itemName));
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (ShareInGuideOption.isEnabled(p)) {
                    if (p.hasPermission("slimefun.command.search")) {
                        p.sendMessage(clickToSearch);
                    } else {
                        p.sendMessage(clickToCopy);
                    }
                }
            });
        } else {
            String sharedMessage = SHARED_ITEM_MESSAGE.format(new Object[]{playerName, ChatColors.color(itemName)});
            TextComponent msg = new TextComponent(sharedMessage);
            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(CLICK_TO_SEARCH)));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sf search " + s));

            Bukkit.getOnlinePlayers().forEach(p -> {
                if (ShareInGuideOption.isEnabled(p)) {
                    if (p.hasPermission("slimefun.command.search")) {
                        ClipboardUtil.send(p, msg);
                    } else {
                        ClipboardUtil.send(p, ClipboardUtil.makeComponent(sharedMessage, CLICK_TO_SEARCH, itemName));
                    }
                }
            });
        }
    }

    /**
     * 点击配方类型时:
     * Q建: 分享配方类型                       // Case 1 按下 Q 键
     * 右键: 查找使用此配方类型的物品: 搜索: $名字 // Case2 按下右键
     * Shift左键: 打开配方类型所在物品组（若有）   // Case3 按下 Shift+左键
     * Shift右键: 查找相关物品/机器: 搜索: 名字   // Case4 按下 Shift+右键
     */
    interface RecipeType extends OnClick {
        ObjectImmutableList<Action> listActions = ObjectImmutableList.of(
                Action.of("q", "分享配方类型", (guide, player, slot, recipeType, action, menu, page) -> {
                    String recipeTypeName = recipeType.getItem(player).getItemMeta().getDisplayName();
                    share(player, recipeTypeName);
                }),
                Action.of("right-click", "查找使用此配方类型的物品", (guide, player, slot, recipeType, action, menu, page) -> {
                    String recipeTypeName = recipeType.getItem(player).getItemMeta().getDisplayName();
                    player.chat("/sf search " + FilterType.BY_RECIPE_TYPE_NAME.getSymbol() + recipeTypeName);
                }),
                Action.of("shift-left", "打开配方类型所在物品组", (guide, player, slot, recipeType, action, menu, page) -> {
                    SlimefunItem machine = recipeType.getMachine();
                    if (machine != null) {
                        PlayerProfile profile = PlayerProfile.find(player).orElse(null);
                        if (profile == null) return;
                        guide.openItemGroup(profile, machine.getItemGroup(), 1);
                    }
                }),
                Action.of("shift-right", "查找相关物品/机器", (guide, player, slot, recipeType, action, menu, page) -> {
                    String recipeTypeName = recipeType.getItem(player).getItemMeta().getDisplayName();
                    player.chat("/sf search " + recipeTypeName);
                }),
                Action.of("default", "默认", (guide, player, slot, recipeType, action, menu, page) -> {
                })
        );

        static ObjectImmutableList<Action> listActions() {
            return listActions;
        }

        static Action findAction(Player player, String key) {
            for (Action action : listActions()) {
                String k = action.getKey().getKey();
                String remap = player.getPersistentDataContainer().get(KeyUtil.newKey("keybind-" + k), PersistentDataType.STRING);
                if (remap != null) k = remap;

                if (k.equals(key)) return action;
            }

            return new Action() {
                @Override
                public String name() {
                    return "Key Not Found";
                }

                @Override
                public boolean click(JEGSlimefunGuideImplementation guide, Player player, int slot, io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType recipeType, ClickAction clickAction, ChestMenu menu, int page) {
                    player.sendMessage("&c未找到按键: " + key);
                    return false;
                }

                @Override
                public @NotNull NamespacedKey getKey() {
                    return KeyUtil.newKey(key);
                }
            };
        }

        static ClickHandler create(JEGSlimefunGuideImplementation guide, ChestMenu menu, io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType recipeType) {
            return (event, player, slot, cursor, action) -> EventUtil.callEvent(new GuideEvents.RecipeTypeButtonClickEvent(player, event.getCurrentItem(), slot, action, menu, guide)).ifSuccess(() -> {
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
            });
        }

        @FunctionalInterface
        interface ClickHandler extends ChestMenu.AdvancedMenuClickHandler {
            @Override
            default boolean onClick(Player player, int i, ItemStack itemStack, ClickAction clickAction) {
                return false;
            }
        }

        @FunctionalInterface
        interface ActionHandle {
            void click(JEGSlimefunGuideImplementation guide, Player player, int slot, io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType recipeType, ClickAction clickAction, ChestMenu menu, int page);
        }

        interface Action extends Keyed {
            static Action of(String key, String name, ActionHandle handle) {
                return new Action() {

                    @Override
                    public @NotNull NamespacedKey getKey() {
                        return KeyUtil.newKey(key);
                    }

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public boolean click(JEGSlimefunGuideImplementation guide, Player player, int slot, io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType recipeType, ClickAction clickAction, ChestMenu menu, int page) {
                        handle.click(guide, player, slot, recipeType, clickAction, menu, page);
                        return false;
                    }
                };
            }

            String name();

            boolean click(JEGSlimefunGuideImplementation guide, Player player, int slot, io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType recipeType, ClickAction clickAction, ChestMenu menu, int page);
        }
    }

    /**
     * 点击物品时:
     * F键: 搜索配方展示物品的名字涉及此物品的名字的物品: 搜索: %名字 // Case1 按下 F 键
     * Q键: 分享物品                                         // Case2 按下 Q 键
     * 右键: 查找物品用途: 搜索: #名字                          // Case3 按下右键
     * Shift左键: 打开物品所在物品组                            // Case4 按下 Shift+左键
     * Shift右键: 查找相关物品/机器: 搜索: 名字                  // Case5 按下 Shift+右键
     * 有op权限:
     * (光标空+中键) 放光标上
     * (创造书 || 光标有物品) 放背包里
     * 显示界面                                              // Case6 以上条件均不符合
     */
    @SuppressWarnings("ConstantValue")
    interface Item extends OnClick {
        Item Normal = new Normal();
        Item ItemMark = new ItemMark();
        Item BookMark = new BookMark();
        Item Research = new Research();
        Item NoPermission = new NoPermission();

        default ClickHandler create(JEGSlimefunGuideImplementation guide, ChestMenu menu, int page) {
            return (event, player, slot, cursor, action) -> EventUtil.callEvent(new GuideEvents.ItemButtonClickEvent(player, event.getCurrentItem(), slot, action, menu, guide)).ifSuccess(() -> {
                ItemStack item = event.getCurrentItem();
                if (item == null) return false;
                ClickType clickType = event.getClick();
                // F键
                if (clickType == ClickType.SWAP_OFFHAND) {
                    return findAction(player, "f").click(guide, player, slot, item, action, menu, page);
                }
                // Q键
                if (clickType == ClickType.DROP || clickType == ClickType.CONTROL_DROP) {
                    return findAction(player, "q").click(guide, player, slot, item, action, menu, page);
                }
                // 右键
                if (clickType == ClickType.RIGHT) {
                    return findAction(player, "right-click").click(guide, player, slot, item, action, menu, page);
                }
                // Shift+左键
                if (clickType == ClickType.SHIFT_LEFT) {
                    return findAction(player, "shift-left-click").click(guide, player, slot, item, action, menu, page);
                }
                // Shift+右键
                if (clickType == ClickType.SHIFT_RIGHT) {
                    return findAction(player, "shift-right-click").click(guide, player, slot, item, action, menu, page);
                }
                // 有权限
                if (player.isOp() || player.hasPermission("slimefun.cheat.items")) {
                    if (event.getClick() == ClickType.MIDDLE && (cursor == null || cursor.getType() == Material.AIR)) {
                        return findAction(player, "clone-item").click(guide, player, slot, item, action, menu, page);
                    }
                    if (guide.getMode() == SlimefunGuideMode.CHEAT_MODE || (cursor != null && cursor.getType() != Material.AIR)) {
                        return findAction(player, "take-item").click(guide, player, slot, item, action, menu, page);
                    }
                }

                return findAction(player, "default").click(guide, player, slot, item, action, menu, page);
            });
        }

        ObjectImmutableList<Action> listActions();

        default Action findAction(Player player, String key) {
            for (Action action : listActions()) {
                String k = action.getKey().getKey();
                String remap = player.getPersistentDataContainer().get(KeyUtil.newKey("keybind-" + k), PersistentDataType.STRING);
                if (remap != null) k = remap;

                if (k.equals(key)) return action;
            }

            return new Action() {
                @Override
                public String name() {
                    return "Key Not Found";
                }

                @Override
                public boolean click(JEGSlimefunGuideImplementation guide, Player player, int slot, ItemStack itemStack, ClickAction clickAction, ChestMenu menu, int page) {
                    player.sendMessage("&c未找到按键: " + key);
                    return false;
                }

                @Override
                public @NotNull NamespacedKey getKey() {
                    return KeyUtil.newKey(key);
                }
            };
        }

        @FunctionalInterface
        interface ClickHandler extends ChestMenu.AdvancedMenuClickHandler {
            @Override
            boolean onClick(InventoryClickEvent event, Player player, int slot, ItemStack cursor, ClickAction action);

            @Override
            default boolean onClick(Player player, int slot, ItemStack itemStack, ClickAction action) {
                return false;
            }
        }

        @FunctionalInterface
        interface ActionHandle {
            void click(JEGSlimefunGuideImplementation guide, Player player, int slot, ItemStack itemStack, ClickAction clickAction, ChestMenu menu, int page);
        }

        interface Action extends Keyed {
            static Action of(String key, String name, ActionHandle handle) {
                return new Action() {

                    @Override
                    public @NotNull NamespacedKey getKey() {
                        return KeyUtil.newKey(key);
                    }

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public boolean click(JEGSlimefunGuideImplementation guide, Player player, int slot, ItemStack itemStack, ClickAction clickAction, ChestMenu menu, int page) {
                        handle.click(guide, player, slot, itemStack, clickAction, menu, page);
                        return false;
                    }
                };
            }

            String name();

            boolean click(JEGSlimefunGuideImplementation guide, Player player, int slot, ItemStack itemStack, ClickAction clickAction, ChestMenu menu, int page);
        }

        class BookMark implements Item {
            public static ObjectImmutableList<Action> listActions = ObjectImmutableList.of(
                    Action.of("book-mark", "查看标记的物品", (guide, player, slot, item, action, menu, page) -> {
                        PlayerProfile playerProfile = PlayerProfile.find(player).orElse(null);
                        if (playerProfile == null) return;
                        SlimefunItem slimefunItem = SlimefunItem.getByItem(item);
                        if (slimefunItem == null) return;

                        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                        JustEnoughGuide.getBookmarkManager().removeBookmark(player, slimefunItem);

                        List<SlimefunItem> items = JustEnoughGuide.getBookmarkManager().getBookmarkedItems(player);
                        if (items == null || items.isEmpty()) {
                            player.closeInventory();
                            return;
                        }
                        new BookmarkGroup(guide, player, items).open(player, playerProfile, guide.getMode());
                    })
            );

            @Override
            public ClickHandler create(JEGSlimefunGuideImplementation guide, ChestMenu menu, int page) {
                return (event, player, slot, cursor, action) -> {
                    ItemStack item = event.getCurrentItem();
                    if (item == null) return false;
                    // 注入右键
                    if (event.getClick() == ClickType.RIGHT) {
                        findAction(player, "book-mark").click(guide, player, slot, item, action, menu, page);
                    }

                    return Item.super.create(guide, menu, page).onClick(event, player, slot, item, action);
                };
            }

            @Override
            public ObjectImmutableList<Action> listActions() {
                return listActions;
            }
        }

        class ItemMark implements Item {
            public static ObjectImmutableList<Action> listActions = ObjectImmutableList.of(
                    Action.of("item-mark", "物品标记", (guide, player, slot, item, action, menu, page) -> {
                        SlimefunItem slimefunItem = SlimefunItem.getByItem(item);
                        if (slimefunItem == null) return;
                        EventUtil.callEvent(new GuideEvents.CollectItemEvent(player, item, slot, action, menu, guide)).ifSuccess(() -> {
                            JustEnoughGuide.getBookmarkManager().addBookmark(player, slimefunItem);
                            player.sendMessage(ChatColor.GREEN + "已添加到收藏列表!");
                            player.playSound(player.getLocation(), Sounds.COLLECTED_ITEM, 1f, 1f);

                            return false;
                        });
                    })
            );

            @Override
            public ClickHandler create(JEGSlimefunGuideImplementation guide, ChestMenu menu, int page) {
                return (event, player, slot, cursor, action) -> {
                    ItemStack item = event.getCurrentItem();
                    if (item == null) return false;
                    // 注入左键
                    if (event.getClick() == ClickType.LEFT) {
                        findAction(player, "item-mark").click(guide, player, slot, item, action, menu, page);
                    }

                    return Item.super.create(guide, menu, page).onClick(event, player, slot, item, action);
                };
            }

            @Override
            public ObjectImmutableList<Action> listActions() {
                return listActions;
            }
        }

        class Research implements Item {
            public static ObjectImmutableList<Action> listActions = ObjectImmutableList.of(
                    Action.of("research", "研究物品", (guide, player, slot, item, action, menu, page) -> {
                        String id = item.getItemMeta().getPersistentDataContainer().get(JEGSlimefunGuideImplementation.UNLOCK_ITEM_KEY, PersistentDataType.STRING);
                        if (id == null) return;
                        SlimefunItem slimefunItem = SlimefunItem.getById(id);
                        if (slimefunItem == null) return;
                        io.github.thebusybiscuit.slimefun4.api.researches.Research research = slimefunItem.getResearch();
                        if (research == null) return;
                        PlayerProfile playerProfile = PlayerProfile.find(player).orElse(null);
                        if (playerProfile == null) return;

                        research.unlockFromGuide(
                                guide,
                                player,
                                playerProfile,
                                slimefunItem,
                                slimefunItem.getItemGroup(),
                                page);
                    })
            );

            @Override
            public ClickHandler create(JEGSlimefunGuideImplementation guide, ChestMenu menu, int page) {
                return (event, player, slot, cursor, action) -> EventUtil.callEvent(new GuideEvents.ResearchItemEvent(player, event.getCurrentItem(), slot, action, menu, guide)).ifSuccess(() -> {
                    ItemStack item = event.getCurrentItem();
                    if (item == null) return false;
                    return findAction(player, "research").click(guide, player, slot, item, action, menu, page);
                });
            }

            @Override
            public ObjectImmutableList<Action> listActions() {
                return listActions;
            }
        }

        @SuppressWarnings("CodeBlock2Expr")
        class Normal implements Item {
            public static ObjectImmutableList<Action> listActions = ObjectImmutableList.of(
                    Action.of("f", "搜索配方展示物品的名字涉及此物品的名字的物品", (guide, player, slot, item, clickAction, menu, page) -> {
                        String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName()).trim();
                        while (itemName.contains(" ")) itemName = itemName.substring(0, itemName.indexOf(" "));

                        player.chat("/sf search " + FilterType.BY_DISPLAY_ITEM_NAME.getSymbol() + itemName);
                    }),
                    Action.of("q", "分享物品", (guide, player, slot, item, clickAction, menu, page) -> {
                        share(player, item.getItemMeta().getDisplayName().trim());
                    }),
                    Action.of("right-click", "搜索物品作用", (guide, player, slot, item, clickAction, menu, page) -> {
                        String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName()).trim();
                        while (itemName.contains(" ")) itemName = itemName.substring(0, itemName.indexOf(" "));

                        player.chat("/sf search " + FilterType.BY_RECIPE_ITEM_NAME.getSymbol() + itemName);
                    }),
                    Action.of("shift-left-click", "打开物品所在物品组", (guide, player, slot, item, clickAction, menu, p2) -> {
                        final SlimefunItem sfItem = SlimefunItem.getByItem(item);
                        if (sfItem == null) {
                            return;
                        }

                        final ItemGroup itemGroup = sfItem.getItemGroup();
                        AtomicInteger page = new AtomicInteger(1);
                        if (GuideUtil.isTaggedGroupType(itemGroup)) {
                            page.set((itemGroup.getItems().indexOf(sfItem) / 36) + 1);
                        }
                        EventUtil.callEvent(new GuideEvents.GroupLinkButtonClickEvent(player, item, slot, clickAction, menu, guide)).ifSuccess(() -> {
                            PlayerProfile.get(player, profile -> guide.openItemGroup(profile, itemGroup, page.get()));
                            return false;
                        });
                    }),
                    Action.of("shift-right-click", "查找相关物品", (guide, player, slot, item, clickAction, menu, page) -> {
                        String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName()).trim();
                        while (itemName.contains(" ")) itemName = itemName.substring(0, itemName.indexOf(" "));
                        player.chat("/sf search " + itemName);
                    }),
                    Action.of("clone-item", "作弊模式 - 复制物品", (guide, player, slot, item, clickAction, menu, page) -> {
                        if (!player.isOp() && !player.hasPermission("slimefun.cheat.items")) return;
                        ItemStack cursor = player.getItemOnCursor();
                        if (cursor == null || cursor.getType() == Material.AIR) {
                            SlimefunItem slimefunItem = SlimefunItem.getByItem(item);
                            if (slimefunItem instanceof MultiBlockMachine) {
                                Slimefun.getLocalization().sendMessage(player, "guide.cheat.no-multiblocks");
                                return;
                            }

                            player.setItemOnCursor(StackUtils.getAsQuantity(item, item.getMaxStackSize()));
                        }
                    }),
                    Action.of("take-item", "作弊模式 - 取出物品", (guide, player, slot, item, clickAction, menu, page) -> {
                        if (!player.isOp() && !player.hasPermission("slimefun.cheat.items")) return;
                        int amount = 1;
                        if (clickAction.isShiftClicked()) amount = item.getMaxStackSize();

                        player.getInventory().addItem(StackUtils.getAsQuantity(item, amount));
                    }),
                    Action.of("default", "默认", (guide, player, slot, item, clickAction, menu, page) -> {
                        PlayerProfile profile = PlayerProfile.find(player).orElse(null);
                        if (profile == null) return;
                        guide.displayItem(profile, item, 0, true);
                    })
            );

            @Override
            public ObjectImmutableList<Action> listActions() {
                return listActions;
            }
        }

        class NoPermission implements Item {
            public static final ObjectImmutableList<Action> listActions = ObjectImmutableList.of();

            @Override
            public ClickHandler create(JEGSlimefunGuideImplementation guide, ChestMenu menu, int page) {
                return (event, player, slot, item, action) -> false;
            }

            @Override
            public ObjectImmutableList<Action> listActions() {
                return listActions;
            }
        }
    }
}
