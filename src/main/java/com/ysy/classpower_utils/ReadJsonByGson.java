package com.ysy.classpower_utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;

/**
 * Created by 姚圣禹 on 2016/2/4.
 */
public class ReadJsonByGson {

    JsonObject object = null;

    public ReadJsonByGson(String json) {
        JsonParser parser = new JsonParser();
        object = (JsonObject) parser.parse(json);
    }

    public ReadJsonByGson(InputStreamReader json) {
        JsonParser parser = new JsonParser();
        object = (JsonObject) parser.parse(json);
    }

    public String getValue(String key) {
        return object.get(key).getAsString();
    }

    public String getArrayValue(String key, String array_key) {
        JsonObject arrayObject = object.get(key).getAsJsonObject();
        return arrayObject.get(array_key).getAsString();
    }

}
