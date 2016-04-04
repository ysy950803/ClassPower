package com.ysy.classpower_student.activities.home;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ysy.classpower.R;
import com.ysy.classpower_common.constant.ErrorCodeConstant;
import com.ysy.classpower_common.constant.ServerUrlConstant;
import com.ysy.classpower_seatchoose.OnSeatClickListener;
import com.ysy.classpower_seatchoose.model.Seat;
import com.ysy.classpower_seatchoose.model.SeatInfo;
import com.ysy.classpower_seatchoose.view.SSThumbView;
import com.ysy.classpower_seatchoose.view.SSView;
import com.ysy.classpower_student.activities.base.StudentPersonalCenterActivity;
import com.ysy.classpower_student.activities.base.StudentWelcomeActivity;
import com.ysy.classpower_student.activities.test.TestPreviewActivity;
import com.ysy.classpower_student.adapters.StudentHomeNotificationsListAdapter;
import com.ysy.classpower_student.adapters.StudentHomeTestsListAdapter;
import com.ysy.classpower_utils.ConnectionDetector;
import com.ysy.classpower_utils.OwnApp;
import com.ysy.classpower_utils.OwnMaterialSearchView;
import com.ysy.classpower_utils.for_design.CircularImageView;
import com.ysy.classpower_utils.EmptyListWithTipsAdapter;
import com.ysy.classpower_utils.ListOnItemClickListener;
import com.ysy.classpower_utils.json_processor.PostJsonAndGetCallback;
import com.ysy.classpower_utils.json_processor.ReadJsonByGson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StudentHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static int previousI = -1, previousJ = -1;

    private long exitTime;
    private LinearLayout notificationLayout;
    private LinearLayout testLayout;
    private RelativeLayout seatChooseLayout;

    private List<String> ntfcsListByData;
    private List<String> ntfcsListContentData;
    private List<String> ntfcsListCreatedOnData;
    private List<String> ntfcsListIdData;
    private List<String> ntfcsListTitleData;
    private int ntfcsListOnTopCount;
    private boolean isNotificationsListEmpty;
    private UltimateRecyclerView notificationsURV;

    private List<String> testsListBeginsOnData;
    private List<String> testsListExpiresOnData;
    private UltimateRecyclerView testsURV;
    private RelativeLayout testsEmptyTipsLayout;
    private TextView testsEmptyListTipsTextView;

    private List<Boolean> testsListStateData;
    private List<String> testsListTestIdData;

    private TextView studentSeatTextView;
    private TextView studentNameTextView;
    private TextView studentSexTextView;
    private CircularImageView studentAvatarImageView;
    private TextView refreshTipsTextView;
    private TextView refreshLittleTipsTextView;
    private CardView refreshTipsCardView;
    private RelativeLayout seatChooseChildLayout;

    private SSView mSSView;
    private SSThumbView mSSThumbView;

    private int RowNum = 0;
    private int ColNum = 0;
    private ArrayList<SeatInfo> list_seatInfo;
    private ArrayList<ArrayList<Integer>> list_seat_conditions;

    public static boolean isSeatChooseEmpty = true;
    public static boolean isSeatChooseOpen;
    public static boolean isDirectlyCheckResult = false;
