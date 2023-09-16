package com.cobelpvp.practice.party.command;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.party.PartyHandler;
import com.cobelpvp.atheneum.command.Command;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.UUID;

public final class PartyInviteCommand {

    @Command(names = {"party invite", "p invite"}, permission = "")
    public static void partyInvite(Player sender, @Param(name = "player") Player target) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You can't invite yourself to your own party.");
            return;
        }

        if (sender.hasMetadata("ModMode")) {
            sender.sendMessage(ChatColor.RED + "You can't do this while in silent mode");
            return;
        }

        if (party != null) {
            if (party.isMember(target.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + target.getName() + " is already in your party.");
                return;
            }

            if (party.getInvite(target.getUniqueId()) != null) {
                sender.sendMessage(ChatColor.RED + target.getName() + " already has a pending party invite.");
                return;
            }
        }

        if (partyHandler.hasParty(target)) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is already in another party.");
            return;
        }

        party = partyHandler.getOrCreateParty(sender);

        if (party.getMembers().size() >= Party.MAX_SIZE && !sender.isOp()) { // I got the permission from "/party invite **" below
            sender.sendMessage(ChatColor.RED + "Your party has reached the " + Party.MAX_SIZE + " player limit.");
            return;
        }

        if (party.isLeader(sender.getUniqueId())) {
            party.invite(target);
        } else {
            askLeaderToInvite(party, sender, target);
        }
    }

    @Command(names = {"party invite **", "p invite **", "t invite **", "team invite **", "invite **", "inv **", "party inv **", "p inv **", "t inv **", "team invite **", "f invite **", "f inv **"}, permission = "op")
    public static void partyInviteAll(Player sender) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        Party party = partyHandler.getOrCreateParty(sender);

        if (sender.hasMetadata("ModMode")) {
            sender.sendMessage(ChatColor.RED + "You can't do this while in silent mode");
            return;
        }

        int sent = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerUuid = player.getUniqueId();
            boolean isMember = party.isMember(playerUuid);
            boolean hasInvite = party.getInvite(playerUuid) != null;

            if (!isMember && !hasInvite) {
                party.invite(player);
                sent++;
            }
        }

        if (sent == 0) {
            sender.sendMessage(ChatColor.GREEN + "No invites to send.");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Sent " + sent + " invite" + (sent == 1 ? "" : "s") + ".");
        }
    }

    private static void askLeaderToInvite(Party party, Player requester, Player target) {
        requester.sendMessage(ChatColor.GREEN + "You have requested to invite " + target.getName() + ChatColor.GREEN + ".");

        Player leader = Bukkit.getPlayer(party.getLeader());

        if (leader == null) {
            return;
        }

        leader.sendMessage(requester.getName() + ChatColor.GREEN + " wants you to invite " + target.getDisplayName() + ChatColor.YELLOW + ".");
        leader.spigot().sendMessage(createInviteButton(target));
    }

    private static TextComponent createInviteButton(Player target) {
        BaseComponent[] hoverTooltip = { new TextComponent(ChatColor.GREEN + "Click to invite") };
        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT;
        ClickEvent.Action runCommand = ClickEvent.Action.RUN_COMMAND;
        TextComponent inviteButton = new TextComponent("Click here to send the invitation");
        inviteButton.setColor(ChatColor.GREEN);
        inviteButton.setHoverEvent(new HoverEvent(showText, hoverTooltip));
        inviteButton.setClickEvent(new ClickEvent(runCommand, "/invite " + target.getName()));
        return inviteButton;
    }

}
