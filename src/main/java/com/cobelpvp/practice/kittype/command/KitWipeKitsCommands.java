package com.cobelpvp.practice.kittype.command;

import com.cobelpvp.practice.util.uuid.UniqueIDCache;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.UUID;

public final class KitWipeKitsCommands {

    @Command(names = "kits wipeKits Type", permission = "op")
    public static void kitWipeKitsType(Player sender, @Param(name="kit type") KitType kitType) {
        int modified = Practice.getInstance().getKitHandler().wipeKitsWithType(kitType);
        sender.sendMessage(ChatColor.YELLOW + "Wiped " + modified + " " + kitType.getDisplayName() + " kits.");
        sender.sendMessage(ChatColor.GRAY + "^ We would have a proper count here if we ran recent versions of MongoDB");
    }

    @Command(names = "kits wipeKits Player", permission = "op")
    public static void kitWipeKitsPlayer(Player sender, @Param(name="target") UUID target) {
        Practice.getInstance().getKitHandler().wipeKitsForPlayer(target);
        sender.sendMessage(ChatColor.YELLOW + "Wiped " + UniqueIDCache.name(target) + "'s kits.");
    }

}