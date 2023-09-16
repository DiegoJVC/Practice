package com.cobelpvp.practice.match.listener;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.util.VisibilityUtils;
import com.cobelpvp.practice.lobby.setting.Setting;
import com.cobelpvp.practice.lobby.setting.event.SettingUpdateEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;

public final class SpectatorPreventionListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchSpectating(event.getPlayer());

        if (match != null) {
            match.removeSpectator(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (matchHandler.isSpectatingMatch(event.getEntity())) {
            event.setKeepInventory(true);
        }
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
            Player damager = (Player) event.getDamager();

            if (matchHandler.isSpectatingMatch(damager)) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onPlayerDropitem(PlayerDropItemEvent event) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (matchHandler.isSpectatingMatch(event.getPlayer())) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onPlayerPickupitem(PlayerPickupItemEvent event) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (matchHandler.isSpectatingMatch(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (matchHandler.isSpectatingMatch(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (matchHandler.isSpectatingMatch(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (matchHandler.isSpectatingMatch((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (matchHandler.isSpectatingMatch((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        InventoryHolder inventoryHolder = event.getSource().getHolder();

        if (inventoryHolder instanceof Player) {
            MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

            if (matchHandler.isSpectatingMatch((Player) inventoryHolder)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSettingUpdate(SettingUpdateEvent event) {
        if (event.getSetting() == Setting.VIEW_OTHER_SPECTATORS) {
            VisibilityUtils.updateVisibility(event.getPlayer());
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Entity shooter = (Entity) event.getEntity().getShooter();

        if (shooter instanceof Player) {
            MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

            if (matchHandler.isSpectatingMatch((Player) shooter)) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        for (LivingEntity entity : event.getAffectedEntities()) {
            if (entity instanceof Player && matchHandler.isSpectatingMatch((Player) entity)) {
                event.setIntensity(entity, 0F);
            }
        }
    }

}