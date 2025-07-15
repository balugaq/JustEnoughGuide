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

package com.balugaq.jeg.api.interfaces;

import com.balugaq.jeg.implementation.guide.CheatGuideImplementation;
import com.balugaq.jeg.implementation.guide.SurvivalGuideImplementation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * This interface defines the methods that a BookmarkRelocation implementation should implement.
 * Used for relocating the buttons of the guide to a different location.
 *
 * @author balugaq
 * @see SurvivalGuideImplementation
 * @see CheatGuideImplementation
 * @since 1.1
 */
@SuppressWarnings("SameReturnValue")
public interface BookmarkRelocation {
    /**
     * Gets the slot number for the back button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the back button.
     */
    @Deprecated
    @ParametersAreNonnullByDefault
    default int getBackButtonLegacy(JEGSlimefunGuideImplementation implementation, Player player) {
        return 1;
    }

    /**
     * Gets the slot number for the search button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the search button.
     */
    @Deprecated
    @ParametersAreNonnullByDefault
    default int getSearchButtonLegacy(JEGSlimefunGuideImplementation implementation, Player player) {
        return 7;
    }

    /**
     * Gets the slot number for the previous button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the previous button.
     */
    @Deprecated
    @ParametersAreNonnullByDefault
    default int getPreviousButtonLegacy(JEGSlimefunGuideImplementation implementation, Player player) {
        return 46;
    }

    /**
     * Gets the slot number for the next button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the next button.
     */
    @Deprecated
    @ParametersAreNonnullByDefault
    default int getNextButtonLegacy(JEGSlimefunGuideImplementation implementation, Player player) {
        return 52;
    }

    /**
     * Gets the slot number for the bookmark button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the bookmark button.
     */
    @Deprecated
    @ParametersAreNonnullByDefault
    default int getBookMarkLegacy(JEGSlimefunGuideImplementation implementation, Player player) {
        return 49;
    }

    /**
     * Gets the slot number for the item mark button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the item mark button.
     */
    @Deprecated
    @ParametersAreNonnullByDefault
    default int getItemMarkLegacy(JEGSlimefunGuideImplementation implementation, Player player) {
        return 48;
    }

    /**
     * Gets the slot numbers for the border in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot numbers for the border.
     */
    @Deprecated
    @ParametersAreNonnullByDefault
    int[] getBorderLegacy(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot numbers for the main contents in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot numbers for the main contents.
     */
    @Deprecated
    @ParametersAreNonnullByDefault
    int[] getMainContentsLegacy(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the back button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the back button.
     */
    @ParametersAreNonnullByDefault
    @NotNull List<Integer> getBackButton(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the search button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the search button.
     */
    @ParametersAreNonnullByDefault
    @NotNull List<Integer> getSearchButton(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the previous button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the previous button.
     */
    @ParametersAreNonnullByDefault
    @NotNull List<Integer> getPreviousButton(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the next button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the next button.
     */
    @ParametersAreNonnullByDefault
    @NotNull List<Integer> getNextButton(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the bookmark button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the bookmark button.
     */
    @ParametersAreNonnullByDefault
    @NotNull List<Integer> getBookMark(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the item mark button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the item mark button.
     */
    @ParametersAreNonnullByDefault
    @NotNull List<Integer> getItemMark(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot numbers for the border in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot numbers for the border.
     */
    @ParametersAreNonnullByDefault
    @NotNull List<Integer> getBorder(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot numbers for the main contents in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot numbers for the main contents.
     */
    @ParametersAreNonnullByDefault
    @NotNull List<Integer> getMainContents(JEGSlimefunGuideImplementation implementation, Player player);
}
