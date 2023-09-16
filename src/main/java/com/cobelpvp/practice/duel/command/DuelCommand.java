package com.cobelpvp.practice.duel.command;

import com.cobelpvp.practice.match.arena.ArenaSchematic;
import com.cobelpvp.practice.kittype.menu.select.SelectKitTypeMenu;
import com.cobelpvp.practice.lobby.LobbyHandler;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.party.PartyHandler;
import com.cobelpvp.practice.util.listener.PracticeLang;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.kittype.menu.select.SelectArenaMenu;
import com.cobelpvp.practice.duel.DuelHandler;
import com.cobelpvp.practice.duel.DuelInvite;
import com.cobelpvp.practice.duel.PartyDuelInvite;
import com.cobelpvp.practice.duel.PlayerDuelInvite;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.practice.util.PracticeValidation;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class DuelCommand {

    @Command(names = {"duel"}, permission = "")
    public static void duel(Player sender, @Param(name = "player") Player target) {
        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You can't duel yourself!");
            return;
        }

        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        LobbyHandler lobbyHandler = Practice.getInstance().getLobbyHandler();

        Party senderParty = partyHandler.getParty(sender);
        Party targetParty = partyHandler.getParty(target);

        if (senderParty != null && targetParty != null) {
            if (!PracticeValidation.canSendDuel(senderParty, targetParty, sender)) {
                return;
            }

            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();

                Party newSenderParty = partyHandler.getParty(sender);
                Party newTargetParty = partyHandler.getParty(target);

                if (newSenderParty != null && newTargetParty != null) {
                    if (newSenderParty.isLeader(sender.getUniqueId())) {
                        duel(sender, newSenderParty, newTargetParty, kitType);
                    } else {
                        sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
                    }
                }
            }, true, "§6Select a kit").openMenu(sender);

        } else if (senderParty == null && targetParty == null) {
            if (!PracticeValidation.canSendDuel(sender, target)) {
                return;
            }

            new SelectKitTypeMenu(kitType -> {
                selectArena(sender, target, kitType);
            }, "§6Select a kit").openMenu(sender);

            if (target.hasPermission("practice.youtuber") && System.currentTimeMillis() - lobbyHandler.getLastLobbyTime(target) < 3_000) {
                sender.sendMessage(ChatColor.RED + "Please wait a moment.");
                return;
            }

        } else if (senderParty == null) {
            sender.sendMessage(ChatColor.RED + "You must create a party to duel " + target.getName() + "'s party.");
        } else {
            sender.sendMessage(ChatColor.RED + "You must leave your party to duel " + target.getName() + ".");
        }
    }

    public static void selectArena(Player sender, Player target, KitType kitType) {
        new SelectArenaMenu(arena -> {
            sender.closeInventory();
            duel(sender, target, kitType, arena);
        }, "§6Select an arena", kitType).openMenu(sender);
    }

    public static void duel(Player sender, Player target, KitType kitType, ArenaSchematic arena) {
        if (!PracticeValidation.canSendDuel(sender, target)) {
            return;
        }

        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();
        DuelInvite autoAcceptInvite = duelHandler.findInvite(target, sender);

        if (autoAcceptInvite != null && autoAcceptInvite.getKitType() == kitType && autoAcceptInvite.getArena() == arena) {
            AcceptCommand.accept(sender, target);
            return;
        }

        DuelInvite alreadySentInvite = duelHandler.findInvite(sender, target);

        if (alreadySentInvite != null) {
            if (alreadySentInvite.getKitType() == kitType && alreadySentInvite.getArena() == arena) {
                sender.sendMessage(ChatColor.YELLOW + "You have already invited " + ChatColor.DARK_GREEN + target.getName() + ChatColor.YELLOW + " to a " + ChatColor.GREEN +kitType.getDisplayName() + ChatColor.YELLOW + " duel.");
                return;
            } else {
                duelHandler.removeInvite(alreadySentInvite);
            }
        }

        target.spigot().sendMessage(createInviteNotification(sender.getName(), null, kitType, arena));

        if(arena == null) {
            sender.sendMessage(ChatColor.YELLOW + "Successfully sent a " + ChatColor.GREEN + kitType.getDisplayName() + ChatColor.YELLOW + " duel request to " + ChatColor.GREEN + target.getName() + ChatColor.GREEN + ".");
        }else {
            sender.sendMessage(ChatColor.YELLOW + "Successfully sent a " + ChatColor.GREEN + kitType.getDisplayName() + ChatColor.YELLOW + " duel request to " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " on " + ChatColor.GREEN + arena.getName() + ChatColor.YELLOW + ".");
        }
        duelHandler.insertInvite(new PlayerDuelInvite(sender, target, kitType, arena));
    }

    public static void duel(Player sender, Party senderParty, Party targetParty, KitType kitType) {
        if (!PracticeValidation.canSendDuel(senderParty, targetParty, sender)) {
            return;
        }

        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();
        DuelInvite autoAcceptInvite = duelHandler.findInvite(targetParty, senderParty);
        String targetPartyLeader = Bukkit.getPlayer(targetParty.getLeader()).getName();

        if (autoAcceptInvite != null && autoAcceptInvite.getKitType() == kitType) {
            AcceptCommand.accept(sender, Bukkit.getPlayer(targetParty.getLeader()));
            return;
        }

        DuelInvite alreadySentInvite = duelHandler.findInvite(senderParty, targetParty);

        if (alreadySentInvite != null) {
            if (alreadySentInvite.getKitType() == kitType) {
                sender.sendMessage(ChatColor.YELLOW + "You have already invited " + ChatColor.GREEN + targetPartyLeader + ChatColor.YELLOW + "'s party to a " + ChatColor.GREEN + kitType.getDisplayName() + ChatColor.YELLOW + " duel.");
                return;
            } else {
                duelHandler.removeInvite(alreadySentInvite);
            }
        }

        Bukkit.getPlayer(targetParty.getLeader()).spigot().sendMessage(createInviteNotification(sender.getName(), senderParty, kitType, null));

        sender.sendMessage(ChatColor.YELLOW + "Successfully sent a " + ChatColor.GREEN + kitType.getDisplayName() + ChatColor.YELLOW + " duel invite to " + ChatColor.GREEN + targetPartyLeader + ChatColor.YELLOW + "'s party.");
        duelHandler.insertInvite(new PartyDuelInvite(senderParty, targetParty, kitType));
    }

    private static TextComponent[] createInviteNotification(String sender, Party senderParty, KitType kitType, ArenaSchematic arena) {
        TextComponent firstPart;
        if(arena == null) {
            if (senderParty == null) {
                firstPart = new TextComponent(ChatColor.GREEN + sender + ChatColor.YELLOW + " has sent you a " + ChatColor.GREEN +  kitType.getDisplayName() + ChatColor.YELLOW + " duel. ");
            } else {
                firstPart = new TextComponent(ChatColor.GREEN + sender + ChatColor.YELLOW + "'s Party " +ChatColor.GOLD +"("+ senderParty.getMembers().size() + ")" + ChatColor.YELLOW + " has sent you a " + ChatColor.GREEN +kitType.getDisplayName() + ChatColor.YELLOW + " duel. ");
            }
        }else {
            if (senderParty == null) {
                firstPart = new TextComponent(ChatColor.GREEN + sender + ChatColor.YELLOW + " has sent you a " + ChatColor.GREEN +  kitType.getDisplayName() + ChatColor.YELLOW + " duel on " + ChatColor.GREEN + arena.getName() + ChatColor.GREEN +  ". ");
            } else {
                firstPart = new TextComponent(ChatColor.GREEN + sender + ChatColor.YELLOW +  "'s Party (" + senderParty.getMembers().size() + ")" + " has sent you a " + ChatColor.GREEN + kitType.getDisplayName() + ChatColor.YELLOW + " duel on" + ChatColor.GREEN + arena.getName() + ChatColor.GREEN + ". ");
            }
        }
        TextComponent commandPart = new TextComponent("[Click to Accept]");
        TextComponent secondPart = new TextComponent("");

        firstPart.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        commandPart.setColor(net.md_5.bungee.api.ChatColor.GOLD);
        secondPart.setColor(net.md_5.bungee.api.ChatColor.YELLOW);

        ClickEvent.Action runCommand = ClickEvent.Action.RUN_COMMAND;
        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT;

        firstPart.setClickEvent(new ClickEvent(runCommand, "/accept " + sender));
        firstPart.setHoverEvent(new HoverEvent(showText, new BaseComponent[] { new TextComponent(ChatColor.GREEN + "Click here to accept") }));

        commandPart.setClickEvent(new ClickEvent(runCommand, "/accept " + sender));
        commandPart.setHoverEvent(new HoverEvent(showText, new BaseComponent[] { new TextComponent(ChatColor.GREEN + "Click here to accept") }));

        secondPart.setClickEvent(new ClickEvent(runCommand, "/accept " + sender));
        secondPart.setHoverEvent(new HoverEvent(showText, new BaseComponent[] { new TextComponent(ChatColor.GREEN + "Click here to accept") }));

        return new TextComponent[] { firstPart, commandPart, secondPart };
    }

}