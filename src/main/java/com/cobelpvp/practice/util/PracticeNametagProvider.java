package com.cobelpvp.practice.util;

import com.cobelpvp.atheneum.nametag.NametagInfo;
import com.cobelpvp.atheneum.nametag.NametagProvider;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.follow.FollowHandler;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.match.MatchTeam;
import com.cobelpvp.practice.kit.pvpclasses.pvpclasses.ArcherClass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.cobelpvp.engine.profile.Profile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class PracticeNametagProvider extends NametagProvider {

    public PracticeNametagProvider() {
        super("Practice Provider", 5);
    }

    @Override
    public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
        String prefixColor = getNameColor(toRefresh, refreshFor);
        return createNametag(prefixColor, "");
    }

    public static String getNameColor(Player toRefresh, Player refreshFor) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (matchHandler.isPlayingOrSpectatingMatch(toRefresh)) {
            return ChatColor.translateAlternateColorCodes('&', getNameColorMatch(toRefresh, refreshFor).toString());
        } else {
            return ChatColor.translateAlternateColorCodes('&', getNameColorLobby(toRefresh, refreshFor));
        }
    }

    private static ChatColor getNameColorMatch(Player toRefresh, Player refreshFor) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        Match toRefreshMatch = matchHandler.getMatchPlayingOrSpectating(toRefresh);
        MatchTeam toRefreshTeam = toRefreshMatch.getTeam(toRefresh.getUniqueId());

        if (toRefreshTeam == null) {
            return ChatColor.GRAY;
        }

        MatchTeam refreshForTeam = toRefreshMatch.getTeam(refreshFor.getUniqueId());

        if (refreshForTeam == null) {
            refreshForTeam = toRefreshMatch.getPreviousTeam(refreshFor.getUniqueId());
        }

        if (refreshForTeam != null) {
            if (toRefreshTeam == refreshForTeam) {
                return ChatColor.GREEN;
            } else {
                if (ArcherClass.getMarkedPlayers().containsKey(toRefresh.getName()) && System.currentTimeMillis() < ArcherClass.getMarkedPlayers().get(toRefresh.getName())) {
                    return ChatColor.DARK_RED;
                }
                return ChatColor.WHITE;
            }
        }

        List<MatchTeam> teams = toRefreshMatch.getTeams();

        if (teams.size() == 2) {
            if (toRefreshTeam == teams.get(0)) {
                return ChatColor.RED;
            } else {
                return ChatColor.GREEN;
            }
        } else {
            return ChatColor.WHITE;
        }
    }

    private static String getNameColorLobby(Player toRefresh, Player refreshFor) {
        FollowHandler followHandler = Practice.getInstance().getFollowHandler();

        Optional<UUID> following = followHandler.getFollowing(refreshFor);
        boolean refreshForFollowingTarget = following.isPresent() && following.get().equals(toRefresh.getUniqueId());

        if (refreshForFollowingTarget) {
            return ChatColor.GREEN.toString();
        } else {
            Profile profile = Profile.getByUsername(toRefresh.getName());
            return profile.getActiveRank().getGameColor();
        }
    }

}