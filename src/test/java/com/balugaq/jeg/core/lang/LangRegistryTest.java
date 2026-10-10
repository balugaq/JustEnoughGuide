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

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link LangRegistry} 的单元测试：键解析、占位符格式化、图标规格、缺键兜底。
 * 只依赖 Bukkit 的纯 Java 部分（YamlConfiguration / Material 枚举），无服务器可跑。
 *
 * @author balugaq
 */
@DisplayName("LangRegistry：键解析与格式化")
class LangRegistryTest {

    @TempDir
    Path tempDir;

    private final List<String> warnings = new ArrayList<>();
    private LangRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new LangRegistry(new YamlConfiguration(), new YamlConfiguration());
        registry.onWarn(warnings::add);
    }

    private static YamlConfiguration configOf(String yaml) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(yaml);
        } catch (InvalidConfigurationException e) {
            throw new IllegalStateException("测试 yml 解析失败: " + e.getMessage(), e);
        }
        return config;
    }

    // ---- 字符串键 ----

    @Test
    @DisplayName("字符串键：直接解析，无参数时原样返回")
    void resolvesStringWithoutArgs() throws IOException {
        registry = new LangRegistry(configOf("greeting: \"你好 {0}\""), new YamlConfiguration());
        assertEquals("你好 {0}", registry.text("greeting"));
    }

    @Test
    @DisplayName("字符串键：占位符按参数顺序替换")
    void formatsArguments() throws IOException {
        registry = new LangRegistry(configOf("greeting: \"你好 {0}，今天是 {1}\""), new YamlConfiguration());
        assertEquals("你好 用户，今天是 周六", registry.text("greeting", "用户", "周六"));
    }

    @Test
    @DisplayName("占位符：参数不足时原样保留，方便排查")
    void keepsUnmatchedPlaceholder() {
        assertEquals("a{2}b", LangRegistry.format("a{2}b", "x", "y"));
        assertEquals("a{0}b", LangRegistry.format("a{0}b", (Object) null));
        assertEquals("a{0}b", LangRegistry.format("a{0}b", (Object[]) null));
    }

    @Test
    @DisplayName("占位符：多位数下标可用")
    void supportsMultiDigitIndex() {
        assertEquals("第11个", LangRegistry.format("第{10}个", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "11"));
    }

    @Test
    @DisplayName("占位符：文本中的单引号不做转义（MessageFormat 陷阱回归测试）")
    void doesNotEscapeApostrophes() {
        assertEquals("it's ok", LangRegistry.format("it's {0}", "ok"));
        assertEquals("it's ok", LangRegistry.format("it's ok"));
    }

    // ---- 列表键 ----

    @Test
    @DisplayName("列表键：text 按行拼接")
    void textOnListKeyJoinsLines() throws IOException {
        registry = new LangRegistry(configOf("""
                help:
                  - "第一行 {0}"
                  - "第二行"
                """), new YamlConfiguration());
        assertEquals("第一行 A\n第二行", registry.text("help", "A"));
    }

    @Test
    @DisplayName("列表键：lines 逐行返回并格式化")
    void linesOnListKeyReturnsEachLine() throws IOException {
        registry = new LangRegistry(configOf("""
                help:
                  - "a {0}"
                  - "b"
                """), new YamlConfiguration());
        assertEquals(List.of("a X", "b"), registry.lines("help", "X"));
    }

    @Test
    @DisplayName("字符串键：lines 包装成单元素列表")
    void linesOnStringKeyWrapsSingleton() throws IOException {
        registry = new LangRegistry(configOf("msg: \"单行 {0}\""), new YamlConfiguration());
        assertEquals(List.of("单行 X"), registry.lines("msg", "X"));
    }

    // ---- 优先级与缺键 ----

    @Test
    @DisplayName("优先级：用户文件覆盖内置兜底")
    void userOverrideWinsOverDefaults() throws IOException {
        registry = new LangRegistry(
                configOf("key: \"用户版\""),
                configOf("key: \"内置版\"\nonly-default: \"兜底\""));
        assertEquals("用户版", registry.text("key"));
        assertEquals("兜底", registry.text("only-default"));
    }

    @Test
    @DisplayName("缺键：返回键本身，且同一条键只告警一次")
    void missingKeyFallsBackToKeyAndWarnsOnce() {
        String result = registry.text("no.such.key");
        assertEquals("no.such.key", result);
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("no.such.key"));

        // 同一条键再取一次，不再告警
        registry.text("no.such.key");
        assertEquals(1, warnings.size());
    }

    @Test
    @DisplayName("缺键：lines 返回单元素列表兜底")
    void missingKeyLinesFallback() {
        assertEquals(List.of("no.such.key"), registry.lines("no.such.key"));
    }

    // ---- 图标 ----

    @Test
    @DisplayName("图标：|- 块标量 lore 按行拆开，material 生效")
    void iconParsesBlockScalarLore() throws IOException {
        registry = new LangRegistry(configOf("""
                icons:
                  search:
                    material: BOOK
                    name: "&b搜索"
                    lore: |-
                      第一行
                      第二行
                """), new YamlConfiguration());

        LangRegistry.IconSpec icon = registry.iconSpec("search");
        assertNotNull(icon);
        assertEquals("&b搜索", icon.name());
        assertEquals(List.of("第一行", "第二行"), icon.lore());
        assertEquals(Material.BOOK, icon.material());
    }

    @Test
    @DisplayName("图标：lore 也可以写成列表")
    void iconParsesListLore() throws IOException {
        registry = new LangRegistry(configOf("""
                icons:
                  bookmark:
                    name: "&e书签"
                    lore:
                      - "第一行"
                      - "第二行"
                """), new YamlConfiguration());

        LangRegistry.IconSpec icon = registry.iconSpec("bookmark");
        assertNotNull(icon);
        assertEquals(List.of("第一行", "第二行"), icon.lore());
        assertEquals(Material.PAPER, icon.material()); // 未写 material 时默认 PAPER
    }

    @Test
    @DisplayName("图标：material 无效时回退 PAPER 并告警")
    void iconInvalidMaterialFallsBackAndWarns() throws IOException {
        registry = new LangRegistry(configOf("""
                icons:
                  bad:
                    material: NOT_A_MATERIAL
                    name: "x"
                """), new YamlConfiguration());
        registry.onWarn(warnings::add);

        LangRegistry.IconSpec icon = registry.iconSpec("bad");
        assertNotNull(icon);
        assertEquals(Material.PAPER, icon.material());
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("icons.bad.material"));
    }

    @Test
    @DisplayName("图标：键缺失返回 null 并告警一次")
    void iconMissingKeyReturnsNullAndWarnsOnce() {
        assertNull(registry.iconSpec("ghost"));
        assertEquals(1, warnings.size());
        assertNull(registry.iconSpec("ghost"));
        assertEquals(1, warnings.size());
    }

    // ---- 资源一致性（CI 级校验：所有内置语言文件必须能解析、结构合法）----

    @Test
    @DisplayName("resources/lang/ 下所有语言文件必须是合法 yml 且 icons 结构完整")
    void allBundledLangFilesAreWellFormed() throws IOException {
        Path langDir = Path.of("src/main/resources/lang");
        assertTrue(Files.isDirectory(langDir), "缺少语言资源目录: " + langDir.toAbsolutePath());

        try (Stream<Path> files = Files.list(langDir)) {
            List<Path> ymlFiles = files.filter(p -> p.getFileName().toString().endsWith(".yml")).toList();
            assertTrue(!ymlFiles.isEmpty(), "语言资源目录下没有任何 .yml 文件");

            for (Path file : ymlFiles) {
                YamlConfiguration config = new YamlConfiguration();
                // 解析失败会在这里以测试失败的形式暴露
                assertDoesNotThrow(() -> config.loadFromString(Files.readString(file, StandardCharsets.UTF_8)),
                        file.getFileName() + " 不是合法的 yml");

                if (config.isConfigurationSection("icons")) {
                    for (String key : config.getConfigurationSection("icons").getKeys(false)) {
                        assertTrue(config.contains("icons." + key + ".name"),
                                file.getFileName() + " 的图标 " + key + " 缺少 name 节点");
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("config.yml 的 language 键必须指向一个存在的内置语言文件")
    void configLanguagePointsToExistingBundledLangFile() throws IOException {
        YamlConfiguration config = new YamlConfiguration();
        assertDoesNotThrow(() -> config.loadFromString(Files.readString(Path.of("src/main/resources/config.yml"), StandardCharsets.UTF_8)),
                "config.yml 不是合法的 yml");

        String language = config.getString("language");
        assertNotNull(language, "config.yml 缺少 language 键");
        assertTrue(Files.isRegularFile(Path.of("src/main/resources/lang", language + ".yml")),
                "config.yml 的 language=" + language + " 没有对应的内置语言文件");
    }

    // ---- section：批量数据表 ----

    @Test
    @DisplayName("section：读取整节键值对，用户文件覆盖内置兜底")
    void sectionMergesUserOverDefaults() {
        LangRegistry merged = new LangRegistry(
                configOf("addon-names:\n  Slimefun: 自定义粘液\n"),
                configOf("addon-names:\n  Slimefun: 粘液科技\n  Networks: 网络\n"));
        Map<String, String> result = merged.section("addon-names");
        assertEquals(2, result.size());
        assertEquals("自定义粘液", result.get("Slimefun"));
        assertEquals("网络", result.get("Networks"));
    }

    @Test
    @DisplayName("section：节不存在返回空表")
    void sectionMissingReturnsEmpty() {
        assertTrue(registry.section("ghost").isEmpty());
    }

    @Test
    @DisplayName("内置语言文件必须包含 addon-names 节且含 Slimefun 条目")
    void bundledLangFilesContainAddonNames() throws IOException {
        Path langDir = Path.of("src/main/resources/lang");
        try (Stream<Path> files = Files.list(langDir)) {
                for (Path file : files.filter(p -> p.getFileName().toString().endsWith(".yml")).toList()) {
                    YamlConfiguration config = new YamlConfiguration();
                    assertDoesNotThrow(() -> config.loadFromString(Files.readString(file, StandardCharsets.UTF_8)),
                            file.getFileName() + " 不是合法的 yml");
                    assertTrue(config.isConfigurationSection("addon-names"),
                            file.getFileName() + " 缺少 addon-names 节");
                    assertTrue(config.contains("addon-names.Slimefun"),
                            file.getFileName() + " 的 addon-names 缺少 Slimefun 条目");
                }
        }
    }

    // ---- models：静态图标文案 ----

    @Test
    @DisplayName("内置语言文件必须包含 models 节，且 {models.xxx} 行内引用指向存在的键")
    void bundledLangModelsSectionIsConsistent() throws IOException {
        Path langDir = Path.of("src/main/resources/lang");
        try (Stream<Path> files = Files.list(langDir)) {
            for (Path file : files.filter(p -> p.getFileName().toString().endsWith(".yml")).toList()) {
                YamlConfiguration config = new YamlConfiguration();
                assertDoesNotThrow(() -> config.loadFromString(Files.readString(file, StandardCharsets.UTF_8)),
                        file.getFileName() + " 不是合法的 yml");
                if (!config.isConfigurationSection("models")) {
                    continue;
                }

                // Models.java 引用的键，缺一不可
                List<String> required = List.of(
                        "models.book-mechanism-1", "models.book-mechanism-2", "models.book-mechanism-3",
                        "models.gui-mechanism-1", "models.gui-mechanism-2",
                        "models.rts-item.name", "models.special-menu-item.name",
                        "models.input-text-icon.name", "models.item-mark-background.name",
                        "models.slimefun-recipe-edit.name",
                        "models.jeg-guide-group.name", "models.hidden-items-group.name",
                        "models.nexcavate-items-group.name", "models.vanilla-items-group.name",
                        "models.recipe-completable-group.name", "models.jeg-items-group.name",
                        "models.replacement-cards-group.name", "models.banned-items-group.name",
                        "models.multi-block-builder-items-group.name",
                        "models.recipe-complete-guide.name", "models.recipe-complete-guide.lore",
                        "models.usage-info.name", "models.usage-info.lore",
                        "models.mechanism.name", "models.mechanism.lore",
                        "models.supported-addons-info.name", "models.supported-addons-info.lore",
                        "models.jeg-recipe-complete-button.name", "models.jeg-recipe-complete-button.lore",
                        "models.custom-lag-block.name", "models.custom-lag-block.lore");
                for (String key : required) {
                    assertTrue(config.contains(key),
                            file.getFileName() + " 缺少 models 键: " + key);
                }

                // {models.xxx} 行内引用指向的键必须存在
                for (String key : config.getConfigurationSection("models").getKeys(true)) {
                    if (config.isConfigurationSection("models." + key)) {
                        continue;
                    }
                    Object value = config.get("models." + key);
                    if (!(value instanceof String text) || !text.matches("\\{[a-z0-9.\\-]+}")) {
                        continue;
                    }
                    assertTrue(config.contains(text.substring(1, text.length() - 1)),
                            file.getFileName() + " 的 " + key + " 引用了不存在的键: " + text);
                }
            }
        }
    }
}
