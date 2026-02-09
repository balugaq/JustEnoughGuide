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

package com.balugaq.jeg.core.integrations.networks;

import com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry;
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.core.integrations.Integration;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("DataFlowIssue")
@NullMarked
public class NetworksIntegrationMain implements Integration {
    public static final int[] ENCODER_RECIPE_SLOTS = new int[] {12, 13, 14, 21, 22, 23, 30, 31, 32};
    public static final int[] CRAFTING_GRID_RECIPE_SLOTS = new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26};
    public static final int[] QUANTUM_WORKBENCH_RECIPE_SLOTS = new int[] {10, 11, 12, 19, 20, 21, 28, 29, 30};
    public static final List<SlimefunItem> handledSlimefunItems = new ArrayList<>();
    public static final BlockFace[] VALID_FACES = new BlockFace[] {
            BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST
    };
    public static @Nullable JavaPlugin plugin = null;

    public static JavaPlugin getPlugin() {
        if (plugin == null) {
            plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Networks");
        }

        if (plugin == null) {
            plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Networks-Changed");
        }

        return plugin;
    }

    @Nullable
    public static NetworkRoot findNearbyNetworkRoot(Location location) {
        NetworkRoot root = null;

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
            NodeDefinition def2 = NetworkStorage.getNode(clone);
            if (def2 != null && def2.getNode() != null) {
                root = def2.getNode().getRoot();
                break;
            }
        }

        return root;
    }

    @Override
    public String getHookPlugin() {
        return "Networks";
    }

    @Override
    public void onEnable() {
        RecipeCompleteProvider.addSource(new NetworksRecipeCompleteSlimefunSource());
        RecipeCompleteProvider.addSource(new NetworksRecipeCompleteVanillaSource());

        rrc("NTW_RECIPE_ENCODER", ENCODER_RECIPE_SLOTS, false);
        rrc("NTW_CRAFTING_GRID", CRAFTING_GRID_RECIPE_SLOTS, false);
        rrc("NTW_QUANTUM_WORKBENCH", QUANTUM_WORKBENCH_RECIPE_SLOTS, false);
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
