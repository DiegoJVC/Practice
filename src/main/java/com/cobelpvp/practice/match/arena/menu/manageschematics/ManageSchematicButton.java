package com.cobelpvp.practice.match.arena.menu.manageschematics;

import com.cobelpvp.practice.match.arena.menu.manageschematic.ManageSchematicMenu;
import com.google.common.base.Preconditions;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.arena.Arena;
import com.cobelpvp.practice.match.arena.ArenaHandler;
import com.cobelpvp.practice.match.arena.ArenaSchematic;
import library.cobelpvp.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import java.util.ArrayList;
import java.util.List;

final class ManageSchematicButton extends Button {

    private final ArenaSchematic schematic;

    ManageSchematicButton(ArenaSchematic schematic) {
        this.schematic = Preconditions.checkNotNull(schematic, "schematic");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + "Manage " + schematic.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        int totalCopies = 0;
        int inUseCopies = 0;

        for (Arena arena : arenaHandler.getArenas(schematic)) {
            totalCopies++;

            if (arena.isInUse()) {
                inUseCopies++;
            }
        }

        List<String> description = new ArrayList<>();

        description.add("");
        description.add(ChatColor.GREEN + "Enabled: " + ChatColor.WHITE + (schematic.isEnabled() ? "Yes" : "No"));
        description.add(ChatColor.GREEN + "Copies: " + ChatColor.WHITE + totalCopies);
        description.add(ChatColor.GREEN + "Copies in use: " + ChatColor.WHITE + inUseCopies);

        return description;
    }

    @Override
    public int getAmount(Player player) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        return arenaHandler.getArenas(schematic).size();
    }

    @Override
    public Material getMaterial(Player player) {
        return schematic.isEnabled() ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        player.closeInventory();
        new ManageSchematicMenu(schematic).openMenu(player);
    }

}