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

package com.balugaq.jeg.core.integrations.slimeaeplugin;

import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import com.balugaq.jeg.api.recipe_complete.source.RecipeCompleteProvider;
import com.balugaq.jeg.api.recipe_complete.source.ItemSource;
import me.ddggdd135.guguslimefunlib.items.ItemKey;
import me.ddggdd135.slimeae.api.interfaces.IStorage;
import me.ddggdd135.slimeae.api.items.ItemRequest;
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
public interface SlimeAEPluginSource extends ItemSource {
    @Override
    default JavaPlugin plugin() {
        return SlimeAEPluginIntegrationMain.getPlugin();
    }

    default boolean handleable(RecipeCompleteSession session) {
        return !SlimeAEPluginIntegrationMain.findNearbyIStorages(session.getPlayer(), session.getLocation()).isEmpty();
    }

    @SuppressWarnings({"unused", "unchecked"})
    @Override
    @NonNegative
    default long getItemStack(RecipeCompleteSession session, ItemStack itemStack, long need) {
        Player player = session.getPlayer();
        // Issue #67
        Set<IStorage> networkStorages = (Set<IStorage>) session.getCache(this, Set.class);
        if (networkStorages == null) {
            networkStorages = SlimeAEPluginIntegrationMain.findNearbyIStorages(session.getPlayer(), session.getLocation());
            if (networkStorages.isEmpty()) return 0;

            session.setCache(this, networkStorages);
        }

        // get from networkStorage
        long got = 0;
        ItemKey key = new ItemKey(itemStack);
        for (var networkStorage : networkStorages) {
            ItemRequest request = new ItemRequest(key, need - got);
            ItemStack[] gotten = networkStorage
                .takeItem(request)
                .toItemStacks();
            if (gotten.length != 0) {
                got += gotten[0].getAmount();
            }
            if (got >= need) return got;
        }

        return got;
    }

    @Override
    default long countAmount(RecipeCompleteSession session, ItemStack template) {
        Set<IStorage> networkStorages = (Set<IStorage>) session.getCache(this, Set.class);
        if (networkStorages == null) {
            networkStorages = SlimeAEPluginIntegrationMain.findNearbyIStorages(session.getPlayer(), session.getLocation());
            if (networkStorages.isEmpty()) return 0;

            session.setCache(this, networkStorages);
        }

        // get from networkStorage
        long total = 0;
        for (var networkStorage : networkStorages) {
            total += networkStorage.getStorageUnsafe().getKey(new ItemKey(template));
        }

        return total;
    }

    @Override
    default int handleLevel() {
        return RecipeCompleteProvider.SLIME_AE_PLUGIN_HANDLE_LEVEL;
    }
}
