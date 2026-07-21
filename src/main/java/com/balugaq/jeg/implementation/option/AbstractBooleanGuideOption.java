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

import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.api.patches.PrioritySlimefunGuideOption;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.KeyUtil;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"SameReturnValue"})
@NullMarked
public abstract class AbstractBooleanGuideOption implements PrioritySlimefunGuideOption<Boolean> {
    public boolean isEnabled(Player p) {
        return getSelectedOption(p);
    }

    public boolean getSelectedOption(Player p) {
        return (defaultValue() && !PersistentDataAPI.hasByte(p, getKey())) ||
            PersistentDataAPI.getByte(p, getKey()) == (defaultValue() ? 1 : 0);
    }

    @Override
    public SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        boolean enabled = getSelectedOption(p, guide).orElse(defaultValue());
        ItemStack item = getDisplayItem(p, guide, enabled);
        return Optional.of(item);
    }

    @SuppressWarnings("unused")
    public abstract ItemStack getDisplayItem(Player player, ItemStack guide, boolean enabled);

    @Override
    public NamespacedKey getKey() {
        return KeyUtil.newKey(key0());
    }

    public abstract String key0();

    public boolean defaultValue() {
        return true;
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        setSelectedOption(p, guide, !getSelectedOption(p, guide).orElse(defaultValue()));
        JEGGuideSettings.openSettings(p, guide);
    }

    @Override
    public Optional<Boolean> getSelectedOption(Player p, ItemStack guide) {
        return Optional.of(getSelectedOption(p));
    }

    @Override
    public void setSelectedOption(Player p, ItemStack guide, Boolean value) {
        PersistentDataAPI.setByte(p, getKey(), value ? (byte) 1 : (byte) 0);
    }
}
