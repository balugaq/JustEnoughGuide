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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("DataFlowIssue")
@NullMarked
public class NetworksIntegrationMain implements Integration {
    // @formatter:off
    public static final int[] ENCODER_RECIPE_SLOTS = new int[] {12, 13, 14, 21, 22, 23, 30, 31, 32};
    public static final int[] CRAFTING_GRID_RECIPE_SLOTS = new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26};
    public static final int[] QUANTUM_WORKBENCH_RECIPE_SLOTS = new int[] {10, 11, 12, 19, 20, 21, 28, 29, 30};
    // a trick
    public static final int[] PUSHER_SLOTS = new int[] {
            0,  1,  2,  3,  4,  5,  6,  7,  8,
            9,  10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44,
            45, 46, 47, 48, 49, 50, 51, 52, 53
    };
    // @formatter:on
    public static final List<SlimefunItem> handledSlimefunItems = new ArrayList<>();
    public static final BlockFace[] VALID_FACES = new BlockFace[]{
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

    public static Set<NetworkRoot> findNearbyNetworkRoots(Location location) {
        Set<NetworkRoot> roots = new HashSet<>();

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
                roots.add(def2.getNode().getRoot());
            }
        }

        return roots;
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
        rrc("NTW_PUSHER", PUSHER_SLOTS, true);
        rrc("NTW_MOREPUSHER", PUSHER_SLOTS, true);
        rrc("NTW_BESTPUSHER", PUSHER_SLOTS, true);
        rrc("NTW_IMPORTER", PUSHER_SLOTS, true);

        RecipeCompletableRegistry.registerPlayerInventoryItemGetter(new QuantumStoragePlayerInventoryItemSeeker());
    }

    @Override
    public void onDisable() {
        for (SlimefunItem slimefunItem : handledSlimefunItems) {
            RecipeCompletableRegistry.unregisterRecipeCompletable(slimefunItem);
        }
    }
}
