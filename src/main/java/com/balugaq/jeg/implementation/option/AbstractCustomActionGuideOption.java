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
public abstract class AbstractCustomActionGuideOption extends AbstractBooleanGuideOption {
    @Override
    public Optional<Boolean> getSelectedOption(Player p, ItemStack guide) {
        return Optional.of(false);
    }

    @Override
    public void setSelectedOption(Player p, ItemStack guide, Boolean value) {
    }

    @SuppressWarnings("unused")
    public abstract ItemStack getDisplayItem(Player player, ItemStack guide, boolean enabled);
}
