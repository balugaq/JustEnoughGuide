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

package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.api.objects.collection.Pair;
import com.balugaq.jeg.utils.Debug;
import com.github.houbb.opencc4j.support.data.impl.OpenccDatas;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is responsible for managing the configuration of the plugin. Includes the following features'
 * configuration: - Auto-update: Whether the plugin should check for updates and download them automatically. - Debug:
 * Whether the plugin should print debug messages to the console. - Survival Improvements: Whether the plugin should
 * include survival improvements in the guide. - Cheat Improvements: Whether the plugin should include cheat
 * improvements in the guide. - Pinyin Search: Whether the plugin should enable pinyin search in the guide. - Bookmark:
 * Whether the plugin should enable bookmark in the guide.
 *
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings({"ConstantValue", "unused"})
@NullMarked
public class ConfigManager extends AbstractManager {
    private final boolean AUTO_UPDATE;
    private final boolean DEBUG;
    private final boolean SURVIVAL_IMPROVEMENTS;
    private final boolean CHEAT_IMPROVEMENTS;
    private final boolean RECIPE_COMPLETE;
    private final boolean PINYIN_SEARCH;
    private final boolean BOOKMARK;
    private final boolean RTS_SEARCH;
    private final boolean BEGINNER_OPTION;
    private final boolean DISABLE_BUNDLE_INTERACTION;
    private final String SURVIVAL_GUIDE_TITLE;
    private final String CHEAT_GUIDE_TITLE;
    private final String SETTINGS_GUIDE_TITLE;
    private final String CREDITS_GUIDE_TITLE;
    private final List<String> SHARED_CHARS;
    private final List<String> BLACKLIST;
    private final List<String> MAIN_FORMAT;
    private final List<String> NESTED_GROUP_FORMAT;
    private final List<String> SUB_GROUP_FORMAT;
    private final List<String> RECIPE_FORMAT;
    private final List<String> HELPER_FORMAT;
    private final List<String> RECIPE_VANILLA_FORMAT;
    private final List<String> RECIPE_DISPLAY_FORMAT;
    private final List<String> SETTINGS_FORMAT;
    private final List<String> CONTRIBUTORS_FORMAT;
    private final List<String> KEYBINDS_FORMAT;
    private final List<String> KEYBIND_FORMAT;
    private final List<String> ACTION_SELECT_FORMAT;
    private final Map<String, String> LOCAL_TRANSLATE;
    private final List<String> BANLIST;
    private final JavaPlugin plugin;
    private final boolean EMC_VALUE_DISPLAY;
    private final boolean FinalTech_VALUE_DISPLAY;
    private final boolean FinalTECH_VALUE_DISPLAY;
    private final boolean ITEM_SHAREABLE;
    private final boolean CER_PATCH;
    private final boolean ALLOW_ACTION_REDIRECT;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setupDefaultConfig();
        FileConfiguration cfg = plugin.getConfig();

        this.AUTO_UPDATE = cfg.getBoolean("auto-update", false);
        this.DEBUG = cfg.getBoolean("debug", false);
        this.SURVIVAL_IMPROVEMENTS = cfg.getBoolean("guide.survival-improvements", true);
        this.CHEAT_IMPROVEMENTS = cfg.getBoolean("guide.cheat-improvements", true);
        this.RECIPE_COMPLETE = cfg.getBoolean("guide.recipe-complete", true);
        this.PINYIN_SEARCH = cfg.getBoolean("improvements.pinyin-search", true);
        this.BOOKMARK = cfg.getBoolean("improvements.bookmark", true);
        this.SURVIVAL_GUIDE_TITLE = cfg
                .getString("guide.survival-guide-title", "&2&lSlimefun 指南 (生存模式)         &e&l爱来自 JustEnoughGuide");
        this.CHEAT_GUIDE_TITLE = cfg
                .getString("guide.cheat-guide-title", "&c&lSlimefun 指南 (作弊模式)         &e&l爱来自 JustEnoughGuide");
        this.SETTINGS_GUIDE_TITLE = cfg.getString("guide.settings-guide-title", "设置 & 详情");
        this.CREDITS_GUIDE_TITLE = cfg.getString("guide.credits-guide-title", "Slimefun4 贡献者");
        this.RTS_SEARCH = cfg.getBoolean("improvements.rts-search", true);

