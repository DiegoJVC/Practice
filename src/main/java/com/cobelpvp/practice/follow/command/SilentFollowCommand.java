package com.cobelpvp.practice.follow.command;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.command.LeaveCommand;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class SilentFollowCommand {

    @Command(names = "silentfollow", permission = "practice.silent")
    public static void silentfollow(Player sender, @Param(name = "target") Player target) {
        sender.setMetadata("ModMode", new FixedMetadataValue(Practice.getInstance(), true));
        sender.setMetadata("invisible", new FixedMetadataValue(Practice.getInstance(), true));

        if (Practice.getInstance().getPartyHandler().hasParty(sender)) {
            LeaveCommand.leave(sender);
        }

        FollowCommand.follow(sender, target);
    }

}
