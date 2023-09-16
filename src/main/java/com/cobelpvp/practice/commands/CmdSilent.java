package com.cobelpvp.practice.commands;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.util.VisibilityUtils;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public final class CmdSilent {

    @Command(names = {"silent"}, permission = "practice.silent")
    public static void silent(Player sender) {
        if (sender.hasMetadata("ModMode")) {
            sender.removeMetadata("ModMode", Practice.getInstance());
            sender.removeMetadata("invisible", Practice.getInstance());
            sender.sendMessage(ChatColor.RED + "Silent mode disabled.");
        } else {
            sender.setMetadata("ModMode", new FixedMetadataValue(Practice.getInstance(), true));
            sender.setMetadata("invisible", new FixedMetadataValue(Practice.getInstance(), true));
            sender.sendMessage(ChatColor.GREEN + "Silent mode enabled.");
        }
        VisibilityUtils.updateVisibility(sender);
    }

}