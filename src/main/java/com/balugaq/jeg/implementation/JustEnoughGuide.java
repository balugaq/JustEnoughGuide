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

package com.balugaq.jeg.implementation;

import com.balugaq.jeg.api.CustomGroupConfigurations;
import com.balugaq.jeg.api.cost.please_set_cer_patch_to_false_in_config_when_you_see_this.CERCalculator;
import com.balugaq.jeg.api.editor.GroupResorter;
import com.balugaq.jeg.api.groups.SearchGroup;
import com.balugaq.jeg.api.groups.VanillaItemsGroup;
import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.core.integrations.finaltechs.finalTECHCommon.FinalTECHValueDisplayOption;
import com.balugaq.jeg.core.managers.BookmarkManager;
import com.balugaq.jeg.core.managers.CommandManager;
import com.balugaq.jeg.core.managers.ConfigManager;
import com.balugaq.jeg.core.managers.IntegrationManager;
import com.balugaq.jeg.core.managers.ListenerManager;
import com.balugaq.jeg.core.managers.RTSBackpackManager;
import com.balugaq.jeg.implementation.guide.CheatGuideImplementation;
import com.balugaq.jeg.implementation.guide.SurvivalGuideImplementation;
import com.balugaq.jeg.implementation.items.GroupSetup;
import com.balugaq.jeg.implementation.items.ItemsSetup;
import com.balugaq.jeg.implementation.option.BeginnersGuideOption;
import com.balugaq.jeg.implementation.option.CerPatchGuideOption;
import com.balugaq.jeg.implementation.option.KeybindsSettingsGuideOption;
import com.balugaq.jeg.implementation.option.NoticeMissingMaterialGuideOption;
import com.balugaq.jeg.implementation.option.RecursiveRecipeFillingGuideOption;
import com.balugaq.jeg.implementation.option.ShareInGuideOption;
import com.balugaq.jeg.implementation.option.ShareOutGuideOption;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.MinecraftVersion;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.SlimefunRegistryUtil;
import com.balugaq.jeg.utils.SpecialMenuProvider;
import com.balugaq.jeg.utils.UUIDUtils;
import com.balugaq.jeg.utils.platform.PlatformUtil;
import com.balugaq.jeg.utils.platform.scheduler.TaskScheduler;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.CheatSheetSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import lombok.Getter;
import net.guizhanss.guizhanlibplugin.updater.GuizhanUpdater;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This is the main class of the JustEnoughGuide plugin. It depends on the Slimefun4 plugin and provides a set of
 * features to improve the game experience.
 *
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings({"unused", "Lombok", "deprecation", "ResultOfMethodCallIgnored"})
@Getter
@NullMarked
public class JustEnoughGuide extends JavaPlugin implements SlimefunAddon {
    public static final int RECOMMENDED_JAVA_VERSION = 17;
    public static final MinecraftVersion RECOMMENDED_MC_VERSION = MinecraftVersion.MINECRAFT_1_16;

    @Getter
    @UnknownNullability
    private static JustEnoughGuide instance = null;

    @Getter
    @UnknownNullability
    private static UUID serverUUID = null;

    @Getter
    private final String username;

    @Getter
    private final String repo;

    @Getter
    private final String branch;

    @Getter
    @UnknownNullability
    private BookmarkManager bookmarkManager = null;

    @Getter
    @UnknownNullability
    private CommandManager commandManager = null;

    @Getter
    @UnknownNullability
    private ConfigManager configManager = null;

    @Getter
    @UnknownNullability
    private IntegrationManager integrationManager = null;

    @Getter
    @UnknownNullability
    private ListenerManager listenerManager = null;

    @Getter
    @UnknownNullability
    private RTSBackpackManager rtsBackpackManager = null;

    @Getter
    @UnknownNullability
    private MinecraftVersion minecraftVersion = null;

    @Getter
    @UnknownNullability
    private TaskScheduler scheduler = null;

    @Getter
    private int javaVersion = 0;

    public JustEnoughGuide() {
        this.username = "balugaq";
        this.repo = "JustEnoughGuide";
        this.branch = "master";
    }

    public static BookmarkManager getBookmarkManager() {
        return getInstance().bookmarkManager;
    }

    public static JustEnoughGuide getInstance() {
        return JustEnoughGuide.instance;
    }

    public static CommandManager getCommandManager() {
        return getInstance().commandManager;
    }    public static ConfigManager getConfigManager() {
        return getInstance().configManager;
    }

    public static ListenerManager getListenerManager() {
        return getInstance().listenerManager;
    }    public static IntegrationManager getIntegrationManager() {
        return getInstance().integrationManager;
    }

