package com.cobelpvp.practice.party;

import com.cobelpvp.atheneum.util.ItemUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import lombok.experimental.UtilityClass;
import static org.bukkit.ChatColor.*;

@UtilityClass
public final class PartyItems {

    public static final Material ICON_TYPE = Material.ENCHANTED_BOOK;

    public static final ItemStack LEAVE_PARTY_ITEM = new ItemStack(Material.INK_SACK, 1, (byte) DyeColor.ORANGE.getDyeData());
    public static final ItemStack ASSIGN_CLASSES = new ItemStack(Material.ITEM_FRAME);
    public static final ItemStack START_TEAM_SPLIT_ITEM = new ItemStack(Material.STONE_SWORD);
    public static final ItemStack START_FFA_ITEM = new ItemStack(Material.GOLD_AXE);
    public static final ItemStack OTHER_PARTIES_ITEM = new ItemStack(Material.SKULL_ITEM);

    static {
        ItemUtils.setDisplayName(LEAVE_PARTY_ITEM, RED + "Leave Party");
        ItemUtils.setDisplayName(ASSIGN_CLASSES, YELLOW + "Team Roster");
        ItemUtils.setDisplayName(START_TEAM_SPLIT_ITEM, YELLOW + "Start Team Split");
        ItemUtils.setDisplayName(START_FFA_ITEM, YELLOW + "Start Party FFA");
        ItemUtils.setDisplayName(OTHER_PARTIES_ITEM, GREEN + "Other Parties");
    }

    public static ItemStack icon(Party party) {
        ItemStack item = new ItemStack(ICON_TYPE);
        String displayName = BLUE + "Party Information";
        com.cobelpvp.atheneum.util.ItemUtils.setDisplayName(item, displayName);
        return item;
    }

}
