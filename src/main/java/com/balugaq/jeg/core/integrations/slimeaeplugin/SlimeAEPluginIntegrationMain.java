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

package com.balugaq.jeg.core.integrations.slimeaeplugin;

import com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry;
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.ddggdd135.slimeae.SlimeAEPlugin;
import me.ddggdd135.slimeae.api.interfaces.IStorage;
import me.ddggdd135.slimeae.core.NetworkInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"DataFlowIssue", "ConstantValue"})
@NullMarked
public class SlimeAEPluginIntegrationMain implements Integration {
    public static final int[] CRAFTING_TERMINAL_INPUT_SLOTS = new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26};
    public static final int[] PATTERN_TERMINAL_INPUT_SLOTS = new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26};
    public static final int[] PATTERN_WORKBENCH_INPUT_SLOTS = new int[] {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26
    };
    public static final List<SlimefunItem> handledSlimefunItems = new ArrayList<>();
    public static final BlockFace[] VALID_FACES = new BlockFace[] {
            BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST
    };
    public static JavaPlugin plugin = null;

    public static JavaPlugin getPlugin() {
        if (plugin == null) {
            plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("SlimeAEPlugin");
        }

        return plugin;
    }

    public static Set<IStorage> findNearbyIStorages(Location location) {
        Set<IStorage> networkStorages = new HashSet<>();

        for (BlockFace blockFace : VALID_FACES) {
            Location clone = location.clone();
            switch (blockFace) {
                case NORTH -> clone.set(clone.getBlockX(), clone.getBlockY(), clone.getBlockZ() - 1);
                case EAST -> clone.set(clone.getBlockX() + 1, clone.getBlockY(), clone.getBlockZ());
                case SOUTH -> clone.set(clone.getBlockX(), clone.getBlockY(), clone.getBlockZ() + 1);
                case WEST -> clone.set(clone.getBlockX() - 1, clone.getBlockY(), clone.getBlockZ());
                case UP -> clone.set(clone.getBlockX(), clone.getBlockY() + 1, clone.getBlockZ());
                case DOWN -> clone.set(clone.getBlockX(), clone.getBlockY() - 1, clone.getBlockZ());
            }
            NetworkInfo def2 = SlimeAEPlugin.getNetworkData().getNetworkInfo(clone);
            if (def2 != null) {
                var storage = getIStorage(def2);
                if (storage != null) {
                    networkStorages.add(storage);
                }
            }
        }

        return networkStorages;
    }

    @Nullable
    public static IStorage getIStorage(NetworkInfo info) {
        return (IStorage) ReflectionUtil.invokeMethod(info, "getStorage");
    }

    @Override
    public String getHookPlugin() {
        return "SlimeAEPlugin";
    }

    @Override
    public void onEnable() {
        if (JustEnoughGuide.getIntegrationManager().isEnabledGuguSlimefunLib()) {
            RecipeCompleteProvider.addSource(new SlimeAEPluginRecipeCompleteSlimefunSource());
            RecipeCompleteProvider.addSource(new SlimeAEPluginRecipeCompleteVanillaSource());
        }

        rrc("ME_CRAFTING_TERMINAL", CRAFTING_TERMINAL_INPUT_SLOTS, false);
        rrc("ME_PATTERN_TERMINAL", PATTERN_TERMINAL_INPUT_SLOTS, false);
        rrc("PATTERN_WORKBENCH", PATTERN_WORKBENCH_INPUT_SLOTS, true);
    }

    public static void rrc(String id, int[] slots, boolean unordered) {
        SlimefunItem slimefunItem = SlimefunItem.getById(id);
        if (slimefunItem != null) {
            rrc(slimefunItem, slots, unordered);
        }
    }

    public static void rrc(SlimefunItem slimefunItem, int[] slots, boolean unordered) {
        handledSlimefunItems.add(slimefunItem);
        RecipeCompletableRegistry.registerRecipeCompletable(slimefunItem, slots, unordered);
    }

    @Override
    public void onDisable() {
        for (SlimefunItem slimefunItem : handledSlimefunItems) {
            RecipeCompletableRegistry.unregisterRecipeCompletable(slimefunItem);
        }
    }
}
