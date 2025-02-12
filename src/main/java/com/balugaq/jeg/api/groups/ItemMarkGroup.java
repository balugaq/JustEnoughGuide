package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.BookmarkRelocation;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.JEGVersionedItemFlag;
import com.balugaq.jeg.utils.Lang;
import com.balugaq.jeg.utils.LocalHelper;
import com.balugaq.jeg.utils.SlimefunOfficialSupporter;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
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
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This class used to create groups to mark items into {@link BookmarkGroup} in the guide.
 * Will not display Item Mark Button in {@link NestedItemGroup}
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"deprecation", "unused"})
@NotDisplayInSurvivalMode
@NotDisplayInCheatMode
public class ItemMarkGroup extends FlexItemGroup {
    private static final ItemStack ICON_BACKGROUND = Lang.getIcon("item-mark-background", Material.GREEN_STAINED_GLASS_PANE);
    private static final JavaPlugin JAVA_PLUGIN = JustEnoughGuide.getInstance();
    private final int BACK_SLOT;
    private final int SEARCH_SLOT;
    private final int PREVIOUS_SLOT;
    private final int NEXT_SLOT;
    private final int[] BORDER;
    private final int[] MAIN_CONTENT;
    private final JEGSlimefunGuideImplementation implementation;
    private final Player player;
    private final @NotNull ItemGroup itemGroup;
    private final int page;
    private final @NotNull List<SlimefunItem> slimefunItemList;
    private Map<Integer, ItemMarkGroup> pageMap = new LinkedHashMap<>();

    /**
     * Create a new instance of ItemMarkGroup.
     *
     * @param implementation The implementation of JEGSlimefunGuideImplementation.
     * @param itemGroup      The item group to mark items.
     * @param player         The player who open the guide.
     */
    public ItemMarkGroup(JEGSlimefunGuideImplementation implementation, @NotNull ItemGroup itemGroup, Player player) {
        this(implementation, itemGroup, player, 1);
    }

    /**
     * Create a new instance of ItemMarkGroup.
     *
     * @param implementation The implementation of JEGSlimefunGuideImplementation.
     * @param itemGroup      The item group to mark items.
     * @param player         The player who open the guide.
     * @param page           The page number to display.
     */
    public ItemMarkGroup(
            JEGSlimefunGuideImplementation implementation, @NotNull ItemGroup itemGroup, Player player, int page) {
        super(
                new NamespacedKey(JAVA_PLUGIN, "jeg_item_mark_group_" + UUID.randomUUID()),
                new ItemStack(Material.BARRIER));
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

    /**
     * Get the page number of this group.
     *
     * @param itemMarkGroup The ItemMarkGroup instance.
     * @param page          The page number to get.
     */
    protected ItemMarkGroup(@NotNull ItemMarkGroup itemMarkGroup, int page) {
        this(itemMarkGroup.implementation, itemMarkGroup.itemGroup, itemMarkGroup.player, page);
    }

    /**
     * Always return false.
     *
     * @param player            The player who open the guide.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The slimefun guide mode.
     * @return false.
     */
    @Override
    public boolean isVisible(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        return false;
    }

    /**
     * Opens the group for the player.
     *
     * @param player            The player who open the guide.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The slimefun guide mode.
     */
    @Override
    public void open(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        playerProfile.getGuideHistory().add(this, this.page);
        this.generateMenu(player, playerProfile, slimefunGuideMode).open(player);
    }

    /**
     * Refresh the group for the player.
     *
     * @param player            The player who open the guide.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The slimefun guide mode.
     */
    public void refresh(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
        this.open(player, playerProfile, slimefunGuideMode);
    }

    /**
     * Get the ItemMarkGroup instance by page number.
     *
     * @param player            The player who open the guide.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The slimefun guide mode.
     * @return The ItemMarkGroup instance by page number.
     */
    @NotNull
    private ChestMenu generateMenu(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu(Lang.getGuideMessage("item-mark-title"));

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
        chestMenu.addItem(SEARCH_SLOT, ItemStackUtil.getCleanItem(ChestMenuUtils.getSearchButton(player)));
        chestMenu.addMenuClickHandler(SEARCH_SLOT, (pl, slot, item, action) -> {
            pl.closeInventory();

            Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
            ChatInput.waitForPlayer(
                    JAVA_PLUGIN,
                    pl,
                    msg -> implementation.openSearch(
                            playerProfile, msg, implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE));

            return false;
        });

        chestMenu.addItem(
                PREVIOUS_SLOT,
                ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(
                        player, this.page, (this.slimefunItemList.size() - 1) / MAIN_CONTENT.length + 1)));
        chestMenu.addMenuClickHandler(PREVIOUS_SLOT, (p, slot, item, action) -> {
            GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
            ItemMarkGroup itemMarkGroup = this.getByPage(Math.max(this.page - 1, 1));
            itemMarkGroup.open(player, playerProfile, slimefunGuideMode);
            return false;
        });

        chestMenu.addItem(
                NEXT_SLOT,
                ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(
                        player, this.page, (this.slimefunItemList.size() - 1) / MAIN_CONTENT.length + 1)));
        chestMenu.addMenuClickHandler(NEXT_SLOT, (p, slot, item, action) -> {
            GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
            ItemMarkGroup itemMarkGroup = this.getByPage(
                    Math.min(this.page + 1, (this.slimefunItemList.size() - 1) / MAIN_CONTENT.length + 1));
            itemMarkGroup.open(player, playerProfile, slimefunGuideMode);
            return false;
        });

