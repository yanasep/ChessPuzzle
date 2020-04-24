package util;

import app.Controller;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonIO {
    private static final Gson gson = new Gson();
    
    public static List<Controller.ScoreRow> readJsonStream(InputStream in) throws IOException {
        var reader = new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        List<Controller.ScoreRow> list = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            Controller.ScoreRow item = gson.fromJson(reader, Controller.ScoreRow.class);
            list.add(item);
        }
        reader.endArray();
        reader.close();
        return list;
    }

    public static void writeJsonStream(OutputStream out, List<Controller.ScoreRow> list) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        writer.setIndent("  ");
        writer.beginArray();
        for (var item : list) {
            gson.toJson(item, Controller.ScoreRow.class, writer);
        }
        writer.endArray();
        writer.close();
    }
}
