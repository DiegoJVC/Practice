package com.cobelpvp.practice.lobby.queue;

import com.cobelpvp.practice.kittype.KitType;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.elo.EloHandler;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.match.MatchTeam;
import com.cobelpvp.practice.util.PatchedPlayerUtils;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;

public final class MatchQueue {

    @Getter private final KitType kitType;
    @Getter private final boolean ranked;
    private final List<MatchQueueEntry> entries = new CopyOnWriteArrayList<>();

    MatchQueue(KitType kitType, boolean ranked) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.ranked = ranked;
    }

    void tick() {
        List<MatchQueueEntry> entriesCopy = new ArrayList<>(entries);
        EloHandler eloHandler = Practice.getInstance().getEloHandler();

        if (ranked) {
            entriesCopy.sort(Comparator.comparing(e -> eloHandler.getElo(e.getMembers(), kitType)));
        }

        while (entriesCopy.size() >= 2) {
            MatchQueueEntry a = entriesCopy.remove(0);
            MatchQueueEntry b = entriesCopy.remove(0);

            if (ranked) {
                int aElo = eloHandler.getElo(a.getMembers(), kitType);
                int bElo = eloHandler.getElo(b.getMembers(), kitType);
                int aEloWindow = a.getWaitSeconds() * QueueHandler.RANKED_WINDOW_GROWTH_PER_SECOND;
                int bEloWindow = b.getWaitSeconds() * QueueHandler.RANKED_WINDOW_GROWTH_PER_SECOND;
                if (Math.abs(aElo - bElo) > Math.max(aEloWindow, bEloWindow)) {
                    continue;
                }
            }
            createMatchAndRemoveEntries(a, b);
        }
    }

    public int countPlayersQueued() {
        int count = 0;

        for (MatchQueueEntry entry : entries) {
            count += entry.getMembers().size();
        }

        return count;
    }

    void addToQueue(MatchQueueEntry entry) {
        entries.add(entry);
    }

    void removeFromQueue(MatchQueueEntry entry) {
        entries.remove(entry);
    }

    private void createMatchAndRemoveEntries(MatchQueueEntry entryA, MatchQueueEntry entryB) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        QueueHandler queueHandler = Practice.getInstance().getQueueHandler();

        MatchTeam teamA = new MatchTeam(entryA.getMembers());
        MatchTeam teamB = new MatchTeam(entryB.getMembers());

        Match match = matchHandler.startMatch(
                ImmutableList.of(teamA, teamB),
                kitType,
                ranked,
                !ranked,
                null
        );

        if (match != null) {
            queueHandler.removeFromQueueCache(entryA);
            queueHandler.removeFromQueueCache(entryB);
            String teamAElo = "";
            String teamBElo = "";

            if (ranked) {

                EloHandler eloHandler = Practice.getInstance().getEloHandler();
                teamAElo = " (" + eloHandler.getElo(teamA.getAliveMembers(), kitType) + ")";
                teamBElo = " (" + eloHandler.getElo(teamB.getAliveMembers(), kitType) + ")";
            }
            String foundStart = ChatColor.GREEN.toString() + kitType.getDisplayName() + ChatColor.YELLOW + " ranked queue match found: " + ChatColor.GREEN;
            teamA.messageAlive(foundStart + Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(teamA.getAllMembers())) + teamAElo + ChatColor.YELLOW + " vs " + ChatColor.GREEN + Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(teamB.getAllMembers())) + teamBElo);
            teamB.messageAlive(foundStart + Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(teamB.getAllMembers())) + teamBElo + ChatColor.YELLOW + " vs " + ChatColor.GREEN + Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(teamA.getAllMembers())) + teamAElo);
            entries.remove(entryA);
            entries.remove(entryB);
        }
    }

}
