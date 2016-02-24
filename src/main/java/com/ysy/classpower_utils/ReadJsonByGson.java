package com.ysy.classpower_utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

    public String[] getCoursesTimesWeeksInfo() {
        return null;
    }

    public String[] getCoursesTimesRoomNameInfo() {
        return null;
    }

    public String[] getCoursesTimesRoomIdInfo() {
        return null;
    }

    public String[] getCoursesTimesPeriodInfo() {
        return null;
    }

    // 根据传递当前周数week来获取是否有课并且是星期几上课
    public String[] getCoursesTimesDaysInfo(String week) {
        JsonObject course_object;
        JsonArray courses_array = object.getAsJsonArray("courses");
        JsonArray courses_times_array;
        JsonObject courses_time_object;
        String[] details = new String[courses_array.size()];
        for (int i = 0; i < courses_array.size(); ++i) {
            course_object = (JsonObject) courses_array.get(i);
            courses_times_array = course_object.getAsJsonArray("times");
            for (int j = 0; j < courses_times_array.size(); ++j) {
                courses_time_object = courses_times_array.get(j).getAsJsonObject();
                String weeks_str = courses_time_object.get("weeks").toString(); // [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]
                if (weeks_str.contains(week + ",") || weeks_str.contains(week + "]")) {
                    String days_str = courses_time_object.get("days").toString().replace("[", "");
                    days_str = days_str.replace("]", "");
                    if (details[i] == null)
                        details[i] = days_str + ",";
                    else
                        details[i] += (days_str + ",");
                    details[i] = details[i].substring(0, details[i].length() - 1);
                } else
                    details[i] = "";
            }
        }
        return details;
    }

    public String[] getCoursesTeachersInfo() {
        JsonObject course_object;
        JsonArray courses_array = object.getAsJsonArray("courses");
        JsonArray courses_teachers_array;
        String[] details = new String[courses_array.size()];
        for (int i = 0; i < courses_array.size(); ++i) {
            course_object = (JsonObject) courses_array.get(i);
            courses_teachers_array = course_object.getAsJsonArray("teachers");
            for (int j = 0; j < courses_teachers_array.size(); ++j) {
                if (j == courses_teachers_array.size() - 1) {
                    if (details[i] == null)
                        details[i] = courses_teachers_array.get(j).getAsString();
                    else
                        details[i] += courses_teachers_array.get(j).getAsString();
                } else {
                    if (details[i] == null)
                        details[i] = (courses_teachers_array.get(j).getAsString() + "/");
                    else
                        details[i] += (courses_teachers_array.get(j).getAsString() + "/");
                }
            }
        }
        return details;
    }

    public String[] getCoursesBasicInfo(String course_details) {
        JsonObject course_object;
        JsonArray courses_array = object.getAsJsonArray("courses");
        String[] details = new String[courses_array.size()];
        if (course_details.equals("course_id")) {
            for (int i = 0; i < courses_array.size(); ++i) {
                course_object = (JsonObject) courses_array.get(i);
                details[i] = course_object.get("course_id").getAsString();
            }
        } else if (course_details.equals("course_name")) {
            for (int i = 0; i < courses_array.size(); ++i) {
                course_object = (JsonObject) courses_array.get(i);
                details[i] = course_object.get("course_name").getAsString();
            }
        } else if (course_details.equals("sub_id")) {
            for (int i = 0; i < courses_array.size(); ++i) {
                course_object = (JsonObject) courses_array.get(i);
                details[i] = course_object.get("sub_id").getAsString();
            }
        }
        return details;
    }

    public String[] getSchoolsInfo(String school_details) {
        JsonObject school_object;
        JsonArray schools_array = object.getAsJsonArray("schools");
        String[] details = new String[schools_array.size()];
        if (school_details.equals("school_id")) {
            for (int i = 0; i < schools_array.size(); ++i) {
                school_object = (JsonObject) schools_array.get(i);
                details[i] = school_object.get("school_id").getAsString();
            }
        } else if (school_details.equals("school_name")) {
            for (int i = 0; i < schools_array.size(); ++i) {
                school_object = (JsonObject) schools_array.get(i);
                details[i] = school_object.get("school_name").getAsString();
            }
        }
        return details;
    }

    public String[] getMajorsInfo(String major_details) {
        JsonObject major_object;
        JsonArray majors_array = object.getAsJsonArray("majors");
        String[] details = new String[majors_array.size()];
        if (major_details.equals("major_id")) {
            for (int i = 0; i < majors_array.size(); ++i) {
                major_object = (JsonObject) majors_array.get(i);
                details[i] = major_object.get("major_id").getAsString();
            }
        } else if (major_details.equals("major_name")) {
            for (int i = 0; i < majors_array.size(); ++i) {
                major_object = (JsonObject) majors_array.get(i);
                details[i] = major_object.get("major_name").getAsString();
            }
        }
        return details;
    }

    public String[] getClassesInfo(String class_details) {
        JsonObject class_object;
        JsonArray classes_array = object.getAsJsonArray("classes");
        String[] details = new String[classes_array.size()];
        if (class_details.equals("class_id")) {
            for (int i = 0; i < classes_array.size(); ++i) {
                class_object = (JsonObject) classes_array.get(i);
                details[i] = class_object.get("class_id").getAsString();
            }
        } else if (class_details.equals("class_name")) {
            for (int i = 0; i < classes_array.size(); ++i) {
                class_object = (JsonObject) classes_array.get(i);
                details[i] = class_object.get("class_name").getAsString();
            }
        }
        return details;
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

    public boolean getBoolValue(String key) {
        return object.get(key).getAsBoolean();
    }

    public String getArrayValue(String key, String array_key) {
        if (object.get(key).isJsonNull())
            return "";
        else {
            JsonObject arrayObject = object.get(key).getAsJsonObject();
            if (arrayObject.get(array_key).isJsonNull())
                return "";
            else
                return arrayObject.get(array_key).getAsString();
        }
    }

    public boolean getArrayBoolValue(String key, String array_key) {
        if (object.get(key).isJsonNull())
            return false;
        else {
            JsonObject arrayObject = object.get(key).getAsJsonObject();
            return arrayObject.get(array_key).getAsBoolean();
        }
    }

}