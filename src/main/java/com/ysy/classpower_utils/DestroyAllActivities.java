package com.ysy.classpower_utils;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

/**
 * 一个类 用来结束所有后台activity
 *
 * @author Administrator
 */
public class DestroyAllActivities extends Application {
    //运用list来保存们每一个activity是关键
    private List<Activity> mList = new LinkedList<Activity>();
    //为了实现每次使用该类时不创建新的对象而创建的静态对象
    private static DestroyAllActivities instance;

    //构造方法
    private DestroyAllActivities() {
    }

    //实例化一次
    public synchronized static DestroyAllActivities getInstance() {
        if (null == instance) {
            instance = new DestroyAllActivities();
        }
        return instance;
    }

    // add Activity  
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    //关闭每一个list内的activity
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            System.exit(0);
// 注释缘由，exit方法在跳转到LoginActivity后执行（避免页面过渡不自然），但Login不能被exit掉，故跳转之前的页面需手动finish
        }
    }

    //杀进程
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}