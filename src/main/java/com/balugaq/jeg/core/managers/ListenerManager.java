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

package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.core.listeners.BundleListener;
import com.balugaq.jeg.core.listeners.CerPatchListener;
import com.balugaq.jeg.core.listeners.GroupTierEditorListener;
import com.balugaq.jeg.core.listeners.GuideGUIFixListener;
import com.balugaq.jeg.core.listeners.GuideListener;
import com.balugaq.jeg.core.listeners.MenuListener;
import com.balugaq.jeg.core.listeners.RTSListener;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.balugaq.jeg.core.listeners.ReplacementCardAdaptItemListener;
import com.balugaq.jeg.core.listeners.SlimefunGuideOptionPatchFixListener;
import com.balugaq.jeg.core.listeners.SlimefunIdPatchListener;
import com.balugaq.jeg.core.listeners.SlimefunRegistryFinalizeListener;
import com.balugaq.jeg.core.listeners.SpecialMenuFixListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.WatchdogHandler;
import com.balugaq.jeg.utils.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for managing the listeners of the plugin.
 *
 * @author balugaq
 * @since 1.0
 */
@Getter
@NullMarked
public class ListenerManager extends AbstractManager {
    private final List<Listener> listeners = new ArrayList<>();

    private final Plugin plugin;
    private @UnknownNullability RegisteredListener slimefunGuideListener;

    public ListenerManager(Plugin plugin) {
        this.plugin = plugin;
        listeners.add(new GuideListener());
        listeners.add(new SpecialMenuFixListener());
        listeners.add(new RTSListener());
        listeners.add(new GroupTierEditorListener());
        listeners.add(new GuideGUIFixListener());
        listeners.add(new MenuListener());
        if (JustEnoughGuide.getConfigManager().isSlimefunIdDisplay()) {
            listeners.add(new SlimefunIdPatchListener());
        }
        if (JustEnoughGuide.getConfigManager().isCerPatch()) {
            listeners.add(new CerPatchListener());
        }
        if (JustEnoughGuide.getConfigManager().isRecipeComplete()) {
            listeners.add(new RecipeCompletableListener());
        }
        if (JustEnoughGuide.getConfigManager().isDebug()) {
            listeners.add(new WatchdogHandler());
        }
        if (MinecraftVersion.current().isAtLeast(MinecraftVersion.V1_17)
            && JustEnoughGuide.getConfigManager().isDisabledBundleInteraction()) {
            listeners.add(new BundleListener());
        }
        listeners.add(new SlimefunGuideOptionPatchFixListener());
        listeners.add(new SlimefunRegistryFinalizeListener());
        listeners.add(new ReplacementCardAdaptItemListener());
    }

    public void registerListener(Listener listener) {
        listeners.add(listener);
        Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void load() {
        registerListeners();
        for (RegisteredListener rl : PlayerRightClickEvent.getHandlerList().getRegisteredListeners()) {
            if (rl.getListener().getClass().getName().equals("io.github.thebusybiscuit.slimefun4.implementation.listeners.SlimefunGuideListener")) {
                slimefunGuideListener = rl;
                PlayerRightClickEvent.getHandlerList().unregister(rl);
                PlayerRightClickEvent.getHandlerList().bake();
                break;
            }
        }
    }

    private void registerListeners() {
        for (Listener listener : new ArrayList<>(listeners)) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    @Override
    public void unload() {
        unregisterListeners();
        if (slimefunGuideListener != null) {
            PlayerRightClickEvent.getHandlerList().register(slimefunGuideListener);
            slimefunGuideListener = null;
        }
    }

    private void unregisterListeners() {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
    }
}
