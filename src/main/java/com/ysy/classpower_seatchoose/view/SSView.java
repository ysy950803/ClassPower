package com.ysy.classpower_seatchoose.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.ysy.classpower.R;
import com.ysy.classpower_seatchoose.OnSeatClickListener;
import com.ysy.classpower_seatchoose.model.Seat;
import com.ysy.classpower_seatchoose.model.SeatInfo;

import java.util.ArrayList;

public class SSView extends View {
    Context mContext;
    int x_offset = 0;

    /**
     * 普通状态
     */
    private Bitmap mBitMapSeatNormal = null;
    /**
     * 已锁定
     */
    private Bitmap mBitMapSeatLock = null;
    /**
     * 已选中
     */
    private Bitmap mBitMapSeatChecked = null;

    /**
     * 缩略图画布
     */
    private Canvas mCanvas = null;

    /**
     * 是否显示缩略图
     */
    private boolean U = false;

    /**
     * 每个座位的高度 - 57
     */
    private int ss_seat_current_height = 57;
    /**
     * 每个座位的宽度
     */
    private int ss_seat_current_width = 57;
    /**
     * 座位之间的间距
     */
    private int L = 5;
    private double T = 1.0D;

    private double t = -1.0D;
    private double u = 1.0D;
    /**
     * 是否可缩放
     */
    private boolean v = false;

    /**
     * 座位最小高度
     */
    private int ss_seat_min_height = 0;
    /**
     * 座位最大高度
     */
    private int ss_seat_max_height = 0;
    /**
     * 座位最小宽度
     */
    private int ss_seat_min_width = 0;
    /**
     * 座位最大宽度
     */
    private int ss_seat_max_width = 0;

    private OnSeatClickListener mOnSeatClickListener = null;

    public static double a = 1.0E-006D;
    private int I = 0;
    private int ss_between_offset = 2;
    private int ss_seat_check_size = 50;
    private SSThumbView mSSThumbView = null;
    private int ss_seat_thumb_size_w = 120;
    private int ss_seat_thumb_size_h = 90;
    private int ss_seat_rect_line = 2;
    /**
     * 选座缩略图
     */
    private Bitmap mBitMapThumbView = null;
    private volatile int V = 1500;
    /**
     * 左边距
     */
    private int h = 0;
    /**
     * 右边距
     */
    private int i = 0;
    /**
     * 上边距
     */
    private int j = 0;
    /**
     * 下边距
     */
    private int k = 0;
    /**
     * 排数x轴偏移量
     */
    private float n = 0.0F;
    /**
     * 排数y轴偏移量
     */
    private float o = 0.0F;
    /**
     * 座位距离排数的距离
     */
    private int p = 0;
    /**
     * 可视座位距离顶端的距离
     */
    private int q = 0;
    /**
     * 整个view的宽度
     */
    private int r = 0;
    /**
     * 整个view的高度
     */
    private int s = 0;
    /**
     * 能否移动
     */
    private boolean w = true;

    private boolean first_load_bg = true;
    private int tempX;
    private int tempY;

    GestureDetector mGestureDetector = new GestureDetector(mContext,
            new GestureListener(this));

    private ArrayList<SeatInfo> mListSeatInfo = null;
    private ArrayList<ArrayList<Integer>> mListSeatConditions = null;
    private int iMaxPay = 0;
    private int totalCountEachRow;
    private int rows;

    public SSView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public SSView(Context paramContext, AttributeSet paramAttributeSet,
                  int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        this.mContext = paramContext;
    }

