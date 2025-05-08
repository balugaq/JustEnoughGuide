package com.balugaq.jeg.api.interfaces;

import com.balugaq.jeg.api.groups.BookmarkGroup;
import com.balugaq.jeg.api.groups.ItemMarkGroup;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@SuppressWarnings("deprecation")
public interface JEGSlimefunGuideImplementation extends SlimefunGuideImplementation {
    /**
     * Displays the specified Slimefun item to the player.
     *
     * @param profile      The player profile.
     * @param item         The Slimefun item to display.
     * @param addToHistory Whether to add the item to the guide history.
     * @param maybeSpecial Whether to check for special menu items.
     */
    @ParametersAreNonnullByDefault
    void displayItem(PlayerProfile profile, SlimefunItem item, boolean addToHistory, boolean maybeSpecial);

    /**
     * Creates the header of the guide menu.
     *
     * @param p       The player.
     * @param profile The player profile.
     * @param menu    The guide menu.
     */
    @ParametersAreNonnullByDefault
    void createHeader(Player p, PlayerProfile profile, ChestMenu menu);

    /**
     * Creates a new guide menu for the player.
     *
     * @param p The player.
     * @return The created guide menu.
     */
    @NotNull ChestMenu create(@NotNull Player p);

    /**
     * Opens the bookmark group for the player.
     *
     * @param player  The player.
     * @param profile The player profile.
     */
    default void openBookMarkGroup(@NotNull Player player, @NotNull PlayerProfile profile) {
        List<SlimefunItem> items = JustEnoughGuide.getBookmarkManager().getBookmarkedItems(player);
        if (items == null || items.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You haven't collected any items yet!");
            return;
        }
        new BookmarkGroup(this, player, items).open(player, profile, getMode());
    }

    /**
     * Opens the item mark group for the player.
     *
     * @param itemGroup The item group.
     * @param player    The player.
     * @param profile   The player profile.
     */
    default void openItemMarkGroup(
            @NotNull ItemGroup itemGroup, @NotNull Player player, @NotNull PlayerProfile profile) {
        new ItemMarkGroup(this, itemGroup, player).open(player, profile, getMode());
    }
}
