package com.cobelpvp.practice.match.command;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.practice.match.MatchHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ToggleMatchCommands {

    @Command(names = { "toggleMatches unranked" }, permission = "practice.togglematches")
    public static void toggleMatchesUnranked(Player sender) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        boolean newState = !matchHandler.isUnrankedMatchesDisabled();
        matchHandler.setUnrankedMatchesDisabled(newState);

        sender.sendMessage(ChatColor.YELLOW + "Unranked matches are now " + ChatColor.UNDERLINE + (newState ? "disabled" : "enabled") + ChatColor.YELLOW + ".");
    }

    @Command(names = { "toggleMatches ranked" }, permission = "practice.togglematches")
    public static void toggleMatchesRanked(Player sender) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        boolean newState = !matchHandler.isRankedMatchesDisabled();
        matchHandler.setRankedMatchesDisabled(newState);

        sender.sendMessage(ChatColor.YELLOW + "Ranked matches are now " + ChatColor.UNDERLINE + (newState ? "disabled" : "enabled") + ChatColor.YELLOW + ".");
    }

}