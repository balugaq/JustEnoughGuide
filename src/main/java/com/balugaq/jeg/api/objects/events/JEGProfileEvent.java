package com.balugaq.jeg.api.objects.events;

import com.balugaq.jeg.core.profiler.TimedSample;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Queue;

/**
 * @author balugaq
 * @since 2.2
 */
@NullMarked
public class JEGProfileEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    final Queue<TimedSample> samples;
    final Map<CommandSender, Boolean> waiting;

    public JEGProfileEvent(Queue<TimedSample> samples, Map<CommandSender, Boolean> waiting) {
        super(true);
        this.samples = samples;
        this.waiting = waiting;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