        this.BEGINNER_OPTION = cfg.getBoolean("improvements.beginner-option", true);
        this.DISABLE_BUNDLE_INTERACTION = cfg.getBoolean("disable-bundle-interaction", true);

        List<String> rawBlacklist = cfg.getStringList("blacklist");
        if (rawBlacklist == null || rawBlacklist.isEmpty()) {
            this.BLACKLIST = new ArrayList<>();
            this.BLACKLIST.add("快捷");
        } else {
            this.BLACKLIST = rawBlacklist;
        }

        List<String> rawSharedChars = cfg.getStringList("shared-chars");
        if (rawSharedChars == null || rawSharedChars.isEmpty()) {
            this.SHARED_CHARS = new ArrayList<>();
        } else {
            this.SHARED_CHARS = rawSharedChars;
        }

        try {
            processSharedChars(OpenccDatas.stChar().data().getDataMap().entrySet());
            processSharedChars(OpenccDatas.twStChar().data().getDataMap().entrySet());
        } catch (Exception e) {
            Debug.warn(e);
        }

        List<String> rawMainFormat = cfg.getStringList("custom-format.main");
        if (rawMainFormat == null || rawMainFormat.isEmpty()) {
            this.MAIN_FORMAT = new ArrayList<>();
            this.MAIN_FORMAT.add("BTBBBBRSB");
            this.MAIN_FORMAT.add("GGGGGGGGG");
            this.MAIN_FORMAT.add("GGGGGGGGG");
            this.MAIN_FORMAT.add("GGGGGGGGG");
            this.MAIN_FORMAT.add("GGGGGGGGG");
            this.MAIN_FORMAT.add("BPBBCBBNB");
        } else {
            this.MAIN_FORMAT = rawMainFormat;
        }

        List<String> rawNestedGroupFormat = cfg.getStringList("custom-format.nested-group");
        if (rawNestedGroupFormat == null || rawNestedGroupFormat.isEmpty()) {
            this.NESTED_GROUP_FORMAT = new ArrayList<>();
            this.NESTED_GROUP_FORMAT.add("BbBBBBRSB");
            this.NESTED_GROUP_FORMAT.add("GGGGGGGGG");
            this.NESTED_GROUP_FORMAT.add("GGGGGGGGG");
            this.NESTED_GROUP_FORMAT.add("GGGGGGGGG");
            this.NESTED_GROUP_FORMAT.add("GGGGGGGGG");
            this.NESTED_GROUP_FORMAT.add("BPBBCBBNB");
        } else {
            this.NESTED_GROUP_FORMAT = rawNestedGroupFormat;
        }

        List<String> rawSubGroupFormat = cfg.getStringList("custom-format.sub-group");
        if (rawSubGroupFormat == null || rawSubGroupFormat.isEmpty()) {
            this.SUB_GROUP_FORMAT = new ArrayList<>();
            this.SUB_GROUP_FORMAT.add("BbBBBBRSB");
            this.SUB_GROUP_FORMAT.add("iiiiiiiii");
            this.SUB_GROUP_FORMAT.add("iiiiiiiii");
            this.SUB_GROUP_FORMAT.add("iiiiiiiii");
            this.SUB_GROUP_FORMAT.add("iiiiiiiii");
            this.SUB_GROUP_FORMAT.add("BPBcCBBNB");
        } else {
            this.SUB_GROUP_FORMAT = rawSubGroupFormat;
        }

        List<String> rawRecipeFormat = cfg.getStringList("custom-format.recipe");
        if (rawRecipeFormat == null || rawRecipeFormat.isEmpty()) {
            this.RECIPE_FORMAT = new ArrayList<>();
            this.RECIPE_FORMAT.add("b  rrr  w");
            this.RECIPE_FORMAT.add(" t rrr i ");
            this.RECIPE_FORMAT.add("m  rrr  E");
        } else {
            List<String> old = new ArrayList<>();
            old.add("b  rrr  w");
            old.add(" t rrr i ");
            old.add("   rrr  E");
            List<String> n = new ArrayList<>();
            n.add("b  rrr  w");
            n.add(" t rrr i ");
            n.add("m  rrr  E");
            if (rawRecipeFormat.equals(old)) {
                cfg.set("custom-format.recipe", n);
                this.RECIPE_FORMAT = n;
            } else {
                this.RECIPE_FORMAT = rawRecipeFormat;
            }
        }

