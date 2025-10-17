package com.sky.oa.gson;

import android.graphics.Point;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by libin on 2018/12/13 9:15 PM.
 */
public class PointAdapter extends TypeAdapter<Point> {
    public Point read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String xy = reader.nextString();
        String[] parts = xy.split(",");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        return new Point(x, y);
    }

    public void write(JsonWriter writer, Point value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }
        String xy = value.x + "," + value.y;
        writer.value(xy);
    }
}
