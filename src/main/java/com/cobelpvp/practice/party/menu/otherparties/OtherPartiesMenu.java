package com.cobelpvp.practice.party.menu.otherparties;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.cobelpvp.practice.Practice;
import library.cobelpvp.menu.Button;
import com.cobelpvp.practice.lobby.LobbyHandler;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.party.PartyHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.cobelpvp.practice.lobby.setting.Setting;
import com.cobelpvp.practice.lobby.setting.SettingHandler;
import library.cobelpvp.menu.pagination.PaginatedMenu;

public final class OtherPartiesMenu extends PaginatedMenu {

    public OtherPartiesMenu() {
        setPlaceholder(true);
        setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Parties";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        LobbyHandler lobbyHandler = Practice.getInstance().getLobbyHandler();

        Map<Integer, Button> buttons = new HashMap<>();
        List<Party> parties = new ArrayList<>(partyHandler.getParties());
        int index = 0;

        parties.sort(Comparator.comparing(p -> p.getMembers().size()));

        for (Party party : parties) {
            if (party.isMember(player.getUniqueId())) {
                continue;
            }

            if (!lobbyHandler.isInLobby(Bukkit.getPlayer(party.getLeader()))) {
                continue;
            }

            if (!settingHandler.getSetting(Bukkit.getPlayer(party.getLeader()), Setting.RECEIVE_DUELS)) {
                continue;
            }

            buttons.put(index++, new OtherPartyButton(party));
        }

        return buttons;
    }

    @Override
    public int size(Map<Integer, ? extends Button> buttons) {
        return 9 * 6;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 9 * 5; // top row is dedicated to switching
    }
}