package com.cobelpvp.practice.match.command;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.practice.match.Match;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.match.MatchTeam;
import com.cobelpvp.practice.lobby.setting.Setting;
import com.cobelpvp.practice.lobby.setting.SettingHandler;
import com.cobelpvp.practice.util.PracticeValidation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class SpectateCommand {

    private static final int SPECTATE_COOLDOWN_SECONDS = 2;
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    @Command(names = {"spectate", "spec"}, permission = "")
    public static void spectate(Player sender, @Param(name = "target") Player target) {
        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You can't spectate yourself.");
            return;
        } else if (cooldowns.containsKey(sender.getUniqueId()) && cooldowns.get(sender.getUniqueId()) > System.currentTimeMillis()) {
            sender.sendMessage(ChatColor.RED + "Please wait before using this command again.");
            return;
        }

        cooldowns.put(sender.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(SPECTATE_COOLDOWN_SECONDS));
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        Match targetMatch = matchHandler.getMatchPlayingOrSpectating(target);

        if (targetMatch == null) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not in a match.");
            return;
        }

        boolean bypassesSpectating = false;

        if (!bypassesSpectating && (targetMatch.getTeam(target.getUniqueId()) != null && !settingHandler.getSetting(target, Setting.ALLOW_SPECTATORS))) {
            if (sender.isOp() || sender.hasPermission("practice.staff")) {
                sender.sendMessage(ChatColor.RED + "Bypassing " + target.getName() + "'s no spectators preference...");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " doesn't allow spectators at the moment.");
                return;
            }
        }

        if ((!sender.isOp() && !sender.hasPermission("practice.staff")) && targetMatch.getTeams().size() == 2 && !bypassesSpectating) {
            MatchTeam teamA = targetMatch.getTeams().get(0);
            MatchTeam teamB = targetMatch.getTeams().get(1);

            if (teamA.getAllMembers().size() == 1 && teamB.getAllMembers().size() == 1) {
                UUID teamAPlayer = teamA.getFirstMember();
                UUID teamBPlayer = teamB.getFirstMember();

                if (
                    !settingHandler.getSetting(Bukkit.getPlayer(teamAPlayer), Setting.ALLOW_SPECTATORS) ||
                    !settingHandler.getSetting(Bukkit.getPlayer(teamBPlayer), Setting.ALLOW_SPECTATORS)
                ) {
                    sender.sendMessage(ChatColor.RED + "Not all players in that 1v1 have spectators enabled.");
                    return;
                }
            }
        }

        Player teleportTo = null;

        if (!targetMatch.isSpectator(target.getUniqueId())) {
            teleportTo = target;
        }

        if (PracticeValidation.canUseSpectateItemIgnoreMatchSpectating(sender)) {
            Match currentlySpectating = matchHandler.getMatchSpectating(sender);

            if (currentlySpectating != null) {
                if (currentlySpectating.equals(targetMatch)) {
                    sender.sendMessage(ChatColor.RED + "You're already spectating this match.");
                    return;
                }
                currentlySpectating.removeSpectator(sender);
            }
            targetMatch.addSpectator(sender, teleportTo);
        }
    }

}