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

package com.balugaq.jeg.api;

import com.balugaq.jeg.api.groups.RTSSearchGroup;
import com.balugaq.jeg.api.objects.collection.data.Bookmark;
import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.api.recipe_complete.source.RecipeCompleteProvider;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.core.managers.BookmarkManager;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * JustEnoughGuide 的统一 API 门面。
 *
 * <p>为其它插件提供配方补全、实时搜索（RTS）、书签三大功能的稳定入口，
 * 屏蔽内部的 Provider / Listener / Manager 细节。</p>
 *
 * <p>用法示例：</p>
 * <pre>{@code
 * JustEnoughGuideAPI.getBookmarkAPI().addBookmark(player, slimefunItem);
 * JustEnoughGuideAPI.getRTSAPI().openRTS(player, SlimefunGuideMode.SURVIVAL_MODE);
 * }</pre>
 *
 * @author balugaq
 * @since 2.1
 */
@SuppressWarnings("unused")
@NullMarked
public final class JustEnoughGuideAPI {
    private static final RecipeCompletionAPI RECIPE_COMPLETION_API = new RecipeCompletionAPI();
    private static final RTSAPI RTS_API = new RTSAPI();
    private static final BookmarkAPI BOOKMARK_API = new BookmarkAPI();

    private JustEnoughGuideAPI() {
    }

    public static RecipeCompletionAPI getRecipeCompletionAPI() {
        return RECIPE_COMPLETION_API;
    }

    public static RTSAPI getRTSAPI() {
        return RTS_API;
    }

    public static BookmarkAPI getBookmarkAPI() {
        return BOOKMARK_API;
    }

    /**
     * 配方补全 API。
     *
     * @author balugaq
     * @since 2.1
     */
    @NullMarked
    public static final class RecipeCompletionAPI {
        private RecipeCompletionAPI() {
        }

        /**
         * 判断配方补全功能是否开启。
         */
        public boolean isEnabled() {
            return JustEnoughGuide.getConfigManager().isRecipeComplete();
        }

        /**
         * 创建一次配方补全会话并让玩家打开配方补全界面（Slimefun 侧）。
         * 玩家在指南中选中物品后，补全的原料会被放入 {@code menu} 指定的容器。
         *
         * @param menu            补全目标容器（即配方所在机器的 BlockMenu）
         * @param player          进行补全的玩家
         * @param clickAction     触发补全的点击方式（左右键 / 是否 Shift）
         * @param ingredientSlots 配方原料所在的槽位
         * @param unordered       配方是否为无序配方
         * @param recipeDepth     补全递归深度
         * @return 会话是否成功创建并打开
         */
        public boolean openSlimefun(BlockMenu menu, Player player, ClickAction clickAction, int[] ingredientSlots, boolean unordered, int recipeDepth) {
            RecipeCompleteSession session = RecipeCompleteSession.create(menu, player, clickAction, ingredientSlots, unordered, recipeDepth);
            if (session == null) {
                return false;
            }
            RecipeCompleteProvider.openSlimefun(session);
            return true;
        }

        /**
         * 创建一次配方补全会话并让玩家打开配方补全界面（原版侧）。
         * 玩家在原版合成界面中选中物品后，补全的原料会被放入 {@code inventory} 指定的容器。
         *
         * @see #openSlimefun(BlockMenu, Player, ClickAction, int[], boolean, int)
         */
        public boolean openVanilla(org.bukkit.block.Block block, org.bukkit.inventory.Inventory inventory, Player player, ClickAction clickAction, int[] ingredientSlots, boolean unordered, int recipeDepth) {
            RecipeCompleteSession session = RecipeCompleteSession.create(block, inventory, player, clickAction, ingredientSlots, unordered, recipeDepth);
            if (session == null) {
                return false;
            }
            RecipeCompleteProvider.openVanilla(session);
            return true;
        }

        /**
         * 获取玩家当前进行中的配方补全会话。
         *
         * @return 会话；玩家没有进行中的补全会话时返回 {@code null}
         */
        @Nullable
        public RecipeCompleteSession getSession(Player player) {
            return RecipeCompleteSession.getSession(player);
        }

        /**
         * 获取玩家上次（最近一次）在指南中点击进行配方补全的物品。
         *
         * @return 上次补全的物品；玩家从未补全过时返回 {@code null}
         */
        @Nullable
        public ItemStack getLastCompletedItem(Player player) {
            var event = RecipeCompletableListener.getLastEvent(player.getUniqueId());
            return event == null ? null : event.getClickedItem();
        }

        /**
         * 取消玩家当前进行中的配方补全会话。
         */
        public void cancel(Player player) {
            RecipeCompleteSession.cancel(player);
        }
    }

    /**
     * 实时搜索（RTS）API。
     *
     * @author balugaq
     * @since 2.1
     */
    @NullMarked
    public static final class RTSAPI {
        private RTSAPI() {
        }

        /**
         * 判断实时搜索功能是否开启。
         */
        public boolean isEnabled() {
            return JustEnoughGuide.getConfigManager().isRTSSearch();
        }

        /**
         * 让玩家打开实时搜索界面（铁砧输入框）。
         *
         * @param player    要打开界面的玩家
         * @param guideMode 指南模式（生存 / 抄答案），决定搜索结果的行为
         * @return 是否成功打开
         */
        public boolean openRTS(Player player, SlimefunGuideMode guideMode) {
            return openRTS(player, guideMode, null);
        }

        /**
         * 让玩家打开实时搜索界面，并预填搜索词。
         *
         * @param presetSearchTerm 预填的搜索词；{@code null} 表示不预填
         * @return 是否成功打开
         */
        public boolean openRTS(Player player, SlimefunGuideMode guideMode, @Nullable String presetSearchTerm) {
            if (!isEnabled()) {
                return false;
            }
            return RTSSearchGroup.newRTSInventoryFor(player, guideMode, presetSearchTerm) != null;
        }
    }

    /**
     * 书签 API。
     *
     * @author balugaq
     * @since 2.1
     */
    @NullMarked
    public static final class BookmarkAPI {
        private BookmarkAPI() {
        }

        /**
         * 获取玩家的所有书签。
         */
        public List<Bookmark> getBookmarks(Player player) {
            return manager().getBookmarkedItems(player);
        }

        /**
         * 给玩家的书签增加一个 Slimefun 物品。
         */
        public void addBookmark(Player player, SlimefunItem slimefunItem) {
            manager().addBookmark(player, slimefunItem);
        }

        /**
         * 给玩家的书签增加一个物品组。
         */
        public void addBookmark(Player player, ItemGroup itemGroup) {
            manager().addBookmark(player, itemGroup);
        }

        /**
         * 从玩家的书签中删除一个 Slimefun 物品。
         */
        public void removeBookmark(Player player, SlimefunItem slimefunItem) {
            manager().removeBookmark(player, slimefunItem);
        }

        /**
         * 从玩家的书签中删除一个物品组。
         */
        public void removeBookmark(Player player, ItemGroup itemGroup) {
            manager().removeBookmark(player, itemGroup);
        }

        /**
         * 清空玩家的所有书签。
         */
        public void clearBookmarks(Player player) {
            manager().clearBookmarks(player);
        }

        private BookmarkManager manager() {
            return JustEnoughGuide.getBookmarkManager();
        }
    }
}
