package com.cobelpvp.practice.kit.menu;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.kit.Kit;
import com.cobelpvp.practice.kit.KitHandler;
import library.cobelpvp.menu.Button;
import com.cobelpvp.practice.kittype.KitType;
import library.cobelpvp.menu.Menu;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class KitsMenu extends Menu {

    private final KitType kitType;

    public KitsMenu(KitType kitType) {
        super("Viewing " + kitType.getDisplayName() + " kits");

        setPlaceholder(true);
        setAutoUpdate(true);

        this.kitType = kitType;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        KitHandler kitHandler = Practice.getInstance().getKitHandler();
        Map<Integer, Button> buttons = new HashMap<>();

        // kit slots are 1-indexed
        for (int kitSlot = 1; kitSlot <= KitHandler.KITS_PER_TYPE; kitSlot++) {
            Optional<Kit> kitOpt = kitHandler.getKit(player, kitType, kitSlot);
            int column = (kitSlot * 2) - 1; // - 1 to compensate for this being 0-indexed

            buttons.put(getSlot(column, 1), new KitIconButton(kitOpt, kitType, kitSlot));
            //buttons.put(getSlot(column, 2), new KitEditButton(kitOpt, kitType, kitSlot));


            if (kitOpt.isPresent()) {
                int finalKitSlot = kitSlot;
                Kit resolvedKit = kitOpt.orElseGet(() -> kitHandler.saveDefaultKit(player, kitType, finalKitSlot));
                buttons.put(getSlot(column, 2), new KitRenameButton(kitOpt.get()));
                buttons.put(getSlot(column, 4), new KitDeleteButton(kitType, kitSlot));
            } else {
                buttons.put(getSlot(column, 2), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.RED.getWoolData(), ""));
                buttons.put(getSlot(column, 3), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.RED.getWoolData(), ""));
                buttons.put(getSlot(column, 4), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.RED.getWoolData(), ""));
                buttons.put(getSlot(column, 5), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.BLACK.getWoolData(), ""));
            }
        }

        return buttons;
    }

}