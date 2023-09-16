package com.cobelpvp.practice.lobby.queue;

import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.practice.lobby.queue.listener.QueueGeneralListener;
import com.cobelpvp.practice.lobby.queue.listener.QueueItemListener;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.elo.EloHandler;
import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.util.InventoryUtils;
import com.cobelpvp.practice.util.PracticeValidation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

public final class QueueHandler {

    public static final int RANKED_WINDOW_GROWTH_PER_SECOND = 15;

    private static final String JOIN_SOLO_MESSAGE = ChatColor.GREEN + "§eYou joined to %s %s";
    private static final String LEAVE_SOLO_MESSAGE = ChatColor.GREEN + "§eYou were removed from the %s %s §equeue.";
    private static final String JOIN_PARTY_MESSAGE = ChatColor.GREEN + "§eYour party joined to %s %s §equeue.";
    private static final String LEAVE_PARTY_MESSAGE = ChatColor.GREEN + "§eYour party is no longer queued for %s %s §equeue.";

    private final Table<KitType, Boolean, MatchQueue> soloQueues = HashBasedTable.create();
    private final Table<KitType, Boolean, MatchQueue> partyQueues = HashBasedTable.create();

    private final Map<UUID, SoloMatchQueueEntry> soloQueueCache = new ConcurrentHashMap<>();
    private final Map<Party, PartyMatchQueueEntry> partyQueueCache = new ConcurrentHashMap<>();

    @Getter private int queuedCount = 0;

    public QueueHandler() {
        Bukkit.getPluginManager().registerEvents(new QueueGeneralListener(this), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new QueueItemListener(this), Practice.getInstance());

        for (KitType kitType : KitType.getAllTypes()) {
            soloQueues.put(kitType, true, new MatchQueue(kitType, true));
            soloQueues.put(kitType, false, new MatchQueue(kitType, false));

            partyQueues.put(kitType, true, new MatchQueue(kitType, true));
            partyQueues.put(kitType, false, new MatchQueue(kitType, false));
        }

        Bukkit.getScheduler().runTaskTimer(Practice.getInstance(), () -> {
            soloQueues.values().forEach(MatchQueue::tick);
            partyQueues.values().forEach(MatchQueue::tick);

            int i = 0;

            for (MatchQueue queue : soloQueues.values()) {
                i += queue.countPlayersQueued();
            }

            for (MatchQueue queue : partyQueues.values()) {
                i += queue.countPlayersQueued();
            }

            queuedCount = i;
        }, 20L, 20L);
    }

    public void addQueues(KitType kitType) {
        soloQueues.put(kitType, true, new MatchQueue(kitType, true));
        soloQueues.put(kitType, false, new MatchQueue(kitType, false));

        partyQueues.put(kitType, true, new MatchQueue(kitType, true));
        partyQueues.put(kitType, false, new MatchQueue(kitType, false));
    }

    public void removeQueues(KitType kitType) {
        soloQueues.remove(kitType, true);
        soloQueues.remove(kitType, false);

        partyQueues.remove(kitType, true);
        partyQueues.remove(kitType, false);
    }

    public int countPlayersQueued(KitType kitType, boolean ranked) {
        return soloQueues.get(kitType, ranked).countPlayersQueued() +
                partyQueues.get(kitType, ranked).countPlayersQueued();
    }

    public boolean joinQueue(Player player, KitType kitType, boolean ranked) {
        if (!PracticeValidation.canJoinQueue(player)) {
            return false;
        }

        MatchQueue queue = soloQueues.get(kitType, ranked);
        SoloMatchQueueEntry entry = new SoloMatchQueueEntry(queue, player.getUniqueId());
        queue.addToQueue(entry);
        soloQueueCache.put(player.getUniqueId(), entry);
        EloHandler eloHandler = Practice.getInstance().getEloHandler();
        player.sendMessage(String.format(JOIN_SOLO_MESSAGE, ChatColor.GREEN + kitType.getDisplayName() , ranked ? ChatColor.YELLOW + "ranked queue with " + ChatColor.GREEN + eloHandler.getElo(player , kitType) +" elo." : ChatColor.YELLOW + "un-ranked queue."));
        InventoryUtils.resetInventoryDelayed(player);
        return true;
    }

