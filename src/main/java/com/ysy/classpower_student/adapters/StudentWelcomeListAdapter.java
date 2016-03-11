package com.ysy.classpower_student.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.ysy.classpower.R;
import com.ysy.classpower_utils.ListOnItemClickListener;

import java.security.SecureRandom;
import java.util.List;

/**
 * Created by 姚圣禹 on 2016/2/23.
 */
public class StudentWelcomeListAdapter extends UltimateViewAdapter<StudentWelcomeListAdapter.SimpleAdapterViewHolder> {

    private List<String> courseNameList;
    private List<String> teacherList;
    private List<String> dayList;
    private List<String> weekList;
    private List<String> periodList;
    private List<String> roomNameList;

    private ListOnItemClickListener mOnItemClickListener;
    public void setListOnItemClickListener(ListOnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public StudentWelcomeListAdapter(List<String> courseNameList, List<String> teacherList, List<String> dayList,
                                     List<String> weekList, List<String> periodList, List<String> roomNameList) {
        this.courseNameList = courseNameList;
        this.teacherList = teacherList;
        this.dayList = dayList;
        this.weekList = weekList;
        this.periodList = periodList;
        this.roomNameList = roomNameList;
    }

    @Override
    public void onBindViewHolder(final SimpleAdapterViewHolder holder, int position) {
        if (position < getItemCount() && (customHeaderView != null ? position <= courseNameList.size() : position < courseNameList.size()) && (customHeaderView != null ? position > 0 : true)) {

            holder.listItemDetailsLayout.setBackgroundColor(getRandomColor());
            holder.courseNameTextView.setText(courseNameList.get(customHeaderView != null ? position - 1 : position));
            holder.teachersTextView.setText(teacherList.get(customHeaderView != null ? position - 1 : position));
            holder.daysTextView.setText("星期" + dayList.get(customHeaderView != null ? position - 1 : position));
            holder.weeksTextView.setText(weekList.get(customHeaderView != null ? position - 1 : position));
            holder.periodTextView.setText(periodList.get(customHeaderView != null ? position - 1 : position));
            holder.roomNameTextView.setText(roomNameList.get(customHeaderView != null ? position - 1 : position));

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
    public int getAdapterItemCount() {
        return courseNameList.size();
    }

    @Override
    public SimpleAdapterViewHolder getViewHolder(View view) {
        return new SimpleAdapterViewHolder(view, false);
    }

    @Override
    public SimpleAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_welcome_list, parent, false);
        SimpleAdapterViewHolder vh = new SimpleAdapterViewHolder(v, true);
        return vh;
    }

    public void insert(String string, int position) {
        insertInternal(courseNameList, string, position);
    }

    public void remove(int position) {
        removeInternal(courseNameList, position);
    }

    public void clear() {
        clearInternal(courseNameList);
    }

    @Override
    public void toggleSelection(int pos) {
        super.toggleSelection(pos);
    }

    @Override
    public void setSelected(int pos) {
        super.setSelected(pos);
    }

    @Override
    public void clearSelection(int pos) {
        super.clearSelection(pos);
    }


    public void swapPositions(int from, int to) {
        swapPositions(courseNameList, from, to);
    }

    @Override
    public long generateHeaderId(int position) {
        // URLogs.d("position--" + position + "   " + getItem(position));
        if (getItem(position).length() > 0)
            return getItem(position).charAt(0);
        else return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_student_welcome_list_stick_head, viewGroup, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

//        TextView textView = (TextView) viewHolder.itemView.findViewById(R.id.student_welcome_list_stick_textView);
//        textView.setText(String.valueOf(getItem(position).charAt(0)));
//        viewHolder.itemView.setBackgroundColor(Color.parseColor("#AAffffff"));
//        ImageView imageView = (ImageView) viewHolder.itemView.findViewById(R.id.student_welcome_list_stick_imgView);
//
//        // 为Header的ImageView设置随机的图片资源
//        SecureRandom imgGen = new SecureRandom();
//        switch (imgGen.nextInt(3)) {
//            case 0:
//                imageView.setImageResource(R.drawable.ic_close_black_24dp);
//                break;
//            case 1:
//                imageView.setImageResource(R.drawable.ic_arrow_back_black_24dp);
//                break;
//            case 2:
//                imageView.setImageResource(R.drawable.ic_info_black_24dp);
//                break;
//        }

    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        swapPositions(fromPosition, toPosition);
//        notifyItemMoved(fromPosition, toPosition);
        super.onItemMove(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        if (position > 0) // 设置可以被滑动删除的列表项
            remove(position);
//        notifyItemRemoved(position);
//        notifyDataSetChanged();
        super.onItemDismiss(position);
    }

    private int getRandomColor() {
        SecureRandom rgen = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                rgen.nextInt(359), 1, 1
        });
    }

    public void setOnDragStartListener(OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;

    }

    public class SimpleAdapterViewHolder extends UltimateRecyclerviewViewHolder {
        View item_view;
        FrameLayout listItemDetailsLayout;
        TextView periodTextView;
        TextView courseNameTextView;
        TextView teachersTextView;
        TextView daysTextView;
        TextView weeksTextView;
        TextView roomNameTextView;
        ImageView imageViewSample;
        ProgressBar progressBarSample;

        public SimpleAdapterViewHolder(View itemView, boolean isItem) {
            super(itemView);
//            itemView.setOnTouchListener(new SwipeDismissTouchListener(itemView, null, new SwipeDismissTouchListener.DismissCallbacks() {
//                @Override
//                public boolean canDismiss(Object token) {
//                    Logs.d("can dismiss");
//                    return true;
//                }
//
//                @Override
//                public void onDismiss(View view, Object token) {
//                   // Logs.d("dismiss");
//                    remove(getPosition());
//
//                }
//            }));
            if (isItem) {
                item_view = itemView.findViewById(R.id.student_welcome_list_item_view);
                listItemDetailsLayout = (FrameLayout) itemView.findViewById(R.id.student_welcome_list_item_details_layout);

                periodTextView = (TextView) itemView.findViewById(R.id.student_welcome_list_textView_period);
                courseNameTextView = (TextView) itemView.findViewById(R.id.student_welcome_list_textView_course_name);
                teachersTextView = (TextView) itemView.findViewById(R.id.student_welcome_list_textView_teachers);
                daysTextView = (TextView) itemView.findViewById(R.id.student_welcome_list_textView_days);
                weeksTextView = (TextView) itemView.findViewById(R.id.student_welcome_list_textView_weeks);
                roomNameTextView = (TextView) itemView.findViewById(R.id.student_welcome_list_textView_room_name);
                imageViewSample = (ImageView) itemView.findViewById(R.id.student_welcome_list_imageView);

                progressBarSample = (ProgressBar) itemView.findViewById(R.id.student_welcome_list_progressbar);
                progressBarSample.setVisibility(View.GONE);
            }

        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

    }

    public String getItem(int position) {
        if (customHeaderView != null)
            position--;
        if (position < courseNameList.size())
            return courseNameList.get(position);
        else return "";
    }

}
