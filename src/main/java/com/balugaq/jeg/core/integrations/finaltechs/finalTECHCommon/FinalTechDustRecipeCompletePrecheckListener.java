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

package com.balugaq.jeg.core.integrations.finaltechs.finalTECHCommon;

import com.balugaq.jeg.api.objects.events.RecipeCompleteEvents;
import com.balugaq.jeg.core.integrations.ItemPatchListener;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings("ConstantValue")
public class FinalTechDustRecipeCompletePrecheckListener implements Listener {
    @Setter
    @Getter
    public static boolean booted = false;

    @EventHandler(ignoreCancelled = true)
    public void onRecipeComplete(RecipeCompleteEvents.SessionStartEvent event) {
        var session = event.getSession();
        BlockMenu menu = session.getMenu();
        if (menu == null) return;
        String machineId = menu.getPreset().getID();
        if (!machineId.equals("FINALTECH_ORDERED_DUST_FACTORY_STONE") && !machineId.equals("_FINALTECH_ORDERED_DUST_FACTORY_STONE")) {
            return;
        }

        ItemStack target = ItemPatchListener.untag(session.getEvent().getClickedItem());
        SlimefunItem sf = SlimefunItem.getByItem(target);
        if (sf == null) return;
        String id = sf.getId();
        if (!id.equals("FINALTECH_ORDERED_DUST") && !id.equals("FINALTECH_UNORDERED_DUST") && !id.equals("_FINALTECH_ORDERED_DUST") && !id.equals("_FINALTECH_UNORDERED_DUST")) {
            return;
        }

        for (var slot : menu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.INSERT)) {
            var existing = menu.getItemInSlot(slot);
            if (existing != null && existing.getType() != Material.AIR) {
                event.setCancelled(true);
                event.setCancelReason("&c输入槽中有物品");
                return;
            }
        }

        // Issue #63
        event.getSession().setTimes(1);
    }
}
