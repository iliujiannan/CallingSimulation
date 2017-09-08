package com.ljn.callingsimulation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.text.Layout;
import android.view.*;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ljn.callingsimulation.R;
import com.ljn.callingsimulation.util.DateUtil;
import com.ljn.callingsimulation.util.SQLiteOpenHelperUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 12390 on 2017/8/30.
 */
public class CallingAdder extends AppCompatActivity {

    private TextView cancelButton;
    private TextView confirmButton;
    private SQLiteOpenHelperUtil sqLiteOpenHelperUtil;
    private PickerView hour_pv;
    private PickerView minute_pv;
    private String hour = "0";
    private String minute = "0";
    private CardView repeatButton;
    private CardView schemeButton;
    private CardView voiceButton;
    private SwitchCompat delSwitch;
    private TextView hintText;
    private TextView voiceText;
    private TextView repeatText;
    private TextView schemeText;
    private EditText caller;
    private String del = "1";
    private Boolean addEnable = true;
    private Boolean subEnable = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_add_phone);
        initDB();
        initComponent();
        setInitValues();
    }

    private void initComponent() {

        //获取所有控件
        this.minute_pv = (PickerView) findViewById(R.id.minute_pv);
        this.hour_pv = (PickerView) findViewById(R.id.hour_pv);
        this.cancelButton = (TextView) findViewById(R.id.cancel_button);
        this.confirmButton = (TextView) findViewById(R.id.confirm_button);
        this.repeatButton = (CardView) findViewById(R.id.repeat_button);
        this.schemeButton = (CardView) findViewById(R.id.scheme_button);
        this.voiceButton = (CardView) findViewById(R.id.voice_button);
        this.delSwitch = (SwitchCompat) findViewById(R.id.del_switch);
        this.repeatText = (TextView) findViewById(R.id.repeat_text);
        this.schemeText = (TextView) findViewById(R.id.scheme_text);
        this.voiceText = (TextView) findViewById(R.id.void_text);
        this.caller = (EditText) findViewById(R.id.caller);
        this.hintText = (TextView) findViewById(R.id.add_phone_hint_text);
        //设置TimePicker 初始参数
        List<String> data = new ArrayList<String>();
        List<String> seconds = new ArrayList<String>();
        for (int i = 0; i < 24; i++) {
            data.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i++) {
            seconds.add(i < 10 ? "0" + i : "" + i);
        }
        hour_pv.setData(data);
        minute_pv.setData(seconds);

        //监听事件

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRepeatDialog();
            }
        });
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVoiceDialog();
            }
        });
        hour_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                hour = text;
                caculateTimePicker();
                hintText.setText(DateUtil.getDistanceTime(DateUtil.dateToString(new Date()), caculate()));
            }
        });
        minute_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                minute = text;
                caculateTimePicker();
                hintText.setText(DateUtil.getDistanceTime(DateUtil.dateToString(new Date()), caculate()));
            }
        });

        schemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSchemeDialog();
            }
        });

        this.delSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    del = "1";
                } else {
                    del = "0";
                }
            }
        });
        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        this.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] values = new String[10];
                values[0] = "";
                values[1] = caller.getText().toString();
                values[2] = schemeText.getText().toString();
                values[3] = "";
                values[4] = caculate();
                values[5] = voiceText.getText().toString();
                values[6] = "1";
                values[7] = "1";
                values[8] = "1";
                values[9] = del;
                sqLiteOpenHelperUtil.doInsert(values);
                //sqLiteOpenHelperUtil.doInsert(new String[]{"","ljn", "1", "", "2017-09-05 17:00:00", "1", "1","1", "1","1"});
                finish();
                CallingAdder.this.startActivity(new Intent(CallingAdder.this, MainActivity.class));
            }
        });
    }

    private void caculateTimePicker(){
        String chooseTime = caculate();
        String nowDate = DateUtil.dateToString(new Date());
        int res = DateUtil.compareDate(chooseTime, nowDate);
        if(res==-1&&addEnable){
            Date nextDate = new Date(DateUtil.stringToDate(chooseTime).getTime() + 24*60*60*1000);
            repeatText.setText(DateUtil.getWeekOfDate(nextDate));
            addEnable = false;
        }
        Integer nowHour = Integer.valueOf(DateUtil.dateToString(new Date(), "HH"));
        Integer nowMinute = Integer.valueOf(DateUtil.dateToString(new Date(), "mm"));
        if(res==1&&(Integer.valueOf(hour)> nowHour||(Integer.valueOf(hour)==nowHour && Integer.valueOf(minute)>nowMinute)))
        {
            repeatText.setText(DateUtil.getWeekOfDate(DateUtil.stringToDate(nowDate)));
            addEnable = true;
        }
    }

    private void showDialog() {
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        final Dialog dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.dialog);
        Window window = dialog1.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM);
        lp.height = (int) (d.getHeight() * 0.3);
        lp.width = (int) getResources().getDisplayMetrics().widthPixels;
        window.setAttributes(lp);
        dialog1.show();
        TextView cancel = (TextView) window.findViewById(R.id.dialog_cancel);
        TextView confirm = (TextView) window.findViewById(R.id.dialog_confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.cancel();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                CallingAdder.this.startActivity(new Intent(CallingAdder.this, MainActivity.class));
            }
        });

    }

    private void showRepeatDialog() {
        final String[] items = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        AlertDialog.Builder listDialog = new AlertDialog.Builder(CallingAdder.this);
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                repeatText.setText(items[which]);
                hintText.setText(DateUtil.getDistanceTime(DateUtil.dateToString(new Date()), caculate()));
            }

        });
        AlertDialog alertDialog = listDialog.create();
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM);
        lp.height = (int) (d.getHeight() * 0.3);
        lp.width = (int) getResources().getDisplayMetrics().widthPixels;
        window.setAttributes(lp);

        alertDialog.show();
    }

    private void showSchemeDialog() {
        final String[] items = {"自定义对话", "智能对话"};
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        AlertDialog.Builder listDialog = new AlertDialog.Builder(CallingAdder.this);
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                schemeText.setText(items[which]);
            }

        });
        AlertDialog alertDialog = listDialog.create();
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM);
        lp.height = (int) (d.getHeight() * 0.3);
        lp.width = (int) getResources().getDisplayMetrics().widthPixels;
        window.setAttributes(lp);

        alertDialog.show();
    }

    private void showVoiceDialog() {
        final String[] items = {"男声", "女声"};
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        AlertDialog.Builder listDialog = new AlertDialog.Builder(CallingAdder.this);
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                voiceText.setText(items[which]);
            }

        });
        AlertDialog alertDialog = listDialog.create();
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM);
        lp.height = (int) (d.getHeight() * 0.3);
        lp.width = (int) getResources().getDisplayMetrics().widthPixels;
        window.setAttributes(lp);

        alertDialog.show();
    }

    private String caculate() {
        //通过repeat hour minute 计算响铃的yyyy-MM-dd HH：mm：ss
        Integer day = DateUtil.getIndOfDay(repeatText.getText().toString());
        Integer nowDay = DateUtil.getIndOfDay(DateUtil.getWeekOfDate(new Date()));
        Integer c = 0;
        if (day != -1 && nowDay != -1) {
            if (day >= nowDay) {
                c = day - nowDay;
            } else {
                c = 7 + nowDay - day;
            }
        }
        String strNowDay = DateUtil.dateToString(new Date(), "yyyy-MM-dd");
        Date nowDate = DateUtil.stringToDate(strNowDay + " 00:00:00");
        Date targetDate = new Date(nowDate.getTime() + c * 24 * 60 * 60 * 1000 + (Integer.valueOf(hour) * 60 * 60 * 1000) + Integer.valueOf(minute) * 60 * 1000);
        String targetStringDate = DateUtil.dateToString(targetDate);
        return targetStringDate;
    }

    private void setInitValues() {
        //初始化hintText,TimePicker, repeatText
        this.repeatText.setText(DateUtil.getWeekOfDate(DateUtil.stringToDate(DateUtil.getNextTHTime())));
        this.hintText.setText("将在1小时59分后来电");
        String dateTime = DateUtil.getNextTHTime();
        Integer hour = Integer.valueOf(dateTime.substring(11, 13));
        Integer minute = Integer.valueOf(dateTime.substring(14, 16));
        this.hour_pv.setSelected(hour);
        this.minute_pv.setSelected(minute);
        this.hour = hour.toString();
        this.minute = minute.toString();
        this.schemeText.setText("自定义对话");
        this.voiceText.setText("男声");

    }

    public void initDB() {
        sqLiteOpenHelperUtil = new SQLiteOpenHelperUtil(CallingAdder.this);
    }
}
