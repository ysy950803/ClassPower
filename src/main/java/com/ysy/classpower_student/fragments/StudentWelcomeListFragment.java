package com.ysy.classpower_student.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.RecyclerItemClickListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.ysy.classpower.R;
import com.ysy.classpower_student.activities.home.StudentHomeActivity;
import com.ysy.classpower_student.adapters.StudentWelcomeListAdapter;
import com.ysy.classpower_utils.ReadJsonByGson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 姚圣禹 on 2016/2/5.
 */
public class StudentWelcomeListFragment extends Fragment {

    View view;
    private FloatingActionButton addFab;
    private List<String> courseNameData;
    private List<String> teacherData;
    private List<String> dayData;
    private StudentWelcomeListAdapter listAdapter = null;
    private LinearLayoutManager linearLayoutManager;
    private UltimateRecyclerView ultimateRecyclerView;
    private ItemTouchHelper mItemTouchHelper;

    public StudentWelcomeListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_student_welcome_list, container, false);

        initData();

        listAdapter = new StudentWelcomeListAdapter(courseNameData, teacherData, dayData);
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
                        //   ultimateRecyclerView.scrollBy(0, -50);
//                        linearLayoutManager.scrollToPosition(0);
//                        ultimateRecyclerView.setAdapter(RVAdapter);
//                        RVAdapter.notifyDataSetChanged();
                    }
                }, 1000);
            }
        });
        ultimateRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                view.setBackgroundResource(R.drawable.item_list_press);
                startActivity(new Intent(getActivity(), StudentHomeActivity.class));
                getActivity().finish();
            }
        }));

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

        addFab = (FloatingActionButton) getActivity().findViewById(R.id.student_welcome_add_fab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    //初始化List数据
    protected void initData() {
        SharedPreferences loginJson_sp = getContext().getSharedPreferences("loginJson", Context.MODE_PRIVATE);
        ReadJsonByGson jsonByGson = new ReadJsonByGson(loginJson_sp.getString("loginJson", "{}"));
        courseNameData = new ArrayList<>();
        teacherData = new ArrayList<>();
        dayData = new ArrayList<>();
        String[] courseNameInfo = jsonByGson.getCoursesBasicInfo("course_name");
        String[] teacherInfo = jsonByGson.getCoursesTeachersInfo();
        String[] dayInfo = jsonByGson.getCoursesTimesDaysInfo("1");
        Collections.addAll(courseNameData, courseNameInfo);
        Collections.addAll(teacherData, teacherInfo);
        Collections.addAll(dayData, dayInfo);
    }

}
