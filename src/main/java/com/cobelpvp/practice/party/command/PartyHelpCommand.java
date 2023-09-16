package com.cobelpvp.practice.party.command;

import com.google.common.collect.ImmutableList;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.List;

public final class PartyHelpCommand {

    private static final List<String> HELP_MESSAGE = ImmutableList.of(
            ChatColor.GOLD + "---[" + ChatColor.GREEN + " Party Help " + ChatColor.GOLD +"]---",
            ChatColor.GOLD + "->" + ChatColor.DARK_GREEN +"Party Commands" + ChatColor.GOLD + "<-",
            ChatColor.YELLOW + "/party create:"+ ChatColor.GREEN +" Create a party",
            ChatColor.YELLOW + "/party invite:"+ ChatColor.GREEN +" Invite a player to join your party",
            ChatColor.YELLOW + "/party leave:"+  ChatColor.GREEN +" Leave your current party",
            ChatColor.YELLOW + "/party accept [player]:"+ ChatColor.GREEN +" Accept party invitation",
            ChatColor.YELLOW + "/party info [player]:"+ ChatColor.GREEN +" View the roster of the party",
            ChatColor.RED + "!To use" + ChatColor.YELLOW + " Party Chat " + ChatColor.RED + "prefix your message with the '" + ChatColor.GOLD + "@" + ChatColor.RED + "' sign.",
            "",
            ChatColor.GOLD + "->" + ChatColor.DARK_GREEN +"Party Leader Commands" + ChatColor.GOLD + "<-",
            ChatColor.YELLOW + "/party kick [player]:"+ ChatColor.GREEN +" Kick a player from your party",
            ChatColor.YELLOW + "/party leader [player]:"+ ChatColor.GREEN +" Transfer party leadership",
            ChatColor.YELLOW + "/party disband:"+ ChatColor.GREEN +" Disbands party",
            ChatColor.YELLOW + "/party lock:"+ ChatColor.GREEN +" Lock party from others joining",
            ChatColor.YELLOW + "/party open:"+ ChatColor.GREEN +" Open party to others joining",
            ChatColor.YELLOW + "/party password [password]:"+ ChatColor.GREEN +" Sets party password",
            ""

    );
    @Command(names = {"party", "p", "party help", "p help"}, permission = "")
    public static void party(Player sender) {
        HELP_MESSAGE.forEach(sender::sendMessage);
    }

}
