package com.ljn.callingsimulation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.ljn.callingsimulation.bean.Calling;
import com.ljn.callingsimulation.util.SQLiteOpenHelperUtil;

public class CalledActivity extends AppCompatActivity implements View.OnClickListener {

    TextView call_time;
    boolean RUN_STATE = true;
    private Calling calling;

    int m = 0;
    int s = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);


        //activity在锁屏状态下显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main2);
        FinishListActivity.getInstance().addActivity(this);

//        calling = (Calling) getIntent().getSerializableExtra("calling");
//        ((TextView)findViewById(R.id.name)).setText(calling.getCaller());

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

    private void startCommunication(){
        //对话模式判断
        if(calling.getPattern().equals(CallingAdderActivity.schemeItems[0])){
            //自定义对话
        }else{
            //智能对话
        }
    }
}
