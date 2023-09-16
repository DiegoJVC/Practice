package com.cobelpvp.practice.kittype.menu.select;

import com.cobelpvp.atheneum.util.Callback;
import com.google.common.base.Preconditions;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.kittype.KitType;
import library.cobelpvp.menu.Button;
import library.cobelpvp.menu.Menu;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public final class SelectKitTypeMenu extends Menu {

    private final boolean reset;
    private final Callback<KitType> callback;

    public SelectKitTypeMenu(Callback<KitType> callback, String title) {
        this(callback, true, title);
    }

    public SelectKitTypeMenu(Callback<KitType> callback, boolean reset, String title) {
        super(ChatColor.BLUE + title);

        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.reset = reset;
    }

    @Override
    public void onClose(Player player, boolean manualClose) {
        if (reset) {
            InventoryUtils.resetInventoryDelayed(player);
        }
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (KitType kitType : KitType.getAllTypes()) {
            if (!player.isOp() && kitType.isHidden()) {
                continue;
            }

            buttons.put(index++, new KitTypeButton(kitType, callback));
        }

        Party party = Practice.getInstance().getPartyHandler().getParty(player);
        if (party != null) {
            buttons.put(8, new KitTypeButton(KitType.teamFight, callback));
        }
        return buttons;
    }

}