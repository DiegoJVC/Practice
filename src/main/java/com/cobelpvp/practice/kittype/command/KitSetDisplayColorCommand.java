package com.cobelpvp.practice.kittype.command;

import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitSetDisplayColorCommand {

	@Command(names = { "kits admin setdisplaycolor" }, permission = "practice.admin", description = "Sets a kit-type's display color")
	public static void execute(Player player, @Param(name = "kittype") KitType kitType, @Param(name = "displayColor", wildcard = true) String color) {
		kitType.setDisplayColor(ChatColor.valueOf(color.toUpperCase().replace(" ", "_")));
		kitType.saveAsync();
		player.sendMessage(ChatColor.GREEN + "You've updated this kit-type's display color.");
	}

}
