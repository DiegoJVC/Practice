package com.cobelpvp.practice.lobby.listener;

import com.cobelpvp.practice.follow.command.UnfollowCommand;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.match.MatchState;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.lobby.LobbyHandler;
import com.cobelpvp.practice.lobby.LobbyItems;
import com.cobelpvp.practice.lobby.menu.SpectateMenu;
import com.cobelpvp.practice.lobby.menu.StatisticsMenu;
import com.cobelpvp.practice.party.command.PartyCreateCommand;
import com.cobelpvp.practice.util.ItemListener;
import com.cobelpvp.practice.util.PracticeValidation;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class LobbyItemListener extends ItemListener {

    private final Map<UUID, Long> canUseRandomSpecItem = new HashMap<>();

    public LobbyItemListener(LobbyHandler lobbyHandler) {

        addHandler(LobbyItems.DISABLE_SPEC_MODE_ITEM, player -> {
            if (lobbyHandler.isInLobby(player)) {
                lobbyHandler.setSpectatorMode(player, false);
            }
        });

        addHandler(LobbyItems.ENABLE_SPEC_MODE_ITEM, player -> {
            if (lobbyHandler.isInLobby(player) && PracticeValidation.canUseSpectateItem(player)) {
                lobbyHandler.setSpectatorMode(player, true);
            }
        });

        addHandler(LobbyItems.SPECTATE_MENU_ITEM, player -> {
            if (PracticeValidation.canUseSpectateItemIgnoreMatchSpectating(player)) {
                new SpectateMenu().openMenu(player);
            }
        });

        addHandler(LobbyItems.SPECTATE_RANDOM_ITEM, player -> {
            MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

            if (!PracticeValidation.canUseSpectateItemIgnoreMatchSpectating(player)) {
                return;
            }

            if (canUseRandomSpecItem.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) {
                player.sendMessage(ChatColor.RED + "Please wait before doing this again!");
                return;
            }

            List<Match> matches = new ArrayList<>(matchHandler.getHostedMatches());
            matches.removeIf(m -> m.isSpectator(player.getUniqueId()) || m.getState() == MatchState.ENDING);

            if (matches.isEmpty()) {
                player.sendMessage(ChatColor.RED + "There are no matches available to spectate.");
            } else {
                Match currentlySpectating = matchHandler.getMatchSpectating(player);
                Match newSpectating = matches.get(ThreadLocalRandom.current().nextInt(matches.size()));

                if (currentlySpectating != null) {
                    currentlySpectating.removeSpectator(player, false);
                }
                newSpectating.addSpectator(player, null);
                canUseRandomSpecItem.put(player.getUniqueId(), System.currentTimeMillis() + 3_000L);
            }
        });

        addHandler(LobbyItems.PLAYER_STATISTICS, player -> {
            new StatisticsMenu().openMenu(player);

        });

        addHandler(LobbyItems.PARTY_CREATE, PartyCreateCommand::partyCreate);

        addHandler(LobbyItems.UNFOLLOW_ITEM, UnfollowCommand::unfollow);
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        canUseRandomSpecItem.remove(event.getPlayer().getUniqueId());
    }

}
