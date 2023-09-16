package com.cobelpvp.practice.match.listener;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

public final class MatchRodListener implements Listener {

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();

        if (!Enchantment.PROTECTION_ENVIRONMENTAL.canEnchantItem(event.getItem())) {
            return;
        }

        if (player.getLastDamageCause() != null && player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            if (((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager() instanceof FishHook) {
                event.setCancelled(true);
            }
        }
    }

}
