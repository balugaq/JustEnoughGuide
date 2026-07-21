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

package com.balugaq.jeg.core.listeners;

import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.PatchEvent;
import com.balugaq.jeg.core.integrations.ItemPatchListener;
import com.balugaq.jeg.implementation.option.SlimefunIdDisplayGuideOption;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author balugaq
 * @since 2.0
 */
public class SlimefunIdPatchListener implements ItemPatchListener {
    public static final EnumSet<PatchScope> VALID_SCOPES = EnumSet.of(
        PatchScope.SlimefunItem,
        PatchScope.ItemMarkItem,
        PatchScope.BookMarkItem,
        PatchScope.SearchItem
    );

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSearch(PatchEvent event) {
        PatchScope scope = event.getPatchScope();
        if (notValid(scope)) {
            return;
        }

        Player player = event.getPlayer();
        if (disabledOption(player)) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (isTagged(stack)) {
            return;
        }

        patchItem(stack, scope);
    }

    public boolean notValid(PatchScope patchScope) {
        return !VALID_SCOPES.contains(patchScope);
    }

    public boolean disabledOption(Player player) {
        return !SlimefunIdDisplayGuideOption.instance().isEnabled(player);
    }

    @SuppressWarnings({"deprecation", "unused"})
    public void patchItem(@Nullable ItemStack itemStack, PatchScope scope) {
        if (itemStack == null) {
            return;
        }

        SlimefunItem sf = SlimefunItem.getByItem(itemStack);
        if (sf == null) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(ChatColors.color("&8slimefun:" + sf.getId().toLowerCase()));

        meta.setLore(lore);
        tagMeta(meta);
        itemStack.setItemMeta(meta);
    }
}
