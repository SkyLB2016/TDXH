package com.sky.oa.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.sky.oa.data.model.ActivityModel;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 序列化成json
 */
public class ActivityListSerial implements JsonSerializer<List<ActivityModel>> {

    @Override
    public JsonElement serialize(List<ActivityModel> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray arr = new JsonArray();
        for (int i = 0; i < src.size(); i++) {
            ActivityModel model = src.get(i);
            JsonObject json = new JsonObject();
            json.addProperty("class", model.getActivityName());
            json.addProperty("component", model.getComponentName());
            json.addProperty("image", model.getImg());
            json.addProperty("des", model.getDescribe());
            arr.add(json);
        }
        return arr;
    }
}