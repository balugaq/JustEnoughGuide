package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Lang;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * This class is responsible for managing integrations with other plugins.
 *
 * @author balugaq
 * @since 1.2
 */
@Getter
public class IntegrationManager extends AbstractManager {
    private final @NotNull JavaPlugin plugin;
    private final boolean enabledNetworksExpansion;
    private final boolean enabledOreWorkshop;
    private final boolean enabledSlimefunTranslation;

    public IntegrationManager(@NotNull JavaPlugin plugin) {
        boolean tmp;
        this.plugin = plugin;

        // Check if NetworksExpansion is enabled
        try {
            Class.forName("com.ytdd9527.networksexpansion.core.listener.NetworksGuideListener");
            tmp = true;
        } catch (ClassNotFoundException e) {
            tmp = false;
        }

        enabledNetworksExpansion = tmp;

        // Check if OreWorkshop is enabled
        this.enabledOreWorkshop = plugin.getServer().getPluginManager().isPluginEnabled("OreWorkshop");
        this.enabledSlimefunTranslation = plugin.getServer().getPluginManager().isPluginEnabled("SlimefunTranslation");
        if (enabledSlimefunTranslation) {
            JustEnoughGuide.getInstance().getLogger().info(Lang.getStartup("hook-slimefun-translation"));
        }
    }
}
