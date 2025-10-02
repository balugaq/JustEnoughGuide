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

package com.balugaq.jeg.core.integrations.slimehud;

import com.balugaq.jeg.api.objects.enums.HUDLocation;
import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"UnnecessaryUnicodeEscape", "SameReturnValue"})
public class HUDMachineInfoLocationGuideOption implements SlimefunGuideOption<HUDLocation> {
    public static final @NotNull HUDMachineInfoLocationGuideOption instance = new HUDMachineInfoLocationGuideOption();

    public static @NotNull HUDMachineInfoLocationGuideOption instance() {
        return instance;
    }

    public static @NotNull NamespacedKey key0() {
        return new NamespacedKey(JustEnoughGuide.getInstance(), "hud_machine_info_location");
    }

    public static HUDLocation getSelectedOption(@NotNull Player p) {
        return PersistentDataAPI.hasByte(p, key0())
                ? HUDLocation.values()[PersistentDataAPI.getByte(p, key0())]
                : HUDLocation.DEFAULT;
    }

    @Override
    public @NotNull SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key0();
    }

    @Override
    public @NotNull Optional<ItemStack> getDisplayItem(@NotNull Player p, ItemStack guide) {
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
                "&7\u21E8 &e点击切换为使用 " + (bossbar ? "动作栏" : actionbar ? "默认设置" : "Boss栏") + " 显示机器信息");
        return Optional.of(item);
    }

    @Override
    public void onClick(@NotNull Player p, @NotNull ItemStack guide) {
        HUDLocation current = getSelectedOption(p, guide).orElse(HUDLocation.DEFAULT);
        setSelectedOption(p, guide, current.next());
        JEGGuideSettings.openSettings(p, guide);
    }

    @Override
    public @NotNull Optional<HUDLocation> getSelectedOption(@NotNull Player p, ItemStack guide) {
        NamespacedKey key = getKey();
        byte ordinal = PersistentDataAPI.hasByte(p, key) ? PersistentDataAPI.getByte(p, key) : (byte) HUDLocation.DEFAULT.ordinal();
        HUDLocation value = HUDLocation.values()[ordinal];
        return Optional.of(value);
    }

    @Override
    public void setSelectedOption(@NotNull Player p, ItemStack guide, @NotNull HUDLocation value) {
        PersistentDataAPI.setByte(p, getKey(), (byte) value.ordinal());
    }
}
