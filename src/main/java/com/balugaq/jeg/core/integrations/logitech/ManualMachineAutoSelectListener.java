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

package com.balugaq.jeg.core.integrations.logitech;

import com.balugaq.jeg.api.objects.events.RecipeCompleteEvents;
import com.balugaq.jeg.core.integrations.ItemPatchListener;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings("ConstantValue")
public class ManualMachineAutoSelectListener implements Listener {
    @EventHandler
    public void onComplete(RecipeCompleteEvents.SessionCompleteEvent event) {
        var session = event.getSession();
        BlockMenu menu = session.getMenu();
        if (menu == null) return;
        if (!menu.getPreset().getID().endsWith("_MANUAL")) return;

        SlimefunItem sf = menu.getPreset().getSlimefunItem();
        if (!sf.getAddon().equals(LogitechIntegrationMain.getPlugin())) return;

        ItemStack target = ItemPatchListener.untag(event.getSession().getEvent().getClickedItem());
        try {
            // Logitech v1.0.3
            if (sf instanceof me.matl114.logitech.SlimefunItem.Machines.AbstractManual mm) {
                var rps = mm.getMachineRecipes(menu.getBlock(), menu);
                int targetIndex = getTargetIndex(rps, target);
                if (targetIndex == -1) return;

                int index = me.matl114.logitech.Utils.DataCache.getLastRecipe(menu.getLocation());
                if (index == -1) {
                    mm.updateMenu(menu, menu.getBlock(), me.matl114.logitech.Utils.Settings.RUN);
                } else {
                    int delta = targetIndex - index;
                    if (delta > 0) {
                        for (int i = 0; i < delta; i++)
                            mm.orderSearchRecipe(menu, me.matl114.logitech.Utils.Settings.SEQUNTIAL);
                    }
                    if (delta < 0) {
                        for (int i = 0; i < -delta; i++)
                            mm.orderSearchRecipe(menu, me.matl114.logitech.Utils.Settings.REVERSE);
                    }

                    mm.updateMenu(menu, menu.getBlock(), me.matl114.logitech.Utils.Settings.RUN);
                }
            }
        } catch (Throwable ignored) {
            try {
                if (sf instanceof me.matl114.logitech.core.Machines.Abstracts.AbstractManual mm) {
                    var rps = mm.getMachineRecipes(menu.getBlock(), menu);
                    int targetIndex = getTargetIndex(rps, target);
                    if (targetIndex == -1) return;

                    int index = me.matl114.logitech.utils.DataCache.getLastRecipe(menu.getLocation());
                    try {
                        // Logitech v1.0.4
                        if (index == -1) {
                            mm.updateMenu(menu, menu.getBlock(), me.matl114.logitech.utils.Settings.RUN);
                        } else {
                            int delta = targetIndex - index;
                            if (delta > 0) {
                                for (int i = 0; i < delta; i++)
                                    mm.orderSearchRecipe(menu, me.matl114.logitech.utils.Settings.SEQUNTIAL);
                            }
                            if (delta < 0) {
                                for (int i = 0; i < -delta; i++)
                                    mm.orderSearchRecipe(menu, me.matl114.logitech.utils.Settings.REVERSE);
                            }

                            mm.updateMenu(menu, menu.getBlock(), me.matl114.logitech.utils.Settings.RUN);
                        }
                    } catch (Throwable ignored2) {
                        try {
                            // Logitech 2025-07-07
                            if (index == -1) {
                                mm.updateMenu(menu, menu.getBlock(), me.matl114.logitech.utils.Settings.RUN);
                            } else {
                                int delta = targetIndex - index;
                                if (delta > 0) {
                                    for (int i = 0; i < delta; i++)
                                        ReflectionUtil.invokeMethod(mm, "orderSearchRecipe", menu, me.matl114.logitech.utils.Settings.SEQUNTIAL, true);
                                }
                                if (delta < 0) {
                                    for (int i = 0; i < -delta; i++)
                                        ReflectionUtil.invokeMethod(mm, "orderSearchRecipe", menu, me.matl114.logitech.utils.Settings.REVERSE, true);
                                }

                                mm.updateMenu(menu, menu.getBlock(), me.matl114.logitech.utils.Settings.RUN);
                            }
                        } catch (Throwable ignored3) {
                        }
                    }
                }
            } catch (Throwable ignored2) {
            }
        }
    }

    public static int getTargetIndex(List<MachineRecipe> rps, ItemStack clicked) {
        int targetIndex = -1;
        for (int i = 0; i < rps.size(); i++) {
            if (StackUtils.itemsMatch(rps.get(i).getOutput()[0], clicked)) {
                // found
                targetIndex = i;
                break;
            }
        }
        return targetIndex;
    }
}
