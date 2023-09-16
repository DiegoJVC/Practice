package com.cobelpvp.practice.party.command;

import com.cobelpvp.practice.util.listener.PracticeLang;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.entity.Player;

public final class PartyLeaveCommand {

    @Command(names = {"party leave", "p leave"}, permission = "")
    public static void partyLeave(Player sender) {
        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PracticeLang.NOT_IN_PARTY);
        } else {
            party.leave(sender);
        }
    }

}