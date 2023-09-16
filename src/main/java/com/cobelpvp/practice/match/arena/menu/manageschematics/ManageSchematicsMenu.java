package com.cobelpvp.practice.match.arena.menu.manageschematics;

import com.cobelpvp.practice.commands.CmdPracticeAdmin;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.arena.ArenaHandler;
import com.cobelpvp.practice.match.arena.ArenaSchematic;
import library.cobelpvp.menu.Button;
import library.cobelpvp.menu.Menu;
import com.cobelpvp.practice.util.menu.MenuBackButton;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public final class ManageSchematicsMenu extends Menu {

    public ManageSchematicsMenu() {
        super("Manage schematics");
        setAutoUpdate(true);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        buttons.put(index++, new MenuBackButton(p -> new CmdPracticeAdmin.ManageMenu().openMenu(p)));

        for (ArenaSchematic schematic : arenaHandler.getSchematics()) {
            buttons.put(index++, new ManageSchematicButton(schematic));
        }

        return buttons;
    }

}