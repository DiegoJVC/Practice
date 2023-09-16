package com.cobelpvp.practice.match.arena.event;

import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.arena.Arena;
import org.bukkit.event.HandlerList;
import lombok.Getter;

/**
 * Called when an {@link Arena} is allocated for use by a
 * {@link Match}
 */
public final class ArenaAllocatedEvent extends ArenaEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public ArenaAllocatedEvent(Arena arena) {
        super(arena);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}