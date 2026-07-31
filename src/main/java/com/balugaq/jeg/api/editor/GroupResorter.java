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

package com.balugaq.jeg.api.editor;

import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author balugaq
 * @since 1.8
 */
@SuppressWarnings("DuplicateExpressions")
@NullMarked
public class GroupResorter {
    public static final Map<ItemGroup, Integer> oldTiers = new ConcurrentHashMap<>();
    public static final Set<Player> selectingPlayers = ConcurrentHashMap.newKeySet();
    public static final Map<Player, ItemGroup> selectedGroup = new ConcurrentHashMap<>();
    public static final Map<ItemGroup, Integer> jegGroupTier = new ConcurrentHashMap<>();
    public static final File tiersFile = new File(JustEnoughGuide.getInstance().getDataFolder(), "tiers.yml");
    public static @Nullable FileConfiguration config = null;

    public static void load() {
        // Re-read tiers.yml from disk so manual edits take effect on reload
        // instead of being overwritten by the stale in-memory cache.
        config = tiersFile.exists() ? YamlConfiguration.loadConfiguration(tiersFile) : null;
        loadInternal();
    }

    @ApiStatus.Internal
    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    private static void loadInternal() {
        JustEnoughGuide.runLater(() -> {
            if (!tiersFile.exists()) {
                int i = 0;
                for (ItemGroup itemGroup :
                    new ArrayList<>(Slimefun.getRegistry().getAllItemGroups())) {
                    setTier(itemGroup, i++);
                    setNameCfg(getKey(itemGroup), getDisplayName(itemGroup));
                    setCustomNameCfg(getKey(itemGroup), false);
                }
                saveCfg();
                return;
            }
            int offset = 0;
            ItemGroup lastItemGroup = null;
            for (ItemGroup itemGroup : new ArrayList<>(Slimefun.getRegistry().getAllItemGroups())) {
                oldTiers.put(itemGroup, itemGroup.getTier());

                String key = getKey(itemGroup);
                Integer cfg = getTierCfg(key);
                if (cfg != null) {
                    setTier(itemGroup, cfg + offset);
                } else {
                    if (lastItemGroup != null) {
                        // New ItemGroup
                        // Sort by related order.
                        setTier(itemGroup, getTier(lastItemGroup) + 1);
                        setCustomNameCfg(key, false);
                        offset += 1;
                    } else {
                        // By default
                        setTier(itemGroup, itemGroup.getTier());
                    }
                }
                // name is plugin-managed by default (customname: false),
                // so keep it in sync with the real display name unless the user opted in.
                if (!isCustomNameEnabled(key)) {
                    setNameCfg(key, getDisplayName(itemGroup));
                }
                lastItemGroup = itemGroup;
            }
            saveCfg();
        }, 1L);
    }

    public static boolean hasCfg() {
        return config != null;
    }

    public static @Nullable Integer getTierCfg(final String key) {
        return getOrCreateConfig().getObject(key + ".tier", Integer.class, null);
    }

    public static String getKey(final ItemGroup itemGroup) {
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

    public static void setTier(
        final ItemGroup itemGroup, final @Range(from = Integer.MIN_VALUE, to = Integer.MAX_VALUE) int tier) {
        jegGroupTier.put(itemGroup, tier);
        setTierCfg(getKey(itemGroup), tier);
    }

    public static int getTier(final ItemGroup itemGroup) {
        return jegGroupTier.getOrDefault(itemGroup, itemGroup.getTier());
    }

    public static void setNameCfg(final String key, final String name) {
        getOrCreateConfig().set(key + ".name", name);
    }

    public static void setCustomNameCfg(final String key, final boolean enabled) {
        getOrCreateConfig().set(key + ".customname", enabled);
    }

    public static boolean isCustomNameEnabled(final String key) {
        return getOrCreateConfig().getBoolean(key + ".customname", false);
    }

    public static String getDisplayName(final ItemGroup itemGroup) {
        return itemGroup.getUnlocalizedName();
    }

    /**
     * Matches hex color codes like {@code &#FFAA00} or {@code #FFAA00}.
     */
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("(?:&|§)?#([0-9a-fA-F]{6})");

    /**
     * Converts hex color codes ({@code &#RRGGBB} / {@code #RRGGBB}) into the legacy
     * {@code &x&R&R&G&G&B&B} format so {@link ChatColors#color(String)} can render them.
     */
    private static String translateHexColors(String input) {
        if (!input.contains("#")) {
            return input;
        }
        Matcher matcher = HEX_COLOR_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            StringBuilder replacement = new StringBuilder("&x");
            for (char c : matcher.group(1).toLowerCase().toCharArray()) {
                replacement.append('&').append(c);
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement.toString()));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Applies the custom name configured in tiers.yml to the given item stack.
     * Only applied when the item group's {@code customname} flag is set to true,
     * otherwise the original stack is returned untouched.
     */
    public static ItemStack applyName(final ItemGroup itemGroup, final ItemStack item) {
        if (!isCustomNameEnabled(getKey(itemGroup))) {
            return item;
        }
        final String name = getNameCfg(getKey(itemGroup));
        if (name == null) {
            return item;
        }
        final ItemStack copy = item.clone();
        copy.editMeta(meta -> meta.setDisplayName(ChatColors.color(translateHexColors(name))));
        return copy;
    }

    public static FileConfiguration getOrCreateConfig() {
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
        final String key, final @Range(from = Integer.MIN_VALUE, to = Integer.MAX_VALUE) int tier) {
        getOrCreateConfig().set(key + ".tier", tier);
        saveCfg();
    }

    public static void saveCfg() {
        if (config == null) return;
        try {
            config.save(tiersFile);
        } catch (IOException e) {
            Debug.severe(e);
        }
    }

    public static boolean isSelecting(final Player player) {
        return selectingPlayers.contains(player);
    }

    @Nullable
    public static ItemGroup getSelectedGroup(final Player player) {
        return selectedGroup.get(player);
    }

    public static void setSelectedGroup(final Player player, final @Nullable ItemGroup itemGroup) {
        if (itemGroup == null) {
            selectedGroup.remove(player);
        } else {
            selectedGroup.put(player, itemGroup);
        }
    }

    public static void exitSelecting(final Player player) {
        selectedGroup.remove(player);
        selectingPlayers.remove(player);
    }

    public static void enterSelecting(final Player player) {
        selectingPlayers.add(player);
    }

    public static void swap(final ItemGroup itemGroup1, final ItemGroup itemGroup2) {
        int tier1 = getTier(itemGroup1);
        int tier2 = getTier(itemGroup2);
        itemGroup1.setTier(tier2);
        setTier(itemGroup2, tier1);
        setTier(itemGroup1, tier2);
        resort();
    }

    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    public static void resort() {
        for (ItemGroup itemGroup : new ArrayList<>(Slimefun.getRegistry().getAllItemGroups())) {
            itemGroup.setTier(getTier(itemGroup));
        }

        Slimefun.getRegistry().getAllItemGroups().sort(Comparator.comparingInt(ItemGroup::getTier));
    }

    @SuppressWarnings("unused")
    public static @Nullable String getNameCfg(final String key) {
        return getOrCreateConfig().getString(key + ".name");
    }

    public static void rollback() {
        for (Map.Entry<ItemGroup, Integer> entry : oldTiers.entrySet()) {
            entry.getKey().setTier(entry.getValue());
        }
    }

    public static void sort(final List<ItemGroup> list) {
        list.sort(Comparator.comparingInt(GroupResorter::getTier));
    }
}
