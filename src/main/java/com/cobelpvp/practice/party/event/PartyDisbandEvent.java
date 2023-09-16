package com.cobelpvp.practice.party.event;

import com.cobelpvp.practice.party.command.PartyDisbandCommand;
import com.cobelpvp.practice.party.Party;
import org.bukkit.event.HandlerList;
import lombok.Getter;

/**
 * Called when a {@link Party} is disbanded.
 * @see PartyDisbandCommand
 * @see Party#disband()
 */
public final class PartyDisbandEvent extends PartyEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public PartyDisbandEvent(Party party) {
        super(party);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}