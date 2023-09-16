package com.cobelpvp.practice.lobby.scoreboard;

import com.cobelpvp.atheneum.util.LinkedList;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import com.cobelpvp.atheneum.autoreboot.AutoRebootHandler;
import com.cobelpvp.atheneum.util.TimeUtils;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.elo.EloHandler;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.party.PartyHandler;
import com.cobelpvp.practice.lobby.queue.MatchQueue;
import com.cobelpvp.practice.lobby.queue.MatchQueueEntry;
import com.cobelpvp.practice.lobby.queue.QueueHandler;
import com.cobelpvp.practice.lobby.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

final class LobbyScoreGetter implements BiConsumer<Player, LinkedList<String>> {

    @Override
    public void accept(Player player, LinkedList<String> scores) {
        Optional<UUID> followingOpt = Practice.getInstance().getFollowHandler().getFollowing(player);
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        QueueHandler queueHandler = Practice.getInstance().getQueueHandler();
        EloHandler eloHandler = Practice.getInstance().getEloHandler();
        Party playerParty = partyHandler.getParty(player);

        scores.add("&aOnline: &f" + Practice.getInstance().getCache().getOnlineCount());
        if (playerParty != null) {
            int size = playerParty.getMembers().size();
            scores.add("&9Your Party: &f" + size);
        }
        scores.add("");
        scores.add("&6cobelpvp.com");

        if (followingOpt.isPresent()) {
            Player following = Bukkit.getPlayer(followingOpt.get());
            scores.add("&aFollowing: *&f" + following.getName());

            if (player.hasPermission("practice.staff")) {
                MatchQueueEntry targetEntry = getQueueEntry(following);

                if (targetEntry != null) {
                    MatchQueue queue = targetEntry.getQueue();
                    scores.add("&eTarget queue:");
                    scores.add("&f" + (queue.isRanked() ? ChatColor.GREEN + "Ranked" + " " + queue.getKitType().getDisplayName() : ChatColor.BLUE + "Unranked" + " " + queue.getKitType().getDisplayName()));
                }
            }
        }

        MatchQueueEntry entry = getQueueEntry(player);

        if (entry != null) {
            String waitTimeFormatted = TimeUtils.formatIntoMMSS(entry.getWaitSeconds());
            MatchQueue queue = entry.getQueue();
            scores.add("&b&7&m--------------------");
            scores.add(queue.getKitType().getDisplayColor() + (queue.isRanked() ? ChatColor.GREEN + "Ranked" + " " + queue.getKitType().getDisplayName() : ChatColor.BLUE + "Unranked" + " " + queue.getKitType().getDisplayName()));
            scores.add("&eTime: *&f" + waitTimeFormatted);

            if (queue.isRanked()) {
                int elo = eloHandler.getElo(entry.getMembers(), queue.getKitType());
                int window = entry.getWaitSeconds() * QueueHandler.RANKED_WINDOW_GROWTH_PER_SECOND;
                scores.add("&eSearch range: *&f" + Math.max(0, elo - window) + " - " + (elo + window));
            }
        }

        if (player.hasMetadata("ModMode")) {
            scores.add(ChatColor.GRAY.toString() +"Silent Mode");
        }

        Tournament tournament = Practice.getInstance().getTournamentHandler().getTournament();
        if (tournament != null) {
            scores.add("&7&m--------------------");
            scores.add("&6&lTournament");

            if (tournament.getStage() == Tournament.TournamentStage.WAITING_FOR_TEAMS) {
                int teamSize = tournament.getRequiredPartySize();
                scores.add("&aKit&7: " + tournament.getType().getDisplayName());
                scores.add("&eTeam Size:&f " + teamSize + "v" + teamSize);
                int multiplier = teamSize < 3 ? teamSize : 1;
                scores.add("&e" + (teamSize < 3 ? "Players"  : "Teams") + ":&f " + (tournament.getActiveParties().size() * multiplier + "/" + tournament.getRequiredPartiesToStart() * multiplier));
            } else if (tournament.getStage() == Tournament.TournamentStage.COUNTDOWN) {
                if (tournament.getCurrentRound() == 0) {
                    scores.add("&9");
                    scores.add("&eBegins in &c" + tournament.getBeginNextRoundIn() + "&e second" + (tournament.getBeginNextRoundIn() == 1 ? "." : "s."));
                } else {
                    scores.add("&9");
                    scores.add("&e&lRound:&f " + (tournament.getCurrentRound() + 1));
                    scores.add("&eBegins in &c" + tournament.getBeginNextRoundIn() + "&e second" + (tournament.getBeginNextRoundIn() == 1 ? "." : "s."));
                }
            } else if (tournament.getStage() == Tournament.TournamentStage.IN_PROGRESS) {
                scores.add("&eRound:&f " + tournament.getCurrentRound());
                int teamSize = tournament.getRequiredPartySize();
                int multiplier = teamSize < 3 ? teamSize : 1;
                scores.add("&e" + (teamSize < 3 ? "Players" : "Teams") + ":&f " + tournament.getActiveParties().size() * multiplier + "/" + tournament.getRequiredPartiesToStart() * multiplier);
                scores.add("&eDuration:&f " + TimeUtils.formatIntoMMSS((int) (System.currentTimeMillis() - tournament.getRoundStartedAt()) / 1000));
            }
        }

        if (AutoRebootHandler.isRebooting()) {
            scores.add("&4&lRebooting: &4" + TimeUtils.formatIntoMMSS(AutoRebootHandler.getRebootSecondsRemaining()));
        }
        
    }

    private MatchQueueEntry getQueueEntry(Player player) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        QueueHandler queueHandler = Practice.getInstance().getQueueHandler();

        Party playerParty = partyHandler.getParty(player);
        if (playerParty != null) {
            return queueHandler.getQueueEntry(playerParty);
        } else {
            return queueHandler.getQueueEntry(player.getUniqueId());
        }
    }

}