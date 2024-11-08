package com.balugaq.jeg.groups;

import city.norain.slimefun4.VaultIntegration;
import com.balugaq.jeg.JustEnoughGuide;
import com.balugaq.jeg.interfaces.BookmarkRelocation;
import com.balugaq.jeg.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedItemFlag;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

@NotDisplayInSurvivalMode
@NotDisplayInCheatMode
public class ItemMarkGroup extends FlexItemGroup {
    private static final JavaPlugin JAVA_PLUGIN = JustEnoughGuide.getInstance();
    private final int BACK_SLOT;
    private final int SEARCH_SLOT;
    private final int PREVIOUS_SLOT;
    private final int NEXT_SLOT;
    private final int[] BORDER;
    private final int[] MAIN_CONTENT;
    private final JEGSlimefunGuideImplementation implementation;
    private final Player player;
    private final ItemGroup itemGroup;
    private final int page;
    private final List<SlimefunItem> slimefunItemList;
    private Map<Integer, ItemMarkGroup> pageMap = new LinkedHashMap<>();

    public ItemMarkGroup(JEGSlimefunGuideImplementation implementation, ItemGroup itemGroup, Player player) {
        this(implementation, itemGroup, player, 1);
    }
    public ItemMarkGroup(JEGSlimefunGuideImplementation implementation, ItemGroup itemGroup, Player player, int page) {
        super(new NamespacedKey(JAVA_PLUGIN, "jeg_item_mark_group_" + UUID.randomUUID()), new ItemStack(Material.BARRIER));
        this.page = page;
        this.player = player;
        this.itemGroup = itemGroup;
        this.slimefunItemList = itemGroup.getItems();
        this.implementation = implementation;
        this.pageMap.put(page, this);

        if (itemGroup instanceof BookmarkRelocation bookmarkRelocation) {
            BACK_SLOT = bookmarkRelocation.getBackButton(implementation, player);
            SEARCH_SLOT = bookmarkRelocation.getSearchButton(implementation, player);
            PREVIOUS_SLOT = bookmarkRelocation.getPreviousButton(implementation, player);
            NEXT_SLOT = bookmarkRelocation.getNextButton(implementation, player);
            BORDER = bookmarkRelocation.getBorder(implementation, player);
            MAIN_CONTENT = bookmarkRelocation.getMainContents(implementation, player);
        } else {
            BACK_SLOT = 1;
            SEARCH_SLOT = 7;
            PREVIOUS_SLOT = 46;
            NEXT_SLOT = 52;
            BORDER = new int[]{0, 2, 3, 4, 5, 6, 8, 45, 47, 48, 49, 50, 51, 53};
            MAIN_CONTENT = new int[]{
                    9, 10, 11, 12, 13, 14, 15, 16, 17,
                    18, 19, 20, 21, 22, 23, 24, 25, 26,
                    27, 28, 29, 30, 31, 32, 33, 34, 35,
                    36, 37, 38, 39, 40, 41, 42, 43, 44
            };
        }
    }

    protected ItemMarkGroup(ItemMarkGroup itemMarkGroup, int page) {
        this(itemMarkGroup.implementation, itemMarkGroup.itemGroup, itemMarkGroup.player, page);
    }

    @Override
    public boolean isVisible(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode) {
        return false;
    }

    @Override
    public void open(Player player, PlayerProfile playerProfile, SlimefunGuideMode slimefunGuideMode) {
        playerProfile.getGuideHistory().add(this, this.page);
        this.generateMenu(player, playerProfile, slimefunGuideMode).open(player);
    }

    public void refresh(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode) {
        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
        this.open(player, playerProfile, slimefunGuideMode);
    }

