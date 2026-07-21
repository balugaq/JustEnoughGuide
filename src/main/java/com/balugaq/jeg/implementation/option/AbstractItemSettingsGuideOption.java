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

package com.balugaq.jeg.implementation.option;

import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.api.patches.PrioritySlimefunGuideOption;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author balugaq
 * @since 2.0
 */
@SuppressWarnings("deprecation")
@NullMarked
public abstract class AbstractItemSettingsGuideOption implements PrioritySlimefunGuideOption<Boolean> {
    public static final ItemStack DEFAULT_ICON = Converter.getItem(
        Material.BARRIER,
        "&c未设置物品",
        "&c手持物品点击设置"
    );

    private static NamespacedKey getKey(NamespacedKey key, int index) {
        return KeyUtil.append(key, "_item_" + index);
    }

    @Nullable
    public static ItemStack getItem(Player p, NamespacedKey k, int index) {
        var key = getKey(k, index);
        String s = PersistentDataAPI.getString(p, key);
        if (s == null) return null;
        if (s.startsWith("sf:")) {
            SlimefunItem sf = SlimefunItem.getById(s.substring(3));
            if (sf == null) return null;
            return Converter.getItem(sf.getItem());
        } else if (s.startsWith("mc:")) {
            Material material = Material.getMaterial(s.substring(3).toUpperCase());
            if (material == null) return null;
            return Converter.getItem(material);
        } else {
            return null;
        }
    }

    private static ItemStack getIconOrDefault(Player p, NamespacedKey k, int index) {
        ItemStack ri = getItem(p, k, index);
        if (ri == null) {
            return DEFAULT_ICON;
        }

        ItemStack item = ri.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return Converter.getItem(item, "&f空气");

        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColors.color("&c已设置物品"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static List<RecipeChoice> generateChoices(ItemStack itemStack, int... amounts) {
        return generateChoices(new ItemStack[]{itemStack}, amounts);
    }

    public static List<RecipeChoice> generateChoices(@Nullable ItemStack[] itemStacks, int... amounts) {
        int i = 0;
        List<RecipeChoice> choices = new ArrayList<>();
        for (int amount : amounts) {
            var item = itemStacks[i % itemStacks.length];
            if (item == null) continue;
            choices.add(new RecipeChoice.ExactChoice(Converter.getItem(item, amount)));
            i++;
        }
        return choices;
    }

    @Override
    public SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        openItemSettingsGui(p);
    }

    @Override
    public Optional<Boolean> getSelectedOption(Player p, ItemStack guide) {
        return Optional.of(false);
    }

    @Override
    public void setSelectedOption(Player p, ItemStack guide, Boolean value) {
    }

    public abstract String getTitle();

    @SuppressWarnings("SameReturnValue")
    public abstract int getSize();

    public abstract int[] getItemSlots();

    public ChestMenu getMenu(Player p) {
        ChestMenu menu = new ChestMenu(getTitle(), getSize());
        for (int i = 0; i < getSize(); i++) {
            menu.addItem(i, PatchScope.Background.patch(p, ChestMenuUtils.getBackground()), ChestMenuUtils.getEmptyClickHandler());
        }
        menu.addItem(
            1, ChestMenuUtils.getBackButton(p), (pl, s, is, action) -> EventUtil.callEvent(
                    new GuideEvents.BackButtonClickEvent(pl, is, s, action, menu, GuideUtil.getLastGuide(pl)))
                .ifSuccess(() ->
                    JEGGuideSettings.openSettings(pl, GuideUtil.getLastGuide(pl).getItem())
                )
        );
        return menu;
    }

    @SuppressWarnings("ConstantValue")
    private void openItemSettingsGui(Player p) {
        ChestMenu menu = getMenu(p);

        for (int slot : getItemSlots()) {
            menu.addItem(
                slot, getIconOrDefault(p, getKey(), slot), ((player, i, itemStack, clickAction) -> {
                    ItemStack cursor = player.getItemOnCursor();
                    if (cursor == null || cursor.getType() == Material.AIR) {
                        return false;
                    }

                    setItemStack(p, getKey(), i, cursor);
                    openItemSettingsGui(player);

                    return false;
                })
            );
        }

        menu.setEmptySlotsClickable(false);
        menu.setPlayerInventoryClickable(true);
        menu.open(p);
    }

    private void setItemStack(Player p, NamespacedKey k, int index, String item) {
        var key = getKey(k, index);
        PersistentDataAPI.setString(p, key, item);
    }

    private void setItemStack(Player p, NamespacedKey k, int index, ItemStack item) {
        SlimefunItem sf = SlimefunItem.getByItem(item);
        if (sf == null) {
            Material material = item.getType();
            setItemStack(p, k, index, "mc:" + material.name().toLowerCase());
        } else {
            setItemStack(p, k, index, "sf:" + sf.getId());
        }
    }
}
