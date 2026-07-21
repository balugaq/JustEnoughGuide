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
import com.balugaq.jeg.core.listeners.GuideListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author TheBusyBiscuit
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"deprecation", "DataFlowIssue", "ExtractMethodRecommender"})
@NullMarked
public class GuideModeOption implements PrioritySlimefunGuideOption<SlimefunGuideMode> {
    @Override
    public SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        if (!p.hasPermission("slimefun.cheat.items")) {
            return Optional.empty();
        } else {
            Optional<SlimefunGuideMode> current = this.getSelectedOption(p, guide);
            if (current.isPresent()) {
                SlimefunGuideMode selectedMode = current.get();
                ItemStack item = new ItemStack(Material.AIR);
                if (selectedMode == SlimefunGuideMode.SURVIVAL_MODE) {
                    item.setType(Material.CHEST);
                } else {
                    item.setType(Material.COMMAND_BLOCK);
                }

                ItemMeta meta = item.getItemMeta();
                ChatColor color = ChatColor.GRAY;
                meta.setDisplayName(color + "Slimefun 指南样式: " + ChatColor.YELLOW + selectedMode.getDisplayName());
                List<String> lore = new ArrayList<>();
                lore.add("");
                color = selectedMode == SlimefunGuideMode.SURVIVAL_MODE ? ChatColor.GREEN : ChatColor.GRAY;
                lore.add(color + "普通模式");
                lore.add((selectedMode == SlimefunGuideMode.CHEAT_MODE ? ChatColor.GREEN : ChatColor.GRAY) + "作弊模式");
                lore.add("");
                lore.add(ChatColor.GRAY + "⇨ " + ChatColor.YELLOW + "单击修改指南样式");
                meta.setLore(lore);
                item.setItemMeta(meta);
                return Optional.of(item);
            } else {
                return Optional.empty();
            }
        }
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        Optional<SlimefunGuideMode> current = this.getSelectedOption(p, guide);
        if (current.isPresent()) {
            SlimefunGuideMode next = this.getNextMode(p, current.get());
            this.setSelectedOption(p, guide, next);
        }

        JEGGuideSettings.openSettings(p, guide);
    }

    private SlimefunGuideMode getNextMode(Player p, SlimefunGuideMode mode) {
        if (p.isOp() || p.hasPermission("slimefun.cheat.items")) {
            return mode == SlimefunGuideMode.SURVIVAL_MODE ? SlimefunGuideMode.CHEAT_MODE :
                SlimefunGuideMode.SURVIVAL_MODE;
        }

        return SlimefunGuideMode.SURVIVAL_MODE;
    }

    @Override
    public Optional<SlimefunGuideMode> getSelectedOption(Player p, ItemStack guide) {
        return SlimefunUtils.isItemSimilar(guide, SlimefunGuide.getItem(SlimefunGuideMode.CHEAT_MODE), true, false) ?
            Optional.of(SlimefunGuideMode.CHEAT_MODE) : Optional.of(SlimefunGuideMode.SURVIVAL_MODE);
    }

    @Override
    public void setSelectedOption(Player p, ItemStack guide, SlimefunGuideMode value) {
        guide.setItemMeta(SlimefunGuide.getItem(value).getItemMeta());
        GuideListener.guideModeMap.put(p, value);
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(Slimefun.instance(), "guide_mode");
    }

    @Override
    public int priority() {
        return Priorities.GuideModeOption;
    }
}
