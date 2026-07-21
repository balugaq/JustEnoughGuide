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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.balugaq.jeg.implementation.option.delegate;

import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.api.patches.Priorities;
import com.balugaq.jeg.api.patches.PrioritySlimefunGuideOption;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.JEGVersionedItemFlag;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.config.SlimefunConfigManager;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * @author TheBusyBiscuit
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("DataFlowIssue")
@NullMarked
public class FireworksOption implements PrioritySlimefunGuideOption<Boolean> {
    public static NamespacedKey key0() {
        return new NamespacedKey(Slimefun.instance(), "research_fireworks");
    }

    public static boolean isEnabled(Player p) {
        return !PersistentDataAPI.hasByte(p, key0()) || PersistentDataAPI.getByte(p, key0()) == 1;
    }

    @Override
    public SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @Override
    public int priority() {
        return Priorities.FireworksOption;
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        SlimefunConfigManager cfgManager = Slimefun.getConfigManager();
        if (cfgManager.isResearchingEnabled() && cfgManager.isResearchFireworkEnabled()) {
            boolean enabled = this.getSelectedOption(p, guide).orElse(true);
            ItemStack item = new CustomItemStack(
                Material.FIREWORK_ROCKET, "&b烟花特效: &" + (enabled ? "a启用" : "4禁用"),
                "", "&7你现在可以选择是否", "&7在解锁一个新物品的时候", "&7展示烟花特效.", "",
                "&7⇨ &e点击 " + (enabled ? "禁用" : "启用") + " 烟花特效"
            );
            var meta = item.getItemMeta();
            meta.addItemFlags(JEGVersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            item.setItemMeta(meta);
            return Optional.of(item);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public NamespacedKey getKey() {
        return key0();
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        this.setSelectedOption(p, guide, !(Boolean) this.getSelectedOption(p, guide).orElse(true));
        JEGGuideSettings.openSettings(p, guide);
    }

    @Override
    public Optional<Boolean> getSelectedOption(Player p, ItemStack guide) {
        return Optional.of(isEnabled(p));
    }

    @Override
    public void setSelectedOption(Player p, ItemStack guide, Boolean value) {
        PersistentDataAPI.setByte(p, this.getKey(), (byte) (value ? 1 : 0));
    }
}
