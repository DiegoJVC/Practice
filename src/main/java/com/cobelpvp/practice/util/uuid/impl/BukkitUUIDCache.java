package com.cobelpvp.practice.util.uuid.impl;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.util.uuid.UUIDCache;
import java.util.UUID;

public final class BukkitUUIDCache
        implements UUIDCache {
    @Override
    public UUID uuid(String name) {
        return Practice.getInstance().getServer().getOfflinePlayer(name).getUniqueId();
    }

    @Override
    public String name(UUID uuid) {
        return Practice.getInstance().getServer().getOfflinePlayer(uuid).getName();
    }

    @Override
    public void ensure(UUID uuid) {
    }

    @Override
    public void update(UUID uuid, String name) {
    }
}

