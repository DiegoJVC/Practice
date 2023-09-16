package com.cobelpvp.practice.kit.menu;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.kit.Kit;
import com.cobelpvp.practice.kit.KitHandler;
import com.cobelpvp.practice.kittype.KitType;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import library.cobelpvp.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.util.List;
import java.util.Optional;

final class KitEditButton extends Button {

    private final Optional<Kit> kitOpt;
    private final KitType kitType;
    private final int slot;

    KitEditButton(Optional<Kit> kitOpt, KitType kitType, int slot) {
        this.kitOpt = Preconditions.checkNotNull(kitOpt, "kitOpt");
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.slot = slot;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + ChatColor.BOLD + "Load/Edit";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.YELLOW + "Click to edit this kit."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.BOOK;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        if (kitOpt.isPresent()) {
            Kit kit = kitOpt.get();
            kit.apply(player);
            return;
        }
        kitOpt.orElseGet(() -> {
            KitHandler kitHandler = Practice.getInstance().getKitHandler();
            return kitHandler.saveDefaultKit(player, kitType, this.slot);
        });

    }

}