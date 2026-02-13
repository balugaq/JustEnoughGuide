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

import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.api.recipe_complete.source.base.Source;
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
            try {
                var got = root.getItemStack0(player.getLocation(), request);
                if (got != null && got.getType() != Material.AIR) {
                    return got;
                }
            } catch (Exception e) {
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
