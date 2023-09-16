package com.cobelpvp.practice.lobby.setting.command;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.lobby.setting.Setting;
import com.cobelpvp.practice.lobby.setting.SettingHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OtherSpectatorsCommand {

    @Command(names = { "toggleotherspectator", "tos", "tospectators" }, permission = "")
    public static void toggleDuel(Player sender) {
        if (!Setting.VIEW_OTHER_SPECTATORS.canUpdate(sender)) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        boolean enabled = !settingHandler.getSetting(sender, Setting.VIEW_OTHER_SPECTATORS);
        settingHandler.updateSetting(sender, Setting.VIEW_OTHER_SPECTATORS, enabled);

        if (enabled) {
            sender.sendMessage(ChatColor.GREEN + "Toggled view other spectators on.");
        } else {
            sender.sendMessage(ChatColor.RED + "Toggled view other spectators off.");
        }
    }

}
