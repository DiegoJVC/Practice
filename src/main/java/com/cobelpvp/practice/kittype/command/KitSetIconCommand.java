package com.cobelpvp.practice.kittype.command;

import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitSetIconCommand {

	@Command(names = { "kits admin seticon" }, permission = "practice.admin", description = "Sets a kit-type's icon")
	public static void execute(Player player, @Param(name = "kittype") KitType kitType) {
		if (player.getItemInHand() == null) {
			player.sendMessage(ChatColor.RED + "Please hold an item in your hand.");
			return;
		}
		kitType.setIcon(player.getItemInHand().getData());
		kitType.saveAsync();
		player.sendMessage(ChatColor.GREEN + "You've updated this kit-type's icon.");
	}

}