    public static MinecraftVersion getMinecraftVersion() {
        return getInstance().minecraftVersion;
    }

    public static void postServerStartup(Runnable runnable) {
        JustEnoughGuide.runNextTick(runnable);
    }

    public static void runNextTick(Runnable runnable) {
        getScheduler().runNextTick(runnable);
    }

    public static TaskScheduler getScheduler() {
        return getInstance().scheduler;
    }

    public static void postServerStartupAsynchronously(Runnable runnable) {
        JustEnoughGuide.runLaterAsync(runnable, 1L);
    }

    public static void runLaterAsync(Runnable runnable, long delay) {
        getScheduler().runLaterAsync(runnable, delay);
    }

    public static boolean disableAutomaticallyLoadItems() {
        boolean before = Slimefun.getConfigManager().isAutoLoadingEnabled();
        Slimefun.getConfigManager().setAutoLoadingMode(false);
        return before;
    }

    public static void setAutomaticallyLoadItems(boolean value) {
        Slimefun.getConfigManager().setAutoLoadingMode(value);
    }

    public static void runLater(Runnable runnable, long delay) {
        getScheduler().runLater(runnable, delay);
    }

    public static void runTimer(Runnable runnable, long delay, long period) {
        getScheduler().runTimer(runnable, delay, period);
    }

    public static void runTimerAsync(Runnable runnable, long delay, long period) {
        getScheduler().runTimerAsync(runnable, delay, period);
    }

