# AGENTS.md — JustEnoughGuide 操作宪章

> 本文件是 AI 编程助手与本仓库开发者的**操作契约**。所有改动（代码、构建、文档）均须遵守本文约定；
> 当本文与代码冲突时，按末尾《文档更新契约》处理。

## TL;DR（太长了不看）

1. 本仓库是 **Minecraft Paper 服务器 + Slimefun4 附属插件**（Java 21 / Gradle Kotlin DSL），所有改动必须通过 `gradle clean build` 验收后才能提交，禁止提交未编译验证的代码。
2. 玩家可见文案默认走 **Slimefun 本地化 + 内联中文**，附属名翻译依赖软依赖 **SlimefunTranslation**（`SlimefunTranslationAPI.translateItem(...)`）；日志一律走 `getLogger()` 与 `com.balugaq.jeg.utils.Debug` 工具类；**禁止** `System.out.println`、**禁止** `git push -f`。
3. 本仓库即开发仓库，**没有上游**；直接在 `master` 分支开发，提交信息遵循 Conventional Commits（`feat:`/`fix:`/`docs:`/`refactor:`/`chore:`）。**提交与推送动作均由开发者本人手动执行，AI 不代为 `git commit`/`git push`。**

---

## 1. 项目身份与边界

| 维度      | 事实                                                                                                                                |
|---------|-----------------------------------------------------------------------------------------------------------------------------------|
| 定位      | Minecraft Paper 服务器插件，**Slimefun4 附属**，大幅增强原版粘液指南书（搜索 / 书签 / 界面 / 配方补全 / 按键绑定等）                                                   |
| 入口类     | `com.balugaq.jeg.implementation.JustEnoughGuide`（`extends JavaPlugin implements SlimefunAddon`）                                   |
| 版本目标    | Java 21 工具链、`api-version: 1.17`、最低支持 MC 1.16（推荐 1.21.10+）                                                                         |
| 依赖      | `depend: [Slimefun]`；`softdepend: [GuizhanLibPlugin(硬前置校验)、Logitech、PlaceholderAPI、SlimefunTranslation、EMCTech、SlimefunRecipe 等]` |
| 仓库策略    | 本地仓库即开发仓库，**没有上游**；**直接在 `master` 开发**，提交与推送均由开发者本人手动执行                                                                           |
| AI 职责范围 | 编写/修改功能代码、Bug 修复、构建/CI 维护、文档维护                                                                                                    |
| AI 不负责  | 执行 `git commit` / `git push`、发布版本（这些动作由开发者手动完成）                                                                                   |

### 1.1 代码包地图（改代码前先定位）

| 包                                     | 职责                                                                                                                                                                                                                                  |
|---------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `com.balugaq.jeg.implementation`      | 插件核心装配：`JustEnoughGuide`（主类 / 启动生命周期）、`GroupsSetup/ItemsSetup`（JEG 自己的物品组与指南选项注册）、`guide.SurvivalGuideImplementation` / `guide.CheatGuideImplementation`（**替换器**，用反射覆盖 Slimefun 的 guides 实现）                                        |
| `com.balugaq.jeg.core.managers`       | 管理器：`ConfigManager`（配置门面）、`CommandManager`、`ListenerManager`、`IntegrationManager`（软依赖插件是否启用/初始化）、`BookmarkManager`（书签）、`RTSBackpackManager`（实时搜索背包）                                                                                 |
| `com.balugaq.jeg.core.listeners`      | 事件监听：`RTSListener`、`RecipeCompletableListener`、`GroupTierEditorListener`（物品组排序）、`GuideListener`、`BundleListener`、`CerPatchListener` 等                                                                                               |
| `com.balugaq.jeg.core.integrations.*` | 各软依赖附属适配（`emctech`、`slimefuntranslation`、`networksexpansion`、`slimeaeplugin`…），含 `core.integrations.Integration` 接口                                                                                                                 |
| `com.balugaq.jeg.api`                 | 对外扩展点/数据模型：`groups.*`（`BaseGroup`、`SearchGroup`、`RTSSearchGroup`、`CustomGroup`）、`recipe_complete.*`、`editor.GroupResorter`（物品组排序）、`patchcuts.JEGGuideSettings`、`objects.*`（事件 / 枚举 / 数据）、`multiblock.MultiBlockBuilder`             |
| `com.balugaq.jeg.utils`               | 工具类：`GuideUtil`（指南渲染 / 按键打开）、`Debug`、`ReflectionUtil`（反射 patch Slimefun 内部仓库）、`ItemStackUtil`、`formatter.*`（`Formats`/`RecipeFormat`/`Format` 自定义布局）、`clickhandler.*`（`OnClick` / `OnDisplay` / `BaseAction` / `PermissibleAction`） |

---

## 2. 技术栈与项目结构（事实核对）

