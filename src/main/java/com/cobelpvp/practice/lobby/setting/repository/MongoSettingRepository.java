package com.cobelpvp.practice.lobby.setting.repository;

import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.util.MongoUtils;
import com.google.common.collect.ImmutableMap;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.cobelpvp.practice.lobby.setting.Setting;
import org.bson.Document;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MongoSettingRepository implements SettingRepository {

    private static final String MONGO_COLLECTION_NAME = "playerSettings";

    @Override
    public Map<Setting, Boolean> loadSettings(UUID playerUuid) throws IOException {
        MongoCollection<Document> settingsCollection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
        Document settingsDocument;

        try {
            settingsDocument = settingsCollection.find(buildQuery(playerUuid)).first();
        } catch (MongoException ex) {
            throw new IOException(ex);
        }

        if (settingsDocument == null) {
            return ImmutableMap.of();
        }

        Document rawSettings = settingsDocument.get("settings", Document.class);
        Map<Setting, Boolean> parsedSettings = new HashMap<>();

        rawSettings.forEach((rawSetting, value) -> {
            try {
                parsedSettings.put(Setting.valueOf(rawSetting), (Boolean) value);
            } catch (Exception ex) {
                Practice.getInstance().getLogger().info("Failed to load setting " + rawSetting + " (value=" + value + ") for " + playerUuid + ".");
            }
        });

        return ImmutableMap.copyOf(parsedSettings);
    }

    @Override
    public void saveSettings(UUID playerUuid, Map<Setting, Boolean> settings) throws IOException {
        MongoCollection<Document> settingsCollection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
        Document settingsDocument = new Document();

        settings.forEach((setting, value) -> {
            settingsDocument.put(setting.name(), value);
        });

        Document update = new Document("$set", new Document("settings", settingsDocument));

        try {
            settingsCollection.updateOne(buildQuery(playerUuid), update, MongoUtils.UPSERT_OPTIONS);
        } catch (MongoException ex) {
            throw new IOException(ex);
        }
    }

    private Document buildQuery(UUID playerUuid) {
        return new Document("_id", playerUuid.toString());
    }

}