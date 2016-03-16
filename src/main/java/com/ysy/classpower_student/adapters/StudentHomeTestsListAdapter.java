package com.ysy.classpower_student.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.rey.material.widget.TextView;
import com.ysy.classpower.R;
import com.ysy.classpower_utils.ListOnItemClickListener;

import java.security.SecureRandom;
import java.util.List;

/**
 * Created by 姚圣禹 on 2016/3/15.
 */
public class StudentHomeTestsListAdapter extends UltimateViewAdapter<StudentHomeTestsListAdapter.SimpleAdapterViewHolder> {

    private List<String> beginsOnList;
    private List<String> expiresOnList;

    private ListOnItemClickListener mOnItemClickListener;

    public void setListOnItemClickListener(ListOnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public StudentHomeTestsListAdapter(List<String> beginsOnList, List<String> expiresOnList) {
        this.beginsOnList = beginsOnList;
        this.expiresOnList = expiresOnList;
    }

    @Override
    public StudentHomeTestsListAdapter.SimpleAdapterViewHolder getViewHolder(View view) {
        return new SimpleAdapterViewHolder(view, false);
    }

    @Override
    public StudentHomeTestsListAdapter.SimpleAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_item_student_home, parent, false);
        SimpleAdapterViewHolder vh = new SimpleAdapterViewHolder(v, true);
        return vh;
    }

    @Override
    public int getAdapterItemCount() {
        return beginsOnList.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(final StudentHomeTestsListAdapter.SimpleAdapterViewHolder holder, int position) {
        if (position < getItemCount() && (customHeaderView != null ? position <= beginsOnList.size() : position < beginsOnList.size()) && (customHeaderView != null ? position > 0 : true)) {

            holder.beginsOnTextView.setBackgroundColor(getRandomColor());
            holder.beginsOnTextView.setText(beginsOnList.get(customHeaderView != null ? position - 1 : position));
            holder.expiresOnTextView.setText(expiresOnList.get(customHeaderView != null ? position - 1 : position));
            holder.titleTextView.setText("测试" + (position + 1));

            // ((ViewHolder) holder).itemView.setActivated(selectedItems.get(position, false));
            if (mDragStartListener != null) {
//                ((ViewHolder) holder).imageViewSample.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
//                            mDragStartListener.onStartDrag(holder);
//                        }
//                        return false;
//                    }
//                });

                holder.item_view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
            }

            // 如果设置了回调，则设置点击事件
            if (mOnItemClickListener != null) {
                holder.item_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(holder.item_view, pos);
                    }
                });

                holder.item_view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemLongClick(holder.item_view, pos);
                        return false;
                    }
                });
            }

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    public class SimpleAdapterViewHolder extends UltimateRecyclerviewViewHolder {
        View item_view;
        TextView beginsOnTextView;
        TextView expiresOnTextView;
        android.widget.TextView titleTextView;

        public SimpleAdapterViewHolder(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                item_view = itemView.findViewById(R.id.student_home_tests_list_item_view);
                beginsOnTextView = (TextView) itemView.findViewById(R.id.student_home_tests_begins_on_textView);
                expiresOnTextView = (TextView) itemView.findViewById(R.id.student_home_tests_expires_on_textView);
                titleTextView = (android.widget.TextView) itemView.findViewById(R.id.student_home_tests_title_textView);
            }
        }
    }

    private int getRandomColor() {
        SecureRandom rgen = new SecureRandom();
        if (Color.HSVToColor(150, new float[]{
                rgen.nextInt(359), 1, 1
        }) == (R.color.orangered)) { // 置顶item的颜色为深红，且唯一
            return R.color.limegreen;
        } else {
            return Color.HSVToColor(150, new float[]{
                    rgen.nextInt(359), 1, 1
            });
        }
    }
}
