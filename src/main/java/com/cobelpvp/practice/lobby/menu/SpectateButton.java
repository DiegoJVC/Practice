package com.cobelpvp.practice.lobby.menu;

import com.cobelpvp.practice.util.uuid.UniqueIDCache;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchTeam;
import com.google.common.base.Preconditions;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.util.PracticeValidation;
import library.cobelpvp.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class SpectateButton extends Button {

    private final Match match;

    SpectateButton(Match match) {
        this.match = Preconditions.checkNotNull(match, "match");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW.toString() + match.getSimpleDescription(false);
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();
        MatchTeam teamA = match.getTeams().get(0);
        MatchTeam teamB = match.getTeams().get(1);

        if (match.isRanked()) {
            description.add(ChatColor.GREEN + "Ranked");
        } else {
            description.add(ChatColor.BLUE + "Unranked");
        }

        description.add("");
        description.add(ChatColor.YELLOW + "Kit: " + ChatColor.WHITE + match.getKitType().getDisplayName());
        description.add(ChatColor.YELLOW + "Arena: " + ChatColor.WHITE + match.getArena().getSchematic());

        List<UUID> spectators = new ArrayList<>(match.getSpectators());

        spectators.removeIf(uuid -> Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).hasMetadata("ModMode") || match.getPreviousTeam(uuid) != null);

        description.add(ChatColor.GRAY + "Spectators: " + ChatColor.WHITE + spectators.size());

        if (teamA.getAliveMembers().size() != 1 || teamB.getAliveMembers().size() != 1) {
            description.add("");

            for (UUID member : teamA.getAliveMembers()) {
                description.add(ChatColor.YELLOW + UniqueIDCache.name(member));
            }

            description.add(ChatColor.GRAY + "   vs.");

            for (UUID member : teamB.getAliveMembers()) {
                description.add(ChatColor.YELLOW + UniqueIDCache.name(member));
            }
        }

        description.add("");
        description.add(ChatColor.GREEN + "Click to spectate");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.PAPER;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        if (!PracticeValidation.canUseSpectateItemIgnoreMatchSpectating(player)) {
            return;
        }

        Match currentlySpectating = Practice.getInstance().getMatchHandler().getMatchSpectating(player);

        if (currentlySpectating != null) {
            currentlySpectating.removeSpectator(player, false);
        }

        match.addSpectator(player, null);
    }

}