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

package com.balugaq.jeg.utils;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author balugaq
 * @since 1.2
 */
@SuppressWarnings({"deprecation", "ExtractMethodRecommender", "unused", "ConstantValue"})
@NullMarked
public class LocalHelper {
    public static final String def = "未知附属"; // fallback：Lang 未加载时的兜底
    public static final Map<String, Map<String, SlimefunItemStack>> rscItems = new HashMap<>();
    // default language is zh-CN
    // support color symbol
    public static final Map<String, String> addonLocals = new HashMap<>();
    // depends on rsc addons' info.yml
    public static final Map<String, Set<String>> rscLocals = new HashMap<>();

    /**
     * 从语言文件的 {@code addon-names} 节加载附属名对照表，
     * 再叠加 config.yml 的 {@code local-translate} 用户自定义映射（后者优先）。
     * <p>
     * 由主类在 {@code Lang.load()} 之后调用；{@code /jeg reload} 时随语言系统一起重载。
     */
    public static void load() {
        addonLocals.clear();
        addonLocals.putAll(com.balugaq.jeg.core.lang.Lang.section("addon-names"));
        for (Map.Entry<String, String> entry :
            JustEnoughGuide.getConfigManager().getLocalTranslate().entrySet()) {
            addonLocals.put(entry.getKey(), ChatColors.color(entry.getValue()));
        }
    }

public static String getOfficialAddonName(ItemGroup itemGroup, String itemId) {
        return getOfficialAddonName(itemGroup.getAddon(), itemId, def);
    }

    public static String getOfficialAddonName(
        @Nullable SlimefunAddon addon, String itemId, String callback) {
        return getOfficialAddonName(addon == null ? "Slimefun" : addon.getName(), itemId, callback);
    }

    public static String getOfficialAddonName(
        String addonName, String itemId, String callback) {
        return getAddonName(addonName, itemId, callback) + " (" + addonName + ")";
    }

    public static String getAddonName(String addonName, String itemId, String callback) {
        if (addonName == null) {
            return ChatColors.color(callback);
        }

        if ("RykenSlimefunCustomizer".equalsIgnoreCase(addonName)
            || "RykenSlimeCustomizer".equalsIgnoreCase(addonName)) {
            return getRSCLocalName(itemId);
        }
        String localName = addonLocals.get(addonName);
        return ChatColors.color(localName == null ? callback : localName);
    }

    // get a rsc addon name by item id
    public static String getRSCLocalName(String itemId) {
        for (Map.Entry<String, Set<String>> entry : rscLocals.entrySet()) {
            if (entry.getValue().contains(itemId)) {
                return ChatColors.color(entry.getKey());
            }
        }

        String def = addonLocals.get("RykenSlimefunCustomizer");
        if (def == null) {
            def = addonLocals.get("RykenSlimeCustomizer");
        }

        if (rscItems.isEmpty()) {
            try {
                Plugin rsc1 = Bukkit.getPluginManager().getPlugin("RykenSlimefunCustomizer");
                Plugin rsc2 = null;
                if (rsc1 == null) {
                    rsc2 = Bukkit.getPluginManager().getPlugin("RykenSlimeCustomizer");
                    if (rsc2 == null) {
                        return def;
                    }
                }

                Plugin rsc = rsc1 == null ? rsc2 : rsc1;
                if (rsc == null) {
                    return def;
                }
                Object addonManager = ReflectionUtil.getValue(rsc, "addonManager");
                if (addonManager != null) {
                    Object projectAddons = ReflectionUtil.getValue(addonManager, "projectAddons");
                    @SuppressWarnings("unchecked")
                    Map<Object, Object> map = (Map<Object, Object>) projectAddons;
                    if (map != null) {
                        for (Map.Entry<Object, Object> entry : map.entrySet()) {
                            Object addon = entry.getValue();
                            Object addonName = ReflectionUtil.getValue(addon, "addonName");
                            String name = (String) addonName;
                            if (name == null) {
                                continue;
                            }
                            Object preloadItems = ReflectionUtil.getValue(addon, "preloadItems");
                            @SuppressWarnings("unchecked")
                            Map<Object, Object> items = (Map<Object, Object>) preloadItems;
                            Map<String, SlimefunItemStack> read = new HashMap<>();
                            if (items != null) {
                                for (Map.Entry<Object, Object> itemEntry : items.entrySet()) {
                                    String id = (String) itemEntry.getKey();
                                    SlimefunItemStack item = (SlimefunItemStack) itemEntry.getValue();
                                    read.put(id, item);
                                }
                            }
                            rscItems.put(name, read);
                        }
                    }
                }
            } catch (Exception e) {
                Debug.trace(e);
            }
        }

        for (Map.Entry<String, Map<String, SlimefunItemStack>> entry : rscItems.entrySet()) {
            Map<String, SlimefunItemStack> items = entry.getValue();
            if (items.containsKey(itemId)) {
                return ChatColors.color(entry.getKey());
            }
        }

        return ChatColors.color(def);
    }

    public static String getOfficialAddonName(
        ItemGroup itemGroup, String itemId, String callback) {
        return itemGroup.getAddon() == null ? def : getOfficialAddonName(itemGroup.getAddon(), itemId, callback);
    }

    public static String getOfficialAddonName(@Nullable SlimefunAddon addon, String itemId) {
        return getOfficialAddonName(addon, itemId, def);
    }

    public static String getOfficialAddonName(String addonName, String itemId) {
        return getOfficialAddonName(addonName, itemId, def);
    }

    public static String getAddonName(ItemGroup itemGroup, String itemId) {
        return getAddonName(itemGroup, itemId, def);
    }

    public static String getAddonName(ItemGroup itemGroup, String itemId, String callback) {
        return itemGroup.getAddon() == null
            ? def
            : getAddonName(itemGroup.getAddon().getName(), itemId, callback);
    }

    public static String getAddonName(@Nullable SlimefunAddon addon) {
        if (addon == null) return def;
        return ChatColors.color(addonLocals.getOrDefault(addon.getName(), def));
    }

    public static String getAddonName(@Nullable SlimefunAddon addon, String itemId) {
        return getAddonName(addon, itemId, def);
    }

    public static String getAddonName(@Nullable SlimefunAddon addon, String itemId, String callback) {
        return getAddonName(addon == null ? addonLocals.get("Slimefun") : addon.getName(), itemId, callback);
    }

    public static String getAddonName(String addonName, String itemId) {
        return getAddonName(addonName, itemId, def);
    }

    public static void addRSCLocal(String rscAddonName, String itemId) {
        if (!rscLocals.containsKey(rscAddonName)) {
            rscLocals.put(rscAddonName, new HashSet<>());
        }

        rscLocals.get(rscAddonName).add(itemId);
    }

    public static String getDisplayName(ItemGroup itemGroup, Player player) {
        ItemMeta meta = itemGroup.getItem(player).getItemMeta();
        if (meta == null) {
            return def;
        }

        return meta.getDisplayName();
    }
}
