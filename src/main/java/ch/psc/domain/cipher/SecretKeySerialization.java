package ch.psc.domain.cipher;

import com.google.gson.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Type;

public class SecretKeySerialization implements JsonSerializer<SecretKey>, JsonDeserializer<SecretKey> {
    @Override
    public SecretKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        return new SecretKeySpec(jsonObject.get("key").getAsString().getBytes(), jsonObject.get("algorithm").getAsString());
    }

    @Override
    public JsonElement serialize(SecretKey src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("algorithm", new JsonPrimitive(src.getAlgorithm()));
        object.add("key", new JsonPrimitive(new String(src.getEncoded())));
        return object;
    }
}
