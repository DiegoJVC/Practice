package com.cobelpvp.practice.match.postmatchinv;

import com.cobelpvp.atheneum.util.PlayerUtils;
import com.cobelpvp.practice.kittype.HealingMethod;
import com.cobelpvp.practice.kittype.KitType;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import java.util.List;
import java.util.UUID;

public final class PostMatchPlayer {

    @Getter private final UUID playerUuid;
    @Getter private final String lastUsername;
    @Getter private final ItemStack[] armor;
    @Getter private final ItemStack[] inventory;
    @Getter private final List<PotionEffect> potionEffects;
    @Getter private final int hunger;
    @Getter private final int health;
    @Getter private final transient HealingMethod healingMethodUsed;
    @Getter private final int totalHits;
    @Getter private final int longestCombo;
    @Getter private final int missedPots;
    @Getter private final int ping;
    @Getter private int thrownPots;
    @Getter private KitType kit;

    public PostMatchPlayer(Player player, KitType kit, HealingMethod healingMethodUsed, int totalHits, int longestCombo, int missedPots, int thrownPots) {
        this.playerUuid = player.getUniqueId();
        this.lastUsername = player.getName();
        this.armor = player.getInventory().getArmorContents();
        this.inventory = player.getInventory().getContents();
        this.potionEffects = ImmutableList.copyOf(player.getActivePotionEffects());
        this.hunger = player.getFoodLevel();
        this.health = (int) player.getHealth();
        this.healingMethodUsed = healingMethodUsed;
        this.totalHits = totalHits;
        this.longestCombo = longestCombo;
        this.missedPots = missedPots;
        this.ping = PlayerUtils.getPing(player);
        this.thrownPots = thrownPots;
        this.kit = kit;
    }

}
