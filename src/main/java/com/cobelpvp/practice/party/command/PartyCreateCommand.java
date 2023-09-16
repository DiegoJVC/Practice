package com.cobelpvp.practice.party.command;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.practice.party.PartyHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyCreateCommand {

    @Command(names = {"party create", "p create"}, permission = "")
    public static void partyCreate(Player sender) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();

        if (partyHandler.hasParty(sender)) {
            sender.sendMessage(ChatColor.DARK_RED + "You are already in a party.");
            return;
        }
        partyHandler.getOrCreateParty(sender);
        sender.sendMessage(ChatColor.GREEN + "You've created a new party.");
    }

}
