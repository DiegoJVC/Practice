package com.cobelpvp.practice.lobby.tournament;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchState;
import com.cobelpvp.practice.match.MatchTeam;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.util.PatchedPlayerUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.practice.lobby.setting.Setting;
import com.cobelpvp.practice.lobby.setting.SettingHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;
import java.util.stream.Collectors;

public class Tournament {

    @Getter private int currentRound = -1;
    @Getter private int requiredPartiesToStart;

    @Getter private List<Party> activeParties = Lists.newArrayList();
    private List<Party> lost = Lists.newArrayList();

    @Getter private int requiredPartySize;
    @Getter private KitType type;

    @Getter private List<Match> matches = Lists.newArrayList();

    @Getter private int beginNextRoundIn = 31;

    private Map<UUID, Party> partyMap = Maps.newHashMap();

    @Getter private TournamentStage stage = TournamentStage.WAITING_FOR_TEAMS;

    @Getter private long roundStartedAt;

    public Tournament(KitType type, int partySize, int requiredPartiesToStart) {
        this.type = type;
        this.requiredPartySize = partySize;
        this.requiredPartiesToStart = requiredPartiesToStart;
    }

    public void addParty(Party party) {
        activeParties.add(party);
        checkActiveParties();
        joinedTournament(party);
        checkStart();
    }

    public boolean isInTournament(Party party) {
        return activeParties.contains(party);
    }

    public void check() {
        checkActiveParties();
        populatePartyMap();
        checkMatches();

        if (matches.stream().anyMatch(s -> s != null && s.getState() != MatchState.TERMINATED)) return;
        matches.clear();

        if (currentRound == -1) return;

        if (activeParties.isEmpty()) {
            if (lost.isEmpty()) {
                stage = TournamentStage.FINISHED;
                Practice.getInstance().getTournamentHandler().setTournament(null);
                return;
            }

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&cThe tournament's last two teams forfeited. Winner by default: " + ChatColor.GOLD + PatchedPlayerUtils.getFormattedName((lost.get(lost.size() - 1)).getLeader()) + "&c's team!"));
            Practice.getInstance().getTournamentHandler().setTournament(null);
            stage = TournamentStage.FINISHED;
            return;
        }

        if (activeParties.size() == 1) {
            Party party = activeParties.get(0);
            if (party.getMembers().size() == 1) {
                repeatMessage(ChatColor.translateAlternateColorCodes('&', "&6&l" + PatchedPlayerUtils.getFormattedName(party.getLeader()) + " &ewon the tournament!"), 4, 2);
            } else if (party.getMembers().size() == 2) {
                Iterator<UUID> membersIterator = party.getMembers().iterator();
                UUID[] members = new UUID[] { membersIterator.next(), membersIterator.next() };
                repeatMessage(ChatColor.translateAlternateColorCodes('&', "&6&l" + PatchedPlayerUtils.getFormattedName(members[0]) + " &eand &6&l" + PatchedPlayerUtils.getFormattedName(members[1]) + " &ewon the tournament!"), 4, 2);
            } else {
                repeatMessage(ChatColor.translateAlternateColorCodes('&', "&6&l" + PatchedPlayerUtils.getFormattedName(party.getLeader()) + "&e's team won the tournament!"), 4, 2);
            }

            activeParties.clear();
            Practice.getInstance().getTournamentHandler().setTournament(null);
            stage = TournamentStage.FINISHED;
            return;
        }

        if (--beginNextRoundIn >= 1) {
            switch (beginNextRoundIn) {
            case 30:
            case 15:
            case 10:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
                if (currentRound == 0) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eThe &a&ltournament &ewill begin in &6" + beginNextRoundIn + " &esecond" + (beginNextRoundIn == 1 ? "" : "s") + "."));
                } else {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&lRound " + (currentRound + 1) + " &ewill begin in &6" + beginNextRoundIn + " &esecond" + (beginNextRoundIn == 1 ? "" : "s") + "."));
                }
            }

            if (beginNextRoundIn == 30 && currentRound == 0) {

            }

            stage = TournamentStage.COUNTDOWN;
            return;
        }

