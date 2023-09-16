package com.cobelpvp.practice.party.event;

import com.cobelpvp.practice.party.command.PartyCreateCommand;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.party.PartyHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import lombok.Getter;

/**
 * Called when a {@link Party} is created.
 * @see PartyCreateCommand
 * @see PartyHandler#getOrCreateParty(Player)
 */
public final class PartyCreateEvent extends PartyEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public PartyCreateEvent(Party party) {
        super(party);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}