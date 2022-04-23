package ch.psc.datasource;

import ch.psc.domain.cipher.SecretKeySerialization;
import org.hildan.fxgson.FxGson;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class to read or write json files
 * contains method to read a json or to write inside a json
 *
 * @author SandroGuerotto
 * @version 1.0
 */
public class JSONWriterReader {

    /**
     * Writes object as a json object. Support Lists and simple Objects.
     * Supports {@link javafx.beans.property.Property}.
     *
     * @param filePath path for the json file
     * @param object   what to save
     * @return true if save was possible
     */
    public boolean writeToJson(String filePath, Object object) {

        try (FileWriter writer = new FileWriter(filePath)) {
            SecretKeySerialization secretKeySerializer = new SecretKeySerialization();
            String json = FxGson.coreBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(SecretKey.class, secretKeySerializer)
                    .registerTypeAdapter(SecretKeySpec.class, secretKeySerializer)
                    .create().toJson(object);

            writer.write(json);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reads a valid json file and parses it to the given class type.
     *
     * @param path  path to json file
     * @param clazz the class of T
     * @param <T>   type of desired object
     * @return create instance of the read json
     * @throws IOException if file could not be read
     */
    public <T> T readFromJson(String path, Class<T> clazz) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(path));
        SecretKeySerialization secretKeySerialization = new SecretKeySerialization();
        return FxGson.coreBuilder()
                .registerTypeAdapter(SecretKey.class, secretKeySerialization)
                .registerTypeAdapter(SecretKeySpec.class, secretKeySerialization)
                .create().fromJson(reader, clazz);
    }
}