- **构建**：Gradle Kotlin DSL（`build.gradle.kts`）+ Shadow 插件；`build` 依赖 `shadowJar`（非 `defaultTasks`）；产物 `build/libs/JustEnoughGuide-<version>.jar`（fat jar，含 relocation：`net.Zrips.CMILib`→`com.balugaq.jeg.libraries.cmilib`、`com.tcoded.folialib`→`...folialib`、`net.byteflux.libby`→`...libby`、`org.bstats`→`...bstats`、`net.wesjd.anvilgui`→`...anvilgui` 等）。
- **依赖仓库**：仅使用 `build.gradle.kts` 中已有的公开远程仓库（Paper、jitpack、CodeMC、tcoded 等）。**无公司私服，不要新增私服配置。**
- **核心框架**：Paper API + Slimefun4（compileOnly）+ GuizhanLibPlugin（硬前置）+ FoliaLib、Libby、AnvilGUI、bStats、MorePersistentDataTypes（内嵌打包）；`SlimefunTranslation`/`EMCTech`/`NetworksExpansion`/`SlimefunRecipe`/`PlaceholderAPI` 等为 `compileOnly` 软依赖。
- **运行时动态加载库**：`JustEnoughGuide.loadLibraries()` 用 **Libby** 加载 houbb `pinyin` / `opencc4j` / `heaven` / `nlp-common`（拼音与简繁转换用）。
- **任务调度**：不要直接 `Bukkit.getScheduler()`，统一走 `JustEnoughGuide.getScheduler()`（FoliaLib `TaskScheduler`）静态方法：`runAsync` / `runLaterAsync` / `runLater` / `runTimer` / `runTimerAsync` / `postServerStartup`。
- **语言/文案**：**没有独立 `lang/` 目录**。玩家可见中文文案多为内联字符串 + `&` 色码 + `<Theme>`/`ChatColors` 着色；附属翻译依赖软依赖 SlimefunTranslation（`SlimefunTranslationAPI.translateItem` / `getItemName`）。**新增面向的可见文案时遵循第 3.2 节约束。**
- **日志**：`getLogger()`（info/warning/severe）+ `com.balugaq.jeg.utils.Debug`——`Debug.debug(...)` 仅在 `config.yml` 的 `debug` 开启时输出、`Debug.log/warn/severe(...)` 走控制台彩色输出、`Debug.trace/traceExactly(...)` 打异常横幅并**落盘到 `plugins/JustEnoughGuide/error-reports/`**、`Debug.dumpToFile(...)` 写错误快照。
- **启动顺序**（`JustEnoughGuide.onEnable()`，新增初始化逻辑须插入对应阶段，不要打乱顺序）：
  `instance = this` → `environmentCheck()`（前置/版本校验，失败即 disabled）→ `new FoliaLib` → `PlatformUtil.initialize()` → `scheduler = TaskScheduler.create()` → `loadLibraries()`（Libby）→ `saveDefaultConfig()` → `new ConfigManager().load()` → `Formats.load()` → `ListenerManager.load()` → `CommandManager.load()` →（反射）覆盖 Slimefun `guides` 为 `SurvivalGuideImplementation`/`CheatGuideImplementation` → `BookmarkManager.load()` → `GroupSetup.setup()` + 异步 `CustomGroupConfigurations.load()` →（可选 `cer-patch`）`CERCalculator.load()`/`ValueTable.load()` → `ItemsSetup.setup(this)` → `RTSBackpackManager.load()` → `setupServerUUID()` → `SearchGroup.tryInit()` → `GroupResorter.load()` → `SpecialMenuProvider.loadConfiguration()` → `ReplacementCardAdapter.load()` → `MultiBlockBuilder.load()` → `ThirdPartyWarnings.check()` → `IntegrationManager.scheduleRun(JEGGuideSettings::sortOptions)` → `IntegrationManager.load()` → `tryUpdate()` → `metrics = new JEGMetrics()`。
- **卸载顺序**（`unloadInternal()`，`/jeg reload` 与 `onDisable()` 共用）：`CustomGroupConfigurations.unload()` → `GroupResorter.rollback()`（还原 tier）→ `GroupSetup.shutdown()` → `RecipeCompleteProvider.shutdown()` → `GuideUtil.shutdown()` → `SlimefunRegistryUtil.unregisterItems(...)` → 卸载 `JEGGuideSettings` 的 GuideOption + `JEGGuideSettings.unpatchSlimefun()` → 反射还原 Slimefun 原版 guides → 依次 卸载各 Manager → 清空 `ReplacementCardAdapter`/`SearchGroup` 状态。**顺序不可随意调整（尤其 rollback / unregister / unpatch）。**

---

## 3. 决策树：当遇到 A 时，执行 B

### 3.1 拼音搜索

