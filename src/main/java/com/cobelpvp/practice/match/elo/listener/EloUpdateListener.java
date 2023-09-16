package com.cobelpvp.practice.match.elo.listener;

import com.cobelpvp.practice.util.uuid.UniqueIDCache;
import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchTeam;
import com.cobelpvp.practice.match.event.MatchEndEvent;
import com.cobelpvp.practice.match.event.MatchTerminateEvent;
import com.cobelpvp.practice.util.PatchedPlayerUtils;
import com.google.common.base.Joiner;
import com.cobelpvp.practice.match.elo.EloCalculator;
import com.cobelpvp.practice.match.elo.EloHandler;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.List;

public final class EloUpdateListener implements Listener {

    private static final String ELO_CHANGE_MESSAGE = ChatColor.translateAlternateColorCodes('&', "&eElo Changes: &a%s +%d (%d) &c%s -%d (%d)");

    private final EloHandler eloHandler;
    private final EloCalculator eloCalculator;

    public EloUpdateListener(EloHandler eloHandler, EloCalculator eloCalculator) {
        this.eloHandler = eloHandler;
        this.eloCalculator = eloCalculator;
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();
        KitType kitType = match.getKitType();
        List<MatchTeam> teams = match.getTeams();

        if (!match.isRanked() || teams.size() != 2 || match.getWinner() == null) {
            return;
        }

        MatchTeam winnerTeam = match.getWinner();
        MatchTeam loserTeam = teams.get(0) == winnerTeam ? teams.get(1) : teams.get(0);

        EloCalculator.Result result = eloCalculator.calculate(
            eloHandler.getElo(winnerTeam.getAllMembers(), kitType),
            eloHandler.getElo(loserTeam.getAllMembers(), kitType)
        );

        eloHandler.setElo(winnerTeam.getAllMembers(), kitType, result.getWinnerNew());
        eloHandler.setElo(loserTeam.getAllMembers(), kitType, result.getLoserNew());

        match.setEloChange(result);
    }

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        Match match = event.getMatch();
        EloCalculator.Result result = match.getEloChange();

        if (result == null) {
            return;
        }

        List<MatchTeam> teams = match.getTeams();
        MatchTeam winnerTeam = match.getWinner();
        MatchTeam loserTeam = teams.get(0) == winnerTeam ? teams.get(1) : teams.get(0);

        String winnerStr;
        String loserStr;

        if (winnerTeam.getAllMembers().size() == 1 && loserTeam.getAllMembers().size() == 1) {
            winnerStr = UniqueIDCache.name(winnerTeam.getFirstMember());
            loserStr = UniqueIDCache.name(loserTeam.getFirstMember());
        } else {
            winnerStr = Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(winnerTeam.getAllMembers()));
            loserStr = Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(loserTeam.getAllMembers()));
        }
        match.messageAll(String.format(ELO_CHANGE_MESSAGE, winnerStr, result.getWinnerGain(), result.getWinnerNew(), loserStr, -result.getLoserGain(), result.getLoserNew()));
    }

}