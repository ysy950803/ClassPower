package com.ysy.classpower_utils;

import android.app.Application;
import android.graphics.Bitmap;

/**
 * Created by 姚圣禹 on 2016/3/13.
 */
public class OwnApp extends Application {

    private Bitmap mBitmap = null;

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

}