- **当需要把中文物品名转成拼音键用于搜索时**：参照 `api/SearchGroupLoader.java`——`PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, "")` 生成首字母拼音缓存键；受 `ConfigManager.isPinyinSearch()`（`improvements.pinyin-search`）控制。
- **当需要扩展/调整搜索索引时**：在 `SearchGroupLoader` 构建索引处追加或修改（KEYWORD_CACHE / DISPLAY_NAMES 等本质上是"物品姓名 → 物品列表"的搜索索引，非业务缓存），**不要**绕过 `SearchGroup` 的索引读取路径直接全量遍历。

### 3.2 实时搜索（RTS）

- **当需要处理实时搜索交互时**：统一走 `api/groups/RTSSearchGroup`（缓存玩家状态、搜索词、页号）+ `core/listeners/RTSListener`（铁砧 Anvil 界面、搜索词变更、翻页、强制退出）+ `core/managers/RTSBackpackManager`（玩家背包存 JEGRTS 临时物品）。功能开关 `improvements.rts-search`。
- **当往玩家背包注入 RTS 占位物品 / 退出 RTS 时**：调用 `RTSListener.tryQuitRTS(player)` 清理状态与背包，**禁止**私自操作 `RTS_PLAYERS`/`RTS_SEARCH_TERMS` 以外的残留字段。
- **当识别 RTS 假物品时**：用 `RTSListener` 内提供的判假逻辑（参照其 use / click 处理），防止玩家把占位物带出 RTS 界面。

### 3.3 搜索黑名单 / 禁用与相似字

- **当需要屏蔽搜索结果时**：读取 `ConfigManager.getBlacklist()` / `getBanList()`。区别：`blacklist`（非限定词搜索时不显示名字含这些词的物品）、`banlist`（任何形式都禁止搜出）。判定入口 `SearchGroup.inBlacklist(...)`。
- **当需要相似字符互通搜索时**：配置项 `shared-chars`（每组同义字符），加载于 `ConfigManager`；搜索命中与替换逻辑参照 `SearchGroup` / `StringUtil`。
- **当新增默认屏蔽词或相似字组时**：直接改 `src/main/resources/config.yml` 的 `blacklist`/`banlist`/`shared-chars` 段（对应 `ConfigManager` 已读，缺键会自动写回默认）。

### 3.4 附属名翻译

- **当需要翻译附属 / 物品名并已启用 SlimefunTranslation 时**：先 `IntegrationManager.isEnabledXxx()` 判启用，再 `SlimefunTranslationAPI.translateItem(user, itemStack)` / `SlimefunTranslationAPI.getItemName(user, slimefunItem)`（参照 `SlimefunTranslationIntegrationMain` / `ItemStackUtil`）；用 try/catch 包软依赖相关调用，**禁止**直接引用不在 `compileOnly` 的类。
- **当新增对某个附属的翻译/显示适配时**：在 `core.integrations.<addon>` 包新建实现 `Integration` 接口的类，并在 `IntegrationManager.load()` 的 `addIntegration(isEnabledX, XxxIntegrationMain::new)` 处挂上；不要散落其它地方判启用。

### 3.5 界面优化（自定义布局）

- **当需要调整指南界面元素位置时**：改 `config.yml` 的 `custom-format` 段（主界面 / 组界面 / 子组 / 配方 / 按键等），映射字符见注释（`B` 背景、`b` 返回、`T` 设置、`R` 实时搜索、`C` 收藏、`S` 搜索、`G` 物品组、`P/N` 翻页等）；代码侧用 `utils/formatter/Formats`（`MainFormat`/`RecipeFormat` 等，`Formats.load()` 时从 config 读取）。
- **当改某界面渲染逻辑时**：入口在 `GuideUtil` + `implementation/groups/*ItemsGroup`（`JEGGuideGroup`、`KeybindsItemsGroup` 等）的 `generateMenu(...)`，通过 `Format.getChars(Formats.Char.XXX)` 取槽位；渲染成品用 `PatchScope.XXX.patch(player, item)`修饰。
- **当需要给界面加图标/样式时**：用 `utils.Models`、`utils/clickhandler/OnDisplay`、`api/interfaces.CustomIconDisplay` 等既定机制；自定义图标可写到 `resources/custom-icons.yml` / `custom-groups.yml`。
- **当需要替换 Slimefun 原版指南时**：在 `onEnable()` 的 `ReflectionUtil.setValue(Slimefun.getRegistry(), "guides", newGuides)` 处维护 `SurvivalGuideImplementation` / `CheatGuideImplementation`；卸载时用 `unloadInternal()` 还原。

### 3.6 书签系统

