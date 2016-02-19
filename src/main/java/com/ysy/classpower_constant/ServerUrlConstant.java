package com.ysy.classpower_constant;

/**
 * Created by 姚圣禹 on 2016/2/18.
 */
public class ServerUrlConstant {

    private static final String URL = "http://10.0.2.2:5000/";
    public static final String USER_LOGIN_URL = URL + "user/login";
    public static final String USER_REGISTER_STUDENT_URL = URL + "user/register/student";
    public static final String USER_REGISTER_TEACHER_URL = URL + "user/register/teacher";
    public static final String USER_REGISTER_GETSCHOOLS_URL = URL + "user/register/getSchools";
    public static final String USER_REGISTER_GETMAJORS_URL = URL + "user/register/getMajors";
    public static final String USER_REGISTER_GETCLASSES_URL = URL + "user/register/getClasses";
    public static final String SEAT_GETSEATTOKEN_URL = URL + "seat/getSeatToken";
    public static final String SEAT_GETSEATMAP_URL = URL + "seat/getSeatMap";
    public static final String SEAT_CHOOSESEAT_URL = URL + "seat/chooseSeat";
    public static final String SEAT_FREESEAT = URL + "seat/freeSeat";
    public static final String USER_GETUSER_URL = URL + "user/getUser";
    public static final String USER_AVATAR_URL = URL + "user/avatar";
    public static final String USER_REGISTERCOURSE_URL = URL + "user/registerCourse";
    public static final String SEAT_CHECKIFOPEN_URL = URL + "seat/checkIfOpen";
    public static final String COURSE_QUESTION_GETALLQUESTIONS_URL = URL + "course/question/getAllQuestions";

}
