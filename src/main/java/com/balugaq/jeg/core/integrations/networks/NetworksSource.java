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

import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.api.recipe_complete.source.base.Source;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

/**
 * @author balugaq
 * @since 2.0
 */
@NullMarked
public interface NetworksSource extends Source {
    @Override
    default JavaPlugin plugin() {
        return NetworksIntegrationMain.getPlugin();
    }

    default boolean handleable(RecipeCompleteSession session) {
        return !NetworksIntegrationMain.findNearbyNetworkRoots(session.getLocation()).isEmpty();
    }

    @Override
    @SuppressWarnings({"unchecked", "removal"})
    @Nullable
    default ItemStack getItemStack(RecipeCompleteSession session, ItemStack itemStack) {
        Player player = session.getPlayer();
        // Issue #67
        Set<NetworkRoot> roots = (Set<NetworkRoot>) session.getCache(this, Set.class);
        if (roots == null) {
            roots = NetworksIntegrationMain.findNearbyNetworkRoots(session.getLocation());
            if (roots.isEmpty()) return null;

            session.setCache(this, roots);
        }

        // get from root
        ItemRequest request = new ItemRequest(itemStack, Math.max(1, Math.min(itemStack.getAmount(), itemStack.getMaxStackSize())));
        for (var root : roots) {
            if (JustEnoughGuide.getIntegrationManager().isEnabledNetworksExpansion()) {
                var got = root.getItemStack0(player.getLocation(), request);
                if (got != null && got.getType() != Material.AIR) {
                    return got;
                }
            } else {
                var got = root.getItemStack(request);
                if (got != null && got.getType() != Material.AIR) {
                    return got;
                }
            }
        }

        return null;
    }

    @Override
    default int handleLevel() {
        return RecipeCompleteProvider.NETWORKS_HANDLE_LEVEL;
    }
}
