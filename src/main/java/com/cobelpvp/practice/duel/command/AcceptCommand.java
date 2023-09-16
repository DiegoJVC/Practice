package com.cobelpvp.practice.duel.command;

import com.cobelpvp.practice.util.uuid.UniqueIDCache;
import com.cobelpvp.practice.duel.PlayerDuelInvite;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.match.MatchTeam;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.party.PartyHandler;
import com.google.common.collect.ImmutableList;
import com.cobelpvp.practice.util.listener.PracticeLang;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.duel.DuelHandler;
import com.cobelpvp.practice.duel.DuelInvite;
import com.cobelpvp.practice.duel.PartyDuelInvite;
import com.cobelpvp.practice.util.PracticeValidation;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class AcceptCommand {

    @Command(names = {"accept"}, permission = "")
    public static void accept(Player sender, @Param(name = "player") Player target) {
        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You can't accept a duel from yourself!");
            return;
        }

        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();

        Party senderParty = partyHandler.getParty(sender);
        Party targetParty = partyHandler.getParty(target);

        if (senderParty != null && targetParty != null) {
            PartyDuelInvite invite = duelHandler.findInvite(targetParty, senderParty);

            if (invite != null) {
                acceptParty(sender, senderParty, targetParty, invite);
            } else {
                String leaderName = UniqueIDCache.name(targetParty.getLeader());
                sender.sendMessage(ChatColor.RED + "Your party doesn't have a duel invite from " + leaderName + "'s party.");
            }
        } else if (senderParty == null && targetParty == null) {
            PlayerDuelInvite invite = duelHandler.findInvite(target, sender);

            if (invite != null) {
                acceptPlayer(sender, target, invite);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have a duel invite from " + target.getName() + ".");
            }
        } else if (senderParty == null) {
            sender.sendMessage(ChatColor.RED + "You don't have a duel invite from " + target.getName() + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Your party doesn't have a duel invite from " + target.getName() + "'s party.");
        }
    }

    private static void acceptParty(Player sender, Party senderParty, Party targetParty, DuelInvite invite) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();

        if (!senderParty.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
            return;
        }

        if (!PracticeValidation.canAcceptDuel(senderParty, targetParty, sender)) {
            return;
        }

        Match match = matchHandler.startMatch(
                ImmutableList.of(new MatchTeam(senderParty.getMembers()), new MatchTeam(targetParty.getMembers())),
                invite.getKitType(),
                false,
                true,
                invite.getArena()
        );

        if (match != null) {
            duelHandler.removeInvite(invite);
        } else {
            senderParty.message(PracticeLang.ERROR_WHILE_STARTING_MATCH);
            targetParty.message(PracticeLang.ERROR_WHILE_STARTING_MATCH);
        }
    }

    private static void acceptPlayer(Player sender, Player target, DuelInvite invite) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();

        if (!PracticeValidation.canAcceptDuel(sender, target)) {
            return;
        }

        Match match = matchHandler.startMatch(
                ImmutableList.of(new MatchTeam(sender.getUniqueId()), new MatchTeam(target.getUniqueId())),
                invite.getKitType(),
                false,
                true,
                invite.getArena()
        );

        if (match != null) {
            duelHandler.removeInvite(invite);
        } else {
            sender.sendMessage(PracticeLang.ERROR_WHILE_STARTING_MATCH);
            target.sendMessage(PracticeLang.ERROR_WHILE_STARTING_MATCH);
        }
    }

}