package com.cobelpvp.practice.match.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.lobby.setting.SettingHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class MatchDeathMessageListener implements Listener {

    private static final String NO_KILLER_MESSAGE = ChatColor.translateAlternateColorCodes('&', "&f%s&f died.");
    private static final String KILLED_BY_OTHER_MESSAGE = ChatColor.translateAlternateColorCodes('&', "&c%s&7 was slain by &a%s&f.");

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getEntity());

        if (match == null) {
            return;
        }

        Player killed = event.getEntity();
        Player killer = killed.getKiller();
        PacketContainer lightningPacket = createLightningPacket(killed.getLocation());

        float thunderSoundPitch = 0.8F + ThreadLocalRandom.current().nextFloat() * 0.2F;
        float explodeSoundPitch = 0.5F + ThreadLocalRandom.current().nextFloat() * 0.2F;

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            UUID onlinePlayerUuid = onlinePlayer.getUniqueId();

            if (match.getTeam(onlinePlayerUuid) == null && !match.isSpectator(onlinePlayerUuid)) {
                continue;
            }


            String killedFormattedName = killed.getName();

            if (killer == null || match.isSpectator(killer.getUniqueId())) {
                onlinePlayer.sendMessage(String.format(NO_KILLER_MESSAGE, killedFormattedName));
            } else {
                String killerFormattedName = killer.getName();

                onlinePlayer.sendMessage(String.format(KILLED_BY_OTHER_MESSAGE, killedFormattedName, killerFormattedName));
            }
        }
    }

    private PacketContainer createLightningPacket(Location location) {
        PacketContainer lightningPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_WEATHER);

        lightningPacket.getModifier().writeDefaults();
        lightningPacket.getIntegers().write(0, 128);
        lightningPacket.getIntegers().write(4, 1);
        lightningPacket.getIntegers().write(1, (int) (location.getX() * 32.0D));
        lightningPacket.getIntegers().write(2, (int) (location.getY() * 32.0D));
        lightningPacket.getIntegers().write(3, (int) (location.getZ() * 32.0D));

        return lightningPacket;
    }

    private void sendLightningPacket(Player target, PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(target, packet);
        } catch (InvocationTargetException ignored) {
        }
    }

}