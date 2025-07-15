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

package com.balugaq.jeg.api.patches.slimefun;

import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author TheBusyBiscuit
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"deprecation", "DataFlowIssue"})
public class GuideModeOption implements SlimefunGuideOption<SlimefunGuideMode> {
    @NotNull
    public SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @NotNull
    public NamespacedKey getKey() {
        return new NamespacedKey(Slimefun.instance(), "guide_mode");
    }

    @NotNull
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
                ChatColor var10001 = ChatColor.GRAY;
                meta.setDisplayName(var10001 + "Slimefun Guide Type: " + ChatColor.YELLOW + ChatUtils.humanize(selectedMode.name()));
                List<String> lore = new ArrayList();
                lore.add("");
                var10001 = selectedMode == SlimefunGuideMode.SURVIVAL_MODE ? ChatColor.GREEN : ChatColor.GRAY;
                lore.add(var10001 + "Survival Mode");
                lore.add((selectedMode == SlimefunGuideMode.CHEAT_MODE ? ChatColor.GREEN : ChatColor.GRAY) + "Cheat Sheet");
                lore.add("");
                lore.add(ChatColor.GRAY + "⇨ " + ChatColor.YELLOW + "Click to change the type");
                meta.setLore(lore);
                item.setItemMeta(meta);
                return Optional.of(item);
            } else {
                return Optional.empty();
            }
        }
    }

    public void onClick(@NotNull Player p, @NotNull ItemStack guide) {
        Optional<SlimefunGuideMode> current = this.getSelectedOption(p, guide);
        if (current.isPresent()) {
            SlimefunGuideMode next = this.getNextMode(p, current.get());
            this.setSelectedOption(p, guide, next);
        }

        JEGGuideSettings.openSettings(p, guide);
    }

    @NotNull
    private SlimefunGuideMode getNextMode(@NotNull Player p, @NotNull SlimefunGuideMode mode) {
        if (p.hasPermission("slimefun.cheat.items")) {
            return mode == SlimefunGuideMode.SURVIVAL_MODE
                    ? SlimefunGuideMode.CHEAT_MODE
                    : SlimefunGuideMode.SURVIVAL_MODE;
        } else {
            return SlimefunGuideMode.SURVIVAL_MODE;
        }
    }

    @NotNull
    public Optional<SlimefunGuideMode> getSelectedOption(@NotNull Player p, @NotNull ItemStack guide) {
        return SlimefunUtils.isItemSimilar(guide, SlimefunGuide.getItem(SlimefunGuideMode.CHEAT_MODE), true, false)
                ? Optional.of(SlimefunGuideMode.CHEAT_MODE)
                : Optional.of(SlimefunGuideMode.SURVIVAL_MODE);
    }

    @ParametersAreNonnullByDefault
    public void setSelectedOption(Player p, ItemStack guide, SlimefunGuideMode value) {
        guide.setItemMeta(SlimefunGuide.getItem(value).getItemMeta());
    }
}
