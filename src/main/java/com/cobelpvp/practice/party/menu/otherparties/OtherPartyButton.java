package com.cobelpvp.practice.party.menu.otherparties;

import com.cobelpvp.practice.util.uuid.UniqueIDCache;
import com.google.common.base.Preconditions;
import com.cobelpvp.practice.Practice;
import library.cobelpvp.menu.Button;
import com.cobelpvp.practice.duel.command.DuelCommand;
import com.cobelpvp.practice.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class OtherPartyButton extends Button {

    private final Party party;

    OtherPartyButton(Party party) {
        this.party = Preconditions.checkNotNull(party, "party");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_PURPLE + UniqueIDCache.name(party.getLeader());
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();

        description.add("");

        for (UUID member : party.getMembers()) {
            ChatColor color = party.isLeader(member) ? ChatColor.GOLD : ChatColor.GREEN;
            description.add(color + UniqueIDCache.name(member));
        }

        description.add("");
        description.add(ChatColor.DARK_GREEN + "» Click to duel «");
        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SKULL_ITEM;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 3; // player head
    }

    @Override
    public int getAmount(Player player) {
        return party.getMembers().size();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        Party senderParty = Practice.getInstance().getPartyHandler().getParty(player);

        if (senderParty == null) {
            return;
        }

        if (senderParty.isLeader(player.getUniqueId())) {
            DuelCommand.duel(player, Bukkit.getPlayer(party.getLeader()));
        } else {
            player.sendMessage(ChatColor.DARK_RED + "Only the leader can duel other parties!");
        }
    }

}