package com.balugaq.jeg.utils;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class SlimefunOfficialSupporter {
    public static ItemStack getBackButton(Player player) {
        return ItemStackUtil.getCleanItem(ChestMenuUtils.getBackButton(player, Lang.getStringArray("message.guide.back-button-extra-lore")));
    }
    public static boolean isShowHiddenItemGroups() {
        return Slimefun.getCfg().getBoolean("guide.show-hidden-item-groups-in-search");
    }

    public static boolean isShowVanillaRecipes() {
        return Slimefun.getCfg().getBoolean("guide.show-vanilla-recipes");
    }

    public static boolean isEnableResearching() {
        return Slimefun.getResearchCfg().getBoolean("enable-researching");
    }
}
