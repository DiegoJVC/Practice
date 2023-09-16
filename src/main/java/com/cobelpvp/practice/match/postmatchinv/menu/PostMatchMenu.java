package com.cobelpvp.practice.match.postmatchinv.menu;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.kittype.HealingMethod;
import library.cobelpvp.menu.Button;
import library.cobelpvp.menu.Menu;
import com.cobelpvp.practice.util.InventoryUtils;
import com.google.common.base.Preconditions;
import com.cobelpvp.practice.match.postmatchinv.PostMatchPlayer;
import com.cobelpvp.practice.match.postmatchinv.PostMatchInvHandler;
import org.bukkit.entity.Player;
import com.cobelpvp.practice.util.uuid.UniqueIDCache;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public final class PostMatchMenu extends Menu {

    private final PostMatchPlayer target;

    public PostMatchMenu(PostMatchPlayer target) {
        super("Inventory of " + UniqueIDCache.name(target.getPlayerUuid()));

        this.target = Preconditions.checkNotNull(target, "target");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int x = 0;
        int y = 0;

        List<ItemStack> targetInv = new ArrayList<>(Arrays.asList(target.getInventory()));

        for (int i = 0; i < 9; i++) {
            targetInv.add(targetInv.remove(0));
        }

        for (ItemStack inventoryItem : targetInv) {
            buttons.put(getSlot(x, y), Button.fromItem(inventoryItem));

            if (x++ > 7) {
                x = 0;
                y++;
            }
        }

        x = 3;

        for (ItemStack armorItem : target.getArmor()) {
            buttons.put(getSlot(x--, y), Button.fromItem(armorItem));
        }

        y++;

        int position = 0;
        buttons.put(getSlot(position++, y), new PostMatchHealthButton(target.getHealth()));
        buttons.put(getSlot(position++, y), new PostMatchFoodLevelButton(target.getHunger()));
        buttons.put(getSlot(position++, y), new PostMatchPotionEffectsButton(target.getPotionEffects()));

        HealingMethod healingMethod = target.getHealingMethodUsed();

        if (healingMethod != null) {
            int count = healingMethod.count(targetInv.toArray(new ItemStack[targetInv.size()]));
            buttons.put(getSlot(position++, y), new PostMatchHealsLeftButton(target.getPlayerUuid(), healingMethod, count, target.getMissedPots()));
        }

        buttons.put(getSlot(position++, y), new PostMatchStatisticsButton(target.getKit(), target.getHealingMethodUsed(), target.getTotalHits(), target.getLongestCombo(), target.getMissedPots(), target.getThrownPots()));

        PostMatchInvHandler postMatchInvHandler = Practice.getInstance().getPostMatchInvHandler();
        Collection<PostMatchPlayer> postMatchPlayers = postMatchInvHandler.getPostMatchData(player.getUniqueId()).values();

        if (postMatchPlayers.size() == 2) {
            PostMatchPlayer otherPlayer = null;

            for (PostMatchPlayer postMatchPlayer : postMatchPlayers) {
                if (!postMatchPlayer.getPlayerUuid().equals(target.getPlayerUuid())) {
                    otherPlayer = postMatchPlayer;
                }
            }

            buttons.put(getSlot(8, y), new PostMatchSwapTargetButton(otherPlayer));
        }

        return buttons;
    }

    public void onClose(Player player) {
        InventoryUtils.resetInventoryDelayed(player);
    }

}