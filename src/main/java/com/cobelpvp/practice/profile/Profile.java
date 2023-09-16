package com.cobelpvp.practice.profile;

import com.cobelpvp.practice.util.MongoUtils;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Data;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
public class Profile {

    private final UUID uniqueId;

    private int gamesPlayed = 0, gamesWon = 0, loses = 0;

    public Profile(UUID uniqueId) {
        this.uniqueId = uniqueId;

        load();
    }

    public void load() {
        Document document = MongoUtils.getCollection("profiles").find(Filters.eq("uuid", uniqueId.toString())).first();

        if (document == null) return;

        gamesPlayed = document.getInteger("gamesPlayed");
        gamesWon = document.getInteger("gamesWon");
        loses = document.getInteger("loses");

    }

    public void save() {
        CompletableFuture.runAsync(() -> {
            Document document = new Document();

            document.put("uuid", this.uniqueId.toString());
            document.put("gamesPlayed", gamesPlayed);
            document.put("gamesWon", gamesWon);
            document.put("loses", loses);

            Bson filter = Filters.eq("uuid", uniqueId.toString());

            MongoUtils.getCollection("profiles").replaceOne(filter, document, new ReplaceOptions().upsert(true));
        });
    }
}
