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

package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * A classic implementation of GuideGroup.
 *
 * @author balugaq
 * @since 1.3
 */
@SuppressWarnings({"unused", "SameParameterValue"})
@Getter
@NotDisplayInCheatMode
@NullMarked
public class ClassicGuideGroup extends GuideGroup {
    private static final int SIZE = 54;
    private static final boolean CLASSIC = true;

    protected ClassicGuideGroup(NamespacedKey key, ItemStack icon) {
        super(key, icon);
    }

    protected ClassicGuideGroup(NamespacedKey key, ItemStack icon, int tier) {
        super(key, icon, tier);
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public boolean isClassic() {
        return CLASSIC;
    }
}
