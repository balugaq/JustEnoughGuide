package com.balugaq.jeg.api.objects.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.2
 */
@NullMarked
public class SlimefunTickEndEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    public SlimefunTickEndEvent() {
        super(true);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
