package com.ljn.callingsimulation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.widget.ListView;
import android.widget.TextView;
import com.ljn.callingsimulation.bean.Calling;
import com.ljn.callingsimulation.util.DateUtil;
import com.ljn.callingsimulation.util.SQLiteOpenHelperUtil;

import java.util.Date;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Created by 12390 on 2017/9/5.
 */
public class CallingService extends Service {

    private SQLiteOpenHelperUtil dbHelper;
    private Intent intent = new Intent("com.ljn.callingsimulation.RECEIVER");
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        dbHelper = new SQLiteOpenHelperUtil(CallingService.this);
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
                                MainActivity.callings.remove(calling);
                                startActivity(new Intent(CallingService.this,CallActivity.class).putExtra("name",calling.getCaller()));
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