- **当需要读写玩家收藏时**：经 `JustEnoughGuide.getBookmarkManager()` 的 `addBookmark(player, item|itemGroup)` / `removeBookmark` / `clearBookmarks` / `getBookmarkedItems`（返回 `List<Bookmark>`，`Bookmark` 见 `api/objects/collection/data/Bookmark.java` 的 `of(sfitem)/of(itemGroup)`）。存储载体是玩家背包的 "JEGBookmarkBackpack"（`PlayerBackpack`），书签物品通过持久化 data 标记识别（`markItemAsBookmarksItem` / `isBookmarksItem`）。
- **当处理玩家切换存档 / 书签物品失效时**：走 `api/interfaces/BookmarkRelocation` 与 `BookmarkManager` 的迁移逻辑，**禁止**手动改背包里书签物品键。

### 3.7 EMC 适配显示

- **当需要显示 EMC 值时**：先判 `IntegrationManager.isEnabledEMCTech()`（`EMCTech` 软依赖），再经 `core/integrations/emctech/EMCTechIntegrationMain` 与 `core/integrations/emctech/EMCValueDisplayGuideOption`；指南设置入口 `improvements.emc-display-option`（`ConfigManager.isEMCValueDisplay()`）。
- **当需要显示 FinalTECH 的数值时**：走 `core/integrations/finaltechs/*` 的数值显示选项（对物品显示"性价比/价值"等数值）——`FinalTechValueDisplayGuideOption`（finalTech v1，开关 `isFinalTechValueDisplay()`，键 `improvements.finaltech-emc-display-option`）与 `FinalTECHValueDisplayGuideOption`（finalTECHChanged v3 / finalTECH v2 共用，`core/integrations/finaltechs/finalTECHCommon`，开关 `isFinalTECHValueDisplay()`，键 `improvements.finalTECH-emc-display-option`）。后者带 `setBooted(true)` 幂等标志（见其 `*IntegrationMain` 与 `JustEnoughGuide.unloadInternal()` 里的 `setBooted(false)` 还原）。
- **当新增类似数值显示选项时**：新建 `EMCValueDisplayGuideOption` 同型的 `*ValueDisplayGuideOption`（`PrioritySlimefunGuideOption<Boolean>`），在对应 `*IntegrationMain` 里判启用后注册到 `SlimefunGuideSettings.addOption(...)` 并按 `Priorities` 排序；若涉及 `setBooted` 标志，卸载时必须对称还原。

### 3.8 指南按键绑定

- **当需要打开按键编辑界面时**：`GuideUtil.openKeybindsGui(player)`（列出按键）→ `GuideUtil.openKeybindGui(player, OnClick)`（编辑单按键）→ `GuideUtil.openActionSelectGui(...)`（选择动作）。界面实现见 `implementation/groups/KeybindsItemsGroup`、`SubKeybindsItemsGroup`、`ActionSelectGroup`。
- **当需要定义/触发按键动作时**：用 `utils/clickhandler/OnClick`（一组 `BaseAction`）+ `ActionKey` + `PermissibleAction`（按权限过滤）；点击事件走 `GuideEvents.*ButtonClickEvent` + `EventUtil.callEvent(...).ifSuccess(...)`。
- **当需要新增可绑定动作时**：在 clickhandler 动作体系里新增 `BaseAction` 子类并接入 `GuideUtil` 的动作选择/图标逻辑，**不要**绕开 OnClick 直接写按键调用。

### 3.9 超大配方显示

- **当需要展示超大配方 / 多方块结构配方时**：走 `utils/formatter/RecipeFormat` + `config.yml` 的 `custom-format.recipe` 布局；指南选项 `OpenBigRecipeMenuWhenPossibleGuideOption`（配方便于大菜单展示时打开）。
- **当新增支持某种配方的展示时**：确定来源归属（原版 / Slimefun / 其它附属），在 `RecipeFormat` / guide 渲染路径接入对应适配，参照 `core/integrations` 的挂接方式。

### 3.10 配方补全

- **当需要配方补全时**：框架在 `api/recipe_complete/*`（`RecipeCompleteProvider`、`RecipeCompletableRegistry`、`RecipeCompleteSession`），源头在 `source/base/Source|VanillaSource|SlimefunSource`，事件在 `api/objects/events/RecipeCompleteEvents`，监听在 `core/listeners/RecipeCompletableListener`，指南选项在 `implementation/option/Recipe*GuideOption`（补全深度、自动抓取、打开模式、缺料提示）。
- **当新增一个可配方补全的配方来源（某附属的机器）时**：新建一个 `Source` 实现并注册到 `RecipeCompleteProvider`（`getSources()`/`getSpecialRecipeHandlers()` 等），**不要**直接复制裸逻辑。
- **当给机器自动加"配方补全按钮"时**：受 `auto-add-recipe-complete-button` 及 `no-auto-add-recipe-complete-*` 黑名单配置控制，经 `RecipeCompleteProvider`（`JustEnoughGuide.getConfigManager().isAutoAddRecipeCompleteButton()`）统一处理。

### 3.11 自定义物品组排序

