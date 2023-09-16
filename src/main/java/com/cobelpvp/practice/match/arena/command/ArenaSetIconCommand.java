package com.cobelpvp.practice.match.arena.command;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.arena.ArenaHandler;
import com.cobelpvp.practice.match.arena.ArenaSchematic;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class ArenaSetIconCommand {

    @Command(names = { "arena seticon" }, permission = "op")
    public static void arenaSetIconCommand(Player sender, @Param(name="schematic") String schematicName) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();

        ArenaSchematic schematic = arenaHandler.getSchematic(schematicName);

        if(schematic == null) {
            sender.sendMessage(ChatColor.RED + "No schematic found with the name" + schematicName + ".");
            return;
        }

        if(sender.getItemInHand() == null) {
            sender.sendMessage(ChatColor.RED + "You don't have an item in your hand!");
            return;
        }

        MaterialData mat = sender.getItemInHand().getData();

        schematic.setIcon(mat);
        sender.sendMessage(ChatColor.GREEN + "You have successfully set the item for the arena " + schematicName +  ".");
    }

}
