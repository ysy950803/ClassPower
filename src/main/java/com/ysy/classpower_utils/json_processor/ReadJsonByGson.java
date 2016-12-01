package com.ysy.classpower_utils.json_processor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

    public String[] getChoicesOfQuestionsInTest(int position) {
        JsonArray questions_array = object.getAsJsonArray("questions");
        JsonObject choice_object = questions_array.get(position).getAsJsonObject();
        JsonArray choices_array = choice_object.getAsJsonArray("choices");
        String[] details = new String[choices_array.size()];
        for (int i = 0; i < choices_array.size(); ++i) {
            details[i] = choices_array.get(i).getAsString();
        }
        return details;
    }

    public String[] getAnswersOfQuestionsInTest(int position) {
        JsonArray questions_array = object.getAsJsonArray("questions");
        JsonObject choice_object = questions_array.get(position).getAsJsonObject();
        JsonArray choices_array = choice_object.getAsJsonArray("answers");
        String[] details = new String[choices_array.size()];
        for (int i = 0; i < choices_array.size(); ++i) {
            details[i] = choices_array.get(i).getAsString();
        }
        return details;
    }

    public String[] getQuestionsInTest(String question_details) {
        JsonObject question_object;
        JsonArray questions_array = object.getAsJsonArray("questions");
        String[] details = new String[questions_array.size()];
        for (int i = 0; i < questions_array.size(); ++i) {
            question_object = questions_array.get(i).getAsJsonObject();
            details[i] = question_object.get(question_details).getAsString();
        }
        return details;
    }

    public String[] getAllTestsInfo(String test_details) {
        JsonObject test_object;
        JsonArray tests_array = object.getAsJsonArray("tests");
        String[] details = new String[tests_array.size()];
        for (int i = 0; i < tests_array.size(); ++i) {
            test_object = tests_array.get(i).getAsJsonObject();
            if (test_object.get(test_details).isJsonNull())
                details[i] = "0";
            else
                details[i] = test_object.get(test_details).getAsString();
        }
        return details;
    }

    public String[] getNotificationsInfo(String notification_details) {
        JsonObject notification_object;
        JsonArray notifications_array = object.getAsJsonArray("notifications");
        List<JsonObject> objectList = new ArrayList<>();
        int on_top_count = 0;
        // Gson/Json操作待定，暂时使用List泛型来操作
        for (int i = 0; i < notifications_array.size(); ++i) {
            objectList.add(i, (JsonObject) notifications_array.get(i));
        }
        // 置顶冒泡排序 true > false，把on_top值为true的排头
        for (int i = notifications_array.size() - 1; i >= 0; --i) {
            JsonObject notifications_object_temp;
            // 获取置顶数目
            if (objectList.get(i).getAsJsonObject().get("on_top").toString().equals("true")) {
                on_top_count = on_top_count + 1;
            }
            for (int j = 0; j < i; ++j) {
                if (objectList.get(j + 1).getAsJsonObject().get("on_top").toString().equals("true") &&
                        objectList.get(j).getAsJsonObject().get("on_top").toString().equals("false")) {
                    notifications_object_temp = objectList.get(j);
                    objectList.set(j, objectList.get(j + 1));
                    objectList.set(j + 1, notifications_object_temp);
                }
            }
        }
        String[] details = new String[notifications_array.size()];
        if (notification_details.equals("on_top_count")) { // 传递特定参数获取置顶数目，待优化
            details[0] = "on_top_count_" + on_top_count;
//            details[1] = "" + on_top_count;
        } else if (notification_details.equals("on_top")) {
            for (int i = 0; i < notifications_array.size(); ++i) {
//                notification_object = (JsonObject) notifications_array.get(i);
                notification_object = objectList.get(i);
                details[i] = notification_object.get(notification_details).toString();
            }
        } else {
            for (int i = 0; i < notifications_array.size(); ++i) {
//                notification_object = (JsonObject) notifications_array.get(i);
                notification_object = objectList.get(i);
                details[i] = notification_object.get(notification_details).getAsString();
            }
        }
        return details;
    }

    public String[] getCoursesTimesWeeksInfo() {
        JsonObject course_object;
        JsonArray courses_array = object.getAsJsonObject("user").getAsJsonArray("courses");
        JsonArray courses_times_array;
        JsonObject courses_time_object;
        String[] details = new String[courses_array.size()];
        String[] details_temp = new String[courses_array.size()];
        int temp_count = 0;
        for (int i = 0; i < courses_array.size(); ++i) {
            temp_count = 0;
            course_object = (JsonObject) courses_array.get(i);
            courses_times_array = course_object.getAsJsonArray("times");
            for (int j = 0; j < courses_times_array.size(); ++j) {
                courses_time_object = courses_times_array.get(j).getAsJsonObject();
                String week_str = courses_time_object.get("weeks").toString();
                if (week_str.contains(",")) {
                    week_str = week_str.replace("[", "");
                    week_str = week_str.replace("]", "");
                    if (details[i] == null) {
                        details[i] = week_str + ",";
                    } else
                        details[i] += (week_str + ",");
                } else
                    details[i] = "";
                // 从上至下到此步得到形如1,2,3,4,5,6,7,8,
            }
            for (int k = 1; k <= 16; ++k) { // 剔除重复week，并且自然形成升序排序，使用for循环减少contains函数的调用次数
                if (details[i].contains(k + ",")) {
                    if (details_temp[i] == null)
                        details_temp[i] = k + ",";
                    else
                        details_temp[i] += (k + ",");
                    temp_count = temp_count + 1;
                }
            }
            if (temp_count == 16)
                details[i] = "1-16周";
            else if (temp_count == 8 && details_temp[i].contains("8,"))
                details[i] = "1-8周";
            else if (temp_count == 8 && details_temp[i].contains("16,"))
                details[i] = "9-16周";
            else
                details[i] = details_temp[i].substring(0, details_temp[i].length() - 1); // substring覆盖
        }
        return details;
    }

    public String[] getCoursesTimesRoomNameInfo(String current_week, String current_day) {
        JsonObject course_object;
        JsonArray courses_array = object.getAsJsonObject("user").getAsJsonArray("courses");
        JsonArray courses_times_array;
        JsonObject courses_time_object;
        String[] details = new String[courses_array.size()];
        for (int i = 0; i < courses_array.size(); ++i) {
            course_object = (JsonObject) courses_array.get(i);
            courses_times_array = course_object.getAsJsonArray("times");
            for (int j = 0; j < courses_times_array.size(); ++j) {
                courses_time_object = courses_times_array.get(j).getAsJsonObject();
                String weeks_str = courses_time_object.get("weeks").toString(); // [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]
                if (weeks_str.contains(current_week + ",") || weeks_str.contains(current_week + "]")) {
                    String days_str = courses_time_object.get("days").toString();
                    if (days_str.contains(current_day + ",") || days_str.contains(current_day + "]")) {
                        details[i] = courses_time_object.get("room_name").getAsString();
                    } else
                        details[i] = "";
                } else
                    details[i] = "";
            }
        }
        return details;
    }

    public String[] getCoursesTimesRoomIdInfo(String current_week, String current_day) {
        JsonObject course_object;
        JsonArray courses_array = object.getAsJsonObject("user").getAsJsonArray("courses");
        JsonArray courses_times_array;
        JsonObject courses_time_object;
        String[] details = new String[courses_array.size()];
        for (int i = 0; i < courses_array.size(); ++i) {
            course_object = (JsonObject) courses_array.get(i);
            courses_times_array = course_object.getAsJsonArray("times");
            for (int j = 0; j < courses_times_array.size(); ++j) {
                courses_time_object = courses_times_array.get(j).getAsJsonObject();
                String weeks_str = courses_time_object.get("weeks").toString(); // [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]
                if (weeks_str.contains(current_week + ",") || weeks_str.contains(current_week + "]")) {
                    String days_str = courses_time_object.get("days").toString();
                    if (days_str.contains(current_day + ",") || days_str.contains(current_day + "]")) {
                        details[i] = courses_time_object.get("room_id").getAsString();
                    } else
                        details[i] = "";
                } else
                    details[i] = "";
            }
        }
        return details;
    }

    public String[] getCoursesTimesPeriodInfo(String current_week, String current_day) {
        JsonObject course_object;
        JsonArray courses_array = object.getAsJsonObject("user").getAsJsonArray("courses");
        JsonArray courses_times_array;
        JsonObject courses_time_object;
        String[] details = new String[courses_array.size()];
        for (int i = 0; i < courses_array.size(); ++i) {
            course_object = (JsonObject) courses_array.get(i);
            courses_times_array = course_object.getAsJsonArray("times");
            for (int j = 0; j < courses_times_array.size(); ++j) {
                courses_time_object = courses_times_array.get(j).getAsJsonObject();
                String weeks_str = courses_time_object.get("weeks").toString(); // [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]
                if (weeks_str.contains(current_week + ",") || weeks_str.contains(current_week + "]")) {
                    String days_str = courses_time_object.get("days").toString();
                    if (days_str.contains(current_day + ",") || days_str.contains(current_day + "]")) {
                        details[i] = courses_time_object.get("period").toString();
                    } else
                        details[i] = "";
                } else
                    details[i] = "";
            }
        }
        return details;
    }

    // 根据传递当前周数week来获取是否有课并且是星期几上课
    public String[] getCoursesTimesDaysInfo(String current_week) {
        JsonObject course_object;
        JsonArray courses_array = object.getAsJsonObject("user").getAsJsonArray("courses");
        JsonArray courses_times_array;
        JsonObject courses_time_object;
        String[] details = new String[courses_array.size()];
        for (int i = 0; i < courses_array.size(); ++i) {
            course_object = (JsonObject) courses_array.get(i);
            courses_times_array = course_object.getAsJsonArray("times");
            for (int j = 0; j < courses_times_array.size(); ++j) {
                courses_time_object = courses_times_array.get(j).getAsJsonObject();
                String weeks_str = courses_time_object.get("weeks").toString(); // [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]
                if (weeks_str.contains(current_week + ",") || weeks_str.contains(current_week + "]")) {
                    String days_str = courses_time_object.get("days").toString().replace("[", "");
                    days_str = days_str.replace("]", "");
                    if (details[i] == null)
                        details[i] = days_str + ",";
                    else
                        details[i] += (days_str + ",");
                } else
                    details[i] = "";
            }
            details[i] = details[i].substring(0, details[i].length() - 1); // substring覆盖
        }
        return details;
    }

    public String[] getCoursesTeachersInfo() {
        JsonObject course_object;
        JsonArray courses_array = object.getAsJsonObject("user").getAsJsonArray("courses");
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
        JsonArray courses_array = object.getAsJsonObject("user").getAsJsonArray("courses");
        String[] details = new String[courses_array.size()];
        for (int i = 0; i < courses_array.size(); ++i) {
            course_object = (JsonObject) courses_array.get(i);
            details[i] = course_object.get(course_details).getAsString();
        }
        return details;
    }

    public String[] getSchoolsInfo(String school_details) {
        JsonObject school_object;
        JsonArray schools_array = object.getAsJsonArray("schools");
        String[] details = new String[schools_array.size()];
        for (int i = 0; i < schools_array.size(); ++i) {
            school_object = (JsonObject) schools_array.get(i);
            details[i] = school_object.get(school_details).getAsString();
        }
        return details;
    }

    public String[] getMajorsInfo(String major_details) {
        JsonObject major_object;
        JsonArray majors_array = object.getAsJsonArray("majors");
        String[] details = new String[majors_array.size()];
        for (int i = 0; i < majors_array.size(); ++i) {
            major_object = (JsonObject) majors_array.get(i);
            details[i] = major_object.get(major_details).getAsString();
        }
        return details;
    }

    public String[] getClassesInfo(String class_details) {
        JsonObject class_object;
        JsonArray classes_array = object.getAsJsonArray("classes");
        String[] details = new String[classes_array.size()];
        for (int i = 0; i < classes_array.size(); ++i) {
            class_object = (JsonObject) classes_array.get(i);
            details[i] = class_object.get(class_details).getAsString();
        }
        return details;
    }

    public int[][] getSeatState(int col, int row) {
        int[][] seat_state = new int[col][row]; // 0无座 1好座无人 2好座有人或者坏座
        JsonObject seat_object;
        JsonArray seats_array = object.getAsJsonArray("seats");
        for (int i = 0; i < seats_array.size(); ++i) {
            seat_object = (JsonObject) seats_array.get(i);
            int col_num = seat_object.get("col").getAsInt() - 1;
            int row_num = seat_object.get("row").getAsInt() - 1;
            int status = seat_object.get("status").getAsInt(); // 0好座 1坏座
            String cur_stu_str = seat_object.get("cur_stu").getAsString();
            if (status == 0 && cur_stu_str.equals("")) {
                seat_state[col_num][row_num] = 1;
            } else if (status == 1 || (status == 0 && !cur_stu_str.equals(""))) {
                seat_state[col_num][row_num] = 2;
            } else {

            }
        }
        return seat_state;
    }

    public String[][] getSeatInfo(int col, int row, String info_details) {
        String[][] seat_info = new String[col][row];
        JsonObject seat_object;
        JsonArray seats_array = object.getAsJsonArray("seats");
        for (int i = 0; i < seats_array.size(); ++i) {
            seat_object = (JsonObject) seats_array.get(i);
            int col_num = seat_object.get("col").getAsInt() - 1;
            int row_num = seat_object.get("row").getAsInt() - 1;
            seat_info[col_num][row_num] = seat_object.get(info_details).getAsString();
        }
        return seat_info;
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

    public String[] getArray(String key) {
        JsonArray jsonArray = object.getAsJsonArray(key);
        String[] details = new String[jsonArray.size()];
        if (jsonArray.size() == 0)
            details = new String[0];
        else {
            for (int i = 0; i < jsonArray.size(); ++i) {
                details[i] = jsonArray.get(i).getAsString();
            }
        }
        return details;
    }

}