package com.cobelpvp.practice.kit.pvpclasses.pvpclasses;

import com.cobelpvp.atheneum.util.TimeUtils;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.MatchTeam;
import kotlin.Pair;
import lombok.Getter;
import com.cobelpvp.atheneum.nametag.TeamsNametagHandler;
import com.cobelpvp.practice.kit.pvpclasses.PvPClass;
import com.cobelpvp.practice.kit.pvpclasses.PvPClassHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
public class ArcherClass extends PvPClass {

    public static final int MARK_SECONDS = 10;

    private static Map<String, Long> lastSpeedUsage = new HashMap<>();
    private static Map<String, Long> lastJumpUsage = new HashMap<>();
    @Getter private static Map<String, Long> markedPlayers = new ConcurrentHashMap<>();

    @Getter private static Map<String, Set<Pair<String, Long>>> markedBy = new HashMap<>();

    public ArcherClass() {
        super("Archer", 15, "LEATHER_", Arrays.asList(Material.SUGAR, Material.FEATHER));
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
    }

    @Override
    public void tick(Player player) {
        if (!this.qualifies(player.getInventory())) {
            super.tick(player);
            return;
        }

        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        }
        super.tick(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityArrowHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            final Player player = (Player) event.getEntity();

            if (!(arrow.getShooter() instanceof Player)) {
                return;
            }

            Player shooter = (Player) arrow.getShooter();
            float pullback = arrow.getMetadata("Pullback").get(0).asFloat();

            if (!PvPClassHandler.hasKitOn(shooter, this)) {
                return;
            }

            int damage = isMarked(player) ? 4 : 2;

            if (pullback < 0.5F) {
                damage = 2; // 1 heart
            }

            if (player.getHealth() - damage <= 0D) {
                event.setCancelled(true);
            } else {
                event.setDamage(0D);
            }

            Location shotFrom = (Location) arrow.getMetadata("ShotFromDistance").get(0).value();
            double distance = shotFrom.distance(player.getLocation());

            //DeathMessageHandler.addDamage(player, new ArrowTracker.ArrowDamageByPlayer(player.getName(), damage, ((Player) arrow.getShooter()).getName(), shotFrom, distance));
            double health = player.getHealth() - damage;
            if (health <= 0D) {
                player.setHealth(0);
            } else {
                player.setHealth(health);
            }

            if (PvPClassHandler.hasKitOn(player, this)) {
                shooter.sendMessage(ChatColor.GREEN + "[" + ChatColor.YELLOW + "Arrow Range" + ChatColor.GREEN + " (" + ChatColor.RED + (int) distance + ChatColor.GREEN + ")"+ ChatColor.YELLOW +"] " + ChatColor.RED + "Cannot mark other Archers. " + ChatColor.YELLOW.toString() + ChatColor.BOLD + "(" + damage / 2 + " heart" + ((damage / 2 == 1) ? "" : "s") + ")");
            } else if (pullback >= 0.5F) {
                shooter.sendMessage(ChatColor.GREEN + "[" + ChatColor.YELLOW + "Arrow Range" + ChatColor.GREEN + " (" + ChatColor.RED + (int) distance + ChatColor.GREEN + ")"+ ChatColor.YELLOW +"] " + ChatColor.YELLOW + "Marked player for " + ChatColor.GOLD + MARK_SECONDS + ChatColor.YELLOW + " seconds. " + ChatColor.YELLOW.toString() + ChatColor.BOLD + "(" + damage / 2 + " heart" + ((damage / 2 == 1) ? "" : "s") + ")");

                if (!isMarked(player)) {
                    player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Marked! " + ChatColor.YELLOW + "An archer has shot you and marked you (+25% damage) for " + ChatColor.GOLD + MARK_SECONDS + ChatColor.YELLOW + " seconds.");
                }

                PotionEffect invis = null;

                for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                    if (potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
                        invis = potionEffect;
                        break;
                    }
                }

                if (invis != null) {
                    PvPClass playerClass = PvPClassHandler.getPvPClass(player);

                    player.removePotionEffect(invis.getType());

                    final PotionEffect invisFinal = invis;
                }

                getMarkedPlayers().put(player.getName(), System.currentTimeMillis() + (MARK_SECONDS * 1000));
                getMarkedBy().putIfAbsent(shooter.getName(), new HashSet<>());
                getMarkedBy().get(shooter.getName()).add(new Pair<>(player.getName(), System.currentTimeMillis() + (MARK_SECONDS * 1000)));
                TeamsNametagHandler.reloadPlayer(player);

                new BukkitRunnable() {

                    public void run() {
                        TeamsNametagHandler.reloadPlayer(player);
                    }

                }.runTaskLater(Practice.getInstance(), (MARK_SECONDS * 20) + 5);
            } else {
                shooter.sendMessage(ChatColor.GREEN + "[" + ChatColor.YELLOW + "Arrow Range" + ChatColor.GREEN + " (" + ChatColor.RED + (int) distance + ChatColor.GREEN + ")"+ ChatColor.YELLOW +"] " + ChatColor.RED + "Bow wasn't fully drawn back. " + ChatColor.YELLOW.toString() + ChatColor.BOLD + "(" + damage / 2 + " heart" + ((damage / 2 == 1) ? "" : "s") + ")");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (isMarked(player)) {
                Player damager = null;
                if (event.getDamager() instanceof Player) {
                    damager = (Player) event.getDamager();
                } else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
                    damager = (Player) ((Projectile) event.getDamager()).getShooter();
                }

                if (damager != null && !canUseMark(damager, player)) {
                    return;
                }
                event.setDamage(event.getDamage() * 1.25D);
            }
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        event.getProjectile().setMetadata("ShotFromDistance", new FixedMetadataValue(Practice.getInstance(), event.getProjectile().getLocation()));
        event.getProjectile().setMetadata("Pullback", new FixedMetadataValue(Practice.getInstance(), event.getForce()));
    }

    @Override
    public boolean itemConsumed(Player player, Material material) {
        if (material == Material.SUGAR) {
            if (lastSpeedUsage.containsKey(player.getName()) && lastSpeedUsage.get(player.getName()) > System.currentTimeMillis()) {
                long millisLeft = lastSpeedUsage.get(player.getName()) - System.currentTimeMillis();
                String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);
                player.sendMessage(ChatColor.RED + "You can't use this for another §c§l" + msg + "§c.");
                return (false);
            }

            lastSpeedUsage.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 3), true);
            return (true);
        } else {
            if (lastJumpUsage.containsKey(player.getName()) && lastJumpUsage.get(player.getName()) > System.currentTimeMillis()) {
                long millisLeft = lastJumpUsage.get(player.getName()) - System.currentTimeMillis();
                String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);
                player.sendMessage(ChatColor.RED + "You can't use this for another §c§l" + msg + "§c.");
                return (false);
            }

            lastJumpUsage.put(player.getName(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 5, 4));
            return (false);
        }
    }

    public static boolean isMarked(Player player) {
        return (getMarkedPlayers().containsKey(player.getName()) && getMarkedPlayers().get(player.getName()) > System.currentTimeMillis());
    }

    private boolean canUseMark(Player player, Player victim) {
        if (Practice.getInstance().getMatchHandler().getMatchPlaying(player) != null) {
            MatchTeam team = Practice.getInstance().getMatchHandler().getMatchPlaying(player).getTeam(player.getUniqueId());

            if (team != null) {
                int amount = 0;
                for (UUID memberUUID : team.getAllMembers()) {
                    Player member = Bukkit.getPlayer(memberUUID);

                    if (member == null) continue;
                    if (PvPClassHandler.hasKitOn(member, this)) {
                        amount++;

                        if (amount > 3) {
                            break;
                        }
                    }
                }

                if (amount > 3) {
                    player.sendMessage(ChatColor.RED + "Your team has too many archers. Archer mark was not applied.");
                    return false;
                }
            }
        }

        if (markedBy.containsKey(player.getName())) {
            for (Pair<String, Long> pair : markedBy.get(player.getName())) {
                if (victim.getName().equals(pair.getFirst()) && pair.getSecond() > System.currentTimeMillis()) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

}
