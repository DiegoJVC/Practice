package com.cobelpvp.practice.match.postmatchinv.command;

import com.cobelpvp.practice.util.uuid.UniqueIDCache;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.practice.match.postmatchinv.PostMatchPlayer;
import com.cobelpvp.practice.match.postmatchinv.menu.PostMatchMenu;
import com.cobelpvp.practice.match.postmatchinv.PostMatchInvHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.Map;
import java.util.UUID;

public final class CheckPostMatchInvCommand {

    @Command(names = { "matchinventory" }, permission = "")
    public static void checkPostMatchInv(Player sender, @Param(name = "target") UUID target) {
        PostMatchInvHandler postMatchInvHandler = Practice.getInstance().getPostMatchInvHandler();
        Map<UUID, PostMatchPlayer> players = postMatchInvHandler.getPostMatchData(sender.getUniqueId());

        if (players.containsKey(target)) {
            new PostMatchMenu(players.get(target)).openMenu(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Data for " + UniqueIDCache.name(target) + " not found.");
        }
    }

}