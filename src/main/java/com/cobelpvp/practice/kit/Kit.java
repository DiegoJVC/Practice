package com.cobelpvp.practice.kit;

import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.practice.util.ItemUtils;
import com.cobelpvp.practice.util.PatchedPlayerUtils;
import com.cobelpvp.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import lombok.Getter;
import lombok.Setter;

public final class Kit {

    @Getter @Setter private String name;
    @Getter @Setter private int slot;
    @Getter @Setter private KitType type;
    @Getter @Setter private ItemStack[] inventoryContents;

    public static Kit ofDefaultKitCustomName(KitType kitType, String name) {
        return ofDefaultKit(kitType, name, 0);
    }

    public static Kit ofDefaultKit(KitType kitType) {
        return ofDefaultKit(kitType, "Default", 0);
    }

    public static Kit ofDefaultKit(KitType kitType, String name, int slot) {
        Kit kit = new Kit();

        kit.setName(name);
        kit.setType(kitType);
        kit.setSlot(slot);
        kit.setInventoryContents(kitType.getDefaultInventory());

        return kit;
    }

    public void apply(Player player) {
        PatchedPlayerUtils.resetInventory(player);

        player.getInventory().setArmorContents(type.getDefaultArmor());
        player.getInventory().setContents(inventoryContents);

        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), player::updateInventory, 1L);
    }

    public int countHeals() {
        return ItemUtils.countStacksMatching(inventoryContents, ItemUtils.INSTANT_HEAL_POTION_PREDICATE);
    }

    public int countDebuffs() {
        return ItemUtils.countStacksMatching(inventoryContents, ItemUtils.DEBUFF_POTION_PREDICATE);
    }

    public int countFood() {
        return ItemUtils.countStacksMatching(inventoryContents, ItemUtils.EDIBLE_PREDICATE);
    }

    public int countPearls() {
        return ItemUtils.countStacksMatching(inventoryContents, v -> v.getType() == Material.ENDER_PEARL);
    }

    public boolean isSelectionItem(ItemStack itemStack) {
        if (itemStack.getType() != Material.ENCHANTED_BOOK) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.YELLOW.toString() + ChatColor.BOLD + name);
    }

    public ItemStack createSelectionItem() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + name);
        item.setItemMeta(itemMeta);
        return item;
    }

}