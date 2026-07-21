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

package com.balugaq.jeg.core.integrations.momotech;

import com.balugaq.jeg.api.objects.events.RecipeCompleteEvents;
import com.balugaq.jeg.core.integrations.ItemPatchListener;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings("ConstantValue")
public class MomotechCreativeItemRecipeCompletePrecheckListener implements Listener {
    @Setter
    @Getter
    public static boolean booted = false;

    @EventHandler(ignoreCancelled = true)
    public void onRecipeComplete(RecipeCompleteEvents.SessionStartEvent event) {
        var session = event.getSession();
        BlockMenu menu = session.getMenu();
        if (menu == null) return;
        String machineId = menu.getPreset().getID();
        if (!machineId.equals("MOMOTECH_CREATIVE_ITEM_GENERATOR") && !machineId.equals("MOMOTECH_NONE_GENERATOR")) {
            return;
        }

        ItemStack target = ItemPatchListener.untag(session.getEvent().getClickedItem());
        SlimefunItem sf = SlimefunItem.getByItem(target);
        if (sf == null) return;
        String id = sf.getId();
        if (!id.equals("MOMOTECH_CREATIVE") && !id.equals("MOMOTECH_CREATIVE_1") && !id.equals("MOMOTECH_NONE")) {
            return;
        }

        // Issue #63
        event.getSession().setTimes(1);
    }
}
