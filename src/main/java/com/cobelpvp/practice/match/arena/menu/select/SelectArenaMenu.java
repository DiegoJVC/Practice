package com.cobelpvp.practice.match.arena.menu.select;

import java.util.Map;
import java.util.Set;

import com.cobelpvp.atheneum.util.Callback;
import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.practice.kittype.menu.select.ToggleAllButton;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.Practice;
import library.cobelpvp.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.cobelpvp.practice.match.arena.ArenaSchematic;
import com.cobelpvp.practice.kittype.menu.select.SendDuelButton;
import library.cobelpvp.menu.Menu;

public class SelectArenaMenu extends Menu {
    
    private KitType kitType;
    private Callback<Set<String>> mapsCallback;
    Set<String> allMaps;
    Set<String> enabledSchematics = Sets.newHashSet();
    
    public SelectArenaMenu(KitType kitType, Callback<Set<String>> mapsCallback, String title) {
        super(ChatColor.BLUE.toString() + ChatColor.BOLD + title);

        this.kitType = kitType;
        this.mapsCallback = mapsCallback;
        
        for (ArenaSchematic schematic : Practice.getInstance().getArenaHandler().getSchematics()) {
            if (MatchHandler.canUseSchematic(this.kitType, schematic)) {
                enabledSchematics.add(schematic.getName());
            }
        }
        
        this.allMaps = ImmutableSet.copyOf(enabledSchematics);
    }

    @Override
    public Map<Integer, Button> getButtons(Player arg0) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        int i = 0;
        for (String mapName : allMaps) {
            buttons.put(i++, new ArenaButton(mapName, enabledSchematics));
        }
        
        int bottomRight = 8;
        while (buttons.get(bottomRight) != null) {
            bottomRight += 9;
        }
        
        bottomRight += 9;
        
        buttons.put(bottomRight, new ToggleAllButton(allMaps, enabledSchematics));
        buttons.put(bottomRight - 8, new SendDuelButton(enabledSchematics, mapsCallback));
        
        return buttons;
    }
    
}
