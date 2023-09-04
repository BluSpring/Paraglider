package tictim.paraglider.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.chat.Component;

public class ExtraExtraCodecs {
    public static final Codec<JsonElement> JSON = Codec.PASSTHROUGH.xmap(dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(), jsonElement -> new Dynamic(JsonOps.INSTANCE, jsonElement));
    public static final Codec<Component> COMPONENT = JSON.flatXmap(jsonElement -> {
        try {
            return DataResult.success(Component.Serializer.fromJson(jsonElement));
        }
        catch (JsonParseException jsonParseException) {
            return DataResult.error(jsonParseException.getMessage());
        }
    }, component -> {
        try {
            return DataResult.success(Component.Serializer.toJsonTree(component));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return DataResult.error(illegalArgumentException.getMessage());
        }
    });
    public static final Codec<Component> FLAT_COMPONENT = Codec.STRING.flatXmap(string -> {
        try {
            return DataResult.success(Component.Serializer.fromJson(string));
        }
        catch (JsonParseException jsonParseException) {
            return DataResult.error(jsonParseException.getMessage());
        }
    }, component -> {
        try {
            return DataResult.success(Component.Serializer.toJson(component));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return DataResult.error(illegalArgumentException.getMessage());
        }
    });
}
