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
import com.balugaq.jeg.api.objects.annotaions.Warn;
import com.balugaq.jeg.utils.Debug;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for managing the configuration of the plugin.
 * Includes the following features' configuration:
 * - Auto-update: Whether the plugin should check for updates and download them automatically.
 * - Debug: Whether the plugin should print debug messages to the console.
 * - Survival Improvements: Whether the plugin should include survival improvements in the guide.
 * - Cheat Improvements: Whether the plugin should include cheat improvements in the guide.
 * - Pinyin Search: Whether the plugin should enable pinyin search in the guide.
 * - Bookmark: Whether the plugin should enable bookmark in the guide.
 *
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings({"ConstantValue", "unused"})
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
    private final @NotNull String SURVIVAL_GUIDE_TITLE;
    private final @NotNull String CHEAT_GUIDE_TITLE;
    private final @NotNull String SETTINGS_GUIDE_TITLE;
    private final @NotNull String CREDITS_GUIDE_TITLE;
    @Warn(reason = "No longer using it in EN version")
    private final @NotNull List<String> SHARED_CHARS;
    private final @NotNull List<String> SHARED_WORDS;
    private final @NotNull List<String> BLACKLIST;
    private final @NotNull List<String> MAIN_FORMAT;
    private final @NotNull List<String> NESTED_GROUP_FORMAT;
    private final @NotNull List<String> SUB_GROUP_FORMAT;
    private final @NotNull List<String> RECIPE_FORMAT;
    private final @NotNull List<String> HELPER_FORMAT;
    private final @NotNull List<String> RECIPE_VANILLA_FORMAT;
    private final @NotNull List<String> RECIPE_DISPLAY_FORMAT;
    private final @NotNull List<String> SETTINGS_FORMAT;
    private final @NotNull List<String> CONTRIBUTORS_FORMAT;
    private final @NotNull Map<String, String> LOCAL_TRANSLATE;
    private final @NotNull List<String> BANLIST;
    private final @NotNull String LANGUAGE;
    private final @NotNull JavaPlugin plugin;
    private final boolean EMC_VALUE_DISPLAY;
    private final boolean FinalTech_VALUE_DISPLAY;
    private final boolean FinalTECH_VALUE_DISPLAY;

    public ConfigManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        setupDefaultConfig();
        this.AUTO_UPDATE = plugin.getConfig().getBoolean("auto-update", false);
        this.DEBUG = plugin.getConfig().getBoolean("debug", false);
        this.SURVIVAL_IMPROVEMENTS = plugin.getConfig().getBoolean("guide.survival-improvements", true);
        this.CHEAT_IMPROVEMENTS = plugin.getConfig().getBoolean("guide.cheat-improvements", true);
        this.RECIPE_COMPLETE = plugin.getConfig().getBoolean("guide.recipe-complete", true);
        this.PINYIN_SEARCH = plugin.getConfig().getBoolean("improvements.pinyin-search", false);
        this.BOOKMARK = plugin.getConfig().getBoolean("improvements.bookmark", true);
        this.SURVIVAL_GUIDE_TITLE = plugin.getConfig().getString("guide.survival-guide-title", "&2&lSlimefun Guide &7(Chest GUI) &8Advanced");
        this.CHEAT_GUIDE_TITLE = plugin.getConfig().getString("guide.cheat-guide-title", "&c&l&cSlimefun Guide &4(Cheat Sheet) &8Advanced");
        this.SETTINGS_GUIDE_TITLE = plugin.getConfig().getString("guide.settings-guide-title", "Settings & Info");
        this.CREDITS_GUIDE_TITLE = plugin.getConfig().getString("guide.credits-guide-title", "Slimefun4 Contributors");
        this.RTS_SEARCH = plugin.getConfig().getBoolean("improvements.rts-search", true);

        this.BEGINNER_OPTION = plugin.getConfig().getBoolean("improvements.beginner-option", true);
        List<String> rawBlacklist = plugin.getConfig().getStringList("blacklist");
        if (rawBlacklist == null || rawBlacklist.isEmpty()) {
            this.BLACKLIST = new ArrayList<>();
            this.BLACKLIST.add("Fast Machines");
        } else {
            this.BLACKLIST = rawBlacklist;
        }

        List<String> rawSharedChars = plugin.getConfig().getStringList("shared-chars");
        if (rawSharedChars == null || rawSharedChars.isEmpty()) {
            // Deprecated
            this.SHARED_CHARS = new ArrayList<>();
        } else {
            this.SHARED_CHARS = rawSharedChars;
        }

        var rawSharedWords = plugin.getConfig().getStringList("shared-words");
        if (rawSharedWords == null || rawSharedWords.isEmpty()) {
            this.SHARED_WORDS = new ArrayList<>();
            this.SHARED_WORDS.add("barrel storage");
        } else {
            this.SHARED_WORDS = rawSharedWords;
        }

        List<String> rawMainFormat = plugin.getConfig().getStringList("custom-format.main");
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

        List<String> rawNestedGroupFormat = plugin.getConfig().getStringList("custom-format.nested-group");
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

        List<String> rawSubGroupFormat = plugin.getConfig().getStringList("custom-format.sub-group");
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

        List<String> rawRecipeFormat = plugin.getConfig().getStringList("custom-format.recipe");
        if (rawRecipeFormat == null || rawRecipeFormat.isEmpty()) {
            this.RECIPE_FORMAT = new ArrayList<>();
            this.RECIPE_FORMAT.add("b  rrr  w");
            this.RECIPE_FORMAT.add(" t rrr i ");
            this.RECIPE_FORMAT.add("   rrr  E");
        } else {
            this.RECIPE_FORMAT = rawRecipeFormat;
        }

        List<String> rawHelperFormat = plugin.getConfig().getStringList("custom-format.helper");
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

        List<String> rawRecipeVanillaFormat = plugin.getConfig().getStringList("custom-format.recipe-vanilla");
        if (rawRecipeVanillaFormat == null || rawRecipeVanillaFormat.isEmpty()) {
            this.RECIPE_VANILLA_FORMAT = new ArrayList<>();
            this.RECIPE_VANILLA_FORMAT.add("b  rrr  w");
            this.RECIPE_VANILLA_FORMAT.add(" t rrr i ");
            this.RECIPE_VANILLA_FORMAT.add("   rrr   ");
            this.RECIPE_VANILLA_FORMAT.add("BPBBBBBNB");
        } else {
            this.RECIPE_VANILLA_FORMAT = rawRecipeVanillaFormat;
        }

        List<String> rawRecipeDisplayFormat = plugin.getConfig().getStringList("custom-format.recipe-display");
        if (rawRecipeDisplayFormat == null || rawRecipeDisplayFormat.isEmpty()) {
            this.RECIPE_DISPLAY_FORMAT = new ArrayList<>();
            this.RECIPE_DISPLAY_FORMAT.add("b  rrr  w");
            this.RECIPE_DISPLAY_FORMAT.add(" t rrr i ");
            this.RECIPE_DISPLAY_FORMAT.add("   rrr  E");
            this.RECIPE_DISPLAY_FORMAT.add("BPBBBBBNB");
            this.RECIPE_DISPLAY_FORMAT.add("ddddddddd");
            this.RECIPE_DISPLAY_FORMAT.add("ddddddddd");
        } else {
            this.RECIPE_DISPLAY_FORMAT = rawRecipeDisplayFormat;
        }

        List<String> rawSettingsFormat = plugin.getConfig().getStringList("custom-format.settings");
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

        List<String> rawContributorsFormat = plugin.getConfig().getStringList("custom-format.contributors");
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

        this.LOCAL_TRANSLATE = new HashMap<>();
        ConfigurationSection c = plugin.getConfig().getConfigurationSection("local-translate");
        if (c != null) {
            for (String k : c.getKeys(false)) {
                this.LOCAL_TRANSLATE.put(k, c.getString(k));
            }
        }

        this.BANLIST = plugin.getConfig().getStringList("banlist");
        this.EMC_VALUE_DISPLAY = plugin.getConfig().getBoolean("improvements.emc-display-option", true);
        this.FinalTech_VALUE_DISPLAY = plugin.getConfig().getBoolean("improvements.finaltech-emc-display-option", true);
        this.FinalTECH_VALUE_DISPLAY = plugin.getConfig().getBoolean("improvements.finalTECH-emc-display-option", true);
        this.LANGUAGE = plugin.getConfig().getString("language", "en-US");
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

    @ParametersAreNonnullByDefault
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

    public @NotNull String getSurvivalGuideTitle() {
        return SURVIVAL_GUIDE_TITLE;
    }

    public @NotNull String getCheatGuideTitle() {
        return CHEAT_GUIDE_TITLE;
    }

    public @NotNull String getSettingsGuideTitle() {
        return SETTINGS_GUIDE_TITLE;
    }

    public @NotNull String getCreditsGuideTitle() {
        return CREDITS_GUIDE_TITLE;
    }

    public boolean isRTSSearch() {
        return RTS_SEARCH;
    }

    @Warn(reason = "No longer using it in EN version")
    public @NotNull List<String> getSharedChars() {
        return SHARED_CHARS;
    }

    public @NotNull List<String> getSharedWords() {
        return SHARED_WORDS;
    }

    public @NotNull List<String> getBlacklist() {
        return BLACKLIST;
    }

    public @NotNull List<String> getMainFormat() {
        return MAIN_FORMAT;
    }

    public @NotNull List<String> getNestedGroupFormat() {
        return NESTED_GROUP_FORMAT;
    }

    public @NotNull List<String> getSubGroupFormat() {
        return SUB_GROUP_FORMAT;
    }

    public @NotNull List<String> getRecipeFormat() {
        return RECIPE_FORMAT;
    }

    public @NotNull List<String> getHelperFormat() {
        return HELPER_FORMAT;
    }

    public @NotNull List<String> getRecipeVanillaFormat() {
        return RECIPE_VANILLA_FORMAT;
    }

    public @NotNull List<String> getRecipeDisplayFormat() {
        return RECIPE_DISPLAY_FORMAT;
    }

    public @NotNull List<String> getSettingsFormat() {
        return SETTINGS_FORMAT;
    }

    public @NotNull List<String> getContributorsFormat() {
        return CONTRIBUTORS_FORMAT;
    }

    public @NotNull Map<String, String> getLocalTranslate() {
        return LOCAL_TRANSLATE;
    }

    public @NotNull List<String> getBanlist() {
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

    public @NotNull String getLanguage() {
        return LANGUAGE;
    }
}
