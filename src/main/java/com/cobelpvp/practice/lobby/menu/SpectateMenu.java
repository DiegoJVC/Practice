package com.cobelpvp.practice.lobby.menu;

import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchState;
import com.cobelpvp.practice.match.MatchTeam;
import com.cobelpvp.practice.Practice;
import library.cobelpvp.menu.Button;
import com.cobelpvp.practice.lobby.setting.Setting;
import com.cobelpvp.practice.lobby.setting.SettingHandler;
import library.cobelpvp.menu.pagination.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SpectateMenu extends PaginatedMenu {
    
    public SpectateMenu() {
        setAutoUpdate(true);
    }
    
    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Spectate a match";
    }
    
    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 0;
        
        for (Match match : Practice.getInstance().getMatchHandler().getHostedMatches()) {
            if (match.isSpectator(player.getUniqueId())) {
                continue;
            }
            
            if (match.getState() == MatchState.ENDING) {
                continue;
            }
            
            if (!Practice.getInstance().getTournamentHandler().isInTournament(match)) {
                int numTotalPlayers = 0;
                int numSpecDisabled = 0;
                
                for (MatchTeam team : match.getTeams()) {
                    for (UUID member : team.getAliveMembers()) {
                        numTotalPlayers++;
                        
                        if (!settingHandler.getSetting(Bukkit.getPlayer(member), Setting.ALLOW_SPECTATORS)) {
                            numSpecDisabled++;
                        }
                    }
                }
                
                if ((float) numSpecDisabled / (float) numTotalPlayers >= 0.5) {
                    continue;
                }
            }
            
            buttons.put(i++, new SpectateButton(match));
        }
        
        return buttons;
    }

    @Override
    public int size(@NotNull Map<Integer, ? extends Button> buttons) {
        return 9 * 6;
    }
    
}