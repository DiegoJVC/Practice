package com.cobelpvp.practice.lobby.menu;

import java.util.HashMap;
import java.util.Map;
import com.cobelpvp.atheneum.menu.Menu;
import com.cobelpvp.atheneum.util.ItemBuilder;
import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.practice.lobby.menu.statistics.*;
import com.cobelpvp.atheneum.menu.Button;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class StatisticsMenu extends Menu {

    private static final Button BLACK_GLASS_PANEL = Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).data(DyeColor.BLACK.getData()).name(" ").build());

    public StatisticsMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Statistics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 45; i++) {
            buttons.put(i, BLACK_GLASS_PANEL);
        }

        buttons.put(getSlot(3, 4), new PlayerButton());
        buttons.put(getSlot(5, 4), new RecordsButton());
        buttons.put(getSlot(4, 0), new GlobalEloButton());

        int[] kitSlots = {20,21,22,23,24,25,29,30,31,32,33,34};
        int e = 0;

        for (KitType kitType : KitType.getAllTypes()) {
            if (!kitType.isSupportsRanked()) continue;
            if (kitType.getId().contains("_")) continue;
            if (kitType.isHidden()) continue;
            buttons.put(kitSlots[e], new KitButton(kitType));
            ++e;
        }

        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return super.size(buttons);
    }
}