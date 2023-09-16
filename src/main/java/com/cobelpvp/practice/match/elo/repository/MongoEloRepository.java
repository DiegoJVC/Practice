package com.cobelpvp.practice.match.elo.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.cobelpvp.practice.util.uuid.UniqueIDCache;
import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.practice.util.MongoUtils;
import com.cobelpvp.practice.util.PatchedPlayerUtils;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.elo.EloHandler;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import com.cobelpvp.atheneum.command.Command;
import net.minecraft.util.com.google.common.collect.ImmutableSet;

public final class MongoEloRepository implements EloRepository {

    private static final String MONGO_COLLECTION_NAME = "elo";

    private static Map<String, Map<String, Integer>> cachedFormattedElo = Maps.newHashMap();
    private static MongoEloRepository instance;

    public MongoEloRepository() {
        instance = this;
        MongoUtils.getCollection(MONGO_COLLECTION_NAME).createIndex(new Document("players", 1));

        Bukkit.getScheduler().runTaskTimerAsynchronously(Practice.getInstance(), () -> {
            refreshFormattedElo();
        }, 5 * 30, 5 * 30);
    }

    @Override
    public Map<KitType, Integer> loadElo(Set<UUID> playerUuids) throws IOException {
        MongoCollection<Document> partyEloCollection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
        Set<String> playerUuidStrings = playerUuids.stream().map(u -> u.toString()).collect(Collectors.toSet());

        try {
            Document eloDocument = partyEloCollection.find(new Document("players", playerUuidStrings)).first();

            if (eloDocument == null) {
                return ImmutableMap.of();
            }

            Map<KitType, Integer> parsedElo = new HashMap<>();
            final Document finalEloDocument = eloDocument;

            KitType.getAllTypes().forEach((kitType) -> {
                Integer elo = finalEloDocument.getInteger(kitType.getId());
                if (elo != null) {
                    parsedElo.put(kitType, elo);
                } else {
                    parsedElo.put(kitType, EloHandler.DEFAULT_ELO);
                }
            });

            return ImmutableMap.copyOf(parsedElo);
        } catch (MongoException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void saveElo(Set<UUID> playerUuids, Map<KitType, Integer> elo) throws IOException {
        Document document = new Document();
        elo.forEach((kit, value) -> document.put(kit.getId(), value));
        int[] wrapper = new int[2];

        KitType.getAllTypes().forEach(kitType -> {
            document.putIfAbsent(kitType.getId(), EloHandler.DEFAULT_ELO);
        });

        KitType.getAllTypes().stream().filter(kitType -> kitType.isSupportsRanked()).forEach(kitType -> {
            wrapper[0] = wrapper[0] + 1;
            wrapper[1] = wrapper[1] + elo.getOrDefault(kitType, EloHandler.DEFAULT_ELO);
        });

        document.put("GLOBAL", wrapper[1] / wrapper[0]);
        if (playerUuids.size() == 1) {
            document.put("lastUsername", UniqueIDCache.name(playerUuids.iterator().next()));
        }

        try {
            MongoUtils.getCollection(MONGO_COLLECTION_NAME).updateOne(new Document("players", playerUuids.stream().map(u -> u.toString()).collect(Collectors.toSet())), new Document("$set", document), MongoUtils.UPSERT_OPTIONS // creates document if it doesn't exist
            );
        } catch (MongoException ex) {
            throw new IOException(ex);
        }
    }

    @Command(names = {"recalcGlobalElo"}, permission = "op")
    public static void recalcGlobalElo(Player sender) {
        List<Document> documents = MongoUtils.getCollection(MONGO_COLLECTION_NAME).find().into(new ArrayList<Document>());
        sender.sendMessage(ChatColor.GREEN + "Recalculating " + documents.size() + " players global elo...");
        final int[] wrapper = new int[2];
        documents.forEach(document -> {
            try {
                UUID uuid = UUID.fromString((String) document.get("players", ArrayList.class).get(0));
                instance.saveElo(ImmutableSet.of(uuid), instance.loadElo(ImmutableSet.of(uuid)));
                wrapper[0]++;
                if (wrapper[0] % 100 == 0) {
                    sender.sendMessage(ChatColor.GREEN + "Finished " + wrapper[0] + " out of " + documents.size() + " players...");
                }
            } catch (Exception e) {
                e.printStackTrace();
                wrapper[1]++;
            }
        });
    }

    @Override
    public Map<String, Integer> topElo(KitType type) throws IOException {
        return cachedFormattedElo.getOrDefault(type == null ? "GLOBAL" : type.getId(), ImmutableMap.of());
    }

    private void refreshFormattedElo() {
        KitType.getAllTypes().stream().filter(type -> type.isSupportsRanked()).forEach(type -> {
            Map<String, Integer> topElo = Maps.newLinkedHashMap();
            mapTop5(type.getId(), topElo);
            cachedFormattedElo.put(type.getId(), topElo);
        });

        Map<String, Integer> topGlobal = Maps.newLinkedHashMap();
        mapTop5("GLOBAL", topGlobal);
        cachedFormattedElo.put("GLOBAL", topGlobal);
    }

    public void mapTop5(String kitTypeName, Map<String, Integer> toInsert) {
        try {
            MongoUtils.getCollection(MONGO_COLLECTION_NAME).find().sort(Sorts.descending(kitTypeName)).limit(10).forEach(new Consumer<Document>() {
                @Override
                public void accept(Document document) {
                    Object eloNumber = document.get(kitTypeName);
                    int elo = eloNumber != null && eloNumber instanceof Number ? ((Number) eloNumber).intValue() : EloHandler.DEFAULT_ELO;
                    toInsert.put(PatchedPlayerUtils.getFormattedName(UUID.fromString((String) document.get("players", ArrayList.class).get(0))), elo);
                }
            });

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}