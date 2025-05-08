package com.balugaq.jeg.utils;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.experimental.UtilityClass;
import net.guizhanss.slimefuntranslation.api.SlimefunTranslationAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class SlimefunOfficialSupporter {
    public static @NotNull ItemStack getBackButton(@NotNull Player player) {
        return Converter.getItem(ChestMenuUtils.getBackButton(player, Lang.getStringArray("message.guide.back-button-extra-lore")));
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

    public static @NotNull ItemStack translateItem(@NotNull Player player, @NotNull ItemStack itemStack) {
        itemStack = Converter.getItem(itemStack);
        if (JustEnoughGuide.getIntegrationManager().isEnabledSlimefunTranslation()) {
            SlimefunTranslationAPI.translateItem(SlimefunTranslationAPI.getUser(player), itemStack);
        }

        return itemStack;
    }

    public static @NotNull String getTranslatedItemName(@NotNull Player player, @NotNull SlimefunItem slimefunItem) {
        if (JustEnoughGuide.getIntegrationManager().isEnabledSlimefunTranslation()) {
            return SlimefunTranslationAPI.getItemName(SlimefunTranslationAPI.getUser(player), slimefunItem);
        }

        return slimefunItem.getItemName();
    }
}
