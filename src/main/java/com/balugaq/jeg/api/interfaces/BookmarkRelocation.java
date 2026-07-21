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

package com.balugaq.jeg.api.interfaces;

import com.balugaq.jeg.api.groups.ItemMarkGroup;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * This interface defines the methods that a BookmarkRelocation implementation should implement.
 * Used for relocating the buttons of the guide to a different location.
 *
 * @author balugaq
 * @see ItemMarkGroup
 * @since 1.1
 */
@SuppressWarnings("SameReturnValue")
@NullMarked
public interface BookmarkRelocation {
    List<Integer> getBackButton(JEGSlimefunGuideImplementation implementation, Player player);
    List<Integer> getSearchButton(JEGSlimefunGuideImplementation implementation, Player player);
    List<Integer> getPreviousButton(JEGSlimefunGuideImplementation implementation, Player player);
    List<Integer> getNextButton(JEGSlimefunGuideImplementation implementation, Player player);
    List<Integer> getBookMark(JEGSlimefunGuideImplementation implementation, Player player);
    List<Integer> getItemMark(JEGSlimefunGuideImplementation implementation, Player player);
    List<Integer> getBorder(JEGSlimefunGuideImplementation implementation, Player player);
    List<Integer> getMainContents(JEGSlimefunGuideImplementation implementation, Player player);
}
