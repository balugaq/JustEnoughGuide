<div align="center">

[简体中文](./README.md) | **English**

</div>

# JustEnoughGuide - A Better Slimefun Guide

<img src="https://builds.guizhanss.com/api/badge/balugaq/JustEnoughGuide/master/latest">

[Download](https://resources.guizhanss.com/plugin/JustEnoughGuide/versions)

![](images/附属图标.png)

JustEnoughGuide (JEG) is a **Slimefun addon** that significantly improves the vanilla Slimefun Guide, letting players browse Slimefun item recipes and information more intuitively and efficiently.

- **Requires**: Minecraft 1.16+ (1.21.10+ recommended), Paper or its forks
- **Hard dependencies**: Slimefun4, GuizhanLibPlugin
- **Optional integrations**: SlimefunTranslation, EMCTech, FinalTech / FinalTECH, Logitech, and more

![](images/主界面.png)

## Recipe Completion

Fill a machine's ingredients directly from the recipe page — no more hauling items back and forth:

- **One-click completion**: click the completion button on a recipe page to auto-fill materials into the target machine and start it
- **Recursive completion**: missing intermediate materials? Sub-recipes are completed automatically (toggleable in guide settings)
- **Nearby container support**: materials are pulled from chests next to you first (toggleable in guide settings)
- **Missing material notice**: when materials fall short, it tells you exactly what is missing
- **Auto-added completion button**: adapted machines show the completion entry automatically; exclusions per block / addon are configurable

![](images/配方补全.png)

## Pinyin Search

- Search Chinese item names by **full pinyin or initials** — type `nj` or `ningjiao` to find 凝胶 (gel)
- **Shared similar characters**: group easily-confused characters together (e.g. 粘/黏, 荧/萤) so typos still match
- **Blacklist / banlist**: keep unwanted keywords out of search results

![](images/拼音搜索.png)

## Real-Time Search (RTS)

- Search **while typing** in an anvil GUI — no need to enter the full name
- Results refresh live; click to jump straight to the item page
- Can be launched from the guide itself, pairing well with bookmarks for quick access

![](images/实时搜索.png)

## Interface Improvements

- **Fully customizable layouts**: the main page, categories, recipes, settings and a dozen more screens are all adjustable via config
- **Character-mapped layouts**: every slot is a character (background `B`, search `S`, item group `G`…), so moving a button is as easy as moving a letter
- **Clearer navigation**: back, page turn, search and bookmark buttons are right where you expect them

![](images/界面优化.png)

## Bookmarks

- **Favorite items**: bookmark any item on its page, then revisit it from the bookmark list anytime
- **Persistent storage**: bookmarks are saved per player and survive relogs

![](images/书签系统.png)

## EMC Value Display

- Displays EMC values from **EMCTech**, **FinalTech / FinalTECH** and other supported addons in the guide
- Each addon's EMC display can be toggled independently in the config

## Guide Keybinds

- Players can remap guide actions (back, search, etc.) in **guide settings**
- After remapping, clicking the original button position **automatically redirects** to the mapped action
- Servers can disable redirection entirely, forcing everyone onto the default layout

![](images/指南按键绑定.png)

## Oversized Recipe Display

- Full display for **oversized recipes** (6x6) — no longer crammed into a 3x3 grid
- Recipe pages reserve an oversized-recipe slot (layout character `E`) that you can reposition freely

## Custom Item Group Sorting

- Create **custom item groups** to gather scattered items into one place
- Supports **group ordering** and **in-group tier sorting** — tame the chaos of addon groups
- Item groups you don't want to show can be hidden

## Addon Name Translations

- Built-in Chinese display names for **hundreds of Slimefun addons**, so addon names no longer mix Chinese and English in search
- Missing a translation? Add one line in the config yourself

## Commands

| Command | Description |
|---------|-------------|
| `/jeg help` | Show help |
| `/jeg timings` | Slimefun machine performance report (last-tick timings, scores, top machines / chunks / plugins) |

## Configuration

Almost every behavior is adjustable in `config.yml` — **every option ships with a one-line comment** explaining what it does and how to change it to get what you want. The config file is generated at `plugins/JustEnoughGuide/config.yml` on first startup; restart the server (or run `/jeg reload`) to apply changes.

## Getting Started

1. Drop the plugin into your server's `plugins` folder
2. Start the server to generate the config file
3. Tweak the config as needed (every option is commented)
4. Restart the server to apply the config
5. All improvements apply automatically whenever players open the Slimefun Guide

## Contributing

Issues and Pull Requests are welcome — please read the [Code of Conduct](./CODE_OF_CONDUCT.md) first.

## License

This project is open source under the GPLv3 license.

## Sponsoring

If you enjoy this addon, please consider [sponsoring the author](./SPONSOR.md) — or simply [buy them a bubble tea](./SPONSOR.md)~
