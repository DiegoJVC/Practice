package com.cobelpvp.practice.kittype.command;

import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class KitSaveDefaultCommand {

    @Command(names = "kits admin saveDefault", permission = "practice.admin")
    public static void kitSaveDefault(Player sender, @Param(name="kit type") KitType kitType) {
        kitType.setDefaultArmor(sender.getInventory().getArmorContents());
        kitType.setDefaultInventory(sender.getInventory().getContents());
        kitType.saveAsync();
        sender.sendMessage(ChatColor.YELLOW + "Saved default armor/inventory for " + kitType + ".");
    }

}