package com.cobelpvp.practice.party;

import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.practice.kittype.menu.select.SelectKitTypeMenu;
import com.cobelpvp.practice.party.menu.oddmanout.OddManOutMenu;
import com.google.common.collect.ImmutableList;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchTeam;
import com.cobelpvp.practice.util.PracticeValidation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PartyUtils {

    public static void startTeamSplit(Party party, Player initiator) {

        if (!PracticeValidation.canStartTeamSplit(party, initiator)) {
            return;
        }

        new SelectKitTypeMenu(kitType -> {
            initiator.closeInventory();

            if (party.getMembers().size() % 2 == 0) {
                startTeamSplit(party, initiator, kitType, false);
            } else {
                new OddManOutMenu(oddManOut -> {
                    initiator.closeInventory();
                    startTeamSplit(party, initiator, kitType, oddManOut);
                }).openMenu(initiator);
            }
        }, "§eTeam Split").openMenu(initiator);
    }

    public static void startTeamSplit(Party party, Player initiator, KitType kitType, boolean oddManOut) {
        if (!PracticeValidation.canStartTeamSplit(party, initiator)) {
            return;
        }

        List<UUID> members = new ArrayList<>(party.getMembers());
        Collections.shuffle(members);

        Set<UUID> team1 = new HashSet<>();
        Set<UUID> team2 = new HashSet<>();
        Player spectator = null; // only can be one

        while (members.size() >= 2) {
            team1.add(members.remove(0));
            team2.add(members.remove(0));
        }

        if (!members.isEmpty()) {
            if (oddManOut) {
                spectator = Bukkit.getPlayer(members.remove(0));
                party.message(ChatColor.YELLOW + spectator.getName() + " was selected as the odd-man out.");
            } else {
                team1.add(members.remove(0));
            }
        }

        Match match = Practice.getInstance().getMatchHandler().startMatch(
                ImmutableList.of(
                        new MatchTeam(team1),
                        new MatchTeam(team2)
                ),
                kitType,
                false,
                false,
                null
        );

        if (match == null) {
            initiator.sendMessage(ChatColor.RED + "Failed to start team split.");
            return;
        }

        if (spectator != null) {
            match.addSpectator(spectator, null);
        }
    }

}