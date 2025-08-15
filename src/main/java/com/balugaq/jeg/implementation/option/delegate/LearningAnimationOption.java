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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.balugaq.jeg.implementation.option.delegate;

import com.balugaq.jeg.api.patches.JEGGuideSettings;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * @author TheBusyBiscuit
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("DataFlowIssue")
public class LearningAnimationOption implements SlimefunGuideOption<Boolean> {
    @NotNull
    public SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @NotNull
    public NamespacedKey getKey() {
        return new NamespacedKey(Slimefun.instance(), "research_learning_animation");
    }

    @NotNull
    public Optional<ItemStack> getDisplayItem(@NotNull Player p, @NotNull ItemStack guide) {
        SlimefunConfigManager cfgManager = Slimefun.getConfigManager();
        if (cfgManager.isResearchingEnabled() && !cfgManager.isLearningAnimationDisabled()) {
            boolean enabled = this.getSelectedOption(p, guide).orElse(true);
            String optionState = enabled ? "enabled" : "disabled";
            List<String> lore = Slimefun.getLocalization().getMessages(p, "guide.options.learning-animation." + optionState + ".text");
            lore.add("");
            String var10001 = Slimefun.getLocalization().getMessage(p, "guide.options.learning-animation." + optionState + ".click");
            lore.add("&7⇨ " + var10001);
            ItemStack item = new CustomItemStack(enabled ? Material.MAP : Material.PAPER, lore);
            return Optional.of(item);
        } else {
            return Optional.empty();
        }
    }

    public void onClick(@NotNull Player p, @NotNull ItemStack guide) {
        this.setSelectedOption(p, guide, !(Boolean) this.getSelectedOption(p, guide).orElse(true));
        JEGGuideSettings.openSettings(p, guide);
    }

    public Optional<Boolean> getSelectedOption(@NotNull Player p, @NotNull ItemStack guide) {
        NamespacedKey key = this.getKey();
        boolean value = !PersistentDataAPI.hasByte(p, key) || PersistentDataAPI.getByte(p, key) == 1;
        return Optional.of(value);
    }

    public void setSelectedOption(@NotNull Player p, @NotNull ItemStack guide, @NotNull Boolean value) {
        PersistentDataAPI.setByte(p, this.getKey(), (byte) (value ? 1 : 0));
    }
}
