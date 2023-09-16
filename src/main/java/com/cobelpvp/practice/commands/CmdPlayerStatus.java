package com.cobelpvp.practice.commands;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.follow.FollowHandler;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.match.MatchTeam;
import com.cobelpvp.practice.party.PartyHandler;
import com.cobelpvp.practice.lobby.queue.QueueHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class CmdPlayerStatus {

    @Command(names = {"playerstatus"}, permission = "practice.playerstatus")
    public static void pStatus(Player sender, @Param(name="target", defaultValue = "self") Player target) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        QueueHandler queueHandler = Practice.getInstance().getQueueHandler();
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        FollowHandler followHandler = Practice.getInstance().getFollowHandler();

        sender.sendMessage(ChatColor.RED + target.getName() + ":");
        sender.sendMessage("In match: " + matchHandler.isPlayingMatch(target));
        sender.sendMessage("In match (NC): " + noCacheIsPlayingMatch(target));
        sender.sendMessage("Spectating match: " + matchHandler.isSpectatingMatch(target));
        sender.sendMessage("Spectating match (NC): " + noCacheIsSpectatingMatch(target));
        sender.sendMessage("In or spectating match: " + matchHandler.isPlayingOrSpectatingMatch(target));
        sender.sendMessage("In or spectating match (NC): " + noCacheIsPlayingOrSpectatingMatch(target));
        sender.sendMessage("In queue: " + queueHandler.isQueued(target.getUniqueId()));
        sender.sendMessage("In party: " + partyHandler.hasParty(target));
        sender.sendMessage("Following: " + followHandler.getFollowing(target).isPresent());
    }

    private static boolean noCacheIsPlayingMatch(Player target) {
        for (Match match : Practice.getInstance().getMatchHandler().getHostedMatches()) {
            for (MatchTeam team : match.getTeams()) {
                if (team.isAlive(target.getUniqueId())) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean noCacheIsSpectatingMatch(Player target) {
        for (Match match : Practice.getInstance().getMatchHandler().getHostedMatches()) {
            if (match.isSpectator(target.getUniqueId())) {
                return true;
            }
        }

        return false;
    }

    private static boolean noCacheIsPlayingOrSpectatingMatch(Player target) {
        return noCacheIsPlayingMatch(target) || noCacheIsSpectatingMatch(target);
    }

}