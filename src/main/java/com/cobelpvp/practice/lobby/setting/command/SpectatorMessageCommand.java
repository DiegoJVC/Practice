package com.cobelpvp.practice.lobby.setting.command;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.practice.lobby.setting.Setting;
import com.cobelpvp.practice.lobby.setting.SettingHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpectatorMessageCommand {

    @Command(names = { "togglespectatormessage", "tsm", "tsmessage" }, permission = "")
    public static void toggleDuel(Player sender) {
        if (!Setting.SHOW_SPECTATOR_JOIN_MESSAGES.canUpdate(sender)) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        boolean enabled = !settingHandler.getSetting(sender, Setting.SHOW_SPECTATOR_JOIN_MESSAGES);
        settingHandler.updateSetting(sender, Setting.SHOW_SPECTATOR_JOIN_MESSAGES, enabled);

        if (enabled) {
            sender.sendMessage(ChatColor.GREEN + "Toggled spectator join message on.");
        } else {
            sender.sendMessage(ChatColor.RED + "Toggled spectator join message off.");
        }
    }

}