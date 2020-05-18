package util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides I/O for Json.
 */
public class JsonIO {
    private static final Gson gson = new Gson();

    /**
     * Reads Json file and returns list of objects of the specified type.
     * @param in source of json
     * @param clazz class of T
     * @param <T> Object type to which json entry is converted
     * @return list of objects of type clazz
     * @throws IOException
     */
    public static <T> List<T> readJsonStream(InputStream in, Class<T> clazz) throws IOException {
        var reader = new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        List<T> list = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            T item = gson.fromJson(reader, clazz);
            list.add(item);
        }
        reader.endArray();
        reader.close();
        return list;
    }

    /**
     * Write list of objects to a json file.
     * @param out destination of json
     * @param list objects to be stored in json
     * @throws IOException
     */
    public static void writeJsonStream(OutputStream out, List<?> list) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        writer.setIndent("  ");
        writer.beginArray();
        for (var item : list) {
            gson.toJson(item, item.getClass(), writer);
        }
        writer.endArray();
        writer.close();
    }
}
