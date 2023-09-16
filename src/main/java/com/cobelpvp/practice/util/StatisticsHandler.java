package com.cobelpvp.practice.util;

import com.cobelpvp.practice.profile.Profile;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.event.MatchTerminateEvent;
import com.cobelpvp.practice.kittype.KitType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class StatisticsHandler implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMatchEnd(MatchTerminateEvent event) {
        Match match = event.getMatch();

        if (match.getKitType().equals(KitType.teamFight)) return;

        match.getWinningPlayers().forEach(uuid -> {

            Profile p = Practice.getInstance().getProfileManager().getProfile(uuid);
            p.setGamesWon(p.getGamesWon() + 1);
            p.setGamesPlayed(p.getGamesPlayed() + 1);
            p.save();

            Player player = Bukkit.getPlayer(uuid);
        });

        match.getLosingPlayers().forEach(uuid -> {

            Profile p = Practice.getInstance().getProfileManager().getProfile(uuid);
            p.setLoses(p.getLoses() + 1);
            p.setGamesPlayed(p.getGamesPlayed() + 1);
            p.save();
        });
    }
}
