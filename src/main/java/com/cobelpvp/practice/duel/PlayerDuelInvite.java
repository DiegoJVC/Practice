package com.cobelpvp.practice.duel;

import java.util.UUID;

import com.cobelpvp.practice.match.arena.ArenaSchematic;
import org.bukkit.entity.Player;
import com.cobelpvp.practice.kittype.KitType;

public final class PlayerDuelInvite extends DuelInvite<UUID> {

    public PlayerDuelInvite(Player sender, Player target, KitType kitType, ArenaSchematic arena) {
        super(sender.getUniqueId(), target.getUniqueId(), kitType, arena);
    }

}