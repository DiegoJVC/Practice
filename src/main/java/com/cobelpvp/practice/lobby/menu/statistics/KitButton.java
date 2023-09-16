package com.cobelpvp.practice.lobby.menu.statistics;

import java.util.List;
import java.util.Map;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.elo.EloHandler;
import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.atheneum.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import com.google.common.collect.Lists;

public class KitButton extends Button {

    private static EloHandler eloHandler;
    private KitType kitType;

    public KitButton(KitType kitType) {
        this.kitType = kitType;
    }

    @Override
    public String getName(final Player player) {
        return ChatColor.GOLD + this.kitType.getDisplayName() + ChatColor.WHITE + " (Top 5)";
    }

    @Override
    public List<String> getDescription(final Player player) {
        final List<String> description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");
        int counter = 1;
        for (final Map.Entry<String, Integer> entry : Practice.getInstance().getEloHandler().topElo(this.kitType).entrySet()) {
            String color = ((counter <= 3) ? ChatColor.GOLD : ChatColor.GOLD).toString();
            description.add(ChatColor.GRAY + color + counter + ChatColor.GRAY + ". " + ChatColor.YELLOW + entry.getKey() + ": " + ChatColor.WHITE + entry.getValue());
            ++counter;
        }
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");
        return description;
    }

    @Override
    public Material getMaterial(final Player player) {
        return this.kitType.getIcon().getItemType();
    }

    static {
        KitButton.eloHandler = Practice.getInstance().getEloHandler();
    }

    @Override
    public byte getDamageValue(final Player player) {
        return this.kitType.getIcon().getData();
    }
}
