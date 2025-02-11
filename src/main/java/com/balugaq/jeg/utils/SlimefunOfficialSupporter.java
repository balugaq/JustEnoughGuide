package com.balugaq.jeg.utils;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SlimefunOfficialSupporter {
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
