package com.cobelpvp.practice.kittype.command;

import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitSetDisplayNameCommand {

	@Command(names = { "kits admin setdisplayname" }, permission = "practice.admin", description = "Sets a kit-type's display name")
	public static void execute(Player player, @Param(name = "kittype") KitType kitType, @Param(name = "displayName", wildcard = true) String displayName) {
		kitType.setDisplayName(displayName);
		kitType.saveAsync();
		player.sendMessage(ChatColor.GREEN + "You've updated this kit-type's display name.");
	}

}
