package com.cobelpvp.practice.match.postmatchinv.listener;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.MatchTeam;
import com.cobelpvp.practice.match.event.MatchCountdownStartEvent;
import com.cobelpvp.practice.match.event.MatchTerminateEvent;
import com.cobelpvp.practice.match.postmatchinv.PostMatchInvHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.UUID;

public final class PostMatchInvGeneralListener implements Listener {

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        PostMatchInvHandler postMatchInvHandler = Practice.getInstance().getPostMatchInvHandler();
        postMatchInvHandler.recordMatch(event.getMatch());
    }

    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        PostMatchInvHandler postMatchInvHandler = Practice.getInstance().getPostMatchInvHandler();

        for (MatchTeam team : event.getMatch().getTeams()) {
            for (UUID member : team.getAllMembers()) {
                postMatchInvHandler.removePostMatchData(member);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PostMatchInvHandler postMatchInvHandler = Practice.getInstance().getPostMatchInvHandler();
        UUID playerUuid = event.getPlayer().getUniqueId();
        postMatchInvHandler.removePostMatchData(playerUuid);
    }

}