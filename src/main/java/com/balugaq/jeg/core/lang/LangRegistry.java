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

import com.balugaq.jeg.utils.compatibility.Converter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 多语言注册表：键解析、占位符格式化、图标规格解析。
 * <p>
 * 设计要点（与 NetworksExpansion 的 {@code Language} 范式的差异）：
 * <ul>
 *   <li><b>用户文件只读</b>：{@code user}（磁盘上的用户文件）与 {@code defaults}（插件内置资源）
 *       分开保存，读取时先查 user、缺键再查 defaults，<b>绝不把合并结果写回磁盘</b>——
 *       NE 每次加载都 {@code save()}，会毁掉用户文件里的注释与排版。</li>
 *   <li><b>无 MessageFormat</b>：占位符用自实现的 {@code {0}}…{@code {n}} 简单替换——
 *       {@code MessageFormat} 会把文本里的单引号当转义符（必须写成 {@code ''}），运营改文案必踩坑。</li>
 *   <li><b>可单测</b>：本类只依赖 Bukkit 的纯 Java 部分（{@code YamlConfiguration} 与
 *       {@code Material} 枚举），不需要服务器实例即可在 JUnit 里跑。</li>
 * </ul>
 * 缺键策略：同一条键只告警一次（避免刷屏），{@link #text} 返回键本身、{@link #lines} 返回单元素列表，
 * 保证调用方永远不会拿到 null。
 *
 * @author balugaq
 */
@NullMarked
public final class LangRegistry {
    /**
     * 占位符：{@code {0}}、{@code {1}}……
     */
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{(\\d+)}");

    private final FileConfiguration user;
    private final FileConfiguration defaults;
    private final Set<String> warned = ConcurrentHashMap.newKeySet();

    /**
     * 告警出口：生产环境由 {@link Lang} 接到 {@code Debug.warn}，测试里可换成收集器断言。
     */
    private Consumer<String> warnSink = message -> {
    };

    public LangRegistry(FileConfiguration user, FileConfiguration defaults) {
        this.user = user;
        this.defaults = defaults;
    }

    /**
     * 注册告警出口（重复调用以最后一次为准）。
     *
     * @param sink 告警消费者
     */
    void onWarn(Consumer<String> sink) {
        this.warnSink = sink;
    }

    /**
     * @return 已告警过的键集合（测试断言用）
     */
    Set<String> warnedKeys() {
        return Set.copyOf(warned);
    }

    /**
     * 原始取值：先查用户文件，缺键再查内置兜底。
     *
     * @param key 语言键
     * @return 原始值；两个来源都没有时返回 null
     */
    public @Nullable Object raw(String key) {
        Object value = user.get(key);
        if (value == null) {
            value = defaults.get(key);
        }
        return value;
    }

    /**
     * 取一条翻译文本（含占位符格式化）。
     * <p>
     * 若该键实际是列表，会把各行用换行符拼接后统一格式化；
     * 若该键是配置节点（既非字符串也非列表），按缺键处理。
     *
     * @param key  语言键
     * @param args 占位符参数
     * @return 翻译后的文本；缺键时返回键本身
     */
    public String text(String key, @Nullable Object... args) {
        Object value = raw(key);
        if (value instanceof List<?> list) {
            List<String> lines = new ArrayList<>(list.size());
            for (Object line : list) {
                lines.add(format(String.valueOf(line), args));
            }
            return String.join("\n", lines);
        }
        if (!(value instanceof String string)) {
            warnOnce(key);
            return key;
        }
        return format(string, args);
    }

    /**
     * 取一个消息键的全部行（用于 {@code sendMessage}）。
     * <p>
     * 键对应字符串时返回单元素列表，对应列表时逐行返回——两种 yml 写法等价。
     *
     * @param key  语言键
     * @param args 占位符参数
     * @return 各行文本；缺键时返回只含键本身的列表
     */
    public List<String> lines(String key, @Nullable Object... args) {
        Object value = raw(key);
        if (value instanceof List<?> list) {
            List<String> lines = new ArrayList<>(list.size());
            for (Object line : list) {
                lines.add(format(String.valueOf(line), args));
            }
            return lines;
        }
        if (value instanceof String string) {
            return List.of(format(string, args));
        }
        warnOnce(key);
        return List.of(key);
    }

    /**
     * 读取一个配置节下的全部键值对（字符串值），用于批量数据表（如 {@code addon-names}）。
     * <p>
     * 用户文件优先：同名键以用户文件的值为准，内置兜底补齐缺失键。
     * 非字符串值的键会被跳过。
     *
     * @param path 配置节路径（如 {@code addon-names}）
     * @return 键值对（保持 yml 顺序）；节不存在时返回空表
     */
    public Map<String, String> section(String path) {
        Map<String, String> result = new LinkedHashMap<>();
        collectSection(defaults, path, result);
        collectSection(user, path, result);
        return result;
    }

    private static void collectSection(FileConfiguration config, String path, Map<String, String> out) {
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            String value = section.getString(key);
            if (value != null) {
                out.put(key, value);
            }
        }
    }

    /**
     * 解析一个图标（{@code icons.<key>} 节点）并构建 ItemStack。
     * <p>
     * yml 形如：
     * <pre>
     * icons:
     *   example:
     *     material: BOOK   # 可选，默认 PAPER
     *     name: "&amp;b示例"
     *     lore: |-
     *       第一行
     *       第二行
     * </pre>
     * {@code lore} 支持 {@code |-} 多行字符串与 {@code -} 列表两种写法；
     * 构建走 {@link Converter#getItem(Material, String, List)}，name 与 lore 自动上色。
     *
     * @param key 图标键（不含 {@code icons.} 前缀）
     * @return 图标物品；键缺失、结构不对或 Lang 未加载时返回以 {@code &c键名} 命名的 PAPER 兜底（并告警一次）
     */
    public ItemStack icon(String key) {
        IconSpec spec = iconSpec(key);
        if (spec == null) {
            return Converter.getItem(Material.PAPER, "&c" + key, List.of());
        }
        return Converter.getItem(spec.material(), spec.name(), spec.lore());
    }

    /**
     * 解析图标规格（不做 ItemStack 构建，供 {@link #icon} 与单测使用）。
     *
     * @param key 图标键（不含 {@code icons.} 前缀）
     * @return 图标规格；键缺失或结构不对时返回 null（并告警一次）
     */
    @Nullable IconSpec iconSpec(String key) {
        String path = "icons." + key;
        Object value = raw(path);
        if (!(value instanceof ConfigurationSection section)) {
            warnOnce(path);
            return null;
        }

        String name = section.getString("name", key);
        return new IconSpec(name, loreOf(section), materialOf(section, path));
    }
    /**
     * 把模板里的 {@code {n}} 占位符替换成对应参数。
     * <p>
     * 越界或对应的参数为 null 时，占位符原样保留（方便排查参数个数错误）；
     * 文本中的单引号等字符不做任何转义处理。
     *
     * @param template 模板文本
     * @param args     参数
     * @return 格式化后的文本
     */
    public static String format(String template, @Nullable Object... args) {
        if (args == null || args.length == 0) {
            return template;
        }

        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            int index = Integer.parseInt(matcher.group(1));
            String replacement = matcher.group();
            if (index >= 0 && index < args.length && args[index] != null) {
                replacement = String.valueOf(args[index]);
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private void warnOnce(String key) {
        if (warned.add(key)) {
            warnSink.accept("语言键缺失: " + key);
        }
    }

    private static List<String> loreOf(ConfigurationSection section) {
        if (section.isList("lore")) {
            return List.copyOf(section.getStringList("lore"));
        }

        // |- 块标量是单个多行字符串，按行拆开
        String block = section.getString("lore", "");
        if (block.isEmpty()) {
            return List.of();
        }
        return List.of(block.split("\n", -1));
    }

    private Material materialOf(ConfigurationSection section, String path) {
        String name = section.getString("material", "");
        if (name.isEmpty()) {
            return Material.PAPER;
        }

        Material material = Material.matchMaterial(name);
        if (material == null) {
            warnOnce(path + ".material");
            return Material.PAPER;
        }
        return material;
    }

    /**
     * 图标规格：物品名、各行 lore、材质。
     *
     * @param name     物品名（未上色）
     * @param lore     各行 lore（未上色）
     * @param material 材质
     */
    public record IconSpec(String name, List<String> lore, Material material) {
    }
}
