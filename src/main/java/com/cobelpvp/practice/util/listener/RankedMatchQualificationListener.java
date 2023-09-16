package com.cobelpvp.practice.util.listener;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.match.MatchTeam;
import com.cobelpvp.practice.match.event.MatchEndEvent;
import com.cobelpvp.practice.util.MongoUtils;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import redis.clients.jedis.Jedis;

public final class RankedMatchQualificationListener implements Listener {

    public static final String KEY_PREFIX = "practice:rankedMatchQualification:";
    public static final int MIN_MATCH_WINS = 0;
    private static final Map<UUID, Integer> rankedMatchQualificationWins = new ConcurrentHashMap<>();

    public static int getWinsNeededToQualify(UUID playerUuid) {
        return Math.max(0, MIN_MATCH_WINS - rankedMatchQualificationWins.getOrDefault(playerUuid, 0));
    }
    public static boolean isQualified(UUID playerUuid) {
        return rankedMatchQualificationWins.getOrDefault(playerUuid, 0) >= MIN_MATCH_WINS;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        try (Jedis jedis = Atheneum.getInstance().getLocalJedisPool().getResource()) {
            String existing = jedis.get(KEY_PREFIX + event.getUniqueId());

            if (existing != null && !existing.isEmpty()) {
                rankedMatchQualificationWins.put(event.getUniqueId(), Integer.parseInt(existing));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        rankedMatchQualificationWins.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();

        if (match.getWinner() == null || match.isRanked()) {
            return;
        }

        List<MatchTeam> teams = match.getTeams();

        if (teams.size() != 2 || teams.get(0).getAllMembers().size() != 1 || teams.get(1).getAllMembers().size() != 1) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> {
            try (Jedis jedis = Atheneum.getInstance().getLocalJedisPool().getResource()) {
                UUID winner = match.getWinner().getFirstAliveMember();
                rankedMatchQualificationWins.put(winner, jedis.incr(KEY_PREFIX + winner).intValue());
            }
        });
    }

    @Command(names = {"rmqRead"}, permission = "op")
    public static void rmqRead(Player sender, @Param(name="target",defaultValue="self") Player target) {
        sender.sendMessage(ChatColor.DARK_PURPLE + "Wins: " + ChatColor.GRAY.toString() + rankedMatchQualificationWins.getOrDefault(target.getUniqueId(), 0));
        sender.sendMessage(ChatColor.DARK_PURPLE + "Qualified: " + ChatColor.GRAY.toString() + isQualified(target.getUniqueId()));
    }

    @Command(names = {"rmqSet"}, permission = "op")
    public static void rmqSet(Player sender, @Param(name="target") Player target, @Param(name="count") int count) {
        rankedMatchQualificationWins.put(target.getUniqueId(), count);

        try (Jedis jedis = Atheneum.getInstance().getLocalJedisPool().getResource()) {
            jedis.set(KEY_PREFIX + target.getUniqueId(), String.valueOf(count));
        }

        sender.sendMessage(ChatColor.GOLD + "Updated!");
    }

    @Command(names = {"rmqImport"}, permission = "op", async = true)
    public static void rmqImport(Player sender) {
        MongoCollection<Document> matchCollection = MongoUtils.getCollection(MatchHandler.MONGO_COLLECTION_NAME);

        sender.sendMessage(ChatColor.GOLD + "Starting...");

        try (Jedis jedis = Atheneum.getInstance().getLocalJedisPool().getResource()) {
            matchCollection.find(new Document("ranked", false).append("winner", new Document("$gte", 0))).forEach((Block<Document>) match -> {
                List<Document> teams = (List<Document>) match.get("teams", List.class);

                if (teams.size() != 2) {
                    return;
                }
                for (Document team : teams) {
                    int size = team.get("allMembers", List.class).size();

                    if (size != 1) {
                        return;
                    }
                }

                Document winnerTeam = teams.get(match.getInteger("winner"));
                List<String> winnerPlayers = (List<String>) winnerTeam.get("allMembers", List.class);

                for (String winnerPlayer : winnerPlayers) {
                    jedis.incr(KEY_PREFIX + winnerPlayer);
                }

                sender.sendMessage(ChatColor.GOLD + "Imported match " + ChatColor.WHITE + match.getObjectId("_id") + ChatColor.GOLD + ".");
            });
        }
        sender.sendMessage(ChatColor.GREEN + "Done!");
    }

}