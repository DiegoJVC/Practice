package com.cobelpvp.practice.match.event;

import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchState;
import org.bukkit.event.HandlerList;
import lombok.Getter;

/**
 * Called when a match is terminated (when its {@link MatchState} changes
 * to {@link MatchState#TERMINATED})
 * @see MatchState#TERMINATED
 */
public final class MatchTerminateEvent extends MatchEvent {

    @Getter private static HandlerList handlerList = new HandlerList();


    public MatchTerminateEvent(Match match) {
        super(match);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}