package com.ysy.classpower_common.constant;

/**
 * Created by 姚圣禹 on 2016/2/18.
 */

public class ServerUrlConstant {

    private static final String URL = "http://10.15.85.200:5000/"; // 127.0.0.1 /10.15.85.197 /10.16.82.250 /10.0.2.2 + 5000
    public static final String USER_LOGIN_URL = URL + "user/login";
    public static final String USER_REGISTER_STUDENT_URL = URL + "user/register/student";
    public static final String USER_REGISTER_TEACHER_URL = URL + "user/register/teacher";
    public static final String USER_REGISTER_GETSCHOOLS_URL = URL + "user/register/getSchools";
    public static final String USER_REGISTER_GETMAJORS_URL = URL + "user/register/getMajors";
    public static final String USER_REGISTER_GETCLASSES_URL = URL + "user/register/getClasses";
    public static final String USER_ME_URL = URL + "user/me";
    public static final String USER_AVATAR_URL = URL + "user/avatar";
    public static final String USER_REGISTERCOURSE_URL = URL + "user/registerCourse";
    public static final String COURSE_NTFC_GETNTFCS_URL = URL + "course/notification/getNotifications";
    public static final String COURSE_QUESTION_GETALLQUESTIONS_URL = URL + "course/question/getAllQuestions";
    public static final String SEAT_GETSEATTOKEN_URL = URL + "seat/getSeatToken";
    public static final String SEAT_GETSEATMAP_URL = URL + "seat/getSeatMap";
    public static final String SEAT_CHOOSESEAT_URL = URL + "seat/chooseSeat";
    public static final String SEAT_FREESEAT = URL + "seat/freeSeat";
    public static final String SEAT_CHECKIFOPEN_URL = URL + "seat/checkIfOpen";

}
