package com.ljn.callingsimulation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;

import com.ljn.callingsimulation.bean.Calling;
import com.ljn.callingsimulation.util.DateUtil;
import com.ljn.callingsimulation.util.SQLiteOpenHelperUtil;

import java.util.Date;

/**
 * Created by 12390 on 2017/9/5.
 */
public class MainService extends Service {

    private SQLiteOpenHelperUtil dbHelper;
    private Intent intent = new Intent("com.ljn.callingsimulation.RECEIVER");
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("模拟来电在正后台运行");
        startForeground(1,builder.build());
        dbHelper = new SQLiteOpenHelperUtil(MainService.this);
        System.out.println("ttt");
        new Thread(){
            @Override
            public void run() {
                while (true) {
                    //Vector<Calling> callings = dbHelper.doQuery("datetime("+ SQLiteOpenHelperUtil.args[4]+")>datetime(CURRENT_TIMESTAMP,'localtime')",null);
                    for (Calling calling: MainActivity.callings) {
                        //System.out.println(calling.getIsOpen());
                        if (calling.getIsOpen().equals("1")) {
                            System.out.println(DateUtil.dateToString(new Date()));
                            System.out.println(calling.getStartTime());
                            if (DateUtil.compareDate(DateUtil.dateToString(new Date()), calling.getStartTime()) != -1) {
//                                intent.putExtra("id", calling.getCallingId());
//                                sendBroadcast(intent);

                                Intent intent = new Intent(MainService.this,CallActivity.class);
                                Bundle mBundle = new Bundle();
                                mBundle.putSerializable("calling", calling);
                                intent.putExtras(mBundle);
                                MainActivity.callings.remove(calling);
                                String[] values = {String.valueOf(calling.getCallingId())};
                                dbHelper.doDelete("calling_id=?", values);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                break;
                            }
                        }
                    }

                    try {
                        Thread.sleep(3*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

}
