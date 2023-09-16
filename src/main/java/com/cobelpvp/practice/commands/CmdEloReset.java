package com.cobelpvp.practice.commands;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.practice.Practice;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class CmdEloReset {

    @Command(names = "eloreset", permission = "practice.administrator", description = "Manually reset the player's elo")
    public static void eloreset(Player sender, @Param(name="target") OfflinePlayer target) {
        Practice.getInstance().getEloHandler().resetElo(target.getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "Resetting elo of " + target.getName() + ChatColor.GREEN + ".");
    }
}
