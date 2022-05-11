package ch.psc.datasource;

import org.hildan.fxgson.FxGson;

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

        try(FileWriter writer = new FileWriter(filePath))  {
            String json = FxGson.coreBuilder()
                    .setPrettyPrinting()
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
        return FxGson.create().fromJson(reader, clazz);
    }
}
