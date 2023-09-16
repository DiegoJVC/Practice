package com.cobelpvp.practice.lobby.setting.event;

import com.google.common.base.Preconditions;
import com.cobelpvp.practice.lobby.setting.Setting;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import lombok.Getter;

public final class SettingUpdateEvent extends PlayerEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    @Getter private final Setting setting;

    @Getter private final boolean enabled;

    public SettingUpdateEvent(Player player, Setting setting, boolean enabled) {
        super(player);
        this.setting = Preconditions.checkNotNull(setting, "setting");
        this.enabled = enabled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}