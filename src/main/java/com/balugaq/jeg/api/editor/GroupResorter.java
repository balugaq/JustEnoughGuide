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

package com.balugaq.jeg.api.editor;

import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author balugaq
 * @since 1.8
 */
@SuppressWarnings("DuplicateExpressions")
public class GroupResorter {
    public static final Map<ItemGroup, Integer> oldTiers = new ConcurrentHashMap<>();
    public static final Set<Player> selectingPlayers = ConcurrentHashMap.newKeySet();
    public static final Map<Player, ItemGroup> selectedGroup = new ConcurrentHashMap<>();
    public static final Map<ItemGroup, Integer> jegGroupTier = new ConcurrentHashMap<>();
    public static final File tiersFile = new File(JustEnoughGuide.getInstance().getDataFolder(), "tiers.yml");
    public static @Nullable FileConfiguration config = null;

    static {
        load();
    }

    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    public static void load() {
        Bukkit.getScheduler()
                .runTaskLater(
                        JustEnoughGuide.getInstance(),
                        () -> {
                            if (hasCfg()) {
                                int offset = 0;
                                ItemGroup lastItemGroup = null;
                                for (ItemGroup itemGroup :
                                        Slimefun.getRegistry().getAllItemGroups()) {
                                    oldTiers.put(itemGroup, itemGroup.getTier());

                                    Integer cfg = getTierCfg(getKey(itemGroup));
                                    if (cfg != null) {
                                        setTier(itemGroup, cfg + offset);
                                    } else {
                                        if (lastItemGroup != null) {
                                            // New ItemGroup
                                            // Sort by related order.
                                            setTier(itemGroup, getTier(lastItemGroup) + 1);
                                            setNameCfg(getKey(itemGroup), getDisplayName(itemGroup));
                                            offset += 1;
                                        } else {
                                            // By default
                                            setTier(itemGroup, itemGroup.getTier());
                                        }
                                    }
                                    lastItemGroup = itemGroup;
                                }
                            } else {
                                int i = 0;
                                for (ItemGroup itemGroup :
                                        Slimefun.getRegistry().getAllItemGroups()) {
                                    setTier(itemGroup, i++);
                                    setNameCfg(getKey(itemGroup), getDisplayName(itemGroup));
                                }
                            }
                        },
                        1L);
    }

    public static boolean isSelecting(final @NotNull Player player) {
        return selectingPlayers.contains(player);
    }

    @Nullable
    public static ItemGroup getSelectedGroup(final @NotNull Player player) {
        return selectedGroup.get(player);
    }

    public static void setSelectedGroup(final @NotNull Player player, final @Nullable ItemGroup itemGroup) {
        if (itemGroup == null) {
            selectedGroup.remove(player);
        } else {
            selectedGroup.put(player, itemGroup);
        }
    }

    public static void exitSelecting(final @NotNull Player player) {
        selectedGroup.remove(player);
        selectingPlayers.remove(player);
    }

    public static void enterSelecting(final @NotNull Player player) {
        selectingPlayers.add(player);
    }

    public static @NotNull String getDisplayName(final @NotNull ItemGroup itemGroup) {
        return itemGroup.getUnlocalizedName();
    }

    public static int getTier(final @NotNull ItemGroup itemGroup) {
        return jegGroupTier.getOrDefault(itemGroup, itemGroup.getTier());
    }

    public static void setTier(
            final ItemGroup itemGroup, final @Range(from = Integer.MIN_VALUE, to = Integer.MAX_VALUE) int tier) {
        jegGroupTier.put(itemGroup, tier);
        setTierCfg(getKey(itemGroup), tier);
    }

    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    public static void resort() {
        List<ItemGroup> copy = new ArrayList<>(Slimefun.getRegistry().getAllItemGroups());
        for (ItemGroup itemGroup : copy) {
            itemGroup.setTier(getTier(itemGroup));
        }

        Slimefun.getRegistry().getAllItemGroups().sort(Comparator.comparingInt(ItemGroup::getTier));
    }

    public static void swap(final @NotNull ItemGroup itemGroup1, final @NotNull ItemGroup itemGroup2) {
        int tier1 = getTier(itemGroup1);
        int tier2 = getTier(itemGroup2);
        itemGroup1.setTier(tier2);
        setTier(itemGroup2, tier1);
        setTier(itemGroup1, tier2);
        resort();
    }

    public static @NotNull FileConfiguration getOrCreateConfig() {
        if (config != null) {
            return config;
        }

        if (!tiersFile.exists()) {
            try {
                // read from jar input stream
                JustEnoughGuide.getInstance().saveResource("tiers.yml", false);
            } catch (Exception e) {
                Debug.trace(e);
            }
        }

        return config = YamlConfiguration.loadConfiguration(tiersFile);
    }

    public static void setTierCfg(
            final @NotNull String key, final @Range(from = Integer.MIN_VALUE, to = Integer.MAX_VALUE) int tier) {
        getOrCreateConfig().set(key + ".tier", tier);
        saveCfg();
    }

    public static @Nullable Integer getTierCfg(final @NotNull String key) {
        return getOrCreateConfig().getObject(key + ".tier", Integer.class, null);
    }

    public static void setNameCfg(final @NotNull String key, final @NotNull String name) {
        getOrCreateConfig().set(key + ".name", name);
    }

    @SuppressWarnings("unused")
    public static @Nullable String getNameCfg(final @NotNull String key) {
        return getOrCreateConfig().getString(key + ".name");
    }

    public static @NotNull String getKey(final @NotNull ItemGroup itemGroup) {
        if (itemGroup instanceof NestedItemGroup n) {
            return n.getKey().getNamespace() + "-" + n.getKey().getKey() + ".nested";
        } else if (itemGroup instanceof SubItemGroup s) {
            NestedItemGroup n = s.getParent();
            return n.getKey().getNamespace() + "-" + n.getKey().getKey() + ".sub."
                    + s.getKey().getNamespace() + "-" + n.getKey().getKey();
        } else if (itemGroup.getClass() == ItemGroup.class) {
            return itemGroup.getKey().getNamespace() + "-" + itemGroup.getKey().getKey();
        } else {
            return itemGroup.getKey().getNamespace() + "-" + itemGroup.getKey().getKey();
        }
    }

    public static boolean hasCfg() {
        return config != null;
    }

    @SneakyThrows
    public static void saveCfg() {
        if (config == null) return;
        config.save(tiersFile);
    }

    public static void rollback() {
        for (Map.Entry<ItemGroup, Integer> entry : oldTiers.entrySet()) {
            entry.getKey().setTier(entry.getValue());
        }
    }

    public static void sort(final @NotNull List<ItemGroup> list) {
        list.sort(Comparator.comparingInt(GroupResorter::getTier));
    }
}
