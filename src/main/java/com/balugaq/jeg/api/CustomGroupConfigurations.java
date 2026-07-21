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

package com.balugaq.jeg.api;

import com.balugaq.jeg.api.cfgparse.parser.ConfigurationParser;
import com.balugaq.jeg.api.groups.CustomGroup;
import com.balugaq.jeg.api.objects.CustomGroupConfiguration;
import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.formatter.Formats;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
@NullMarked
public class CustomGroupConfigurations {
    public static final String FILE_NAME = "custom-groups.yml";
    public static final File fileCustomGroups =
        new File(JustEnoughGuide.getInstance().getDataFolder(), FILE_NAME);

    @Getter
    private static final List<CustomGroupConfiguration> configurations = new ArrayList<>();

    private static final Map<String, CustomGroup> groups = new HashMap<>();

    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    public static void load() {
        if (!fileCustomGroups.exists()) {
            JustEnoughGuide.getInstance().saveResource(FILE_NAME, false);
            JustEnoughGuide.getInstance().getLogger().info("Created " + FILE_NAME);
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(fileCustomGroups);

        boolean enable = configuration.getBoolean("enabled", true);
        if (!enable) {
            return;
        }

        ConfigurationSection groups = configuration.getConfigurationSection("groups");
        if (groups == null) {
            JustEnoughGuide.getInstance().getLogger().warning("No groups found in " + FILE_NAME);
            return;
        }

        for (String key : groups.getKeys(false)) {
            ConfigurationSection section = groups.getConfigurationSection(key);
            if (section == null) {
                continue;
            }

            try {
                CustomGroupConfiguration parsed = ConfigurationParser.parse(section, CustomGroupConfiguration.class);
                configurations.add(parsed);
            } catch (Exception e) {
                Debug.trace(e);
            }
        }

        for (CustomGroupConfiguration ccg : configurations) {
            if (ccg.enabled()) {
                CustomGroup group = new CustomGroup(ccg);
                if (group.objects.isEmpty()) {
                    continue;
                }
                group.register(JustEnoughGuide.getInstance());
                CustomGroupConfigurations.groups.put(ccg.id(), group);
            }
        }
    }

    public static void unload() {
        configurations.clear();
        groups.clear();
        Formats.unload();
    }

    public static CustomGroup getGroup(String id) {
        return groups.get(id);
    }

    public static List<CustomGroup> getGroups() {
        return new ArrayList<>(groups.values());
    }
}
