package com.cobelpvp.practice.match.listener;

import com.cobelpvp.practice.kittype.HealingMethod;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class MatchSoupListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)

    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.MUSHROOM_SOUP || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Player player = event.getPlayer();
        Match match = matchHandler.getMatchPlaying(player);

        if ((match != null && match.getKitType().getHealingMethod() == HealingMethod.SOUP && player.getHealth() <= 19)) {
            double current = player.getHealth();
            double max = player.getMaxHealth();

            player.getItemInHand().setType(Material.BOWL);
            player.setHealth(Math.min(max, current + 7D));
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying((Player) event.getEntity());

        if (match != null && match.getKitType().getHealingMethod() == HealingMethod.SOUP) {
            event.setFoodLevel(20);
        }
    }

}