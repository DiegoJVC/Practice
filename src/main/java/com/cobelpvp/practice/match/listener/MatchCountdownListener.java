package com.cobelpvp.practice.match.listener;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.match.MatchState;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

public final class MatchCountdownListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying((Player) event.getEntity());

        if (match != null && match.getState() != MatchState.IN_PROGRESS) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        ItemStack item = event.getItem();
        Material type = item.getType();

        if ((type == Material.POTION && Potion.fromItemStack(item).isSplash()) || type == Material.ENDER_PEARL || type == Material.SNOW_BALL) {
            MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
            Match match = matchHandler.getMatchPlaying(event.getPlayer());

            if (match != null && match.getState() == MatchState.COUNTDOWN) {
                event.setCancelled(true);
                event.getPlayer().updateInventory();
            }
        }
    }


    /**
     * Prevents bow-shooting, rods and projectiles in general from being used in non IN_PROGRESS matches.
     * @param event
     */
    @EventHandler
    public void onPlayerShoot(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();

        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(player);

        if (match != null && match.getState() == MatchState.COUNTDOWN) {
            event.setCancelled(true);
        }
    }
}