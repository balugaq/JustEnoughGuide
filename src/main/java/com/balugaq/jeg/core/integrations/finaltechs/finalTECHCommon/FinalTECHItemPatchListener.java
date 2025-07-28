/*
 * Copyright (c) 2024-2025 balugaq
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

package com.balugaq.jeg.core.integrations.finaltechs.finalTECHCommon;

import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.PatchEvent;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
public class FinalTECHItemPatchListener implements Listener {
    public static final EnumSet<PatchScope> VALID_SCOPES = EnumSet.of(
            PatchScope.SlimefunItem,
            PatchScope.ItemMarkItem,
            PatchScope.BookMarkItem,
            PatchScope.SearchItem,
            PatchScope.ItemRecipeIngredient);
    public static final String DEFAULT_INPUT_VALUE = "0";
    public static final String DEFAULT_OUTPUT_VALUE = "INFINITY";
    public static Class<?> class_ItemValueTable = null;
    public static Object ItemValueTableInstance = null;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean initValueTable() {
        if (class_ItemValueTable == null) {
            try {
                class_ItemValueTable = Class.forName("io.taraxacum.libs.slimefun.dto.ItemValueTable");
            } catch (ClassNotFoundException e) {
                Debug.trace(e);
                return false;
            }
        }

        if (ItemValueTableInstance == null) {
            ItemValueTableInstance = ReflectionUtil.invokeStaticMethod(class_ItemValueTable, "getInstance");
        }

        return ItemValueTableInstance != null;
    }

    public static String getOrCalItemInputValue(@Nullable ItemStack itemStack) {
        if (!initValueTable()) {
            return DEFAULT_INPUT_VALUE;
        }

        if (itemStack == null) {
            return DEFAULT_INPUT_VALUE;
        }

        return (String) ReflectionUtil.invokeMethod(ItemValueTableInstance, "getOrCalItemInputValue", itemStack);
    }

    public static String getOrCalItemOutputValue(@Nullable ItemStack itemStack) {
        if (!initValueTable()) {
            return DEFAULT_OUTPUT_VALUE;
        }

        if (itemStack == null) {
            return DEFAULT_OUTPUT_VALUE;
        }

        return (String) ReflectionUtil.invokeMethod(ItemValueTableInstance, "getOrCalItemOutputValue", itemStack);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void patchItem(@NotNull PatchEvent event) {
        PatchScope scope = event.getPatchScope();
        if (notValid(scope)) {
            return;
        }

        Player player = event.getPlayer();
        if (disabledOption(player)) {
            return;
        }

        patchItem(event.getItemStack(), scope);
    }

    public boolean notValid(@NotNull PatchScope patchScope) {
        return !VALID_SCOPES.contains(patchScope);
    }

    public boolean disabledOption(@NotNull Player player) {
        return !FinalTECHValueDisplayOption.isEnabled(player);
    }

    @SuppressWarnings("deprecation")
    public void patchItem(@Nullable ItemStack itemStack, @NotNull PatchScope scope) {
        if (itemStack == null) {
            return;
        }

        if (scope == PatchScope.ItemRecipeIngredient
                && StackUtils.itemsMatch(itemStack, new ItemStack(itemStack.getType()))) {
            // Do not process vanilla item
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        String inputEmc = getOrCalItemInputValue(itemStack);
        String outputEmc = getOrCalItemOutputValue(itemStack);

        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add(ChatColors.color("&7FinalTECH Input EMC: &6" + inputEmc));
        lore.add(ChatColors.color("&7FinalTECH Output EMC: &6" + outputEmc));
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
    }
}
