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
