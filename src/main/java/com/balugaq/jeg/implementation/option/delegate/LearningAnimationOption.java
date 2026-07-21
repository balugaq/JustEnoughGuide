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

import java.util.List;
import java.util.Optional;

/**
 * @author TheBusyBiscuit
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("DataFlowIssue")
@NullMarked
public class LearningAnimationOption implements PrioritySlimefunGuideOption<Boolean> {
    public static NamespacedKey key0() {
        return new NamespacedKey(Slimefun.instance(), "research_learning_animation");
    }

    public static boolean isEnabled(Player p) {
        return !PersistentDataAPI.hasByte(p, key0()) || PersistentDataAPI.getByte(p, key0()) == 1;
    }

    @Override
    public SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        SlimefunConfigManager cfgManager = Slimefun.getConfigManager();
        if (cfgManager.isResearchingEnabled() && !cfgManager.isLearningAnimationDisabled()) {
            boolean enabled = this.getSelectedOption(p, guide).orElse(true);
            String optionState = enabled ? "enabled" : "disabled";
            List<String> lore = Slimefun.getLocalization().getMessages(
                p,
                "guide.options.learning-animation." + optionState + ".text"
            );
            lore.add("");
            String var10001 = Slimefun.getLocalization().getMessage(
                p,
                "guide.options.learning-animation." + optionState + ".click"
            );
            lore.add("&7⇨ " + var10001);
            ItemStack item = new CustomItemStack(enabled ? Material.MAP : Material.PAPER, lore);
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

    @Override
    public int priority() {
        return Priorities.LearningAnimationOption;
    }
}
