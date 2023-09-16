package com.cobelpvp.practice.util.listener;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.MatchTeam;
import com.cobelpvp.practice.match.event.MatchCountdownStartEvent;
import com.cobelpvp.practice.match.event.MatchTerminateEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class PearlCooldownListener implements Listener {

    private static final long PEARL_COOLDOWN_MILLIS = TimeUnit.SECONDS.toMillis(16);

    private final Map<UUID, Long> pearlCooldown = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntityType() != EntityType.ENDER_PEARL) {
            return;
        }

        EnderPearl pearl = (EnderPearl) event.getEntity();
        Player shooter = (Player) pearl.getShooter();

        pearlCooldown.put(shooter.getUniqueId(), System.currentTimeMillis() + PEARL_COOLDOWN_MILLIS);

        new BukkitRunnable() {

            public void run() {
                long cooldownExpires = pearlCooldown.getOrDefault(shooter.getUniqueId(), 0L);

                if (cooldownExpires < System.currentTimeMillis()) {
                    cancel();
                    return;
                }

                int millisLeft = (int) (cooldownExpires - System.currentTimeMillis());
                float percentLeft = (float) millisLeft / PEARL_COOLDOWN_MILLIS;

                shooter.setExp(percentLeft);
                shooter.setLevel(millisLeft / 1_000);
            }

        }.runTaskTimer(Practice.getInstance(), 1L, 1L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.ENDER_PEARL || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        Player player = event.getPlayer();
        long cooldownExpires = pearlCooldown.getOrDefault(player.getUniqueId(), 0L);

        if (cooldownExpires < System.currentTimeMillis()) {
            return;
        }

        int millisLeft = (int) (cooldownExpires - System.currentTimeMillis());
        double secondsLeft = millisLeft / 1000D;
        secondsLeft = Math.round(10D * secondsLeft) / 10D;
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You can't use this for another " + ChatColor.BOLD + secondsLeft + ChatColor.RED + " seconds!");
        player.updateInventory();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        pearlCooldown.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        for (EnderPearl pearl : player.getWorld().getEntitiesByClass(EnderPearl.class)) {
            if (pearl.getShooter() == player) {
                pearl.remove();
            }
        }

        pearlCooldown.remove(player.getUniqueId());
    }

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        for (MatchTeam team : event.getMatch().getTeams()) {
            team.getAliveMembers().forEach(pearlCooldown::remove);
        }
    }

    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        for (MatchTeam team : event.getMatch().getTeams()) {
            team.getAllMembers().forEach(pearlCooldown::remove);
        }
    }

}