package com.cobelpvp.practice.lobby.setting.command;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.practice.lobby.setting.Setting;
import com.cobelpvp.practice.lobby.setting.SettingHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ToggleScoreboardCommand {

    @Command(names = { "togglescoreboard", "tsb", "tboard" }, permission = "")
    public static void toggleDuel(Player sender) {
        if (!Setting.SHOW_SCOREBOARD.canUpdate(sender)) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        boolean enabled = !settingHandler.getSetting(sender, Setting.SHOW_SCOREBOARD);
        settingHandler.updateSetting(sender, Setting.SHOW_SCOREBOARD, enabled);

        if (enabled) {
            sender.sendMessage(ChatColor.GREEN + "Toggled scoreboard on.");
        } else {
            sender.sendMessage(ChatColor.RED + "Toggled scoreboard off.");
        }
    }

}