    @Nonnull
    private ChestMenu generateMenu(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu("添加收藏物 - JEG");

        chestMenu.setEmptySlotsClickable(false);
        chestMenu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1));

        chestMenu.addItem(BACK_SLOT, ItemStackUtil.getCleanItem(ChestMenuUtils.getBackButton(player)));
        chestMenu.addMenuClickHandler(BACK_SLOT, (pl, s, is, action) -> {
            GuideHistory guideHistory = playerProfile.getGuideHistory();
            if (action.isShiftClicked()) {
                SlimefunGuide.openMainMenu(playerProfile, slimefunGuideMode, guideHistory.getMainMenuPage());
            } else {
                guideHistory.goBack(Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.SURVIVAL_MODE));
            }
            return false;
        });

        // Search feature!
        chestMenu.addItem(SEARCH_SLOT, ChestMenuUtils.getSearchButton(player));
        chestMenu.addMenuClickHandler(SEARCH_SLOT, (pl, slot, item, action) -> {
            pl.closeInventory();

            Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
            ChatInput.waitForPlayer(
                    JAVA_PLUGIN,
                    pl,
                    msg -> implementation.openSearch(playerProfile, msg, implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE));

            return false;
        });

        chestMenu.addItem(PREVIOUS_SLOT, ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(player, this.page, (this.slimefunItemList.size() - 1) / MAIN_CONTENT.length + 1)));
        chestMenu.addMenuClickHandler(PREVIOUS_SLOT, (p, slot, item, action) -> {
            GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
            ItemMarkGroup itemMarkGroup = this.getByPage(Math.max(this.page - 1, 1));
            itemMarkGroup.open(player, playerProfile, slimefunGuideMode);
            return false;
        });

        chestMenu.addItem(NEXT_SLOT, ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(player, this.page, (this.slimefunItemList.size() - 1) / MAIN_CONTENT.length + 1)));
        chestMenu.addMenuClickHandler(NEXT_SLOT, (p, slot, item, action) -> {
            GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
            ItemMarkGroup itemMarkGroup = this.getByPage(Math.min(this.page + 1, (this.slimefunItemList.size() - 1) / MAIN_CONTENT.length + 1));
            itemMarkGroup.open(player, playerProfile, slimefunGuideMode);
            return false;
        });

        for (int slot : BORDER) {
            chestMenu.addItem(slot, ItemStackUtil.getCleanItem(ChestMenuUtils.getBackground()));
            chestMenu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i = 0; i < MAIN_CONTENT.length; i++) {
            int index = i + this.page * MAIN_CONTENT.length - MAIN_CONTENT.length;
            if (index < this.slimefunItemList.size()) {
                SlimefunItem slimefunItem = slimefunItemList.get(index);
                Research research = slimefunItem.getResearch();
                ItemStack itemstack;
                ChestMenu.MenuClickHandler handler;
                if (implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE && research != null && !playerProfile.hasUnlocked(research)) {
                    String lore;

                    if (VaultIntegration.isEnabled()) {
                        lore = String.format("%.2f", research.getCurrencyCost()) + " 游戏币";
                    } else {
                        lore = research.getLevelCost() + " 级经验";
                    }

                    itemstack = new CustomItemStack(new CustomItemStack(
                            ChestMenuUtils.getNoPermissionItem(),
                            "&f" + ItemUtils.getItemName(slimefunItem.getItem()),
                            "&7" + slimefunItem.getId(),
                            "&4&l" + Slimefun.getLocalization().getMessage(player, "guide.locked"),
                            "",
                            "&a> 单击解锁",
                            "",
                            "&7需要 &b",
                            lore));
                    handler = (pl, slot, item, action) -> {
                        research.unlockFromGuide(implementation, pl, playerProfile, slimefunItem, itemGroup, page);
                        return false;
                    };
                } else {
                    itemstack = new CustomItemStack(slimefunItem.getItem(), meta -> {
                        ItemGroup itemGroup = slimefunItem.getItemGroup();
                        List<String> additionLore = List.of("",
                                ChatColor.DARK_GRAY + "\u21E8 " + ChatColor.WHITE + (itemGroup.getAddon() == null ? "Slimefun" : itemGroup.getAddon().getName()) + " - " + itemGroup.getDisplayName(player),
                                ChatColor.YELLOW + "左键点击以收藏物品");
                        if (meta.hasLore() && meta.getLore() != null) {
                            List<String> lore = meta.getLore();
                            lore.addAll(additionLore);
                            meta.setLore(lore);
                        } else {
                            meta.setLore(additionLore);
                        }

                        meta.addItemFlags(
                                ItemFlag.HIDE_ATTRIBUTES,
                                ItemFlag.HIDE_ENCHANTS,
                                VersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                    });
                    handler = (pl, slot, itm, action) -> {
                        try {
                            if (implementation.getMode() != SlimefunGuideMode.SURVIVAL_MODE) {
                                pl.getInventory().addItem(slimefunItem.getItem().clone());
                            } else {
                                JustEnoughGuide.getBookmarkManager().addBookmark(pl, slimefunItem);
                            }
                        } catch (Exception | LinkageError x) {
                            printErrorMessage(pl, slimefunItem, x);
                        }

                        return false;
                    };
                }

                chestMenu.addItem(MAIN_CONTENT[i], ItemStackUtil.getCleanItem(itemstack), handler);
            }
        }

        chestMenu.addItem(48, ItemStackUtil.getCleanItem(GuideUtil.getItemMarkMenuButton()));
        chestMenu.addMenuClickHandler(48, (pl, s, is, action) -> {
            GuideHistory guideHistory = playerProfile.getGuideHistory();
            if (action.isShiftClicked()) {
                SlimefunGuide.openMainMenu(playerProfile, slimefunGuideMode, guideHistory.getMainMenuPage());
            } else {
                guideHistory.goBack(Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.SURVIVAL_MODE));
            }
            return false;
        });

        chestMenu.addItem(49, ItemStackUtil.getCleanItem(GuideUtil.getBookMarkMenuButton()));
        chestMenu.addMenuClickHandler(49, (pl, s, is, action) -> {
            implementation.openBookMarkGroup(pl, playerProfile);
            return false;
        });

        return chestMenu;
    }

    @Nonnull
    private ItemMarkGroup getByPage(int page) {
        if (this.pageMap.containsKey(page)) {
            return this.pageMap.get(page);
        } else {
            synchronized (this.pageMap.get(1)) {
                if (this.pageMap.containsKey(page)) {
                    return this.pageMap.get(page);
                }

                ItemMarkGroup itemMarkGroup = new ItemMarkGroup(this, page);
                itemMarkGroup.pageMap = this.pageMap;
                this.pageMap.put(page, itemMarkGroup);
                return itemMarkGroup;
            }
        }
    }

    private List<SlimefunItem> getAllMatchedItems(Player p, String searchTerm, boolean pinyin) {
        return Slimefun.getRegistry().getEnabledSlimefunItems()
                .stream()
                .filter(slimefunItem -> {
                    return !slimefunItem.isHidden()
                            && isItemGroupAccessible(p, slimefunItem)
                            && isSearchFilterApplicable(slimefunItem, searchTerm, pinyin);
                })
                .toList();
    }

    @ParametersAreNonnullByDefault
    private boolean isItemGroupAccessible(Player p, SlimefunItem slimefunItem) {
        return Slimefun.getConfigManager().isShowHiddenItemGroupsInSearch()
                || slimefunItem.getItemGroup().isAccessible(p);
    }

    @ParametersAreNonnullByDefault
    private boolean isSearchFilterApplicable(SlimefunItem slimefunItem, String searchTerm, boolean pinyin) {
        String itemName = ChatColor.stripColor(slimefunItem.getItemName()).toLowerCase(Locale.ROOT);
        if (itemName.isEmpty()) {
            return false;
        }
        if (pinyin) {
            final String pinyinName = PinyinHelper.toPinyin(itemName, PinyinStyleEnum.INPUT, "");
            final String pinyinFirstLetter = PinyinHelper.toPinyin(itemName, PinyinStyleEnum.FIRST_LETTER, "");
            return itemName.contains(searchTerm) || pinyinName.contains(searchTerm) || pinyinFirstLetter.contains(searchTerm);
        } else {
            return itemName.contains(searchTerm);
        }
    }

    @ParametersAreNonnullByDefault
    private void printErrorMessage(Player p, Throwable x) {
        p.sendMessage("&4服务器发生了一个内部错误. 请联系管理员处理.");
        JAVA_PLUGIN.getLogger().log(Level.SEVERE, "在打开指南书里的 Slimefun 物品时发生了意外!", x);
    }

    @ParametersAreNonnullByDefault
    private void printErrorMessage(Player p, SlimefunItem item, Throwable x) {
        p.sendMessage(ChatColor.DARK_RED
                + "An internal server error has occurred. Please inform an admin, check the console for"
                + " further info.");
        item.error(
                "This item has caused an error message to be thrown while viewing it in the Slimefun" + " guide.", x);
    }
}