        List<String> rawHelperFormat = cfg.getStringList("custom-format.helper");
        if (rawHelperFormat == null || rawHelperFormat.isEmpty()) {
            this.HELPER_FORMAT = new ArrayList<>();
            this.HELPER_FORMAT.add("BbBBBBRSB");
            this.HELPER_FORMAT.add("B   A   B");
            this.HELPER_FORMAT.add("BhhhhhhhB");
            this.HELPER_FORMAT.add("BhhhhhhhB");
            this.HELPER_FORMAT.add("BhhhhhhhB");
            this.HELPER_FORMAT.add("BPBBCBBNB");
        } else {
            this.HELPER_FORMAT = rawHelperFormat;
        }

        List<String> rawRecipeVanillaFormat = cfg.getStringList("custom-format.recipe-vanilla");
        if (rawRecipeVanillaFormat == null || rawRecipeVanillaFormat.isEmpty()) {
            this.RECIPE_VANILLA_FORMAT = new ArrayList<>();
            this.RECIPE_VANILLA_FORMAT.add("b  rrr  w");
            this.RECIPE_VANILLA_FORMAT.add(" t rrr i ");
            this.RECIPE_VANILLA_FORMAT.add("   rrr   ");
            this.RECIPE_VANILLA_FORMAT.add("BPBBBBBNB");
        } else {
            this.RECIPE_VANILLA_FORMAT = rawRecipeVanillaFormat;
        }

        List<String> rawRecipeDisplayFormat = cfg.getStringList("custom-format.recipe-display");
        if (rawRecipeDisplayFormat == null || rawRecipeDisplayFormat.isEmpty()) {
            this.RECIPE_DISPLAY_FORMAT = new ArrayList<>();
            this.RECIPE_DISPLAY_FORMAT.add("b  rrr  w");
            this.RECIPE_DISPLAY_FORMAT.add(" t rrr i ");
            this.RECIPE_DISPLAY_FORMAT.add("m  rrr  E");
            this.RECIPE_DISPLAY_FORMAT.add("BPBBBBBNB");
            this.RECIPE_DISPLAY_FORMAT.add("ddddddddd");
            this.RECIPE_DISPLAY_FORMAT.add("ddddddddd");
        } else {
            List<String> old = new ArrayList<>();
            old.add("b  rrr  w");
            old.add(" t rrr i ");
            old.add("   rrr  E");
            old.add("BPBBBBBNB");
            old.add("ddddddddd");
            old.add("ddddddddd");
            List<String> n = new ArrayList<>();
            n.add("b  rrr  w");
            n.add(" t rrr i ");
            n.add("m  rrr  E");
            n.add("BPBBBBBNB");
            n.add("ddddddddd");
            n.add("ddddddddd");
            if (rawRecipeDisplayFormat.equals(old)) {
                cfg.set("custom-format.recipe-display", n);
                this.RECIPE_DISPLAY_FORMAT = n;
            } else {
                this.RECIPE_DISPLAY_FORMAT = rawRecipeDisplayFormat;
            }
        }

        List<String> rawSettingsFormat = cfg.getStringList("custom-format.settings");
        if (rawSettingsFormat == null || rawSettingsFormat.isEmpty()) {
            this.SETTINGS_FORMAT = new ArrayList<>();
            this.SETTINGS_FORMAT.add("bBsBvBuBW");
            this.SETTINGS_FORMAT.add("BBBBBBBBB");
            this.SETTINGS_FORMAT.add("BoooooooB");
            this.SETTINGS_FORMAT.add("BoooooooB");
            this.SETTINGS_FORMAT.add("BPBBBBBNB");
            this.SETTINGS_FORMAT.add("BBlBBBUBB");
        } else {
            this.SETTINGS_FORMAT = rawSettingsFormat;
        }

        List<String> rawContributorsFormat = cfg.getStringList("custom-format.contributors");
        if (rawContributorsFormat == null || rawContributorsFormat.isEmpty()) {
            this.CONTRIBUTORS_FORMAT = new ArrayList<>();
            this.CONTRIBUTORS_FORMAT.add("BbBBBBBBB");
            this.CONTRIBUTORS_FORMAT.add("ppppppppp");
            this.CONTRIBUTORS_FORMAT.add("ppppppppp");
            this.CONTRIBUTORS_FORMAT.add("ppppppppp");
            this.CONTRIBUTORS_FORMAT.add("ppppppppp");
            this.CONTRIBUTORS_FORMAT.add("BPBBBBBNB");
        } else {
            this.CONTRIBUTORS_FORMAT = rawContributorsFormat;
        }

