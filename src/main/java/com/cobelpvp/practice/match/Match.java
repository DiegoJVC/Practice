package com.cobelpvp.practice.match;

import com.cobelpvp.practice.util.uuid.UniqueIDCache;
import com.cobelpvp.practice.match.event.*;
import com.cobelpvp.practice.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import lombok.Getter;
import lombok.Setter;
import com.cobelpvp.atheneum.nametag.TeamsNametagHandler;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.arena.Arena;
import com.cobelpvp.practice.match.elo.EloCalculator;
import com.cobelpvp.practice.kittype.KitType;
import com.cobelpvp.practice.lobby.LobbyHandler;
import com.cobelpvp.practice.match.postmatchinv.PostMatchPlayer;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import java.util.*;
import java.util.stream.Collectors;

public final class Match {

    private static final int MATCH_END_DELAY_SECONDS = 3;

    @Getter
    private final String _id = UUID.randomUUID().toString().substring(0, 7);

    @Getter
    private final KitType kitType;
    @Getter
    private final Arena arena;
    @Getter
    private final List<MatchTeam> teams;
    private final Map<UUID, PostMatchPlayer> postMatchPlayers = new HashMap<>();
    private final Set<UUID> spectators = new HashSet<>();

    @Getter
    private MatchTeam winner;
    @Getter
    private MatchEndReason endReason;
    @Getter
    private MatchState state;
    @Getter
    private Date startedAt;
    @Getter
    private Date endedAt;
    @Getter
    private boolean ranked;

    @Getter
    private boolean allowRematches;
    @Getter
    @Setter
    private EloCalculator.Result eloChange;

    private final Set<BlockVector> placedBlocks = new HashSet<>();

    private final transient Set<UUID> spectatorMessagesUsed = new HashSet<>();

    @Getter
    private Map<UUID, UUID> lastHit = Maps.newHashMap();
    @Getter
    @Setter
    private Location lastHitLocation;
    @Getter
    private Map<UUID, Integer> combos = Maps.newHashMap();
    @Getter
    private Map<UUID, Integer> totalHits = Maps.newHashMap();
    @Getter
    private Map<UUID, Integer> longestCombo = Maps.newHashMap();
    @Getter
    private Map<UUID, Integer> missedPots = Maps.newHashMap();
    @Getter
    private Map<UUID, Integer> thrownPots = Maps.newHashMap();

    @Getter
    private final transient Set<Integer> runnables = new HashSet<>();

    @Getter
    private Set<UUID> allPlayers = Sets.newHashSet();

    @Getter
    private Set<UUID> winningPlayers;
    @Getter
    private Set<UUID> losingPlayers;

    public Match(KitType kitType, Arena arena, List<MatchTeam> teams, boolean ranked, boolean allowRematches) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.arena = Preconditions.checkNotNull(arena, "arena");
        this.teams = ImmutableList.copyOf(teams);
        this.ranked = ranked;
        this.allowRematches = allowRematches;

