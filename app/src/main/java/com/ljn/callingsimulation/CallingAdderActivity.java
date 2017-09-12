package com.ljn.callingsimulation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.view.*;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ljn.callingsimulation.util.DateUtil;
import com.ljn.callingsimulation.util.SQLiteOpenHelperUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 12390 on 2017/8/30.
 */
public class CallingAdderActivity extends AppCompatActivity {

    public static final String[] schemeItems = {"自定义对话", "智能对话"};
    public static final String[] dayItems = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    public static final String[] voiceItems = {"男声", "女声"};
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
    private String dialogueContent = "";
    private String content1 = "";
    private String content2 = "";
    private String content3 = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_add_phone);

        //初始化数据库
        initDB();

        //初始化控件
        initComponent();

        //初始化控件默认值
        setInitValues();

        //放置所有控件监听事件
        setAllClickListenner();
    }

    public void initDB() {
        sqLiteOpenHelperUtil = new SQLiteOpenHelperUtil(CallingAdderActivity.this);
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

    }

    private void setAllClickListenner(){


        //监听事件

        //重复
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRepeatDialog();
            }
        });
        //声音
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVoiceDialog();
            }
        });
        //TimePicker's hour component.
        hour_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                hour = text;
                calculateTimePicker();
                hintText.setText(DateUtil.getDistanceTime(DateUtil.dateToString(new Date()), calculate()));
            }
        });
        //TimePicker's minute component.
        minute_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                minute = text;
                calculateTimePicker();
                hintText.setText(DateUtil.getDistanceTime(DateUtil.dateToString(new Date()), calculate()));
            }
        });

        //对话模式按钮
        schemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSchemeDialog();
            }
        });

        //响铃后删除
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

        //左上角取消按钮监听
        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelDialog();
            }
        });

        //右上角确定按钮
        this.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] values = new String[10];
                values[0] = "";
                values[1] = caller.getText().toString();
                values[2] = schemeText.getText().toString();
                values[3] = dialogueContent;
                values[4] = calculate();
                values[5] = voiceText.getText().toString();
                values[6] = "1";
                values[7] = "1";
                values[8] = "1";
                values[9] = del;
                sqLiteOpenHelperUtil.doInsert(values);
                //sqLiteOpenHelperUtil.doInsert(new String[]{"","ljn", "1", "", "2017-09-05 17:00:00", "1", "1","1", "1","1"});
                finish();
                CallingAdderActivity.this.startActivity(new Intent(CallingAdderActivity.this, MainActivity.class));
            }
        });
    }

    private void calculateTimePicker(){
        String chooseTime = calculate();
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

    private void showCancelDialog() {
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
                CallingAdderActivity.this.startActivity(new Intent(CallingAdderActivity.this, MainActivity.class));
            }
        });

    }

    private void showRepeatDialog() {

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        AlertDialog.Builder listDialog = new AlertDialog.Builder(CallingAdderActivity.this);
        listDialog.setItems(dayItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                repeatText.setText(dayItems[which]);
                hintText.setText(DateUtil.getDistanceTime(DateUtil.dateToString(new Date()), calculate()));
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

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        AlertDialog.Builder listDialog = new AlertDialog.Builder(CallingAdderActivity.this);
        listDialog.setItems(schemeItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                schemeText.setText(schemeItems[which]);
                if(which==0){
                    showDialogueContentDialog();
                }
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

    private void showDialogueContentDialog() {
//        TextInputLayout mTextInput1 = new TextInputLayout(CallingAdderActivity.this);
//        TextInputLayout mTextInput2 = new TextInputLayout(CallingAdderActivity.this);
//        TextInputLayout mTextInput3 = new TextInputLayout(CallingAdderActivity.this);
        final EditText mEditText1 = new EditText(CallingAdderActivity.this);
        final EditText mEditText2 = new EditText(CallingAdderActivity.this);
        final EditText mEditText3 = new EditText(CallingAdderActivity.this);
        mEditText1.setTextSize(16);
        mEditText2.setTextSize(16);
        mEditText3.setTextSize(16);
        if(content1.equals("")) {
            mEditText1.setHint("对话内容1");
        }else{
            mEditText1.setText(content1);
        }
        if(content2.equals("")) {
            mEditText2.setHint("对话内容2");
        }else{
            mEditText2.setText(content2);
        }
        if(content3.equals("")) {
            mEditText3.setHint("对话内容3");
        }else{
            mEditText3.setText(content3);
        }
        mEditText1.setSingleLine(true);
        mEditText2.setSingleLine(true);
        mEditText3.setSingleLine(true);
        mEditText1.setMaxLines(1);
        mEditText2.setMaxLines(1);
        mEditText3.setMaxLines(1);

//        mTextInput1.setHint("对话内容1");
//        mTextInput2.setHint("对话内容2");
//        mTextInput3.setHint("对话内容3");
//        mTextInput1.addView(mEditText1);
//        mTextInput2.addView(mEditText2);
//        mTextInput3.addView(mEditText3);
        LinearLayout mLinearLayout = new LinearLayout(CallingAdderActivity.this);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearLayout.addView(mEditText1);
        mLinearLayout.addView(mEditText2);
        mLinearLayout.addView(mEditText3);
        final AlertDialog.Builder builder = new AlertDialog.Builder(CallingAdderActivity.this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                content1 = mEditText1.getText().toString();
                content2 = mEditText2.getText().toString();
                content3 = mEditText3.getText().toString();
                dialogueContent = content1 + "\n" + content2 + "\n" + content3;
                System.out.println(dialogueContent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog mInputDialog = builder.create();
        mInputDialog.setTitle("请设置对话内容");
        mInputDialog.setView(mLinearLayout);


        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        Window window = mInputDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM);
        lp.height = (int) (d.getHeight() * 0.3);
        lp.width = (int) getResources().getDisplayMetrics().widthPixels;
        window.setAttributes(lp);

        mInputDialog.show();
    }

    private void showVoiceDialog() {

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        AlertDialog.Builder listDialog = new AlertDialog.Builder(CallingAdderActivity.this);
        listDialog.setItems(voiceItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                voiceText.setText(voiceItems[which]);
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

    private String calculate() {
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

        //设置TimePicker 初始参数
        List<String> data = new ArrayList<String>();
        List<String> seconds = new ArrayList<String>();
        for (int i = 0; i < 24; i++) {
            data.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i++) {
            seconds.add(i < 10 ? "0" + i : "" + i);
        }
        String dateTime = DateUtil.getNextTHTime();
        Integer hour = Integer.valueOf(dateTime.substring(11, 13));
        Integer minute = Integer.valueOf(dateTime.substring(14, 16));
//        this.hour_pv.setSelected(16);
//        this.minute_pv.setSelected(20);
        this.hour = hour.toString();
        this.minute = minute.toString();
        hour_pv.setData(data, hour);
        minute_pv.setData(seconds, minute);

        this.schemeText.setText(schemeItems[1]);
        this.voiceText.setText("男声");

    }


}
