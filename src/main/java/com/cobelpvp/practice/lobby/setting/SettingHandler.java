package com.cobelpvp.practice.lobby.setting;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.lobby.setting.event.SettingUpdateEvent;
import com.cobelpvp.practice.lobby.setting.listener.SettingLoadListener;
import com.cobelpvp.practice.lobby.setting.repository.MongoSettingRepository;
import com.cobelpvp.practice.lobby.setting.repository.SettingRepository;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SettingHandler {

    private final Map<UUID, Map<Setting, Boolean>> settingsData = new ConcurrentHashMap<>();
    private final SettingRepository settingRepository;

    public SettingHandler() {
        Bukkit.getPluginManager().registerEvents(new SettingLoadListener(), Practice.getInstance());
        settingRepository = new MongoSettingRepository();
    }

    /**
     * Retrieves the value of a setting for the player provided, falling back to the
     * setting's default value if the player hasn't updated the setting or the player's
     * settings failed to load.
     *
     * @param player The player to look up settings for
     * @param setting The Setting to look up the value of
     * @return If the setting is, after considered defaults and player customizations, enabled.
     */
    public boolean getSetting(Player player, Setting setting) {
        Map<Setting, Boolean> playerSettings = settingsData.getOrDefault(player.getUniqueId(), ImmutableMap.of());
        return playerSettings.getOrDefault(setting, setting.getDefaultValue());
    }

    /**
     * Updates the value of a setting for the player provided. Automatically handles
     * calling {@link SettingUpdateEvent}s and saving the changes in a database.
     *
     * @param player The player to update settings for
     * @param setting The Setting to update the value of
     * @param enabled If the setting should be enabled
     */
    public void updateSetting(Player player, Setting setting, boolean enabled) {
        Map<Setting, Boolean> playerSettings = settingsData.computeIfAbsent(player.getUniqueId(), i -> new HashMap<>());
        playerSettings.put(setting, enabled);

        Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> {
            try {
                settingRepository.saveSettings(player.getUniqueId(), playerSettings);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        Bukkit.getPluginManager().callEvent(new SettingUpdateEvent(player, setting, enabled));
    }

    public void loadSettings(UUID playerUuid) {
        Map<Setting, Boolean> playerSettings;

        try {
            playerSettings = new ConcurrentHashMap<>(settingRepository.loadSettings(playerUuid));
        } catch (IOException ex) {

            ex.printStackTrace();
            playerSettings = new ConcurrentHashMap<>();
        }
        settingsData.put(playerUuid, playerSettings);
    }

    public void unloadSettings(Player player) {
        settingsData.remove(player.getUniqueId());
    }

}