package com.cobelpvp.practice.util.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class ChatFormatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("EnginePrefix")) {
            String prefix = player.getMetadata("EnginePrefix").get(0).asString();
            event.setFormat(prefix + ("%s" + ChatColor.RESET + ": %s"));
        } else {
            event.setFormat("%s" + ChatColor.RESET + ": %s");
        }
    }

}