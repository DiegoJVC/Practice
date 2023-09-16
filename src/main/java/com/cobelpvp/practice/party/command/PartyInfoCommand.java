package com.cobelpvp.practice.party.command;

import com.google.common.base.Joiner;
import com.cobelpvp.practice.util.listener.PracticeLang;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.util.PatchedPlayerUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PartyInfoCommand {

    @Command(names = {"party info", "p info"}, permission = "")
    public static void partyInfo(Player sender, @Param(name = "player", defaultValue = "self") Player target) {
        Party party = Practice.getInstance().getPartyHandler().getParty(target);

        if (party == null) {
            if (sender == target) {
                sender.sendMessage(PracticeLang.NOT_IN_PARTY);
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " isn't in a party.");
            }

            return;
        }

        String leaderName = Bukkit.getPlayer(party.getLeader()).getName();
        int memberCount = party.getMembers().size();
        String members = Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(party.getMembers()));

        Player p = Bukkit.getPlayer(leaderName);
        if(Bukkit.getPlayer(leaderName) == null) {
            sender.sendMessage(ChatColor.GOLD + "Leader: " + ChatColor.DARK_GREEN + leaderName);
        }else {
            sender.sendMessage(ChatColor.GOLD + "Leader: " + ChatColor.DARK_GREEN + p.getName());
        }
        sender.sendMessage(ChatColor.YELLOW + "Members " + ChatColor.GOLD + "(" + memberCount + ")" + ": " + ChatColor.GREEN + members);

        switch (party.getAccessRestriction()) {
            case PUBLIC:
                sender.sendMessage(ChatColor.YELLOW + "Privacy: " + ChatColor.GREEN + "Open");
                break;
            case INVITE_ONLY:
                sender.sendMessage(ChatColor.YELLOW + "Privacy: " + ChatColor.RED + "Invite-Only");
                break;
            case PASSWORD:
                if (party.isLeader(sender.getUniqueId())) {
                    HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT;
                    BaseComponent[] passwordComponent = { new TextComponent(party.getPassword()) };
                    ComponentBuilder builder = new ComponentBuilder("§ePrivacy: ").color(ChatColor.GREEN);
                    builder.append("Password Protected ").color(ChatColor.RED);
                    builder.append("[Hover for password]").color(ChatColor.GRAY);
                    builder.event(new HoverEvent(showText, passwordComponent));

                    sender.spigot().sendMessage(builder.create());
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Privacy: " + ChatColor.RED + "Password Protected");
                }
                break;
            default:
                break;
        }

    }

}
