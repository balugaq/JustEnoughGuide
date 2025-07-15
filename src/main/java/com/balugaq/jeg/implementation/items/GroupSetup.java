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

package com.balugaq.jeg.implementation.items;

import com.balugaq.jeg.api.groups.HiddenItemsGroup;
import com.balugaq.jeg.api.groups.VanillaItemsGroup;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.Lang;
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.SlimefunRegistryUtil;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

/**
 * This class is responsible for registering all the JEG groups.
 *
 * @author balugaq
 * @since 1.3
 */
public class GroupSetup {
    public static JEGGuideGroup guideGroup;
    public static HiddenItemsGroup hiddenItemsGroup;
    public static VanillaItemsGroup vanillaItemsGroup;
    public static ItemGroup jegItemsGroup;

    /**
     * Registers all the JEG groups.
     */
    public static void setup() {
        guideGroup = new JEGGuideGroup(
                new NamespacedKey(JustEnoughGuide.getInstance(), "jeg_guide_group"),
                Lang.getIcon("usage-guide", Material.KNOWLEDGE_BOOK));
        guideGroup.register(JustEnoughGuide.getInstance());
        hiddenItemsGroup = new HiddenItemsGroup(
                new NamespacedKey(JustEnoughGuide.getInstance(), "hidden_items_group"),
                Lang.getIcon("hidden-items", Material.BARRIER));
        hiddenItemsGroup.register(JustEnoughGuide.getInstance());
        vanillaItemsGroup = new VanillaItemsGroup(KeyUtil.newKey("vanilla_items_group"), Lang.VANILLA_ITEMS_GROUP_ITEM);
        vanillaItemsGroup.register(JustEnoughGuide.getInstance());

        jegItemsGroup = new ItemGroup(KeyUtil.newKey("jeg_items_group"), Lang.JEG_ITEMS_GROUP_ITEM);
        jegItemsGroup.setTier(Integer.MAX_VALUE);
    }

    /**
     * Unregisters all the JEG groups.
     */
    public static void shutdown() {
        SlimefunRegistryUtil.unregisterItemGroups(JustEnoughGuide.getInstance());
    }
}
