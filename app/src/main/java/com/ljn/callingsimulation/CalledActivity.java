package com.ljn.callingsimulation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class CalledActivity extends AppCompatActivity implements View.OnClickListener {

    TextView call_time;
    boolean RUN_STATE = true;

    int m = 0;
    int s = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_main2);

        //activity在锁屏状态下显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        FinishListActivity.getInstance().addActivity(this);

        ((TextView)findViewById(R.id.name)).setText(getIntent().getStringExtra("name"));

        findViewById(R.id.end).setOnClickListener(this);

        call_time = (TextView) findViewById(R.id.call_time);
        new Thread() {
            @Override
            public void run() {
                while (RUN_STATE) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (s == 60) {
                                m++;
                                s = 0;
                            }
                            String mm, ss;
                            if (m < 10) mm = "0" + m;
                            else mm = "" + m;
                            if (s < 10) ss = "0" + s;
                            else ss = "" + s;
                            call_time.setText(mm + ":" + ss);
                            s++;
                        }
                    });
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.end:
                RUN_STATE = false;
                FinishListActivity.getInstance().exit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RUN_STATE = false;
        FinishListActivity.getInstance().exit();
    }

    public void startMediaRecorder(){

    }

}
