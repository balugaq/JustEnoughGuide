package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.google.common.base.Preconditions;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is the base class for all guide groups.
 * It provides a simple way to add guides to a group and open them in a chest menu.
 * The group can be customized by overriding the following methods:
 * - getSize() - returns the size of the chest menu
 * - isClassic() - returns whether the chest menu should be in classic mode
 * - getContentSlots() - returns the slots where the guides should be placed
 * - getBackSlot() - returns the slot where the back button should be placed
 *
 * @author balugaq
 * @since 1.3
 */
@SuppressWarnings({"deprecation", "unused"})
@Getter
@NotDisplayInCheatMode
public abstract class GuideGroup extends FlexItemGroup {
    private static final int PREVIOUS_SLOT = 46;
    private static final int NEXT_SLOT = 52;
    private final Map<Integer, Set<Integer>> slots = new HashMap<>();
    private final Map<Integer, Map<Integer, ItemStack>> contents = new HashMap<>();
    private final Map<Integer, Map<Integer, ChestMenu.MenuClickHandler>> clickHandlers = new HashMap<>();

    /**
     * Creates a new guide group with the given key and icon.
     *
     * @param key  The key of the group.
     * @param icon The icon of the group.
     */
    protected GuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon) {
        super(key, icon);
    }

    /**
     * Creates a new guide group with the given key, icon, and tier.
     *
     * @param key  The key of the group.
     * @param icon The icon of the group.
     * @param tier The tier of the group.
     */
    protected GuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon, int tier) {
        super(key, icon, tier);
    }

    /**
     * Adds a guide to the group.
     *
     * @param page      The page where the guide should be placed.
     * @param slot      The slot where the guide should be placed.
     * @param itemStack The item stack representing the guide.
     * @param handler   The click handler for the guide.
     * @return The group itself.
     */
    @NotNull
    public GuideGroup addGuide(
            @Range(from = 1, to = Byte.MAX_VALUE) int page,
            @Range(from = 9, to = 44) int slot,
            @NotNull ItemStack itemStack,
            @NotNull ChestMenu.MenuClickHandler handler) {
        Preconditions.checkArgument(slot >= 9 && slot <= 44, "Slot must be between 9 and 44");
        Preconditions.checkArgument(itemStack != null, "Item must not be null");
        Preconditions.checkArgument(handler != null, "Handler must not be null");
        Preconditions.checkArgument(itemStack.getType() != Material.AIR, "Item must not be air");
        Preconditions.checkArgument(itemStack.getType().isItem(), "Item must be an item");
        Preconditions.checkArgument(
                slots.size() <= getContentSlots().length, "Too many guides in this group. Maximum of 36 allowed.");

        slots.computeIfAbsent(page, k -> new HashSet<>()).add(slot);
        contents.computeIfAbsent(page, k -> new LinkedHashMap<>()).put(slot, itemStack);
        clickHandlers.computeIfAbsent(page, k -> new LinkedHashMap<>()).put(slot, handler);
        return this;
    }

    /**
     * Adds a guide to the group.
     *
     * @param slot      The slot where the guide should be placed.
     * @param itemStack The item stack representing the guide.
     * @param handler   The click handler for the guide.
     * @return The group itself.
     */
    @NotNull
    public GuideGroup addGuide(
            @Range(from = 9, to = 44) int slot,
            @NotNull ItemStack itemStack,
            @NotNull ChestMenu.MenuClickHandler handler) {
        return addGuide(1, slot, itemStack, handler);
    }

    /**
     * Adds a guide to the group.
     *
     * @param slot      The slot where the guide should be placed.
     * @param itemStack The item stack representing the guide.
     * @return The group itself.
     */
    @NotNull
    public GuideGroup addGuide(@Range(from = 9, to = 44) int slot, @NotNull ItemStack itemStack) {
        return addGuide(slot, itemStack, ChestMenuUtils.getEmptyClickHandler());
    }

    /**
     * Adds a guide to the group.
     *
     * @param itemStack The item stack representing the guide.
     * @param handler   The click handler for the guide.
     * @return The group itself.
     */
    @NotNull
    public GuideGroup addGuide(@NotNull ItemStack itemStack, @NotNull ChestMenu.MenuClickHandler handler, @Range(from = 1, to = Byte.MAX_VALUE) int page) {
        return addGuide(findEmptySlot(page), itemStack, handler);
    }

    /**
     * Adds a guide to the group.
     *
     * @param itemStack The item stack representing the guide.
     * @return The group itself.
     */
    @NotNull
    public GuideGroup addGuide(@NotNull ItemStack itemStack, @Range(from = 1, to = Byte.MAX_VALUE) int page) {
        return addGuide(itemStack, ChestMenuUtils.getEmptyClickHandler(), page);
    }

    /**
     * Finds an empty slot in the chest menu.
     *
     * @param page The page to search in.
     * @return An empty slot in the chest menu, or -1 if no slot is available.
     */
    private int findEmptySlot(int page) {
        for (int i = 0; i < 54; i++) {
            if (!slots.getOrDefault(page, new HashSet<>()).contains(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns whether the group is visible to the given player.
     *
     * @param player            The player to check.
     * @param playerProfile     The player profile of the player.
     * @param slimefunGuideMode The guide mode of the player.
     * @return Whether the group is visible to the given player.
     */
    @Override
    public boolean isVisible(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        return true;
    }

    @Override
    public void open(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode
    ) {
        open(player, playerProfile, slimefunGuideMode, 1);
    }

    /**
     * Opens the customized group for the given player.
     *
     * @param player            The player to open the group for.
     * @param playerProfile     The player profile of the player.
     * @param slimefunGuideMode The guide mode of the player.
     * @param page              The page to open.
     */
    public void open(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode,
            @Range(from = 1, to = Byte.MAX_VALUE) int page) {
        if (page < 1 || page > this.contents.size()) {
            // Do nothing if the page is out of range.
            return;
        }

        SlimefunGuideImplementation guide = GuideUtil.getGuide(player, slimefunGuideMode);
        playerProfile.getGuideHistory().add(this, page);
        if (guide instanceof JEGSlimefunGuideImplementation jeg) {
            ChestMenu menu = new ChestMenu(getDisplayName(player));
            if (isClassic()) {
                jeg.createHeader(player, playerProfile, menu);
            }
            menu.addItem(getBackSlot(), ItemStackUtil.getCleanItem(ChestMenuUtils.getBackButton(player)));
            menu.addMenuClickHandler(getBackSlot(), (pl, s, is, action) -> {
                GuideHistory guideHistory = playerProfile.getGuideHistory();
                if (action.isShiftClicked()) {
                    SlimefunGuide.openMainMenu(playerProfile, slimefunGuideMode, guideHistory.getMainMenuPage());
                } else {
                    guideHistory.goBack(Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.SURVIVAL_MODE));
                }
                return false;
            });

            for (Map.Entry<Integer, ItemStack> entry : contents.getOrDefault(page, new LinkedHashMap<>()).entrySet()) {
                menu.addItem(entry.getKey(), entry.getValue());
            }

            for (Map.Entry<Integer, ChestMenu.MenuClickHandler> entry : clickHandlers.getOrDefault(page, new LinkedHashMap<>()).entrySet()) {
                menu.addMenuClickHandler(entry.getKey(), entry.getValue());
            }

            menu.addItem(
                    PREVIOUS_SLOT,
                    ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(
                            player, page, (this.contents.size() - 1) / 36 + 1)));
            menu.addMenuClickHandler(PREVIOUS_SLOT, (p, slot, item, action) -> {
                if (page - 1 < 1) {
                    return false;
                }
                GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                open(player, playerProfile, slimefunGuideMode, Math.max(1, page - 1));
                return false;
            });

            menu.addItem(
                    NEXT_SLOT,
                    ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(
                            player, page, (this.contents.size() - 1) / 36 + 1)));
            menu.addMenuClickHandler(NEXT_SLOT, (p, slot, item, action) -> {
                if (page + 1 > this.contents.size()) {
                    return false;
                }
                GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                open(player, playerProfile, slimefunGuideMode, Math.min(this.contents.size(), page + 1));
                return false;
            });

            menu.open(player);
        } else {
            player.sendMessage("§cJEG has already been disabled!");
        }
    }

    /**
     * Returns the size of the chest menu.
     *
     * @return The size of the chest menu.
     */
    public abstract int getSize();

    /**
     * Returns whether the chest menu should be generated in classic mode.
     *
     * @return Whether the chest menu should be generated in classic mode.
     */
    public abstract boolean isClassic();

    /**
     * Returns the slots where the guides should be placed.
     *
     * @return The slots where the guides should be placed.
     */
    public abstract int[] getContentSlots();

    /**
     * Returns the slot where the back button should be placed.
     *
     * @return The slot where the back button should be placed.
     */
    public abstract int getBackSlot();
}
