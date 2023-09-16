package com.cobelpvp.practice.lobby.menu.statistics;

import java.util.List;
import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.kittype.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import com.google.common.collect.Lists;

public class PlayerButton extends Button {

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + player.getName() + ChatColor.WHITE + " Elo Statistics";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");
        description.add(ChatColor.YELLOW + "Global Elo: " + ChatColor.WHITE + Practice.getInstance().getEloHandler().getGlobalElo(player.getUniqueId()));
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");
        for (KitType kitType : KitType.getAllTypes()) {
            if (kitType.isSupportsRanked()) {
                description.add(ChatColor.YELLOW + kitType.getDisplayName() + ": " + ChatColor.WHITE + Practice.getInstance().getEloHandler().getElo(player, kitType));
            }
        }
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");
        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SKULL_ITEM;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 1;
    }
}
