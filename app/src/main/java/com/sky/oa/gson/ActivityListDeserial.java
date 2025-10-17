package com.sky.oa.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sky.oa.entity.ActivityModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * json转实体
 * Created by libin on 2018/12/13 8:04 PM.
 */
public class ActivityListDeserial implements JsonDeserializer<List<ActivityModel>> {

    @Override
    public List<ActivityModel> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<ActivityModel> list = new ArrayList<>();
        JsonArray array = json.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            JsonElement cla = object.get("activityName");
            JsonElement img = object.get("img");
            ActivityModel model = new ActivityModel(
                    cla.isJsonNull() ? null : cla.getAsString(),
                    object.get("describe").getAsString(),
                    img.isJsonNull() ? 0 : img.getAsInt(),
                    object.get("componentName").getAsString()
            );
            model.setVersion(object.get("version").getAsDouble());
            list.add(model);
        }
        return list;
    }
}
