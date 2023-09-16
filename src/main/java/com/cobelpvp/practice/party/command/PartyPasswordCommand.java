package com.cobelpvp.practice.party.command;

import com.cobelpvp.practice.util.listener.PracticeLang;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.party.PartyAccessRestriction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyPasswordCommand {

    @Command(names = {"party password", "p password"}, permission = "")
    public static void partyPassword(Player sender, @Param(name = "password") String password) {
        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PracticeLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
        } else {
            party.setAccessRestriction(PartyAccessRestriction.PASSWORD);
            party.setPassword(password);
            sender.sendMessage(ChatColor.GREEN + "Your party's password is now " + ChatColor.YELLOW + password + ChatColor.GREEN + ".");
        }
    }

}
