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

package com.balugaq.jeg.implementation.guide;

import com.balugaq.jeg.api.editor.GroupResorter;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.utils.GuideUtil;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.guide.CheatSheetSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.SlimefunGuideItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings({"deprecation", "unused"})
@NullMarked
public class CheatGuideImplementation extends CheatSheetSlimefunGuide implements JEGSlimefunGuideImplementation {
    public final ItemStack item;

    public CheatGuideImplementation() {
        ItemMeta meta = SlimefunGuide.getItem(getMode()).getItemMeta();
        String name = "";
        if (meta != null) {
            name = meta.getDisplayName();
        }
        item = new SlimefunGuideItem(this, name);
    }

    // fallback
    @Deprecated
    public CheatGuideImplementation(boolean v1, boolean v2) {
        this();
    }

    @Override
    public List<ItemGroup> getVisibleItemGroups(Player p, PlayerProfile profile) {
        return GuideUtil.getVisibleItemGroupsCheat(p, profile, GroupResorter.isSelecting(p));
    }

    @Override
    public SlimefunGuideMode getMode() {
        return SlimefunGuideMode.CHEAT_MODE;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }
}
