package com.cobelpvp.practice.util.listener;

import com.cobelpvp.practice.util.FancyPlayerInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FancyInventoryListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FancyPlayerInventory.join(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        FancyPlayerInventory.quit(event.getPlayer());
    }

}
