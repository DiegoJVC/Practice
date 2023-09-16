package com.cobelpvp.practice.party.listener;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.event.MatchSpectatorLeaveEvent;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.party.PartyHandler;
import com.cobelpvp.practice.party.PartyInvite;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.UUID;

public final class PartyLeaveListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();

        for (Party party : Practice.getInstance().getPartyHandler().getParties()) {
            if (party.isMember(playerUuid)) {
                party.leave(player);
            }

            PartyInvite invite = party.getInvite(playerUuid);

            if (invite != null) {
                party.revokeInvite(invite);
            }
        }
    }

    @EventHandler
    public void onMatchSpectatorLeave(MatchSpectatorLeaveEvent event) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(event.getSpectator());

        if (party != null) {
            party.leave(event.getSpectator());
        }
    }

}