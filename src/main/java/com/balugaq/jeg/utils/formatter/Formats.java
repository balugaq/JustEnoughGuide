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

package com.balugaq.jeg.utils.formatter;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.ItemStackUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author balugaq
 * @since 1.6
 */
public class Formats {
    public static final String FILE_NAME = "custom-icons.yml";
    public static final File fileCustomIcons =
            new File(JustEnoughGuide.getInstance().getDataFolder(), FILE_NAME);
    public static final Map<String, Format> customFormats = new HashMap<>();
    public static final MainFormat main = new MainFormat();
    public static final NestedGroupFormat nested = new NestedGroupFormat();
    public static final SubGroupFormat sub = new SubGroupFormat();
    public static final RecipeFormat recipe = new RecipeFormat();
    public static final HelperFormat helper = new HelperFormat();
    public static final RecipeVanillaFormat recipe_vanilla = new RecipeVanillaFormat();
    public static final RecipeDisplayFormat recipe_display = new RecipeDisplayFormat();
    public static final SettingsFormat settings = new SettingsFormat();
    public static final ContributorsFormat contributors = new ContributorsFormat();

    static {
        main.loadMapping();
        nested.loadMapping();
        sub.loadMapping();
        recipe.loadMapping();
        helper.loadMapping();
        recipe_vanilla.loadMapping();
        recipe_display.loadMapping();
        settings.loadMapping();
        contributors.loadMapping();

        loadCustomIcon();
    }

    public static void loadCustomIcon() {
        if (!fileCustomIcons.exists()) {
            JustEnoughGuide.getInstance().saveResource(FILE_NAME, false);
            JustEnoughGuide.getInstance().getLogger().info("Created " + FILE_NAME);
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(fileCustomIcons);

        Set<String> keys = configuration.getKeys(false);
        for (String key : keys) {
            if (key.length() > 1) {
                JustEnoughGuide.getInstance().getLogger().warning(FILE_NAME + " 中发现无效的 Icon 自定义字符: " + key);
                continue;
            }

            char c = key.charAt(0);
            ItemStack read = ItemStackUtil.readItem(c, configuration.getConfigurationSection(key));
            if (read != null) {
                Format.customMapping.put(c, read);
            }
        }
    }

    public static void addCustomFormat(String id, Format format) {
        customFormats.put(id, format);
    }

    public static void unload() {
        customFormats.clear();
        Format.customMapping.clear();
    }
}
