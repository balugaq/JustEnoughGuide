package com.balugaq.jeg.api.interfaces;

import org.bukkit.entity.Player;

/**
 * This interface defines the methods that a BookmarkRelocation implementation should implement.
 * Used for relocating the buttons of the guide to a different location.
 *
 * @author balugaq
 * @see com.balugaq.jeg.implementation.guide.SurvivalGuideImplementation
 * @see com.balugaq.jeg.implementation.guide.CheatGuideImplementation
 * @since 1.1
 */
public interface BookmarkRelocation {
    /**
     * Gets the slot number for the back button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the back button.
     */
    int getBackButton(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the search button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the search button.
     */
    int getSearchButton(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the previous button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the previous button.
     */
    int getPreviousButton(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the next button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the next button.
     */
    int getNextButton(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the bookmark button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the bookmark button.
     */
    int getBookMark(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the item mark button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the item mark button.
     */
    int getItemMark(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot numbers for the border in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot numbers for the border.
     */
    int[] getBorder(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot numbers for the main contents in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot numbers for the main contents.
     */
    int[] getMainContents(JEGSlimefunGuideImplementation implementation, Player player);
}
