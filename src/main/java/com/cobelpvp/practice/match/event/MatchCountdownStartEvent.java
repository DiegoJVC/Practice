package com.cobelpvp.practice.match.event;

import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchState;
import org.bukkit.event.HandlerList;
import lombok.Getter;

/**
 * Called when a match's countdown starts (when its {@link MatchState} changes
 * to {@link MatchState#COUNTDOWN})
 * @see MatchState#COUNTDOWN
 */
public final class MatchCountdownStartEvent extends MatchEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public MatchCountdownStartEvent(Match match) {
        super(match);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}