//    public static String STUDENT_TEST_STATE = null; // DOING, DONE, WILL

    private String userId = "";
    private String name = "";
    private String password = "";
    private String email = "";
    private String token = "";
    private String courseId = "";
    private String subId = "";
    private String seatMapToken = "";
    private String seatToken = "";
    private int lateSecs = 0;
    private boolean checkFinal = false;
    private String seatId = "";

    private Timer remainingSecsTimer;
    private int remainingSecs = 0;
    private Handler remainingSecsHandler;

    private RelativeLayout emptyTipsLayout;
    private TextView emptyTipsTextView;

    private OwnApp ownApp;

    private ProgressDialog waitDialog;
    private OwnMaterialSearchView searchView;

    private String[] allStudents = new String[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ownApp = (OwnApp) getApplication();

        // 核心参数优先获取
        SharedPreferences userId_sp = getSharedPreferences("userId", MODE_PRIVATE);
        SharedPreferences name_sp = getSharedPreferences("name", MODE_PRIVATE);
        SharedPreferences password_sp = getSharedPreferences("password", MODE_PRIVATE);
        SharedPreferences email_sp = getSharedPreferences("email", MODE_PRIVATE);
        SharedPreferences token_sp = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences courseId_sp = getSharedPreferences("courseId", MODE_PRIVATE);
        SharedPreferences subId_sp = getSharedPreferences("subId", MODE_PRIVATE);
        userId = userId_sp.getString("userId", "");
        name = name_sp.getString("name", "");
        password = password_sp.getString("password", "");
        email = email_sp.getString("email", "");
        token = token_sp.getString("token", "");
        courseId = courseId_sp.getString("courseId", "");
        subId = subId_sp.getString("subId", "");

        isSeatChooseOpen = false;

        // 通知、测试、选座页面
        notificationLayout = (LinearLayout) findViewById(R.id.notification_layout);
        testLayout = (LinearLayout) findViewById(R.id.test_layout);
        seatChooseLayout = (RelativeLayout) findViewById(R.id.seat_choose_layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 为抽屉里的头部Layout添加监听（注意先获取父级容器）
        View navHeaderStudentHomeView = navigationView.getHeaderView(0);
        LinearLayout navHeaderStudentHomeLayout = (LinearLayout) navHeaderStudentHomeView.findViewById(R.id.nav_header_student_home_layout);
        TextView navHeaderName = (TextView) navHeaderStudentHomeView.findViewById(R.id.student_home_nav_header_name_textView);
        TextView navHeaderEmail = (TextView) navHeaderStudentHomeView.findViewById(R.id.student_home_nav_header_email_textView);
        final CircularImageView navHeaderAvatar = (CircularImageView) navHeaderStudentHomeView.findViewById(R.id.student_home_nav_header_avatar_imageView);
        navHeaderStudentHomeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent(StudentHomeActivity.this, StudentPersonalCenterActivity.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StudentHomeActivity.this, navHeaderAvatar, "student_home_personalCenter_transition");
                    startActivity(intent, options.toBundle());
                } else
                    startActivity(new Intent(StudentHomeActivity.this, StudentPersonalCenterActivity.class));
            }
        });
        navHeaderName.setText(name);
        navHeaderEmail.setText(email);
        new AsyncHttpClient().get(ServerUrlConstant.getUserAvatarUrl(ownApp.getURL_FIGURE()) + "/" + userId + ".jpg", new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                OwnApp ownApp = (OwnApp) getApplication();
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ownApp.setBitmap(bm);
                navHeaderAvatar.setImageBitmap(bm);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                navHeaderAvatar.setImageResource(R.drawable.ic_account_circle_black_48dp);
            }

        });

        // 获取通知List模块
        initNotifications();

        // 获取测试List模块
        initTests();

        // 初始化选座
        initSeatsData();
        // 确定选座按钮
        Button confirmSeatChooseButton = (Button) findViewById(R.id.confirm_seat_choose_button);
        assert confirmSeatChooseButton != null;
        confirmSeatChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mSeatId_sp = getSharedPreferences("mSeat_id_" + subId + "_" + courseId, MODE_PRIVATE);
                String mSeatId = mSeatId_sp.getString("mSeat_id_" + subId + "_" + courseId, "");
                if (isSeatChooseEmpty) {
                    Toast.makeText(StudentHomeActivity.this, "你还没有选择任何一个座位！", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mSeatId.equals("")) {
                        Toast.makeText(StudentHomeActivity.this, "请先释放座位再重新选座哦！", Toast.LENGTH_SHORT).show();
                    } else
                        // 若已选座，则提交数据至服务器
                        handSeatConfirmInfo();
                }
            }
        });
        // 重新选座按钮
        Button clearSeatChooseButton = (Button) findViewById(R.id.clear_seat_choose_button);
        assert clearSeatChooseButton != null;
        clearSeatChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mSeatId_sp = getSharedPreferences("mSeat_id_" + subId + "_" + courseId, MODE_PRIVATE);
                String mSeatId = mSeatId_sp.getString("mSeat_id_" + subId + "_" + courseId, "");
                if (mSeatId.equals("")) {
                    Toast.makeText(StudentHomeActivity.this, "你还没选到座位哦，不能释放座位！", Toast.LENGTH_SHORT).show();
                } else
                    clearSeatChooseTips(mSeatId);
            }
        });

        searchView = (OwnMaterialSearchView) findViewById(R.id.search_view);
        searchView.setCursorDrawable(R.drawable.color_cursor_white);

    }

    // 初始化通知List模块
    private void initNotifications() {
        initNotificationsData(0, null);
        LinearLayoutManager notificationsLM = new LinearLayoutManager(this);
        notificationsURV = (UltimateRecyclerView) findViewById(R.id.student_home_notifications_list_urv);
        notificationsURV.setLayoutManager(notificationsLM);
        emptyTipsLayout = (RelativeLayout) findViewById(R.id.empty_tips_layout);
        emptyTipsTextView = (TextView) findViewById(R.id.empty_list_tips_text_view);
        if (isNotificationsListEmpty) {
            emptyTipsLayout.setVisibility(View.VISIBLE);
            emptyTipsTextView.setText("没有新的通知哦！");
            List<String> emptyListData = new ArrayList<>();
            emptyListData.add(0, "");
            EmptyListWithTipsAdapter emptyListAdapter = new EmptyListWithTipsAdapter(emptyListData);
            notificationsURV.setAdapter(emptyListAdapter);
        } else {
            emptyTipsLayout.setVisibility(View.GONE);
            emptyTipsTextView.setText("加载中…");
            StudentHomeNotificationsListAdapter notificationsListAdapter = new StudentHomeNotificationsListAdapter(ntfcsListByData, ntfcsListContentData,
                    ntfcsListCreatedOnData, ntfcsListTitleData, ntfcsListOnTopCount);
            notificationsURV.setAdapter(notificationsListAdapter);
            notificationsListAdapter.setListOnItemClickListener(new ListOnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
        }
        notificationsURV.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNotifications();
            }
        });

    }

    private void refreshNotifications() {
        notificationsURV.setRefreshing(true);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", token);
            jsonObject.put("course_id", courseId);
            jsonObject.put("sub_id", subId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getCourseNtfcGetntfcsUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (s.contains("error_code")) {
                    ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                    if (jsonByGson.getValue("error_code").equals(ErrorCodeConstant.TOKEN_EXPIRED)) { // token过期，闭环处理
                        updateToken(0);
                    }
                } else {
                    Toast.makeText(StudentHomeActivity.this, "网络错误，刷新失败，请重试！", Toast.LENGTH_SHORT).show();
                    notificationsURV.setRefreshing(false);
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                initNotificationsData(1, s);
                if (isNotificationsListEmpty) {
                    emptyTipsLayout.setVisibility(View.VISIBLE);
                    emptyTipsTextView.setText("没有新的通知哦！");
                    List<String> emptyListData = new ArrayList<>();
                    emptyListData.add(0, "");
                    EmptyListWithTipsAdapter emptyListAdapter = new EmptyListWithTipsAdapter(emptyListData);
                    notificationsURV.setAdapter(emptyListAdapter);
                } else {
                    emptyTipsLayout.setVisibility(View.GONE);
                    emptyTipsTextView.setText("加载中…");
                    StudentHomeNotificationsListAdapter notificationsListAdapter = new StudentHomeNotificationsListAdapter(ntfcsListByData, ntfcsListContentData,
                            ntfcsListCreatedOnData, ntfcsListTitleData, ntfcsListOnTopCount);
                    notificationsURV.setAdapter(notificationsListAdapter);
                    notificationsListAdapter.setListOnItemClickListener(new ListOnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                        }

                        @Override
                        public void onItemLongClick(View view, int position) {

                        }
                    });
                }
                notificationsURV.setRefreshing(false);
                Toast.makeText(StudentHomeActivity.this, "课程通知刷新成功！", Toast.LENGTH_SHORT).show();
            }

        });
    }

    // 获取通知List模块数据
    private void initNotificationsData(int switch_num, String json) {
        if (switch_num == 0 && json == null) {
            SharedPreferences notificationsList_sp = getSharedPreferences("notificationsList", MODE_PRIVATE);
            String list_json = notificationsList_sp.getString("notifications_list", "{}");
            list_json = "{\n" +
                    "  \"msg\": \"Success\",\n" +
                    "    \"notifications\": [\n" +
                    "    {\n" +
                    "      \"by\": \"test\",\n" +
                    "      \"content\": \"test_notification_modified\",\n" +
                    "      \"created_on\": \"2016-02-22 17:38:59\",\n" +
                    "      \"ntfc_id\": \"56cad733b902a4154cec1ff6\",\n" +
                    "      \"on_top\": true,\n" +
                    "      \"title\": \"adsgfgs\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"by\": \"test\",\n" +
                    "      \"content\": \"test_notification_modified\",\n" +
                    "      \"created_on\": \"2016-02-22 17:39:02\",\n" +
                    "      \"ntfc_id\": \"56cad736b902a4154cec2001\",\n" +
                    "      \"on_top\": false,\n" +
                    "      \"title\": \"adsgfgs\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"by\": \"test\",\n" +
                    "      \"content\": \"test_notification_modified\",\n" +
                    "      \"created_on\": \"2016-02-22 17:38:54\",\n" +
                    "      \"ntfc_id\": \"56cad72eb902a4154cec1fed\",\n" +
                    "      \"on_top\": false,\n" +
                    "      \"title\": \"adsgfgs\"\n" +
                    "    },\n" +
                    "\t{\n" +
                    "      \"by\": \"test\",\n" +
                    "      \"content\": \"test_notification_modified\",\n" +
                    "      \"created_on\": \"2016-02-22 17:38:54\",\n" +
                    "      \"ntfc_id\": \"56cad72eb902a4154cec1fed\",\n" +
                    "      \"on_top\": false,\n" +
                    "      \"title\": \"adsgfgs\"\n" +
                    "    },\n" +
                    "\t{\n" +
                    "      \"by\": \"test\",\n" +
                    "      \"content\": \"test_notification_modified\",\n" +
                    "      \"created_on\": \"2016-02-22 17:38:54\",\n" +
                    "      \"ntfc_id\": \"56cad72eb902a4154cec1fed\",\n" +
                    "      \"on_top\": false,\n" +
                    "      \"title\": \"adsgfgs\"\n" +
                    "    },\n" +
                    "\t{\n" +
                    "      \"by\": \"test\",\n" +
                    "      \"content\": \"test_notification_modified\",\n" +
                    "      \"created_on\": \"2016-02-22 17:38:54\",\n" +
                    "      \"ntfc_id\": \"56cad72eb902a4154cec1fed\",\n" +
                    "      \"on_top\": false,\n" +
                    "      \"title\": \"adsgfgs\"\n" +
                    "    },\n" +
                    "\t{\n" +
                    "      \"by\": \"test\",\n" +
                    "      \"content\": \"test_notification_modified\",\n" +
                    "      \"created_on\": \"2016-02-22 17:38:54\",\n" +
                    "      \"ntfc_id\": \"56cad72eb902a4154cec1fed\",\n" +
                    "      \"on_top\": false,\n" +
                    "      \"title\": \"adsgfgs\"\n" +
                    "    },\n" +
                    "\t{\n" +
                    "      \"by\": \"test\",\n" +
                    "      \"content\": \"test_notification_modified\",\n" +
                    "      \"created_on\": \"2016-02-22 17:38:54\",\n" +
                    "      \"ntfc_id\": \"56cad72eb902a4154cec1fed\",\n" +
                    "      \"on_top\": false,\n" +
                    "      \"title\": \"adsgfgs\"\n" +
                    "    },\n" +
                    "\t{\n" +
                    "      \"by\": \"test\",\n" +
                    "      \"content\": \"test_notification_modified\",\n" +
                    "      \"created_on\": \"2016-02-22 17:38:54\",\n" +
                    "      \"ntfc_id\": \"56cad72eb902a4154cec1fed\",\n" +
                    "      \"on_top\": false,\n" +
                    "      \"title\": \"adsgfgs\"\n" +
                    "    },\n" +
                    "\t{\n" +
                    "      \"by\": \"test\",\n" +
                    "      \"content\": \"test_notification_modified\",\n" +
                    "      \"created_on\": \"2016-02-22 17:38:54\",\n" +
                    "      \"ntfc_id\": \"56cad72eb902a4154cec1fed\",\n" +
                    "      \"on_top\": false,\n" +
                    "      \"title\": \"adsgfgs\"\n" +
                    "    },\n" +
                    "\t{\n" +
                    "      \"by\": \"test\",\n" +
                    "      \"content\": \"test_notification_modified\",\n" +
                    "      \"created_on\": \"2016-02-22 17:38:54\",\n" +
                    "      \"ntfc_id\": \"56cad72eb902a4154cec1fed\",\n" +
                    "      \"on_top\": false,\n" +
                    "      \"title\": \"adsgfgs\"\n" +
                    "    },\n" +
                    "\t{\n" +
                    "      \"by\": \"test\",\n" +
                    "      \"content\": \"test_notification_modified\",\n" +
                    "      \"created_on\": \"2016-02-22 17:38:54\",\n" +
                    "      \"ntfc_id\": \"56cad72eb902a4154cec1fed\",\n" +
                    "      \"on_top\": false,\n" +
                    "      \"title\": \"adsgfgs\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"by\": \"test\",\n" +
                    "      \"content\": \"test_notification_modified\",\n" +
                    "      \"created_on\": \"2016-02-22 17:38:51\",\n" +
                    "      \"ntfc_id\": \"56cad72bb902a4154cec1fe6\",\n" +
                    "      \"on_top\": false,\n" +
                    "      \"title\": \"adsgfgs\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            if (list_json.equals("{}") || list_json.equals("[]")) {
                isNotificationsListEmpty = true;
            } else {
                isNotificationsListEmpty = false;
                ReadJsonByGson jsonByGson = new ReadJsonByGson(list_json);
                ntfcsListByData = new ArrayList<>();
                ntfcsListContentData = new ArrayList<>();
                ntfcsListCreatedOnData = new ArrayList<>();
                ntfcsListIdData = new ArrayList<>();
                ntfcsListTitleData = new ArrayList<>();
                ntfcsListOnTopCount = 0;
                String[] ntfcListByInfo = jsonByGson.getNotificationsInfo("by");
                String[] ntfcListContentInfo = jsonByGson.getNotificationsInfo("content");
                String[] ntfcListCreatedOnInfo = jsonByGson.getNotificationsInfo("created_on");
                String[] ntfcListIdInfo = jsonByGson.getNotificationsInfo("ntfc_id");
                String[] ntfcListTitleInfo = jsonByGson.getNotificationsInfo("title");
                if (jsonByGson.getNotificationsInfo("on_top_count")[0].equals("on_top_count")) {
                    ntfcsListOnTopCount = Integer.valueOf(jsonByGson.getNotificationsInfo("on_top_count")[1]);
                }
                Collections.addAll(ntfcsListByData, ntfcListByInfo);
                Collections.addAll(ntfcsListContentData, ntfcListContentInfo);
                Collections.addAll(ntfcsListCreatedOnData, ntfcListCreatedOnInfo);
                Collections.addAll(ntfcsListIdData, ntfcListIdInfo);
                Collections.addAll(ntfcsListTitleData, ntfcListTitleInfo);
            }
        } else if (switch_num == 1 && json != null && !json.contains("[]")) {
            isNotificationsListEmpty = false;
            ReadJsonByGson jsonByGson = new ReadJsonByGson(json);
            ntfcsListByData = new ArrayList<>();
            ntfcsListContentData = new ArrayList<>();
            ntfcsListCreatedOnData = new ArrayList<>();
            ntfcsListIdData = new ArrayList<>();
            ntfcsListTitleData = new ArrayList<>();
            ntfcsListOnTopCount = 0;
            String[] ntfcListByInfo = jsonByGson.getNotificationsInfo("by");
            String[] ntfcListContentInfo = jsonByGson.getNotificationsInfo("content");
            String[] ntfcListCreatedOnInfo = jsonByGson.getNotificationsInfo("created_on");
            String[] ntfcListIdInfo = jsonByGson.getNotificationsInfo("ntfc_id");
            String[] ntfcListTitleInfo = jsonByGson.getNotificationsInfo("title");
            if (jsonByGson.getNotificationsInfo("on_top_count")[0].equals("on_top_count")) {
                ntfcsListOnTopCount = Integer.valueOf(jsonByGson.getNotificationsInfo("on_top_count")[1]);
            }
            Collections.addAll(ntfcsListByData, ntfcListByInfo);
            Collections.addAll(ntfcsListContentData, ntfcListContentInfo);
            Collections.addAll(ntfcsListCreatedOnData, ntfcListCreatedOnInfo);
            Collections.addAll(ntfcsListIdData, ntfcListIdInfo);
            Collections.addAll(ntfcsListTitleData, ntfcListTitleInfo);
        } else if (switch_num == 1 && json != null && json.contains("[]")) {
            isNotificationsListEmpty = true;
        }

    }

    // 初始化测试List模块
    private void initTests() {
        LinearLayoutManager testsLM = new LinearLayoutManager(this);
        testsURV = (UltimateRecyclerView) findViewById(R.id.student_home_tests_list_urv);
        testsURV.setLayoutManager(testsLM);
        testsEmptyTipsLayout = (RelativeLayout) findViewById(R.id.tests_empty_tips_layout);
        testsEmptyListTipsTextView = (TextView) findViewById(R.id.tests_empty_list_tips_text_view);
        testsURV.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTests(0);
            }
        });
    }

    private void refreshTests(final int finished_page) {
        testsURV.setRefreshing(true);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", token);
            jsonObject.put("course_id", courseId);
            jsonObject.put("sub_id", subId);
            jsonObject.put("role", 2);
//            jsonObject.put("page", page);
//            jsonObject.put("finished", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getCourseTestGetunfinishedtestsUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (s.contains("error_code")) {
                    ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                    if (jsonByGson.getValue("error_code").equals(ErrorCodeConstant.TOKEN_EXPIRED)) { // token过期，闭环处理
                        updateToken(1);
                    }
                } else {
                    Toast.makeText(StudentHomeActivity.this, "网络错误，刷新失败，请重试！", Toast.LENGTH_SHORT).show();
                    testsURV.setRefreshing(false);
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                if (s.contains("\"tests\": []")) { // unfinished 空
                    JSONObject jsonObject2 = new JSONObject();
                    try {
                        jsonObject2.put("token", token);
                        jsonObject2.put("course_id", courseId);
                        jsonObject2.put("sub_id", subId);
//                        jsonObject2.put("role", 2);
                        jsonObject2.put("page", finished_page);
                        jsonObject2.put("descending", true); // 是否置顶
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String json2 = jsonObject2.toString();
                    new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getCourseTestGetfinishedtestsUrl(ownApp.getURL_FIGURE()), json2, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                            if (s.contains("error_code")) {
                                ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                                if (jsonByGson.getValue("error_code").equals(ErrorCodeConstant.TOKEN_EXPIRED)) { // token过期，闭环处理
                                    updateToken(1);
                                }
                            } else {
                                Toast.makeText(StudentHomeActivity.this, "网络错误，刷新失败，请重试！", Toast.LENGTH_SHORT).show();
                                testsURV.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            if (s.contains("\"tests\": []")) { // unfinished 空 finished 空
                                testsEmptyTipsLayout.setVisibility(View.VISIBLE);
                                testsEmptyListTipsTextView.setText("没有任何测试哦！");
                                List<String> emptyListData = new ArrayList<>();
                                emptyListData.add(0, "");
                                EmptyListWithTipsAdapter emptyListAdapter = new EmptyListWithTipsAdapter(emptyListData);
                                testsURV.setAdapter(emptyListAdapter);
                            } else { // unfinished 空 finished 非空
                                ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                                testsListBeginsOnData = new ArrayList<>();
                                testsListExpiresOnData = new ArrayList<>();
                                testsListStateData = new ArrayList<>();
                                testsListTestIdData = new ArrayList<>();
                                String[] testsListBeginsOnInfo = jsonByGson.getAllTestsInfo("begins_on");
                                String[] testsListExpiresOnInfo = jsonByGson.getAllTestsInfo("expires_on");
                                final String[] testsListTestIdInfo = jsonByGson.getAllTestsInfo("test_id");
                                for (int j = 0; j < testsListBeginsOnInfo.length; ++j)
                                    testsListStateData.add(j, true);
                                Collections.addAll(testsListBeginsOnData, testsListBeginsOnInfo);
                                Collections.addAll(testsListExpiresOnData, testsListExpiresOnInfo);
                                Collections.addAll(testsListTestIdData, testsListTestIdInfo);
                                testsEmptyTipsLayout.setVisibility(View.GONE);
                                testsEmptyListTipsTextView.setText("加载中…");
                                StudentHomeTestsListAdapter testsListAdapter = new StudentHomeTestsListAdapter(testsListBeginsOnData, testsListExpiresOnData);
                                testsURV.setAdapter(testsListAdapter);
                                testsListAdapter.setListOnItemClickListener(new ListOnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        JSONObject obj = new JSONObject();
                                        try {
                                            obj.put("begins_on", testsListBeginsOnData.get(position));
                                            obj.put("expires_on", testsListExpiresOnData.get(position));
                                            obj.put("test_id", testsListTestIdData.get(position));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        testListItemOnClick(obj.toString(), testsListStateData.get(position));
                                    }

                                    @Override
                                    public void onItemLongClick(View view, int position) {

                                    }
                                });

                            }
                            testsURV.setRefreshing(false);
                            Toast.makeText(StudentHomeActivity.this, "课程测试刷新成功！", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else { // unfinished 非空
                    final ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                    testsListBeginsOnData = new ArrayList<>();
                    testsListExpiresOnData = new ArrayList<>();
                    testsListStateData = new ArrayList<>();
                    testsListTestIdData = new ArrayList<>();
                    final String[] testsListBeginsOnInfo = jsonByGson.getAllTestsInfo("begins_on");
                    final String[] testsListExpiresOnInfo = jsonByGson.getAllTestsInfo("expires_on");
                    final String[] testsListTestIdInfo = jsonByGson.getAllTestsInfo("test_id");
                    Collections.addAll(testsListBeginsOnData, testsListBeginsOnInfo);
                    Collections.addAll(testsListExpiresOnData, testsListExpiresOnInfo);
                    Collections.addAll(testsListTestIdData, testsListTestIdInfo);
                    for (int j = 0; j < testsListBeginsOnInfo.length; ++j)
                        testsListStateData.add(j, false);

                    testsEmptyTipsLayout.setVisibility(View.GONE);
                    testsEmptyListTipsTextView.setText("加载中…");
                    StudentHomeTestsListAdapter testsListAdapter = new StudentHomeTestsListAdapter(testsListBeginsOnData, testsListExpiresOnData);
                    testsURV.setAdapter(testsListAdapter);
                    testsListAdapter.setListOnItemClickListener(new ListOnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put("begins_on", testsListBeginsOnData.get(position));
                                obj.put("expires_on", testsListExpiresOnData.get(position));
                                obj.put("test_id", testsListTestIdData.get(position));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            testListItemOnClick(obj.toString(), testsListStateData.get(position));
                        }

                        @Override
                        public void onItemLongClick(View view, int position) {

                        }
                    });

                    JSONObject jsonObject2 = new JSONObject();
                    try {
                        jsonObject2.put("token", token);
                        jsonObject2.put("course_id", courseId);
                        jsonObject2.put("sub_id", subId);
//                        jsonObject2.put("role", 2);
                        jsonObject2.put("page", finished_page);
                        jsonObject2.put("descending", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String json2 = jsonObject2.toString();
                    new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getCourseTestGetfinishedtestsUrl(ownApp.getURL_FIGURE()), json2, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                            if (s.contains("error_code")) {
                                ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                                if (jsonByGson.getValue("error_code").equals(ErrorCodeConstant.TOKEN_EXPIRED)) { // token过期，闭环处理
                                    updateToken(1);
                                }
                            } else {
                                Toast.makeText(StudentHomeActivity.this, "网络中断，只获得部分测试，请再刷新试试！", Toast.LENGTH_SHORT).show();
                                testsURV.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            if (!s.contains("\"tests\": []")) { // unfinished 非空 finished 非空
                                ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                                final String[] testsListBeginsOnInfo2 = jsonByGson.getAllTestsInfo("begins_on");
                                final String[] testsListExpiresOnInfo2 = jsonByGson.getAllTestsInfo("expires_on");
                                String[] testsListTestIdInfo2 = jsonByGson.getAllTestsInfo("test_id");
                                for (int j = testsListBeginsOnInfo.length; j < (testsListBeginsOnInfo2.length + testsListBeginsOnInfo.length); ++j) {
                                    testsListBeginsOnData.add(j, testsListBeginsOnInfo2[j - testsListBeginsOnInfo.length]);
                                    testsListExpiresOnData.add(j, testsListExpiresOnInfo2[j - testsListBeginsOnInfo.length]);
                                    testsListTestIdData.add(j, testsListTestIdInfo2[j - testsListBeginsOnInfo.length]);
                                    testsListStateData.add(j, true);
                                }

                                StudentHomeTestsListAdapter testsListAdapter = new StudentHomeTestsListAdapter(testsListBeginsOnData, testsListExpiresOnData);
                                testsURV.setAdapter(testsListAdapter);
                                testsListAdapter.setListOnItemClickListener(new ListOnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        JSONObject obj = new JSONObject();
                                        try {
                                            obj.put("begins_on", testsListBeginsOnData.get(position));
                                            obj.put("expires_on", testsListExpiresOnData.get(position));
                                            obj.put("test_id", testsListTestIdData.get(position));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        testListItemOnClick(obj.toString(), testsListStateData.get(position));
                                    }

                                    @Override
                                    public void onItemLongClick(View view, int position) {

                                    }
                                });
                            } else { // unfinished 非空 finished 空

                            }
                            testsURV.setRefreshing(false);
                            Toast.makeText(StudentHomeActivity.this, "课程测试刷新成功！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

    // 初始化测试List模块数据
    private void initTestsData() {
        refreshTests(0);
    }

    private void testListItemOnClick(String test_preview_info, final boolean isFinished) {
//        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
//        if (cd.isConnectingToInternet()) {
//            JSONObject object = new JSONObject();
//            try {
//                object.put("token", token);
//                object.put("course_id", courseId);
//                object.put("sub_id", subId);
//                object.put("test_id", testId);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            String json = object.toString();
//            new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getCourseTestGettestdetailsUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
//                @Override
//                public void onStart() {
//                    waitDialog = ProgressDialog.show(StudentHomeActivity.this, "正在连接测试中心", "请稍等…");
//                    super.onStart();
//                }
//
//                @Override
//                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//                    waitDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "网络错误，请重试！", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onSuccess(int i, Header[] headers, String s) {
//                    waitDialog.dismiss();
//                    OwnApp ownApp = (OwnApp) getApplication();
//                    ownApp.setTestPreviewInfo(s);
//                    ownApp.setTestIsFinished(isFinished);
//                    startActivity(new Intent(StudentHomeActivity.this, TestPreviewActivity.class));
//                }
//            });
//        } else
//            Toast.makeText(getApplicationContext(), "断开连接，请检查网络！", Toast.LENGTH_SHORT).show();
        ownApp.setTestPreviewInfo(test_preview_info);
        ownApp.setTestIsFinished(isFinished);
        if (test_preview_info != null)
            startActivity(new Intent(StudentHomeActivity.this, TestPreviewActivity.class));
        else
            Toast.makeText(StudentHomeActivity.this, "未知错误，请重试！", Toast.LENGTH_SHORT).show();
    }

//    // 自定义接口，然后在onBindViewHolder中去为holder.itemView去设置相应的监听最后回调设置的监听
//    public interface OnItemClickListener {
//        void onItemClick(View view, int position);
//
//        void onItemLongClick(View view, int position);
//    }
//
//    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
//        @Override
//        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return new MyViewHolder(LayoutInflater.from(StudentHomeActivity.this).inflate(R.layout.test_item_student_home, parent,
//                    false));
//        }
//
//        private OnItemClickListener mOnItemClickListener;
//
//        public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
//            this.mOnItemClickListener = mOnItemClickListener;
//        }
//
//        @Override
//        public void onBindViewHolder(final MyViewHolder holder, final int position) {
//            holder.tv.setText(testsListData.get(position));
//            if (position == 0)
//                holder.tv.setBackgroundResource(R.drawable.item_doing_press);
//            else if (position == 1)
//                holder.tv.setBackgroundResource(R.drawable.item_will_press);
//            else
//                holder.tv.setBackgroundResource(R.drawable.item_done_press);
//
//            // 如果设置了回调，则设置点击事件
//            if (mOnItemClickListener != null) {
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int pos = holder.getLayoutPosition();
//                        mOnItemClickListener.onItemClick(holder.itemView, pos);
//                    }
//                });
//
//                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        int pos = holder.getLayoutPosition();
//                        mOnItemClickListener.onItemLongClick(holder.itemView, pos);
//                        return false;
//                    }
//                });
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return testsListData.size();
//        }
//
//        class MyViewHolder extends RecyclerView.ViewHolder {
//            TextView tv;
//
//            public MyViewHolder(View view) {
//                super(view);
//                tv = (TextView) view.findViewById(R.id.id_num);
//            }
//        }
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
//            super.onBackPressed();
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "请再按一次以退出！", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                isSeatChooseOpen = false;
                System.exit(0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.student_home, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search_student); //在菜单中找到对应控件的item
        searchView.setMenuItem(menuItem);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
//        searchView.setQueryHint("找同学"); //设置搜索框内的hint文字
//        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() { //设置打开关闭动作监听
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                Toast.makeText(StudentHomeActivity.this, "找找同学啦！", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                return true;
//            }
//        });
        if (isSeatChooseOpen) { //根据选座界面是否打开来设置search按钮的隐藏与否
            menuItem.setVisible(true);
        } else
            menuItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            this.finish();
            System.exit(0);
//            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notification) {
            // Handle the camera action
            isSeatChooseOpen = false;
            invalidateOptionsMenu();
            notificationLayout.setVisibility(View.VISIBLE);
            testLayout.setVisibility(View.GONE);
            seatChooseLayout.setVisibility(View.GONE);

        } else if (id == R.id.nav_test) {
            isSeatChooseOpen = false;
            invalidateOptionsMenu();
            notificationLayout.setVisibility(View.GONE);
            testLayout.setVisibility(View.VISIBLE);
            seatChooseLayout.setVisibility(View.GONE);
            initTestsData(); // 减轻onCreate主线程压力

        } else if (id == R.id.nav_choose_seat) {
            refreshSeats(true); // 在用户点击选座界面时才开始加载座位数据，以减轻onCreate主线程压力
            isSeatChooseOpen = true;
            invalidateOptionsMenu(); // 更新Toolbar上的控件排布
            notificationLayout.setVisibility(View.GONE);
            testLayout.setVisibility(View.GONE);
            seatChooseLayout.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            isSeatChooseOpen = false;
            invalidateOptionsMenu();
            startActivity(new Intent(StudentHomeActivity.this, StudentWelcomeActivity.class));
            this.finish();
        }
//        else if (id == R.id.nav_exit) {
//            isSeatChooseOpen = false;
//            invalidateOptionsMenu();
//            this.finish();
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 加载选座数据模块
    private void initSeatsData() {
        CardView personalInfoCardView = (CardView) findViewById(R.id.student_personal_info_cardView);
        personalInfoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        seatChooseChildLayout = (RelativeLayout) findViewById(R.id.seat_choose_child_layout);
        refreshTipsTextView = (TextView) findViewById(R.id.student_home_refresh_tips_textView);
        refreshTipsCardView = (CardView) findViewById(R.id.student_home_refresh_tips_cardView);
        refreshTipsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        refreshLittleTipsTextView = (TextView) findViewById(R.id.student_home_refresh_little_tips_textView);
        // 点击座位出现的学生信息
        studentSeatTextView = (TextView) findViewById(R.id.student_seat_text_view);
        studentNameTextView = (TextView) findViewById(R.id.student_name_text_view);
        studentSexTextView = (TextView) findViewById(R.id.student_sex_text_view);
        studentAvatarImageView = (CircularImageView) findViewById(R.id.student_avatar_imageView);

        mSSView = (SSView) this.findViewById(R.id.mSSView);
        mSSThumbView = (SSThumbView) this.findViewById(R.id.ss_ssthumview);
        //显示缩略图
        SSView.a(mSSView, true);
        mSSView.invalidate();
//		mSSView.setXOffset(10);
//        mSSView.init(Col, Row, new ArrayList<SeatInfo>(), new ArrayList<ArrayList<Integer>>(), mSSThumbView, 5);
//        mSSView.setOnSeatClickListener(new OnSeatClickListener() {
//            @Override
//            public boolean b(int column_num, int row_num, boolean paramBoolean) {
//
//                return false;
//            }
//
//            @Override
//            public boolean a(int column_num, int row_num, boolean paramBoolean) {
//
//                return false;
//            }
//
//            @Override
//            public void a() {
//                // TODO Auto-generated method stub
//
//            }
//        });
        mSSView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        mSSThumbView.setVisibility(View.VISIBLE);
                        studentAvatarImageView.setVisibility(View.INVISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        mSSThumbView.setVisibility(View.INVISIBLE);
                        studentAvatarImageView.setVisibility(View.VISIBLE);
                        break;
                }
                return false; //此处必须返回false，否则View无法移动
            }
        });
    }

    private void refreshSeats(final boolean isSetSeatsNap) {
        isSeatChooseEmpty = true;
        studentSeatTextView.setText("");
        studentNameTextView.setText("");
        studentSexTextView.setText("");
        studentAvatarImageView.setImageResource(R.drawable.ic_account_circle_black_48dp);
        previousI = -1;
        previousJ = -1;
        list_seatInfo = new ArrayList<>();
        list_seat_conditions = new ArrayList<>();
        JSONObject object = new JSONObject();
        try {
            object.put("token", token);
            object.put("course_id", courseId);
            object.put("sub_id", subId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = object.toString();
        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getSeatGetseattokenUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                waitDialog = ProgressDialog.show(StudentHomeActivity.this, "正在加载座位", "请稍等…");
                super.onStart();
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                waitDialog.dismiss();
                if (i == 0) {
                    Toast.makeText(StudentHomeActivity.this, "服务器未响应，请稍后重试！", Toast.LENGTH_SHORT).show();
                } else if (s.contains("error_code")) {
                    ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                    if (jsonByGson.getValue("error_code").equals(ErrorCodeConstant.SEAT_CHOOSING_NOT_AVAILABLE_YET)) { // 还没上课
                        if (remainingSecsTimer != null)
                            remainingSecsTimer.cancel();
                        seatChooseChildLayout.setVisibility(View.GONE);
                        refreshTipsCardView.setVisibility(View.VISIBLE);
                        refreshLittleTipsTextView.setText("请耐心等待…");
                        remainingSecs = Integer.valueOf(jsonByGson.getValue("remaining_secs"));
                        startRemainingSecsTimer();
                        remainingSecsHandler = new Handler() {
                            public void handleMessage(Message msg) {
                                refreshTipsTextView.setText("还有 " + msg.arg1 + " 秒才上课呢！");
                                startRemainingSecsTimer();
                            }
                        };
                    } else if (jsonByGson.getValue("error_code").equals(ErrorCodeConstant.COURSE_ALREADY_OVER)) { // 已经下课
                        if (remainingSecsTimer != null)
                            remainingSecsTimer.cancel();
                        seatChooseChildLayout.setVisibility(View.GONE);
                        refreshTipsCardView.setVisibility(View.VISIBLE);
                        refreshTipsTextView.setText("已经下课啦，休息一下吧！");
                        refreshLittleTipsTextView.setText("返回课程列表可进入下一堂课");
                    } else if (jsonByGson.getValue("error_code").equals(ErrorCodeConstant.COURSE_IS_NOT_ON_TODAY)) { // 不是今天上课
                        if (remainingSecsTimer != null)
                            remainingSecsTimer.cancel();
                        seatChooseChildLayout.setVisibility(View.GONE);
                        refreshTipsCardView.setVisibility(View.VISIBLE);
                        refreshTipsTextView.setText("不是今天上这堂课哦！");
                        refreshLittleTipsTextView.setText("返回课程列表可选择其它课");
                    } else if (jsonByGson.getValue("error_code").equals(ErrorCodeConstant.TOKEN_EXPIRED)) { // token过期，闭环处理
                        updateToken(2);
                    }
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                waitDialog.dismiss();
                ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                seatMapToken = jsonByGson.getValue("seat_map_token");
                seatToken = jsonByGson.getValue("seat_token");
                if (!isSetSeatsNap) {
                    handSeatConfirmInfo(); // refreshSeats(3)/onSuccess闭环衔接
                } else {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("token", token); // 此处的token来自上一层闭环处理获取到的新token，瞬时处理，永不存在过期
                        object.put("seat_map_token", seatMapToken);
                        object.put("check_final", checkFinal);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String json = object.toString();
                    new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getSeatGetseatmapUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            waitDialog = ProgressDialog.show(StudentHomeActivity.this, "正在加载座位", "请稍等…");
                            super.onStart();
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                            waitDialog.dismiss();
                            Toast.makeText(StudentHomeActivity.this, "获取座位图失败，请稍后刷新重试！", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            waitDialog.dismiss();
                            ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                            refreshTipsCardView.setVisibility(View.GONE);
                            seatChooseChildLayout.setVisibility(View.VISIBLE);
                            // 闭环成功节点，开始绘制座位图
                            final int COL = jsonByGson.getIntValue("col_num");
                            final int ROW = jsonByGson.getIntValue("row_num");
                            setSeatInfo(COL, ROW, jsonByGson);
                            final String seat_id[][] = jsonByGson.getSeatInfo(COL, ROW, "seat_id");
                            final String seat_cur_stu[][] = jsonByGson.getSeatInfo(COL, ROW, "cur_stu");
                            mSSView.init(COL, ROW, list_seatInfo, list_seat_conditions, mSSThumbView, 5);
                            mSSView.setOnSeatClickListener(new OnSeatClickListener() {
                                @Override
                                public boolean b(int column_num, int row_num, boolean paramBoolean) {
                                    seatId = seat_id[column_num][row_num];
                                    studentSeatTextView.setText(seatId);
                                    if (!seat_cur_stu[column_num][row_num].equals("")) {
                                        JSONObject obj = new JSONObject();
                                        try {
                                            obj.put("course_id", courseId);
                                            obj.put("sub_id", subId);
                                            obj.put("token", token);
                                            obj.put("student_id", seat_cur_stu[column_num][row_num]);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        new AsyncHttpClient().get(ServerUrlConstant.getUserAvatarUrl(ownApp.getURL_FIGURE()) + "/" + seat_cur_stu[column_num][row_num] + ".jpg", new BinaryHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                studentAvatarImageView.setImageBitmap(bm);
                                            }

                                            @Override
                                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                                studentAvatarImageView.setImageResource(R.drawable.ic_account_circle_black_48dp);
                                            }
                                        });
                                        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getCourseGetstudentinfoUrl(ownApp.getURL_FIGURE()), obj.toString(), new TextHttpResponseHandler() {
                                            @Override
                                            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                                                studentNameTextView.setText("");
                                                studentSexTextView.setText("");
                                            }

                                            @Override
                                            public void onSuccess(int i, Header[] headers, String s) {
                                                ReadJsonByGson read = new ReadJsonByGson(s);
                                                studentNameTextView.setText(read.getArrayValue("student", "name"));
                                                if (read.getArrayBoolValue("student", "gender"))
                                                    studentSexTextView.setText("男");
                                                else
                                                    studentSexTextView.setText("女");
                                            }
                                        });
                                    } else {
                                        studentAvatarImageView.setImageResource(R.drawable.ic_account_circle_black_48dp);
                                    }
                                    return false;
                                }

                                @Override
                                public boolean a(int column_num, int row_num, boolean paramBoolean) {
                                    studentNameTextView.setText("");
                                    studentSexTextView.setText("");
                                    studentSeatTextView.setText("");
                                    studentAvatarImageView.setImageResource(R.drawable.ic_account_circle_black_48dp);
                                    return false;
                                }

                                @Override
                                public void a() {
                                    // TODO Auto-generated method stub

                                }
                            });

                            searchView.setOnQueryTextListener(new OwnMaterialSearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    //Do some magic
                                    for (int i = 0; i < COL; ++i) {
                                        for (int j = 0; j < ROW; ++j) {
                                            if (seat_cur_stu[i][j].equals(query))
                                                studentSeatTextView.setText(seat_id[i][j]);
                                        }
                                    }
                                    JSONObject obj = new JSONObject();
                                    try {
                                        obj.put("course_id", courseId);
                                        obj.put("sub_id", subId);
                                        obj.put("token", token);
                                        obj.put("student_id", query);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    new AsyncHttpClient().get(ServerUrlConstant.getUserAvatarUrl(ownApp.getURL_FIGURE()) + "/" + query + ".jpg", new BinaryHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            studentAvatarImageView.setImageBitmap(bm);
                                        }

                                        @Override
                                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                            studentAvatarImageView.setImageResource(R.drawable.ic_account_circle_black_48dp);
                                        }
                                    });
                                    new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getCourseGetstudentinfoUrl(ownApp.getURL_FIGURE()), obj.toString(), new TextHttpResponseHandler() {
                                        @Override
                                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                                            Toast.makeText(StudentHomeActivity.this, "网络错误，获取信息失败，请重试！", Toast.LENGTH_SHORT).show();
                                            studentNameTextView.setText("");
                                            studentSexTextView.setText("");
                                            studentSeatTextView.setText("");
                                        }

                                        @Override
                                        public void onSuccess(int i, Header[] headers, String s) {
                                            ReadJsonByGson read = new ReadJsonByGson(s);
                                            studentNameTextView.setText(read.getArrayValue("student", "name"));
                                            if (read.getArrayBoolValue("student", "gender"))
                                                studentSexTextView.setText("男");
                                            else
                                                studentSexTextView.setText("女");
                                        }
                                    });
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    //Do some magic
//                                    String[] new_allStudents = new String[allStudents.length];
                                    List<String> new_allStudents = new ArrayList<String>();
                                    for (int j = 0, i = 0; j < allStudents.length; ++j) {
                                        if (allStudents[j].contains(newText)) {
                                            new_allStudents.add(i, allStudents[j]);
                                            ++i;
                                        }
                                    }
                                    String[] new_allStudents_str = new String[new_allStudents.size()];
                                    if (new_allStudents_str.length == 0) {
                                        String[] no_results = {"无结果"};
                                        searchView.setSuggestions(no_results);
                                    } else {
                                        for (int i = 0; i < new_allStudents_str.length; ++i) {
                                            new_allStudents_str[i] = new_allStudents.get(i);
                                        }
                                        searchView.setSuggestions(new_allStudents_str);
                                    }
                                    return false;
                                }
                            });
                            searchView.setOnSearchViewListener(new OwnMaterialSearchView.SearchViewListener() {
                                @Override
                                public void onSearchViewShown() {
                                    //Do some magic
                                    Toast.makeText(StudentHomeActivity.this, "找找同学啦！", Toast.LENGTH_SHORT).show();
                                    JSONObject object = new JSONObject();
                                    try {
                                        object.put("course_id", courseId);
                                        object.put("sub_id", subId);
                                        object.put("token", token);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getCourseGetallstudentsUrl(ownApp.getURL_FIGURE()), object.toString(), new TextHttpResponseHandler() {
                                        @Override
                                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                                            onSearchViewClosed();
                                            Toast.makeText(StudentHomeActivity.this, "网络错误，无法搜索，请重试！", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onSuccess(int i, Header[] headers, String s) {
                                            ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                                            allStudents = jsonByGson.getArray("students");
//                                            searchView.setSuggestions(allStudents);
                                        }
                                    });
                                }

                                @Override
                                public void onSearchViewClosed() {
                                    //Do some magic
                                }
                            });
                        }
                    });
                }

            }
        });
    }

    private void clearSeatChoose(String mSeatId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", token);
            jsonObject.put("seat_token", seatToken);
            jsonObject.put("seat_id", mSeatId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getSeatFreeseatUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                waitDialog = ProgressDialog.show(StudentHomeActivity.this, "正在释放座位", "请稍等…");
                super.onStart();
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                waitDialog.dismiss();
                Toast.makeText(StudentHomeActivity.this, "网络错误，请稍后重试！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                waitDialog.dismiss();
                SharedPreferences mSeatId_sp = getSharedPreferences("mSeat_id_" + subId + "_" + courseId, MODE_PRIVATE);
                SharedPreferences.Editor mSeatId_editor = mSeatId_sp.edit();
                mSeatId_editor.putString("mSeat_id_" + subId + "_" + courseId, "");
                mSeatId_editor.commit();
                Toast.makeText(StudentHomeActivity.this, "成功释放已选座位，你现在可以重新选座了！", Toast.LENGTH_SHORT).show();
                refreshSeats(true);
            }
        });
    }

    private void handSeatConfirmInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", token);
            jsonObject.put("seat_token", seatToken);
            jsonObject.put("seat_id", seatId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getSeatChooseseatUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                waitDialog = ProgressDialog.show(StudentHomeActivity.this, "正在提交选座", "请稍等…");
                super.onStart();
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                waitDialog.dismiss();
                if (i == 0) {
                    Toast.makeText(StudentHomeActivity.this, "服务器未响应，请稍后重试！", Toast.LENGTH_SHORT).show();
                } else if (s.contains("error_code")) {
                    ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                    if (jsonByGson.getValue("error_code").equals(ErrorCodeConstant.TOKEN_EXPIRED)) { // token过期
                        updateToken(3);
                    } else if (jsonByGson.getValue("error_code").equals(ErrorCodeConstant.SEAT_ALREADY_TAKEN)) { // 座位已被选
                        seatChooseTips();
                    } else if (jsonByGson.getValue("error_code").equals(ErrorCodeConstant.COURSE_ALREADY_OVER)) { // 已经下课
                        if (remainingSecsTimer != null)
                            remainingSecsTimer.cancel();
                        seatChooseChildLayout.setVisibility(View.GONE);
                        refreshTipsCardView.setVisibility(View.VISIBLE);
                        refreshTipsTextView.setText("已经下课啦，休息一下吧！");
                        refreshLittleTipsTextView.setText("返回课程列表可进入下一堂课");
                    }
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                waitDialog.dismiss();
                // 选座成功获取seat_id
                Toast.makeText(StudentHomeActivity.this, "你已成功选座！\n（座位号：" + seatId + "）", Toast.LENGTH_SHORT).show();
                SharedPreferences mSeatId_sp = getSharedPreferences("mSeat_id_" + subId + "_" + courseId, MODE_PRIVATE);
                SharedPreferences.Editor mSeatId_editor = mSeatId_sp.edit();
                mSeatId_editor.putString("mSeat_id_" + subId + "_" + courseId, seatId);
                mSeatId_editor.commit();
                refreshSeats(true);
            }
        });
    }

    private void clearSeatChooseTips(final String mSeatId) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("即将释放之前已选座位").setMessage("释放之后，可以重新选座。\n（点击确定可释放座位）").setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearSeatChoose(mSeatId);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();

        //设置透明度
        Window window = alert.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.75f;
        window.setAttributes(lp);
    }

    private void seatChooseTips() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("座位已被其他同学选了").setMessage("请另选座位吧！\n（点击确定可刷新座位）").setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refreshSeats(true);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();

        //设置透明度
        Window window = alert.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.75f;
        window.setAttributes(lp);
    }

    private void setSeatInfo(int COL, int ROW, ReadJsonByGson jsonByGson) {
        int seat_state[][] = jsonByGson.getSeatState(COL, ROW);
        for (int i = 0; i < ROW; ++i) { //行
            SeatInfo mSeatInfo = new SeatInfo();
            ArrayList<Seat> mSeatList = new ArrayList<>();
            ArrayList<Integer> mConditionList = new ArrayList<>();
            for (int j = 0; j < COL; ++j) { //列
                Seat mSeat = new Seat();
                if (seat_state[j][i] == 0) {
                    mSeat.setN("Z");
                    mConditionList.add(0); //设置无座位
                } else if (seat_state[j][i] == 1) {
                    mSeat.setN(String.valueOf(j));
                    mConditionList.add(1); //设置无人坐
                } else if (seat_state[j][i] == 2) {
                    mSeat.setN(String.valueOf(j));
                    mConditionList.add(2); //设置有人坐（暂时也包括坏座）
                }
//                mSeat.setDamagedFlg("");
//                mSeat.setLoveInd("0");
                mSeatList.add(mSeat);
            }
            mSeatInfo.setDesc(String.valueOf(i + 1));
            mSeatInfo.setRow(String.valueOf(i + 1));
            mSeatInfo.setSeatList(mSeatList);
            list_seatInfo.add(mSeatInfo);
            list_seat_conditions.add(mConditionList);
        }
    }

    private void updateToken(final int whichLayout) { // 0:notifications 1:tests 2:seats 3:confirmChoose
        JSONObject object = new JSONObject();
        try {
            object.put("user_id", userId);
            object.put("password", password);
            object.put("role", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = object.toString();
        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getUserLoginUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                // 若token获取失败，提示用户手动刷新
                if (whichLayout == 0) {
                    Toast.makeText(StudentHomeActivity.this, "网络错误，请再刷新试试！", Toast.LENGTH_SHORT).show();
                    notificationsURV.setRefreshing(false);
                } else if (whichLayout == 1) {
                    Toast.makeText(StudentHomeActivity.this, "网络错误，请再刷新试试！", Toast.LENGTH_SHORT).show();
                    testsURV.setRefreshing(false);
                } else if (whichLayout == 2) {
                    // 选座布局消失，显示错误提示布局并提供刷新按钮
                    seatChooseChildLayout.setVisibility(View.GONE);
                    refreshTipsCardView.setVisibility(View.VISIBLE);
                    refreshTipsCardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refreshSeats(true); // 用户手动闭环处理（低概率事件）
                        }
                    });
                    refreshTipsTextView.setText("网络错误，请刷新重试！");
                    refreshLittleTipsTextView.setText("点击刷新");
                } else if (whichLayout == 3) {
                    Toast.makeText(StudentHomeActivity.this, "网络错误，请重新提交试试！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                token = jsonByGson.getValue("token");
                if (whichLayout == 0) { // 成功获取新token，后台自动继续刷新
                    refreshNotifications(); // 闭环处理
                } else if (whichLayout == 1) {
                    refreshTests(0);
                } else if (whichLayout == 2) {
                    refreshSeats(true); // 闭环处理
                } else if (whichLayout == 3) {
                    refreshSeats(false); // 闭环处理
                }
            }
        });
    }

    private void startRemainingSecsTimer() {
        remainingSecsTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                remainingSecs--;
                Message message = remainingSecsHandler.obtainMessage();
                message.arg1 = remainingSecs;
                remainingSecsHandler.sendMessage(message);
            }
        };
        remainingSecsTimer.schedule(task, 1000);
        if (remainingSecs == 0) { //由于有mHandler，所以此处可以动态判断
            remainingSecsTimer.cancel();
            seatChooseChildLayout.setVisibility(View.VISIBLE);
            refreshTipsCardView.setVisibility(View.GONE);
            refreshSeats(true);
        }
    }

}