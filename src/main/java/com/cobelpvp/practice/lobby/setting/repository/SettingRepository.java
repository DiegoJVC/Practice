package com.cobelpvp.practice.lobby.setting.repository;

import com.cobelpvp.practice.lobby.setting.Setting;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public interface SettingRepository {

    Map<Setting, Boolean> loadSettings(UUID playerUuid) throws IOException;
    void saveSettings(UUID playerUuid, Map<Setting, Boolean> settings) throws IOException;

}