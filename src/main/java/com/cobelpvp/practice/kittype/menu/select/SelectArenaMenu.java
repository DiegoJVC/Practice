package com.cobelpvp.practice.kittype.menu.select;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.arena.ArenaHandler;
import com.cobelpvp.practice.match.arena.ArenaSchematic;
import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.util.InventoryUtils;
import com.google.common.base.Preconditions;
import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.atheneum.menu.Menu;
import com.cobelpvp.atheneum.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class SelectArenaMenu extends Menu {

    private final boolean reset;
    private final Callback<ArenaSchematic> callback;
    private final KitType kit;

    public SelectArenaMenu(Callback<ArenaSchematic> callback, String title, KitType kit) {
        this(callback, true, title, kit);
    }

    public SelectArenaMenu(Callback<ArenaSchematic> callback, boolean reset, String title, KitType kit) {
        super(ChatColor.BLUE.toString() + ChatColor.BOLD + title);

        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.reset = reset;
        this.kit = kit;
    }

    @Override
    public void onClose(Player player) {
        if (reset) {
            InventoryUtils.resetInventoryDelayed(player);
        }
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();

        for (ArenaSchematic schematic : arenaHandler.getSchematics()) {
            if (schematic.isEnabled() &&
                    !schematic.isTeamFightsOnly() &&
                    MatchHandler.canUseSchematic(kit, schematic) &&
                    (kit.getId().equals("ARCHER") || !schematic.isArcherOnly())) {
                buttons.put(index++, new ArenaButton(schematic, callback));
            }
        }

        buttons.put(index++, new ArenaButton(null, callback));

        return buttons;
    }

}
