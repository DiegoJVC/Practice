package com.cobelpvp.practice.lobby.menu.statistics;

import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.profile.Profile;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class RecordsButton extends Button {

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + ( player.getDisplayName() + " ") + ChatColor.WHITE + "Records";
    }

    @Override
    public List<String> getDescription(Player player) {
        final List<String> description = Lists.newArrayList();

        Profile p = Practice.getInstance().getProfileManager().getProfile(player);

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");
        description.add(ChatColor.YELLOW + ("Games Played: " + ChatColor.WHITE + p.getGamesPlayed()));
        description.add(ChatColor.YELLOW + ("Wins: " + ChatColor.WHITE + p.getGamesWon()));
        description.add(ChatColor.YELLOW + ("Loses: " + ChatColor.WHITE + p.getLoses()));
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.ENDER_CHEST;
    }
}
