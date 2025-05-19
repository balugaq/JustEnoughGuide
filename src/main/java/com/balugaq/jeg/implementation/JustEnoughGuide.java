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

package com.balugaq.jeg.implementation;

import com.balugaq.jeg.core.managers.BookmarkManager;
import com.balugaq.jeg.core.managers.CommandManager;
import com.balugaq.jeg.core.managers.ConfigManager;
import com.balugaq.jeg.core.managers.IntegrationManager;
import com.balugaq.jeg.core.managers.ListenerManager;
import com.balugaq.jeg.core.managers.RTSBackpackManager;
import com.balugaq.jeg.core.services.LocalizationService;
import com.balugaq.jeg.implementation.guide.CheatGuideImplementation;
import com.balugaq.jeg.implementation.guide.SurvivalGuideImplementation;
import com.balugaq.jeg.implementation.items.GroupSetup;
import com.balugaq.jeg.implementation.option.BeginnersGuideOption;
import com.balugaq.jeg.utils.Lang;
import com.balugaq.jeg.utils.MinecraftVersion;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.SlimefunOfficialSupporter;
import com.balugaq.jeg.utils.UUIDUtils;
import com.google.common.base.Preconditions;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.CheatSheetSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.libraries.dough.updater.BlobBuildUpdater;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import lombok.Getter;
import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * This is the main class of the JustEnoughGuide plugin.
 * It depends on the Slimefun4 plugin and provides a set of features to improve the game experience.
 *
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings("unused")
@Getter
public class JustEnoughGuide extends JavaPlugin implements SlimefunAddon {
    public static final int RECOMMENDED_JAVA_VERSION = 17;
    public static final MinecraftVersion RECOMMENDED_MC_VERSION = MinecraftVersion.MINECRAFT_1_16;
    @Getter
    private static JustEnoughGuide instance;
    @Getter
    private static UUID serverUUID;
    @Getter
    private final @NotNull String username;
    @Getter
    private final @NotNull String repo;
    @Getter
    private final @NotNull String branch;
    @Getter
    private BookmarkManager bookmarkManager;
    @Getter
    private CommandManager commandManager;
    @Getter
    private ConfigManager configManager;
    @Getter
    private IntegrationManager integrationManager;
    @Getter
    private ListenerManager listenerManager;
    @Getter
    private RTSBackpackManager rtsBackpackManager;
    @Getter
    private @Nullable LocalizationService localizationService;
    @Getter
    private @Nullable MinecraftVersion minecraftVersion;
    @Getter
    private int javaVersion;
    @Getter
    @Nullable
    private Boolean interceptSearch_SlimefunTranslation;

    public JustEnoughGuide() {
        this.username = "balugaq";
        this.repo = "JustEnoughGuide";
        this.branch = "en-master";
    }

    public static BookmarkManager getBookmarkManager() {
        return getInstance().bookmarkManager;
    }

    public static CommandManager getCommandManager() {
        return getInstance().commandManager;
    }

    public static ConfigManager getConfigManager() {
        return getInstance().configManager;
    }

    public static IntegrationManager getIntegrationManager() {
        return getInstance().integrationManager;
    }

    public static ListenerManager getListenerManager() {
        return getInstance().listenerManager;
    }

    public static MinecraftVersion getMCVersion() {
        return getInstance().minecraftVersion;
    }

    public static @NotNull JustEnoughGuide getInstance() {
        Preconditions.checkArgument(instance != null, "JustEnoughGuide has not been enabled yet！");
        return JustEnoughGuide.instance;
    }

