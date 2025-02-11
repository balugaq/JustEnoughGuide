package com.balugaq.jeg.utils;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class contains utility methods for the guide system.
 *
 * @author Final_ROOT, balugaq
 * @since 1.0
 */
@UtilityClass
public final class GuideUtil {
    private static final ItemStack BOOK_MARK_MENU_BUTTON =
            ItemStackUtil.getCleanItem(new CustomItemStack(Material.NETHER_STAR, "&e&lCollected"));
    private static final ItemStack ITEM_MARK_MENU_BUTTON =
            ItemStackUtil.getCleanItem(new CustomItemStack(Material.WRITABLE_BOOK, "&e&lCollecting"));

    /**
     * Open the main menu of the guide for the given player and mode.
     *
     * @param player       The player to open the guide for.
     * @param mode         The mode to open the guide for.
     * @param selectedPage The page to open the guide to.
     */
    @ParametersAreNonnullByDefault
    public static void openMainMenuAsync(Player player, SlimefunGuideMode mode, int selectedPage) {
        if (!PlayerProfile.get(
                player, profile -> Slimefun.runSync(() -> openMainMenu(player, profile, mode, selectedPage)))) {
            Slimefun.getLocalization().sendMessage(player, "messages.opening-guide");
        }
    }

    /**
     * Open the main menu of the guide for the given player and mode.
     *
     * @param player       The player to open the guide for.
     * @param profile      The player's profile.
     * @param mode         The mode to open the guide for.
     * @param selectedPage The page to open the guide to.
     */
    @ParametersAreNonnullByDefault
    public static void openMainMenu(Player player, PlayerProfile profile, SlimefunGuideMode mode, int selectedPage) {
        getGuide(player, mode).openMainMenu(profile, selectedPage);
    }

    /**
     * Get the guide implementation for the given player and mode.
     *
     * @param player The player to get the guide for.
     * @param mode   The mode to get the guide for.
     * @return The guide implementation for the given player and mode.
     */
    public static @NotNull SlimefunGuideImplementation getGuide(@NotNull Player player, SlimefunGuideMode mode) {
        if (mode == SlimefunGuideMode.SURVIVAL_MODE) {
            return Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.SURVIVAL_MODE);
        }

        // Player must be op or have the permission "slimefun.cheat.items" to access the cheat guide
        if ((player.isOp() || player.hasPermission("slimefun.cheat.items")) && mode == SlimefunGuideMode.CHEAT_MODE) {
            return Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.CHEAT_MODE);
        }

        // Fallback to survival guide if no permission is given
        return Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.SURVIVAL_MODE);
    }

    public static void removeLastEntry(@NotNull GuideHistory guideHistory) {
        try {
            Method getLastEntry = guideHistory.getClass().getDeclaredMethod("getLastEntry", boolean.class);
            getLastEntry.setAccessible(true);
            getLastEntry.invoke(guideHistory, true);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static @NotNull ItemStack getBookMarkMenuButton() {
        return BOOK_MARK_MENU_BUTTON;
    }

    public static @NotNull ItemStack getItemMarkMenuButton() {
        return ITEM_MARK_MENU_BUTTON;
    }
}
