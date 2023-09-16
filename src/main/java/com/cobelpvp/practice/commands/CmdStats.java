package com.cobelpvp.practice.commands;

import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Command;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdStats {

    @Command(names = {"stats", "stat"}, permission = "")
    public static void statsCommand(Player sender, @Param(name="target", defaultValue = "self") Player target) {
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 35));
        sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.GRAY + " | " + ChatColor.WHITE + " Stats");
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 35));
        for (KitType kitType : KitType.getAllTypes()) {
            if (kitType.isSupportsRanked()) {
                sender.sendMessage(ChatColor.GOLD + kitType.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.WHITE + Practice.getInstance().getEloHandler().getElo(target, kitType));
            }
        }
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 35));
    }
}