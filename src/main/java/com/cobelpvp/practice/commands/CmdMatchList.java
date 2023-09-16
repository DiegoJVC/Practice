package com.cobelpvp.practice.commands;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class CmdMatchList {

    @Command(names = { "match list" }, permission = "practice.matchlist")
    public static void matchList(Player sender) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        for (Match match : matchHandler.getHostedMatches()) {
            sender.sendMessage(ChatColor.RED + match.getSimpleDescription(true));
        }
    }

}