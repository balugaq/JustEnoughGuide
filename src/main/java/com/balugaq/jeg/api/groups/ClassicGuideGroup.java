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

package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A classic implementation of GuideGroup.
 *
 * @author balugaq
 * @since 1.3
 */
@SuppressWarnings("unused")
@Getter
@NotDisplayInCheatMode
public class ClassicGuideGroup extends GuideGroup {
    private static final int[] CONTENT_SLOTS = {
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44
    };

    private static final int BACK_SLOT = 1;
    private static final int SIZE = 54;
    private static final boolean CLASSIC = true;

    /**
     * Constructor.
     *
     * @param key  The key of the group.
     * @param icon The icon of the group.
     */
    protected ClassicGuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon) {
        super(key, icon);
    }

    /**
     * Constructor.
     *
     * @param key  The key of the group.
     * @param icon The icon of the group.
     * @param tier The tier of the group.
     */
    protected ClassicGuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon, int tier) {
        super(key, icon, tier);
    }

    /**
     * Returns the size of the group.
     *
     * @return The size of the group.
     */
    @Override
    public int getSize() {
        return SIZE;
    }

    /**
     * Returns whether the group is classic or not.
     *
     * @return Whether the group is classic or not.
     */
    @Override
    public boolean isClassic() {
        return CLASSIC;
    }

    /**
     * Returns the slots where the content of the group can be placed.
     *
     * @return The slots where the content of the group can be placed.
     */
    @Override
    public int[] getContentSlots() {
        return CONTENT_SLOTS;
    }

    /**
     * Returns the slot where the back of the group can be placed.
     *
     * @return The slot where the back of the group can be placed.
     */
    @Override
    public int getBackSlot() {
        return BACK_SLOT;
    }
}
