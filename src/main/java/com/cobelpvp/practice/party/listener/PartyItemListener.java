package com.cobelpvp.practice.party.listener;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.party.PartyHandler;
import com.cobelpvp.practice.party.PartyItems;
import com.cobelpvp.practice.party.command.PartyFfaCommand;
import com.cobelpvp.practice.party.command.PartyInfoCommand;
import com.cobelpvp.practice.party.command.PartyLeaveCommand;
import com.cobelpvp.practice.party.command.PartyTeamSplitCommand;
import com.cobelpvp.practice.party.menu.RosterMenu;
import com.cobelpvp.practice.party.menu.otherparties.OtherPartiesMenu;
import com.cobelpvp.practice.util.ItemListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public final class PartyItemListener extends ItemListener {

    public PartyItemListener(PartyHandler partyHandler) {
        addHandler(PartyItems.LEAVE_PARTY_ITEM, PartyLeaveCommand::partyLeave);
        addHandler(PartyItems.START_TEAM_SPLIT_ITEM, PartyTeamSplitCommand::partyTeamSplit);
        addHandler(PartyItems.START_FFA_ITEM, PartyFfaCommand::partyFfa);
        addHandler(PartyItems.OTHER_PARTIES_ITEM, p -> new OtherPartiesMenu().openMenu(p));
        addHandler(PartyItems.ASSIGN_CLASSES, p -> new RosterMenu(partyHandler.getParty(p)).openMenu(p));
    }

    @EventHandler
    public void fastPartyIcon(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        if (event.getItem().getType() != PartyItems.ICON_TYPE) {
            return;
        }

        boolean permitted = canUseButton.getOrDefault(event.getPlayer().getUniqueId(), 0L) < System.currentTimeMillis();

        if (permitted) {
            Player player = event.getPlayer();
            Party party = Practice.getInstance().getPartyHandler().getParty(player);

            if (party != null && PartyItems.icon(party).isSimilar(event.getItem())) {
                event.setCancelled(true);
                PartyInfoCommand.partyInfo(player, player);
            }
            canUseButton.put(player.getUniqueId(), System.currentTimeMillis() + 500);
        }
    }

}