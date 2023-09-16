package com.cobelpvp.practice.match;

import com.cobelpvp.practice.follow.FollowHandler;
import com.cobelpvp.practice.party.PartyHandler;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.lobby.LobbyItems;
import com.cobelpvp.practice.lobby.setting.Setting;
import com.cobelpvp.practice.lobby.setting.SettingHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class MatchUtils {

    public static void resetInventory(Player player) {
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        FollowHandler followHandler = Practice.getInstance().getFollowHandler();
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        Match match = matchHandler.getMatchSpectating(player);

        if (match == null) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);

        if (match.getState() != MatchState.ENDING) {
            boolean canViewInventories = player.hasPermission("practice.admin");

            if (!canViewInventories) {
                for (MatchTeam team : match.getTeams()) {
                    if (team.getAllMembers().contains(player.getUniqueId())) {
                        canViewInventories = true;
                        break;
                    }
                }
            }

            if (canViewInventories) {
                inventory.setItem(0, SpectatorItems.VIEW_INVENTORY_ITEM);
            }
            int slot = canViewInventories ? 1 : 0;

            if (settingHandler.getSetting(player, Setting.VIEW_OTHER_SPECTATORS)) {
                inventory.setItem(slot, SpectatorItems.HIDE_SPECTATORS_ITEM);
            } else {
                inventory.setItem(slot, SpectatorItems.SHOW_SPECTATORS_ITEM);
            }

            if (partyHandler.hasParty(player)) {
                inventory.setItem(8, SpectatorItems.LEAVE_PARTY_ITEM);
            } else {
                inventory.setItem(8, SpectatorItems.RETURN_TO_LOBBY_ITEM);

                if (!followHandler.getFollowing(player).isPresent()) {
                    inventory.setItem(3, LobbyItems.SPECTATE_RANDOM_ITEM);
                    inventory.setItem(5, LobbyItems.SPECTATE_MENU_ITEM);
                    inventory.setItem(8, SpectatorItems.RETURN_TO_LOBBY_ITEM);
                }else{
                    if (followHandler.getFollowing(player).isPresent()) {
                        inventory.setItem(8, SpectatorItems.RETURN_TO_LOBBY_ITEM);
                    }
                }
            }

            Bukkit.getScheduler().runTaskLater(Practice.getInstance(), player::updateInventory, 1L);
        }

    }
}