        saveState();
    }

    private void saveState() {
        if (kitType.isBuildingAllowed())
            this.arena.takeSnapshot();
    }

    void startCountdown() {
        state = MatchState.COUNTDOWN;

        Map<UUID, Match> playingCache = Practice.getInstance().getMatchHandler().getPlayingMatchCache();
        Set<Player> updateVisiblity = new HashSet<>();

        for (MatchTeam team : this.getTeams()) {
            for (UUID playerUuid : team.getAllMembers()) {

                if (!team.isAlive(playerUuid))
                    continue;

                Player player = Bukkit.getPlayer(playerUuid);

                playingCache.put(player.getUniqueId(), this);

                Location spawn = (team == teams.get(0) ? arena.getTeam1Spawn() : arena.getTeam2Spawn()).clone();
                Vector oldDirection = spawn.getDirection();

                Block block = spawn.getBlock();
                while (block.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                    block = block.getRelative(BlockFace.DOWN);
                    if (block.getY() <= 0) {
                        block = spawn.getBlock();
                        break;
                    }
                }

                spawn = block.getLocation();
                spawn.setDirection(oldDirection);
                spawn.add(0.5, 0, 0.5);

                player.teleport(spawn);
                player.getInventory().setHeldItemSlot(0);

                TeamsNametagHandler.reloadPlayer(player);
                TeamsNametagHandler.reloadOthersFor(player);

                updateVisiblity.add(player);
                PatchedPlayerUtils.resetInventory(player, GameMode.SURVIVAL);
            }
        }

        updateVisiblity.forEach(VisibilityUtils::updateVisibilityFlicker);

        Bukkit.getPluginManager().callEvent(new MatchCountdownStartEvent(this));

        //messageAll(ChatColor.GREEN + "Starting in");//
        //messageAll("");//
        new BukkitRunnable() {

            int countdownTimeRemaining = kitType.getId().equals("SUMO") ? 5 : 5;

            public void run() {
                if (state != MatchState.COUNTDOWN) {
                    cancel();
                    return;
                }

                if (countdownTimeRemaining == 0) {
                    playSoundAll(Sound.NOTE_PLING, 2F);
                    startMatch();
                    return;
                } else if (countdownTimeRemaining <= 3) {
                    playSoundAll(Sound.NOTE_PLING, 1F);
                }

                messageAll(ChatColor.GREEN + "Starting in " +ChatColor.YELLOW.toString() + countdownTimeRemaining + ChatColor.GREEN +" seconds!");
                countdownTimeRemaining--;
            }

        }.runTaskTimer(Practice.getInstance(), 0L, 20L);
    }

    private void startMatch() {
        state = MatchState.IN_PROGRESS;
        startedAt = new Date();

        messageAll(ChatColor.GREEN + "Duel starting now!");
        messageAll("");
        messageAll("");
        messageAll(ChatColor.DARK_RED + "REMINDER: " + ChatColor.RED + "Butterfly clicking is not allowed and could result in a ban and elo reset. " + ChatColor.DARK_RED + ChatColor.UNDERLINE + "Use at your own risk.");
        Bukkit.getPluginManager().callEvent(new MatchStartEvent(this));
    }

    public void endMatch(MatchEndReason reason) {
        if (state == MatchState.ENDING || state == MatchState.TERMINATED) {
            return;
        }

        state = MatchState.ENDING;
        endedAt = new Date();
        endReason = reason;
        this.getRunnables().forEach(id -> Practice.getInstance().getServer().getScheduler().cancelTask(id));
        try {
            for (MatchTeam matchTeam : this.getTeams()) {
                for (UUID playerUuid : matchTeam.getAllMembers()) {
                    allPlayers.add(playerUuid);
                    if (!matchTeam.isAlive(playerUuid))
                        continue;
                    Player player = Bukkit.getPlayer(playerUuid);

                    postMatchPlayers.computeIfAbsent(playerUuid, v -> new PostMatchPlayer(player, kitType, kitType.getHealingMethod(), totalHits.getOrDefault(player.getUniqueId(), 0), longestCombo.getOrDefault(player.getUniqueId(), 0), missedPots.getOrDefault(player.getUniqueId(), 0), thrownPots.getOrDefault(player.getUniqueId(), 0)));
                }
            }
            messageSpectators(ChatColor.DARK_GREEN + "Thanks for spectating the match <3");
            messageAlive(ChatColor.DARK_GREEN + "Thanks for playing on cobelpvp.com!");
            messageAlive(ChatColor.GOLD + "You are the Winner!." );
            Bukkit.getPluginManager().callEvent(new MatchEndEvent(this));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        int delayTicks = MATCH_END_DELAY_SECONDS * 25;
        if (JavaPlugin.getProvidingPlugin(this.getClass()).isEnabled()) {
            Bukkit.getScheduler().runTaskLater(Practice.getInstance(), this::terminateMatch, delayTicks);
        } else {
            this.terminateMatch();
        }
    }

    private void terminateMatch() {
        if (state == MatchState.TERMINATED) {
            return;
        }

        state = MatchState.TERMINATED;

        if (startedAt == null) {
            startedAt = new Date();
        }

        if (endedAt == null) {
            endedAt = new Date();
        }

        if (endReason != MatchEndReason.DURATION_LIMIT_EXCEEDED && endReason != MatchEndReason.FORCEFULLY_TERMINATED) {
            this.winningPlayers = winner.getAllMembers();
            this.losingPlayers = teams.stream().filter(team -> team != winner).flatMap(team -> team.getAllMembers().stream()).collect(Collectors.toSet());
        } else {
            this.ranked = false;
            this.winningPlayers = this.allPlayers;
            this.losingPlayers = this.allPlayers;
        }

        Bukkit.getPluginManager().callEvent(new MatchTerminateEvent(this));

        if (this.isRanked()) {
            JsonObject document = Practice.getGson().toJsonTree(this).getAsJsonObject();

            document.addProperty("winner", teams.indexOf(winner));
            document.addProperty("arena", arena.getSchematic());
            Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> {
                Document parsedDocument = Document.parse(document.toString());
                parsedDocument.put("startedAt", startedAt);
                parsedDocument.put("endedAt", endedAt);
                MongoUtils.getCollection(MatchHandler.MONGO_COLLECTION_NAME).insertOne(parsedDocument);
            });
        }


        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        LobbyHandler lobbyHandler = Practice.getInstance().getLobbyHandler();

        Map<UUID, Match> playingCache = matchHandler.getPlayingMatchCache();
        Map<UUID, Match> spectateCache = matchHandler.getSpectatingMatchCache();

        if (kitType.isBuildingAllowed())
            arena.restore();
        Practice.getInstance().getArenaHandler().releaseArena(arena);
        matchHandler.removeMatch(this);


        getTeams().forEach(team -> {
            team.getAllMembers().forEach(player -> {
                if (team.isAlive(player)) {
                    playingCache.remove(player);
                    spectateCache.remove(player);
                    lobbyHandler.returnToLobby(Bukkit.getPlayer(player));
                }
            });
        });

        spectators.forEach(player -> {
            if (Bukkit.getPlayer(player) != null) {
                playingCache.remove(player);
                spectateCache.remove(player);
                lobbyHandler.returnToLobby(Bukkit.getPlayer(player));
            }
        });
    }

    public Set<UUID> getSpectators() {
        return ImmutableSet.copyOf(spectators);
    }

    public Map<UUID, PostMatchPlayer> getPostMatchPlayers() {
        return ImmutableMap.copyOf(postMatchPlayers);
    }

    private void checkEnded() {
        if (state == MatchState.ENDING || state == MatchState.TERMINATED) {
            return;
        }

        List<MatchTeam> teamsAlive = new ArrayList<>();

        for (MatchTeam team : teams) {
            if (!team.getAliveMembers().isEmpty()) {
                teamsAlive.add(team);
            }
        }

        if (teamsAlive.size() == 1) {
            this.winner = teamsAlive.get(0);
            endMatch(MatchEndReason.ENEMIES_ELIMINATED);
        }
    }

    public boolean isSpectator(UUID uuid) {
        return spectators.contains(uuid);
    }

    public void addSpectator(Player player, Player target) {
        addSpectator(player, target, false);
    }

    public void addSpectator(Player player, Player target, boolean fromMatch) {
        if (!fromMatch && state == MatchState.ENDING) {
            player.sendMessage(ChatColor.DARK_RED + "This match is no longer available for spectating.");
            return;
        }

        Map<UUID, Match> spectateCache = Practice.getInstance().getMatchHandler().getSpectatingMatchCache();

        spectateCache.put(player.getUniqueId(), this);
        spectators.add(player.getUniqueId());

        if (!fromMatch) {
            Location tpTo = arena.getSpectatorSpawn();

            if (target != null) {
                tpTo = target.getLocation().clone().add(0, 1.5, 0);
            }

            player.teleport(tpTo);
            player.sendMessage(ChatColor.YELLOW + "Now spectating " + ChatColor.YELLOW + getSimpleDescription(true));

            sendSpectatorMessage(player, ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " is now spectating.");
        } else {
            player.getInventory().setHeldItemSlot(0);
        }

        TeamsNametagHandler.reloadPlayer(player);
        TeamsNametagHandler.reloadOthersFor(player);

        VisibilityUtils.updateVisibility(player);
        PatchedPlayerUtils.resetInventory(player, GameMode.SURVIVAL, true);
        InventoryUtils.resetInventoryDelayed(player);
        player.setAllowFlight(true);
        player.setFlying(true);
        ItemListener.addButtonCooldown(player, 1_500);

        Bukkit.getPluginManager().callEvent(new MatchSpectatorJoinEvent(player, this));
    }

    public void removeSpectator(Player player) {
        removeSpectator(player, true);
    }

    public void removeSpectator(Player player, boolean returnToLobby) {
        Map<UUID, Match> spectateCache = Practice.getInstance().getMatchHandler().getSpectatingMatchCache();

        spectateCache.remove(player.getUniqueId());
        spectators.remove(player.getUniqueId());
        ItemListener.addButtonCooldown(player, 1_500);

        sendSpectatorMessage(player, ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " is no longer spectating.");

        if (returnToLobby) {
            Practice.getInstance().getLobbyHandler().returnToLobby(player);
        }

        Bukkit.getPluginManager().callEvent(new MatchSpectatorLeaveEvent(player, this));
    }

    private void sendSpectatorMessage(Player spectator, String message) {
        // see comment on spectatorMessagesUsed field for more
        if (spectatorMessagesUsed.add(spectator.getUniqueId())) {
            return;
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online == spectator) {
                continue;
            }

            boolean sameMatch = isSpectator(online.getUniqueId()) || getTeam(online.getUniqueId()) != null;

            if (sameMatch) {
                if (!spectator.hasPermission("practice.staff") || online.hasPermission("practice.staff")) {
                    online.sendMessage(message);
                }
            }
        }
    }

    public void markDead(Player player) {
        MatchTeam team = getTeam(player.getUniqueId());

        if (team == null) {
            return;
        }

        Map<UUID, Match> playingCache = Practice.getInstance().getMatchHandler().getPlayingMatchCache();

        team.markDead(player.getUniqueId());
        playingCache.remove(player.getUniqueId());

        postMatchPlayers.put(player.getUniqueId(), new PostMatchPlayer(player, kitType, kitType.getHealingMethod(), totalHits.getOrDefault(player.getUniqueId(), 0), longestCombo.getOrDefault(player.getUniqueId(), 0), missedPots.getOrDefault(player.getUniqueId(), 0), thrownPots.getOrDefault(player.getUniqueId(), 0)));
        checkEnded();
    }

    public MatchTeam getTeam(UUID playerUuid) {
        for (MatchTeam team : teams) {
            if (team.isAlive(playerUuid)) {
                return team;
            }
        }

        return null;
    }

    public MatchTeam getPreviousTeam(UUID playerUuid) {
        for (MatchTeam team : teams) {
            if (team.getAllMembers().contains(playerUuid)) {
                return team;
            }
        }

        return null;
    }

    /**
     * Creates a simple, one line description of this match This will include two
     * players (if a 1v1) or player counts and the kit type
     *
     * @return A simple description of this match
     */
    public String getSimpleDescription(boolean includeRankedUnranked) {
        String players;

        if (teams.size() == 2) {
            MatchTeam teamA = teams.get(0);
            MatchTeam teamB = teams.get(1);

            if (teamA.getAliveMembers().size() == 1 && teamB.getAliveMembers().size() == 1) {
                String nameA = UniqueIDCache.name(teamA.getFirstAliveMember());
                String nameB = UniqueIDCache.name(teamB.getFirstAliveMember());

                players = nameA + " vs " + nameB;
            } else {
                players = teamA.getAliveMembers().size() + " vs " + teamB.getAliveMembers().size();
            }
        } else {
            int numTotalPlayers = 0;

            for (MatchTeam team : teams) {
                numTotalPlayers += team.getAliveMembers().size();
            }

            players = numTotalPlayers + " player fight";
        }

        if (includeRankedUnranked) {
            String rankedStr = ranked ? "Ranked" : "Unranked";
            return players + " (" + rankedStr + " " + kitType.getDisplayName() + ")";
        } else {
            return players;
        }
    }

    /**
     * Sends a basic chat message to all alive participants and spectators
     *
     * @param message the message to send
     */
    public void messageAll(String message) {
        messageAlive(message);
        messageSpectators(message);
    }

    /**
     * Plays a sound for all alive participants and spectators
     *
     * @param sound the Sound to play
     * @param pitch the pitch to play the provided sound at
     */
    public void playSoundAll(Sound sound, float pitch) {
        playSoundAlive(sound, pitch);
        playSoundSpectators(sound, pitch);
    }

    /**
     * Sends a basic chat message to all spectators
     *
     * @param message the message to send
     */
    public void messageSpectators(String message) {
        for (UUID spectator : spectators) {
            Player spectatorBukkit = Bukkit.getPlayer(spectator);

            if (spectatorBukkit != null) {
                spectatorBukkit.sendMessage(message);
            }
        }
    }

    /**
     * Plays a sound for all spectators
     *
     * @param sound the Sound to play
     * @param pitch the pitch to play the provided sound at
     */
    public void playSoundSpectators(Sound sound, float pitch) {
        for (UUID spectator : spectators) {
            Player spectatorBukkit = Bukkit.getPlayer(spectator);

            if (spectatorBukkit != null) {
                spectatorBukkit.playSound(spectatorBukkit.getEyeLocation(), sound, 10F, pitch);
            }
        }
    }

    /**
     * Sends a basic chat message to all alive participants
     *
     * @param message the message to send
     * @see MatchTeam#messageAlive(String)
     */
    public void messageAlive(String message) {
        for (MatchTeam team : teams) {
            team.messageAlive(message);
        }
    }

    /**
     * Plays a sound for all alive participants
     *
     * @param sound the Sound to play
     * @param pitch the pitch to play the provided sound at
     */
    public void playSoundAlive(Sound sound, float pitch) {
        for (MatchTeam team : teams) {
            team.playSoundAlive(sound, pitch);
        }
    }

    /**
     * Records a placed block during this match. Used to keep track of which blocks
     * can be broken.
     */
    public void recordPlacedBlock(Block block) {
        placedBlocks.add(block.getLocation().toVector().toBlockVector());
    }

    /**
     * Checks if a block can be broken in this match. Only used if the KitType
     * allows building.
     */
    public boolean canBeBroken(Block block) {
        return (kitType.getId().equals("SPLEEF") && (block.getType() == Material.SNOW_BLOCK || block.getType() == Material.GRASS || block.getType() == Material.DIRT)) || placedBlocks.contains(block.getLocation().toVector().toBlockVector());
    }

    public void addRunnable(int id) {
        this.runnables.add(id);
    }

}