    /**
     * Initializes the plugin and sets up all necessary components.
     */
    @Override
    public void onEnable() {
        Preconditions.checkArgument(instance == null, "JustEnoughGuide has already been enabled!");
        instance = this;

        getLogger().info("Loading configuration...");
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.configManager.load();

        getLogger().info("Loading localization...");
        this.localizationService = new LocalizationService(this);
        String language = getConfigManager().getLanguage();
        this.localizationService.addLanguage(language);
        this.localizationService.addLanguage("en-US"); // Default language

        // Checking environment compatibility
        boolean isCompatible = environmentCheck();

        if (!isCompatible) {
            getLogger().severe(Lang.getStartup("environment-check-failed"));
            onDisable();
            return;
        }

        getLogger().info(Lang.getStartup("integrating-other-plugins"));
        this.integrationManager = new IntegrationManager(this);
        this.integrationManager.load();

        getLogger().info(Lang.getStartup("register-listeners"));
        this.listenerManager = new ListenerManager(this);
        this.listenerManager.load();

        if (getConfigManager().isAutoUpdate() && getDescription().getVersion().startsWith("DEV - ")) {
            new BlobBuildUpdater(this, getFile(), "JustEnoughGuide", "Dev").start();
        }

        getLogger().info(Lang.getStartup("register-commands"));
        this.commandManager = new CommandManager(this);
        this.commandManager.load();

        if (!commandManager.registerCommands()) {
            getLogger().warning(Lang.getStartup("register-commands-failed"));
        }

        final boolean survivalOverride = getConfigManager().isSurvivalImprovement();
        final boolean cheatOverride = getConfigManager().isCheatImprovement();
        if (survivalOverride || cheatOverride) {
            getLogger().info(Lang.getStartup("enabled-guide-override"));
            getLogger().info(Lang.getStartup("override-guide"));
            Field field = ReflectionUtil.getField(Slimefun.getRegistry().getClass(), "guides");
            if (field != null) {
                field.setAccessible(true);

                Map<SlimefunGuideMode, SlimefunGuideImplementation> newGuides = new EnumMap<>(SlimefunGuideMode.class);
                newGuides.put(
                        SlimefunGuideMode.SURVIVAL_MODE,
                        survivalOverride ? new SurvivalGuideImplementation() : new SurvivalSlimefunGuide(SlimefunOfficialSupporter.isShowVanillaRecipes(), SlimefunOfficialSupporter.isShowHiddenItemGroups()));
                newGuides.put(
                        SlimefunGuideMode.CHEAT_MODE,
                        cheatOverride ? new CheatGuideImplementation() : new CheatSheetSlimefunGuide());
                try {
                    field.set(Slimefun.getRegistry(), newGuides);
                } catch (IllegalAccessException ignored) {

                }
            }
            getLogger().info(survivalOverride ? Lang.getStartup("replaced-survival-guide") : Lang.getStartup("not-replaced-survival-guide"));
            getLogger().info(cheatOverride ? Lang.getStartup("replaced-cheat-guide") : Lang.getStartup("not-replaced-cheat-guide"));

            getLogger().info(Lang.getStartup("loading-bookmark"));
            this.bookmarkManager = new BookmarkManager(this);
            this.bookmarkManager.load();

            getLogger().info(Lang.getStartup("loading-guide-group"));
            GroupSetup.setup();

            getLogger().info("untranslated-checking-newbeginners-guide");

            if (getConfigManager().isBeginnerOption()) {
                getLogger().info("untranslated-loading-newbeginners-guide");
                SlimefunGuideSettings.addOption(new BeginnersGuideOption());
                getLogger().info("untranslated-loaded-newbeginners-guide");
            }

            getLogger().info(Lang.getStartup("loaded-guide-group"));
        }

        this.rtsBackpackManager = new RTSBackpackManager(this);
        this.rtsBackpackManager.load();

        File uuidFile = new File(getDataFolder(), "server-uuid");
        if (uuidFile.exists()) {
            try {
                serverUUID = UUID.nameUUIDFromBytes(Files.readAllBytes(Path.of(uuidFile.getPath())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            serverUUID = UUID.randomUUID();
            try {
                getDataFolder().mkdirs();
                uuidFile.createNewFile();
                Files.write(Path.of(uuidFile.getPath()), UUIDUtils.toByteArray(serverUUID));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (getIntegrationManager().isEnabledSlimefunTranslation()) {
            Object value = ReflectionUtil.getValue(SlimefunTranslation.getConfigService(), "interceptSearch");
            if (value instanceof Boolean bool) {
                interceptSearch_SlimefunTranslation = bool;
                ReflectionUtil.setValue(SlimefunTranslation.getConfigService(), "interceptSearch", false);
            }
        }

        getLogger().info(Lang.getStartup("enabled-jeg"));
    }

    /**
     * Cleans up resources and shuts down the plugin.
     */
    @Override
    public void onDisable() {
        Preconditions.checkArgument(instance != null, "JustEnoughGuide has not been enabled yet!");
        GroupSetup.shutdown();

        Field field = ReflectionUtil.getField(Slimefun.getRegistry().getClass(), "guides");
        if (field != null) {
            field.setAccessible(true);

            Map<SlimefunGuideMode, SlimefunGuideImplementation> newGuides = new EnumMap<>(SlimefunGuideMode.class);
            newGuides.put(SlimefunGuideMode.SURVIVAL_MODE, new SurvivalSlimefunGuide(SlimefunOfficialSupporter.isShowVanillaRecipes(), SlimefunOfficialSupporter.isShowHiddenItemGroups()));
            newGuides.put(SlimefunGuideMode.CHEAT_MODE, new CheatSheetSlimefunGuide());
            try {
                field.set(Slimefun.getRegistry(), newGuides);
            } catch (IllegalAccessException ignored) {

            }
        }

        // Rollback SlimefunTranslation interceptSearch
        if (getIntegrationManager().isEnabledSlimefunTranslation()) {
            if (interceptSearch_SlimefunTranslation != null) {
                ReflectionUtil.setValue(SlimefunTranslation.getConfigService(), "interceptSearch", interceptSearch_SlimefunTranslation);
            }
        }

        // Managers
        if (this.bookmarkManager != null) {
            this.bookmarkManager.unload();
        }

        if (this.integrationManager != null) {
            this.integrationManager.unload();
        }

        if (this.commandManager != null) {
            this.commandManager.unload();
        }

        if (this.listenerManager != null) {
            this.listenerManager.unload();
        }

        if (this.rtsBackpackManager != null) {
            this.rtsBackpackManager.unload();
        }

        if (this.configManager != null) {
            this.configManager.unload();
        }

        this.bookmarkManager = null;
        this.integrationManager = null;
        this.commandManager = null;
        this.listenerManager = null;
        this.rtsBackpackManager = null;
        this.localizationService = null;
        this.configManager = null;

        // Other fields
        this.minecraftVersion = null;
        this.javaVersion = 0;

        // Clear instance
        instance = null;
        getLogger().info("Disabled JustEnoughGuide");
    }

    /**
     * Returns the JavaPlugin instance.
     *
     * @return the JavaPlugin instance
     */
    @Override
    public @NotNull JavaPlugin getJavaPlugin() {
        return this;
    }

    /**
     * Returns the bug tracker URL for the plugin.
     *
     * @return the bug tracker URL
     */
    @Nullable
    @Override
    public String getBugTrackerURL() {
        return MessageFormat.format("https://github.com/{0}/{1}/issues/", this.username, this.repo);
    }

    /**
     * Logs a debug message if debugging is enabled.
     *
     * @param message the debug message to log
     */
    public void debug(String message) {
        if (getConfigManager().isDebug()) {
            getLogger().warning("[DEBUG] " + message);
        }
    }

    /**
     * Returns the version of the plugin.
     *
     * @return the version of the plugin
     */

    public @NotNull String getVersion() {
        return getDescription().getVersion();
    }

    /**
     * Checks the environment compatibility for the plugin.
     *
     * @return true if the environment is compatible, false otherwise
     */
    private boolean environmentCheck() {
        this.minecraftVersion = MinecraftVersion.getCurrentVersion();
        this.javaVersion = NumberUtils.getJavaVersion();
        if (minecraftVersion == null) {
            getLogger().warning(Lang.getStartup("null-mc-version"));
            return false;
        }

        if (minecraftVersion == MinecraftVersion.UNKNOWN) {
            getLogger().warning(Lang.getStartup("unknown-mc-version"));
        }

        if (!minecraftVersion.isAtLeast(RECOMMENDED_MC_VERSION)) {
            getLogger().warning(Lang.getStartup("mc-version-too-old", "recommended_major", RECOMMENDED_MC_VERSION.getMajor(), "recommended_minor", RECOMMENDED_MC_VERSION.getMinor()));
        }

        if (javaVersion < RECOMMENDED_JAVA_VERSION) {
            getLogger().warning(Lang.getStartup("java-version-too-old", "recommended_version", RECOMMENDED_JAVA_VERSION));
        }

        return true;
    }

    /**
     * Checks if debugging is enabled.
     *
     * @return true if debugging is enabled, false otherwise
     */
    public boolean isDebug() {
        return getConfigManager().isDebug();
    }
}
