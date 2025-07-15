package com.balugaq.jeg.utils;

import com.balugaq.jeg.core.services.LocalizationService;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Lang {
    public static final ItemStack RAW_INPUT_TEXT_ICON = Lang.getIcon("input-text", Material.PAPER);
    public static final ItemStack INPUT_TEXT_ICON = Converter.getItem(new SlimefunItemStack("_UI_RTS_INPUT_TEXT_ICON", RAW_INPUT_TEXT_ICON));
    public static final ItemStack RAW_RTS_ITEM = Lang.getIcon("guide.real-time-search", Material.ANVIL);
    public static final ItemStack RTS_ITEM = Converter.getItem(new SlimefunItemStack("_UI_RTS_ICON", RAW_RTS_ITEM));
    public static final ItemStack RAW_SPECIAL_MENU_ITEM = Lang.getIcon("guide.special-menu", Material.COMPASS);
    public static final ItemStack SPECIAL_MENU_ITEM = Converter.getItem(new SlimefunItemStack("_UI_SPECIAL_MENU_ICON", RAW_SPECIAL_MENU_ITEM));
    public static final ItemStack RAW_NEXCAVATE_ITEMS_GROUP_ITEM = Lang.getIcon("nexcavate-items-group", Material.BLACKSTONE);
    public static final ItemStack NEXCAVATE_ITEMS_GROUP_ITEM = Converter.getItem(new SlimefunItemStack("JEG_NEXCAVATE_ITEMS_GROUP_ICON", RAW_NEXCAVATE_ITEMS_GROUP_ITEM));
    public static final ItemStack RAW_VANILLA_ITEMS_GROUP_ITEM = Lang.getIcon("vanilla-items-group", Material.CRAFTING_TABLE);
    public static final ItemStack VANILLA_ITEMS_GROUP_ITEM = Converter.getItem(new SlimefunItemStack("JEG_VANILLA_ITEMS_GROUP", RAW_VANILLA_ITEMS_GROUP_ITEM));
    public static final ItemStack RAW_JEG_ITEMS_GROUP_ITEM = Lang.getIcon("jeg-items-group", Material.BOOK);
    public static final ItemStack JEG_ITEMS_GROUP_ITEM = Converter.getItem(new SlimefunItemStack("JEG_JEG_ITEMS_GROUP", RAW_JEG_ITEMS_GROUP_ITEM));
    public static final ItemStack RAW_RECIPE_COMPLETE_GUIDE = Lang.getIcon("items.recipe-complete-guide", Material.SLIME_BALL);
    public static final SlimefunItemStack RECIPE_COMPLETE_GUIDE = new SlimefunItemStack("JEG_RECIPE_COMPLETE_BOOK", RAW_RECIPE_COMPLETE_GUIDE);
    public static final ItemStack RAW_USAGE_INFO = Lang.getIcon("items.recipe-complete-usage-info", Material.PAPER);
    public static final SlimefunItemStack USAGE_INFO = new SlimefunItemStack("JEG_RECIPE_COMPLETE_USAGE_INFO", RAW_USAGE_INFO);
    public static final ItemStack RAW_MECHANISM = Lang.getIcon("items.recipe-complete-mechanism", Material.PAPER);
    public static final SlimefunItemStack MECHANISM = new SlimefunItemStack("JEG_RECIPE_COMPLETE_MECHANISM", RAW_MECHANISM);
    public static final ItemStack RAW_SUPPORTED_ADDONS_INFO = Lang.getIcon("items.recipe-complete-supported-addons-info", Material.PAPER);
    public static final SlimefunItemStack SUPPORTED_ADDONS_INFO = new SlimefunItemStack("JEG_RECIPE_COMPLETE_SUPPORTED_ADDONS_INFO", RAW_SUPPORTED_ADDONS_INFO);

    public static @Nullable LocalizationService get() {
        return JustEnoughGuide.getInstance().getLocalizationService();
    }

    public static @NotNull String getString(@NotNull String path) {
        return get().getString(path);
    }

    public static @NotNull String getString(@NotNull String path, Object... args) {
        return decorate(get().getString(path), args);
    }

    public static @NotNull String decorate(String text, Object @NotNull ... args) {
        for (int i = 0; i < args.length; i += 2) {
            String key = "{" + args[i] + "}";
            String value = String.valueOf(args[i + 1]);
            text = text.replace(key, value);
        }

        return text;
    }

    public static @NotNull List<String> getStringList(@NotNull String path) {
        return get().getStringList(path);
    }

    public static @NotNull List<String> getStringList(@NotNull String path, Object... args) {
        List<String> raw = get().getStringList(path);
        List<String> decorated = new ArrayList<>();
        for (String s : raw) {
            decorated.add(decorate(s, args));
        }

        return decorated;
    }

    public static @NotNull String @NotNull [] getStringArray(@NotNull String path) {
        return get().getStringList(path).toArray(new String[0]);
    }

    public static @NotNull String @NotNull [] getStringArray(@NotNull String path, Object... args) {
        List<String> raw = get().getStringList(path);
        List<String> decorated = new ArrayList<>();
        for (String s : raw) {
            decorated.add(decorate(s, args));
        }

        return decorated.toArray(new String[0]);
    }

    public static @NotNull String getDebug(String path) {
        return get().getString("debug." + path);
    }

    public static @NotNull String getDebug(String path, Object... args) {
        return decorate(get().getString("debug." + path), args);
    }

    public static @NotNull String getItemName(String itemId) {
        return get().getString("item." + itemId + ".name");
    }

    public static @NotNull String getItemName(String itemId, Object... args) {
        return decorate(get().getString("item." + itemId + ".name"), args);
    }

    public static @NotNull String getMessage(String path) {
        return get().getString("message." + path);
    }

    public static @NotNull String getMessage(String path, Object... args) {
        return decorate(get().getString("message." + path), args);
    }

    public static @NotNull String @NotNull [] getItemLore(String itemId) {
        return get().getStringList("item." + itemId + ".lore").toArray(new String[0]);
    }

    public static @NotNull String @NotNull [] getItemLore(String itemId, Object... args) {
        List<String> raw = get().getStringList("item." + itemId + ".lore");
        List<String> decorated = new ArrayList<>();
        for (String s : raw) {
            decorated.add(decorate(s, args));
        }

        return decorated.toArray(new String[0]);
    }

    public static @NotNull String getStartup(String path) {
        return get().getString("startup." + path);
    }

    public static @NotNull String getStartup(String path, Object... args) {
        return decorate(get().getString("startup." + path), args);
    }

    public static @NotNull String getShutdown(String path) {
        return get().getString("shutdown." + path);
    }

    public static @NotNull String getShutdown(String path, Object... args) {
        return decorate(get().getString("shutdown." + path), args);
    }

    public static @NotNull ItemStack getIcon(String path, @NotNull Material material) {
        String iconName = getString("icon." + path + ".name");
        String[] iconLore = getStringArray("icon." + path + ".lore");
        return Converter.getItem(material, iconName, iconLore);
    }

    public static @NotNull ItemStack getIcon(String path, @NotNull Material material, Object... args) {
        String iconName = getString("icon." + path + ".name", args);
        String[] iconLore = getStringArray("icon." + path + ".lore", args);
        return Converter.getItem(material, iconName, iconLore);
    }

    public static @NotNull ItemStack getGuideGroupIcon(String path, @NotNull Material material) {
        String iconName = getString("icon.guide-group." + path + ".name");
        String[] iconLore = getStringArray("icon.guide-group." + path + ".lore");
        return Converter.getItem(material, iconName, iconLore);
    }

    public static @NotNull ItemStack getGuideGroupIcon(String path, @NotNull Material material, Object... args) {
        String iconName = getString("icon.guide-group." + path + ".name", args);
        String[] iconLore = getStringArray("icon.guide-group." + path + ".lore", args);
        return Converter.getItem(material, iconName, iconLore);
    }

    public static @NotNull String[] getCommandSuccess(String command) {
        return getStringArray("message.command." + command + ".success");
    }

    public static @NotNull String getCommandMessage(String path) {
        return getString("message.command." + path);
    }

    public static @NotNull String getCommandMessage(String command, String path) {
        return getString("message.command." + command + "." + path);
    }

    public static @NotNull String getCommandMessage(String command, String path, Object... args) {
        return getString("message.command." + command + "." + path, args);
    }

    public static @NotNull String getGuideMessage(String path) {
        return getMessage("guide." + path);
    }

    public static @NotNull String getGuideMessage(String path, Object... args) {
        return getMessage("guide." + path, args);
    }

    public static @NotNull String getError(String path) {
        return getString("error." + path);
    }

    public static @NotNull String getError(String path, Object... args) {
        return decorate(getString("error." + path), args);
    }
}
