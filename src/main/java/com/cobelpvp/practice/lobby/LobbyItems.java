package com.cobelpvp.practice.lobby;

import com.cobelpvp.atheneum.util.ItemUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import lombok.experimental.UtilityClass;
import static org.bukkit.ChatColor.*;

@UtilityClass
public final class LobbyItems {

    public static final ItemStack SPECTATE_RANDOM_ITEM = new ItemStack(Material.SKULL_ITEM);
    public static final ItemStack SPECTATE_MENU_ITEM = new ItemStack(Material.PAPER);
    public static final ItemStack ENABLE_SPEC_MODE_ITEM = new ItemStack(Material.BOOK_AND_QUILL);
    public static final ItemStack DISABLE_SPEC_MODE_ITEM = new ItemStack(Material.LEVER);
    public static final ItemStack UNFOLLOW_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
    public static final ItemStack PLAYER_STATISTICS = new ItemStack(Material.SIGN);
    public static final ItemStack PARTY_CREATE = new ItemStack(Material.ENDER_CHEST);

    static {
        ItemUtils.setDisplayName(SPECTATE_RANDOM_ITEM, GREEN + "Spectate Random Match");
        ItemUtils.setDisplayName(SPECTATE_MENU_ITEM, BLUE + "Spectate Menu");
        ItemUtils.setDisplayName(ENABLE_SPEC_MODE_ITEM, BLUE + "Enable Spectator Mode");
        ItemUtils.setDisplayName(DISABLE_SPEC_MODE_ITEM, BLUE+ "Disable Spectator Mode");
        ItemUtils.setDisplayName(UNFOLLOW_ITEM, DARK_RED + "Stop Following");
        ItemUtils.setDisplayName(PLAYER_STATISTICS, GOLD + "Leaderboards");
        ItemUtils.setDisplayName(PARTY_CREATE, LIGHT_PURPLE + "Party Create");
    }

}