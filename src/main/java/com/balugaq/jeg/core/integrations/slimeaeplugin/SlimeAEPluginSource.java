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

/**
 * @author balugaq
 * @since 2.0
 */
@NullMarked
public interface SlimeAEPluginSource extends Source {
    default boolean handleable(RecipeCompleteSession session) {
        return SlimeAEPluginIntegrationMain.findNearbyIStorage(session.getLocation()) != null;
    }

    @Override
    default int handleLevel() {
        return RecipeCompleteProvider.SLIME_AE_PLUGIN_HANDLE_LEVEL;
    }

    @SuppressWarnings("unused")
    @Override
    @Nullable
    default ItemStack getItemStack(RecipeCompleteSession session, ItemStack itemStack) {
        Player player = session.getPlayer();
        IStorage networkStorage = session.getCache(this, IStorage.class);
        if (networkStorage == null) {
            networkStorage = SlimeAEPluginIntegrationMain.findNearbyIStorage(session.getLocation());
            if (networkStorage == null) return null;

            session.setCache(this, networkStorage);
        }

        // get from networkStorage
        ItemStack[] gotten = networkStorage
                .takeItem(new ItemRequest(new ItemKey(itemStack), Math.max(1, Math.min(itemStack.getAmount(), itemStack.getMaxStackSize()))))
                .toItemStacks();
        if (gotten.length != 0) {
            return gotten[0];
        }

        return null;
    }

    @Override
    default JavaPlugin plugin() {
        return SlimeAEPluginIntegrationMain.getPlugin();
    }

}
