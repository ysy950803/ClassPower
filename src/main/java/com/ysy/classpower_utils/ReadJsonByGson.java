package com.ysy.classpower_utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;

/**
 * Created by 姚圣禹 on 2016/2/4.
 */
public class ReadJsonByGson {

    JsonObject object = null;

    public ReadJsonByGson(String json) {
        JsonParser parser = new JsonParser();
        object = (JsonObject) parser.parse(json);

//        JsonArray array = object.getAsJsonArray("seats");
//        for (int i = 0; i < array.size(); ++i) {
//            object = (JsonObject) array.get(i);
//        }

    }

    public ReadJsonByGson(InputStreamReader json) {
        JsonParser parser = new JsonParser();
        object = (JsonObject) parser.parse(json);
    }

    public String getValue(String key) {
        if (object.get(key).isJsonNull())
            return "";
        else
            return object.get(key).getAsString();
    }

    public String getArrayValue(String key, String array_key) {
        if (object.get(key).isJsonNull())
            return "";
        else {
            JsonObject arrayObject = object.get(key).getAsJsonObject();
            if (arrayObject.get(key).isJsonNull())
                return "";
            else
                return arrayObject.get(array_key).getAsString();
        }
    }

}
