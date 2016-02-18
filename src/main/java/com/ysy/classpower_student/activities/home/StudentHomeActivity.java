package com.ysy.classpower_student.activities.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import android.widget.Button;
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
import com.ysy.classpower_student.activities.test.TestPreviewActivity;
import com.ysy.classpower_utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class StudentHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private long exitTime;
    private LinearLayout notificationLayout;
    private LinearLayout testLayout;
    private RelativeLayout seatChooseLayout;
    private List<String> mData;
    private TextView studentSeatTextView;
    private TextView studentNameTextView;
    private TextView studentSexTextView;

    public static final int ROW = 12;
    public static final int EACH_ROW_COUNT = 12;
    private ArrayList<SeatInfo> list_seatInfo = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> list_seat_conditions = new ArrayList<>();

    public static boolean isSeatChooseEmpty = true;
    public static boolean isSeatChooseOpen;
    public static boolean isDirectlyCheckResult = false;
    public static String STUDENT_TEST_STATE = null; // DOING, DONE, WILL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isSeatChooseOpen = false;

        //通知、测试、选座页面
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

        //为抽屉里的头部Layout添加监听（注意先获取父级容器）
        View navHeaderStudentHomeView = navigationView.getHeaderView(0);
        LinearLayout navHeaderStudentHomeLayout = (LinearLayout) navHeaderStudentHomeView.findViewById(R.id.nav_header_student_home_layout);
        navHeaderStudentHomeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentHomeActivity.this, StudentWelcomeActivity.class));
                finish();
            }
        });

        //加载测试页面的RecyclerView（代替ListView）
        initData();
        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        HomeAdapter mAdapter = new HomeAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(StudentHomeActivity.this,
                DividerItemDecoration.VERTICAL_LIST));
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

        final SwipeRefreshLayout studentTestSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.student_test_swipe_refresh_layout);
        studentTestSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        studentTestSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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

        //初始化选座
        init();
        //点击座位出现的学生信息
        studentSeatTextView = (TextView) findViewById(R.id.student_seat_text_view);
        studentNameTextView = (TextView) findViewById(R.id.student_name_text_view);
        studentSexTextView = (TextView) findViewById(R.id.student_sex_text_view);
        //确定选座按钮
        Button confirmSeatChooseButton = (Button) findViewById(R.id.confirm_seat_choose_button);
        confirmSeatChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSeatChooseEmpty) {
                    Toast.makeText(StudentHomeActivity.this, "你还没有选择任何一个座位！", Toast.LENGTH_SHORT).show();
                } else {
                    //若已选座，则提交数据至服务器

                }
            }
        });

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
            isSeatChooseOpen = true;
            invalidateOptionsMenu(); //更新Toolbar上的控件排布
            notificationLayout.setVisibility(View.GONE);
            testLayout.setVisibility(View.GONE);
            seatChooseLayout.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            isSeatChooseOpen = false;
            invalidateOptionsMenu();
            startActivity(new Intent(this, LoginActivity.class));
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

    //加载选座数据模块
    private void init() {
        SSView mSSView = (SSView) this.findViewById(R.id.mSSView);
        //显示缩略图
        SSView.a(mSSView, true);
        mSSView.invalidate();

        SSThumbView mSSThumbView = (SSThumbView) this.findViewById(R.id.ss_ssthumview);
//		mSSView.setXOffset(20);
        setSeatInfo();
        mSSView.init(EACH_ROW_COUNT, ROW, list_seatInfo, list_seat_conditions, mSSThumbView, 5);
        mSSView.setOnSeatClickListener(new OnSeatClickListener() {

            @Override
            public boolean b(int column_num, int row_num, boolean paramBoolean) {
                if (row_num + 1 == 4 && column_num + 1 == 4) {
                    studentSeatTextView.setText((row_num + 1) + "排" + (column_num + 1) + "列");
                    studentNameTextView.setText("郑智予");
                    studentSexTextView.setText("女");
                } else {
                    studentSeatTextView.setText("        ");
                    studentNameTextView.setText("        ");
                    studentSexTextView.setText("        ");
                }
//                String desc = "您选择了第" + (row_num + 1) + "排" + " 第" + (column_num + 1) + "列";
//                Toast.makeText(StudentHomeActivity.this, desc.toString(), Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean a(int column_num, int row_num, boolean paramBoolean) {
//              String desc = "您取消了第" + (row_num + 1) + "排" + " 第" + (column_num + 1) + "列";
//				Toast.makeText(StudentHomeActivity.this,desc.toString(), Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void a() {
                // TODO Auto-generated method stub

            }
        });
    }

    private void setSeatInfo() {
        for (int i = 0; i < ROW; i++) {//12行
            SeatInfo mSeatInfo = new SeatInfo();
            ArrayList<Seat> mSeatList = new ArrayList<>();
            ArrayList<Integer> mConditionList = new ArrayList<>();
            for (int j = 0; j < EACH_ROW_COUNT; j++) {//每排12个座位
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
