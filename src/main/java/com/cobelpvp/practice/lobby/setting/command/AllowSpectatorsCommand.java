package com.cobelpvp.practice.lobby.setting.command;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.lobby.setting.Setting;
import com.cobelpvp.practice.lobby.setting.SettingHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AllowSpectatorsCommand {

    @Command(names = { "togglespectators", "tallowspec", "taspectators" }, permission = "")
    public static void toggleDuel(Player sender) {
        if (!Setting.ALLOW_SPECTATORS.canUpdate(sender)) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        boolean enabled = !settingHandler.getSetting(sender, Setting.ALLOW_SPECTATORS);
        settingHandler.updateSetting(sender, Setting.ALLOW_SPECTATORS, enabled);

        if (enabled) {
            sender.sendMessage(ChatColor.GREEN + "Toggled allow spectators on.");
        } else {
            sender.sendMessage(ChatColor.RED + "Toggled allow spectators off.");
        }
    }

}