        List<String> rawKeybindsFormat = cfg.getStringList("custom-format.keybinds");
        if (rawKeybindsFormat == null || rawKeybindsFormat.isEmpty()) {
            this.KEYBINDS_FORMAT = new ArrayList<>();
            this.KEYBINDS_FORMAT.add("BBBBBBBBB");
            this.KEYBINDS_FORMAT.add("BiiiiiiiB");
            this.KEYBINDS_FORMAT.add("BiiiiiiiB");
            this.KEYBINDS_FORMAT.add("BiiiiiiiB");
            this.KEYBINDS_FORMAT.add("BiiiiiiiB");
            this.KEYBINDS_FORMAT.add("BPBBBBBNB");
        } else {
            this.KEYBINDS_FORMAT = rawKeybindsFormat;
        }

        List<String> rawKeybindFormat = cfg.getStringList("custom-format.keybind");
        if (rawKeybindFormat == null || rawKeybindFormat.isEmpty()) {
            this.KEYBIND_FORMAT = new ArrayList<>();
            this.KEYBIND_FORMAT.add("BbBBBBBBB");
            this.KEYBIND_FORMAT.add("BxyzBxyzB");
            this.KEYBIND_FORMAT.add("BxyzBxyzB");
            this.KEYBIND_FORMAT.add("BxyzBxyzB");
            this.KEYBIND_FORMAT.add("BxyzBxyzB");
            this.KEYBIND_FORMAT.add("BPBBBBBNB");
        } else {
            this.KEYBIND_FORMAT = rawKeybindFormat;
        }

        List<String> rawActionSelectFormat = cfg.getStringList("custom-format.action-select");
        if (rawActionSelectFormat == null || rawActionSelectFormat.isEmpty()) {
            this.ACTION_SELECT_FORMAT = new ArrayList<>();
            this.ACTION_SELECT_FORMAT.add("iiiiiiiii");
            this.ACTION_SELECT_FORMAT.add("iiiiiiiii");
            this.ACTION_SELECT_FORMAT.add("BPBBBBBNB");
        } else {
            this.ACTION_SELECT_FORMAT = rawActionSelectFormat;
        }

        this.LOCAL_TRANSLATE = new HashMap<>();
        ConfigurationSection c = cfg.getConfigurationSection("local-translate");
        if (c != null) {
            for (String k : c.getKeys(false)) {
                String s = c.getString(k);
                if (s != null) {
                    this.LOCAL_TRANSLATE.put(k, s);
                }
            }
        }

