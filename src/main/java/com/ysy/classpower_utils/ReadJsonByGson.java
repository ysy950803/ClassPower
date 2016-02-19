package com.ysy.classpower_utils;

import com.google.gson.JsonArray;
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

    public int[][] getSeatState(int col, int row) {
        int seat_state[][] = new int[col][row]; // 0无座 1好座无人 2好座有人或者坏座
        JsonObject seat_object;
        JsonArray seats_array = object.getAsJsonArray("seats");
        for (int i = 0; i < seats_array.size(); ++i) {
            seat_object = (JsonObject) seats_array.get(i);
            int col_num = seat_object.get("col").getAsInt() - 1;
            int row_num = seat_object.get("row").getAsInt() - 1;
            boolean exists = seat_object.get("exists").getAsBoolean();
            int status = seat_object.get("status").getAsInt(); // 0好座 1坏座
            String cur_stu_str = seat_object.get("cur_stu").getAsString();
            if (exists) {
                if (status == 0 && cur_stu_str.equals("")) {
                    seat_state[col_num][row_num] = 1;
                } else if (status == 1 || (status == 0 && !cur_stu_str.equals(""))) {
                    seat_state[col_num][row_num] = 2;
                }
            }
//            else
//                seat_state[col_num][row_num] = 0;
        }
        return seat_state;
    }

    public String getValue(String key) {
        if (object.get(key).isJsonNull())
            return "";
        else
            return object.get(key).getAsString();
    }

    public int getIntValue(String key) {
        return object.get(key).getAsInt();
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
