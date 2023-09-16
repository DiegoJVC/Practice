package com.cobelpvp.practice.lobby.queue.listener;

import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.practice.kittype.menu.select.CustomSelectKitTypeMenu;
import com.google.common.collect.ImmutableList;
import com.cobelpvp.atheneum.uuid.TeamsUUIDCache;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.util.listener.RankedMatchQualificationListener;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.lobby.queue.QueueHandler;
import com.cobelpvp.practice.lobby.queue.QueueItems;
import com.cobelpvp.practice.util.ItemListener;
import com.cobelpvp.practice.util.PracticeValidation;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public final class QueueItemListener extends ItemListener {

    private final Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionAdditionRanked = selectionMenuAddition(true);
    private final Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionAdditionUnranked = selectionMenuAddition(false);
    private final QueueHandler queueHandler;

    public QueueItemListener(QueueHandler queueHandler) {
        this.queueHandler = queueHandler;

        addHandler(QueueItems.JOIN_SOLO_UNRANKED_QUEUE_ITEM, joinSoloConsumer(false));
        addHandler(QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM, joinSoloConsumer(true));

        addHandler(QueueItems.JOIN_PARTY_UNRANKED_QUEUE_ITEM, joinPartyConsumer(false));
        addHandler(QueueItems.JOIN_PARTY_RANKED_QUEUE_ITEM, joinPartyConsumer(true));

        addHandler(QueueItems.LEAVE_SOLO_UNRANKED_QUEUE_ITEM, p -> queueHandler.leaveQueue(p, false));
        addHandler(QueueItems.LEAVE_SOLO_RANKED_QUEUE_ITEM, p -> queueHandler.leaveQueue(p, false));

        Consumer<Player> leaveQueuePartyConsumer = player -> {
            Party party = Practice.getInstance().getPartyHandler().getParty(player);

            if (party != null && party.isLeader(player.getUniqueId())) {
                queueHandler.leaveQueue(party, false);
            }
        };

        addHandler(QueueItems.LEAVE_PARTY_UNRANKED_QUEUE_ITEM, leaveQueuePartyConsumer);
        addHandler(QueueItems.LEAVE_PARTY_RANKED_QUEUE_ITEM, leaveQueuePartyConsumer);
    }

    private Consumer<Player> joinSoloConsumer(boolean ranked) {
        return player -> {
            if (ranked) {
                if(!player.hasPermission("practice.bypassranked"))
                    if (!RankedMatchQualificationListener.isQualified(player.getUniqueId())) {
                        int needed = RankedMatchQualificationListener.getWinsNeededToQualify(player.getUniqueId());
                        player.sendMessage(ChatColor.RED + "You can't join ranked queues with less than " + RankedMatchQualificationListener.MIN_MATCH_WINS + " unranked 1v1 wins. You need " + needed + " more wins!");
                        return;
                    }
            }

            if (PracticeValidation.canJoinQueue(player)) {
                new CustomSelectKitTypeMenu(kitType -> {
                    queueHandler.joinQueue(player, kitType, ranked);
                    player.closeInventory();
                }, ranked ? selectionAdditionRanked : selectionAdditionUnranked, "" + (ranked ? "§aSelect a Ranked Queue" : "§9Select a Un-Ranked Queue"), ranked).openMenu(player);
            }
        };
    }

    private Consumer<Player> joinPartyConsumer(boolean ranked) {
        return player -> {
            Party party = Practice.getInstance().getPartyHandler().getParty(player);

            if (party == null || !party.isLeader(player.getUniqueId())) {
                return;
            }

            if (ranked) {
                for (UUID member : party.getMembers()) {
                    if (!RankedMatchQualificationListener.isQualified(member)) {
                        int needed = RankedMatchQualificationListener.getWinsNeededToQualify(member);
                        player.sendMessage(ChatColor.RED + "Your party can't join ranked queues because " + TeamsUUIDCache.name(member) + " has less than " + RankedMatchQualificationListener.MIN_MATCH_WINS + " unranked 1v1 wins. They need " + needed + " more wins!");
                        return;
                    }
                }
            }

            if (PracticeValidation.canJoinQueue(party)) {
                new CustomSelectKitTypeMenu(kitType -> {
                    queueHandler.joinQueue(party, kitType, ranked);
                    player.closeInventory();
                }, ranked ? selectionAdditionRanked : selectionAdditionUnranked, "" + (ranked ? "§aSelect a Ranked Queue" : "§9Select a Un-Ranked Queue"), ranked).openMenu(player);
            }
        };
    }

    private Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionMenuAddition(boolean ranked) {
        return kitType -> {
            MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

            int inFightsRanked = matchHandler.countPlayersPlayingMatches(m -> m.getKitType() == kitType && m.isRanked());
            int inQueueRanked = queueHandler.countPlayersQueued(kitType, true);
            int inFightsUnranked = matchHandler.countPlayersPlayingMatches(m -> m.getKitType() == kitType && !m.isRanked());
            int inQueueUnranked = queueHandler.countPlayersQueued(kitType, false);

            return new CustomSelectKitTypeMenu.CustomKitTypeMeta(
                    Math.max(1, Math.min(64, ranked ? inQueueRanked + inFightsRanked : inQueueUnranked + inFightsUnranked)),
                    ranked ?  ImmutableList.of(
                            ChatColor.YELLOW + "In queue: " + ChatColor.GREEN + inQueueRanked,
                            ChatColor.YELLOW + "In fights: " + ChatColor.GREEN + inFightsRanked) :
                            ImmutableList.of(
                                    ChatColor.YELLOW + "In queue: " + ChatColor.GREEN + inQueueUnranked,
                                    ChatColor.YELLOW + "In fights: " + ChatColor.GREEN + inFightsUnranked
                            )
            );
        };
    }

}
