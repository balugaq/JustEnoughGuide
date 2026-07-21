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
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.api.recipe_complete.source.base.Source;
import me.ddggdd135.guguslimefunlib.items.ItemKey;
import me.ddggdd135.slimeae.api.interfaces.IStorage;
import me.ddggdd135.slimeae.api.items.ItemRequest;
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
public interface SlimeAEPluginSource extends Source {
    @Override
    default JavaPlugin plugin() {
        return SlimeAEPluginIntegrationMain.getPlugin();
    }

    default boolean handleable(RecipeCompleteSession session) {
        return !SlimeAEPluginIntegrationMain.findNearbyIStorages(session.getLocation()).isEmpty();
    }

    @SuppressWarnings({"unused", "unchecked"})
    @Override
    @Nullable
    default ItemStack getItemStack(RecipeCompleteSession session, ItemStack itemStack) {
        Player player = session.getPlayer();
        // Issue #67
        Set<IStorage> networkStorages = (Set<IStorage>) session.getCache(this, Set.class);
        if (networkStorages == null) {
            networkStorages = SlimeAEPluginIntegrationMain.findNearbyIStorages(session.getLocation());
            if (networkStorages.isEmpty()) return null;

            session.setCache(this, networkStorages);
        }

        // get from networkStorage
        ItemRequest request = new ItemRequest(new ItemKey(itemStack), Math.max(1, Math.min(itemStack.getAmount(), itemStack.getMaxStackSize())));
        for (var networkStorage : networkStorages) {
            ItemStack[] gotten = networkStorage
                .takeItem(request)
                .toItemStacks();
            if (gotten.length != 0) {
                return gotten[0];
            }
        }

        return null;
    }

    @Override
    default int handleLevel() {
        return RecipeCompleteProvider.SLIME_AE_PLUGIN_HANDLE_LEVEL;
    }

}
