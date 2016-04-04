package com.ysy.classpower_student.activities.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.ysy.classpower.R;
import com.ysy.classpower_common.constant.ServerUrlConstant;
import com.ysy.classpower_student.activities.home.StudentHomeActivity;
import com.ysy.classpower_student.adapters.StudentWelcomeListAdapter;
import com.ysy.classpower_student.fragments.SearchStaticRecyclerFragment;
import com.ysy.classpower_student.fragments.StudentWelcomeListFragment;
import com.ysy.classpower_utils.ConnectionDetector;
import com.ysy.classpower_utils.ListOnItemClickListener;
import com.ysy.classpower_utils.OwnApp;
import com.ysy.classpower_utils.OwnSearchViewLayout;
import com.ysy.classpower_utils.json_processor.PostJsonAndGetCallback;
import com.ysy.classpower_utils.json_processor.ReadJsonByGson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import xyz.sahildave.widget.SearchViewLayout;

public class StudentWelcomeActivity extends AppCompatActivity {

    private long exitTime;
    private AppBarLayout appBarLayout;
    private String[] courseNameInfo;
    private String[] teacherInfo;
    private String[] dayInfo;
    private String[] weekInfo;
    private String[] periodInfo;
    private String[] roomNameInfo;
    private String[] roomIdInfo;
    private String[] courseIdInfo;
    private String[] subIdInfo;
    private List<String> courseNameData;
    private List<String> courseIdData;
    private List<String> subIdData;
    private List<String> teacherData;
    private List<String> dayData;
    private List<String> weekData;
    private List<String> periodData;
    private List<String> roomNameData;
    private List<String> roomIdData;

    private String token;
    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_welcome);
//        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final OwnSearchViewLayout searchViewLayout = (OwnSearchViewLayout) findViewById(R.id.search_view_container);

        SharedPreferences token_sp = getSharedPreferences("token", Context.MODE_PRIVATE);
        token = token_sp.getString("token", "");
        SharedPreferences loginJson_sp = getSharedPreferences("loginJson", Context.MODE_PRIVATE);
        SharedPreferences currentWeek_sp = getSharedPreferences("currentWeek", Context.MODE_PRIVATE);
        String currentWeek = currentWeek_sp.getString("currentWeek", "1");
        ReadJsonByGson jsonByGson = new ReadJsonByGson(loginJson_sp.getString("loginJson", "{}"));
        courseNameInfo = jsonByGson.getCoursesBasicInfo("course_name");
        courseIdInfo = jsonByGson.getCoursesBasicInfo("course_id");
        subIdInfo = jsonByGson.getCoursesBasicInfo("sub_id");
        teacherInfo = jsonByGson.getCoursesTeachersInfo();
        dayInfo = jsonByGson.getCoursesTimesDaysInfo(currentWeek); // 参数：week
        weekInfo = jsonByGson.getCoursesTimesWeeksInfo();
        periodInfo = jsonByGson.getCoursesTimesPeriodInfo(currentWeek, "1");
        roomNameInfo = jsonByGson.getCoursesTimesRoomNameInfo(currentWeek, "1");
        roomIdInfo = jsonByGson.getCoursesTimesRoomIdInfo(currentWeek, "1");

//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        final FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.student_welcome_add_fab);
        assert addFab != null;
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        List<Fragment> fragments = new ArrayList<>();
//        Fragment list_fragment = new StudentWelcomeListFragment();
//        fragments.add(list_fragment);

