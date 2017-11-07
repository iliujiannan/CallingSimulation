package com.ljn.callingsimulation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.ljn.callingsimulation.bean.Calling;
import com.ljn.callingsimulation.util.CustomCommunicate;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class CalledActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView call_time;
    private TextView speaker;
    private boolean RUN_STATE = true;
    private Calling calling;
    private int currentVolume = -1;
    private int state = 0;
    private EventManager asr;
    private AudioManager mAudioManager;
    private String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private int m = 0;
    private int s = 0;
    private CustomCommunicate customCommunicate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAudioManager = (AudioManager) getSystemService(CalledActivity.this.AUDIO_SERVICE);
        setInCall();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);


        //activity在锁屏状态下显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.called);
        FinishListActivity.getInstance().addActivity(this);

        calling = (Calling) getIntent().getSerializableExtra("calling");
        ((TextView) findViewById(R.id.name)).setText(calling.getCaller());

        findViewById(R.id.end).setOnClickListener(this);
        speaker = (TextView) findViewById(R.id.speaker);
        findViewById(R.id.speaker).setOnClickListener(this);

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
        startCommunication();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.end:
                RUN_STATE = false;
                FinishListActivity.getInstance().exit();
                break;
            case R.id.speaker:
                if (state == 0) {

                    setInSpeaker();
                    speaker.setTextColor(Color.BLUE);

                    state = 1;
                } else {
                    setInCall();
                    speaker.setTextColor(Color.WHITE);

                    state = 0;
                }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RUN_STATE = false;
        FinishListActivity.getInstance().exit();
    }

    @Override
    protected void onDestroy() {
        if (customCommunicate != null) {
            customCommunicate.end();
        }
        if (currentVolume != -1) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            System.out.println("-1");
        }
        super.onDestroy();

    }

    private void startCommunication() {
        //对话模式判断
        if (calling.getPattern().equals(CallingAdderActivity.schemeItems[0])) {
            //自定义对话
            customCommunicate = new CustomCommunicate(calling, CalledActivity.this);
            customCommunicate.begin();
        } else {
            //智能对话
//            asr = EventManagerFactory.create(this, "asr");
//            asr.registerListener(this);
//            System.out.println("speech");
        }
    }

    private void setInCall() {
        //听筒模式
        mAudioManager.setSpeakerphoneOn(false);
        if (currentVolume != -1) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        }
    }

    private void setInSpeaker() {
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
        mAudioManager.setSpeakerphoneOn(true);
    }
}
