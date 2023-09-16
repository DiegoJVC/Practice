package com.cobelpvp.practice.commands;

import com.google.common.collect.ImmutableList;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.practice.match.MatchHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.List;

public final class CmdHelp {

    private static final List<String> HELP_MESSAGE_HEADER = ImmutableList.of(
            ChatColor.GOLD + "---[" + ChatColor.GREEN + " Practice Information " + ChatColor.GOLD +"]---"
    );

    private static final List<String> HELP_MESSAGE_LOBBY = ImmutableList.of(
            ChatColor.GOLD + "->" +ChatColor.DARK_GREEN +"Common Practice Commands" + ChatColor.GOLD + "<-",
            ChatColor.YELLOW + "/duel [player]:" + ChatColor.GREEN +" Challenge a player to a duel",
            ChatColor.YELLOW + "/party invite:"+ ChatColor.GREEN +" Invite a player to join your party",
            ChatColor.YELLOW + "/spectate [player]:" + ChatColor.GREEN + " Spectate a player in a match",
            "",
            ChatColor.GOLD + "->" + ChatColor.DARK_GREEN +"Common Party Commands" + ChatColor.GOLD + "<-",
            ChatColor.YELLOW + "/party create" + ChatColor.GREEN +" Create a party",
            ChatColor.YELLOW + "/party invite:"+ ChatColor.GREEN +" Invite a player to join your party",
            ChatColor.YELLOW + "/party accept [player]:"+ ChatColor.GREEN +" Accept party invitation",
            ChatColor.RED + "!If you need more help use" + ChatColor.YELLOW + " /party help"

    );

    private static final List<String> HELP_MESSAGE_MATCH = ImmutableList.of(
            ChatColor.GOLD + "->" + ChatColor.DARK_GREEN + "Solicitude Commands" + ChatColor.GOLD + "<-",
            ChatColor.YELLOW + "/report [player] [reason]:" + ChatColor.GREEN + " Report a player for violating the rules",
            ChatColor.YELLOW + "/request [message]:" + ChatColor.GREEN + " Request assistance from a staff member"
    );

    private static final List<String> HELP_MESSAGE_FOOTER = ImmutableList.of(
            "",
            ChatColor.GOLD + "->" + ChatColor.DARK_GREEN + "Official Media" + ChatColor.GOLD + "<-",
            Practice.getInstance().getDominantColor() == ChatColor.WHITE ? "§eDiscord §7- §ahttps://discord.gg/aUAKqkBkb9" : "§eDiscord §7- §ahttps://discord.gg/aUAKqkBkb9",
            Practice.getInstance().getDominantColor() == ChatColor.WHITE ? "§eStore §7- §ahttp://cobelpvp.tebex.io" : "§eStore §7- §ahttp://cobelpvp.tebex.io"
    );

    @Command(names = {"help", "?", "helpme"}, permission = "")
    public static void help(Player sender) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        HELP_MESSAGE_HEADER.forEach(sender::sendMessage);

        if (matchHandler.isPlayingOrSpectatingMatch(sender)) {
            HELP_MESSAGE_MATCH.forEach(sender::sendMessage);
        } else {
            HELP_MESSAGE_LOBBY.forEach(sender::sendMessage);
        }

        HELP_MESSAGE_FOOTER.forEach(sender::sendMessage);
    }

}
