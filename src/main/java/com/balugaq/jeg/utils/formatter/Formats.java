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
    public static final KeybindsFormat keybinds = new KeybindsFormat();
    public static final KeybindFormat keybind = new KeybindFormat();
    public static final ActionSelectFormat actionSelect = new ActionSelectFormat();

    public static void load() {
        main.loadMapping();
        nested.loadMapping();
        sub.loadMapping();
        recipe.loadMapping();
        helper.loadMapping();
        recipe_vanilla.loadMapping();
        recipe_display.loadMapping();
        settings.loadMapping();
        contributors.loadMapping();
        keybinds.loadMapping();
        keybind.loadMapping();
        actionSelect.loadMapping();
        actionSelect.setSize(4 * 9);

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

    /**
     * @author balugaq
     * @since 2.1
     */
    public static class Char {
        public static final char
        BACKGROUND = 'B',
        BACK = 'b',
        PREVIOUS_PAGE = 'P',
        NEXT_PAGE = 'N',
        BIG_RECIPE = 'E',
        ITEM_WIKI_PAGE = 'w',
        SETTINGS_PANEL_BUTTON = 'T',
        SEARCH = 'S',
        RTS = 'R',
        CONTENT = 'i',
        ITEM = 'i',
        RECIPE_INGREDIENT = 'r',
        RECIPE_TYPE = 't',
        RECIPE_DISPLAY = 'd',
        RECIPE_RESULT = 'i',
        ITEM_GROUP = 'G',
        ITEM_MARK = 'c',
        CER_PATCH = 'm',
        RECIPE_EDIT = 'K',
        BOOK_MARK = 'C',
        CONTRIBUTOR = 'p',
        CREDITS = 's',
        VERSION = 'v',
        SOURCE_CODE = 'u',
        SLIMEFUN_WIKI_PAGE = 'W',
        ADDONS = 'l',
        UNOFFICIAL_TIPS = 'z',
        UNKNOWN_FEATURE = 'U'
        ;
    }
}
