package com.sky.oa.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sky.oa.data.model.ActivityModel;

import java.lang.reflect.Type;

/**
 * json转实体
 * Created by libin on 2018/12/13 8:04 PM.
 */
public class ActivityDeserial implements JsonDeserializer<ActivityModel> {

    @Override
    public ActivityModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        JsonElement cla = object.get("activityName");
        JsonElement img = object.get("img");
        ActivityModel model = new ActivityModel(
                cla.isJsonNull() ? null : cla.getAsString(),
                object.get("describe").getAsString(),
                img.isJsonNull() ? 0 : img.getAsInt(),
                object.get("componentName").getAsString()
        );
        JsonElement version = object.get("version");
        if (version!=null&&!version.isJsonNull())
            model.setVersion(version.getAsDouble());
        return model;
    }
}
