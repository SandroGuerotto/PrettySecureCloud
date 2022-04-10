package ch.psc.datasource;

import com.google.gson.reflect.TypeToken;
import org.hildan.fxgson.FxGson;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class JSONWriterReader {

    /**
     * Writes object as a json object. Support Lists and simple Objects.
     * In addition write supports {@link javafx.beans.property.Property}.
     *
     * @param filePath path for the json file
     * @param object   what to save
     * @return true if save was possible
     */
    public boolean writeToJson(String filePath, Object object) {

        try (FileWriter writer = new FileWriter(filePath)) {
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
     * Reads a JSON File with an array and converts it to a List.
     *
     * @param fileName which json to read? // path to json
     * @param clazz    class to convert json
     * @param <T>      ListItem type
     * @return List of the given type
     * @throws IOException can not read file
     */
    public <T> List<T> readListFromJson(String fileName, Class<T> clazz) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(fileName));
        Type typeOfT = TypeToken.getParameterized(List.class, clazz).getType();
        return FxGson.create().fromJson(reader, typeOfT);

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