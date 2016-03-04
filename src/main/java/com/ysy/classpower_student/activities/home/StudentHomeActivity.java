package com.ysy.classpower_student.activities.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.marshalchen.ultimaterecyclerview.RecyclerItemClickListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.ysy.classpower.R;
import com.ysy.classpower_student.activities.base.StudentLoginActivity;
import com.ysy.classpower_common.constant.ServerUrlConstant;
import com.ysy.classpower_seatchoose.OnSeatClickListener;
import com.ysy.classpower_seatchoose.model.Seat;
import com.ysy.classpower_seatchoose.model.SeatInfo;
import com.ysy.classpower_seatchoose.view.SSThumbView;
import com.ysy.classpower_seatchoose.view.SSView;
import com.ysy.classpower_student.activities.base.StudentWelcomeActivity;
import com.ysy.classpower_student.activities.test.TestPreviewActivity;
import com.ysy.classpower_student.adapters.StudentHomeNotificationsListAdapter;
import com.ysy.classpower_utils.DividerItemDecoration;
import com.ysy.classpower_utils.EmptyListWithTipsAdapter;
import com.ysy.classpower_utils.PostJsonAndGetCallback;
import com.ysy.classpower_utils.ReadJsonByGson;

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
    private List<String> mData;

    private List<String> ntfcsListByData;
    private List<String> ntfcsListContentData;
    private List<String> ntfcsListCreatedOnData;
    private List<String> ntfcsListIdData;
    private List<String> ntfcsListTitleData;
    private int ntfcsListOnTopCount;
    private boolean isNotificationsListEmpty;
    private UltimateRecyclerView notificationsURV;

    private TextView studentSeatTextView;
    private TextView studentNameTextView;
    private TextView studentSexTextView;
    private TextView refreshTipsTextView;
    private TextView refreshLittleTipsTextView;
    private RelativeLayout refreshTipsLayout;
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
    public static String STUDENT_TEST_STATE = null; // DOING, DONE, WILL

    private static final String SEAT_GETSEATTOKEN_URL = ServerUrlConstant.SEAT_GETSEATTOKEN_URL;
    private static final String SEAT_GETSEATMAP_URL = ServerUrlConstant.SEAT_GETSEATMAP_URL;
    private static final String COURSE_NTFC_GETNTFCS_URL = ServerUrlConstant.COURSE_NTFC_GETNTFCS_URL;
    private static final String USER_LOGIN_URL = ServerUrlConstant.USER_LOGIN_URL;
    private static final String SEAT_CHOOSESEAT = ServerUrlConstant.SEAT_CHOOSESEAT_URL;
    private static final String SEAT_FREESEAT = ServerUrlConstant.SEAT_FREESEAT;

    private String userId = "";
    private String password = "";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 核心参数优先获取
        SharedPreferences userId_sp = getSharedPreferences("userId", MODE_PRIVATE);
        SharedPreferences password_sp = getSharedPreferences("password", MODE_PRIVATE);
        SharedPreferences token_sp = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences courseId_sp = getSharedPreferences("courseId", MODE_PRIVATE);
        SharedPreferences subId_sp = getSharedPreferences("subId", MODE_PRIVATE);
        userId = userId_sp.getString("userId", "");
        password = password_sp.getString("password", "");
        token = token_sp.getString("token", "");
        courseId = courseId_sp.getString("courseId", "");
        subId = subId_sp.getString("subId", "");

        isSeatChooseOpen = false;

        // 通知、测试、选座页面
        notificationLayout = (LinearLayout) findViewById(R.id.notification_layout);
        testLayout = (LinearLayout) findViewById(R.id.test_layout);
        seatChooseLayout = (RelativeLayout) findViewById(R.id.seat_choose_layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        navHeaderStudentHomeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentHomeActivity.this, StudentWelcomeActivity.class));
                finish();
            }
        });

        // 获取通知List模块
        initNotifications();

        // 加载测试页面的RecyclerView（代替ListView）
        initTestsData();
        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        HomeAdapter mAdapter = new HomeAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(StudentHomeActivity.this,
                DividerItemDecoration.VERTICAL_LIST));
        // List当中Item的监听
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0)
                    STUDENT_TEST_STATE = "DOING";
                else if (position == 1)
                    STUDENT_TEST_STATE = "WILL";
                else
                    STUDENT_TEST_STATE = "DONE";
                startActivity(new Intent(StudentHomeActivity.this, TestPreviewActivity.class));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        final SwipeRefreshLayout studentTestSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.student_test_swipe_refresh_layout);
        studentTestSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        studentTestSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changedTestsData();
                        HomeAdapter mAdapter = new HomeAdapter();
                        mRecyclerView.setAdapter(mAdapter);
                        //List当中Item的监听
                        mAdapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                if (position == 0)
                                    STUDENT_TEST_STATE = "DOING";
                                else if (position == 1)
                                    STUDENT_TEST_STATE = "WILL";
                                else
                                    STUDENT_TEST_STATE = "DONE";
                                startActivity(new Intent(StudentHomeActivity.this, TestPreviewActivity.class));
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {

                            }
                        });
                        studentTestSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        // 初始化选座
        initSeatsData();
        // 点击座位出现的学生信息
        studentSeatTextView = (TextView) findViewById(R.id.student_seat_text_view);
        studentNameTextView = (TextView) findViewById(R.id.student_name_text_view);
        studentSexTextView = (TextView) findViewById(R.id.student_sex_text_view);
        // 确定选座按钮
        Button confirmSeatChooseButton = (Button) findViewById(R.id.confirm_seat_choose_button);
        confirmSeatChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mSeatId_sp = getSharedPreferences("mSeat_id_" + subId + "_" + courseId, MODE_PRIVATE);
                String mSeatId = mSeatId_sp.getString("mSeat_id_" + subId + "_" + courseId, "");
                if (isSeatChooseEmpty) {
                    Toast.makeText(StudentHomeActivity.this, "你还没有选择任何一个座位！", Toast.LENGTH_SHORT).show();
                } else {
                    if (seatId.equals("")) {
                        Toast.makeText(StudentHomeActivity.this, "你还没有选择任何一个座位！", Toast.LENGTH_SHORT).show();
                    }
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
        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), SEAT_FREESEAT, json, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(StudentHomeActivity.this, "网络错误，请稍后重试！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
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
        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), SEAT_CHOOSESEAT, json, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (s.contains("error_code")) {
                    ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                    if (jsonByGson.getValue("error_code").equals("605")) { // token过期
                        updateToken(3);
                    } else if (jsonByGson.getValue("error_code").equals("901")) { // 座位已被选
                        seatChooseTips();
                    }
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                // 选座成功获取seat_id
                Toast.makeText(StudentHomeActivity.this, "你已成功选座（座位号：" + seatId + "）！", Toast.LENGTH_SHORT).show();
                SharedPreferences mSeatId_sp = getSharedPreferences("mSeat_id_" + subId + "_" + courseId, MODE_PRIVATE);
                SharedPreferences.Editor mSeatId_editor = mSeatId_sp.edit();
                mSeatId_editor.putString("mSeat_id_" + subId + "_" + courseId, seatId);
                mSeatId_editor.commit();
                refreshSeats(true);
            }
        });
    }

    private void clearSeatChooseTips(final String mSeatId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        final AlertDialog alert = builder.create();
        alert.show();

        //设置透明度
        Window window = alert.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.75f;
        window.setAttributes(lp);
    }

    private void seatChooseTips() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        final AlertDialog alert = builder.create();
        alert.show();

        //设置透明度
        Window window = alert.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.75f;
        window.setAttributes(lp);
    }

    // 初始化通知List模块
    private void initNotifications() {
        initNotificationsData(0, null);
        LinearLayoutManager notificationsLM = new LinearLayoutManager(this);
        notificationsURV = (UltimateRecyclerView) findViewById(R.id.student_home_notifications_list_urv);
        notificationsURV.setLayoutManager(notificationsLM);
        if (isNotificationsListEmpty) {
            List<String> emptyListData = new ArrayList<>();
            emptyListData.add(0, "没有新的课程通知");
            EmptyListWithTipsAdapter emptyListAdapter = new EmptyListWithTipsAdapter(emptyListData);
            notificationsURV.setAdapter(emptyListAdapter);
        } else {
            StudentHomeNotificationsListAdapter notificationsListAdapter = new StudentHomeNotificationsListAdapter(ntfcsListByData, ntfcsListContentData,
                    ntfcsListCreatedOnData, ntfcsListTitleData, ntfcsListOnTopCount);
            notificationsURV.setAdapter(notificationsListAdapter);
        }
        notificationsURV.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNotifications();
            }
        });
        notificationsURV.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        }));

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
        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), COURSE_NTFC_GETNTFCS_URL, json, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (s.contains("error_code")) {
                    ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                    if (jsonByGson.getValue("error_code").equals("605")) { // token过期，闭环处理
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
                    List<String> emptyListData = new ArrayList<>();
                    emptyListData.add(0, "没有新的课程通知");
                    EmptyListWithTipsAdapter emptyListAdapter = new EmptyListWithTipsAdapter(emptyListData);
                    notificationsURV.setAdapter(emptyListAdapter);
                } else {
                    StudentHomeNotificationsListAdapter notificationsListAdapter = new StudentHomeNotificationsListAdapter(ntfcsListByData, ntfcsListContentData,
                            ntfcsListCreatedOnData, ntfcsListTitleData, ntfcsListOnTopCount);
                    notificationsURV.setAdapter(notificationsListAdapter);
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

    // 初始化List数据（离线Demo）
    protected void initTestsData() {
        mData = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (i == 0)
                mData.add("2015-10-15 13:37:42    正在进行");
            else if (i == 1)
                mData.add("2015-10-15 13:37:34    未开始");
            else
                mData.add("2015-10-15 13:37:3" + i + "    已结束");
        }
    }

    // 刷新后的List数据（离线Demo）
    protected void changedTestsData() {
        mData = new ArrayList<>();
        for (int i = 0; i < 17; i++) {
            if (i == 0)
                mData.add("2016-01-15 15:35:45    正在进行");
            else if (i == 1)
                mData.add("2016-01-15 15:35:35    未开始");
            else
                mData.add("2016-01-15 15:35:3" + i + "    已结束");
        }
    }

    // 自定义接口，然后在onBindViewHolder中去为holder.itemView去设置相应的监听最后回调设置的监听
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(StudentHomeActivity.this).inflate(R.layout.item_test_student_home, parent,
                    false));
        }

        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.tv.setText(mData.get(position));
            if (position == 0)
                holder.tv.setBackgroundResource(R.drawable.item_doing_press);
            else if (position == 1)
                holder.tv.setBackgroundResource(R.drawable.item_will_press);
            else
                holder.tv.setBackgroundResource(R.drawable.item_done_press);

            // 如果设置了回调，则设置点击事件
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(holder.itemView, pos);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                        return false;
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv;

            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.id_num);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("找同学"); //设置搜索框内的hint文字
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() { //设置打开关闭动作监听
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Toast.makeText(StudentHomeActivity.this, "找找同学啦！", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
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
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
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
            startActivity(new Intent(this, StudentLoginActivity.class));
            this.finish();
        } else if (id == R.id.nav_exit) {
            isSeatChooseOpen = false;
            invalidateOptionsMenu();
            this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 加载选座数据模块
    private void initSeatsData() {
        seatChooseChildLayout = (RelativeLayout) findViewById(R.id.seat_choose_child_layout);
        refreshTipsTextView = (TextView) findViewById(R.id.student_home_refresh_tips_textView);
        refreshTipsLayout = (RelativeLayout) findViewById(R.id.student_home_refresh_tips_layout);
        refreshLittleTipsTextView = (TextView) findViewById(R.id.student_home_refresh_little_tips_textView);

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
    }

    private void refreshSeats(final boolean isSetSeatsNap) {
        seatId = "";
        studentSeatTextView.setText("");
        studentNameTextView.setText("");
        studentSexTextView.setText("");
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
        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), SEAT_GETSEATTOKEN_URL, json, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (s.contains("error_code")) {
                    ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                    if (jsonByGson.getValue("error_code").equals("906")) { // 还没上课
                        if (remainingSecsTimer != null)
                            remainingSecsTimer.cancel();
                        seatChooseChildLayout.setVisibility(View.GONE);
                        refreshTipsLayout.setVisibility(View.VISIBLE);
                        refreshLittleTipsTextView.setText("请耐心等待…");
                        remainingSecs = Integer.valueOf(jsonByGson.getValue("remaining_secs"));
                        startRemainingSecsTimer();
                        remainingSecsHandler = new Handler() {
                            public void handleMessage(Message msg) {
                                refreshTipsTextView.setText("还有 " + msg.arg1 + " 秒才上课呢！");
                                startRemainingSecsTimer();
                            }
                        };
                    } else if (jsonByGson.getValue("error_code").equals("909")) { // 已经下课
                        if (remainingSecsTimer != null)
                            remainingSecsTimer.cancel();
                        seatChooseChildLayout.setVisibility(View.GONE);
                        refreshTipsLayout.setVisibility(View.VISIBLE);
                        refreshTipsTextView.setText("已经下课啦，休息一下吧！");
                        refreshLittleTipsTextView.setText("返回课程列表可进入下一堂课");
                    } else if (jsonByGson.getValue("error_code").equals("605")) { // token过期，闭环处理
                        updateToken(2);
                    }
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
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
                    new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), SEAT_GETSEATMAP_URL, json, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                            Toast.makeText(StudentHomeActivity.this, "获取座位图失败，请稍后刷新重试！", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            ReadJsonByGson jsonByGson = new ReadJsonByGson(s);
                            refreshTipsLayout.setVisibility(View.GONE);
                            seatChooseChildLayout.setVisibility(View.VISIBLE);
                            // 闭环成功节点，开始绘制座位图
                            int COL = jsonByGson.getIntValue("col_num");
                            int ROW = jsonByGson.getIntValue("row_num");
                            setSeatInfo(COL, ROW, jsonByGson);
                            final String seat_id[][] = jsonByGson.getSeatId(COL, ROW);
                            mSSView.init(COL, ROW, list_seatInfo, list_seat_conditions, mSSThumbView, 5);
                            mSSView.setOnSeatClickListener(new OnSeatClickListener() {
                                @Override
                                public boolean b(int column_num, int row_num, boolean paramBoolean) {
                                    seatId = seat_id[column_num][row_num];
                                    studentSeatTextView.setText(seatId);
                                    return false;
                                }

                                @Override
                                public boolean a(int column_num, int row_num, boolean paramBoolean) {

                                    return false;
                                }

                                @Override
                                public void a() {
                                    // TODO Auto-generated method stub

                                }
                            });
                        }
                    });
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
            refreshTipsLayout.setVisibility(View.GONE);
            refreshSeats(true);
        }
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
        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), USER_LOGIN_URL, json, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                // 若token获取失败，提示用户手动刷新
                if (whichLayout == 0) {
                    Toast.makeText(StudentHomeActivity.this, "网络错误，请再刷新试试！", Toast.LENGTH_SHORT).show();
                    notificationsURV.setRefreshing(false);
                } else if (whichLayout == 1) {

                } else if (whichLayout == 2) {
                    // 选座布局消失，显示错误提示布局并提供刷新按钮
                    seatChooseChildLayout.setVisibility(View.GONE);
                    refreshTipsLayout.setVisibility(View.VISIBLE);
                    refreshTipsLayout.setOnClickListener(new View.OnClickListener() {
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

                } else if (whichLayout == 2) {
                    refreshSeats(true); // 闭环处理
                } else if (whichLayout == 3) {
                    refreshSeats(false); // 闭环处理
                }
            }
        });
    }

}