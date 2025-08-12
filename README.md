# JustEnoughGuide - Better Slimefun Guide

JustEnoughGuide (JEG for short) is a Slimefun addon for Minecraft that significantly enhances the functionality and user experience of the original Slimefun guide book. This plugin aims to provide a more intuitive and efficient way to access Slimefun item recipes and information for Minecraft servers.

## Key Enhanced Features

### 1. Enhanced User Interface
- **Custom Layout System**: Fully customize the guide book interface layout through configuration files
- **Improved Visual Design**: Clearer item display and navigation system
- **Multi-page Support**: Support for more complex interface designs with more functional areas

### 2. Powerful Search Functionality
- **Pinyin Search**: Search Chinese item names using pinyin
- **Real-Time Search (RTS)**: Search items in real-time within an anvil interface without typing complete names
- **Smart Filtering**: Support for blacklist and ban list to control which items can be searched
- **Character Mapping**: Allow searching across similar characters (e.g. "粘黏" and "荧萤")

### 3. Bookmark System
- **Personal Collections**: Players can bookmark frequently used items for quick access
- **Persistent Storage**: Bookmark data is saved in each player's personal backpack

### 4. Multiple Guide Options
- **Beginner's Guide**: Provide more friendly guidance for new players
- **EMC Display**: Display EMC values of items (for related addons)
- **Recipe Sharing**: Allow players to share item recipes
- **Recursive Recipe Filling**: Automatically fill sub-recipes in recipes

### 5. Custom Item Groups
- **Flexible Grouping System**: Support for creating custom item groups
- **Group Reordering**: Rearrange the display order of item groups
- **Hidden Groups**: Hide specific item groups from appearing in the guide

### 6. Advanced Recipe Display
- **Recipe Completion**: Provide recipe completion functionality to help players understand the complete crafting process
- **Large Recipe Support**: Support for displaying complex multi-block structure recipes
- **Recipe Type Display**: Clearly identify the crafting type of each recipe

## Configuration Options

The plugin provides extensive configuration options that allow server administrators to fully customize the guide book's behavior:

```yaml
guide:
  survival-improvements: true     # Enable survival mode guide improvements
  cheat-improvements: true        # Enable cheat mode guide improvements
  survival-guide-title: "..."     # Survival mode guide title
  cheat-guide-title: "..."        # Cheat mode guide title

improvements:
  pinyin-search: true             # Enable pinyin search
  bookmark: true                  # Enable bookmark functionality
  rts-search: true                # Enable real-time search
  beginner-option: true           # Enable beginner option
```

## Interface Customization

Through the `custom-format` section in the configuration file, you can fully customize various interface layouts:

- Main interface layout
- Nested group interface
- Sub-group interface
- Recipe display interface
- Settings interface, etc.

Each interface can be defined with character mapping to position different elements, such as Background(B), Back button(b), Search(S), Item Groups(G), etc.

## Localization Support

The plugin includes Chinese translations for a large number of Slimefun addons, ensuring that all items and groups have appropriate Chinese names displayed.

## Technical Features

- **High Performance**: Uses asynchronous processing and caching mechanisms to ensure smooth experience
- **Strong Compatibility**: Compatible with most Slimefun addons
- **Easy Configuration**: All features can be adjusted through configuration files
- **Modular Design**: Independent functional modules that can be enabled/disabled as needed

## Usage

1. Place the plugin in the server's plugins folder
2. Start the server to generate configuration files
3. Modify the configuration files as needed
4. Restart the server to apply the configuration
5. All improvements will automatically apply when players use the Slimefun guide book

## Contributing

Feel free to submit issues and pull requests to help improve this plugin.

## License

This project is open-sourced under the MIT License.



# JustEnoughGuide - 更好的粘液书

JustEnoughGuide（简称JEG）是一个针对Slimefun的插件附属，它显著改进了原版Slimefun指南书的功能和用户体验。该插件旨在为Minecraft服务器提供更直观、更高效的Slimefun物品制作指南。

## 主要改进功能

### 1. 增强的用户界面
- **自定义布局系统**：通过配置文件完全自定义指南书的界面布局
- **改进的视觉设计**：更清晰的物品展示和导航系统
- **多页面支持**：支持更复杂的界面设计，提供更多功能区域

### 2. 强大的搜索功能
- **拼音搜索**：支持使用拼音搜索中文物品名称
- **实时搜索（RTS）**：在铁砧界面中实时搜索物品，无需输入完整名称
- **智能过滤**：支持黑名单和禁用列表，控制哪些物品可以被搜索到
- **字符映射**：允许相似字符间互通搜索（如"粘黏"和"荧萤"）

### 3. 书签系统
- **个人收藏**：玩家可以收藏常用物品，方便快速访问
- **持久化存储**：书签数据保存在每个玩家的个人背包中

### 4. 多种指南选项
- **新手指引**：为新手玩家提供更友好的指引功能
- **EMC显示**：显示物品的EMC值（适用于相关附属）
- **配方分享**：允许玩家分享物品配方
- **递归配方填充**：自动填充配方中的子配方

### 5. 自定义物品组
- **灵活的分组系统**：支持创建自定义物品组
- **物品组重排序**：可以重新排列物品组的显示顺序
- **隐藏物品组**：可以隐藏特定物品组不显示在指南中

### 6. 高级配方显示
- **配方补全**：提供配方补全功能，帮助玩家了解完整制作流程
- **超大配方支持**：支持显示复杂的多方块结构配方
- **配方类型显示**：清晰标识每种配方的制作类型

## 配置选项

插件提供丰富的配置选项，允许服务器管理员完全自定义指南书的行为：

```yaml
guide:
  survival-improvements: true     # 启用生存模式指南改进
  cheat-improvements: true        # 启用作弊模式指南改进
  survival-guide-title: "..."     # 生存模式指南标题
  cheat-guide-title: "..."        # 作弊模式指南标题

improvements:
  pinyin-search: true             # 启用拼音搜索
  bookmark: true                  # 启用书签功能
  rts-search: true                # 启用实时搜索
  beginner-option: true           # 启用新手选项
```

## 界面自定义

通过配置文件中的`custom-format`部分，可以完全自定义各种界面布局：

- 主界面布局
- 嵌套组界面
- 子组界面
- 配方显示界面
- 设置界面等

每个界面都可以通过字符映射来定义不同元素的位置，如背景板(B)、搜索(S)、返回(b)、物品组(G)等。

## 本地化支持

插件内置大量Slimefun附属的中文翻译，确保所有物品和组都有恰当的中文名称显示。

## 技术特性

- **高性能**：使用异步处理和缓存机制确保流畅体验
- **兼容性强**：与大多数Slimefun附属兼容
- **易于配置**：所有功能都可通过配置文件调整
- **模块化设计**：各功能模块独立，可根据需要启用/禁用

## 使用方法

1. 将插件放入服务器plugins文件夹
2. 启动服务器以生成配置文件
3. 根据需要修改配置文件
4. 重启服务器使配置生效
5. 玩家使用Slimefun指南书时将自动应用所有改进

## 贡献

欢迎提交Issue和Pull Request来帮助改进这个插件。

## 许可证

本项目基于MIT许可证开源。