- **当需要调整物品组顺序时**：经 `api/editor/GroupResorter`——`getTier/setTier(itemGroup, tier)`、`swap(group1, group2)`、`resort()`、`sort(list)`；状态持久化在 `plugins/JustEnoughGuide/tiers.yml`（`GroupResorter.tiersFile` / `getOrCreateConfig()`），`GroupResorter.load()` 在启动时读盘、`rollback()` 在卸载时还原 `oldTiers`。
- **当玩家在指南里排序 / 改名物品组时**：走 `GroupTierEditorListener` 的选中流程（`enterSelecting` / `isSelecting` / `getSelectedGroup`），改名仅当 `custom_name: true` 时经 `applyName(...)` 生效。
- **当界面应按自定义顺序出组时**：`SurvivalGuideImplementation` / `CheatGuideImplementation` 的 `getVisibleItemGroups*` 已接 `GroupResorter.sort(...)`；不要另起一套排序。

### 3.12 配置项

- **当新增配置项时**：在 `src/main/resources/config.yml` 添加默认值（带注释），并在 `core/managers/ConfigManager` 增加带默认值的只读 getter（构造时一次性读入 final 字段），**禁止**在业务代码直接 `getConfig().getXxx(...)` 散落魔法字符串。

### 3.13 日志 / 异常

- **当捕获异常需要记录时**：`Debug.trace(e)` / `Debug.trace(e, "doing 描述")` / `Debug.traceExactly(...)`，**禁止** catch 后吞异常、也禁止裸 `e.printStackTrace()`。
- **当需要调试输出时**：`Debug.debug(...)`（受 `config.yml` 的 `debug` 开关控制），**禁止** `System.out.println()`。
- **当需要** `getLogger()`：info/warning/severe 常规日志；玩家弹消息用 `Debug.sendMessage(...)`（如需开关控制再包 Debug.debug）。

### 3.14 反射 patch Slimefun

- **当需要覆盖/还原 Slimefun 内部字段或静态方法时**：一律用 `utils/ReflectionUtil`（如 `setValue(...)`），并且**必须**在 `unloadInternal()` 中做对称还原（参照 guides / `JEGGuideSettings` / `FinalTECHValueDisplayGuideOption` 的 booted 标志）。还原不彻底 = 脏状态，影响 `/jeg reload`。

### 3.15 依赖与构建

- **当新增 Maven 依赖时**：加入 `build.gradle.kts` 的 `dependencies`（区分 `compileOnly` 与 `implementation`；服务端/玩家运行时不需打包的用 `compileOnly`）；软依赖插件在 `IntegrationManager` 判 `isPluginEnabled` 后使用，并 try/catch `NoClassDefFoundError`。跑 `gradle build` 验证。
- **当新增运行时动态加载库（如 pinyin）时**：在 `loadLibraries()` 里 `libraryManager.addMavenCentral()` + `Library.builder()...build()` + `loadLibrary(...)`，同时 `compileOnly` 声明对应依赖以编译。
- **当涉及 `lib/` 本地 JAR 时**：保持 `compileOnly(fileTree(mapOf("dir" to "lib", ...)))` 不变，不要删除 `lib/` 下文件。
- **当代码需要测试时**：为纯逻辑类（无 Bukkit 运行时依赖的算法/工具类）编写 JUnit 5（Jupiter）测试放 `src/test/java`；注意：`build.gradle.kts` 目前**尚未配置** `testImplementation("org.junit.jupiter:junit-jupiter:...")` 与 `test { useJUnitPlatform() }`，首次引入须先补齐。涉及 Bukkit/Slimefun 运行时的代码不做单元测试，靠游戏内验证。
- **当构建失败时**：先读报错定位（依赖缺失 / 编译错 / Shadow relocation 冲突），修复后重跑；**禁止**为绕过失败而 `-x test`/`--offline` 硬跳过。

### 3.16 提交与推送

- **当准备提交时**：先跑 `gradle clean build` 且通过，再 `git add` 相关文件；提交信息用 Conventional Commits（见第 7 节）。
- **当需要提交/推送时**：本仓库**没有上游**；`git commit` 与 `git push` 由开发者本人手动执行，AI 不代为执行。

---

## 4. 红线（绝对禁止操作）

> 违反以下任一条都属于严重事故。AI 在执行任务时若发现可能触碰红线，必须停下并向开发者说明。

