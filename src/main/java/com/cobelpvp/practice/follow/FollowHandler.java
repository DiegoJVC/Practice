package com.cobelpvp.practice.follow;

import com.cobelpvp.atheneum.nametag.TeamsNametagHandler;
import com.cobelpvp.atheneum.uuid.TeamsUUIDCache;
import com.cobelpvp.practice.follow.listener.FollowGeneralListener;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.match.MatchState;
import com.cobelpvp.practice.util.InventoryUtils;
import com.cobelpvp.practice.util.VisibilityUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class FollowHandler {

    private final Map<UUID, UUID> followingData = new ConcurrentHashMap<>();

    public FollowHandler() {
        Bukkit.getPluginManager().registerEvents(new FollowGeneralListener(this), Practice.getInstance());
    }

    public Optional<UUID> getFollowing(Player player) {
        return Optional.ofNullable(followingData.get(player.getUniqueId()));
    }

    public void startFollowing(Player player, Player target) {
        followingData.put(player.getUniqueId(), target.getUniqueId());
        player.sendMessage(ChatColor.WHITE + "Now following " + ChatColor.RED + target.getName() + ChatColor.WHITE + ", exit with /unfollow.");

        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Match targetMatch = matchHandler.getMatchPlayingOrSpectating(target);

        if (targetMatch != null && targetMatch.getState() != MatchState.ENDING) {
            targetMatch.addSpectator(player, target);
        } else {
            InventoryUtils.resetInventoryDelayed(player);
            VisibilityUtils.updateVisibility(player);
            TeamsNametagHandler.reloadOthersFor(player);

            player.teleport(target);
        }
    }

    public void stopFollowing(Player player) {
        UUID prevTarget = followingData.remove(player.getUniqueId());

        if (prevTarget != null) {
            player.sendMessage(ChatColor.WHITE + "Stopped following " + ChatColor.RED + TeamsUUIDCache.name(prevTarget) + ChatColor.WHITE + ".");
            InventoryUtils.resetInventoryDelayed(player);
            VisibilityUtils.updateVisibility(player);
            TeamsNametagHandler.reloadOthersFor(player);
        }
    }

    public Set<UUID> getFollowers(Player player) {
        Set<UUID> followers = new HashSet<>();

        followingData.forEach((follower, followed) -> {
            if (followed == player.getUniqueId()) {
                followers.add(follower);
            }
        });

        return followers;
    }

}