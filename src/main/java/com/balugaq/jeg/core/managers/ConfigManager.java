package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
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
public class ConfigManager extends AbstractManager {
    private final boolean AUTO_UPDATE;
    private final boolean DEBUG;
    private final boolean SURVIVAL_IMPROVEMENTS;
    private final boolean CHEAT_IMPROVEMENTS;
    private final boolean PINYIN_SEARCH;
    private final boolean BOOKMARK;
    private final boolean RTS_SEARCH;
    private final @NotNull String SURVIVAL_GUIDE_TITLE;
    private final @NotNull String CHEAT_GUIDE_TITLE;
    private final @NotNull JavaPlugin plugin;

    public ConfigManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        setupDefaultConfig();
        this.AUTO_UPDATE = plugin.getConfig().getBoolean("auto-update", false);
        this.DEBUG = plugin.getConfig().getBoolean("debug", false);
        this.SURVIVAL_IMPROVEMENTS = plugin.getConfig().getBoolean("guide.survival-improvements", true);
        this.CHEAT_IMPROVEMENTS = plugin.getConfig().getBoolean("guide.cheat-improvements", true);
        this.PINYIN_SEARCH = plugin.getConfig().getBoolean("improvements.pinyin-search", true);
        this.BOOKMARK = plugin.getConfig().getBoolean("improvements.bookmark", true);
        this.SURVIVAL_GUIDE_TITLE = plugin.getConfig().getString("guide.survival-guide-title", "&2&lSlimefun Guide &7(Chest GUI) &8Advanced");
        this.CHEAT_GUIDE_TITLE = plugin.getConfig().getString("guide.cheat-guide-title", "&c&l&cSlimefun Guide &4(Cheat Sheet) &8Advanced");
        this.RTS_SEARCH = plugin.getConfig().getBoolean("improvements.rts-search", true);
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
            e.printStackTrace();
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

    public String getSurvivalGuideTitle() {
        return SURVIVAL_GUIDE_TITLE;
    }

    public String getCheatGuideTitle() {
        return CHEAT_GUIDE_TITLE;
    }

    public boolean isRTSSearch() {
        return RTS_SEARCH;
    }
}
