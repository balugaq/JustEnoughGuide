package com.balugaq.jeg.utils;

import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class Models {
    public static final ItemStack RTS_ITEM = Converter.getItem(new SlimefunItemStack("_UI_RTS_ICON", Converter.getItem(Material.ANVIL, "Deprecated Icon")));
    public static final ItemStack SPECIAL_MENU_ITEM = Converter.getItem(new SlimefunItemStack("_UI_SPECIAL_MENU_ICON", Converter.getItem(Material.COMPASS, "Deprecated Icon")));
    public static final ItemStack INPUT_TEXT_ICON = Converter.getItem(new SlimefunItemStack("_UI_RTS_INPUT_TEXT_ICON", Converter.getItem(Material.PAPER, "Deprecated Icon")));
}