    public boolean leaveQueue(Player player, boolean silent) {
        MatchQueueEntry entry = getQueueEntry(player.getUniqueId());

        if (entry == null) {
            return false;
        }

        MatchQueue queue = entry.getQueue();
        queue.removeFromQueue(entry);
        soloQueueCache.remove(player.getUniqueId());

        if (!silent) {
            player.sendMessage(String.format(LEAVE_SOLO_MESSAGE, ChatColor.GREEN + queue.getKitType().getDisplayName(), queue.isRanked() ? ChatColor.YELLOW + "ranked" : "un-ranked"));
        }

        InventoryUtils.resetInventoryDelayed(player);
        return true;
    }

    public boolean joinQueue(Party party, KitType kitType, boolean ranked) {
        if (!PracticeValidation.canJoinQueue(party)) {
            return false;
        }

        MatchQueue queue = partyQueues.get(kitType, ranked);
        PartyMatchQueueEntry entry = new PartyMatchQueueEntry(queue, party);
        queue.addToQueue(entry);
        partyQueueCache.put(party, entry);
        party.message(String.format(JOIN_PARTY_MESSAGE, ChatColor.GREEN + kitType.getDisplayName() , ranked ? ChatColor.YELLOW + "ranked 2v2" : "un-ranked 2v2"));
        party.resetInventoriesDelayed();
        return true;
    }

    public boolean leaveQueue(Party party, boolean silent) {
        MatchQueueEntry entry = getQueueEntry(party);

        if (entry == null) {
            return false;
        }

        MatchQueue queue = entry.getQueue();
        queue.removeFromQueue(entry);
        partyQueueCache.remove(party);

        if (!silent) {
            party.message(String.format(LEAVE_PARTY_MESSAGE, ChatColor.GREEN + queue.getKitType().getDisplayName(), queue.isRanked() ? ChatColor.YELLOW + "ranked" : "un-ranked"));
        }

        party.resetInventoriesDelayed();
        return true;
    }

    public boolean isQueued(UUID player) {
        return soloQueueCache.containsKey(player);
    }

    public boolean isQueuedRanked(UUID player) {
        SoloMatchQueueEntry entry = getQueueEntry(player);
        return entry != null && entry.getQueue().isRanked();
    }

    public boolean isQueuedUnranked(UUID player) {
        SoloMatchQueueEntry entry = getQueueEntry(player);
        return entry != null && !entry.getQueue().isRanked();
    }

    public SoloMatchQueueEntry getQueueEntry(UUID player) {
        return soloQueueCache.get(player);
    }

    public boolean isQueued(Party party) {
        return partyQueueCache.containsKey(party);
    }

    public boolean isQueuedRanked(Party party) {
        PartyMatchQueueEntry entry = getQueueEntry(party);
        return entry != null && entry.getQueue().isRanked();
    }

    public boolean isQueuedUnranked(Party party) {
        PartyMatchQueueEntry entry = getQueueEntry(party);
        return entry != null && !entry.getQueue().isRanked();
    }


    public PartyMatchQueueEntry getQueueEntry(Party party) {
        return partyQueueCache.get(party);
    }

    void removeFromQueueCache(MatchQueueEntry entry) {
        if (entry instanceof SoloMatchQueueEntry) {
            soloQueueCache.remove(((SoloMatchQueueEntry) entry).getPlayer());
        } else if (entry instanceof PartyMatchQueueEntry) {
            partyQueueCache.remove(((PartyMatchQueueEntry) entry).getParty());
        }
    }

}
