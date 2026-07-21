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

package com.balugaq.jeg.core.integrations.slimefuntranslation;

import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import lombok.Getter;
import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.api.SlimefunTranslationAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.9
 */
@NullMarked
public class SlimefunTranslationIntegrationMain implements Integration {
    @Getter
    @Nullable
    private Boolean interceptSearch;

    public static ItemStack translateItem(Player player, ItemStack itemStack) {
        itemStack = Converter.getItem(itemStack);
        if (JustEnoughGuide.getIntegrationManager().isEnabledSlimefunTranslation()) {
            SlimefunTranslationAPI.translateItem(SlimefunTranslationAPI.getUser(player), itemStack);
        }

        return itemStack;
    }

    public static String getTranslatedItemName(Player player, SlimefunItem slimefunItem) {
        if (JustEnoughGuide.getIntegrationManager().isEnabledSlimefunTranslation()) {
            return SlimefunTranslationAPI.getItemName(SlimefunTranslationAPI.getUser(player), slimefunItem);
        }

        return slimefunItem.getItemName();
    }

    @Override
    public String getHookPlugin() {
        return "SlimefunTranslation";
    }

    @Override
    public void onEnable() {
        Object value = ReflectionUtil.getValue(SlimefunTranslation.getConfigService(), "interceptSearch");
        if (value instanceof Boolean bool) {
            interceptSearch = bool;
            ReflectionUtil.setValue(SlimefunTranslation.getConfigService(), "interceptSearch", false);
        }
    }

    @Override
    public void onDisable() {
        // Rollback SlimefunTranslation interceptSearch
        if (interceptSearch != null) {
            ReflectionUtil.setValue(SlimefunTranslation.getConfigService(), "interceptSearch", interceptSearch);
        }
    }
}