package com.cobelpvp.practice.commands;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.lobby.menu.StatisticsMenu;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.practice.match.MatchHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdLeaderboard {

    @Command(names = {"leaderboards", "lb", "elo"})
    public static void leaderboards(Player sender) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        if (matchHandler.isPlayingMatch(sender)) {
            sender.sendMessage(ChatColor.RED + "You can't do this while playing in a match.");
            return;
        }
        new StatisticsMenu().openMenu(sender);
    }

}
