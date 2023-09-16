package com.cobelpvp.practice.party.command;

import com.cobelpvp.practice.util.listener.PracticeLang;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.party.PartyHandler;
import com.cobelpvp.practice.party.PartyUtils;
import org.bukkit.entity.Player;

public final class PartyTeamSplitCommand {

    @Command(names = {"party teamsplit", "p teamsplit"}, permission = "")
    public static void partyTeamSplit(Player sender) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (party == null) {
            sender.sendMessage(PracticeLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
        } else {
            PartyUtils.startTeamSplit(party, sender);
        }
    }

}