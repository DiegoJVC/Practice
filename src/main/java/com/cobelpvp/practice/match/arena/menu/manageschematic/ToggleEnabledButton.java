package com.cobelpvp.practice.match.arena.menu.manageschematic;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.arena.ArenaSchematic;
import library.cobelpvp.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import java.io.IOException;
import java.util.List;

final class ToggleEnabledButton extends Button {

    private final ArenaSchematic schematic;

    ToggleEnabledButton(ArenaSchematic schematic) {
        this.schematic = Preconditions.checkNotNull(schematic, "schematic");
    }

    @Override
    public String getName(Player player) {
        if (schematic.isEnabled()) {
            return ChatColor.RED + "Disable " + schematic.getName();
        } else {
            return ChatColor.GREEN + "Enable " + schematic.getName();
        }
    }

    @Override
    public List<String> getDescription(Player player) {
        if (schematic.isEnabled()) {
            return ImmutableList.of(
                "",
                ChatColor.YELLOW + "Click to disable " + schematic.getName() + ", which will prevent matches",
                ChatColor.YELLOW + "being scheduled on these arenas. Admin",
                ChatColor.YELLOW + "commands will not be impacted."
            );
        } else {
            return ImmutableList.of(
                    "",
                    ChatColor.YELLOW + "Click to enable " + schematic.getName() + ", which will allow matches",
                    ChatColor.YELLOW + "to be scheduled on these arenas."
            );
        }
    }

    @Override
    public Material getMaterial(Player player) {
        return schematic.isEnabled() ? Material.REDSTONE_BLOCK : Material.EMERALD_BLOCK;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        schematic.setEnabled(!schematic.isEnabled());
        try {
            Practice.getInstance().getArenaHandler().saveSchematics();
        } catch (IOException ex) {
            player.sendMessage(ChatColor.RED + "Failed to save " + schematic.getName() + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}