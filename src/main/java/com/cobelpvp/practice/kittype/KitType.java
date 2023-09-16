package com.cobelpvp.practice.kittype;

import com.mongodb.client.MongoCollection;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.util.MongoUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.SerializedName;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public final class KitType {

    private static final String MONGO_COLLECTION_NAME = "kitTypes";
    @Getter private static final List<KitType> allTypes = new ArrayList<>();
    public static KitType teamFight = new KitType("team_fight");

    static {
        MongoCollection<Document> collection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);

        collection.find().iterator().forEachRemaining(doc -> {
            allTypes.add(Practice.plainGson.fromJson(doc.toJson(), KitType.class));
        });
        teamFight.icon = new MaterialData(Material.BEACON);
        teamFight.id = "team_fight";
        teamFight.displayName = "Team Fights";
        teamFight.displayColor = ChatColor.DARK_GREEN;
        allTypes.sort(Comparator.comparing(KitType::getSort));
    }

    public KitType(String id) {
        this.id = id;
    }

    @Getter @SerializedName("_id") private String id;

    @Setter private String displayName;

    @Getter @Setter private ChatColor displayColor;

    @Setter private MaterialData icon;

    @Getter @Setter private ItemStack[] editorItems = new ItemStack[0];

    @Setter private ItemStack[] defaultArmor = new ItemStack[0];

    @Setter private ItemStack[] defaultInventory = new ItemStack[0];

    @Getter @Setter private boolean editorSpawnAllowed = true;

    @Getter @Setter private boolean hidden = false;

    @Getter @Setter private HealingMethod healingMethod = HealingMethod.POTIONS;

    @Getter @Setter private boolean buildingAllowed = false;

    @Getter @Setter private boolean healthShown = false;

    @Getter @Setter private boolean hardcoreHealing = false;

    @Getter @Setter private boolean pearlDamage = true;

    @Getter @Setter private int sort = 0;

    @Getter @Setter private boolean supportsRanked = false;

    public static KitType byId(String id) {
        for (KitType kitType : allTypes) {
            if (kitType.getId().equalsIgnoreCase(id)) {
                return kitType;
            }
        }

        return null;
    }

    public String getColoredDisplayName() {
        return displayColor + displayName;
    }

    public void saveAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> {
            MongoCollection<Document> collection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
            Document kitTypeDoc = Document.parse(Practice.plainGson.toJson(this));
            kitTypeDoc.remove("_id"); // upserts with an _id field is weird.

            Document query = new Document("_id", id);
            Document kitUpdate = new Document("$set", kitTypeDoc);

            collection.updateOne(query, kitUpdate, MongoUtils.UPSERT_OPTIONS);
        });
    }

    public void deleteAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> {
            MongoCollection<Document> collection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
            collection.deleteOne(new Document("_id", id));
        });
    }

    @Override
    public String toString() {
        return displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public MaterialData getIcon() {
        return icon;
    }

    public ItemStack[] getDefaultArmor() {
        return defaultArmor;
    }

    public ItemStack[] getDefaultInventory() {
        return defaultInventory;
    }

}