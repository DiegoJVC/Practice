package com.cobelpvp.practice.commands;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.practice.util.VisibilityUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class CmdUpdateVisbility {

    @Command(names = {"updatevisibility", "updatevis", "upvis", "uv"}, permission = "")
    public static void updateVisibility(Player sender) {
        VisibilityUtils.updateVisibility(sender);
        sender.sendMessage(ChatColor.GREEN + "Updated your visibility.");
    }

    @Command(names = {"updatevisibilityFlicker", "updatevisFlicker", "upvisFlicker", "uvf"}, permission = "")
    public static void updateVisibilityFlicker(Player sender) {
        VisibilityUtils.updateVisibilityFlicker(sender);
        sender.sendMessage(ChatColor.GREEN + "Updated your visibility (flicker mode).");
    }

}