        startRound();
    }

    private void checkActiveParties() {
        Set<UUID> realParties = Practice.getInstance().getPartyHandler().getParties().stream().map(p -> p.getPartyId()).collect(Collectors.toSet());
        Iterator<Party> activePartyIterator = activeParties.iterator();
        while (activePartyIterator.hasNext()) {
            Party activeParty = activePartyIterator.next();
            if (!realParties.contains(activeParty.getPartyId())) {
                activePartyIterator.remove();

                if (!lost.contains(activeParty)) {
                    lost.add(activeParty);
                }
            }
        }
    }

    private void repeatMessage(String message, int times, int interval) {
        new BukkitRunnable() {

            private int runs = times;

            @Override
            public void run() {
                if (0 <= --runs) {
                    Bukkit.broadcastMessage(message);
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(Practice.getInstance(), 0, interval * 20);
    }

    public void checkStart() {
        if (activeParties.size() == requiredPartiesToStart) {
            start();
        }
    }

    public void start() {
        if (currentRound == -1) {
            currentRound = 0;
        }
    }

    private void joinedTournament(Party party) {
        broadcastJoinMessage(party);
    }

    private void populatePartyMap() {
        activeParties.forEach(p -> p.getMembers().forEach(u -> {
            partyMap.put(u, p);
        }));
    }

    private void startRound() {
        beginNextRoundIn = 31;
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&lRound " + ++currentRound + " &ehas started. Good luck!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eUse &a/status &eto see who is fighting."));

        List<Party> oldPartyList = Lists.newArrayList(activeParties);

        while (1 < oldPartyList.size()) {
            Party firstParty = oldPartyList.remove(0);
            Party secondParty = oldPartyList.remove(0);

            matches.add(Practice.getInstance().getMatchHandler().startMatch(ImmutableList.of(new MatchTeam(firstParty.getMembers()), new MatchTeam(secondParty.getMembers())), type, false, false, null));
        }

        if (oldPartyList.size() == 1) {
            oldPartyList.get(0).message(ChatColor.RED + "There were an odd number of teams in this round - so your team has advanced to the next round.");
        }

        stage = TournamentStage.IN_PROGRESS;
        roundStartedAt = System.currentTimeMillis();
    }

    private void checkMatches() {
        Iterator<Match> matchIterator = matches.iterator();
        while (matchIterator.hasNext()) {
            Match match = matchIterator.next();
            if (match == null) {
                matchIterator.remove();
                continue;
            }

            if (match.getState() != MatchState.TERMINATED) continue;
            MatchTeam winner = match.getWinner();
            List<MatchTeam> losers = Lists.newArrayList(match.getTeams());
            losers.remove(winner);
            MatchTeam loser = losers.get(0);
            Party loserParty = partyMap.get(loser.getFirstMember());
            if (loserParty != null) {
                activeParties.remove(loserParty);
                broadcastEliminationMessage(loserParty);
                lost.add(loserParty);
                matchIterator.remove();
            }
        }
    }

    public void broadcastJoinMessage() {
        int teamSize = this.getRequiredPartySize();
        int requiredTeams = this.getRequiredPartiesToStart();

        int multiplier = teamSize < 3 ? teamSize : 1;

        if (this.getCurrentRound() != -1) return;

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eA &a&ltournament&e has started. Type &a/join&e to play. (" + (this.activeParties.size() * multiplier) + "/" + (requiredTeams * multiplier) + ")"));
        Bukkit.broadcastMessage("");
    }

    private void broadcastJoinMessage(Party joiningParty) {
        if (getCurrentRound() != -1) {
            FancyMessage message;
            if (joiningParty.getMembers().size() == 1) {
                message = new FancyMessage(ChatColor.translateAlternateColorCodes('&',  "&e&lDONOR ONLY &e- " + ChatColor.GOLD + PatchedPlayerUtils.getFormattedName(joiningParty.getLeader()) + "&e has joined the &atournament&e. &e(" + activeParties.size() + "/" + requiredPartiesToStart + "&e)"));
            } else if (joiningParty.getMembers().size() == 2) {
                Iterator<UUID> membersIterator = joiningParty.getMembers().iterator();
                message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&e&lDONOR ONLY &e- " + ChatColor.GOLD + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&e and &6" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&e have joined the &atournament&e. &e(" + activeParties.size() * 2 + "/" + requiredPartiesToStart * 2 + "&e)"));
            } else {
                message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&e&lDONOR ONLY &e- " + ChatColor.GOLD + PatchedPlayerUtils.getFormattedName(joiningParty.getLeader()) + "&e's team has joined the &atournament&e. &e(" + activeParties.size() + "/" + requiredPartiesToStart + "&e)"));
            }

            message.tooltip(ChatColor.translateAlternateColorCodes('&', "&6&lDonators can join during the tournament countdown. &ePurchase a rank at &a " + (Practice.getInstance().getDominantColor() == ChatColor.LIGHT_PURPLE ? "store.cobelpvp.com" : "store.cobelpvp.com") +  " &e."));
            Bukkit.getOnlinePlayers().forEach(message::send);
            return;
        }

        FancyMessage message;
        if (joiningParty.getMembers().size() == 1) {
            message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&6" + PatchedPlayerUtils.getFormattedName(joiningParty.getLeader()) + "&e has joined the &atournament&e. &e(" + activeParties.size() + "/" + requiredPartiesToStart + "&e)"));
        } else if (joiningParty.getMembers().size() == 2) {
            Iterator<UUID> membersIterator = joiningParty.getMembers().iterator();
            message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&6" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&e and &6" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&e have joined the &atournament&e. &e(" + activeParties.size() * 2 + "/" + requiredPartiesToStart * 2 + "&e)"));
        } else {
            message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&6" + PatchedPlayerUtils.getFormattedName(joiningParty.getLeader()) + "&e's team has joined the &atournament&e. &e(" + activeParties.size() + "/" + requiredPartiesToStart + "&e)"));
        }
        
        message.command("/djm");
        message.tooltip(ChatColor.translateAlternateColorCodes('&', "&c&lCLICK to hide this message."));

        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (joiningParty.isMember(player.getUniqueId()) || settingHandler.getSetting(player, Setting.SEE_TOURNAMENT_JOIN_MESSAGE)) {
                message.send(player);
            }
        }
    }

    private void broadcastEliminationMessage(Party loserParty) {
        FancyMessage message;
        int multiplier = requiredPartySize < 3 ? requiredPartySize : 1;
        if (loserParty.getMembers().size() == 1) {
            message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&4" + PatchedPlayerUtils.getFormattedName(loserParty.getLeader()) + "&c has been eliminated. &c(" + activeParties.size() * multiplier + "/" + requiredPartiesToStart * multiplier + "&c)"));
        } else if (loserParty.getMembers().size() == 2) {
            Iterator<UUID> membersIterator = loserParty.getMembers().iterator();
            message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&4" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&c and &4" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&c were eliminated. &c(" + activeParties.size() * multiplier + "/" + requiredPartiesToStart * multiplier + "&c)"));
        } else {
            message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&4" + PatchedPlayerUtils.getFormattedName(loserParty.getLeader()) + "&c's team has been eliminated. &c(" + activeParties.size() * multiplier + "/" + requiredPartiesToStart * multiplier + "&c)"));
        }

        message.command("/dem");
        message.tooltip(ChatColor.translateAlternateColorCodes('&', "&c&lCLICK to hide this message."));
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();


        for (Player player : Bukkit.getOnlinePlayers()) {
            if (loserParty.isMember(player.getUniqueId()) || settingHandler.getSetting(player, Setting.SEE_TOURNAMENT_ELIMINATION_MESSAGES)) {
                message.send(player);
            }
        }
    }

    @Command(names = { "djm" }, permission = "")
    public static void joinMessages(Player sender) {
        boolean oldValue = Practice.getInstance().getSettingHandler().getSetting(sender, Setting.SEE_TOURNAMENT_JOIN_MESSAGE);
        if (!oldValue) {
            Practice.getInstance().getSettingHandler().updateSetting(sender, Setting.SEE_TOURNAMENT_JOIN_MESSAGE, true);
            sender.sendMessage(ChatColor.GREEN + "Enabled tournament join messages.");
            return;
        }
        Practice.getInstance().getSettingHandler().updateSetting(sender, Setting.SEE_TOURNAMENT_JOIN_MESSAGE, false);
        sender.sendMessage(ChatColor.RED + "Disabled tournament join messages.");
    }

    @Command(names = { "dem" }, permission = "")
    public static void eliminationMessages(Player sender) {
        boolean oldValue = Practice.getInstance().getSettingHandler().getSetting(sender, Setting.SEE_TOURNAMENT_ELIMINATION_MESSAGES);
        if (!oldValue) {
            Practice.getInstance().getSettingHandler().updateSetting(sender, Setting.SEE_TOURNAMENT_ELIMINATION_MESSAGES, true);
            sender.sendMessage(ChatColor.GREEN + "Enabled tournament elimination messages.");
            return;
        }
        Practice.getInstance().getSettingHandler().updateSetting(sender, Setting.SEE_TOURNAMENT_ELIMINATION_MESSAGES, false);
        sender.sendMessage(ChatColor.RED + "Disabled tournament elimination messages.");
    }
    public enum TournamentStage {
        WAITING_FOR_TEAMS,
        COUNTDOWN,
        IN_PROGRESS,
        FINISHED
    }
}
