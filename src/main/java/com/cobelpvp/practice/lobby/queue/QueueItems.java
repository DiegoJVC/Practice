package com.cobelpvp.practice.lobby.queue;

import com.cobelpvp.atheneum.util.ItemUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import lombok.experimental.UtilityClass;
import static org.bukkit.ChatColor.*;

@UtilityClass
public final class QueueItems {

        public static final ItemStack JOIN_SOLO_UNRANKED_QUEUE_ITEM = new ItemStack(Material.IRON_SWORD);
        public static final ItemStack LEAVE_SOLO_UNRANKED_QUEUE_ITEM = new ItemStack(Material.INK_SACK, 1, (byte) DyeColor.ORANGE.getDyeData());
        public static final ItemStack JOIN_SOLO_RANKED_QUEUE_ITEM = new ItemStack(Material.DIAMOND_SWORD);
        public static final ItemStack LEAVE_SOLO_RANKED_QUEUE_ITEM = new ItemStack(Material.INK_SACK, 1, (byte) DyeColor.ORANGE.getDyeData());

        public static final ItemStack JOIN_PARTY_UNRANKED_QUEUE_ITEM = new ItemStack(Material.IRON_SWORD);
        public static final ItemStack LEAVE_PARTY_UNRANKED_QUEUE_ITEM = new ItemStack(Material.INK_SACK, 1, (byte) DyeColor.ORANGE.getDyeData());

        public static final ItemStack JOIN_PARTY_RANKED_QUEUE_ITEM = new ItemStack(Material.DIAMOND_SWORD);
        public static final ItemStack LEAVE_PARTY_RANKED_QUEUE_ITEM = new ItemStack(Material.INK_SACK, 1, (byte) DyeColor.ORANGE.getDyeData());

        static {
            ItemUtils.setDisplayName(JOIN_SOLO_UNRANKED_QUEUE_ITEM, BLUE + "Un-Ranked Queue");
            ItemUtils.setDisplayName(LEAVE_SOLO_UNRANKED_QUEUE_ITEM, RED + "Right click to leave " + YELLOW + "Un-Ranked" + RED + " queue");

            ItemUtils.setDisplayName(JOIN_SOLO_RANKED_QUEUE_ITEM, GREEN + "Ranked Queue");
            ItemUtils.setDisplayName(LEAVE_SOLO_RANKED_QUEUE_ITEM, RED + "Right click to leave " + YELLOW + "Ranked" + RED + " queue");

            ItemUtils.setDisplayName(JOIN_PARTY_UNRANKED_QUEUE_ITEM, BLUE + "Play 2v2 Un-Ranked");
            ItemUtils.setDisplayName(LEAVE_PARTY_UNRANKED_QUEUE_ITEM, RED + "Right click to leave" + YELLOW + " 2v2 Un-Ranked " + RED + "queue");

            ItemUtils.setDisplayName(JOIN_PARTY_RANKED_QUEUE_ITEM, GREEN + "2v2 Ranked Queue");
            ItemUtils.setDisplayName(LEAVE_PARTY_RANKED_QUEUE_ITEM, RED + "Right click to leave"+ YELLOW + " 2v2 Ranked " + RED + "queue");
        }

}
