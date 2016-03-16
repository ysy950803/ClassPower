package com.ysy.classpower_utils;

import android.app.Application;
import android.graphics.Bitmap;

/**
 * Created by 姚圣禹 on 2016/3/13.
 */
public class OwnApp extends Application {

    private Bitmap mBitmap = null;
    private String mTestPreviewInfo = null;
    private boolean mTestIsFinished;

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public String getTestPreviewInfo() {
        return mTestPreviewInfo;
    }

    public boolean getTestIsFinished() {
        return mTestIsFinished;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public void setTestPreviewInfo(String string) {
        this.mTestPreviewInfo = string;
    }

    public void setTestIsFinished(boolean b) {
        this.mTestIsFinished = b;
    }

}
