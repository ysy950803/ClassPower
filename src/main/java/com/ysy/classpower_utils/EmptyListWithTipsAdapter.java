package com.ysy.classpower_utils;

import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.ysy.classpower.R;

import java.security.SecureRandom;
import java.util.List;

/**
 * Created by 姚圣禹 on 2016/2/29.
 */
public class EmptyListWithTipsAdapter extends UltimateViewAdapter<EmptyListWithTipsAdapter.SimpleAdapterViewHolder> {

    private List<String> byList;

    public EmptyListWithTipsAdapter(List<String> byList) {
        this.byList = byList;
    }

    @Override
    public void onBindViewHolder(final SimpleAdapterViewHolder holder, int position) {
        if (position < getItemCount() && (customHeaderView != null ? position <= byList.size() : position < byList.size()) && (customHeaderView != null ? position > 0 : true)) {

            holder.byTextView.setText(byList.get(customHeaderView != null ? position - 1 : position));

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
        }

    }

    @Override
    public int getAdapterItemCount() {
        return byList.size();
    }

    @Override
    public SimpleAdapterViewHolder getViewHolder(View view) {
        return new SimpleAdapterViewHolder(view, false);
    }

    @Override
    public SimpleAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_empty_list, parent, false);
        SimpleAdapterViewHolder vh = new SimpleAdapterViewHolder(v, true);
        return vh;
    }

    public void insert(String string, int position) {
        insertInternal(byList, string, position);
    }

    public void remove(int position) {
        removeInternal(byList, position);
    }

    public void clear() {
        clearInternal(byList);
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
        swapPositions(byList, from, to);
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

    public void setOnDragStartListener(OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;

    }

    public class SimpleAdapterViewHolder extends UltimateRecyclerviewViewHolder {
        View item_view;
        TextView byTextView;

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
                item_view = itemView.findViewById(R.id.empty_list_tips_layout);
                byTextView = (TextView) itemView.findViewById(R.id.empty_list_tips_textView);
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
        if (position < byList.size())
            return byList.get(position);
        else return "";
    }

}
