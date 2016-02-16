package com.ysy.classpower_seatchoose.view;

import android.view.GestureDetector;
import android.view.MotionEvent;
import com.ysy.classpower_student.activities.home.StudentHomeActivity;
import java.util.ArrayList;

class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private SSView mSsView;

    GestureListener(SSView paramSSView) {
        mSsView = paramSSView;
    }

    public boolean onDoubleTap(MotionEvent paramMotionEvent) {
        return super.onDoubleTap(paramMotionEvent);
    }

    public boolean onDoubleTapEvent(MotionEvent paramMotionEvent) {
        return super.onDoubleTapEvent(paramMotionEvent);
    }

    public boolean onDown(MotionEvent paramMotionEvent) {
        return false;
    }

    public boolean onFling(MotionEvent paramMotionEvent1,
                           MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
        return false;
    }

    public void onLongPress(MotionEvent paramMotionEvent) {
    }

    public boolean onScroll(MotionEvent paramMotionEvent1,
                            MotionEvent paramMotionEvent2, float x_scroll_distance, float y_scroll_distance) {
        //是否可以移动和点击
        if (!SSView.a(mSsView)) {
            return false;
        }
        //显示缩略图
        SSView.a(mSsView, true);
        boolean bool1 = true;
        boolean bool2 = true;
        if ((SSView.s(mSsView) < mSsView.getMeasuredWidth())
                && (0.0F == SSView.v(mSsView))) {
            bool1 = false;
        }

        if ((SSView.u(mSsView) < mSsView.getMeasuredHeight())
                && (0.0F == SSView.w(mSsView))) {
            bool2 = false;
        }

        if (bool1) {
            int k = Math.round(x_scroll_distance);
            //修改排数x轴的偏移量
            SSView.c(mSsView, (float) k);
//			Log.i("TAG", SSView.v(mSsView)+"");
            //修改座位距离排数的横向距离
            SSView.k(mSsView, k);
//			Log.i("TAG", SSView.r(mSsView)+"");
            if (SSView.r(mSsView) < 0) {
                //滑到最左
                SSView.i(mSsView, 0);
                SSView.a(mSsView, 0.0F);
            }

            if (SSView.r(mSsView) + mSsView.getMeasuredWidth() > SSView.s(mSsView)) {
                //滑到最右
                SSView.i(mSsView, SSView.s(mSsView) - mSsView.getMeasuredWidth());
                SSView.a(mSsView, (float) (mSsView.getMeasuredWidth() - SSView.s(mSsView)));
            }
        }

        if (bool2) {
            //上负下正- 往下滑则减
            int j = Math.round(y_scroll_distance);
            //修改排数y轴的偏移量
            SSView.d(mSsView, (float) j);
            //修改可视座位距离顶端的距离
            SSView.l(mSsView, j);
//            Log.i("TAG", SSView.t(mSsView) + "");
            if (SSView.t(mSsView) < 0) {
                //滑到顶
                SSView.j(mSsView, 0);
                SSView.b(mSsView, 0.0F);
            }

            if (SSView.t(mSsView) + mSsView.getMeasuredHeight() > SSView
                    .u(mSsView)) {
                //滑到底
                SSView.j(mSsView, SSView.u(mSsView) - mSsView.getMeasuredHeight());
                SSView.b(mSsView, (float) (mSsView.getMeasuredHeight() - SSView.u(mSsView)));
            }
        }

        mSsView.invalidate();

//		Log.i("GestureDetector", "onScroll----------------------");
        return false;
    }

    public void onShowPress(MotionEvent paramMotionEvent) {
    }

    public boolean onSingleTapConfirmed(MotionEvent paramMotionEvent) {
        return false;
    }

    public static int previousI = -1, previousJ = -1;

    public boolean onSingleTapUp(MotionEvent paramMotionEvent) {
//		Log.i("GestureDetector", "onSingleTapUp");
//		if(!SSView.a(mSsView)){
//			return false;
//		}
        //列数
        int i = SSView.a(mSsView, (int) paramMotionEvent.getX());
        //排数
        int j = SSView.b(mSsView, (int) paramMotionEvent.getY());

        if (j >= 0 && j < SSView.b(mSsView).size()) {
            if (i >= 0 && i < ((ArrayList<Integer>) (SSView.b(mSsView).get(j))).size()) {

//                Log.i("TAG", "排数：" + j + "列数：" + i);
                ArrayList<Integer> localArrayList = (ArrayList<Integer>) SSView.b(mSsView).get(j);
                switch (localArrayList.get(i).intValue()) {
                    case 3://已选中
                        localArrayList.set(i, 1);
                        if (StudentHomeActivity.isSeatChooseOpen)
                            StudentHomeActivity.isSeatChooseEmpty = true;
                        if (SSView.d(mSsView) != null) {
                            SSView.d(mSsView).a(i, j, false);
                        }
                        break;
                    case 1://可选
//                        for (int n = 0; n < SSView.b(mSsView).size(); n++) { //清空之前已选
//                            for (int m = 0; m < ((ArrayList<Integer>) (SSView.b(mSsView).get(n))).size(); m++) {
//                                localArrayList = (ArrayList<Integer>) SSView.b(mSsView).get(n);
//                                localArrayList.set(m, Integer.valueOf(1));
//                                if (SSView.d(mSsView) != null) {
//                                    SSView.d(mSsView).a(m, n, false);
//                                }
//                            }
//                        }
                        //这一句与switch之前那句并不冲突，是重新获取坐标位置，否则总是最后一排有响应
//                        localArrayList = (ArrayList<Integer>) SSView.b(mSsView).get(j);
                        if (previousI == -1 && previousJ == -1) {
                            localArrayList.set(i, 3);
                            if (StudentHomeActivity.isSeatChooseOpen)
                                StudentHomeActivity.isSeatChooseEmpty = false;
                            if (SSView.d(mSsView) != null) {
                                SSView.d(mSsView).b(i, j, false);
                                previousI = i;
                                previousJ = j;
                            }
                        } else {
                            localArrayList = (ArrayList<Integer>) SSView.b(mSsView).get(previousJ);
                            localArrayList.set(previousI, 1);
                            if (SSView.d(mSsView) != null) {
                                SSView.d(mSsView).a(previousI, previousJ, false);
                            }
                            localArrayList = (ArrayList<Integer>) SSView.b(mSsView).get(j);
                            localArrayList.set(i, 3);
                            if (StudentHomeActivity.isSeatChooseOpen)
                                StudentHomeActivity.isSeatChooseEmpty = false;
                            if (SSView.d(mSsView) != null) {
                                SSView.d(mSsView).b(i, j, false);
                                previousI = i;
                                previousJ = j;
                            }
                        }
                        break;
                    case 2://给已锁定（即已有人）的座位设置可点击监听
                        if (previousI == -1 && previousJ == -1) {
                            if (StudentHomeActivity.isSeatChooseOpen)
                                StudentHomeActivity.isSeatChooseEmpty = false;
                            if (SSView.d(mSsView) != null) {
                                SSView.d(mSsView).b(i, j, false);
                            }
                        } else {
                            localArrayList = (ArrayList<Integer>) SSView.b(mSsView).get(previousJ);
                            localArrayList.set(previousI, 1);
                            if (SSView.d(mSsView) != null) {
                                SSView.d(mSsView).a(previousI, previousJ, false);
                            }
                            if (StudentHomeActivity.isSeatChooseOpen)
                                StudentHomeActivity.isSeatChooseEmpty = false;
                            if (SSView.d(mSsView) != null) {
                                SSView.d(mSsView).b(i, j, false);
                            }
                        }
                        break;
                    default:
                        break;
                }

            }
        }

        //显示缩略图
        SSView.a(mSsView, true);
        mSsView.invalidate();
        return false;
    }

}