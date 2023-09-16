package com.cobelpvp.practice.match.postmatchinv;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchTeam;
import com.cobelpvp.practice.match.postmatchinv.listener.PostMatchInvGeneralListener;
import com.cobelpvp.practice.util.PatchedPlayerUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class PostMatchInvHandler {

    private static final String WINNER = ChatColor.YELLOW + "" + ChatColor.GRAY;

    private final Map<UUID, Map<UUID, PostMatchPlayer>> playerData = new ConcurrentHashMap<>();

    public PostMatchInvHandler() {
        Bukkit.getPluginManager().registerEvents(new PostMatchInvGeneralListener(), Practice.getInstance());
    }

    public void recordMatch(Match match) {
        saveInventories(match);
        messagePlayers(match);
    }

    private void saveInventories(Match match) {
        Map<UUID, PostMatchPlayer> matchPlayers = match.getPostMatchPlayers();

        for (MatchTeam team : match.getTeams()) {
            for (UUID member : team.getAliveMembers()) {
                playerData.put(member, matchPlayers);
            }
        }

        for (UUID spectator : match.getSpectators()) {
            playerData.put(spectator, matchPlayers);
        }
    }

    private void messagePlayers(Match match) {
        Map<UUID, Object[]> invMessages = new HashMap<>();

        BaseComponent[] spectatorLine;
        List<UUID> spectatorUuids = new ArrayList<>(match.getSpectators());

        spectatorUuids.removeIf(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            return player.hasMetadata("ModMode") || match.getPreviousTeam(uuid) != null;
        });

        if (!spectatorUuids.isEmpty()) {
            List<String> spectatorNames = PatchedPlayerUtils.mapToNames(spectatorUuids);
            spectatorNames.sort(String::compareToIgnoreCase);

            String firstFourNames = Joiner.on(", ").join(
                spectatorNames.subList(
                    0,
                    Math.min(spectatorNames.size(), 4)
                )
            );

            if (spectatorNames.size() > 4) {
                firstFourNames += " (+" + (spectatorNames.size() - 4) + " more)";
            }

            HoverEvent hover = new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                spectatorNames.stream()
                    .map(n -> new TextComponent(ChatColor.BLUE + n + '\n'))
                    .toArray(BaseComponent[]::new)
            );

            spectatorLine = new ComponentBuilder("Spectators (" + spectatorNames.size() + "): ").color(ChatColor.BLUE)
                .append(firstFourNames).color(ChatColor.GRAY).event(hover)
                .create();
        } else {

            spectatorLine = null;
        }

        createInvMessages(match, invMessages);
        invMessages.forEach((uuid, lines) -> {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                return;
            }

            for (Object line : lines) {
                if (line instanceof TextComponent[]) {
                    player.spigot().sendMessage((TextComponent[]) line);
                } else if (line instanceof TextComponent) {
                    player.spigot().sendMessage((TextComponent) line);
                } else if (line instanceof String) {
                    player.sendMessage((String) line);
                }
            }

            if (spectatorLine != null) {
                player.spigot().sendMessage(spectatorLine);
            }
        });
    }

    private void createInvMessages(Match match, Map<UUID, Object[]> invMessages) {
        List<MatchTeam> teams = match.getTeams();

        if (teams.size() != 2) {

            Object[] generic = PostMatchInvLang.genGenericInvs(teams);

            writeSpecInvMessages(match, invMessages, generic);
            writeTeamInvMessages(teams, invMessages, generic);
            return;
        }

        MatchTeam winnerTeam = match.getWinner();
        MatchTeam loserTeam = teams.get(0) == winnerTeam ? teams.get(1) : teams.get(0);

        if (winnerTeam.getAllMembers().size() == 1 && loserTeam.getAllMembers().size() == 1) {

            Object[] generic = PostMatchInvLang.gen1v1PlayerInvs(winnerTeam.getFirstMember(), loserTeam.getFirstMember());

            writeSpecInvMessages(match, invMessages, generic);
            writeTeamInvMessages(teams, invMessages, generic);
        } else {
            writeSpecInvMessages(match, invMessages, PostMatchInvLang.genSpectatorInvs(winnerTeam, loserTeam));
            writeTeamInvMessages(winnerTeam, invMessages, PostMatchInvLang.genTeamInvs(winnerTeam, winnerTeam, loserTeam));
            writeTeamInvMessages(loserTeam, invMessages, PostMatchInvLang.genTeamInvs(loserTeam, winnerTeam, loserTeam));
        }
    }

    private void writeTeamInvMessages(Iterable<MatchTeam> teams, Map<UUID, Object[]> messageMap, Object[] messages) {
        for (MatchTeam team : teams) {
            writeTeamInvMessages(team, messageMap, messages);
        }
    }

    private void writeTeamInvMessages(MatchTeam team, Map<UUID, Object[]> messageMap, Object[] messages) {
        for (UUID member : team.getAllMembers()) {

            if (messageMap.containsKey(member) || team.isAlive(member)) {
                messageMap.put(member, messages);
            }
        }
    }

    private void writeSpecInvMessages(Match match, Map<UUID, Object[]> messageMap, Object[] messages) {
        for (UUID spectator : match.getSpectators()) {
            messageMap.put(spectator, messages);
        }
    }

    public Map<UUID, PostMatchPlayer> getPostMatchData(UUID forWhom) {
        return playerData.getOrDefault(forWhom, ImmutableMap.of());
    }

    public void removePostMatchData(UUID forWhom) {
        playerData.remove(forWhom);
    }
}
