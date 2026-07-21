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

import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.PatchEvent;
import com.balugaq.jeg.core.integrations.ItemPatchListener;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@NullMarked
public class FinalTECHItemPatchListener implements ItemPatchListener {
    public static final EnumSet<PatchScope> VALID_SCOPES = EnumSet.of(
        PatchScope.SlimefunItem,
        PatchScope.ItemMarkItem,
        PatchScope.BookMarkItem,
        PatchScope.SearchItem
    );
    public static final String DEFAULT_INPUT_VALUE = "0";
    public static final String DEFAULT_OUTPUT_VALUE = "INFINITY";
    public static @UnknownNullability Class<?> class_ItemValueTable = null;
    public static @UnknownNullability Object ItemValueTableInstance = null;

    @Nullable
    public static String getOrCalItemInputValue(@Nullable ItemStack itemStack) {
        if (!initValueTable()) {
            return DEFAULT_INPUT_VALUE;
        }

        if (itemStack == null) {
            return DEFAULT_INPUT_VALUE;
        }

        return (String) ReflectionUtil.invokeMethod(ItemValueTableInstance, "getOrCalItemInputValue", itemStack);
    }

    @Nullable
    public static String getOrCalItemOutputValue(@Nullable ItemStack itemStack) {
        if (!initValueTable()) {
            return DEFAULT_OUTPUT_VALUE;
        }

        if (itemStack == null) {
            return DEFAULT_OUTPUT_VALUE;
        }

        return (String) ReflectionUtil.invokeMethod(ItemValueTableInstance, "getOrCalItemOutputValue", itemStack);
    }

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void patchItem(PatchEvent event) {
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
        return !FinalTECHValueDisplayGuideOption.isEnabled(player);
    }

    @SuppressWarnings("deprecation")
    public void patchItem(@Nullable ItemStack itemStack, PatchScope scope) {
        if (itemStack == null) {
            return;
        }

        if (StackUtils.itemsMatch(itemStack, new ItemStack(itemStack.getType()))) {
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

        lore.add(ChatColors.color("&7新乱序输入EMC: &6" + inputEmc));
        lore.add(ChatColors.color("&7新乱序输出EMC: &6" + outputEmc));
        meta.setLore(lore);
        tagMeta(meta);
        itemStack.setItemMeta(meta);
    }
}