1. **禁止 `git push -f` 或改写共享历史**：禁止 force push、rebase 改写、`git reset --hard` 后强推等任何改写历史的行为。
2. **禁止未构建验证就提交**：任何代码改动（含注释、文案、构建脚本）提交前必须通过 `gradle clean build`；禁止 `--offline`、跳过任务等绕过方式。
3. **禁止在生产路径使用 `System.out.println()`**：日志只能走 `getLogger()` 与 `Debug` 工具类；异常的完整记录用 `Debug.trace/traceExactly`。
4. **禁止绕过 Slimefun/集成注册流程**：新附属适配必须在 `IntegrationManager.load()` 的 `addIntegration(...)` 统一挂接；新 GuideOption 必须在 `JEGGuideSettings` 注册并排序。
5. **禁止破坏"替换/还原"对称性**：凡是 `onEnable()` 里反射 patch 了 Slimefun 内部（guides、GuideSettings、interceptSearch 等），`unloadInternal()` 必须有对应的还原；否则 `/jeg reload` 会留下脏状态。
6. **禁止直接引用未在 `compileOnly` 声明的软依赖类**：所有软依赖交互先 `IntegrationManager.isEnabledXxx()` + try/catch `NoClassDefFoundError`。
7. **禁止改动 `onEnable()` / `unloadInternal()` 的初始化与卸载顺序**：乱序会导致状态不一致、书签/RTS 背包残留、物品组 tier 未还原。

---

## 5. 质量规范

- **代码风格**：跟随现有代码风格（Google 风格为主、4 空格缩进、`@NullMarked`/`@Nullable` 标注、`@SuppressWarnings` 说明理由）。未配置 Checkstyle/SpotBugs/PMD，**不要**在本次任务中擅自引入新 linter 或格式化工具。
- **Lombok**：项目已配置 Lombok（`compileOnly` + `annotationProcessor`），新类可按需使用 `@Getter`/`@Setter`/`@UtilityClass` 等；不要移除既有 Lombok 注解。
- **可编译性**：每个完成的改动都必须是"能编译、能运行"的完整状态；不要留下半成品/死代码/未使用 import。
- **向后兼容**：不得破坏已有玩家数据格式（书签背包持久化标记、`tiers.yml` 的 `tier/name/custom_name` 键、配置结构、Slimefun 物品 ID）。改名会破坏已有数据。

---

## 6. 常用 Gradle 命令速查表

> 本地推荐使用系统 `gradle`（仓库亦提供 wrapper：`gradlew.bat`）。受限环境下可加 `--no-daemon --no-watch-fs`。

| 场景                      | 命令                                         |
|-------------------------|--------------------------------------------|
| 标准验收构建（必须通过）            | `gradle clean build`                       |
| 增量编译（快速检查）              | `gradle compileJava`                       |
| 打 fat jar（含 relocation） | `gradle shadowJar`（`build` 已依赖它）           |
| 运行单元测试（引入 JUnit 后）      | `gradle test`                              |
| 查看依赖树                   | `gradle dependencies`                      |
| 清理产物                    | `gradle clean`                             |
| 产物位置                    | `build/libs/JustEnoughGuide-<version>.jar` |

---

## 7. Git 工作流

- **仓库**：本地仓库即开发仓库，**没有上游**；直接在 `master` 开发，提交与推送均由开发者本人手动执行。
- **提交信息**：Conventional Commits，格式 `type(scope): 描述`，中英文均可：
  - `feat:` 新功能
  - `fix:` Bug 修复
  - `docs:` 文档（含本文件）
  - `refactor:` 重构（行为不变）
  - `chore:` 构建/依赖/杂务
  - 例：`feat(search): 新增搜索黑名单对非限定词查询的过滤`
- **提交前自检清单**：
  1. `gradle clean build` 通过；
  2. 未触碰第 4 节红线；
  3. 玩家可见文案沿用既定机制（内联 + 着色，或经 SlimefunTranslation 翻译）；
  4. 只提交相关文件（不提交 `build/`、`.gradle/`、IDE 临时文件）。

---

## 8. 核心类 API 速查与 Contract

> 本节为手写速查，替代逐个翻源码。**Contract 说明**：凡标注"只读"的方法不得修改入参/全局状态；标注"改原参"调用前必须 clone（除非你确实要消费它）。

### 8.1 ConfigManager（`com.balugaq.jeg.core.managers.ConfigManager`）

构造时一次性把 `config.yml` 读入 `final` 字段，并提供只读 getter。常用：

| Getter                                                                                 | 配置键                                         | 默认             |
|----------------------------------------------------------------------------------------|---------------------------------------------|----------------|
| `isDebug()`                                                                            | `debug`                                     | false          |
| `isAutoUpdate()`                                                                       | `auto-update`                               | true           |
| `isPinyinSearch()`                                                                     | `improvements.pinyin-search`                | true           |
| `isBookmark()`                                                                         | `improvements.bookmark`                     | true           |
| `isRTSSearch()`                                                                        | `improvements.rts-search`                   | true           |
| `isBeginnerOption()`                                                                   | `improvements.beginner-option`              | true           |
| `isEMCValueDisplay()`                                                                  | `improvements.emc-display-option`           | true           |
| `isFinalTechValueDisplay()`                                                            | `improvements.finaltech-emc-display-option` | true           |
| `isFinalTECHValueDisplay()`                                                            | `improvements.finalTECH-emc-display-option` | true           |
| `isCerPatch()`                                                                         | `improvements.cer-patch`                    | false          |
| `isRecipeComplete()`                                                                   | `recipe-complete`                           | true           |
| `isAutoAddRecipeCompleteButton()`                                                      | `auto-add-recipe-complete-button`           | true           |
| `getBlacklist()` / `getBanList()`                                                      | `blacklist` / `banlist`                     | `List<String>` |
| `getSharedChars()`                                                                     | `shared-chars`                              | `List<String>` |
| `getMainFormat()` / `getRecipeFormat()` / `getKeybindsFormat()` / `getKeybindFormat()` | `custom-format.*`                           | `List<String>` |

