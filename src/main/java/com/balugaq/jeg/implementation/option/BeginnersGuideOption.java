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

package com.balugaq.jeg.implementation.option;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Lang;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is used to represent the option to show the beginner's guide.
 * which is editable in the settings menu.
 *
 * @author balugaq
 * @since 1.5
 */
public class BeginnersGuideOption implements SlimefunGuideOption<Boolean> {

    public static @NotNull NamespacedKey key() {
        return new NamespacedKey(JustEnoughGuide.getInstance(), "beginners_guide");
    }

    public static boolean isEnabled(Player p) {
        return getSelectedOption(p);
    }

    public static boolean getSelectedOption(Player p) {
        return !PersistentDataAPI.hasByte(p, key()) || PersistentDataAPI.getByte(p, key()) == (byte) 1;
    }

    @Override
    public @NotNull SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key();
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        boolean enabled = getSelectedOption(p, guide).orElse(true);
        ItemStack item = getIcon(enabled);
        return Optional.of(item);
    }

    public @NotNull ItemStack getIcon(boolean enabled) {
        var lk = "icon.options.beginners-guide.";
        List<String> lore = new ArrayList<>(Lang.getStringList(lk + "lore-1"));
        lore.add(Lang.getString(lk + "last-lore-1") +
                Lang.getString(lk + "last-lore-" + (enabled ? "disable" : "enable")) +
                Lang.getString(lk + "last-lore-last"));

        return Converter.getItem(
                enabled ? Material.KNOWLEDGE_BOOK : Material.BOOK,
                Lang.getMessage(lk + "name-1") +
                        Lang.getMessage(lk + "name-" + (enabled ? "enabled" : "disabled")),
                lore);
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        setSelectedOption(p, guide, !getSelectedOption(p, guide).orElse(true));
        SlimefunGuideSettings.openSettings(p, guide);
    }

    @Override
    public Optional<Boolean> getSelectedOption(Player p, ItemStack guide) {
        NamespacedKey key = getKey();
        boolean value = !PersistentDataAPI.hasByte(p, key) || PersistentDataAPI.getByte(p, key) == (byte) 1;
        return Optional.of(value);
    }

    @Override
    public void setSelectedOption(Player p, ItemStack guide, Boolean value) {
        PersistentDataAPI.setByte(p, getKey(), value ? (byte) 1 : (byte) 0);
    }
}