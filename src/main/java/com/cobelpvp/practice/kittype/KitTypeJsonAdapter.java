package com.cobelpvp.practice.kittype;

import org.bukkit.craftbukkit.libs.com.google.gson.TypeAdapter;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonReader;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import java.io.IOException;

public final class KitTypeJsonAdapter extends TypeAdapter<KitType> {

    @Override
    public void write(JsonWriter writer, KitType type) throws IOException {
        writer.value(type.getId());
    }

    @Override
    public KitType read(JsonReader reader) throws IOException {
        return KitType.byId(reader.nextString());
    }

}