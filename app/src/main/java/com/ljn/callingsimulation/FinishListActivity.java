package com.ljn.callingsimulation;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dy on 2016/10/18.
 */

/**
 * 一个类 用来结束所有后台activity
 * @author Administrator
 *
 */
public class FinishListActivity extends Application {
    //运用list来保存们每一个activity是关键
    private List<Activity> mList = new LinkedList<Activity>();
    //为了实现每次使用该类时不创建新的对象而创建的静态对象
    private static FinishListActivity instance;
    //构造方法
    private FinishListActivity(){}
    //实例化一次
    public synchronized static FinishListActivity getInstance(){
        if (null == instance) {
            instance = new FinishListActivity();
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
            for (Activity activity:mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //杀进程
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
