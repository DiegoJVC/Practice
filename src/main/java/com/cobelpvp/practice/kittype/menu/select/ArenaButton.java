package com.cobelpvp.practice.kittype.menu.select;

import com.cobelpvp.practice.match.arena.ArenaSchematic;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.cobelpvp.atheneum.util.Callback;
import com.cobelpvp.atheneum.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import java.util.ArrayList;
import java.util.List;

final class ArenaButton extends Button {

    private final ArenaSchematic arena;
    private final Callback<ArenaSchematic> callback;
    private final int amount;

    ArenaButton(ArenaSchematic arena, Callback<ArenaSchematic> callback) {
        this(arena, callback, ImmutableList.of(), 1);
    }

    ArenaButton(ArenaSchematic arena, Callback<ArenaSchematic> callback, List<String> descriptionLines, int amount) {
        this.arena = arena;
        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.amount = amount;
    }

    @Override
    public String getName(Player player) {
        if(arena == null) {
            return ChatColor.RED + "Random Map";
        }
        return ChatColor.GOLD + arena.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();

        description.add("");
        if(arena == null) {
            description.add(ChatColor.YELLOW + "Pick a random arena for this match.");
        }else {
            description.add(ChatColor.GREEN + "Click here to select " + ChatColor.GOLD + ChatColor.BOLD + arena.getName() + ChatColor.GREEN + ".");
        }

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        if(arena == null) {
            return Material.SIGN;
        }
        return arena.getIcon().getItemType();
    }

    @Override
    public int getAmount(Player player) {
        return amount;
    }

    @Override
    public byte getDamageValue(Player player) {
        if(arena == null) {
            return 0;
        }else {
            return arena.getIcon().getData();
        }
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        callback.callback(arena);
    }

}
