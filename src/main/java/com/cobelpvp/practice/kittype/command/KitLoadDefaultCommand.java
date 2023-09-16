package com.cobelpvp.practice.kittype.command;

import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class KitLoadDefaultCommand {

    @Command(names = "kits admin loadDefault", permission = "practice.admin")
    public static void kitLoadDefault(Player sender, @Param(name="kit type") KitType kitType) {
        sender.getInventory().setArmorContents(kitType.getDefaultArmor());
        sender.getInventory().setContents(kitType.getDefaultInventory());
        sender.updateInventory();
        sender.sendMessage(ChatColor.GOLD + "Loaded default armor/inventory for " + kitType + ".");
    }

}