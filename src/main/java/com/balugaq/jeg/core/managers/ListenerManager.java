/*
 * Copyright (c) 2024-2026 balugaq
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
import com.balugaq.jeg.core.listeners.SearchReloadListener;
import com.balugaq.jeg.core.listeners.SpecialMenuFixListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.WatchdogHandler;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;
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

    private final JavaPlugin plugin;
    private @UnknownNullability RegisteredListener slimefunGuideListener;

    public ListenerManager(JavaPlugin plugin) {
        this.plugin = plugin;
        listeners.add(new GuideListener());
        listeners.add(new SpecialMenuFixListener());
        listeners.add(new RTSListener());
        listeners.add(new GroupTierEditorListener());
        listeners.add(new GuideGUIFixListener());
        listeners.add(new MenuListener());
        if (JustEnoughGuide.getConfigManager().isCerPatch()) {
            listeners.add(new CerPatchListener());
        }
        if (JustEnoughGuide.getConfigManager().isRecipeComplete()) {
            listeners.add(new RecipeCompletableListener());
        }

        if (JustEnoughGuide.getConfigManager().isDebug()) {
            listeners.add(new WatchdogHandler());
        }
        if (JustEnoughGuide.getConfigManager().isDisabledBundleInteraction()) {
            listeners.add(new BundleListener());
        }

        listeners.add(new SearchReloadListener());
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
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    @Override
    public void unload() {
        unregisterListeners();
        PlayerRightClickEvent.getHandlerList().register(slimefunGuideListener);
    }

    private void unregisterListeners() {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
    }
}
