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

package com.balugaq.jeg.core.integrations.nexcavate;

import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.Models;
import com.balugaq.jeg.utils.SlimefunRegistryUtil;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 1.9
 */
@NullMarked
public class NexcavateIntegrationMain implements Integration {
    public static @Nullable NexcavateItemsGroup nexcavateItemsGroup = null;

    @Override
    public String getHookPlugin() {
        return "Nexcavate";
    }

    @Override
    public void onEnable() {
        nexcavateItemsGroup =
            new NexcavateItemsGroup(KeyUtil.newKey("nexcavate_items_group"), Models.NEXCAVATE_ITEMS_GROUP);
        nexcavateItemsGroup.register(JustEnoughGuide.getInstance());
    }

    @Override
    public void onDisable() {
        if (nexcavateItemsGroup != null) {
            SlimefunRegistryUtil.unregisterItemGroup(nexcavateItemsGroup);
        }
    }
}
