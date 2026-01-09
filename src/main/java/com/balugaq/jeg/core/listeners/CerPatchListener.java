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
import org.bukkit.event.Listener;
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
        if (!CerPatchGuideOption.isEnabled(player)) {
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
