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

package com.balugaq.jeg.implementation.option;

import com.balugaq.jeg.api.objects.enums.SearchMode;
import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.api.patches.Priorities;
import com.balugaq.jeg.api.patches.PrioritySlimefunGuideOption;
import com.balugaq.jeg.core.lang.Lang;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * 指南设置里的「搜索模式」选项（Issue #125）：
 * 玩家可在 CJK / 拉丁 / 朴素 三种搜索模式间切换；
 * 默认值由服务端配置的 {@code language} 推导。
 *
 * @author balugaq
 * @since 2.1
 */
@SuppressWarnings("SameReturnValue")
@NullMarked
public class SearchModeGuideOption implements PrioritySlimefunGuideOption<SearchMode> {
    public static final SearchModeGuideOption instance = new SearchModeGuideOption();

    public static SearchModeGuideOption instance() {
        return instance;
    }

    public static NamespacedKey key0() {
        return new NamespacedKey(JustEnoughGuide.getInstance(), "search_mode");
    }

    /** 玩家当前选择的搜索模式；未设置时回落到配置语言推导的默认值。 */
    public static SearchMode getSearchMode(Player p) {
        if (PersistentDataAPI.hasByte(p, key0())) {
            byte ordinal = PersistentDataAPI.getByte(p, key0());
            SearchMode[] values = SearchMode.values();
            if (ordinal >= 0 && ordinal < values.length) {
                return values[ordinal];
            }
        }

        return JustEnoughGuide.getConfigManager().getDefaultSearchMode();
    }

    @Override
    public SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @Override
    public int priority() {
        return Priorities.SearchModeGuideOption;
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        SearchMode current = getSelectedOption(p, guide).orElse(SearchMode.CJK);
        SearchMode target = current.next();
        String currentName = modeName(current);
        String targetName = modeName(target);
        ItemStack item = Converter.getItem(
            Material.COMPASS,
            Lang.t("search-mode.name", currentName),
            Lang.lines("search-mode.lore", targetName).toArray(String[]::new)
        );
        return Optional.of(item);
    }

    private static String modeName(SearchMode mode) {
        return switch (mode) {
            case CJK -> Lang.t("search-mode.mode-cjk");
            case LATIN -> Lang.t("search-mode.mode-latin");
            case NAIVE -> Lang.t("search-mode.mode-naive");
        };
    }

    @Override
    public NamespacedKey getKey() {
        return key0();
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        SearchMode current = getSelectedOption(p, guide).orElse(SearchMode.CJK);
        setSelectedOption(p, guide, current.next());
        JEGGuideSettings.openSettings(p, guide);
    }

    @Override
    public Optional<SearchMode> getSelectedOption(Player p, ItemStack guide) {
        NamespacedKey key = getKey();
        if (PersistentDataAPI.hasByte(p, key)) {
            byte ordinal = PersistentDataAPI.getByte(p, key);
            SearchMode[] values = SearchMode.values();
            if (ordinal >= 0 && ordinal < values.length) {
                return Optional.of(values[ordinal]);
            }
        }

        return Optional.of(JustEnoughGuide.getConfigManager().getDefaultSearchMode());
    }

    @Override
    public void setSelectedOption(Player p, ItemStack guide, SearchMode value) {
        PersistentDataAPI.setByte(p, getKey(), (byte) value.ordinal());
    }
}
