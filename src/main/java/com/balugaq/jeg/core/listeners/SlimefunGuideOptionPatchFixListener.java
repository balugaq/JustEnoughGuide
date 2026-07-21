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

package com.balugaq.jeg.core.listeners;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.option.delegate.FireworksOption;
import com.balugaq.jeg.implementation.option.delegate.LearningAnimationOption;
import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.api.events.ResearchUnlockEvent;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author balugaq
 * @since 2.1
 */
public class SlimefunGuideOptionPatchFixListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onResearch(ResearchUnlockEvent event) {
        if (!FireworksOption.isEnabled(event.getPlayer()) && Slimefun.getConfigManager().isResearchFireworkEnabled()) {
            if (LearningAnimationOption.isEnabled(event.getPlayer())) {
                JustEnoughGuide.runLaterAsync(this::doFix, 5 * 20L);
            } else {
                doFix();
            }
        }
    }

    private void doFix() {
        ReflectionUtil.setValue(Slimefun.getConfigManager(), "researchFireworks", false);
        JustEnoughGuide.runLaterAsync(() -> {
            ReflectionUtil.setValue(Slimefun.getConfigManager(), "researchFireworks", true);
        }, 2L);
    }
}
