package com.cobelpvp.practice.follow.listener;

import com.cobelpvp.practice.follow.FollowHandler;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchTeam;
import com.cobelpvp.practice.match.event.MatchCountdownStartEvent;
import com.cobelpvp.practice.match.event.MatchSpectatorLeaveEvent;
import com.cobelpvp.practice.lobby.setting.Setting;
import com.cobelpvp.practice.lobby.setting.event.SettingUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.UUID;

public final class FollowGeneralListener implements Listener {

    private final FollowHandler followHandler;

    public FollowGeneralListener(FollowHandler followHandler) {
        this.followHandler = followHandler;
    }

    @EventHandler
    public void onMatchStart(MatchCountdownStartEvent event) {
        Match match = event.getMatch();

        for (MatchTeam team : match.getTeams()) {
            for (UUID member : team.getAllMembers()) {
                Player memberBukkit = Bukkit.getPlayer(member);

                for (UUID follower : followHandler.getFollowers(memberBukkit)) {
                    match.addSpectator(Bukkit.getPlayer(follower), memberBukkit);
                }
            }
        }
    }
    @EventHandler
    public void onMatchSpectatorLeave(MatchSpectatorLeaveEvent event) {
        followHandler.stopFollowing(event.getSpectator());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (UUID follower : followHandler.getFollowers(event.getPlayer())) {
            followHandler.stopFollowing(Bukkit.getPlayer(follower));
        }

        followHandler.stopFollowing(event.getPlayer());
    }

    @EventHandler
    public void onSettingUpdate(SettingUpdateEvent event) {
        if (event.getSetting() != Setting.ALLOW_SPECTATORS || event.isEnabled()) {
            return;
        }

        for (UUID follower : followHandler.getFollowers(event.getPlayer())) {
            Player followerPlayer = Bukkit.getPlayer(follower);

            if (followerPlayer.isOp() || followerPlayer.hasMetadata("ModMode")) {
                continue;
            }

            followHandler.stopFollowing(followerPlayer);
        }
    }

}