package com.balugaq.jeg.core.listeners;

import com.balugaq.jeg.api.groups.SearchGroup;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunItemRegistryFinalizedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SearchGroupInitListener implements Listener {
    /**
     * Initialize the SearchGroup
     *
     * @param event The event
     */
    @EventHandler
    public void onInit(SlimefunItemRegistryFinalizedEvent event) {
        SearchGroup.init();
    }
}
