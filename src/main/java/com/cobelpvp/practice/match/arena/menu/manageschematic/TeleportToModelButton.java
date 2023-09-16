package com.cobelpvp.practice.match.arena.menu.manageschematic;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.Vector;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.arena.ArenaHandler;
import library.cobelpvp.menu.Button;
import com.cobelpvp.practice.match.arena.ArenaSchematic;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import java.util.List;

final class TeleportToModelButton extends Button {

    private final ArenaSchematic schematic;

    TeleportToModelButton(ArenaSchematic schematic) {
        this.schematic = Preconditions.checkNotNull(schematic, "schematic");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Teleport to model";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.YELLOW + "Click to teleport to the model arena, which",
            ChatColor.YELLOW + "will allow you to make edits to the schematic."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.BREWING_STAND_ITEM;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        player.closeInventory();

        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        Vector arenaStart = schematic.getModelArenaLocation();

        player.teleport(new Location(
            arenaHandler.getArenaWorld(),
            arenaStart.getX() + 50,
            arenaStart.getY() + 50,
            arenaStart.getZ() + 50
        ));

        player.sendMessage(ChatColor.GREEN + "Teleporting to " + schematic.getName());
    }

}