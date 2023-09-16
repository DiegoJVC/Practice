package com.cobelpvp.practice.lobby;

import com.cobelpvp.practice.follow.FollowHandler;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.party.PartyHandler;
import com.cobelpvp.practice.party.PartyItems;
import com.cobelpvp.practice.lobby.queue.QueueHandler;
import com.cobelpvp.practice.lobby.queue.QueueItems;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.duel.DuelHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class LobbyUtils {

    public static void resetInventory(Player player) {

        if (Practice.getInstance().getMatchHandler().isPlayingOrSpectatingMatch(player)) return;

        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);

        if (partyHandler.hasParty(player)) {
            renderPartyItems(player, inventory, partyHandler.getParty(player));
        } else {
            renderSoloItems(player, inventory);
        }

        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), player::updateInventory, 1L);
    }

    private void renderPartyItems(Player player, PlayerInventory inventory, Party party) {
        QueueHandler queueHandler = Practice.getInstance().getQueueHandler();

        if (party.isLeader(player.getUniqueId())) {
            int partySize = party.getMembers().size();

            if (partySize == 2) {
                if (!queueHandler.isQueuedUnranked(party)) {
                    inventory.setItem(1, QueueItems.JOIN_PARTY_UNRANKED_QUEUE_ITEM);
                    inventory.setItem(3, PartyItems.ASSIGN_CLASSES);
                } else {
                    inventory.setItem(1, QueueItems.LEAVE_PARTY_UNRANKED_QUEUE_ITEM);
                }

                if (!queueHandler.isQueuedRanked(party)) {
                    inventory.setItem(2, QueueItems.JOIN_PARTY_RANKED_QUEUE_ITEM);
                    inventory.setItem(3, PartyItems.ASSIGN_CLASSES);
                } else {
                    inventory.setItem(2, QueueItems.LEAVE_PARTY_RANKED_QUEUE_ITEM);
                }
            } else if (partySize > 2 && !queueHandler.isQueued(party)) {
                inventory.setItem(1, PartyItems.START_TEAM_SPLIT_ITEM);
                inventory.setItem(2, PartyItems.START_FFA_ITEM);
                inventory.setItem(3, PartyItems.ASSIGN_CLASSES);
            }

        } else {
            int partySize = party.getMembers().size();
            if (partySize >= 2) {
                inventory.setItem(1, PartyItems.ASSIGN_CLASSES);
            }
        }

        inventory.setItem(0, PartyItems.icon(party));
        inventory.setItem(6, PartyItems.OTHER_PARTIES_ITEM);
        inventory.setItem(8, PartyItems.LEAVE_PARTY_ITEM);
    }

    private void renderSoloItems(Player player, PlayerInventory inventory) {
        QueueHandler queueHandler = Practice.getInstance().getQueueHandler();
        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();
        FollowHandler followHandler = Practice.getInstance().getFollowHandler();
        LobbyHandler lobbyHandler = Practice.getInstance().getLobbyHandler();

        boolean specMode = lobbyHandler.isInSpectatorMode(player);
        boolean followingSomeone = followHandler.getFollowing(player).isPresent();

        player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE || specMode);

        if (specMode || followingSomeone) {
            inventory.setItem(5, LobbyItems.SPECTATE_MENU_ITEM);
            inventory.setItem(3, LobbyItems.SPECTATE_RANDOM_ITEM);
            inventory.setItem(4, LobbyItems.DISABLE_SPEC_MODE_ITEM);

            if (followingSomeone) {
                inventory.setItem(8, LobbyItems.UNFOLLOW_ITEM);
            }
        } else {
            if (queueHandler.isQueuedRanked(player.getUniqueId())) {
                inventory.setItem(8, QueueItems.LEAVE_SOLO_RANKED_QUEUE_ITEM);

            } else if (queueHandler.isQueuedUnranked(player.getUniqueId())) {
                inventory.setItem(8, QueueItems.LEAVE_SOLO_UNRANKED_QUEUE_ITEM);
            } else {
                inventory.setItem(0, LobbyItems.PARTY_CREATE);
                inventory.setItem(7, QueueItems.JOIN_SOLO_UNRANKED_QUEUE_ITEM);
                inventory.setItem(8, QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM);
                inventory.setItem(4, LobbyItems.PLAYER_STATISTICS);
            }
        }
    }
}