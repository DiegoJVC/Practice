package com.cobelpvp.practice.party.command;

import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.party.PartyHandler;
import com.cobelpvp.practice.party.PartyInvite;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyJoinCommand {

    private static final String NO_PASSWORD_PROVIDED = "kairwaiiajguisga25g1g1";

    @Command(names = {"party join", "p join"}, permission = "")
    public static void partyJoin(Player sender, @Param(name = "player") Player target, @Param(name = "password", defaultValue = NO_PASSWORD_PROVIDED) String providedPassword) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        Party targetParty = partyHandler.getParty(target);

        if (partyHandler.hasParty(sender)) {
            sender.sendMessage(ChatColor.RED + "You are already in a party. You must leave your current party first.");
            return;
        }

        if (targetParty == null) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not in a party.");
            return;
        }

        PartyInvite invite = targetParty.getInvite(sender.getUniqueId());

        switch (targetParty.getAccessRestriction()) {
            case PUBLIC:
                targetParty.join(sender);
                break;
            case INVITE_ONLY:
                if (invite != null) {
                    targetParty.join(sender);
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have an invitation to this party.");
                }

                break;
            case PASSWORD:
                if (providedPassword.equals(NO_PASSWORD_PROVIDED) && invite == null) {
                    sender.sendMessage(ChatColor.RED + "You need the password or an invitation to join this party.");
                    sender.sendMessage(ChatColor.GREEN + "To join with a password, use " + ChatColor.YELLOW + "/party join " + target.getName() + ChatColor.GREEN  + " <password>");
                    return;
                }

                String correctPassword = targetParty.getPassword();

                if (invite == null && !correctPassword.equals(providedPassword)) {
                    sender.sendMessage(ChatColor.RED + "Invalid password.");
                } else {
                    targetParty.join(sender);
                }
                break;
            default:
                break;
        }
    }

}
