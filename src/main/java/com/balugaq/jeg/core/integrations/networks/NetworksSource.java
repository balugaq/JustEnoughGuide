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
import com.balugaq.jeg.api.recipe_complete.source.RecipeCompleteProvider;
import com.balugaq.jeg.api.recipe_complete.source.ItemSource;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.netex.api.interfaces.FeedbackSendable;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

/**
 * @author balugaq
 * @since 2.0
 */
@NullMarked
public interface NetworksSource extends ItemSource {
    @Override
    default JavaPlugin plugin() {
        return NetworksIntegrationMain.getPlugin();
    }

    default boolean handleable(RecipeCompleteSession session) {
        return !NetworksIntegrationMain.findNearbyNetworkRoots(session.getPlayer(), session.getLocation()).isEmpty();
    }

    @Override
    @SuppressWarnings({"unchecked", "removal"})
    @NonNegative
    default long getItemStack(RecipeCompleteSession session, ItemStack itemStack, long need) {
        Player player = session.getPlayer();
        // Issue #67
        Set<NetworkRoot> roots = (Set<NetworkRoot>) session.getCache(this, Set.class);
        if (roots == null) {
            roots = NetworksIntegrationMain.findNearbyNetworkRoots(session.getPlayer(), session.getLocation());
            if (roots.isEmpty()) return 0;

            session.setCache(this, roots);
        }

        // get from root
        ItemRequest request = new ItemRequest(itemStack, (int) need);
        long got = 0;
        var loc = player.getLocation();
        for (var root : roots) {
            if (JustEnoughGuide.getIntegrationManager().isEnabledNetworksExpansion()) {
                boolean subscribedBefore = FeedbackSendable.hasSubscribed(player, loc);
                // 这样玩家可以看到网拓发的 Feedback 信息
                FeedbackSendable.subscribe(player, loc);
                // 以玩家位置为单位支持限流
                var gotten = root.getItemStack0(loc, request);
                if (gotten != null && gotten.getType() != Material.AIR) {
                    got += gotten.getAmount();
                }
                if (!subscribedBefore) FeedbackSendable.unsubscribe(player, loc); // 恢复
                if (got >= need) return got;
            } else {
                var gotten = root.getItemStack(request);
                if (gotten != null && gotten.getType() != Material.AIR) {
                    got += gotten.getAmount();
                }
                if (got >= need) return got;
            }
        }

        return got;
    }

    @Override
    default long countAmount(RecipeCompleteSession session, ItemStack template) {
        Set<NetworkRoot> roots = (Set<NetworkRoot>) session.getCache(this, Set.class);
        if (roots == null) {
            roots = NetworksIntegrationMain.findNearbyNetworkRoots(session.getPlayer(), session.getLocation());
            if (roots.isEmpty()) return 0;

            session.setCache(this, roots);
        }

        long total = 0;
        for (var root : roots) {
            var got = root.getAmount(template);
            total += got;
        }

        return total;
    }

    @Override
    default int handleLevel() {
        return RecipeCompleteProvider.NETWORKS_HANDLE_LEVEL;
    }
}
