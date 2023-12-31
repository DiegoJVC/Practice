package com.cobelpvp.practice.match.listener;

import com.cobelpvp.practice.party.event.PartyMemberJoinEvent;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class MatchPartySpectateListener implements Listener {

    @EventHandler
    public void onPartyMemberJoin(PartyMemberJoinEvent event) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Match leaderMatch = matchHandler.getMatchPlayingOrSpectating(Bukkit.getPlayer(event.getParty().getLeader()));
        if (leaderMatch != null) {
            leaderMatch.addSpectator(event.getMember(), null);
        }
    }

}