    /**
     * Returns the JavaPlugin instance.
     *
     * @return the JavaPlugin instance
     */
    @Override
    public JavaPlugin getJavaPlugin() {
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
     * @param message
     *         the debug message to log
     */
    public void debug(String message) {
        if (getConfigManager().isDebug()) {
            getLogger().warning("[DEBUG] " + message);
        }
    }    /**
     * Initializes the plugin and sets up all necessary components.
     */
    @SuppressWarnings("DuplicateExpressions")
    @Override
    public void onEnable() {
        instance = this;

        // Checking environment compatibility
        boolean isCompatible = environmentCheck();

        if (!isCompatible) {
            getLogger().warning("环境不兼容！插件已被禁用！");
            onDisable();
            return;
        }

        PlatformUtil.initialize();
        this.scheduler = TaskScheduler.create();

        getLogger().info("正在加载配置文件...");
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.configManager.load();

        getLogger().info("正在适配其他插件...");
        this.integrationManager = new IntegrationManager(this);
        this.integrationManager.load();

        getLogger().info("正在注册监听器...");
        this.listenerManager = new ListenerManager(this);
        this.listenerManager.load();

        getLogger().info("尝试自动更新...");
        tryUpdate();

        getLogger().info("正在注册指令");
        this.commandManager = new CommandManager(this);
        this.commandManager.load();

        if (!commandManager.registerCommands()) {
            getLogger().warning("注册指令失败！");
        }

        final boolean survivalOverride = getConfigManager().isSurvivalImprovement();
        final boolean cheatOverride = getConfigManager().isCheatImprovement();
        if (survivalOverride || cheatOverride) {
            getLogger().info("已开启指南替换！");
            getLogger().info("正在替换指南...");
            Map<SlimefunGuideMode, SlimefunGuideImplementation> newGuides = new EnumMap<>(SlimefunGuideMode.class);
            newGuides.put(
                    SlimefunGuideMode.SURVIVAL_MODE,
                    survivalOverride ? new SurvivalGuideImplementation() : new SurvivalSlimefunGuide()
            );
            newGuides.put(
                    SlimefunGuideMode.CHEAT_MODE,
                    cheatOverride ? new CheatGuideImplementation() : new CheatSheetSlimefunGuide()
            );

            try {
                ReflectionUtil.setValue(Slimefun.getRegistry(), "guides", newGuides);
            } catch (Exception e) {
                Debug.trace(e);
            }
            getLogger().info(survivalOverride ? "已开启替换生存指南" : "未开启替换生存指南");
            getLogger().info(cheatOverride ? "已开启替换作弊指南" : "未开启替换作弊指南");

            getLogger().info("正在加载书签...");
            this.bookmarkManager = new BookmarkManager(this);
            this.bookmarkManager.load();

            getLogger().info("正在加载物品组...");
            GroupSetup.setup();
            if (survivalOverride) {
                JustEnoughGuide.runLaterAsync(CustomGroupConfigurations::load, 1L);
            }
            getLogger().info("物品组加载完毕！");

            if (getConfigManager().isBeginnerOption()) {
                getLogger().info("正在加载指南选项...");
                JEGGuideSettings.patchSlimefun();
                JEGGuideSettings.addOption(KeybindsSettingsGuideOption.instance());
                JEGGuideSettings.addOption(BeginnersGuideOption.instance());
                JEGGuideSettings.addOption(CerPatchGuideOption.instance());
                JEGGuideSettings.addOption(ShareInGuideOption.instance());
                JEGGuideSettings.addOption(ShareOutGuideOption.instance());
                JEGGuideSettings.addOption(RecursiveRecipeFillingGuideOption.instance());
                JEGGuideSettings.addOption(NoticeMissingMaterialGuideOption.instance());
                getLogger().info("指南选项加载完毕！");
            }

            if (getConfigManager().isCerPatch()) {
                CERCalculator.load();
            }
        }

        ItemsSetup.setup(this);

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

        SearchGroup.init();
        GroupResorter.load();

        SpecialMenuProvider.loadConfiguration();
        ThirdPartyWarnings.check();

        getLogger().info("成功启用此附属");
    }

    public String getVersion() {
        return getDescription().getVersion();
    }    /**
     * Cleans up resources and shuts down the plugin.
     */
    @Override
    public void onDisable() {
        CustomGroupConfigurations.unload();
        GroupResorter.rollback();

        getIntegrationManager().shutdownIntegrations();

        GroupSetup.shutdown();
        RecipeCompleteProvider.shutdown();
        GuideUtil.shutdown();

        /**
         * Unregister all {@link SlimefunItem}
         *
         * @see VanillaItemsGroup
         * @see ItemsSetup#RECIPE_COMPLETE_GUIDE
         */
        SlimefunRegistryUtil.unregisterItems(JustEnoughGuide.getInstance());

        try {
            List<SlimefunGuideOption<?>> l = JEGGuideSettings.getOptions();
            List<SlimefunGuideOption<?>> copy = new ArrayList<>(l);
            for (SlimefunGuideOption<?> option : copy) {
                if (option.getAddon() instanceof JustEnoughGuide) {
                    l.remove(option);
                }
            }
            JEGGuideSettings.unpatchSlimefun();
            FinalTECHValueDisplayOption.unboot();
        } catch (Exception ignored) {
        }

        try {
            Map<SlimefunGuideMode, SlimefunGuideImplementation> newGuides = new EnumMap<>(SlimefunGuideMode.class);
            newGuides.put(SlimefunGuideMode.SURVIVAL_MODE, new SurvivalSlimefunGuide());
            newGuides.put(SlimefunGuideMode.CHEAT_MODE, new CheatSheetSlimefunGuide());
            ReflectionUtil.setValue(Slimefun.getRegistry(), "guides", newGuides);
        } catch (Exception e) {
            Debug.trace(e);
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
        this.configManager = null;

        // Other fields
        this.minecraftVersion = null;
        this.javaVersion = 0;

        // Clear instance
        instance = null;
        getLogger().info("成功禁用此附属");
    }

    /**
     * Checks if debugging is enabled.
     *
     * @return true if debugging is enabled, false otherwise
     */
    public boolean isDebug() {
        return getConfigManager().isDebug();
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
            getLogger().warning("无法获取到 Minecraft 版本!");
            return false;
        }

        if (minecraftVersion == MinecraftVersion.UNKNOWN) {
            getLogger().warning("无法识别当前的 Minecraft 版本! (" + javaVersion + ")");
        } else if (!minecraftVersion.isAtLeast(RECOMMENDED_MC_VERSION)) {
            getLogger()
                    .warning("当前 Minecraft 版本过低(" + minecraftVersion.humanize() + "), 请使用 Minecraft "
                                     + RECOMMENDED_MC_VERSION.humanize() + " 或以上版本!");
        }

        if (javaVersion < RECOMMENDED_JAVA_VERSION) {
            getLogger().warning("Java 版本过低，请使用 Java " + RECOMMENDED_JAVA_VERSION + " 或以上版本!");
        }

        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("GuizhanLibPlugin")) {
            getLogger().log(Level.SEVERE, "本插件需要 鬼斩前置库插件(GuizhanLibPlugin) 才能运行!");
            getLogger().log(Level.SEVERE, "从此处下载: https://50l.cc/gzlib");
            getLogger().log(Level.SEVERE, "当出现该报错时, 作者对一切后续的报错不负责");
        }

        return true;
    }



    /**
     * Attempts to update the plugin if auto-update is enabled.
     */
    public void tryUpdate() {
        try {
            if (configManager.isAutoUpdate() && getDescription().getVersion().startsWith("Build")) {
                GuizhanUpdater.start(this, getFile(), username, repo, branch);
            }
        } catch (NoClassDefFoundError | NullPointerException | UnsupportedClassVersionError e) {
            getLogger().info("自动更新失败: " + e.getMessage());
            Debug.trace(e);
        }
    }
}
