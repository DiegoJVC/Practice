package com.cobelpvp.practice.commands;

import com.cobelpvp.practice.kittype.menu.manage.ManageKitTypeMenu;
import com.cobelpvp.practice.kittype.menu.select.SelectKitTypeMenu;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import library.cobelpvp.menu.Button;
import com.cobelpvp.practice.match.arena.menu.manageschematics.ManageSchematicsMenu;
import com.cobelpvp.atheneum.command.Command;
import library.cobelpvp.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import java.util.List;
import java.util.Map;

public final class CmdPracticeAdmin {

    @Command(names = {"practice admin"}, permission = "practice.administrator")
    public static void manage(Player sender) {
        new ManageMenu().openMenu(sender);
    }

    public static class ManageMenu extends Menu {

        public ManageMenu() {
            super("Admin Management Menu");
        }

        @Override
        public Map<Integer, Button> getButtons(Player player) {
            return ImmutableMap.of(
                3, new ManageKitButton(),
                5, new ManageArenaButton()
            );
        }

    }

    private static class ManageKitButton extends Button {

        @Override
        public String getName(Player player) {
            return ChatColor.GREEN + "Manage kit type definitions";
        }

        @Override
        public List<String> getDescription(Player player) {
            return ImmutableList.of();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.DIAMOND_CHESTPLATE;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
            player.closeInventory();

            new SelectKitTypeMenu((kitType) -> {
                player.closeInventory();
                new ManageKitTypeMenu(kitType).openMenu(player);
            }, false, "&aManage Kit Type").openMenu(player);
        }

    }

    private static class ManageArenaButton extends Button {

        @Override
        public String getName(Player player) {
            return ChatColor.GOLD + "Manage the arena grid";
        }

        @Override
        public List<String> getDescription(Player player) {
            return ImmutableList.of();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.DIRT;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
            player.closeInventory();
            new ManageSchematicsMenu().openMenu(player);
        }

    }

}