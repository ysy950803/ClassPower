package com.ysy.classpower_common.constant;

/**
 * Created by 姚圣禹 on 2016/3/5.
 */
public class ErrorCodeConstant {

    public static final String UNKNOWN_INTERNAL_ERROR = "500";

    public static final String FORBIDDEN = "601"; // 无权限
    public static final String WRONG_PASSWORD = "602"; // POST to login 密码错误
    public static final String ALREADY_LOGGED_IN = "603"; // 用户已登陆,保留
    public static final String BAD_TOKEN = "604"; // POST to user/getuser token错误
    public static final String TOKEN_EXPIRED = "605"; // token过期
    public static final String ONLY_ACCEPT_JSON = "606"; // POST只接受JSON
    public static final String FIELD_MISSING = "607"; // POST 时缺少必要的参数
    public static final String WRONG_FIELD_TYPE = "609"; // field类型错误
    public static final String UNKNOWN_FIELD = "608"; // POST BODY中存在未知field
    public static final String BASE64_ERROR = "620"; // POST to avatar BASE64字符串错误

    public static final String RESOURCE_NOT_FOUND = "700";
    public static final String USER_NOT_FOUND = "701"; // POST to login 用户不存在
    public static final String MAIN_COURSE_NOT_FOUND = "702";
    public static final String SUB_COURSE_NOT_FOUND = "703";
    public static final String KNOWLEDGE_POINT_NOT_FOUND = "704";
    public static final String QUESTION_NOT_FOUND = "705";
    public static final String NOTIFICATION_NOT_FOUND = "706";
    public static final String SEAT_NOT_FOUND = "707";
    public static final String TEST_NOT_FOUND = "708";
    public static final String ROOM_NOT_FOUND = "709";
    public static final String SCHOOL_NOT_FOUND = "710";
    public static final String DEPARTMENT_NOT_FOUND = "711";
    public static final String MAJOR_NOT_FOUND = "712";
    public static final String CLASS_NOT_FOUND = "713";
    public static final String CHAPTER_NOT_FOUND = "714";
    public static final String SECTION_NOT_FOUND = "715";

    public static final String RESOURCE_ALREADY_EXISTS = "800";
    public static final String USER_ALREADY_EXISTS = "801"; // POST to register 用户已存在
    public static final String MAIN_COURSE_ALREADY_EXISTS = "802"; // 主课程已存在
    public static final String SUB_COURSE_ALREADY_EXISTS = "803"; // 讲台已存在
    public static final String KNOWLEDGE_POINT_ALREADY_EXISTS = "804";
    public static final String QUESTION_ALREADY_EXISTS = "805";
    public static final String NOTIFICATION_ALREADY_EXISTS = "806";
    public static final String SEAT_ALREADY_EXISTS = "807";
    public static final String TEST_ALREADY_EXISTS = "808";
    public static final String ROOM_ALREADY_EXISTS = "809";
    public static final String CHAPTER_ALREADY_EXISTS = "814";
    public static final String SECTION_ALREADY_EXISTS = "815";

    public static final String SEAT_ALREADY_TAKEN = "901"; // 座位已被别人选中
    public static final String SEAT_ALREADY_CHOSEN = "902"; // 座位已经选中
    public static final String SEAT_ALREADY_FREE_OR_TAKEN = "903"; // 座位已空
    public static final String SEAT_TOKEN_EXPIRED = "904";
    public static final String BAD_SEAT_TOKEN = "905";
    public static final String SEAT_CHOOSING_NOT_AVAILABLE_YET = "906"; // 还未到选座时间,需等待
    public static final String COURSE_ALREADY_BEGUN = "907"; // 课程已经开始,无法选座
    public static final String COURSE_IS_NOT_ON_TODAY = "908"; // 选择的课程不在今天
    public static final String COURSE_ALREADY_OVER = "909"; // 课程已经开始,无法选座
    public static final String COURSE_NOT_BEGUN = "910";
    public static final String YOU_ARE_TOO_LATE = "911"; // 迟到时间超过上限
    public static final String RANDOM_TEST_NOT_SET = "920";
    public static final String YOU_DO_NOT_HAVE_THIS_COURSE = "921";
    public static final String YOU_ARE_NOT_THE_TEACHER = "922";
    public static final String YOU_ARE_NOT_A_STUDENT = "923";
    public static final String TEST_EXPIRED = "930";
    public static final String TEST_HAVENT_BEGUN = "931";

}
