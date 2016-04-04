package com.ysy.classpower_student.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.ysy.classpower.R;
import com.ysy.classpower_common.constant.ServerUrlConstant;
import com.ysy.classpower_student.activities.home.StudentHomeActivity;
import com.ysy.classpower_student.adapters.StudentWelcomeListAdapter;
import com.ysy.classpower_utils.ConnectionDetector;
import com.ysy.classpower_utils.ListOnItemClickListener;
import com.ysy.classpower_utils.OwnApp;
import com.ysy.classpower_utils.json_processor.PostJsonAndGetCallback;
import com.ysy.classpower_utils.json_processor.ReadJsonByGson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 姚圣禹 on 2016/2/5.
 */
public class StudentWelcomeListFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    View view;
    private String token;
    private List<String> courseNameData;
    private List<String> courseIdData;
    private List<String> subIdData;
    private List<String> teacherData;
    private List<String> dayData;
    private List<String> weekData;
    private List<String> periodData;
    private List<String> roomNameData;
    private List<String> roomIdData;

    private StudentWelcomeListAdapter listAdapter = null;
    private LinearLayoutManager linearLayoutManager;
    private UltimateRecyclerView ultimateRecyclerView;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private OwnApp ownApp;

    private ProgressDialog waitDialog;

    public StudentWelcomeListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_student_welcome_list, container, false);
        SharedPreferences token_sp = getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
        token = token_sp.getString("token", "");

        ownApp = (OwnApp) getActivity().getApplication();

        initData();

        listAdapter = new StudentWelcomeListAdapter(courseNameData, teacherData, dayData, weekData, periodData, roomNameData);
        linearLayoutManager = new LinearLayoutManager(getContext());

        ultimateRecyclerView = (UltimateRecyclerView) view.findViewById(R.id.student_welcome_list_urv);
        ultimateRecyclerView.setLayoutManager(linearLayoutManager);
        ultimateRecyclerView.setAdapter(listAdapter);
        ultimateRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        RVAdapter.insert(moreNum++ + "  Refresh things", 0);
                        ultimateRecyclerView.setRefreshing(false);
//                        ultimateRecyclerView.scrollBy(0, -50);
//                        linearLayoutManager.scrollToPosition(0);
//                        ultimateRecyclerView.setAdapter(listAdapter);
//                        listAdapter.notifyDataSetChanged();
                        ultimateRecyclerView.setAdapter(listAdapter);
                    }
                }, 1000);
            }
        });
        listAdapter.setListOnItemClickListener(new ListOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ConnectionDetector cd = new ConnectionDetector(getContext());
                if (cd.isConnectingToInternet()) {
                    // 点击列表项时记录各种id，跳转后使用
                    SharedPreferences courseId_sp = getContext().getSharedPreferences("courseId", Context.MODE_PRIVATE);
                    SharedPreferences subId_sp = getContext().getSharedPreferences("subId", Context.MODE_PRIVATE);
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
                    new PostJsonAndGetCallback(new AsyncHttpClient(), getContext(), ServerUrlConstant.getCourseNtfcGetntfcsUrl(ownApp.getURL_FIGURE()), json, new TextHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            waitDialog = ProgressDialog.show(getActivity(), "正在获取课堂信息", "请稍等…");
                            super.onStart();
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                            waitDialog.dismiss();
//                            view.setBackgroundResource(0);
                            Toast.makeText(getContext(), "网络错误，请重试！", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            waitDialog.dismiss();
                            SharedPreferences notificationsList_sp = getContext().getSharedPreferences("notificationsList", Context.MODE_PRIVATE);
                            SharedPreferences.Editor notificationsList_editor = notificationsList_sp.edit();
                            notificationsList_editor.putString("notificationsList", s);
                            notificationsList_editor.commit();
//                            view.setBackgroundResource(R.drawable.item_list_press);
                            startActivity(new Intent(getActivity(), StudentHomeActivity.class));
                            getActivity().finish();
                        }
                    });
                } else
                    Toast.makeText(getContext(), "断开连接，请检查网络！", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

//        // 设置ListHeader装饰，相关内容在Adapter的onBindHeaderViewHolder方法中
//        StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(listAdapter);
//        ultimateRecyclerView.addItemDecoration(headersDecor);
//
//        // 设置item的滑动消除、移动效果
//        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(listAdapter);
//        mItemTouchHelper = new ItemTouchHelper(callback);
//        mItemTouchHelper.attachToRecyclerView(ultimateRecyclerView.mRecyclerView);
//        listAdapter.setOnDragStartListener(new StudentWelcomeListAdapter.OnStartDragListener() {
//            @Override
//            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
//                mItemTouchHelper.startDrag(viewHolder);
//            }
//        });

        return view;
    }

    // 初始化List数据
    protected void initData() {
        SharedPreferences currentWeek_sp = getContext().getSharedPreferences("currentWeek", Context.MODE_PRIVATE);
        String currentWeek = currentWeek_sp.getString("currentWeek", "1");
        SharedPreferences loginJson_sp = getContext().getSharedPreferences("loginJson", Context.MODE_PRIVATE);
        ReadJsonByGson jsonByGson = new ReadJsonByGson(loginJson_sp.getString("loginJson", "{}"));
        courseNameData = new ArrayList<>();
        courseIdData = new ArrayList<>();
        subIdData = new ArrayList<>();
        teacherData = new ArrayList<>();
        dayData = new ArrayList<>();
        weekData = new ArrayList<>();
        periodData = new ArrayList<>();
        roomNameData = new ArrayList<>();
        roomIdData = new ArrayList<>();
        String[] courseNameInfo = jsonByGson.getCoursesBasicInfo("course_name");
        String[] courseIdInfo = jsonByGson.getCoursesBasicInfo("course_id");
        String[] subIdInfo = jsonByGson.getCoursesBasicInfo("sub_id");
        String[] teacherInfo = jsonByGson.getCoursesTeachersInfo();
        String[] dayInfo = jsonByGson.getCoursesTimesDaysInfo(currentWeek); // 参数：week
        String[] weekInfo = jsonByGson.getCoursesTimesWeeksInfo();
        String[] periodInfo = jsonByGson.getCoursesTimesPeriodInfo(currentWeek, "1");
        String[] roomNameInfo = jsonByGson.getCoursesTimesRoomNameInfo(currentWeek, "1");
        String[] roomIdInfo = jsonByGson.getCoursesTimesRoomIdInfo(currentWeek, "1");
        Collections.addAll(courseNameData, courseNameInfo);
        Collections.addAll(courseIdData, courseIdInfo);
        Collections.addAll(subIdData, subIdInfo);
        Collections.addAll(teacherData, teacherInfo);
        Collections.addAll(dayData, dayInfo);
        Collections.addAll(weekData, weekInfo);
        Collections.addAll(periodData, periodInfo);
        Collections.addAll(roomNameData, roomNameInfo);
        Collections.addAll(roomIdData, roomIdInfo);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//        if (collapsingToolbarLayout.getHeight() + verticalOffset <  2 * ViewCompat.getMinimumHeight(collapsingToolbarLayout))
//            ultimateRecyclerView.enableDefaultSwipeRefresh(false);
//        else
//            ultimateRecyclerView.enableDefaultSwipeRefresh(true);
    }

//    @Override
//    public void onResume() {
//        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbar);
//        collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
//        appBarLayout.addOnOffsetChangedListener(this);
//        super.onResume();
//    }

//    @Override
//    public void onPause() {
//        appBarLayout.removeOnOffsetChangedListener(this);
//        super.onPause();
//    }

}
