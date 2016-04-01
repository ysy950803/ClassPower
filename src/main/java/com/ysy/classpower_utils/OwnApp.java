package com.ysy.classpower_utils;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 姚圣禹 on 2016/3/13.
 */
public class OwnApp extends Application {

    private Bitmap mBitmap = null;
    private String mTestPreviewInfo = null;
    private boolean mTestIsFinished;
    private String URL_FIGURE = "10.15.85.196";
//    private String[] questionInfo = null;
    private Bundle[] questionStates = null;

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public String getTestPreviewInfo() {
        return this.mTestPreviewInfo;
    }

    public void setTestPreviewInfo(String string) {
        this.mTestPreviewInfo = string;
    }

    public boolean getTestIsFinished() {
        return this.mTestIsFinished;
    }

    public void setTestIsFinished(boolean b) {
        this.mTestIsFinished = b;
    }

    public String getURL_FIGURE() {
        return this.URL_FIGURE;
    }

    public void setURL_FIGURE(String URL_FIGURE) {
        this.URL_FIGURE = URL_FIGURE;
    }

//    public void setQuestionInfoSize(int size) {
//        this.questionInfo = new String[size];
//        for (int i = 0; i < size; ++i) {
//            this.questionInfo[i] = "";
//        }
//    }
//
//    public void setQuestionInfo(int position, String info) {
//        this.questionInfo[position] = info;
//    }
//
//    public String[] getQuestionInfo() {
//        return this.questionInfo;
//    }

    public void setQuestionStates(int position, boolean[] states) {
        this.questionStates[position].putBooleanArray("items_state", states);
    }

    public void clearQuestionStates() {
        this.questionStates = null;
    }

    public void setQuestionStatesSize(int size) {
        this.questionStates = new Bundle[size];
        for (int i = 0; i < size; ++i) {
            this.questionStates[i] = new Bundle();
            this.questionStates[i].putBooleanArray("items_state", null);
        }
    }

    public boolean[] getQuestionStates(int position) {
        return this.questionStates[position].getBooleanArray("items_state");
    }

    public Bundle[] getQuestionStates() {
        return this.questionStates;
    }

}
