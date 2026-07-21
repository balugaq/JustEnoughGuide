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

package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.editor.GroupResorter;
import com.balugaq.jeg.api.objects.CustomGroupConfiguration;
import com.balugaq.jeg.utils.GuideUtil;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
@Getter
@NullMarked
public class CustomGroup extends MixedGroup<CustomGroup> {
    public final CustomGroupConfiguration configuration;
    public final List<String> actions = new ArrayList<>();

    public CustomGroup(CustomGroupConfiguration configuration) {
        super(configuration.key(), configuration.item(), configuration.tier());
        this.configuration = configuration;

        List<ItemGroup> itemGroups = new ArrayList<>();
        for (Object obj : configuration.objects()) {
            switch (obj) {
                case ItemGroup group -> {
                    if (configuration.mode() == CustomGroupConfiguration.Mode.TRANSFER) {
                        // hide ItemGroup / SlimefunItem
                        GuideUtil.setForceHiddens(group, true);
                    }
                    itemGroups.add(group);
                    addGroup(group);
                }
                case SlimefunItem sf -> {
                    addItem(sf);
                    sf.getItemGroup().remove(sf);
                    sf.setItemGroup(this);
                }
                case String action -> actions.add(action);
                default -> {
                }
            }
        }

        GroupResorter.sort(itemGroups);

        this.pageMap.put(1, this);
    }

    @Override
    public boolean isCrossAddonItemGroup() {
        return true;
    }
}
