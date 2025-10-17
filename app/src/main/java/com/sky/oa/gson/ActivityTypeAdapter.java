package com.sky.oa.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sky.oa.data.model.ActEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 2018/12/13 9:31 PM.
 */
public class ActivityTypeAdapter extends TypeAdapter<ActEntity> {
    @Override
    public ActEntity read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        ActEntity entity = new ActEntity();
//        in.setLenient(true);
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "activityName":
                    entity.setName(in.nextString());
                    break;
                case "componentName":
                    entity.setComponent(in.nextString());
                    break;
                case "describe":
                    entity.setDes(in.nextString());
                    break;
                case "img":
                    entity.setImage(in.nextInt());
                    break;
                case "version":
                    entity.setVersion(in.nextDouble());
                    break;
                case "objList":
                    List<ActEntity> list = new ArrayList<>();
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(read(in));
//                        in.beginObject();
//                        while (in.hasNext()) {
//                            switch (in.nextName()) {
//                                case "activityName":
//                                    in.nextString();
//                                    break;
//                                case "componentName":
//                                    in.nextString();
//                                    break;
//                                case "describe":
//                                    in.nextString();
//                                    break;
//                                case "img":
//                                    in.nextInt();
//                                    break;
//                            }
//                        }
//                        in.endObject();
                    }
                    in.endArray();
                    entity.setObjList(list);
                    break;
            }
        }
        in.endObject();
        return entity;
    }

    @Override
    public void write(JsonWriter out, ActEntity value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.beginObject();
        out.name("clame").value(value.getName());
        out.name("cme").value(value.getComponent());
        out.name("debe").value(value.getDes());
        out.name("img").value(value.getImage());
        out.name("v§ion").value(value.getVersion());
//        if (value.getObjList() != null)
//            out.name("objList").jsonValue(value.getObjList().toString());
        out.endObject();
    }
}