Contract：getter 全部**只读**；`load()`/`unload()` 由主类生命周期统一调用，业务代码不直接 reload。

### 8.2 GuideUtil（`com.balugaq.jeg.utils.GuideUtil`）

指南渲染与导航的门面。常用入口：

| 方法                                                                                        | 用途                                    |
|-------------------------------------------------------------------------------------------|---------------------------------------|
| `getGuide(player, mode)`                                                                  | 取对应模式的 Guide 实现                       |
| `openKeybindsGui(player)`                                                                 | 打开按键列表界面                              |
| `openKeybindGui(player, OnClick)`                                                         | 打开单按键编辑界面                             |
| `openActionSelectGui(player, OnClick, BaseAction)`                                        | 打开按键动作选择界面                            |
| `getVisibleItemGroupsSurvival/Cheat(p, profile, selecting)`                               | 按自定义顺序返回可见物品组（接 `GroupResorter.sort`） |
| `commonRender(menu, format, ...)`                                                         | 依据 `Format` 渲染公共元素（背景/翻页等）            |
| `getKeybindIcon(OnClick)` / `getLeftActionIcon(BaseAction)` / `getActionIcon(BaseAction)` | 按键/动作图标                               |

Contract：`GuideUtil.shutdown()` 在卸载时清理缓存；不手动清缓存以免与 `GroupResorter.resort()`/reload 冲突。

### 8.3 Guides — 覆盖 Slimefun 指南

`implementation/guide/SurvivalGuideImplementation` 与 `CheatGuideImplementation` 实现 Slimefun 的 `SlimefunGuideImplementation`，通过 `ReflectionUtil.setValue(Slimefun.getRegistry(), "guides", newGuides)` 注入。新增"指南可见性/排序"逻辑从这里走。

### 8.4 Bookmark（`api/objects/collection/data/Bookmark.java`）+ BookmarkManager

- `Bookmark.of(SlimefunItem)` / `Bookmark.of(ItemGroup)` / `Bookmark.of(ItemStack)` 构造；数据经玩家背包标记物品持久化。
- `BookmarkManager`（经 `JustEnoughGuide.getBookmarkManager()`）：`addBookmark(player, item|group)` / `removeBookmark` / `clearBookmarks` / `getBookmarkedItems(player)`（返回 `List<Bookmark>`）。
- Contract：书签物品通过持久化 data 判别（`markItemAsBookmarksItem` / `isBookmarksItem`），**不要**手改这类 ItemStack 的持久化键；切档迁移走 `api/interfaces/BookmarkRelocation`。

### 8.5 SearchGroup / RTSSearchGroup / SearchGroupLoader

- `api/groups/SearchGroup`：物品搜索缓存与黑名单判定（`inBlacklist`/`inBanlist`），`SearchGroup.tryInit()` 在启动时构建。
- `api/groups/RTSSearchGroup`：实时搜索的玩家级状态（`RTS_PLAYERS`/`RTS_SEARCH_TERMS`/`RTS_SEARCH_GROUPS`/`RTS_PAGES`）；请**只经 `core/listeners/RTSListener`** 读写，避免残留。
- `api/SearchGroupLoader`：构建搜索索引；拼音键 = `PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, "")`（仅 `isPinyinSearch()` 时）。
- Contract：搜索缓存为只读快照；不清缓存不改索引结构，避免影响其它搜索路径。

### 8.6 GroupResorter（`api/editor/GroupResorter`）— 物品组排序

- `getTier/setTier(ItemGroup, int)`、`swap(Group, Group)`、`resort()`（写回 Slimefun 并清 JEG 缓存排序）、`sort(List<ItemGroup>)`。
- 状态持久化：`plugins/JustEnoughGuide/tiers.yml`（`<key>.tier` / `.<name>` / `.<custom_name>`）；`load()` 读盘、`rollback()` 还原 `oldTiers`。
- `getKey(ItemGroup)` 区分 `NestedItemGroup` / `SubItemGroup` / 平级组的键规则。
- Contract：改名仅当 `custom_name: true` 时经 `applyName(...)` 生效；排序必须以 `resort()` 收尾以刷新 `GuideUtil` 菜单缓存。

