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

package com.balugaq.jeg.core.integrations.slimehud;

import com.balugaq.jeg.api.objects.enums.HUDLocation;
import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.api.patches.Priorities;
import com.balugaq.jeg.api.patches.PrioritySlimefunGuideOption;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"UnnecessaryUnicodeEscape", "SameReturnValue"})
@NullMarked
public class HUDMachineInfoLocationGuideOption implements PrioritySlimefunGuideOption<HUDLocation> {
    public static final HUDMachineInfoLocationGuideOption instance = new HUDMachineInfoLocationGuideOption();

    public static HUDMachineInfoLocationGuideOption instance() {
        return instance;
    }

    public static HUDLocation getSelectedOption(Player p) {
        return PersistentDataAPI.hasByte(p, key0())
            ? HUDLocation.values()[PersistentDataAPI.getByte(p, key0())]
            : HUDLocation.DEFAULT;
    }

    public static NamespacedKey key0() {
        return new NamespacedKey(JustEnoughGuide.getInstance(), "hud_machine_info_location");
    }

    @Override
    public SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @Override
    public int priority() {
        return Priorities.HUDMachineInfoLocationGuideOption;
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        HUDLocation current = getSelectedOption(p, guide).orElse(HUDLocation.DEFAULT);
        boolean bossbar = current == HUDLocation.BOSSBAR;
        boolean actionbar = current == HUDLocation.ACTION_BAR;
        ItemStack item = Converter.getItem(
            bossbar ? Material.GLOW_ITEM_FRAME : actionbar ? Material.ITEM_FRAME : Material.ACACIA_BOAT,
            "&bHUD显示机器信息位置: &" + (bossbar ? "aBoss栏" : actionbar ? "b动作栏" : "e默认设置"),
            "",
            "&7你现在可以自主选择是否",
            "&7在使用SlimeHUD显示机器信息时",
            "&7将机器信息显示在Boss栏或动作栏",
            "",
            "&7\u21E8 &e点击切换为使用 " + (bossbar ? "动作栏" : actionbar ? "默认设置" : "Boss栏") + " 显示机器信息"
        );
        return Optional.of(item);
    }

    @Override
    public NamespacedKey getKey() {
        return key0();
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        HUDLocation current = getSelectedOption(p, guide).orElse(HUDLocation.DEFAULT);
        setSelectedOption(p, guide, current.next());
        JEGGuideSettings.openSettings(p, guide);
    }

    @Override
    public Optional<HUDLocation> getSelectedOption(Player p, ItemStack guide) {
        NamespacedKey key = getKey();
        byte ordinal = PersistentDataAPI.hasByte(p, key) ? PersistentDataAPI.getByte(p, key) :
            (byte) HUDLocation.DEFAULT.ordinal();
        HUDLocation value = HUDLocation.values()[ordinal];
        return Optional.of(value);
    }

    @Override
    public void setSelectedOption(Player p, ItemStack guide, HUDLocation value) {
        PersistentDataAPI.setByte(p, getKey(), (byte) value.ordinal());
    }
}
