package com.cobelpvp.practice.party.command;

import com.cobelpvp.practice.util.listener.PracticeLang;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.party.PartyAccessRestriction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyLockCommand {

    @Command(names = {"party lock", "p lock"}, permission = "")
    public static void partyLock(Player sender) {
        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PracticeLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
        } else if (party.getAccessRestriction() == PartyAccessRestriction.INVITE_ONLY) {
            sender.sendMessage(ChatColor.RED + "Your party is already locked.");
        } else {
            party.setAccessRestriction(PartyAccessRestriction.INVITE_ONLY);
            sender.sendMessage(ChatColor.GREEN + "Your party is now " + ChatColor.YELLOW + "locked" + ChatColor.GREEN + ".");
        }
    }

}