        for (int slot : BORDER) {
            chestMenu.addItem(slot, ItemStackUtil.getCleanItem(ICON_BACKGROUND));
            chestMenu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i = 0; i < MAIN_CONTENT.length; i++) {
            int index = i + this.page * MAIN_CONTENT.length - MAIN_CONTENT.length;
            if (index < this.slimefunItemList.size()) {
                SlimefunItem slimefunItem = slimefunItemList.get(index);
                Research research = slimefunItem.getResearch();
                ItemStack itemstack;
                ChestMenu.MenuClickHandler handler;
                if (implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE
                        && research != null
                        && !playerProfile.hasUnlocked(research)) {

                    itemstack = ItemStackUtil.getCleanItem(new CustomItemStack(
                                    ChestMenuUtils.getNoPermissionItem(),
                                    "&f" + ItemUtils.getItemName(slimefunItem.getItem()),
                                    "&7" + slimefunItem.getId(),
                                    "&4&l" + Slimefun.getLocalization().getMessage(player, "guide.locked"),
                                    "",
                                    Lang.getGuideMessage("click-to-unlock"),
                                    "",
                                    Lang.getGuideMessage("cost", "cost", research.getCost())));
                    handler = (pl, slot, item, action) -> {
                        research.unlockFromGuide(implementation, pl, playerProfile, slimefunItem, itemGroup, page);
                        return false;
                    };
                } else {
                    itemstack = ItemStackUtil.getCleanItem(new CustomItemStack(slimefunItem.getItem(), meta -> {
                        ItemGroup itemGroup = slimefunItem.getItemGroup();
                        List<String> additionLore = List.of(
                                "",
                                ChatColor.DARK_GRAY + "\u21E8 " + ChatColor.WHITE
                                        + (LocalHelper.getAddonName(itemGroup, slimefunItem.getId())) + " - "
                                        + itemGroup.getDisplayName(player),
                                Lang.getGuideMessage("mark-item"));
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
                                JEGVersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                    }));
                    handler = (pl, slot, itm, action) -> {
                        try {
                            JustEnoughGuide.getBookmarkManager().addBookmark(pl, slimefunItem);
                            pl.sendMessage(Lang.getGuideMessage("marked"));
                            pl.playSound(pl.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
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

    /**
     * Get the ItemMarkGroup instance by page number.
     *
     * @param page The page number to get.
     * @return The ItemMarkGroup instance by page number.
     */
    @NotNull
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

    /**
     * Get the ItemMarkGroup instance by page number.
     *
     * @param p            The player who open the guide.
     * @param slimefunItem The SlimefunItem to check.
     * @return The ItemMarkGroup instance by page number.
     */
    @ParametersAreNonnullByDefault
    private boolean isItemGroupAccessible(Player p, SlimefunItem slimefunItem) {
        return SlimefunOfficialSupporter.isShowHiddenItemGroups()
                || slimefunItem.getItemGroup().isAccessible(p);
    }

    @ParametersAreNonnullByDefault
    private void printErrorMessage(Player p, Throwable x) {
        p.sendMessage(Lang.getError("internal-error"));
        JustEnoughGuide.getInstance().getLogger().log(Level.SEVERE, Lang.getError("error-occurred"), x);
        JustEnoughGuide.getInstance().getLogger().warning(Lang.getError("trying-fix-guide", "player_name", p.getName()));
        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.removeLastEntry(profile.getGuideHistory());
    }

    @ParametersAreNonnullByDefault
    private void printErrorMessage(Player p, SlimefunItem item, Throwable x) {
        p.sendMessage(Lang.getError("internal-error"));
        item.error(Lang.getError("item-error"), x);
        JustEnoughGuide.getInstance()
                .getLogger()
                .warning(Lang.getError("trying-fix-guide", "player_name", p.getName()));
        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.removeLastEntry(profile.getGuideHistory());
    }
}
