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

    @EventHandler
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
