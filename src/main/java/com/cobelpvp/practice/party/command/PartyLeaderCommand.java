package com.cobelpvp.practice.party.command;

import com.cobelpvp.practice.util.listener.PracticeLang;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.practice.party.Party;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyLeaderCommand {

    @Command(names = {"party leader", "p leader"}, permission = "")
    public static void partyLeader(Player sender, @Param(name = "player") Player target) {
        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PracticeLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
        } else if (!party.isMember(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " isn't in your party.");
        } else if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You can't promote yourself to the leader of your own party.");
        } else {
            party.setLeader(target);
        }
    }

}