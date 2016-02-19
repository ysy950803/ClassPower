package com.ysy.classpower_teacher.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ysy.classpower.R;
import com.ysy.classpower_common.activities.LoginActivity;
import com.ysy.classpower_seatchoose.OnSeatClickListener;
import com.ysy.classpower_seatchoose.model.Seat;
import com.ysy.classpower_seatchoose.model.SeatInfo;
import com.ysy.classpower_seatchoose.view.SSThumbView;
import com.ysy.classpower_seatchoose.view.SSView;
import com.ysy.classpower_student.activities.home.SettingsActivity;
import com.ysy.classpower_teacher.activities.test.SelectSubjectsActivity;
import com.ysy.classpower_teacher.activities.test.TestPreviewActivity;
import com.ysy.classpower_utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class TeacherHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private long exitTime;
    private LinearLayout notificationLayout;
    private LinearLayout testLayout;
    private RelativeLayout seatLookLayout;
    private FloatingActionButton fab;
    private List<String> mData;
    private TextView studentSeatTextView;
    private TextView studentNameTextView;
    private TextView studentSexTextView;
    private TextView studentNumberTextView;
    private TextView studentClassTextView;
    private ImageView studentHeadImageView;

    public static String TEACHER_TEST_STATE = null; //DOING, WILL, DONE
    private static final int ROW = 12;
    private static final int COL = 12;
    private ArrayList<SeatInfo> list_seatInfo = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> list_seat_conditions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //为抽屉里的头部Layout添加监听（注意先获取父级容器）
        View navHeaderStudentHomeView = navigationView.getHeaderView(0);
        LinearLayout navHeaderStudentHomeLayout = (LinearLayout) navHeaderStudentHomeView.findViewById(R.id.nav_header_teacher_home_layout);
        navHeaderStudentHomeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherHomeActivity.this, TeacherWelcomeActivity.class));
                finish();
            }
        });

        //通知、测试、到位页面
        notificationLayout = (LinearLayout) findViewById(R.id.notification_layout);
        testLayout = (LinearLayout) findViewById(R.id.test_layout);
        seatLookLayout = (RelativeLayout) findViewById(R.id.seat_look_layout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notificationLayout.getVisibility() == View.VISIBLE)
                    startActivity(new Intent(TeacherHomeActivity.this, AddNotificationsActivity.class));
                else if (testLayout.getVisibility() == View.VISIBLE)
                    startActivity(new Intent(TeacherHomeActivity.this, SelectSubjectsActivity.class));
            }
        });

        //到位点名按钮
        Button callTheRollButton = (Button) findViewById(R.id.call_the_roll_button);
        callTheRollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向服务器提交点名请求，获取实时反馈
            }
        });

        //初始化选座
        init();

        //加载测试页面的RecyclerView（代替ListView）
        initData();
        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        HomeAdapter mAdapter;
        mRecyclerView.setAdapter(mAdapter = new HomeAdapter());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        //List当中Item的监听
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0)
                    TEACHER_TEST_STATE = "DOING";
                else if (position == 1)
                    TEACHER_TEST_STATE = "WILL";
                else
                    TEACHER_TEST_STATE = "DONE";
                startActivity(new Intent(TeacherHomeActivity.this, TestPreviewActivity.class));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        final SwipeRefreshLayout teacherTestSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.teacher_test_swipe_refresh_layout);
        teacherTestSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        teacherTestSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changedData();
                        HomeAdapter mAdapter = new HomeAdapter();
                        mRecyclerView.setAdapter(mAdapter);
                        //List当中Item的监听
                        mAdapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                if (position == 0)
                                    TEACHER_TEST_STATE = "DOING";
                                else if (position == 1)
                                    TEACHER_TEST_STATE = "WILL";
                                else
                                    TEACHER_TEST_STATE = "DONE";
                                startActivity(new Intent(TeacherHomeActivity.this, TestPreviewActivity.class));
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {

                            }
                        });
                        teacherTestSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        //点击座位出现的学生信息
        studentSeatTextView = (TextView) findViewById(R.id.student_seat_text_view);
        studentNameTextView = (TextView) findViewById(R.id.student_name_text_view);
        studentSexTextView = (TextView) findViewById(R.id.student_sex_text_view);
        studentClassTextView = (TextView) findViewById(R.id.student_class_text_view);
        studentNumberTextView = (TextView) findViewById(R.id.student_number_text_view);

        studentHeadImageView = (ImageView) findViewById(R.id.student_head_image_view);

    }

    //初始化List数据（离线Demo）
    protected void initData() {
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

    //刷新后的List数据（离线Demo）
    protected void changedData() {
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

    //自定义接口，然后在onBindViewHolder中去为holder.itemView去设置相应的监听最后回调设置的监听
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(TeacherHomeActivity.this).inflate(R.layout.item_test_student_home, parent,
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
                System.exit(0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.teacher_home, menu);
        return true;
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
            notificationLayout.setVisibility(View.VISIBLE);
            testLayout.setVisibility(View.GONE);
            seatLookLayout.setVisibility(View.GONE);
            fab.setImageResource(R.drawable.ic_add_white_24dp);

        } else if (id == R.id.nav_test) {
            notificationLayout.setVisibility(View.GONE);
            testLayout.setVisibility(View.VISIBLE);
            seatLookLayout.setVisibility(View.GONE);
            fab.setImageResource(R.drawable.ic_add_white_24dp);

        } else if (id == R.id.nav_look_seat) {
            notificationLayout.setVisibility(View.GONE);
            testLayout.setVisibility(View.GONE);
            seatLookLayout.setVisibility(View.VISIBLE);
            fab.setImageResource(R.drawable.ic_menu_white_24dp);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_solve) {

        } else if (id == R.id.nav_logout) {
            startActivity(new Intent(this, LoginActivity.class));
            this.finish();
        } else if (id == R.id.nav_exit) {
            this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //加载选座数据模块
    private void init() {

        SSView mSSView = (SSView) this.findViewById(R.id.mSSView_teacher);
        //显示缩略图
        SSView.a(mSSView, true);
        mSSView.invalidate();

        final SSThumbView mSSThumbView = (SSThumbView) this.findViewById(R.id.ss_ssthumview_teacher);
//		mSSView.setXOffset(20);
        setSeatInfo();
        mSSView.init(COL, ROW, list_seatInfo, list_seat_conditions, mSSThumbView, 5);
        mSSView.setOnSeatClickListener(new OnSeatClickListener() {
            @Override
            public boolean b(int column_num, int row_num, boolean paramBoolean) {
                if (row_num + 1 == 4 && column_num + 1 == 4) {
                    studentSeatTextView.setText((row_num + 1) + "排" + (column_num + 1) + "列");
                    studentNameTextView.setText("郑智予");
                    studentSexTextView.setText("女");
                    studentClassTextView.setText("计1303");
                    studentNumberTextView.setText("41355083");
                    studentHeadImageView.setImageResource(R.drawable.ic_student_head_example);
                } else {
                    studentSeatTextView.setText("        ");
                    studentNameTextView.setText("        ");
                    studentSexTextView.setText("        ");
                    studentClassTextView.setText("        ");
                    studentNumberTextView.setText("        ");
                    studentHeadImageView.setImageResource(R.drawable.ic_account_circle_black_48dp);
                }
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
        mSSView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        mSSThumbView.setVisibility(View.VISIBLE);
                        studentHeadImageView.setVisibility(View.INVISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        mSSThumbView.setVisibility(View.INVISIBLE);
                        studentHeadImageView.setVisibility(View.VISIBLE);
                        break;
                }
                return false; //此处必须返回false，否则View无法移动
            }
        });

    }

    private void setSeatInfo() {
        for (int i = 0; i < ROW; i++) {//12行
            SeatInfo mSeatInfo = new SeatInfo();
            ArrayList<Seat> mSeatList = new ArrayList<>();
            ArrayList<Integer> mConditionList = new ArrayList<>();
            for (int j = 0; j < COL; j++) {//每排12个座位
                Seat mSeat = new Seat();
                if (j < 8 && j > 3 && i < 3) {
                    mSeat.setN("Z");
                    mConditionList.add(0); //设置无座位
                } else if (j > 4 && j < 7 && i > 2) {
                    mSeat.setN("Z");
                    mConditionList.add(0); //设置无座位
                } else {
                    mSeat.setN(String.valueOf(j - 2));
//                    mSeat.setN(String.valueOf(j));
                    if (i > 2 && i < 6 && j < 9 && j > 2)
                        mConditionList.add(2); //设置有人坐
                    else mConditionList.add(1); //设置无人坐
                }
                mSeat.setDamagedFlg("");
                mSeat.setLoveInd("0");
                mSeatList.add(mSeat);
            }
            mSeatInfo.setDesc(String.valueOf(i + 1));
            mSeatInfo.setRow(String.valueOf(i + 1));
            mSeatInfo.setSeatList(mSeatList);
            list_seatInfo.add(mSeatInfo);
            list_seat_conditions.add(mConditionList);
        }
    }

}
