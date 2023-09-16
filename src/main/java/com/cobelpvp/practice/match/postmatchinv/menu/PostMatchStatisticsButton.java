package com.cobelpvp.practice.match.postmatchinv.menu;

import com.cobelpvp.practice.kittype.HealingMethod;
import com.cobelpvp.practice.kittype.KitType;
import library.cobelpvp.menu.Button;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.List;

final class PostMatchStatisticsButton extends Button {

    private final KitType kitType;
    private final HealingMethod healingMethodUsed;
    private final int totalHits;
    private final int longestCombo;
    private final double missedHeals;
    private final double thrownHeals;

    PostMatchStatisticsButton(KitType kitType, HealingMethod healingMethodUsed, int totalHits, int longestCombo, double missedHeals, double thrownHeals) {
        this.kitType = kitType;
        this.healingMethodUsed = healingMethodUsed;
        this.totalHits = totalHits;
        this.longestCombo = longestCombo;
        this.missedHeals = missedHeals;
        this.thrownHeals = thrownHeals;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Match Stats";
    }

    @Override
    public List<String> getDescription(Player player) {
        if (healingMethodUsed != HealingMethod.POTIONS) {
            return ImmutableList.of(
                    ChatColor.GREEN + "Hits: " + ChatColor.YELLOW + totalHits + "" + (totalHits == 1 ? "" : ""),
                    ChatColor.GREEN + "Longest Combo: " + ChatColor.YELLOW + longestCombo + "" + (longestCombo == 1 ? "" : ""));
        }
        if (kitType.getId().equals("Debuff") || kitType.getId().equals("Vanilla")) {
            return ImmutableList.of(
                    ChatColor.GREEN + "Hits: " + ChatColor.YELLOW + totalHits + "" + (totalHits == 1 ? "" : ""),
                    ChatColor.GREEN + "Longest Combo: " + ChatColor.YELLOW + longestCombo + "" + (longestCombo == 1 ? "" : ""),
                    ChatColor.GREEN + "Potion Accuracy: " + ChatColor.YELLOW + (getPotionAccuracy() == -1 ? "N/A" : getPotionAccuracy() + "%"));
        }
        return ImmutableList.of(
                ChatColor.GREEN + "Hits: " + ChatColor.YELLOW + totalHits + "" + (totalHits == 1 ? "" : ""),
                ChatColor.GREEN + "Longest Combo: " + ChatColor.YELLOW + longestCombo + "" + (longestCombo == 1 ? "" : ""),
                ChatColor.GREEN + "Potion Accuracy: " + ChatColor.YELLOW + (getPotionAccuracy() == -1 ? "N/A" : getPotionAccuracy() + "%"));
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.PAPER;
    }

    @Override
    public int getAmount(Player player) {
        return 1;
    }

    public int getPotionAccuracy() {
        if (thrownHeals == 0) {
            return -1;
        } else if (missedHeals == 0) {
            return 100;
        } else if (thrownHeals == missedHeals) {
            return 50;
        }

        return (int) Math.round(100 - ((missedHeals / thrownHeals) * 100));
    }

}