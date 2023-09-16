package com.cobelpvp.practice.kit.pvpclasses;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.kit.pvpclasses.event.BardRestoreEvent;
import com.cobelpvp.practice.kit.pvpclasses.pvpclasses.ArcherClass;
import com.cobelpvp.practice.kit.pvpclasses.pvpclasses.BardClass;
import lombok.Getter;
import com.cobelpvp.practice.kittype.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

@SuppressWarnings("deprecation")
public class PvPClassHandler extends BukkitRunnable implements Listener {

    @Getter private static Map<String, PvPClass> equippedKits = new HashMap<>();
    @Getter private static Map<UUID, PvPClass.SavedPotion> savedPotions = new HashMap<>();
    @Getter List<PvPClass> pvpClasses = new ArrayList<>();

    public PvPClassHandler() {
        pvpClasses.add(new ArcherClass());
        pvpClasses.add(new BardClass());

        for (PvPClass pvpClass : pvpClasses) {
            Practice.getInstance().getServer().getPluginManager().registerEvents(pvpClass, Practice.getInstance());
        }
        Practice.getInstance().getServer().getScheduler().runTaskTimer(Practice.getInstance(), this, 2L, 2L);
        Practice.getInstance().getServer().getPluginManager().registerEvents(this, Practice.getInstance());
    }

    @Override
    public void run() {
        for (final Player player : Practice.getInstance().getServer().getOnlinePlayers()) {
            if (PvPClassHandler.equippedKits.containsKey(player.getName())) {
                final PvPClass equippedPvPClass = PvPClassHandler.equippedKits.get(player.getName());
                if (!equippedPvPClass.qualifies(player.getInventory())) {
                    PvPClassHandler.equippedKits.remove(player.getName());
                    player.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.BLUE + equippedPvPClass.getName() + ChatColor.GRAY + " --> " + ChatColor.RED + "Disabled!");
                    equippedPvPClass.remove(player);
                    PvPClass.removeInfiniteEffects(player);
                }
                else {
                    if (player.hasMetadata("frozen")) {
                        continue;
                    }
                    equippedPvPClass.tick(player);
                }
            }
            else {
                final Match match = Practice.getInstance().getMatchHandler().getMatchPlayingOrSpectating(player);
                if (match == null) {
                    continue;
                }
                if (!match.getKitType().equals(KitType.teamFight)) {
                    continue;
                }
                for (final PvPClass pvpClass : this.pvpClasses) {
                    if (pvpClass.qualifies(player.getInventory()) && pvpClass.canApply(player) && !player.hasMetadata("frozen")) {
                        pvpClass.apply(player);
                        getEquippedKits().put(player.getName(), pvpClass);
                        player.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.BLUE + pvpClass.getName() + ChatColor.GRAY + " --> " + ChatColor.GREEN + "Enabled!");
                        break;
                    }
                }
            }
        }
        this.checkSavedPotions();
    }

    public void checkSavedPotions() {
        Iterator<Map.Entry<UUID, PvPClass.SavedPotion>> idIterator = savedPotions.entrySet().iterator();
        while (idIterator.hasNext()) {
            Map.Entry<UUID, PvPClass.SavedPotion> id = idIterator.next();
            Player player = Bukkit.getPlayer(id.getKey());
            if (player != null && player.isOnline()) {
                Bukkit.getPluginManager().callEvent(new BardRestoreEvent(player, id.getValue()));
                if (id.getValue().getTime() < System.currentTimeMillis() && !id.getValue().isPerm()) {
                    if (player.hasPotionEffect(id.getValue().getPotionEffect().getType())) {
                        player.getActivePotionEffects().forEach(potion -> {
                            PotionEffect restore = id.getValue().getPotionEffect();
                            if (potion.getType() == restore.getType() && potion.getDuration() < restore.getDuration() && potion.getAmplifier() <= restore.getAmplifier()) {
                                player.removePotionEffect(restore.getType());
                            }
                        });
                    }

                    if (player.addPotionEffect(id.getValue().getPotionEffect(), true)) {
                        Bukkit.getLogger().info(id.getValue().getPotionEffect().getType() + ", " + id.getValue().getPotionEffect().getDuration() + ", " + id.getValue().getPotionEffect().getAmplifier());
                        idIterator.remove();
                    }
                }
            } else {
                idIterator.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getItemInHand() == null || (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        for (PvPClass pvPClass : pvpClasses) {
            if (hasKitOn(event.getPlayer(), pvPClass) && pvPClass.getConsumables() != null && pvPClass.getConsumables().contains(event.getPlayer().getItemInHand().getType())) {
                if (pvPClass.itemConsumed(event.getPlayer(), event.getItem().getType())) {
                    if (event.getPlayer().getItemInHand().getAmount() > 1) {
                        event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
                    } else {
                        event.getPlayer().getInventory().remove(event.getPlayer().getItemInHand());

                    }
                }
            }
        }
    }

    public static PvPClass getPvPClass(Player player) {
        return (equippedKits.containsKey(player.getName()) ? equippedKits.get(player.getName()) : null);
    }

    public static boolean hasKitOn(Player player, PvPClass pvpClass) {
        return (equippedKits.containsKey(player.getName()) && equippedKits.get(player.getName()) == pvpClass);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (equippedKits.containsKey(event.getPlayer().getName())) {
            equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
            equippedKits.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (equippedKits.containsKey(event.getPlayer().getName())) {
            equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
            equippedKits.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (equippedKits.containsKey(event.getPlayer().getName())) {
            equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
            equippedKits.remove(event.getPlayer().getName());
        }

        for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
            if (potionEffect.getDuration() > 1_000_000) {
                event.getPlayer().removePotionEffect(potionEffect.getType());
            }
        }
    }

    @EventHandler
    public void onPlayerDamageEvent(PlayerItemDamageEvent event) {
        Player player = (Player) event.getPlayer();
        PvPClass kit = equippedKits.get(player.getName());
        if (kit != null && kit.getName().equalsIgnoreCase("bard")) {
            if (Arrays.asList(player.getInventory().getArmorContents()).contains(event.getItem())) {
                if (new Random().nextBoolean()) {
                    event.setCancelled(true);
                }
            }
        }
    }

}