package com.balugaq.jeg.utils;

import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class Models {
    public static final ItemStack RTS_ITEM = Converter.getItem(new SlimefunItemStack("_UI_RTS_ICON", Converter.getItem(Material.ANVIL, "&b实时搜索", "")));
    public static final ItemStack SPECIAL_MENU_ITEM = Converter.getItem(new SlimefunItemStack("_UI_SPECIAL_MENU_ICON", Converter.getItem(Material.COMPASS, "&b超大配方", "", "&a点击打开超大配方(若有)")));
    public static final ItemStack INPUT_TEXT_ICON = Converter.getItem(new SlimefunItemStack("_UI_RTS_INPUT_TEXT_ICON", Converter.getItem(
            Material.PAPER,
            "&f搜索: &7在上方输入搜索词",
            "&fNote:",
            "&7 - &e左侧物品为返回键",
            "&7 - &e中间物品为按键上一页",
            "&7 - &e右侧物品为按键下一页"
    )));
}
