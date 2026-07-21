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

import com.balugaq.jeg.api.cost.please_set_cer_patch_to_false_in_config_when_you_see_this.CERCalculator;
import com.balugaq.jeg.api.groups.CERRecipeGroup;
import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.PatchEvent;
import com.balugaq.jeg.core.integrations.ItemPatchListener;
import com.balugaq.jeg.implementation.option.CerPatchGuideOption;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("deprecation")
public class CerPatchListener implements ItemPatchListener {
    @EventHandler
    public void onSearch(PatchEvent event) {
        if (event.getPatchScope() != PatchScope.SearchItem) {
            return;
        }

        Player player = event.getPlayer();
        if (!CerPatchGuideOption.instance().isEnabled(player)) {
            return;
        }

        ItemStack is = event.getItemStack();
        if (isTagged(is)) {
            return;
        }

        SlimefunItem sf = SlimefunItem.getByItem(is);
        if (sf == null) {
            return;
        }

        double cer = CERCalculator.getCER(sf, SearchGroup.searchTerms.get(player.getUniqueId()));
        if (cer > 0.0D) {
            ItemMeta meta = is.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore == null) lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColors.color("&a机器性价比: " + CERRecipeGroup.FORMAT.format(cer)));
            meta.setLore(lore);
            tagMeta(meta);
            is.setItemMeta(meta);
        }
        event.setItemStack(is);
    }
}
