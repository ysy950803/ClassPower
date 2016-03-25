package com.ysy.classpower_common.constant;

/**
 * Created by 姚圣禹 on 2016/2/18.
 */

public class ServerUrlConstant {

    //    private static String URL_FIGURE = "10.15.85.201"; // 127.0.0.1 /10.15.85.197 /10.16.82.250 /10.0.2.2 + 5000
//    private static String URL = "http://" + URL_FIGURE + ":5000/";
    private static String USER_LOGIN_URL = "user/login";
    private static String USER_REGISTER_STUDENT_URL = "user/register/student";
    private static String USER_REGISTER_TEACHER_URL = "user/register/teacher";
    private static String USER_REGISTER_GETSCHOOLS_URL = "user/register/getSchools";
    private static String USER_REGISTER_GETMAJORS_URL = "user/register/getMajors";
    private static String USER_REGISTER_GETCLASSES_URL = "user/register/getClasses";
    private static String USER_ME_URL = "user/me";
    private static String USER_AVATAR_URL = "user/avatar";
    private static String USER_REGISTERCOURSE_URL = "user/registerCourse";
    private static String COURSE_NTFC_GETNTFCS_URL = "course/notification/getNotifications";
    private static String COURSE_QUESTION_GETALLQUESTIONS_URL = "course/question/getAllQuestions";
    private static String COURSE_TEST_GETFINISHEDTESTS_URL = "course/test/getFinishedTests";
    private static String COURSE_TEST_GETUNFINISHEDTESTS_URL = "course/test/getUnfinishedTests";
    private static String COURSE_TEST_GETQUESTIONSINTEST_URL = "course/test/getQuestionsInTest";
    private static String COURSE_TEST_GETTESTDETAILS_URL = "course/test/getTestDetails";
    private static String SEAT_GETSEATTOKEN_URL = "seat/getSeatToken";
    private static String SEAT_GETSEATMAP_URL = "seat/getSeatMap";
    private static String SEAT_CHOOSESEAT_URL = "seat/chooseSeat";
    private static String SEAT_FREESEAT_URL = "seat/freeSeat";
    private static String SEAT_CHECKIFOPEN_URL = "seat/checkIfOpen";

    public static String getUserAvatarUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + USER_AVATAR_URL;
    }

    public static String getUserMeUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + USER_ME_URL;
    }

    public static String getCourseNtfcGetntfcsUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + COURSE_NTFC_GETNTFCS_URL;
    }

    public static String getUserLoginUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + USER_LOGIN_URL;
    }

    public static String getUserRegistercourseUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + USER_REGISTERCOURSE_URL;
    }

    public static String getUserRegisterGetclassesUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + USER_REGISTER_GETCLASSES_URL;
    }

    public static String getUserRegisterGetmajorsUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + USER_REGISTER_GETMAJORS_URL;
    }

    public static String getCourseQuestionGetallquestionsUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + COURSE_QUESTION_GETALLQUESTIONS_URL;
    }

    public static String getUserRegisterGetschoolsUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + USER_REGISTER_GETSCHOOLS_URL;
    }

    public static String getCourseTestGetfinishedtestsUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + COURSE_TEST_GETFINISHEDTESTS_URL;
    }

    public static String getCourseTestGetunfinishedtestsUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + COURSE_TEST_GETUNFINISHEDTESTS_URL;
    }

    public static String getCourseTestGetquestionsintestUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + COURSE_TEST_GETQUESTIONSINTEST_URL;
    }

    public static String getUserRegisterStudentUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + USER_REGISTER_STUDENT_URL;
    }

    public static String getCourseTestGettestdetailsUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + COURSE_TEST_GETTESTDETAILS_URL;
    }

    public static String getSeatGetseattokenUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + SEAT_GETSEATTOKEN_URL;
    }

    public static String getSeatGetseatmapUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + SEAT_GETSEATMAP_URL;
    }

    public static String getUserRegisterTeacherUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + USER_REGISTER_TEACHER_URL;
    }

    public static String getSeatCheckifopenUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + SEAT_CHECKIFOPEN_URL;
    }

    public static String getSeatChooseseatUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + SEAT_CHOOSESEAT_URL;
    }

    public static String getSeatFreeseatUrl(String URL_FIGURE) {
        return "http://" + URL_FIGURE + ":5000/" + SEAT_FREESEAT_URL;
    }

}
