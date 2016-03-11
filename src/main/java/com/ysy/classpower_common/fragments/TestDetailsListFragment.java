package com.ysy.classpower_common.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ysy.classpower.R;
import com.ysy.classpower_utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 姚圣禹 on 2016/2/5.
 */
public class TestDetailsListFragment extends Fragment {

    View view;
    private List<String> mDatas;
    private HomeAdapter mAdapter;

    public TestDetailsListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_test_details_list, container, false);

        LinearLayout testListLayout = (LinearLayout) view.findViewById(R.id.test_list_layout);

        //加载测试页面的RecyclerView（代替ListView）
        initData();
        final RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.id_recyclerview_test_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setAdapter(mAdapter = new HomeAdapter());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), //注意此处getActivity()的使用，因为是在Fragment里
                DividerItemDecoration.VERTICAL_LIST));
        //List当中Item的监听
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });

        final SwipeRefreshLayout testDetailsListSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.test_details_list_swipe_refresh_layout);
        testDetailsListSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        testDetailsListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                            }
                        });
                        testDetailsListSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        return view;
    }

    //初始化List数据（离线Demo）
    protected void initData() {
        mDatas = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            if (i == 0)
                mDatas.add("知识点1    题1");
            else
                mDatas.add("知识点1    题2");
        }
    }

    //刷新后的List数据（离线Demo）
    protected void changedData() {
        mDatas = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            if (i == 0)
                mDatas.add("知识点2    题1");
            else
                mDatas.add("知识点2    题2");
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
            return new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.test_item_test_details_list, parent,
                    false));
        }

        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.tv.setText(mDatas.get(position));
            if (position == 0)
                holder.tv.setBackgroundResource(R.drawable.item_doing_press);
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
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv;

            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.test_list_textView);
            }
        }
    }

}
