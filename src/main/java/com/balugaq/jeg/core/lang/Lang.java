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
package com.balugaq.jeg.core.lang;

import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 多语言静态门面。用法：
 * <pre>
 * import static com.balugaq.jeg.core.lang.Lang.*;
 *
 * sendMessage(sender, "commands.reload-done");
 * sendMessage(sender, "timings.collecting", verbose ? "（详细模式）" : "");
 * String text = t("prefix") + t("something", arg);
 * ItemStack icon = getIcon("search");
 * </pre>
 * <p>
 * 语言文件位于 {@code resources/lang/<语言>.yml}，由 {@code config.yml} 的 {@code language} 键选择；
 * 插件首次启动时复制到 {@code plugins/JustEnoughGuide/lang/}，之后以磁盘文件为准、
 * 内置版本仅作缺键兜底且<b>不会被改写回磁盘</b>。
 * <p>
 * 键对应的值可以是字符串或字符串列表（{@code sendMessage} 逐行发送）；占位符为
 * {@code {0}}…{@code {n}}。运行时只有一种语言（全局），不需要考虑按玩家切换。
 *
 * @author balugaq
 */
@NullMarked
public final class Lang {
    private static final String DEFAULT_LANGUAGE = "zh_CN";

    private static volatile @Nullable JavaPlugin plugin;
    private static volatile @Nullable LangRegistry registry;

    private Lang() {
    }

    /**
     * 加载语言文件。须在 {@code ConfigManager.load()} 之后调用（要读 {@code language} 键）。
     *
     * @param plugin 插件实例
     */
    public static void load(JavaPlugin plugin) {
        Lang.plugin = plugin;

        String language = plugin.getConfig().getString("language", DEFAULT_LANGUAGE);
        File folder = new File(plugin.getDataFolder(), "lang");
        if (!folder.exists() && !folder.mkdirs()) {
            Debug.warn("Failed to create language folder " + folder.getPath() + ", falling back to the built-in language file");
        }

        // 优先用用户文件；配置的语言不存在时回退到默认语言
        File file = new File(folder, language + ".yml");
        if (!file.exists() && plugin.getResource("lang/" + language + ".yml") == null) {
            Debug.warn("Language file lang/" + language + ".yml does not exist (not in built-in resources either), falling back to " + DEFAULT_LANGUAGE);
            language = DEFAULT_LANGUAGE;
            file = new File(folder, language + ".yml");
        }

        if (!file.exists()) {
            // 首次启动：把内置语言文件原样复制到磁盘（保留注释与排版）
            String path = "lang/" + language + ".yml";
            if (plugin.getResource(path) != null) {
                plugin.saveResource(path, false);
            } else {
                Debug.severe("Built-in language file missing: " + path + ", all language keys will fail to resolve");
            }
        }

        registry = new LangRegistry(readUser(plugin, file), readDefaults(plugin, language));
        registry.onWarn(message -> Debug.warn(message));
        plugin.getLogger().info("Loaded language file: lang/" + language + ".yml");
    }

    /**
     * 卸载语言注册表（{@code /jeg reload} 与 {@code onDisable} 共用）。
     */
    public static void unload() {
        registry = null;
        plugin = null;
    }

    /**
     * @return 语言系统是否已加载
     */
    public static boolean isLoaded() {
        return registry != null;
    }

    /**
     * 向接收者发送一条消息：键对应字符串时发一行，对应列表时逐行发送。
     *
     * @param sender 接收者
     * @param key    语言键
     */
    public static void sendMessage(CommandSender sender, String key) {
        sendMessage(sender, key, (Object[]) null);
    }

    /**
     * 向接收者发送一条消息（带占位符参数）。
     *
     * @param sender 接收者
     * @param key    语言键
     * @param args   占位符参数
     */
    public static void sendMessage(CommandSender sender, String key, @Nullable Object... args) {
        LangRegistry current = registry;
        if (current == null) {
            Debug.warn("Lang is not loaded, cannot resolve language key " + key + "");
            sender.sendMessage(key);
            return;
        }

        for (String line : current.lines(key, args)) {
            sender.sendMessage(ChatColors.color(line));
        }
    }

    /**
     * 取翻译后的文本（含占位符格式化与 {@code &} 色码上色），用于更复杂的组合信息。
     *
     * @param key  语言键
     * @param args 占位符参数
     * @return 翻译后的文本；Lang 未加载或键缺失时返回键本身
     */
    public static String t(String key, @Nullable Object... args) {
        LangRegistry current = registry;
        if (current == null) {
            Debug.warn("Lang is not loaded, cannot resolve language key " + key + "");
            return key;
        }
        return ChatColors.color(current.text(key, args));
    }

    /**
     * 取翻译后的多行文本（含占位符格式化与 {@code &} 色码上色），用于 lore 等。
     *
     * @param key  语言键（yml 中为字符串列表）
     * @param args 占位符参数
     * @return 翻译后的行列表；Lang 未加载或键缺失时返回单元素列表（键本身）
     */
    public static List<String> lines(String key, @Nullable Object... args) {
        LangRegistry current = registry;
        if (current == null) {
            Debug.warn("Lang is not loaded, cannot resolve language lines " + key + "");
            return List.of(key);
        }
        return current.lines(key, args).stream().map(ChatColors::color).toList();
    }

    /**
     * 读取一个配置节下的全部键值对（见 {@link LangRegistry#section}），用于批量数据表。
     *
     * @param path 配置节路径
     * @return 键值对；Lang 未加载时返回空表
     */
    public static Map<String, String> section(String path) {
        LangRegistry current = registry;
        if (current == null) {
            Debug.warn("Lang is not loaded, cannot resolve section " + path);
            return Map.of();
        }
        return current.section(path);
    }

    /**
     * 获取 {@code icons.<key>} 定义的图标（构建走 {@code Converter.getItem}，自动上色）。
     *
     * @param key 图标键（不含 {@code icons.} 前缀）
     * @return 图标；键缺失或 Lang 未加载时返回一张以 {@code &c键名} 命名的 PAPER 兜底物品
     */
    public static ItemStack getIcon(String key) {
        LangRegistry current = registry;
        if (current == null) {
            Debug.warn("Lang is not loaded, cannot resolve icon " + key + "");
            return fallbackIcon(key);
        }
        return current.icon(key);
    }

    private static ItemStack fallbackIcon(String key) {
        return Converter.getItem(Material.PAPER, "&c" + key, List.of());
    }

    /**
     * 读取用户文件（磁盘）。文件可能不存在（内置资源也缺失时），返回空配置。
     */
    private static FileConfiguration readUser(JavaPlugin plugin, File file) {
        if (!file.exists()) {
            return new YamlConfiguration();
        }

        try {
            return YamlConfiguration.loadConfiguration(
                    new InputStreamReader(new java.io.FileInputStream(file), StandardCharsets.UTF_8));
        } catch (Exception e) {
            Debug.trace(e);
            return new YamlConfiguration();
        }
    }

    /**
     * 读取内置兜底配置（jar 内资源，绝不写回磁盘）。
     */
    private static FileConfiguration readDefaults(JavaPlugin plugin, String language) {
        YamlConfiguration defaults = new YamlConfiguration();
        try (InputStream in = plugin.getResource("lang/" + language + ".yml")) {
            if (in != null) {
                defaults.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            Debug.trace(e);
        }
        return defaults;
    }
}