        this.BANLIST = cfg.getStringList("banlist");
        this.EMC_VALUE_DISPLAY = cfg.getBoolean("improvements.emc-display-option", true);
        this.FinalTech_VALUE_DISPLAY = cfg.getBoolean("improvements.finaltech-emc-display-option", true);
        this.FinalTECH_VALUE_DISPLAY = cfg.getBoolean("improvements.finalTECH-emc-display-option", true);
        this.ITEM_SHAREABLE = cfg.getBoolean("improvements.item-shareable", true);
        this.CER_PATCH = cfg.getBoolean("improvements.cer-patch", true);
        this.ALLOW_ACTION_REDIRECT = cfg.getBoolean("improvements.allow-action-redirect", true);
    }

    private void setupDefaultConfig() {
        // config.yml
        final InputStream inputStream = plugin.getResource("config.yml");
        final File existingFile = new File(plugin.getDataFolder(), "config.yml");

        if (inputStream == null) {
            return;
        }

        final Reader reader = new InputStreamReader(inputStream);
        final FileConfiguration resourceConfig = YamlConfiguration.loadConfiguration(reader);
        final FileConfiguration existingConfig = YamlConfiguration.loadConfiguration(existingFile);

        for (String key : resourceConfig.getKeys(false)) {
            checkKey(existingConfig, resourceConfig, key);
        }

        try {
            existingConfig.save(existingFile);
        } catch (IOException e) {
            Debug.trace(e);
        }
    }

    private void checkKey(FileConfiguration existingConfig, FileConfiguration resourceConfig, String key) {
        final Object currentValue = existingConfig.get(key);
        final Object newValue = resourceConfig.get(key);
        if (newValue instanceof ConfigurationSection section) {
            for (String sectionKey : section.getKeys(false)) {
                checkKey(existingConfig, resourceConfig, key + "." + sectionKey);
            }
        } else if (currentValue == null) {
            existingConfig.set(key, newValue);
        }
    }

    public boolean isAutoUpdate() {
        return AUTO_UPDATE;
    }

    public boolean isDebug() {
        return DEBUG;
    }

    public boolean isSurvivalImprovement() {
        return SURVIVAL_IMPROVEMENTS;
    }

    public boolean isCheatImprovement() {
        return CHEAT_IMPROVEMENTS;
    }

    public boolean isPinyinSearch() {
        return PINYIN_SEARCH;
    }

    public boolean isBookmark() {
        return BOOKMARK;
    }

    public boolean isBeginnerOption() {
        return BEGINNER_OPTION;
    }

    public String getSurvivalGuideTitle() {
        return SURVIVAL_GUIDE_TITLE;
    }

    public String getCheatGuideTitle() {
        return CHEAT_GUIDE_TITLE;
    }

    public String getSettingsGuideTitle() {
        return SETTINGS_GUIDE_TITLE;
    }

    public String getCreditsGuideTitle() {
        return CREDITS_GUIDE_TITLE;
    }

    public boolean isRTSSearch() {
        return RTS_SEARCH;
    }

    public List<String> getSharedChars() {
        return SHARED_CHARS;
    }

    public List<String> getBlacklist() {
        return BLACKLIST;
    }

    public List<String> getMainFormat() {
        return MAIN_FORMAT;
    }

    public List<String> getNestedGroupFormat() {
        return NESTED_GROUP_FORMAT;
    }

    public List<String> getSubGroupFormat() {
        return SUB_GROUP_FORMAT;
    }

    public List<String> getRecipeFormat() {
        return RECIPE_FORMAT;
    }

    public List<String> getHelperFormat() {
        return HELPER_FORMAT;
    }

    public List<String> getRecipeVanillaFormat() {
        return RECIPE_VANILLA_FORMAT;
    }

    public List<String> getRecipeDisplayFormat() {
        return RECIPE_DISPLAY_FORMAT;
    }

    public List<String> getSettingsFormat() {
        return SETTINGS_FORMAT;
    }

    public List<String> getContributorsFormat() {
        return CONTRIBUTORS_FORMAT;
    }

    public List<String> getKeybindsFormat() {
        return KEYBINDS_FORMAT;
    }

    public List<String> getKeybindFormat() {
        return KEYBIND_FORMAT;
    }

    public List<String> getActionSelectFormat() {
        return ACTION_SELECT_FORMAT;
    }

    public Map<String, String> getLocalTranslate() {
        return LOCAL_TRANSLATE;
    }

    public List<String> getBanlist() {
        return BANLIST;
    }

    public boolean isRecipeComplete() {
        return RECIPE_COMPLETE;
    }

    public boolean isEMCValueDisplay() {
        return EMC_VALUE_DISPLAY;
    }

    public boolean isFinalTechValueDisplay() {
        return FinalTech_VALUE_DISPLAY;
    }

    public boolean isFinalTECHValueDisplay() {
        return FinalTECH_VALUE_DISPLAY;
    }

    public boolean isItemShareable() {
        return ITEM_SHAREABLE;
    }

    public boolean isDisabledBundleInteraction() {
        return DISABLE_BUNDLE_INTERACTION;
    }

    public boolean isCerPatch() {
        return CER_PATCH;
    }

    public boolean isAllowActionRedirect() {
        return ALLOW_ACTION_REDIRECT;
    }

    private void processSharedChars(Set<Map.Entry<String, List<String>>> data) {
        for (Pair<String, String> p : data.stream().map(e -> new Pair<>(e.getKey(), e.getValue().get(0))).toList()) {
            if (p.first().length() != p.second().length()) {
                continue;
            }

            for (int i = 0; i < p.first().length(); i++) {
                this.SHARED_CHARS.add(" " + p.first.charAt(i) + p.second.charAt(i));
            }
        }
    }
}
