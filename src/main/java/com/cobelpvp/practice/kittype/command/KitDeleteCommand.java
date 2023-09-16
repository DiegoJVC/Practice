package com.cobelpvp.practice.kittype.command;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitDeleteCommand {

	@Command(names = { "kits admin delete" }, permission = "practice.admin", description = "Deletes an existing kit-type")
	public static void execute(Player player, @Param(name = "kittype") KitType kitType) {
		kitType.deleteAsync();
		KitType.getAllTypes().remove(kitType);
		Practice.getInstance().getQueueHandler().removeQueues(kitType);
		player.sendMessage(ChatColor.GREEN + "You've deleted the kit-type by the ID \"" + kitType.getId() + "\".");
	}

}