//        final ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
//        TestDoingFragmentAdapter fragmentAdapter = new TestDoingFragmentAdapter(getSupportFragmentManager(), fragments);
//        mViewPager.setAdapter(fragmentAdapter);
//        tabLayout.setupWithViewPager(mViewPager);
//        tabLayout.setTabsFromPagerAdapter(fragmentAdapter);
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                mViewPager.setCurrentItem(tab.getPosition());
//                if (tab.getPosition() == 1) {
//                    addFab.setVisibility(View.VISIBLE);
//                    invalidateOptionsMenu();
//                } else if (tab.getPosition() == 0) {
//                    addFab.setVisibility(View.GONE);
//                    invalidateOptionsMenu();
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final StudentWelcomeListFragment fragment = new StudentWelcomeListFragment();
        fragmentTransaction.add(R.id.student_welcome_list_linear_layout, fragment);
        fragmentTransaction.commit();

        assert searchViewLayout != null;
        final SearchStaticRecyclerFragment searchStaticRecyclerFragment = new SearchStaticRecyclerFragment();
        searchViewLayout.setExpandedContentFragment(this, searchStaticRecyclerFragment);
        searchViewLayout.handleToolbarAnimation(toolbar);
        searchViewLayout.setCollapsedHint("课表太长？赶紧找找课堂吧！");
        searchViewLayout.setExpandedHint("找课堂");
        ColorDrawable collapsed = new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary));
        ColorDrawable expanded = new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        searchViewLayout.setTransitionDrawables(collapsed, expanded);
        searchViewLayout.setOnToggleAnimationListener(new OwnSearchViewLayout.OnToggleAnimationListener() {
            @Override
            public void onStart(boolean expanding) {
                if (expanding) {
                    addFab.hide();
                } else {
                    addFab.show();
                }
            }

            @Override
            public void onFinish(boolean expanded) {
            }
        });
        searchViewLayout.setSearchListener(new OwnSearchViewLayout.SearchListener() {
            @Override
            public void onFinished(String searchKeyword) {
                UltimateRecyclerView recyclerView = (UltimateRecyclerView) fragment.getView().findViewById(R.id.student_welcome_list_urv);
                searchViewLayout.collapse();
                Log.d("TEST_onFinished", searchKeyword);
                updateListBySearch(recyclerView, searchKeyword);
            }
        });
        searchViewLayout.setSearchBoxListener(new OwnSearchViewLayout.SearchBoxListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("TEST_beforeTextChanged", s.toString() + "start" + start + "after" + after + "count" + count);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("TEST_onTextChanged", s.toString() + "start" + start + "before" + before + "count" + count);
                courseNameData = new ArrayList<>();
                teacherData = new ArrayList<>();
                RecyclerView recyclerView = (RecyclerView) searchStaticRecyclerFragment.getView().findViewById(R.id.search_static_recycler);
                for (int i = 0, j = 0; i < courseNameInfo.length; ++i) {
                    if (courseNameInfo[i].contains(s.toString()) || teacherInfo[i].contains(s.toString())) {
                        courseNameData.add(j, courseNameInfo[i]);
                        teacherData.add(j, teacherInfo[i]);
                        ++j;
                    }
                }
                if (courseNameData.size() == 0) {
                    ListViewAdapter adapter = new ListViewAdapter(new ArrayList<String>(), new ArrayList<String>());
                    recyclerView.setAdapter(adapter);
                } else if (start == 0 && before == 1 && count == 0) {
                    ListViewAdapter adapter = new ListViewAdapter(new ArrayList<String>(), new ArrayList<String>());
                    recyclerView.setAdapter(adapter);
                } else {
                    ListViewAdapter adapter = new ListViewAdapter(teacherData, courseNameData);
                    adapter.setListOnItemClickListener(new ListOnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            searchViewLayout.setExpandedText(courseNameData.get(position));
                        }

                        @Override
                        public void onItemLongClick(View view, int position) {

                        }
                    });
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("TEST_afterTextChanged", s.toString());
            }
        });

    }

    public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ListViewHolder> {

        public List<String> card_details_list;
        public List<String> card_title_list;

        private ListOnItemClickListener mOnItemClickListener;

        public void setListOnItemClickListener(ListOnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        public ListViewAdapter(List<String> details_list, List<String> title_list) {
            this.card_details_list = details_list;
            this.card_title_list = title_list;
        }

        class ListViewHolder extends RecyclerView.ViewHolder {
            private final TextView mDetailText;
            private final TextView mTitleText;

            public ListViewHolder(View itemView) {
                super(itemView);
                mDetailText = (TextView) itemView.findViewById(R.id.card_details);
                mTitleText = (TextView) itemView.findViewById(R.id.card_title);
            }
        }

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ListViewHolder(LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.search_static_list_item, parent, false));
        }

        @Override
        public int getItemCount() {
            return card_details_list.size();
        }

        @Override
        public void onBindViewHolder(final ListViewHolder viewHolder, int position) {
            viewHolder.mDetailText.setText(card_details_list.get(position));
            viewHolder.mTitleText.setText(card_title_list.get(position));

            // 如果设置了回调，则设置点击事件
            if (mOnItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = viewHolder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(viewHolder.itemView, pos);
                    }
                });

                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = viewHolder.getLayoutPosition();
                        mOnItemClickListener.onItemLongClick(viewHolder.itemView, pos);
                        return false;
                    }
                });
            }

        }
    }

    private void updateListBySearch(UltimateRecyclerView recyclerView, String searchKeyword) {
        final OwnApp ownApp = (OwnApp) getApplication();
        courseNameData = new ArrayList<>();
        courseIdData = new ArrayList<>();
        subIdData = new ArrayList<>();
        teacherData = new ArrayList<>();
        dayData = new ArrayList<>();
        weekData = new ArrayList<>();
        periodData = new ArrayList<>();
        roomNameData = new ArrayList<>();
        roomIdData = new ArrayList<>();

        for (int i = 0, j = 0; i < courseNameInfo.length; ++i) {
            if (courseNameInfo[i].contains(searchKeyword)) {
                courseNameData.add(j, courseNameInfo[i]);
                teacherData.add(j, teacherInfo[i]);
                subIdData.add(j, subIdInfo[i]);
                courseIdData.add(j, courseIdInfo[i]);
                dayData.add(j, dayInfo[i]);
                weekData.add(j, weekInfo[i]);
                periodData.add(j, periodInfo[i]);
                roomNameData.add(j, roomNameInfo[i]);
                roomIdData.add(j, roomIdInfo[i]);
                ++j;
            }
        }

        if (courseNameData.size() == 0) {
            StudentWelcomeListAdapter listAdapter = new StudentWelcomeListAdapter(courseNameData, teacherData, dayData, weekData, periodData, roomNameData);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(listAdapter);
            recyclerView.setRefreshing(false);
            Toast.makeText(StudentWelcomeActivity.this, "无结果，请重新搜索或者下拉刷新所有课程！", Toast.LENGTH_LONG).show();
        } else {
            StudentWelcomeListAdapter listAdapter = new StudentWelcomeListAdapter(courseNameData, teacherData, dayData, weekData, periodData, roomNameData);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(listAdapter);
            listAdapter.setListOnItemClickListener(new ListOnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    ConnectionDetector cd = new ConnectionDetector(StudentWelcomeActivity.this);
                    if (cd.isConnectingToInternet()) {
                        // 点击列表项时记录各种id，跳转后使用
                        SharedPreferences courseId_sp = getSharedPreferences("courseId", Context.MODE_PRIVATE);
                        SharedPreferences subId_sp = getSharedPreferences("subId", Context.MODE_PRIVATE);
                        SharedPreferences.Editor courseId_editor = courseId_sp.edit();
                        SharedPreferences.Editor subId_editor = subId_sp.edit();
                        courseId_editor.putString("courseId", courseIdData.get(position));
                        subId_editor.putString("subId", subIdData.get(position));
                        courseId_editor.commit();
                        subId_editor.commit();

                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("token", token);
                            obj.put("course_id", courseIdData.get(position));
                            obj.put("sub_id", subIdData.get(position));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String json = obj.toString();
                        new PostJsonAndGetCallback(new AsyncHttpClient(), getApplicationContext(), ServerUrlConstant.getCourseNtfcGetntfcsUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
                            @Override
                            public void onStart() {
                                waitDialog = ProgressDialog.show(StudentWelcomeActivity.this, "正在获取课堂信息", "请稍等…");
                                super.onStart();
                            }

                            @Override
                            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                                waitDialog.dismiss();
//                            view.setBackgroundResource(0);
                                Toast.makeText(StudentWelcomeActivity.this, "网络错误，请重试！", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(int i, Header[] headers, String s) {
                                waitDialog.dismiss();
                                SharedPreferences notificationsList_sp = getSharedPreferences("notificationsList", Context.MODE_PRIVATE);
                                SharedPreferences.Editor notificationsList_editor = notificationsList_sp.edit();
                                notificationsList_editor.putString("notificationsList", s);
                                notificationsList_editor.commit();
//                            view.setBackgroundResource(R.drawable.item_list_press);
                                startActivity(new Intent(StudentWelcomeActivity.this, StudentHomeActivity.class));
                                finish();
                            }
                        });
                    } else
                        Toast.makeText(StudentWelcomeActivity.this, "断开连接，请检查网络！", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
//            super.onBackPressed();
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "请再按一次以退出！", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_welcome, menu);
//        MenuItem menuItem = menu.findItem(R.id.action_search_class); //在菜单中找到对应控件的item
        MenuItem menuItem1 = menu.findItem(R.id.action_student_message);
        menuItem1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(StudentWelcomeActivity.this, StudentMessageActivity.class));
                return true;
            }
        });
        MenuItem menuItem2 = menu.findItem(R.id.action_student_personal_center);
        menuItem2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(StudentWelcomeActivity.this, StudentPersonalCenterActivity.class));
                return true;
            }
        });
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
//        searchView.setQueryHint("找课堂"); //设置搜索框内的hint文字
//        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() { //设置打开关闭动作监听
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                return true;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                return true;
//            }
//        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            startActivity(new Intent(StudentWelcomeActivity.this, SettingsActivity.class));
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

}
