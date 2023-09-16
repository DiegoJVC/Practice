package com.cobelpvp.practice.kittype.command;

import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.Comparator;

public class KitSetSortCommand {

	@Command(names = { "kits admin setsort" }, permission = "practice.admin", description = "Sets a kit-type's sort")
	public static void execute(Player player, @Param(name = "kittype") KitType kitType, @Param(name = "sort") int sort) {
		kitType.setSort(sort);
		kitType.saveAsync();
		KitType.getAllTypes().sort(Comparator.comparing(KitType::getSort));
		player.sendMessage(ChatColor.GREEN + "You've updated this kit-type's sort.");
	}

}
