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

package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.anvil.AnvilMenu;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.objects.events.RTSEvents;
import com.balugaq.jeg.core.listeners.RTSListener;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author balugaq
 * @since 1.3
 */
@SuppressWarnings({"unused"})
@NotDisplayInSurvivalMode
@NotDisplayInCheatMode
@Getter
@NullMarked
public class RTSSearchGroup extends FlexItemGroup {
    public static final ItemStack PLACEHOLDER = Converter.getItem(
            Converter.getItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "&a", "&a", "&a"),
            meta -> meta.getPersistentDataContainer()
                    .set(RTSListener.FAKE_ITEM_KEY, PersistentDataType.STRING, "____JEG_FAKE_ITEM____")
    );
    public static final Map<Player, SearchGroup> RTS_SEARCH_GROUPS = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> RTS_PAGES = new ConcurrentHashMap<>();
    public static final Map<Player, AnvilInventory> RTS_ANVIL_INVENTORIES = new ConcurrentHashMap<>();
    public static final Function<Player, ItemStack> BACK_ICON =
            (player) -> ChestMenuUtils.getBackButton(player, "", "&f左键: &7返回上一页", "&fShift + 左键: &7返回主菜单");
    private final AnvilInventory anvilInventory;
    private final int page;

    public RTSSearchGroup(AnvilInventory anvilInventory) {
        this(anvilInventory, 1);
    }

    public RTSSearchGroup(AnvilInventory anvilInventory, int page) {
        super(KeyUtil.random(), ItemStackUtil.barrier());
        this.anvilInventory = anvilInventory;
        this.page = page;
    }

    public static void openRTSInventoryFor(Player player) {
        AnvilMenu holder = AnvilMenu.create();
        holder.setGuiItem(0, BACK_ICON.apply(player), (p, s, i, action) -> {
            // back button clicked
            PlayerProfile profile = PlayerProfile.find(player).orElse(null);
            if (profile == null) return false;

            GuideHistory history = profile.getGuideHistory();
            if (action.isShiftClicked()) {
                GuideUtil.getLastGuide(player).openMainMenu(
                        profile,
                        history.getMainMenuPage()
                );
            } else {
                GuideUtil.goBack(history);
            }
            return false;
        });
        holder.setGuiItem(1, Models.INPUT_TEXT_ICON, (p, s, i, a) -> {
            SearchGroup rts = RTS_SEARCH_GROUPS.get(player);
            if (rts != null) {
                int oldPage = RTS_PAGES.getOrDefault(player, 1);
                int newPage = Math.max(1, oldPage - 1);
                callPageChangeEvent(player, oldPage, newPage);
            }
            return false;
        });
        holder.setGuiItem(2, ItemStackUtil.air(), (p, s, i, a) -> {
            // next page button clicked
            SearchGroup rts = RTS_SEARCH_GROUPS.get(player);
            if (rts != null) {
                int oldPage = RTS_PAGES.getOrDefault(player, 1);
                int newPage = Math.min(
                        (rts.slimefunItemList.size() - 1) / RTSListener.FILL_ORDER.length + 1, oldPage + 1);
                callPageChangeEvent(player, oldPage, newPage);
            }
            return false;
        });
        holder.onOpen(RTSListener::tryQuitRTS);
        holder.onClose((p, e) -> {
            RTSListener.tryQuitRTS(p);
        });
        holder.setPlayerInventoryClickHandler((p, s, itemStack, a) -> {
            if (!RTSListener.isRTSPlayer(p)) return false;
            if (itemStack == null || itemStack.getType() == Material.AIR) return false;

            SlimefunGuideMode mode = GuideUtil.getLastGuideMode(player);
            SlimefunGuideImplementation implementation = GuideUtil.getSlimefunGuide(mode);
            PlayerProfile profile = PlayerProfile.find(player).orElse(null);
            if (profile == null) return false;

            String fakeItemKey = itemStack.getItemMeta().getPersistentDataContainer().get(RTSListener.FAKE_ITEM_KEY, PersistentDataType.STRING);
            if (fakeItemKey == null) return false;

            SlimefunItem slimefunItem = SlimefunItem.getById(fakeItemKey);
            if (slimefunItem == null) return false;

            if (mode == SlimefunGuideMode.SURVIVAL_MODE) {
                RTSSearchGroup back = new RTSSearchGroup(
                        RTSSearchGroup.RTS_ANVIL_INVENTORIES.get(player),
                        RTSSearchGroup.RTS_PAGES.get(player)
                );
                profile.getGuideHistory().add(back, 1);
                implementation.displayItem(profile, slimefunItem, true);
                RTSListener.tryQuitRTS(player);
                return false;
            }

            if (!player.isOp() && !player.hasPermission("slimefun.cheat.items")){
                Slimefun.getLocalization().sendMessage(player, "messages.no-permission", true);
                return false;
            }

            if (slimefunItem instanceof MultiBlockMachine) {
                Slimefun.getLocalization().sendMessage(player, "guide.cheat.no-multiblocks");
                return false;
            }

            ItemStack clonedItem = slimefunItem.getItem().clone();

            int addAmount = clonedItem.getMaxStackSize();
            clonedItem.setAmount(addAmount);

            RTSListener.cheatItems.putIfAbsent(player, new ArrayList<>());
            RTSListener.cheatItems.get(player).add(clonedItem);

            ItemMeta meta = itemStack.getItemMeta();
            int originalAmount = meta.getPersistentDataContainer()
                    .getOrDefault(RTSListener.CHEAT_AMOUNT_KEY, PersistentDataType.INTEGER, 0);
            int totalAmount = originalAmount + addAmount;
            meta.getPersistentDataContainer()
                    .set(RTSListener.CHEAT_AMOUNT_KEY, PersistentDataType.INTEGER, totalAmount);
            meta.setDisplayName(ChatColors.color(
                    ItemStackHelper.getDisplayName(clonedItem) + " &c已拿取物品 x" + totalAmount));
            itemStack.setItemMeta(meta);

            return false;
        });
        holder.onRename((p, oldName, newName, event) -> {
            new RTSEvents.SearchTermChangeEvent(p, oldName, newName).callEvent();
        });

        holder.open(player);
    }

    private static void callPageChangeEvent(Player player, int oldPage, int newPage) {
        RTSEvents.PageChangeEvent event = new RTSEvents.PageChangeEvent(
                player, RTS_ANVIL_INVENTORIES.get(player), oldPage, newPage, GuideUtil.getLastGuideMode(player));
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            RTS_PAGES.put(player, newPage);
        }
    }

    @Override
    public boolean isVisible(
            Player player,
            PlayerProfile playerProfile,
            SlimefunGuideMode slimefunGuideMode) {
        return false;
    }

    @Override
    public void open(
            Player player,
            PlayerProfile playerProfile,
            SlimefunGuideMode slimefunGuideMode) {
        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
        openRTSInventoryFor(player);

        RTS_PAGES.put(player, this.page);
        RTSEvents.PageChangeEvent event =
                new RTSEvents.PageChangeEvent(player, RTS_ANVIL_INVENTORIES.get(player), page, page, slimefunGuideMode);
        Bukkit.getPluginManager().callEvent(event);
    }
}
