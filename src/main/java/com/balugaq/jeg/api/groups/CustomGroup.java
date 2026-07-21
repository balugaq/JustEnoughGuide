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
