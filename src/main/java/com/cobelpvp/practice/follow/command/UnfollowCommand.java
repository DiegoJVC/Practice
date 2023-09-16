package com.cobelpvp.practice.follow.command;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.follow.FollowHandler;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class UnfollowCommand {

    @Command(names={"unfollow"}, permission="")
    public static void unfollow(Player sender) {
        FollowHandler followHandler = Practice.getInstance().getFollowHandler();
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (!followHandler.getFollowing(sender).isPresent()) {
            sender.sendMessage(ChatColor.RED + "You're not following anybody.");
            return;
        }

        Match spectating = matchHandler.getMatchSpectating(sender);

        if (spectating != null) {
            spectating.removeSpectator(sender);
        }

        followHandler.stopFollowing(sender);
    }

}