    public void init(int row_count, int rows,
                     ArrayList<SeatInfo> list_seatInfo,
                     ArrayList<ArrayList<Integer>> list_seat_conditions,
                     SSThumbView paramSSThumbView, int iMaxPay) {
        this.iMaxPay = iMaxPay;
        this.mSSThumbView = paramSSThumbView;
        this.totalCountEachRow = row_count;
        this.rows = rows;
        this.mListSeatInfo = list_seatInfo;
        this.mListSeatConditions = list_seat_conditions;
        this.mBitMapSeatNormal = getBitmapFromDrawable((BitmapDrawable) this.mContext
                .getResources().getDrawable(R.drawable.seat_normal));
        this.mBitMapSeatLock = getBitmapFromDrawable((BitmapDrawable) this.mContext
                .getResources().getDrawable(R.drawable.seat_lock));
        this.mBitMapSeatChecked = getBitmapFromDrawable((BitmapDrawable) this.mContext
                .getResources().getDrawable(R.drawable.seat_checked));

        this.ss_seat_thumb_size_w = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.ss_seat_thum_size_w);
        this.ss_seat_thumb_size_h = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.ss_seat_thum_size_h);

        this.ss_seat_max_height = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.seat_max_height);
        this.ss_seat_max_width = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.seat_max_width);
        this.ss_seat_min_height = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.seat_min_height);
        this.ss_seat_min_width = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.seat_min_width);
        this.ss_seat_current_height = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.seat_init_height);
        this.ss_seat_current_width = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.seat_init_width);
        this.ss_seat_check_size = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.ss_seat_check_size);//30dp
        this.ss_between_offset = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.ss_between_offset);//5dp
        invalidate();
    }

    public static Bitmap getBitmapFromDrawable(
            BitmapDrawable paramBitmapDrawable) {
        return paramBitmapDrawable.getBitmap();
    }

    /**
     * @param seatNum      每排的座位顺序号
     * @param rowNum       排号
     * @param paramBitmap
     * @param paramCanvas1
     * @param paramCanvas2
     * @param paramPaint
     */
    private void a(int seatNum, int rowNum, Bitmap paramBitmap,
                   Canvas paramCanvas1, Canvas paramCanvas2, Paint paramPaint) {
        if (paramBitmap == null) {// 走道
            paramCanvas1.drawRect(c(seatNum, rowNum), paramPaint);
            if (this.U) {
                paramCanvas2.drawRect(d(seatNum, rowNum), paramPaint);
            }
        } else {
            paramCanvas1.drawBitmap(paramBitmap, null, c(seatNum, rowNum),
                    paramPaint);
            if (this.U) {
                paramCanvas2.drawBitmap(paramBitmap, null, d(seatNum, rowNum),
                        paramPaint);
            }
        }
    }

    /**
     * @param seatNum 每排的座位号
     * @param rowNum  排号
     * @return
     */
    private Rect c(int seatNum, int rowNum) {
        try {
            return new Rect(this.h + seatNum * this.ss_seat_current_width + this.L,
                    this.j + rowNum * this.ss_seat_current_height + this.L, this.h + (seatNum + 1)
                    * this.ss_seat_current_width - this.L, this.j + (rowNum + 1) * this.ss_seat_current_height
                    - this.L);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return new Rect();
    }

    private Rect d(int seatNum, int rowNum) {
        try {
            return new Rect(
                    5 + (int) (this.T * (this.h + seatNum * this.ss_seat_current_width + this.L)),
                    5 + (int) (this.T * (this.j + rowNum * this.ss_seat_current_height + this.L)),
                    5 + (int) (this.T * (this.h + (seatNum + 1) * this.ss_seat_current_width - this.L)),
                    5 + (int) (this.T * (this.j + (rowNum + 1) * this.ss_seat_current_height - this.L)));
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return new Rect();
    }

    private Rect e(int paramInt1, int paramInt2) {
        int i1;
        int i3;
        try {
            if (getMeasuredWidth() < this.r) {
                i1 = getMeasuredWidth();
            } else {
                i1 = this.r;
            }
            if (getMeasuredHeight() < this.s) {
                i3 = getMeasuredHeight();
            } else {
                i3 = this.s;
            }
            return new Rect((int) (5.0D + this.T * paramInt1),
                    (int) (5.0D + this.T * paramInt2), (int) (5.0D + this.T
                    * paramInt1 + i1 * this.T), (int) (5.0D + this.T
                    * paramInt2 + i3 * this.T));

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return new Rect();
        }
    }

    @Override
    protected void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        // Log.i("TAG", "onDraw()...");
        if (this.totalCountEachRow == 0 || this.rows == 0) {
            return;
        }

        if (this.n + this.r < 0.0f || this.o + this.s < 0.0f) {
            this.n = 0.0f;
            this.o = 0.0f;
            this.p = 0;
            this.q = 0;
        }
        Paint localPaint2 = new Paint();
        if (this.ss_seat_current_width != 0 && this.ss_seat_current_height != 0) {

            this.mBitMapThumbView = Bitmap.createBitmap(this.ss_seat_thumb_size_w,
                    this.ss_seat_thumb_size_h, Bitmap.Config.ARGB_8888);
            this.mCanvas = new Canvas();
            this.mCanvas.setBitmap(this.mBitMapThumbView);
            this.mCanvas.save();

            Paint localPaint1 = new Paint();
            localPaint1.setXfermode(new PorterDuffXfermode(
                    PorterDuff.Mode.CLEAR));
            this.mCanvas.drawPaint(localPaint1);

            double d1 = (this.ss_seat_thumb_size_w - 10.0D)
                    / (this.ss_seat_current_width * this.totalCountEachRow + this.h + this.i); // -
            // v0/v2
            double d2 = (this.ss_seat_thumb_size_h - 10.0D)
                    / (this.ss_seat_current_height * this.rows);
            if (d1 <= d2) {
                this.T = d1;
            } else {
                this.T = d2;
            }
            if (this.U) {
                localPaint2.setColor(-16777216);
                if (first_load_bg) {
                    first_load_bg = false;
                    tempX = 5 + (int) (this.r * this.T);
                    tempY = 5 + (int) (this.s * this.T);
                }
                this.mCanvas.drawRect(5.0F, 5.0F, tempX,
                        tempY, localPaint2);
            }
        }

        paramCanvas.translate(this.n, this.o);
        this.r = this.h + this.ss_seat_current_width * this.totalCountEachRow + this.i;
        this.s = this.ss_seat_current_height * this.rows;

        this.h = (int) Math.round(this.ss_seat_current_width / 2.0D);
        localPaint2.setTextAlign(Paint.Align.CENTER);
        localPaint2.setAntiAlias(true);
        localPaint2.setColor(-16777216);
        for (int i2 = 0; i2 < this.mListSeatConditions.size(); i2++) {
            ArrayList<Integer> localArrayList = this.mListSeatConditions
                    .get(i2);

            for (int i3 = 0; i3 < this.mListSeatInfo.get(i2).getSeatList()
                    .size(); i3++) {// 2344
                // goto5 - 2344
//                Seat localSeat = this.mListSeatInfo.get(i2).getSeat(i3);
                switch (localArrayList.get(i3)) { // 2373
                    case 0: // 2401 - 走道
                        localPaint2.setColor(0);
                        a(i3, i2, null, paramCanvas, this.mCanvas, localPaint2);
                        localPaint2.setColor(-16777216);
                        break;
                    case 1:// 可选
                        a(i3, i2, this.mBitMapSeatNormal, paramCanvas,
                                this.mCanvas, localPaint2);
                        break;
                    case 2://
                        a(i3, i2, this.mBitMapSeatLock, paramCanvas,
                                this.mCanvas, localPaint2);
                        break;
                    case 3: // 2500-一已点击的状态
                        a(i3, i2, this.mBitMapSeatChecked, paramCanvas,
                                this.mCanvas, localPaint2);
                        break;
                    default:
                        break;
                }
            }
            // cond_d - 2538
        }

        // 画排数
        localPaint2.setTextSize(0.4F * this.ss_seat_current_height);
        for (int i1 = 0; i1 < this.mListSeatInfo.size(); i1++) {
            localPaint2.setColor(-1308622848);
            paramCanvas.drawRect(new Rect((int) Math.abs(this.n), this.j
                    + i1 * this.ss_seat_current_height, (int) Math.abs(this.n) + this.ss_seat_current_width / 2,
                    this.j + (i1 + 1) * this.ss_seat_current_height), localPaint2);
            localPaint2.setColor(-1);
            paramCanvas
                    .drawText(this.mListSeatInfo.get(i1)
                            .getDesc(), (int) Math.abs(this.n) + this.ss_seat_current_width / 2
                            / 2, this.j + i1 * this.ss_seat_current_height + this.ss_seat_current_height / 2 + this.k
                            / 2, localPaint2);
        }


        if (this.U) {
            // 画缩略图的黄色框
            localPaint2.setColor(-739328);
            localPaint2.setStyle(Paint.Style.STROKE);
            localPaint2.setStrokeWidth(this.ss_seat_rect_line);
            this.mCanvas.drawRect(
                    e((int) Math.abs(this.n), (int) Math.abs(this.o)),
                    localPaint2);
            localPaint2.setStyle(Paint.Style.FILL);
            // paramCanvas.restore();
            this.mCanvas.restore();
        }

        if (this.mSSThumbView != null) {
            this.mSSThumbView.a(mBitMapThumbView);
            this.mSSThumbView.invalidate();
        }

    }

    public void setXOffset(int x_offset) {
        this.x_offset = x_offset;
    }

    /**
     * 获取两点的直线距离
     *
     * @param paramMotionEvent
     * @return
     */
    private double getTwoPoiniterDistance(MotionEvent paramMotionEvent) {
        float f1 = paramMotionEvent.getX(0) - paramMotionEvent.getX(1);
        float f2 = paramMotionEvent.getY(0) - paramMotionEvent.getY(1);
        return Math.sqrt(f1 * f1 + f2 * f2);
    }

    //多点触控
    private void a(MotionEvent paramMotionEvent) {
        double d1 = getTwoPoiniterDistance(paramMotionEvent);
        if (this.t < 0.0D) {
            this.t = d1;
        } else {
            try {
                this.u = (d1 / this.t);
                this.t = d1;
                if ((this.v) && (Math.round(this.u * this.ss_seat_current_width) > 0L)
                        && (Math.round(this.u * this.ss_seat_current_height) > 0L)) {
                    this.ss_seat_current_width = (int) Math.round(this.u * this.ss_seat_current_width);
                    this.ss_seat_current_height = (int) Math.round(this.u * this.ss_seat_current_height);
                    this.h = (int) Math.round(this.ss_seat_current_width / 2.0D);
                    this.i = this.h;
//					this.L = (int) Math.round(this.u * this.L);
//					this.L = 5;
//					if (this.L <= 0)
//						this.L = 1;
                }
                invalidate();
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
    }

    /**
     * new added
     *
     * @return
     */
    public static int m(SSView mSsView) {
        return mSsView.h;
    }

    /**
     * new added
     *
     * @param mSsView
     * @param paramInt
     * @return
     */
    public static int m(SSView mSsView, int paramInt) {
        mSsView.V = mSsView.V - paramInt;
        return mSsView.V;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static int x(SSView mSsView) {
        return mSsView.V;
    }

    /**
     * new added
     *
     * @param mSsView
     */
    public static void y(SSView mSsView) {
        mSsView.a();
    }

    private void a() {
        // postDelayed(new ag(this), 500L);
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static float w(SSView mSsView) {
        return mSsView.o;
    }

    /**
     * 获取排数x轴偏移量
     *
     * @param mSsView
     * @return
     */
    public static float v(SSView mSsView) {
        return mSsView.n;
    }

    /**
     * 获取整个view的高度
     *
     * @param mSsView
     * @return
     */
    public static int u(SSView mSsView) {
        return mSsView.s;
    }

    /**
     * 获取可视座位距离顶端的距离
     *
     * @param mSsView
     * @return
     */
    public static int t(SSView mSsView) {
        return mSsView.q;
    }

    /**
     * 获取整个view的宽度
     *
     * @param mSsView
     * @return
     */
    public static int s(SSView mSsView) {
        return mSsView.r;
    }

    /**
     * 获取座位距离排数的横向距离
     *
     * @param mSsView
     * @return
     */
    public static int r(SSView mSsView) {
        return mSsView.p;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static int q(SSView mSsView) {
        return mSsView.j;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static int p(SSView mSsView) {
        return mSsView.rows;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static int o(SSView mSsView) {
        return mSsView.i;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static int n(SSView mSsView) {
        return mSsView.totalCountEachRow;
    }

    /**
     * 修改可见座位距离顶端的距离
     *
     * @param mSsView
     * @return
     */
    public static int l(SSView mSsView, int paramInt) {
        mSsView.q = mSsView.q + paramInt;
        return mSsView.q;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static int l(SSView mSsView) {
        return mSsView.ss_seat_current_width;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static int k(SSView mSsView) {
        return mSsView.ss_seat_current_width;
    }

    /**
     * new added
     *
     * @param mSsView
     * @param paramInt
     * @return
     */
    public static int k(SSView mSsView, int paramInt) {
        mSsView.p = mSsView.p + paramInt;
        return mSsView.p;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static int j(SSView mSsView) {
        return mSsView.ss_seat_current_height;
    }

    /**
     * 设置可视座位距离顶端的距离
     *
     * @param mSsView
     * @param paramInt
     * @return
     */
    public static int j(SSView mSsView, int paramInt) {
        mSsView.q = paramInt;
        return mSsView.q;
    }

    /**
     * 设置座位距离排数的横向距离
     *
     * @param mSsView
     * @return
     */
    public static int i(SSView mSsView, int paramInt) {
        mSsView.p = paramInt;
        return mSsView.p;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static boolean i(SSView mSsView) {
        return mSsView.U;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static int h(SSView mSsView, int paramInt) {
        return mSsView.s;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static int h(SSView mSsView) {
        return mSsView.I + 1;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static int g(SSView mSsView, int paramInt) {
        return mSsView.r;
    }

    /**
     * 获取最大支付座位数
     *
     * @param mSsView
     * @return
     */
    public static int getImaxPay(SSView mSsView) {
        return mSsView.iMaxPay;
    }

    /**
     * new added
     *
     * @param mSsView
     * @param param
     * @return
     */
    public static boolean a(SSView mSsView, boolean param) {
        mSsView.U = param;
        return mSsView.U;
    }

    /**
     * 设置排数x轴偏移量
     *
     * @param mSsView
     * @param param
     * @return
     */
    public static float a(SSView mSsView, float param) {
        mSsView.n = param;
        return mSsView.n;
    }

    /**
     * 计算是第几列
     *
     * @param mSsView
     * @param param
     * @return
     */
    public static int a(SSView mSsView, int param) {
        return mSsView.a(param);
    }

    /**
     * 计算是第几列
     *
     * @param paramInt
     * @return
     */
    private int a(int paramInt) {
        try {
            return (paramInt + this.p - this.h) / this.ss_seat_current_width;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return -1;
    }

    /**
     * new added
     *
     * @param mSsView
     * @param param1
     * @param param2
     * @return
     */
    public static Rect a(SSView mSsView, int param1, int param2) {
        return mSsView.f(param1, param2);
    }

    private Rect f(int paramInt1, int paramInt2) {
        try {
            int v1 = this.ss_seat_current_width * paramInt1 + this.h - this.p - this.L;
            int v2 = this.ss_seat_current_height * paramInt2 + this.j - this.q - this.L;
            int v3 = (paramInt1 + 1) * this.ss_seat_current_width + this.h - this.p + this.L;
            int v4 = (this.j + 1) * this.ss_seat_current_height + this.j - this.q + this.L;
            return new Rect(v1, v2, v3, v4);
        } catch (Exception e) {
            e.printStackTrace();
            return new Rect();
        }
    }

    /**
     * 是否可以移动和点击
     *
     * @param mSsView
     * @return
     */
    public static boolean a(SSView mSsView) {
        return mSsView.w;
    }

    private int b() {
        return Math.round(this.ss_seat_current_width / this.ss_seat_check_size
                * this.ss_between_offset);
    }

    /**
     * 修改排数x轴的
     *
     * @param mSsView
     * @param param
     * @return
     */
    public static float c(SSView mSsView, float param) {
        mSsView.n = mSsView.n - param;
        return mSsView.n;
    }

    /**
     * 设置每个座位的高度
     *
     * @param mSsView
     * @param param
     * @return
     */
    public static float c(SSView mSsView, int param) {
        mSsView.ss_seat_current_height = param;
        return mSsView.ss_seat_current_height;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static ArrayList c(SSView mSsView) {
        return mSsView.mListSeatInfo;
    }

    /**
     * 修改排数y轴的偏移量
     *
     * @param mSsView
     * @param param
     * @return
     */
    public static float d(SSView mSsView, float param) {
        mSsView.o = mSsView.o - param;
        return mSsView.o;
    }

    /**
     * new added
     *
     * @param mSsView
     * @param param
     * @return
     */
    public static int d(SSView mSsView, int param) {
        mSsView.ss_seat_current_width = param;
        return mSsView.ss_seat_current_width;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static OnSeatClickListener d(SSView mSsView) {
        return mSsView.mOnSeatClickListener;
    }

    /**
     * 设置排数y轴偏移量
     *
     * @param mSsView
     * @param param
     * @return
     */
    public static float b(SSView mSsView, float param) {
        mSsView.o = param;
        return mSsView.o;
    }

    /**
     * 计算是第几排
     *
     * @param mSsView
     * @param param
     * @return
     */
    public static int b(SSView mSsView, int param) {
        return mSsView.b(param);
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static ArrayList b(SSView mSsView) {
        return mSsView.mListSeatConditions;
    }

    /**
     * 计算是第几排
     *
     * @param paramInt
     * @return
     */
    private int b(int paramInt) {
        try {
            return (paramInt + this.q - this.j) / this.ss_seat_current_height;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return -1;
    }

    /**
     * new added
     *
     * @param mSsView
     * @param param
     * @return
     */
    public static int e(SSView mSsView, int param) {
        mSsView.h = param;
        return mSsView.h;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static int e(SSView mSsView) {
        mSsView.I--;
        return mSsView.I;
    }

    /**
     * new added
     *
     * @param mSsView
     * @return
     */
    public static int f(SSView mSsView) {
        return mSsView.I;
    }

    /**
     * new added
     *
     * @param mSsView
     * @param param
     * @return
     */
    public static int f(SSView mSsView, int param) {
        mSsView.i = param;
        return mSsView.i;
    }

    /**
     * 设置按钮点击事件
     *
     * @param paramOnSeatClickLinstener
     */
    public void setOnSeatClickListener(
            OnSeatClickListener paramOnSeatClickLinstener) {
        this.mOnSeatClickListener = paramOnSeatClickLinstener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getPointerCount() == 1) {
            if (this.v) {
                this.v = false;
                this.w = false;
                this.t = -1.0D;
                this.u = 1.0D;
            } else {
                this.w = true;
            }

            // Toast.makeText(mContext, "单点触控", Toast.LENGTH_SHORT).show();
            while (this.ss_seat_current_width < this.ss_seat_min_width || this.ss_seat_current_height < this.ss_seat_min_height) {
                this.ss_seat_current_width++;
                this.ss_seat_current_height++;
                this.h = (int) Math.round(this.ss_seat_current_width / 2.0D);
                this.i = this.h;
//				this.L = b();
                this.L = 5;
                // 滑到最左和最上
                SSView.i(this, 0);
                SSView.a(this, 0.0F);
                SSView.j(this, 0);
                SSView.b(this, 0.0F);
                invalidate();
            }
            while ((this.ss_seat_current_width > this.ss_seat_max_width) || (this.ss_seat_current_height > this.ss_seat_max_height)) {
                this.ss_seat_current_width--;
                this.ss_seat_current_height--;
                this.h = (int) Math.round(this.ss_seat_current_width / 2.0D);
                this.i = this.h;
//				this.L = b();
                this.L = 5;
                invalidate();
            }

            // 移动功能-点击事件
            this.mGestureDetector.onTouchEvent(event);
        } else {
//            if (this.ss_seat_current_width > this.ss_seat_max_width && this.ss_seat_current_height > this.ss_seat_max_height
//                    || this.ss_seat_current_height < this.ss_seat_min_height && this.ss_seat_current_width < this.ss_seat_min_width) {
//                this.v = false;
//                invalidate();
//            } else {
            // Toast.makeText(mContext, "多点触控", Toast.LENGTH_SHORT).show();
            this.v = true; //允许缩放
            a(event);
//            }
        }
        return true;
    }

}