### 8.7 Debug（`com.balugaq.jeg.utils.Debug`）

| 方法                                        | 说明                                           |
|-------------------------------------------|----------------------------------------------|
| `debug(...)`                              | 仅 `debug: true` 时输出（开发调试）                    |
| `log/warn/severe(...)` / `log(Throwable)` | 控制台彩色输出                                      |
| `trace/traceExactly(e[, doing][, code])`  | 异常横幅 + 堆栈，并 `dumpToFile` 落盘 `error-reports/` |
| `dumpToFile(e, code)`                     | 把异常详情/环境写盘                                   |
| `sendMessage(player, msg)`                | 给玩家发 `[插件名]消息`（原文，不走翻译）                      |

Contract：只读入参、仅副作用；catch 块必须用它记异常。

### 8.8 点击处理 / 按键绑定（`utils/clickhandler`）

- `OnClick`：一个可触发按键，挂一组 `BaseAction`（`listActions()`）。
- `BaseAction`：动作基类；`PermissibleAction` 带权限过滤；`ActionKey` 按键类型；`OnDisplay` 图标显示。
- 交互事件：`api/objects/events/GuideEvents`（`KeybindButtonClickEvent`/`ActionButtonClickEvent`/`KeybindsButtonClickEvent`/`SubKeybindsButtonClickEvent`）；统一用 `EventUtil.callEvent(event).ifSuccess(...)` 分发。
- Contract：动作触发必须经 OnClick/事件链，禁止绕过直接改按键数据。

### 8.9 配方补全（`api/recipe_complete`）

- 入口：`RecipeCompleteProvider`（`isRecipeComplete()` 判总开关；`getSources()`/`getSpecialRecipeHandlers()`/`getItemStack(...)`/`openSlimefun(...)`/`openVanilla(...)`）。
- 源头：`source/base/Source` 接口 + `VanillaSource` / `SlimefunSource` 基类；`RecipeCompleteSession` 承载补全过程状态；事件 `api/objects/events/RecipeCompleteEvents`；监听 `core/listeners/RecipeCompletableListener`。
- 指南选项：`implementation/option/RecursiveRecipeFillingGuideOption`（补全深度）、`RecipeFillingWithNearbyContainerGuideOption`（自动抓取范围）、`RecipeCompleteOpenModeGuideOption`、`NoticeMissingMaterialGuideOption`。

### 8.10 集成适配（`core/integrations/Integration` + `IntegrationManager`）

- 实现 `Integration` 接口（`getAddonName()` / `load()` 等），在 `IntegrationManager.load()` 的 `addIntegration(isEnabledX, XxxIntegrationMain::new)` 注册。
- 常用适配：`emctech/EMCTechIntegrationMain` + `EMCValueDisplayGuideOption`；`slimefuntranslation/SlimefunTranslationIntegrationMain`（`translateItem` + 启动时反射关 `interceptSearch`、卸载时还原）；`networksexpansion` / `slimeaeplugin` 等。
- Contract：**只**经 `IntegrationManager.isEnabledXxx()` 判启用；引用软依赖类用 try/catch `NoClassDefFoundError`（参照 `JustEnoughGuide.tryUpdate()`）。

---

## 9. AI 对用户的回答规范

- **先一句话回答**：回答开头用一句话说清"我做了什么/结论是什么"。
- **再简短补充**：只补充代码/文件里看不出来的信息——决策依据、取舍、待确认事项、风险点。
- **不重复"代码可说明"的内容**：不要把刚写进代码/文档的东西再抄一遍；用户直接查看改动文件即可获得细节。
- **保持简短**：除非用户明确要求详细讲解，回答控制在几句话内。

---

## 10. 文档更新契约

1. **冲突即提示**：当 AI 发现本文与代码不一致时（例如：构建命令、启动顺序、包路径、配置键、依赖声明与本文不符），**必须**在回复中主动指出冲突，并说明应以代码为准还是更新本文。
2. **惯例沉淀**：当本次任务产生了新的、可复用的约定（新的注册模式、新的排序/搜索/补全约定）时，应提议将其补充进本文对应章节（先提议，经开发者确认后修改）。
3. **保持精简**：更新本文时不得堆砌无行动含义的描述性文字；每一条规则都应能被"是否遵守"直接检查。
4. **变更记录**：修改本文后，提交信息使用 `docs(agents): ...`，并在提交说明中一句话概括变更点。
5. **功能范围**：本仓库的核心能力含——配方补全、拼音搜索、实时搜索、搜索黑名单/禁用/相似字、附属名翻译、界面优化（自定义布局）、书签系统、EMC 适配显示、指南按键绑定、超大配方显示、自定义物品组排序。新增/修改这些能力时，先回到第 3 节对应决策分支。
