package com.cobelpvp.practice;

import java.io.IOException;
import com.cobelpvp.atheneum.protocol.InventoryAdapter;
import com.cobelpvp.atheneum.protocol.LagCheck;
import com.cobelpvp.atheneum.protocol.PingAdapter;
import com.cobelpvp.atheneum.scoreboard.TeamsScoreboardHandler;
import com.cobelpvp.atheneum.serialization.PotionEffectAdapter;
import com.cobelpvp.practice.lobby.scoreboard.PracticeScoreboardConfiguration;
import com.cobelpvp.practice.profile.ProfileManager;
import com.cobelpvp.practice.util.listener.*;
import com.cobelpvp.practice.util.uuid.UniqueIDCache;
import com.mongodb.client.MongoCollection;
import com.cobelpvp.practice.match.arena.ArenaHandler;
import com.cobelpvp.practice.duel.DuelHandler;
import com.cobelpvp.practice.match.elo.EloHandler;
import com.cobelpvp.practice.follow.FollowHandler;
import com.cobelpvp.practice.kit.KitHandler;
import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.practice.kittype.KitTypeJsonAdapter;
import com.cobelpvp.practice.kittype.KitTypeParameterType;
import com.cobelpvp.practice.lobby.LobbyHandler;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.party.PartyHandler;
import com.cobelpvp.practice.lobby.queue.QueueHandler;
import com.comphenix.protocol.ProtocolLibrary;
import com.cobelpvp.atheneum.command.TeamsCommandHandler;
import com.cobelpvp.atheneum.nametag.TeamsNametagHandler;
import com.cobelpvp.practice.util.PracticeNametagProvider;
import library.cobelpvp.menu.ButtonListeners;
import com.cobelpvp.atheneum.serialization.*;
import com.cobelpvp.practice.kit.pvpclasses.PvPClassHandler;
import com.cobelpvp.practice.lobby.tournament.TournamentHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.TypeAdapter;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonReader;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import com.cobelpvp.chunksnapshot.ChunkSnapshot;
import com.cobelpvp.practice.match.postmatchinv.PostMatchInvHandler;
import com.cobelpvp.practice.lobby.setting.SettingHandler;
import com.cobelpvp.practice.util.StatisticsHandler;

public final class Practice extends JavaPlugin {

    private static Practice instance;

    @Getter private static Gson gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
        .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
        .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
        .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
        .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
        .registerTypeHierarchyAdapter(KitType.class, new KitTypeJsonAdapter())
        .registerTypeAdapter(ChunkSnapshot.class, new ChunkSnapshotAdapter())
        .serializeNulls()
        .create();

    public static Gson plainGson = new GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .serializeNulls()
            .create();

    private MongoClient mongoClient;
    @Getter private MongoDatabase mongoDatabase;
    @Getter private SettingHandler settingHandler;
    @Getter private DuelHandler duelHandler;
    @Getter private KitHandler kitHandler;
    @Getter private LobbyHandler lobbyHandler;
    private ArenaHandler arenaHandler;
    @Getter private MatchHandler matchHandler;
    @Getter private PartyHandler partyHandler;
    @Getter private QueueHandler queueHandler;
    @Getter private PostMatchInvHandler postMatchInvHandler;
    @Getter private FollowHandler followHandler;
    @Getter private EloHandler eloHandler;
    @Getter private PvPClassHandler pvpClassHandler;
    @Getter private TournamentHandler tournamentHandler;

    @Getter private ChatColor dominantColor = ChatColor.GOLD;
    @Getter private PracticeCache cache = new PracticeCache();
    @Getter private MongoCollection profilesCollection;
    @Getter private ProfileManager profileManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        setupMongo();

        TeamsCommandHandler.registerAll(this);
        TeamsCommandHandler.registerParameterType(KitType.class, new KitTypeParameterType());
        TeamsNametagHandler.registerProvider(new PracticeNametagProvider());
        TeamsScoreboardHandler.setConfiguration(PracticeScoreboardConfiguration.create());

        profilesCollection = mongoDatabase.getCollection("Profiles");
        profileManager = new ProfileManager();

        PingAdapter pingAdapter = new PingAdapter();
        ProtocolLibrary.getProtocolManager().addPacketListener(pingAdapter);
        ProtocolLibrary.getProtocolManager().addPacketListener(new InventoryAdapter());
        getServer().getPluginManager().registerEvents(pingAdapter, this);

        new LagCheck().runTaskTimerAsynchronously(this, 100L, 100L);

        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setTime(6_000L);
        }

        UniqueIDCache.init();
        settingHandler = new SettingHandler();
        duelHandler = new DuelHandler();
        kitHandler = new KitHandler();
        lobbyHandler = new LobbyHandler();
        arenaHandler = new ArenaHandler();
        matchHandler = new MatchHandler();
        partyHandler = new PartyHandler();
        queueHandler = new QueueHandler();
        postMatchInvHandler = new PostMatchInvHandler();
        followHandler = new FollowHandler();
        eloHandler = new EloHandler();
        pvpClassHandler = new PvPClassHandler();
        tournamentHandler = new TournamentHandler();

        getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
        getServer().getPluginManager().registerEvents(new ChatFormatListener(), this);
        getServer().getPluginManager().registerEvents(new PearlCooldownListener(), this);
        getServer().getPluginManager().registerEvents(new RankedMatchQualificationListener(), this);
        getServer().getPluginManager().registerEvents(new TabCompleteListener(), this);
        getServer().getPluginManager().registerEvents(new StatisticsHandler(), this);
        getServer().getPluginManager().registerEvents(new ButtonListeners(), this);
        getServer().getScheduler().runTaskTimerAsynchronously(this, cache, 20L, 20L);

    }

    @Override
    public void onDisable() {
        for (Match match : this.matchHandler.getHostedMatches()) {
            if (match.getKitType().isBuildingAllowed()) match.getArena().restore();
        }

        try {
            arenaHandler.saveSchematics();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String playerName : PvPClassHandler.getEquippedKits().keySet()) {
            PvPClassHandler.getEquippedKits().get(playerName).remove(getServer().getPlayerExact(playerName));
        }

        instance = null;
    }

    private void setupMongo() {
        mongoClient = new MongoClient(
            getConfig().getString("Mongo.Host"),
            getConfig().getInt("Mongo.Port")
        );

        String databaseId = getConfig().getString("Mongo.Database");
        mongoDatabase = mongoClient.getDatabase(databaseId);
    }

    private static class ChunkSnapshotAdapter extends TypeAdapter<ChunkSnapshot> {

        @Override
        public ChunkSnapshot read(JsonReader arg0) throws IOException {
            return null;
        }

        @Override
        public void write(JsonWriter arg0, ChunkSnapshot arg1) throws IOException {

        }

    }

    public ArenaHandler getArenaHandler() {
        return arenaHandler;
    }

    public static Practice getInstance() {
        return instance;
    }
}